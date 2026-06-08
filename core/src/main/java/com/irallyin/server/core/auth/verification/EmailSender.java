package com.irallyin.server.core.auth.verification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Simple email sender using Jakarta Mail. Reads smtp config from classpath:/verification.properties
 * The properties file should include at least:
 * smtp.host, smtp.port, smtp.username, smtp.password, smtp.ssl (true/false)
 */
public class EmailSender {

    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

    private final Properties props = new Properties();
    private final String username;
    private final String password;

    public EmailSender() throws IOException {
        // Prefer the file named by IRALLYIN_VARIABLES_FILE, fallback order:
        // 1) classpath:/${IRALLYIN_VARIABLES_FILE}
        // 2) classpath:/verification.properties
        // 3) file at ${user.dir}/${IRALLYIN_VARIABLES_FILE}
        // 4) file at ${user.dir}/verification.properties
        String variablesFile = System.getenv().getOrDefault("IRALLYIN_VARIABLES_FILE", "irallyin-variables.properties");
        InputStream in = getClass().getClassLoader().getResourceAsStream(variablesFile);
        if (in == null) {
            in = getClass().getClassLoader().getResourceAsStream("verification.properties");
        }
        if (in == null) {
            // try project root files
            String wd = System.getProperty("user.dir");
            File f1 = new File(wd, variablesFile);
            File f2 = new File(wd, "verification.properties");
            try {
                if (f1.exists()) in = new FileInputStream(f1);
                else if (f2.exists()) in = new FileInputStream(f2);
            } catch (IOException e) {
                // will handle below
            }
        }
        if (in == null) throw new IOException(variablesFile + " (or verification.properties) not found on classpath or project root");
        try (InputStream input = in) {
            props.load(input);
        }
        username = props.getProperty("smtp.username");
        password = props.getProperty("smtp.password");
    }

    public void send(String to, String subject, String body) throws MessagingException {
        send(to, subject, body, false);
    }

    public void send(String to, String subject, String body, boolean html) throws MessagingException {
        try {
        Properties sessionProps = new Properties();
        sessionProps.put("mail.smtp.auth", props.getProperty("smtp.auth", "true"));
        String ssl = props.getProperty("smtp.ssl", "true");
        if ("true".equalsIgnoreCase(ssl)) {
            sessionProps.put("mail.smtp.ssl.enable", "true");
            // trust the SMTP host to avoid SSL certificate issues in some environments
            sessionProps.put("mail.smtp.ssl.trust", props.getProperty("smtp.host"));
        } else {
            sessionProps.put("mail.smtp.starttls.enable", props.getProperty("smtp.starttls", "true"));
        }
        sessionProps.put("mail.smtp.host", props.getProperty("smtp.host"));
        sessionProps.put("mail.smtp.port", props.getProperty("smtp.port"));

        Session session = Session.getInstance(sessionProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        // enable JavaMail debug if configured in properties for this session as well
        try {
            boolean debug = Boolean.parseBoolean(props.getProperty("smtp.debug", "false"));
            session.setDebug(debug);
        } catch (Exception ignored) {
        }
        // enable JavaMail debug if configured in properties (smtp.debug=true)
        try {
            boolean debug = Boolean.parseBoolean(props.getProperty("smtp.debug", "false"));
            session.setDebug(debug);
        } catch (Exception ignored) {
        }

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(props.getProperty("smtp.from", username)));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject);
        if (html) {
            msg.setContent(body, "text/html; charset=UTF-8");
        } else {
            msg.setText(body);
        }

        Transport.send(msg);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw e;
        }
    }

    public void send(String to, com.irallyin.server.core.mail.template.EmailTemplate tpl) throws MessagingException {
        if (tpl == null) throw new IllegalArgumentException("EmailTemplate is null");
        try {
        Properties sessionProps = new Properties();
        sessionProps.put("mail.smtp.auth", props.getProperty("smtp.auth", "true"));
        String ssl = props.getProperty("smtp.ssl", "true");
        if ("true".equalsIgnoreCase(ssl)) {
            sessionProps.put("mail.smtp.ssl.enable", "true");
        } else {
            sessionProps.put("mail.smtp.starttls.enable", props.getProperty("smtp.starttls", "true"));
        }
        sessionProps.put("mail.smtp.host", props.getProperty("smtp.host"));
        sessionProps.put("mail.smtp.port", props.getProperty("smtp.port"));

        Session session = Session.getInstance(sessionProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(props.getProperty("smtp.from", username)));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(tpl.getSubject());

        if (tpl.isHtml()) {
            String htmlBody = tpl.getBody();
            String textBody = tpl.getTextBody() != null ? tpl.getTextBody() : stripHtml(htmlBody);

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(textBody, "UTF-8");

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlBody, "text/html; charset=UTF-8");

            Multipart mp = new MimeMultipart("alternative");
            mp.addBodyPart(textPart);
            mp.addBodyPart(htmlPart);
            msg.setContent(mp);
        } else {
            String text = tpl.getBody();
            msg.setText(text);
        }

        Transport.send(msg);
        } catch (MessagingException e) {
            log.error("Failed to send template email to {}: {}", to, e.getMessage(), e);
            throw e;
        }
    }

    private String stripHtml(String html) {
        if (html == null) return "";
        return html.replaceAll("\\<[^>]*>", "");
    }
}
