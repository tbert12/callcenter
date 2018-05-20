package org.alm.tbert.callcenter.employee;

import org.alm.tbert.callcenter.employee.exception.EmployeeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmployeeManager {
    private final List<Employee> freeEmployees;
    private EmployeeManager nextHierarchyLevel;
    private final EmployeeType employeeType;

    EmployeeManager(EmployeeType type) {
        this.freeEmployees = Collections.synchronizedList(new ArrayList<>());
        this.nextHierarchyLevel = null;
        this.employeeType = type;
    }

    EmployeeManager(EmployeeType type, EmployeeManager nextHierarchyLevel) {
        this.freeEmployees = Collections.synchronizedList(new ArrayList<>());
        this.nextHierarchyLevel = nextHierarchyLevel;
        this.employeeType = type;
    }

    int getNumberOfEmployees() {
        return freeEmployees.size();
    }

    private boolean isCircularHierarchy(EmployeeManager nextHierarchyLevel) {
        while (this != nextHierarchyLevel && nextHierarchyLevel != null) {
            nextHierarchyLevel = nextHierarchyLevel.getNextHierarchyLevel();
        }
        return nextHierarchyLevel != null;
    }

    void setNextHierarchyLevel(EmployeeManager nextHierarchyLevel) throws EmployeeException {
        if (isCircularHierarchy(nextHierarchyLevel)) {
            throw new EmployeeException("Circular Hierarchy level");
        }
        this.nextHierarchyLevel = nextHierarchyLevel;
    }

    EmployeeManager getNextHierarchyLevel() {
        return this.nextHierarchyLevel;
    }

    boolean hasNextHierarchyLevel() {
        return nextHierarchyLevel != null;
    }


    void addNewEmployee() {
        freeEmployees.add( new Employee(employeeType) );
    }

    void add(Employee employee) throws EmployeeException {
        if (employee.getType() != employeeType) {
            throw new EmployeeException(String.format("Only accept employee with type %s", employeeType));
        }
        freeEmployees.add(employee);
    }

    synchronized boolean existFreeEmployee() {
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


    EmployeeType getEmployeesType() {
        return this.employeeType;
    }

    public int countTotalEmployees() {
        int nextHierarchyLevelCount = this.hasNextHierarchyLevel() ? this.getNextHierarchyLevel().getNumberOfEmployees() : 0;
        return this.getNumberOfEmployees() + nextHierarchyLevelCount;
    }

    public EmployeeManager getAvailableEmployeeManager() throws EmployeeException {
        if (existFreeEmployee()) {
            return this;
        }
        if (hasNextHierarchyLevel()) {
            return getNextHierarchyLevel().getAvailableEmployeeManager();
        }
        throw new EmployeeException("No available Employee Manager");

    }
}
