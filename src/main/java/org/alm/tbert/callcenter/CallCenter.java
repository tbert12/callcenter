package org.alm.tbert.callcenter;

import org.alm.tbert.callcenter.employee.EmployeeManager;
import org.alm.tbert.callcenter.employee.EmployeeManagerBuilder;
import org.apache.log4j.Logger;

public class CallCenter {
    private static final Logger LOGGER = Logger.getLogger(CallCenter.class);

    private static final int MAX_INCOMING_CALLS = 10;
    private static final int OPERATORS          = 3;
    private static final int SUPERVISORS        = 3;
    private static final int DIRECTORS          = 3;
    private CallDispatcher callDispatcher;

    private void constructor() {
        // 1. Employee Managers with Employees
        // 1.1 Directors
        EmployeeManager directors = EmployeeManagerBuilder.newDirectorEmployeeManager(DIRECTORS);

        // 1.2 Supervisors (with directors as next hierarchy level)
        EmployeeManager supervisors = EmployeeManagerBuilder.newSupervisorEmployeeManager(SUPERVISORS, directors);

        // 1.3 Operators (with supervisors as next hierarchy level)
        EmployeeManager operators = EmployeeManagerBuilder.newOperatorEmployeeManager(OPERATORS, supervisors);

        // 4. Create Dispatcher with first level
        callDispatcher = new CallDispatcher(operators, MAX_INCOMING_CALLS);
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
