package com.app.controlgastos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.mail.MailException;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String subject, String text) {
        log.info("====== SIMULACIÓN DE CORREO ======");
        log.info("Destinatario: {}", to);
        log.info("Asunto: {}", subject);
        log.info("Contenido:\n{}", text);
        log.info("====================================");

        if (mailSender != null) {
            try {
                log.info("Intentando enviar correo real a: {}", to);
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(text, true);
                
                mailSender.send(message);
                log.info("¡Correo enviado exitosamente a {}!", to);
            } catch (MailException e) {
                log.error("Error de Spring Mail al enviar correo a {}: {}", to, e.getMessage(), e);
            } catch (Exception e) {
                log.error("Error inesperado al enviar correo a {}: {}", to, e.getMessage(), e);
            }
        } else {
            log.warn("JavaMailSender es NULL. El correo no será enviado porque falta la configuración SMTP en application.properties");
        }
    }
}
