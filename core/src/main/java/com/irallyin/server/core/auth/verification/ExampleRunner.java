package com.irallyin.server.core.auth.verification;

import com.irallyin.server.core.mail.template.EmailTemplateService;

/**
 * Small example runner demonstrating generation, sending and validation using in-memory store and template service.
 */
public class ExampleRunner {
	public static void main(String[] args) throws Exception {
		InMemoryVerificationCodeStore store = new InMemoryVerificationCodeStore();
		EmailSender sender = null;
		try {
			sender = new EmailSender();
		} catch (Exception e) {
			// if email config not present, we still demonstrate generation
			System.out.println("Email sender not configured: " + e.getMessage());
		}

		EmailTemplateService templateService = new EmailTemplateService();

		VerificationService svc = new VerificationService(store, sender, templateService);

		String id = "user123";
		// key format: ID + "register" (for registration scenario)
		String keyRegister = id + "register";

		String code = svc.generateAndStore(keyRegister);
		System.out.println("Generated code for " + keyRegister + " => " + code);

		if (sender != null) {
			// attempt to send templated verification email
			svc.generateAndSendVerificationEmail(keyRegister, "test@example.com");
			System.out.println("Tried to send verification email to test@example.com");
		}

		boolean ok = svc.validate(keyRegister, code);
		System.out.println("Validation result: " + ok);
	}
}


