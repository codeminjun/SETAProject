package ui;

import util.EmailSender;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * 이메일 발송 다이얼로그
 */
public class EmailDialog extends JDialog {
    private JTextField toField;
    private JTextField subjectField;
    private JTextArea contentArea;
    private JTextField gmailField;
    private JPasswordField passwordField;
    private JCheckBox saveCredentialsCheckBox;

    private static final String CREDENTIALS_FILE = "email_credentials.properties";

    public EmailDialog(Frame parent, String prefilledContent) {
        super(parent, "이메일 발송", true);
        initUI(prefilledContent);
        loadCredentials();
    }

    private void initUI(String prefilledContent) {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        mainPanel.add(createAccountPanel(), BorderLayout.NORTH);
        mainPanel.add(createEmailPanel(prefilledContent), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
        setSize(600, 550);
        setLocationRelativeTo(getParent());
    }

    private JPanel createAccountPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Gmail 계정 정보"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Gmail 주소
        JLabel gmailLabel = new JLabel("Gmail 주소:");
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(gmailLabel, gbc);
        gmailField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1; gbc.gridwidth = 2;
        panel.add(gmailField, gbc);

        // 앱 비밀번호
        JLabel passwordLabel = new JLabel("앱 비밀번호:");
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.gridwidth = 1;
        panel.add(passwordLabel, gbc);
        passwordField = new JPasswordField();
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1;
        panel.add(passwordField, gbc);

        // 계정 정보 저장 체크박스
        saveCredentialsCheckBox = new JCheckBox("계정 정보 저장");
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(saveCredentialsCheckBox, gbc);

        // 안내 메시지
        JLabel infoLabel = new JLabel("<html><span style='font-size:10px;color:#888888;'>* Gmail 2단계 인증 후 앱 비밀번호를 생성하여 입력하세요</span></html>");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        panel.add(infoLabel, gbc);

        return panel;
    }

    private JPanel createEmailPanel(String prefilledContent) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("이메일 정보"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 받는 사람
        JLabel toLabel = new JLabel("받는 사람:");
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(toLabel, gbc);
        toField = new JTextField();
        toField.setToolTipText("여러 명에게 보낼 때는 쉼표(,)로 구분하세요");
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1;
        panel.add(toField, gbc);

        // 제목
        JLabel subjectLabel = new JLabel("제목:");
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(subjectLabel, gbc);
        subjectField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(subjectField, gbc);

        // 내용
        JLabel contentLabel = new JLabel("내용:");
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.NORTH;
        panel.add(contentLabel, gbc);
        contentArea = new JTextArea(prefilledContent);
        contentArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        gbc.gridx = 1; gbc.gridy = 2; gbc.weighty = 1; gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton sendButton = createStyledButton("발송", new Color(46, 204, 113), Color.WHITE);
        JButton cancelButton = createStyledButton("취소", new Color(220, 220, 220), Color.BLACK);
        panel.add(sendButton);
        panel.add(cancelButton);
        sendButton.addActionListener(e -> sendEmail());
        cancelButton.addActionListener(e -> dispose());
        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(100, 35));
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void sendEmail() {
        String gmail = gmailField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (gmail.isEmpty() || password.isEmpty() || toField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 필드를 입력해주세요.");
            return;
        }
        if (saveCredentialsCheckBox.isSelected()) {
            saveCredentials(gmail, password);
        }
        try {
            EmailSender sender = new EmailSender();
            sender.setCredentials(gmail, password);
            sender.sendEmailToMultiple(toField.getText(), subjectField.getText(), contentArea.getText());
            JOptionPane.showMessageDialog(this, "이메일이 성공적으로 발송되었습니다!", "발송 완료", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "이메일 발송 실패: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveCredentials(String gmail, String password) {
        Properties props = new Properties();
        props.setProperty("gmail", gmail);
        props.setProperty("password", password); // 주의: 암호화되지 않은 저장
        try (FileOutputStream out = new FileOutputStream(CREDENTIALS_FILE)) {
            props.store(out, "Email Credentials");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCredentials() {
        File file = new File(CREDENTIALS_FILE);
        if (file.exists()) {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream(file)) {
                props.load(in);
                gmailField.setText(props.getProperty("gmail", ""));
                passwordField.setText(props.getProperty("password", ""));
                saveCredentialsCheckBox.setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}