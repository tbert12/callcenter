package org.alm.tbert.callcenter;

import org.alm.tbert.callcenter.employee.Employee;
import org.alm.tbert.callcenter.employee.exception.EmployeeException;
import org.apache.log4j.Logger;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class CallDispatcher {
    private static final Logger LOGGER = Logger.getLogger(CallDispatcher.class);

    private AtomicInteger totalDispatchedCalls;
    private PriorityBlockingQueue<Employee> employees;
    private ExecutorService callDispatcherExecutor;

    CallDispatcher(PriorityBlockingQueue<Employee> employees, int maxSimultaneousCalls) {
        this.employees              = employees;
        this.callDispatcherExecutor = Executors.newFixedThreadPool(maxSimultaneousCalls);

        this.totalDispatchedCalls   = new AtomicInteger(0);
    }

    public void stop() {
        this.callDispatcherExecutor.shutdown();
        this.callDispatcherExecutor.shutdownNow();
    }


    public void stopWhenAllCallsAreDispatched() throws InterruptedException {
        this.callDispatcherExecutor.shutdown();
        this.callDispatcherExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    private void assignCall(Call call) {
        try {
            Employee employee = employees.take();
            LOGGER.info( String.format("Employee '%s' will take call [%s]", employee.toString(), call.toString()) );
            employee.take(call);
            LOGGER.info( String.format("Employee '%s' end the call [%s]", employee.toString(), call.toString()) );
            totalDispatchedCalls.incrementAndGet();
            employees.add(employee);
        } catch (EmployeeException e) {
            LOGGER.warn("Cannot Assign call [%s]. No free employee. Attempt to dispatch again");
            dispatchCall(call);
        } catch (InterruptedException e) {
            LOGGER.warn(String.format("Interrupted waiting free employee to assign call [%s]. Ignoring it",call.toString()));
        }
    }



    public void dispatchCall(Call call) {
        LOGGER.info(String.format("New call to dispatch [%s]", call.toString()));
        callDispatcherExecutor.submit(() -> assignCall(call));
    }

    public int getTotalCalls() {
        return totalDispatchedCalls.get();
    }
}
