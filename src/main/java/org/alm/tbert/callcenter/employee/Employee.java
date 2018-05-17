package org.alm.tbert.callcenter.employee;

import org.alm.tbert.callcenter.Call;

public abstract class Employee {
    private final String name;
    private boolean isFree;

    Employee(String name) {
        this.name = name;
        this.isFree = true;
    }

    public String getName() {
        return this.name;
    }

    public boolean isFree() {
        return isFree;
    }

    public abstract void answer(Call call);

    public void take(Call call) {
        this.isFree = false;
        this.answer(call);
        this.isFree = true;
    }

}
