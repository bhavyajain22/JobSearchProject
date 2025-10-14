package com.jobflow.alerts.service;

import com.jobflow.jobs.dto.JobDto;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;

import java.util.List;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendJobAlert(String to, List<JobDto> jobs, String jobTitle) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("🔥 New " + jobTitle + " jobs for you!");

            String html = buildHtmlTemplate(jobs, jobTitle);
            helper.setText(html, true);
            mailSender.send(message);

            System.out.printf("[EmailService] sent job alert to %s (%d jobs)%n", to, jobs.size());
        } catch (MessagingException e) {
            System.out.printf("[EmailService] failed to send to %s: %s%n", to, e.getMessage());
        }
    }

    private String buildHtmlTemplate(List<JobDto> jobs, String jobTitle) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family:Arial, sans-serif; color:#333;'>");
        sb.append("<h2>👋 Hi there!</h2>");
        sb.append("<p>Here are the latest <b>").append(jobTitle).append("</b> jobs we found for you:</p>");
        sb.append("<ul style='line-height:1.8;'>");
        for (JobDto j : jobs) {
            sb.append("<li>")
                    .append("<b>").append(j.getTitle()).append("</b>")
                    .append(" — ").append(j.getCompany())
                    .append(" (").append(j.getLocation()).append(")")
                    .append(" [<a href='").append(j.getApplyUrl()).append("'>Apply</a>]")
                    .append("</li>");
        }
        sb.append("</ul>");
        sb.append("<p style='margin-top:20px;'>💼 Visit our app for more jobs anytime.</p>");
        sb.append("<hr><p style='font-size:12px;color:#888;'>You’re receiving this because you created a job alert.</p>");
        sb.append("</div>");
        return sb.toString();
    }

//    public void sendEmail(String to, List<JobDto> jobs) {
//        try {
//            MimeMessage msg = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
//            helper.setTo(to);
//            helper.setSubject("New job matches from JobFlow");
//            StringBuilder html = new StringBuilder("<h3>New job matches</h3><ul>");
//            for (JobDto j : jobs) {
//                html.append("<li>")
//                        .append("<a href=\"").append(j.getApplyUrl()).append("\">").append(j.getTitle()).append("</a>")
//                        .append(" — ").append(j.getCompany())
//                        .append(" (").append(j.getLocation()).append(")")
//                        .append("</li>");
//            }
//            html.append("</ul>");
//            helper.setText(html.toString(), true);
//            mailSender.send(msg);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}
