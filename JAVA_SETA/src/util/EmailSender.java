package util;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/**
 * Gmail SMTP를 사용한 이메일 발송 클래스
 */
public class EmailSender {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    private String username;
    private String password;
    private Properties props;

    public EmailSender() {
        // SMTP 서버 설정
        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", SMTP_HOST);
    }

    /**
     * Gmail 계정 설정
     * @param username Gmail 주소
     * @param password 앱 비밀번호 (2단계 인증 필요)
     */
    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * 이메일 발송
     * @param to 받는 사람 이메일
     * @param subject 제목
     * @param content 내용
     * @return 성공 여부
     */
    public boolean sendEmail(String to, String subject, String content) {
        if (username == null || password == null) {
            throw new IllegalStateException("Gmail 계정 정보가 설정되지 않았습니다.");
        }

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // 디버그 모드 활성화
        session.setDebug(true);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            // HTML 형식 지원
            message.setContent(content, "text/html; charset=UTF-8");

            Transport.send(message);
            System.out.println("이메일 발송 성공!");
            return true;

        } catch (MessagingException e) {
            System.err.println("이메일 발송 실패: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 여러 명에게 이메일 발송
     * @param to 받는 사람들 (쉼표로 구분)
     * @param subject 제목
     * @param content 내용
     * @return 성공 여부
     */
    public boolean sendEmailToMultiple(String to, String subject, String content) {
        if (username == null || password == null) {
            throw new IllegalStateException("Gmail 계정 정보가 설정되지 않았습니다.");
        }

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));

            // 여러 수신자 처리
            String[] recipients = to.split(",");
            InternetAddress[] addresses = new InternetAddress[recipients.length];
            for (int i = 0; i < recipients.length; i++) {
                addresses[i] = new InternetAddress(recipients[i].trim());
            }
            message.setRecipients(Message.RecipientType.TO, addresses);

            message.setSubject(subject);
            message.setContent(content, "text/html; charset=UTF-8");

            Transport.send(message);
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 이메일 주소 유효성 검사
     * @param email 검사할 이메일
     * @return 유효 여부
     */
    public static boolean isValidEmail(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
            return true;
        } catch (AddressException e) {
            return false;
        }
    }
}