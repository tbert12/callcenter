package org.alm.tbert.tbert.employee;

import org.alm.tbert.callcenter.employee.Employee;
import org.alm.tbert.callcenter.employee.EmployeeManager;
import org.alm.tbert.callcenter.employee.EmployeeType;

import static org.junit.Assert.*;

import org.alm.tbert.callcenter.employee.exception.EmployeeException;
import org.junit.BeforeClass;
import org.junit.Test;

public class EmployeeManagerTest {
    private static EmployeeManager operators;
    private static EmployeeManager supervisors;
    private static EmployeeManager directors;

    @BeforeClass
    public static void initEmployeeManagerTest() {
        operators = new EmployeeManager(EmployeeType.OPERATOR);
        supervisors = new EmployeeManager(EmployeeType.SUPERVISOR);
        directors = new EmployeeManager(EmployeeType.DIRECTOR);
    }

    @Test
    public void testFreeEmployees() throws EmployeeException {
        assertFalse(operators.existFreeEmployee());
        assertEquals(operators.getNumberOfEmployees(), 0);
        operators.addNewEmployee();
        assertEquals(operators.getNumberOfEmployees(), 1);
        assertTrue(operators.existFreeEmployee());

        Employee freeEmployee = operators.takeEmployee();
        assertEquals(operators.getNumberOfEmployees(), 0);
        assertFalse(operators.existFreeEmployee());
        operators.freeEmployee(freeEmployee);
        assertEquals(operators.getNumberOfEmployees(), 1);
        assertTrue(operators.existFreeEmployee());

        operators.addNewEmployee();
        assertEquals(operators.getNumberOfEmployees(), 2);
        assertTrue(operators.existFreeEmployee());
        operators.takeEmployee();
        assertTrue(operators.existFreeEmployee());
        operators.takeEmployee();
        assertFalse(operators.existFreeEmployee());
    }

    @Test(expected = EmployeeException.class)
    public void testAddDifferentTypeOfEmployee() throws EmployeeException {
        Employee operatorEmployee = new Employee(EmployeeType.OPERATOR);
        directors.add(operatorEmployee);
    }

    @Test(expected = EmployeeException.class)
    public void testCircularHierarchyLevel() throws EmployeeException {
        operators.setNextHierarchyLevel(operators);
    }

    @Test(expected = EmployeeException.class)
    public void testCircularHierarchyLevelWithThreeEmployeeManager() throws EmployeeException {
        operators.setNextHierarchyLevel(supervisors);
        supervisors.setNextHierarchyLevel(directors);
        directors.setNextHierarchyLevel(operators);

    }
}
