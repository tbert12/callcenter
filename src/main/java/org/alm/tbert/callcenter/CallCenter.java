package org.alm.tbert.callcenter;

import org.alm.tbert.callcenter.employee.EmployeeManager;
import org.alm.tbert.callcenter.employee.EmployeeManagerBuilder;
import org.alm.tbert.callcenter.employee.exception.EmployeeException;
import org.alm.tbert.callcenter.exception.CallCenterException;
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

    private void constructor() throws EmployeeException {
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
        acceptCallsSemaphore = new Semaphore(getTotalEmployeesCount(operators));
    }

    public CallCenter() throws CallCenterException {
        constructor();
        incomingCalls = new ArrayBlockingQueue<>(INCOMING_CALLS_CAPACITY);
        incomingCallsExecutor = Executors.newFixedThreadPool(20);
        dispatchExecutor = Executors.newFixedThreadPool(20);
        running.set(false);
    }

    private void call() {
        incomingCalls.add( new Call() );
    }

    private void waitCall() throws InterruptedException {
        acceptCallsSemaphore.acquire();
            Call incomingCall = incomingCalls.take();
            dispatchExecutor.submit(() -> callDispatcher.dispatchCall(incomingCall));
        acceptCallsSemaphore.release();
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            incomingCallsExecutor.submit(() -> {
                try {
                    while (running.get()) {
                        waitCall();
                    }
                } catch (InterruptedException e) {
                    LOGGER.info("Interrupted calls listener");
                    LOGGER.debug(e);
                }
            });
        }
    }


    public void stop() {
        if (running.get()) {
            return;
        }
        LOGGER.info("Deleted wait calls");
        incomingCalls.clear();

        LOGGER.info("Attempt to stop call receiver");
        running.set(false);
        incomingCallsExecutor.shutdown();

        LOGGER.info("Waiting for answered calls to be finalized");
        dispatchExecutor.shutdown();
        try {
            dispatchExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            LOGGER.info("Finalized all calls");
            incomingCallsExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOGGER.warn("Couldn't finish await termination of executors");
            LOGGER.debug(e);
        }
        LOGGER.info("CallCenter Stopped");
    }
}
