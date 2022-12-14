package io.tech1.framework.emails.services.impl;

import io.tech1.framework.emails.domain.EmailHTML;
import io.tech1.framework.emails.services.EmailService;
import io.tech1.framework.emails.utilities.EmailUtility;
import io.tech1.framework.properties.ApplicationFrameworkProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmailServiceImpl implements EmailService {

    // Services
    private final JavaMailSender javaMailSender;
    // HTML Engine
    private final SpringTemplateEngine springTemplateEngine;
    // Utilities
    private final EmailUtility emailUtility;
    // Properties
    private final ApplicationFrameworkProperties applicationFrameworkProperties;

    @Override
    public void sendPlain(String subject, String message) {
        var emailConfigs = this.applicationFrameworkProperties.getEmailConfigs();
        this.sendPlain(emailConfigs.getTo(), subject, message);
    }

    @Override
    public void sendPlain(String[] to, String subject, String message) {
        var emailConfigs = this.applicationFrameworkProperties.getEmailConfigs();
        if (emailConfigs.isEnabled()) {
            var mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(emailConfigs.getFrom());
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            this.javaMailSender.send(mailMessage);
        }
    }

    @Override
    public void sendPlain(List<String> to, String subject, String message) {
        this.sendPlain(to.toArray(new String[0]), subject, message);
    }

    @Override
    public void sendPlain(Set<String> to, String subject, String message) {
        this.sendPlain(to.toArray(new String[0]), subject, message);
    }

    @Override
    public void sendHTML(EmailHTML emailHTML) {
        var emailConfigs = this.applicationFrameworkProperties.getEmailConfigs();
        if (emailConfigs.isEnabled()) {
            try {
                var tuple2 = this.emailUtility.getMimeMessageTuple2();
                var message = tuple2.getA();
                var mmHelper = tuple2.getB();
                mmHelper.setFrom(emailConfigs.getFrom());
                mmHelper.setTo(emailHTML.getTo().toArray(new String[0]));
                mmHelper.setSubject(emailHTML.getSubject());
                var context = new Context();
                context.setVariables(emailHTML.getTemplateVariables());
                var processedHTML = this.springTemplateEngine.process(emailHTML.getTemplateName(), context);
                mmHelper.setText(processedHTML, true);
                this.javaMailSender.send(message);
            } catch (MessagingException ex) {
                // ignored
            }
        }
    }
}
