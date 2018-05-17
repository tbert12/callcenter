package org.alm.tbert.tbert.employee;

import org.alm.tbert.callcenter.employee.EmployeeManager;
import org.alm.tbert.callcenter.employee.EmployeeManagerBuilder;
import org.alm.tbert.callcenter.employee.EmployeeType;
import org.alm.tbert.callcenter.employee.exception.EmployeeException;

import static org.junit.Assert.*;
import org.junit.Test;

public class EmployeeManagerBuilderTest {

    @Test
    public void testStaticBuilders() throws EmployeeException {
        EmployeeManager employeeManager = EmployeeManagerBuilder.newOperatorEmployeeManager(10);
        assertEquals(employeeManager.getNumberOfEmployees(), 10);
        assertEquals(employeeManager.getEmployeesType(), EmployeeType.OPERATOR);
        assertFalse(employeeManager.hasNextHierarchyLevel());

        employeeManager = EmployeeManagerBuilder.newSupervisorEmployeeManager(10);
        assertEquals(employeeManager.getEmployeesType(), EmployeeType.SUPERVISOR);


        EmployeeManager employeeManager1 = EmployeeManagerBuilder.newSupervisorEmployeeManager(10, employeeManager);
        assertTrue(employeeManager1.hasNextHierarchyLevel());
        assertEquals(employeeManager1.getNextHierarchyLevel(), employeeManager);
    }



}
