import ui.MainFrame;
import javax.swing.*;

/**
 * Smart Template Assistant - 메인 진입점
 *
 * @author MinJun Kim
 * @version 1.2.0
 */
public class Main {
    public static void main(String[] args) {
        // 초기화 옵션 체크
        if (args.length > 0 && args[0].equals("--reset")) {
            resetApplication();
            System.out.println("애플리케이션이 초기화되었습니다.");
            return;
        }

        // 시스템 속성 설정
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Smart Template Assistant");

        // Look and Feel 설정
        try {
            // 시스템 기본 Look and Feel 사용
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // 다크모드 관련 설정
            UIManager.put("TextField.background", UIManager.getColor("TextField.background"));
            UIManager.put("TextArea.background", UIManager.getColor("TextArea.background"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // EDT에서 GUI 생성
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "프로그램 시작 중 오류가 발생했습니다.\n" + e.getMessage(),
                        "오류",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * 애플리케이션 초기화
     */
    private static void resetApplication() {
        String[] filesToDelete = {
                "templates.dat",
                "settings.properties",
                "email_credentials.properties"
        };

        for (String fileName : filesToDelete) {
            java.io.File file = new java.io.File(fileName);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println(fileName + " 삭제됨");
                } else {
                    System.out.println(fileName + " 삭제 실패");
                }
            }
        }
    }
}