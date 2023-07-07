package com.lara.userservice.service;

public interface EmailService {

    void sendSimpleMailMessage(String name, String to, String token);
    void sendMimeMailMessageWithAttachments(String name, String to, String token);
    void sendMimeMessageWithEmbeddedImages(String name, String to, String token);
    void sendHTMLEmail(String name, String to, String token);
    void sendHTMLEmailWithEmbeddedFiles(String name, String to, String token);


}
