package org.alm.tbert.callcenter;

import org.alm.tbert.callcenter.employee.Employee;
import org.alm.tbert.callcenter.employee.EmployeeType;
import org.apache.log4j.Logger;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.IntStream;

public class CallCenter {
    private static final Logger LOGGER = Logger.getLogger(CallCenter.class);
    private static final int EMPLOYEE_QUEUE_CAPACITY = 100;
    private static final int MAX_INCOMING_CALLS      = 10;
    private static final int OPERATORS               = 3;
    private static final int SUPERVISORS             = 3;
    private static final int DIRECTORS               = 3;
    private CallDispatcher callDispatcher;

    private void constructor() {
        // 1. Priority queue with Employees
        PriorityBlockingQueue<Employee> employees = new PriorityBlockingQueue<>(EMPLOYEE_QUEUE_CAPACITY);

        // 1.1 Directors
        IntStream.range(0, OPERATORS).forEach((n) -> employees.add(new Employee(EmployeeType.OPERATOR)));

        // 1.2 Supervisors (with directors as next hierarchy level)
        IntStream.range(0, SUPERVISORS).forEach((n) -> employees.add(new Employee(EmployeeType.SUPERVISOR)));

        // 1.3 Operators (with supervisors as next hierarchy level)
        IntStream.range(0, DIRECTORS).forEach((n) -> employees.add(new Employee(EmployeeType.DIRECTOR)));

        // 4. Create Dispatcher with employees
        callDispatcher = new CallDispatcher(employees, MAX_INCOMING_CALLS);
    }

    public CallCenter() {
        constructor();
    }

    public void call() {
        Call call = new Call();
        LOGGER.info(String.format("Arrived new Call [%s]", call.toString()));
        callDispatcher.dispatchCall(call);
    }

    public int getCountOfAnsweredCalls() {
        return callDispatcher.getTotalCalls();
    }

    public void stop() {
        callDispatcher.stop();
    }

    public void stopOnEndIncomingCalls() {
        try {
            callDispatcher.stopWhenAllCallsAreDispatched();
        } catch (InterruptedException e) {
            LOGGER.warn("Interrupted call dispatcher Stop");
        }
    }
}
