package com.vvvteam.yuglightservice.domain.validators;

public class Command {
    private String commandTypeId;
    private String deviceEui;

    public Command() {
    }

    public String getCommandTypeId() {
        return commandTypeId;
    }

    public void setCommandTypeId(String commandTypeId) {
        this.commandTypeId = commandTypeId;
    }

    public String getDeviceEui() {
        return deviceEui;
    }

    public void setDeviceEui(String deviceEui) {
        this.deviceEui = deviceEui;
    }
}
