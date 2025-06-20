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

    private boolean isDarkMode = false;
    private static final Color DARK_BG = new Color(30, 30, 30);
    private static final Color DARK_PANEL = new Color(45, 45, 45);
    private static final Color DARK_TEXT = new Color(220, 220, 220);
    private static final Color DARK_BORDER = new Color(60, 60, 60);

    private static final String CREDENTIALS_FILE = "email_credentials.properties";

    public EmailDialog(Frame parent, String prefilledContent) {
        super(parent, "이메일 발송", true);

        // 부모 프레임에서 다크모드 상태 가져오기
        if (parent instanceof MainFrame) {
            this.isDarkMode = ((MainFrame) parent).isDarkMode();
        }

        initUI(prefilledContent);
        loadCredentials();

        if (isDarkMode) {
            applyDarkMode();
        }
    }

    private void initUI(String prefilledContent) {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // 상단 패널 (계정 정보)
        JPanel accountPanel = createAccountPanel();
        mainPanel.add(accountPanel, BorderLayout.NORTH);

        // 중앙 패널 (이메일 정보)
        JPanel emailPanel = createEmailPanel(prefilledContent);
        mainPanel.add(emailPanel, BorderLayout.CENTER);

        // 하단 패널 (버튼)
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setSize(600, 550);
        setLocationRelativeTo(getParent());
    }

    private JPanel createAccountPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(isDarkMode ? DARK_BORDER : Color.GRAY),
                "Gmail 계정 정보",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("맑은 고딕", Font.PLAIN, 12),
                isDarkMode ? DARK_TEXT : Color.BLACK
        ));

        if (isDarkMode) {
            panel.setBackground(DARK_BG);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Gmail 주소
        JLabel gmailLabel = new JLabel("Gmail 주소:");
        gmailLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        if (isDarkMode) gmailLabel.setForeground(DARK_TEXT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(gmailLabel, gbc);

        gmailField = new JTextField();
        if (isDarkMode) {
            gmailField.setBackground(DARK_PANEL);
            gmailField.setForeground(DARK_TEXT);
            gmailField.setCaretColor(DARK_TEXT);
        }
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.gridwidth = 2;
        panel.add(gmailField, gbc);

        // 앱 비밀번호
        JLabel passwordLabel = new JLabel("앱 비밀번호:");
        passwordLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        if (isDarkMode) passwordLabel.setForeground(DARK_TEXT);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        if (isDarkMode) {
            passwordField.setBackground(DARK_PANEL);
            passwordField.setForeground(DARK_TEXT);
            passwordField.setCaretColor(DARK_TEXT);
        }
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        panel.add(passwordField, gbc);

        // 계정 정보 저장 체크박스
        saveCredentialsCheckBox = new JCheckBox("계정 정보 저장");
        saveCredentialsCheckBox.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        if (isDarkMode) {
            saveCredentialsCheckBox.setBackground(DARK_BG);
            saveCredentialsCheckBox.setForeground(DARK_TEXT);
        }
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(saveCredentialsCheckBox, gbc);

        // 안내 메시지
        JLabel infoLabel = new JLabel("<html><span style='font-size:10px;color:#888888;'>* Gmail 2단계 인증 후 앱 비밀번호를 생성하여 입력하세요</span></html>");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        panel.add(infoLabel, gbc);

        return panel;
    }

    private JPanel createEmailPanel(String prefilledContent) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(isDarkMode ? DARK_BORDER : Color.GRAY),
                "이메일 정보",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("맑은 고딕", Font.PLAIN, 12),
                isDarkMode ? DARK_TEXT : Color.BLACK
        ));

        if (isDarkMode) {
            panel.setBackground(DARK_BG);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 받는 사람
        JLabel toLabel = new JLabel("받는 사람:");
        toLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        if (isDarkMode) toLabel.setForeground(DARK_TEXT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(toLabel, gbc);

        toField = new JTextField();
        toField.setToolTipText("여러 명에게 보낼 때는 쉼표(,)로 구분하세요");
        if (isDarkMode) {
            toField.setBackground(DARK_PANEL);
            toField.setForeground(DARK_TEXT);
            toField.setCaretColor(DARK_TEXT);
        }
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        panel.add(toField, gbc);

        // 제목
        JLabel subjectLabel = new JLabel("제목:");
        subjectLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        if (isDarkMode) subjectLabel.setForeground(DARK_TEXT);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(subjectLabel, gbc);

        subjectField = new JTextField();
        if (isDarkMode) {
            subjectField.setBackground(DARK_PANEL);
            subjectField.setForeground(DARK_TEXT);
            subjectField.setCaretColor(DARK_TEXT);
        }
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        panel.add(subjectField, gbc);

        // 내용
        JLabel contentLabel = new JLabel("내용:");
        contentLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        if (isDarkMode) contentLabel.setForeground(DARK_TEXT);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        panel.add(contentLabel, gbc);

        contentArea = new JTextArea(prefilledContent);
        contentArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        if (isDarkMode) {
            contentArea.setBackground(DARK_PANEL);
            contentArea.setForeground(DARK_TEXT);
            contentArea.setCaretColor(DARK_TEXT);
        }

        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        if (isDarkMode) {
            scrollPane.setBorder(BorderFactory.createLineBorder(DARK_BORDER));
        }
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(isDarkMode ? DARK_BG : Color.WHITE);

        JButton sendButton, cancelButton;

        if (isDarkMode) {
            sendButton = createStyledButton("발송", new Color(46, 204, 113), Color.WHITE);
            cancelButton = createStyledButton("취소", new Color(60, 60, 60), DARK_TEXT);
        } else {
            sendButton = createStyledButton("발송", new Color(46, 204, 113), Color.WHITE);
            cancelButton = createStyledButton("취소", new Color(240, 240, 240), Color.BLACK);
        }

        panel.add(sendButton);
        panel.add(cancelButton);

        // 이벤트 리스너
        sendButton.addActionListener(e -> sendEmail());
        cancelButton.addActionListener(e -> dispose());

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(fgColor);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        button.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(100, 35));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void sendEmail() {
        String gmail = gmailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String to = toField.getText().trim();
        String subject = subjectField.getText().trim();
        String content = contentArea.getText();

        // 유효성 검사
        if (gmail.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Gmail 계정 정보를 입력해주세요.");
            return;
        }

        if (to.isEmpty()) {
            JOptionPane.showMessageDialog(this, "받는 사람을 입력해주세요.");
            return;
        }

        if (subject.isEmpty()) {
            JOptionPane.showMessageDialog(this, "제목을 입력해주세요.");
            return;
        }

        // 계정 정보 저장
        if (saveCredentialsCheckBox.isSelected()) {
            saveCredentials(gmail, password);
        }

        // 이메일 발송
        try {
            EmailSender sender = new EmailSender();
            sender.setCredentials(gmail, password);

            // 여러 명에게 발송
            String[] recipients = to.split(",");
            for (String recipient : recipients) {
                sender.sendEmail(recipient.trim(), subject, content);
            }

            JOptionPane.showMessageDialog(this,
                    "이메일이 성공적으로 발송되었습니다!",
                    "발송 완료",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "이메일 발송 실패: " + e.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveCredentials(String gmail, String password) {
        Properties props = new Properties();
        props.setProperty("gmail", gmail);
        props.setProperty("password", password);

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

    private void applyDarkMode() {
        getContentPane().setBackground(DARK_BG);
        applyDarkModeToComponent(getContentPane());
    }

    private void applyDarkModeToComponent(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(DARK_BG);
            } else if (comp instanceof JLabel && !((JLabel) comp).getText().contains("<html>")) {
                comp.setForeground(DARK_TEXT);
            } else if (comp instanceof JScrollPane) {
                ((JScrollPane) comp).getViewport().setBackground(DARK_BG);
            }

            if (comp instanceof Container) {
                applyDarkModeToComponent((Container) comp);
            }
        }
    }
}