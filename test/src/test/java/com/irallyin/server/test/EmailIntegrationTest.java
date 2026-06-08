package com.irallyin.server.test;

import com.irallyin.server.core.auth.verification.EmailSender;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Integration test that attempts to send an email using configuration in
 * classpath:/verification.properties. If SMTP configuration is missing, the test will be skipped.
 */
public class EmailIntegrationTest {

    @Test
    public void testSendEmailIfConfigured() throws IOException {
        Properties p = new Properties();
        // Prefer irallyin-variables.properties but keep backward compatibility with verification.properties
        InputStream in = getClass().getClassLoader().getResourceAsStream("irallyin-variables.properties");
        if (in == null) {
            in = getClass().getClassLoader().getResourceAsStream("verification.properties");
        }
        if (in == null) {
            // try project root files
            String wd = System.getProperty("user.dir");
            java.io.File f1 = new java.io.File(wd, "irallyin-variables.properties");
            java.io.File f2 = new java.io.File(wd, "verification.properties");
            try {
                if (f1.exists()) in = new java.io.FileInputStream(f1);
                else if (f2.exists()) in = new java.io.FileInputStream(f2);
            } catch (java.io.FileNotFoundException e) {
                // ignore, will skip below
            }
        }
        if (in == null) {
            // skip if no config found
            Assumptions.assumeTrue(false, "irallyin-variables.properties (or verification.properties) not found on classpath or project root, skipping email integration test");
            return;
        }
        p.load(in);

        String host = p.getProperty("smtp.host");
        String user = p.getProperty("smtp.username");
        String pass = p.getProperty("smtp.password");
        String to = p.getProperty("smtp.test.to");
        if (to == null || to.trim().isEmpty()) to = user;

        // only run if minimal configuration available
        Assumptions.assumeTrue(host != null && host.trim().length() > 0, "SMTP host not configured");
        Assumptions.assumeTrue(user != null && user.trim().length() > 0, "SMTP username not configured");
        Assumptions.assumeTrue(pass != null && pass.trim().length() > 0, "SMTP password not configured");

        EmailSender sender = new EmailSender();

        // attempt to send; assert no exception thrown
        final String finalTo = (to != null ? to : user);
        String id = java.time.Instant.now().toString();
        String subj = "iRallyIn Delivery Check - " + id;
        String body = "Delivery check from iRallyIn. timestamp=" + id + "\nTo=" + finalTo + "\n--";
        System.out.println("[EmailIntegrationTest] sending delivery check to " + finalTo + " subject=" + subj);
        assertDoesNotThrow(() -> sender.send(finalTo, subj, body));
    }
}

