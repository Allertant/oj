package com.shiyixi.ojbackendmodel.email;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmailRequest implements Serializable {
    private String message;
    private String emailType;
    private String to;
    private int arg;

    private static final long serialVersionUID = 857866069957337607L;
}
