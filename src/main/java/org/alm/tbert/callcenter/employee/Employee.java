package org.alm.tbert.callcenter.employee;

import org.alm.tbert.callcenter.Call;
import org.alm.tbert.callcenter.employee.exception.EmployeeException;

import java.util.UUID;

public class Employee {
    private final EmployeeType type;
    private boolean isFree;

    //---- DEBUG Attribute
    private final String _UUID;
    //----

    public Employee(EmployeeType type) {
        this.isFree = true;
        this.type = type;
        this._UUID = UUID.randomUUID().toString().replace("-","");
    }

    public EmployeeType getType() {
        return this.type;
    }

    public boolean isFree() {
        return this.isFree;
    }

    private void answer(Call call) {
        call.accept();
    }

    public void take(Call call) throws EmployeeException {
        if (!this.isFree()) {
            throw new EmployeeException(String.format("Employee type '%s' is not free to take a call", this.getType()));
        }
        this.isFree = false;
        this.answer(call);
        this.isFree = true;
    }

    @Override
    public String toString() {
        return this.getType() + "::" + this._UUID;
    }
}
