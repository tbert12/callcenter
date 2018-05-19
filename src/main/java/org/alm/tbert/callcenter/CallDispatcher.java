package org.alm.tbert.callcenter;

import org.alm.tbert.callcenter.employee.Employee;
import org.alm.tbert.callcenter.employee.EmployeeManager;
import org.alm.tbert.callcenter.employee.exception.EmployeeException;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class CallDispatcher {
    private static final Logger LOGGER = Logger.getLogger(CallDispatcher.class);
    private static final int INCOMING_CALLS_CAPACITY = 100;

    private final EmployeeManager operators;
    private final BlockingQueue<Call> callsToDispatch;
    private Semaphore acceptCallsSemaphore;

    private ExecutorService incomingCallsExecutor;
    private ExecutorService dispatchExecutor;

    private AtomicBoolean running;

    private AtomicInteger totalDispatchedCalls;

    CallDispatcher(EmployeeManager operators, int maxSimultaneousCalls) {
        this.operators              = operators;
        this.callsToDispatch        = new PriorityBlockingQueue<>(INCOMING_CALLS_CAPACITY, Call::compare);

        this.dispatchExecutor       = Executors.newFixedThreadPool(1); // Only one, like server.

        int simultaneousCallsLimit  = Math.min(maxSimultaneousCalls, operators.countTotalEmployees());
        this.acceptCallsSemaphore   = new Semaphore( simultaneousCallsLimit );
        this.incomingCallsExecutor  = Executors.newFixedThreadPool(simultaneousCallsLimit);

        this.running                = new AtomicBoolean(false);

        this.totalDispatchedCalls   = new AtomicInteger(0);
        start();
    }


    private void start() {
        running.set(true);
        dispatchExecutor.submit(() -> {
            while (running.get()) {
                try {
                    awaitCall();
                } catch (InterruptedException e) {
                    LOGGER.info("End call listener");
                }
            }
        });
    }

    public void stop() {
        if (running.compareAndSet(true, false)) {
            LOGGER.info(String.format("OK. Clear unassigned calls (%d)", callsToDispatch.size()));
            callsToDispatch.clear();

            LOGGER.info("Waiting for assigned calls to end");
            incomingCallsExecutor.shutdown();
            try {
                incomingCallsExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                LOGGER.warn("Interrupted await. All assigned calls have not been finalized");
            }
            LOGGER.info("OK. Finalized all calls");

            if (!dispatchExecutor.isShutdown()) {
                dispatchExecutor.shutdownNow();
            }
            LOGGER.info("OK. Stopped CallDispatcher");
        }
    }

    private void callsToDispatchPolling(Runnable action) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                LOGGER.info(String.format("Check incoming calls to stop (%d)",callsToDispatch.size()));
                if (callsToDispatch.isEmpty()) {
                    LOGGER.info("No incoming call. Trigger stop");
                    action.run();
                    timer.cancel();
                }
            }
        }, 0, 1000);
    }

    public void stopWhenAllCallsAreDispatched() throws InterruptedException {
        if (!running.get()) {
            return;
        }

        Semaphore semaphore = new Semaphore(0);
        callsToDispatchPolling(() -> {
            stop();
            semaphore.release();
        });
        semaphore.acquire(); //Wait polling timer

    }

    private void assignCall(Call call) {
        try {
            EmployeeManager employeeManager = operators.getAvailableEmployeeManager();
            Employee employee = employeeManager.takeEmployee();
            LOGGER.info( String.format("Employee '%s' will take call [%s]", employee.toString(), call.toString()) );
            employee.take(call);
            LOGGER.info( String.format("Employee '%s' end the call [%s]", employee.toString(), call.toString()) );
            employeeManager.freeEmployee(employee);
            totalDispatchedCalls.incrementAndGet();
        } catch (EmployeeException e) {
            LOGGER.warn("Cannot Assign call [%s]. No free employee. Attempt to dispatch again (with priority)");
            callsToDispatch.add(call);
        }
    }

    private void awaitCall() throws InterruptedException {
        acceptCallsSemaphore.acquire();
        Call newCallToAssign = callsToDispatch.take();
        incomingCallsExecutor.submit(() -> {
            assignCall(newCallToAssign);
            acceptCallsSemaphore.release();
        });
    }



    public void dispatchCall(Call call) {
        LOGGER.info(String.format("New call to dispatch [%s]", call.toString()));
        callsToDispatch.add(call);
    }

    public int getTotalCalls() {
        return totalDispatchedCalls.get();
    }
}
