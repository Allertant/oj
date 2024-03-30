package com.shiyixi.ojbackendemailservice.model;

import lombok.Data;

@Data
public class EmailSendObj {
    public String to;
    public String message;
    public String type;
    public String subject;
}
