package org.alm.tbert.callcenter.employee;

import org.alm.tbert.callcenter.employee.exception.EmployeeException;

import java.util.ArrayList;
import java.util.List;

public class EmployeeManager {
    private List<Employee> freeEmployees;
    private List<Employee> occupiedEmployees;

    public EmployeeManager() {
        freeEmployees = new ArrayList<Employee>();
        occupiedEmployees = new ArrayList<Employee>();
    }

    public void add(Employee employee) {
        freeEmployees.add(employee);
    }

    public boolean freeEmployee() {
        return !freeEmployees.isEmpty();
    }

    public Employee takeEmployee() throws EmployeeException {
        if (this.freeEmployee()) {
            Employee employee = freeEmployees.remove(0);
            occupiedEmployees.add(employee);
            return employee;
        }
        throw new EmployeeException("No free employee");
    }
}
