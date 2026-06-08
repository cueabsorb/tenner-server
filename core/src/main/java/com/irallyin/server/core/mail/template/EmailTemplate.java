package com.irallyin.server.core.mail.template;

/**
 * Simple container for rendered email subject and body.
 */
public class EmailTemplate {
    private final String subject;
    private final String body;
    private final boolean html;
    private final String textBody;

    public EmailTemplate(String subject, String body) {
        this(subject, body, false);
    }

    public EmailTemplate(String subject, String body, boolean html) {
        this.subject = subject;
        this.body = body;
        this.html = html;
        this.textBody = null;
    }

    public EmailTemplate(String subject, String body, boolean html, String textBody) {
        this.subject = subject;
        this.body = body;
        this.html = html;
        this.textBody = textBody;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public boolean isHtml() {
        return html;
    }

    public String getTextBody() {
        return textBody;
    }
}

