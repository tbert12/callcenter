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

    private EmployeeManager build() throws EmployeeException {
        EmployeeManager employeeManager = new EmployeeManager(type);
        for (int i = 0; i < nEmployees; i++) {
            employeeManager.addNewEmployee();
        }
        employeeManager.setNextHierarchyLevel(this.nextHierarchyLevel);
        return employeeManager;
    }

    private static EmployeeManagerBuilder newEmployeeManager(EmployeeType type, int nEmployees) throws EmployeeException {
        return new EmployeeManagerBuilder()
                .setType(type)
                .setNumberOfEmployees(nEmployees);
    }

    public static EmployeeManager newOperatorEmployeeManager(int nEmployees) throws EmployeeException {
        return newEmployeeManager(EmployeeType.OPERATOR, nEmployees).build();
    }

    public static EmployeeManager newOperatorEmployeeManager(int nEmployees, EmployeeManager nextHierarchyLevel)
            throws EmployeeException {
        return newEmployeeManager(EmployeeType.OPERATOR, nEmployees)
                .setNextHierarchyLevel(nextHierarchyLevel)
                .build();
    }

    public static EmployeeManager newSupervisorEmployeeManager(int nEmployees) throws EmployeeException {
        return newEmployeeManager(EmployeeType.SUPERVISOR, nEmployees).build();
    }

    public static EmployeeManager newSupervisorEmployeeManager(int nEmployees, EmployeeManager nextHierarchyLevel)
            throws EmployeeException {
        return newEmployeeManager(EmployeeType.DIRECTOR, nEmployees)
                .setNextHierarchyLevel(nextHierarchyLevel)
                .build();
    }

    public static EmployeeManager newDirectorEmployeeManager(int nEmployees) throws EmployeeException {
        return newEmployeeManager(EmployeeType.DIRECTOR, nEmployees).build();
    }

}
