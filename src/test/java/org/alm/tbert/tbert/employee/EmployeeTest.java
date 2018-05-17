package org.alm.tbert.tbert.employee;


import org.alm.tbert.callcenter.employee.Director;
import org.alm.tbert.callcenter.employee.Employee;
import org.alm.tbert.callcenter.employee.Operator;
import org.alm.tbert.callcenter.employee.Supervisor;

import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

public class EmployeeTest {
    private static Director director;
    private static Operator operator;
    private static Supervisor supervisor;

    @BeforeClass
    public static void initEmployeeTest() {
        operator = new Operator("operator");
        supervisor = new Supervisor("supervisor");
        director = new Director("director");
    }

    @Test
    public void testEmployeeNames() {
        assertEquals(operator.getName(), "operator");
        assertEquals(supervisor.getName(), "supervisor");
        assertEquals(director.getName(), "director");
    }
}
