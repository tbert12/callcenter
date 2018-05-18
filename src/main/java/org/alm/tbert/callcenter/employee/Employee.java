package org.alm.tbert.callcenter.employee;

import org.alm.tbert.callcenter.Call;
import org.alm.tbert.callcenter.employee.exception.EmployeeException;

public class Employee {
    private final EmployeeType type;
    private boolean isFree;

    //---- DEBUG Attribute
    private static int _uuid = 0;
    private final String _UUID;
    private String _generateUUID() {
        _uuid++;
        return String.valueOf(_uuid);
    }
    //----

    public Employee(EmployeeType type) {
        this.isFree = true;
        this.type = type;
        this._UUID = _generateUUID();
    }

    EmployeeType getType() {
        return this.type;
    }

    boolean isFree() {
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
