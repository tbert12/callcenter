package org.alm.tbert.callcenter.employee;

import static org.junit.Assert.*;
import org.junit.Test;

public class EmployeeManagerBuilderTest {

    @Test
    public void testStaticBuilders() {
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
