package org.alm.tbert.callcenter.employee;

import org.alm.tbert.callcenter.Call;
import org.apache.log4j.Logger;

public class Supervisor extends Employee {
    private static final Logger LOGGER = Logger.getLogger(Supervisor.class);

    public Supervisor(String name) {
        super(name);
    }

    public void answer(Call call) {
        LOGGER.info(String.format("Supervisor '%s' take call", this.getName()));
        call.accept();
        LOGGER.info(String.format("Supervisor '%s' finish call successfully", this.getName()));
    }
}
