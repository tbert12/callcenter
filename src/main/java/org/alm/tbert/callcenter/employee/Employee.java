package org.alm.tbert.callcenter.employee;

import org.alm.tbert.callcenter.Call;
import org.alm.tbert.callcenter.employee.exception.EmployeeException;
import org.apache.log4j.Logger;

public class Employee {
    private static final Logger LOGGER = Logger.getLogger(Employee.class);

    private final EmployeeType type;
    private boolean isFree;

    public Employee(EmployeeType type) {
        this.isFree = true;
        this.type = type;
    }

    public EmployeeType getType() {
        return this.type;
    }

    public boolean isFree() {
        return this.isFree;
    }

    public void answer(Call call) {
        LOGGER.info(String.format("Employee type '%s' take call", this.getType()));
        call.accept();
        LOGGER.info(String.format("Employee type '%s' finish call successfully", this.getType()));
    }

    public void take(Call call) throws EmployeeException {
        if (!this.isFree()) {
            throw new EmployeeException(String.format("Employee type '%s' is not free to take a call", this.getType()));
        }
        this.isFree = false;
        this.answer(call);
        this.isFree = true;
    }
}
