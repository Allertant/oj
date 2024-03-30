package com.shiyixi.ojbackendemailservice.strategy;

import com.shiyixi.ojbackendemailservice.model.EmailSendObj;

public interface EmailStrategy {
    EmailSendObj sendEmail(String message, int arg);
}
