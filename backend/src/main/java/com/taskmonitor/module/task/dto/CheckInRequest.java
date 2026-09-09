package com.taskmonitor.module.task.dto;

import jakarta.validation.constraints.Min;

public class CheckInRequest {

    @Min(value = 0, message = "Additional minutes spent cannot be negative")
    private int additionalMinutes;

    private String note;

    public CheckInRequest() {
    }

    public CheckInRequest(int additionalMinutes, String note) {
        this.additionalMinutes = additionalMinutes;
        this.note = note;
    }

    public int getAdditionalMinutes() { return additionalMinutes; }
    public void setAdditionalMinutes(int additionalMinutes) { this.additionalMinutes = additionalMinutes; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
