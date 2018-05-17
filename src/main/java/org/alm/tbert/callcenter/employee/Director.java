package org.alm.tbert.callcenter.employee;

import org.alm.tbert.callcenter.Call;
import org.apache.log4j.Logger;

public class Director extends Employee {
    private static final Logger LOGGER = Logger.getLogger(Call.class);


    public Director(String name) {
        super(name);
    }

    public void answer(Call call) {
        LOGGER.info(String.format("Director '%s' take call", this.getName()));
        call.accept();
        LOGGER.info(String.format("Director '%s' finish call successfully", this.getName()));
    }
}
