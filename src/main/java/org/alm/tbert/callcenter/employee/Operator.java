package org.alm.tbert.callcenter.employee;

import org.alm.tbert.callcenter.Call;
import org.apache.log4j.Logger;

public class Operator extends Employee{
    private static final Logger LOGGER = Logger.getLogger(Operator.class);

    public Operator(String name) {
        super(name);
    }

    public void answer(Call call) {
        LOGGER.info(String.format("Operator '%s' take call", this.getName()));
        call.accept();
        LOGGER.info(String.format("Operator '%s' finish call successfully", this.getName()));
    }
}
