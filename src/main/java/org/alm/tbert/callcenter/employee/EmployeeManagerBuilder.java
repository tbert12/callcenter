package org.alm.tbert.callcenter.employee;

import org.alm.tbert.callcenter.employee.exception.EmployeeException;

public class EmployeeManagerBuilder {
    private int nEmployees;
    private EmployeeType type;
    private EmployeeManager nextHierarchyLevel;

    private EmployeeManagerBuilder() {
        nEmployees = 0;
        type = null;
        nextHierarchyLevel = null;
    }

    private EmployeeManagerBuilder setNumberOfEmployees(int nEmployees) {
        this.nEmployees = Math.abs(nEmployees);
        return this;
    }

    private EmployeeManagerBuilder setType(EmployeeType type) {
        this.type = type;
        return this;
    }

    private EmployeeManagerBuilder setNextHierarchyLevel(EmployeeManager employeeManager) {
        this.nextHierarchyLevel = employeeManager;
        return this;
    }

    private EmployeeManager build() {
        EmployeeManager employeeManager = (this.nextHierarchyLevel == null) ? new EmployeeManager(type) : new EmployeeManager(type, this.nextHierarchyLevel);
        for (int i = 0; i < nEmployees; i++) {
            employeeManager.addNewEmployee();
        }
        return employeeManager;
    }

    private static EmployeeManager newEmployeeManager(EmployeeType type, int nEmployees, EmployeeManager next) {
        return new EmployeeManagerBuilder()
                .setType(type)
                .setNumberOfEmployees(nEmployees)
                .setNextHierarchyLevel(next)
                .build();
    }

    public static EmployeeManager newOperatorEmployeeManager(int nEmployees) {
        return newEmployeeManager(EmployeeType.OPERATOR, nEmployees, null);
    }

    public static EmployeeManager newOperatorEmployeeManager(int nEmployees, EmployeeManager nextHierarchyLevel) {
        return newEmployeeManager(EmployeeType.OPERATOR, nEmployees, nextHierarchyLevel);
    }

    public static EmployeeManager newSupervisorEmployeeManager(int nEmployees) {
        return newEmployeeManager(EmployeeType.SUPERVISOR, nEmployees, null);
    }

    public static EmployeeManager newSupervisorEmployeeManager(int nEmployees, EmployeeManager nextHierarchyLevel) {
        return newEmployeeManager(EmployeeType.DIRECTOR, nEmployees, nextHierarchyLevel);
    }

    public static EmployeeManager newDirectorEmployeeManager(int nEmployees) {
        return newEmployeeManager(EmployeeType.DIRECTOR, nEmployees, null);
    }

}
