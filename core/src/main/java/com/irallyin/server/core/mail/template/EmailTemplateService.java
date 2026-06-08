package com.irallyin.server.core.mail.template;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Service to render email templates using Thymeleaf. Templates are located under
 * classpath:/mail-templates/ and use the TEXT template mode for simple text emails.
 */
public class EmailTemplateService {

    private final TemplateEngine engine;

    public EmailTemplateService() {
        // Resolver for text templates
        ClassLoaderTemplateResolver textResolver = new ClassLoaderTemplateResolver();
        textResolver.setPrefix("mail-templates/");
        textResolver.setSuffix(".txt");
        textResolver.setTemplateMode("TEXT");
        textResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        textResolver.setCacheable(false);

        // Resolver for HTML templates
        ClassLoaderTemplateResolver htmlResolver = new ClassLoaderTemplateResolver();
        htmlResolver.setPrefix("mail-templates/");
        htmlResolver.setSuffix(".html");
        htmlResolver.setTemplateMode("HTML");
        htmlResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        htmlResolver.setCacheable(false);

        engine = new TemplateEngine();
        engine.addTemplateResolver(htmlResolver);
        engine.addTemplateResolver(textResolver);
    }

    /**
     * Render a template by name with given variables.
     */
    public String render(String templateName, Map<String, Object> variables) {
        Context ctx = new Context();
        if (variables != null) ctx.setVariables(variables);
        return engine.process(templateName, ctx);
    }

    /**
     * Convenience: render the verification code template.
     */
    public EmailTemplate renderVerificationCode(String code) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("code", code);
        // Prefer HTML template if available
        String htmlResource = "mail-templates/verification.html";
        boolean htmlExists = getClass().getClassLoader().getResource(htmlResource) != null;
        String body = render("verification", vars);
        String subject = "你的验证码";
        if (htmlExists) {
            Context ctx = new Context();
            ctx.setVariables(vars);
            String htmlBody = engine.process("verification", ctx);
            // inline CSS from <style> blocks into element style attributes
            String inlined = CssInliner.inline(htmlBody);
            // render text version using a dedicated text engine
            ClassLoaderTemplateResolver textResolver = new ClassLoaderTemplateResolver();
            textResolver.setPrefix("mail-templates/");
            textResolver.setSuffix(".txt");
            textResolver.setTemplateMode("TEXT");
            textResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
            textResolver.setCacheable(false);
            TemplateEngine textEngine = new TemplateEngine();
            textEngine.setTemplateResolver(textResolver);
            String textBody = textEngine.process("verification", ctx);
            return new EmailTemplate(subject, inlined, true, textBody);
        } else {
            return new EmailTemplate(subject, body, false);
        }
    }
}

