package com.lara.userservice.service.impl;

import com.lara.userservice.service.EmailService;
import com.lara.userservice.utils.EmailUtils;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String USER_HOME = "user.home";
    public static final String NEW_USER_ACCOUNT_VERIFICATION_SUBJECT = "New user account verification";
    @Value("${spring.mail.verify.host}")
    private String host;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

    @Async
    @Override
    public void sendSimpleMailMessage(String name, String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(NEW_USER_ACCOUNT_VERIFICATION_SUBJECT);
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setText(EmailUtils.getEmailMessage(name, host, token));
            emailSender.send(message);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Override
    public void sendMimeMailMessageWithAttachments(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION_SUBJECT);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(EmailUtils.getEmailMessage(name, host, token));
            // Add attachments
            FileSystemResource firstImage = new FileSystemResource(new File(System.getProperty(USER_HOME) + "/Downloads/images/ps5-sams.png"));
            FileSystemResource secondImage = new FileSystemResource(new File(System.getProperty(USER_HOME) + "/Downloads/images/ps5-promo.png"));
            FileSystemResource cvDocx = new FileSystemResource(new File(System.getProperty(USER_HOME) + "/Downloads/images/CV_ISITA_LUIS_RIVERA_JAVA.docx"));
            helper.addAttachment(Objects.requireNonNull(firstImage.getFilename()), firstImage);
            helper.addAttachment(Objects.requireNonNull(secondImage.getFilename()), secondImage);
            helper.addAttachment(Objects.requireNonNull(cvDocx.getFilename()), cvDocx);
            emailSender.send(message);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Override
    public void sendMimeMessageWithEmbeddedImages(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION_SUBJECT);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(EmailUtils.getEmailMessage(name, host, token));
            // Add attachments
            FileSystemResource firstImage = new FileSystemResource(new File(System.getProperty(USER_HOME) + "/Downloads/images/ps5-sams.png"));
            FileSystemResource secondImage = new FileSystemResource(new File(System.getProperty(USER_HOME) + "/Downloads/images/ps5-promo.png"));
            FileSystemResource cvDocx = new FileSystemResource(new File(System.getProperty(USER_HOME) + "/Downloads/images/CV_ISITA_LUIS_RIVERA_JAVA.docx"));
            helper.addInline(getContentId(firstImage.getFilename()), firstImage);
            helper.addInline(getContentId(secondImage.getFilename()), secondImage);
            helper.addInline(getContentId(cvDocx.getFilename()), cvDocx);
            emailSender.send(message);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Override
    public void sendHTMLEmail(String name, String to, String token) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("url", EmailUtils.getVerificationUrl(host, token));
            String text = templateEngine.process("emailtemplate", context);
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION_SUBJECT);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(text, true);
            emailSender.send(message);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Override
    public void sendHTMLEmailWithEmbeddedFiles(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION_SUBJECT);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("url", EmailUtils.getVerificationUrl(host, token));
            String text = templateEngine.process("emailtemplate", context);
            // Add HTML email body
            MimeMultipart mimeMultipart = new MimeMultipart("related");
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(text, "text/html");
            mimeMultipart.addBodyPart(messageBodyPart);

            // Adding images to the email body
            BodyPart imageBodyPart = new MimeBodyPart();
            DataSource dataSource = new FileDataSource(System.getProperty(USER_HOME) + "/Downloads/images/ps5-sams.png");
            imageBodyPart.setDataHandler(new DataHandler(dataSource));
            imageBodyPart.setHeader("Content-ID", "image");
            mimeMultipart.addBodyPart(imageBodyPart);

            message.setContent(mimeMultipart);

            emailSender.send(message);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private MimeMessage getMimeMessage() {
        return emailSender.createMimeMessage();
    }

    private String getContentId(String fileName) {
        return "<" + fileName + ">";
    }

}
