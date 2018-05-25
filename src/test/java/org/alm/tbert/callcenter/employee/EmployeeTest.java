package org.alm.tbert.callcenter.employee;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.alm.tbert.callcenter.Call;
import org.alm.tbert.callcenter.employee.exception.EmployeeException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


import java.util.PriorityQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EmployeeTest {
    private static Employee director;
    private static Employee operator;
    private static Employee supervisor;
    private static ScheduledExecutorService executor;

    @BeforeClass
    public static void initEmployeeTest() {
        operator = new Employee(EmployeeType.OPERATOR);
        supervisor = new Employee(EmployeeType.SUPERVISOR);
        director = new Employee(EmployeeType.DIRECTOR);
        executor = Executors.newScheduledThreadPool(5);
    }

    @AfterClass
    public static void endEmployeeTest() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testEmployeeNames() {
        assertEquals(operator.getType(), EmployeeType.OPERATOR);
        assertEquals(supervisor.getType(), EmployeeType.SUPERVISOR);
        assertEquals(director.getType(), EmployeeType.DIRECTOR);
    }

    @Test
    public void testEmployeeCallIsNotFree() throws InterruptedException {
        assertTrue(director.isFree());
        executor.submit(() -> {
            try {
                director.take( new Call(2) );
            } catch (EmployeeException ignored) {
            }
        });

        executor.scheduleWithFixedDelay(() -> assertFalse(director.isFree()), 0, 1, TimeUnit.SECONDS);

        TimeUnit.SECONDS.sleep(3);

        assertTrue(director.isFree());
    }

    @Test(expected = EmployeeException.class)
    public void testEmployeeCannotTakeTwoCall() throws InterruptedException, EmployeeException {
        executor.submit(() -> {
            try {
                operator.take( new Call(2) );
            } catch (EmployeeException ignored) {}
        });
        TimeUnit.SECONDS.sleep(1);
        operator.take( new Call(3) );

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        TimeUnit.SECONDS.sleep(3);
        assertTrue(operator.isFree());

    }

    @Test
    public void testEmployeeComparator() {
        Employee otherSupervisor = new Employee(EmployeeType.SUPERVISOR);
        assertEquals(0, supervisor.compareTo(otherSupervisor));

        assertEquals(-1, operator.compareTo(supervisor));
        assertEquals(-1, operator.compareTo(director));
        assertEquals(-1, supervisor.compareTo(director));

        assertEquals(1, supervisor.compareTo(operator));
        assertEquals(1, director.compareTo(supervisor));

        PriorityQueue<Employee> employees = new PriorityQueue<>();
        employees.add(supervisor);
        employees.add(director);
        employees.add(operator);
        assertEquals(operator, employees.poll());
        assertEquals(supervisor, employees.poll());
        assertEquals(director, employees.poll());
    }
}
