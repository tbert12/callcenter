package org.alm.tbert.callcenter;

import org.alm.tbert.callcenter.employee.Employee;
import org.alm.tbert.callcenter.employee.EmployeeManager;
import org.alm.tbert.callcenter.employee.exception.EmployeeException;
import org.apache.log4j.Logger;

public class CallDispatcher {
    private static final Logger LOGGER = Logger.getLogger(CallDispatcher.class);

    private final EmployeeManager operators;

    CallDispatcher(EmployeeManager operators) {
        this.operators = operators;
    }

    private EmployeeManager getManagerWithFreeEmployees() {
        EmployeeManager employeeManager = operators;
        while (!employeeManager.existFreeEmployee()) {
            employeeManager = employeeManager.getNextHierarchyLevel();
        }
        return employeeManager;
    }

    public void dispatchCall(Call call) {
        // 1. Take operators
        // 2. If the operators are busy. Take supervisor
        // 3. If the supervisor are busy. Take director
        try {
            EmployeeManager employeeManager = getManagerWithFreeEmployees();
            Employee employee = employeeManager.takeEmployee();
            LOGGER.info( String.format("Employee '%s' will take the call", employee.toString()) );
            employee.take(call);
            employeeManager.freeEmployee(employee);
            LOGGER.info( String.format("Employee '%s' end the call", employee.toString()) );
        } catch (EmployeeException e) {
            LOGGER.error("Cannot dispatch call. No free employee");
            LOGGER.debug(e);
        }
    }
}
