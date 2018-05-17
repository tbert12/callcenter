package org.alm.tbert.callcenter;

import org.alm.tbert.callcenter.employee.EmployeeManager;
import org.alm.tbert.callcenter.employee.EmployeeManagerBuilder;
import org.apache.log4j.Logger;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CallCenter {
    private static final Logger LOGGER = Logger.getLogger(CallCenter.class);

    private static final int INCOMING_CALLS_CAPACITY = 100;
    private ArrayBlockingQueue<Call> incomingCalls;
    private CallDispatcher callDispatcher;

    private Semaphore acceptCallsSemaphore;
    private AtomicBoolean running;
    private ExecutorService incomingCallsExecutor;
    private ExecutorService dispatchExecutor;

    private int getTotalEmployeesCount(EmployeeManager firstEmployeeLevel) {
        EmployeeManager employeeManager = firstEmployeeLevel;
        int count = 0;
        while (employeeManager.hasNextHierarchyLevel()) {
            count += employeeManager.getNumberOfEmployees();
            employeeManager = employeeManager.getNextHierarchyLevel();
        }
        return count + employeeManager.getNumberOfEmployees();
    }

    private void constructor() {
        // 1. Employee Managers with Employees
        // 1.1 Directors
        EmployeeManager directors = EmployeeManagerBuilder.newDirectorEmployeeManager(10);

        // 1.2 Supervisors (with directors as next hierarchy level)
        EmployeeManager supervisors = EmployeeManagerBuilder.newSupervisorEmployeeManager(10, directors);

        // 1.3 Operators (with supervisors as next hierarchy level)
        EmployeeManager operators = EmployeeManagerBuilder.newOperatorEmployeeManager(10, supervisors);

        // 4. Create Dispatcher with first level
        callDispatcher = new CallDispatcher(operators);

        // 5. Define Semaphore to block if exceed number of call at the same time
        //      - Init Value: Number of employees
        int totalEmployees = getTotalEmployeesCount(operators);
        acceptCallsSemaphore = new Semaphore(totalEmployees);
        LOGGER.info(String.format("Initialized semaphore with %d permits", totalEmployees));
    }

    public CallCenter() {
        constructor();
        incomingCalls = new ArrayBlockingQueue<>(INCOMING_CALLS_CAPACITY);
        incomingCallsExecutor = Executors.newFixedThreadPool(20);
        dispatchExecutor = Executors.newFixedThreadPool(20);
        running = new AtomicBoolean(false);
    }

    public void call() {
        incomingCalls.add( new Call() );
    }

    private void waitCall() throws InterruptedException {
        LOGGER.info("Waiting for free employee to answer a call");
        acceptCallsSemaphore.acquire();
            LOGGER.info("Waiting new call");
            Call incomingCall = incomingCalls.take();
            LOGGER.info(String.format("Incoming new call [%s]", incomingCall.toString()));
            dispatchExecutor.submit(() -> callDispatcher.dispatchCall(incomingCall));
        acceptCallsSemaphore.release();
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            Semaphore starting = new Semaphore(0);
            incomingCallsExecutor.submit(() -> {
                try {
                    starting.release();
                    LOGGER.info("Started CallCenter");
                    while (running.get()) {
                        waitCall();
                    }
                } catch (InterruptedException e) {
                    LOGGER.info("End calls listener (interrupt signal)");
                }
            });
            try {
                starting.acquire();
            } catch (InterruptedException e) {
                LOGGER.info("Interrupted the wait of start");
            }
        }
    }


    public void stop() {
        if (!running.get()) {
            LOGGER.info("OK. CallCenter is already stopped");
            return;
        }
        LOGGER.info(String.format("OK. Deleted wait calls (%d)", incomingCalls.size()));
        incomingCalls.clear();

        LOGGER.info("Attempt to stop call receiver");
        running.set(false);

        LOGGER.info("Waiting for answered calls to be finalized");
        dispatchExecutor.shutdown();
        try {
            dispatchExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            LOGGER.info("OK. Finalized all calls");
        } catch (InterruptedException e) {
            LOGGER.warn("Couldn't finish await termination of executors (interrupted)");
        }
        incomingCallsExecutor.shutdownNow();
        LOGGER.info("OK. Stopped CallCenter");
    }

    public boolean isRunning() {
        return running.get();
    }
}
