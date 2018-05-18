package org.alm.tbert.callcenter;

import org.alm.tbert.callcenter.employee.Employee;
import org.alm.tbert.callcenter.employee.EmployeeManager;
import org.alm.tbert.callcenter.employee.exception.EmployeeException;
import org.apache.log4j.Logger;

class CallDispatcher {
    private static final Logger LOGGER = Logger.getLogger(CallDispatcher.class);

    private final EmployeeManager operators;

    CallDispatcher(EmployeeManager operators) {
        this.operators = operators;
    }

    private EmployeeManager getManagerWithFreeEmployees() {
        // Chain of responsibilities
        EmployeeManager employeeManager = operators;
        while (!employeeManager.existFreeEmployee() && employeeManager.hasNextHierarchyLevel()) {
            employeeManager = employeeManager.getNextHierarchyLevel();
        }
        return employeeManager;
    }

    public void dispatchCall(Call call) {
        try {
            LOGGER.info(String.format("New call to dispatch [%s]", call.toString()));
            EmployeeManager employeeManager = getManagerWithFreeEmployees();
            Employee employee = employeeManager.takeEmployee();
            LOGGER.info( String.format("Dispatch to employee '%s' call [%s]", employee.toString(), call.toString()) );
            employee.take(call);
            employeeManager.freeEmployee(employee);
            LOGGER.info( String.format("Employee '%s' end the call [%s]", employee.toString(), call.toString()) );
        } catch (EmployeeException e) {
            LOGGER.error("Cannot dispatch call. No free employee");
            LOGGER.debug(e);
        }
    }
}
