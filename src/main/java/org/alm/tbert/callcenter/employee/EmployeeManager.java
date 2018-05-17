package org.alm.tbert.callcenter.employee;

import org.alm.tbert.callcenter.employee.exception.EmployeeException;

import java.util.ArrayList;
import java.util.List;

public class EmployeeManager {
    private List<Employee> freeEmployees;
    private EmployeeManager nextHierarchyLevel;
    private final EmployeeType employeeType;

    public EmployeeManager(EmployeeType type) {
        freeEmployees = new ArrayList<>();
        nextHierarchyLevel = null;
        employeeType = type;
    }

    private boolean isCircularHierarchy(EmployeeManager nextHierarchyLevel) {
        while (this != nextHierarchyLevel && nextHierarchyLevel != null) {
            nextHierarchyLevel = nextHierarchyLevel.getNextHierarchyLevel();
        }
        return nextHierarchyLevel != null;
    }

    public void setNextHierarchyLevel(EmployeeManager nextHierarchyLevel) throws EmployeeException {
        if (isCircularHierarchy(nextHierarchyLevel)) {
            throw new EmployeeException("Circular Hierarchy level");
        }
        this.nextHierarchyLevel = nextHierarchyLevel;
    }

    public EmployeeManager getNextHierarchyLevel() {
        return this.nextHierarchyLevel;
    }

    public boolean hasNextHierarchyLevel() {
        return nextHierarchyLevel != null;
    }

    public void addNewEmployee() {
        freeEmployees.add( new Employee(employeeType) );
    }

    public void add(Employee employee) throws EmployeeException {
        if (employee.getType() != employeeType) {
            throw new EmployeeException(String.format("Only accept employee with type %s", employeeType));
        }
        freeEmployees.add(employee);
    }

    public synchronized boolean existFreeEmployee() {
        return !freeEmployees.isEmpty();
    }

    public synchronized Employee takeEmployee() throws EmployeeException {
        if (this.existFreeEmployee()) {
            return freeEmployees.remove(0);
        }
        throw new EmployeeException("No free employee");
    }

    public synchronized void freeEmployee(Employee employee) throws EmployeeException {
        add(employee);
    }


}
