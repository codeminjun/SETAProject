package ui;

import model.EmailTemplate;
import model.TemplateManager;
import javax.swing.border.TitledBorder;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 메인 프레임 - Smart Template Assistant의 주 화면
 */
// [수정] TemplateManager.TemplateChangeListener 인터페이스 구현
public class MainFrame extends JFrame implements TemplateManager.TemplateChangeListener {
    private TemplateManager templateManager;
    private JTable templateTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private JCheckBox favoriteCheckBox;

    // 다크모드 관련 변수
    private boolean isDarkMode = false;
    private JButton darkModeButton;
    private static final Color DARK_BG = new Color(30, 30, 30);
    private static final Color DARK_PANEL = new Color(45, 45, 45);
    private static final Color DARK_TEXT = new Color(220, 220, 220);
    private static final Color DARK_BORDER = new Color(60, 60, 60);
    private static final Color DARK_SELECTION = new Color(52, 73, 94);

    // 설정 파일
    private static final String SETTINGS_FILE = "settings.properties";

    public MainFrame() {
        templateManager = new TemplateManager();
        templateManager.addChangeListener(this); // [추가] 템플릿 매니저에 리스너 등록

        initUI();

        // 초기 로드 시 테이블 정리
        SwingUtilities.invokeLater(() -> {
            loadTemplates();
            loadSettings();
        });
    }

    // [추가] TemplateChangeListener 인터페이스 구현 메소드
    @Override
    public void onTemplatesChanged() {
        // 데이터 모델이 변경될 때마다 UI를 새로고침합니다.
        SwingUtilities.invokeLater(this::loadTemplates);
    }


    public boolean isDarkMode() {
        return isDarkMode;
    }

    private void initUI() {
        setTitle("Smart Template Assistant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 상단 패널
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // 중앙 패널 (템플릿 테이블)
        JScrollPane centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // 하단 패널
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 검색 패널
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBorder(BorderFactory.createTitledBorder("검색 및 필터"));

        // 다크모드 버튼을 포함한 상단 바
        JPanel headerBar = new JPanel(new BorderLayout());

        searchField = new JTextField();
        searchField.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        headerBar.add(searchField, BorderLayout.CENTER);

        // 다크모드 토글 버튼
        darkModeButton = new JButton("🌙");
        darkModeButton.setFont(new Font("Dialog", Font.PLAIN, 20));
        darkModeButton.setPreferredSize(new Dimension(50, 30));
        darkModeButton.setFocusPainted(false);
        darkModeButton.setBorderPainted(false);
        darkModeButton.setContentAreaFilled(false);
        darkModeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        darkModeButton.addActionListener(e -> toggleDarkMode());

        headerBar.add(darkModeButton, BorderLayout.EAST);

        JButton searchButton = new JButton("검색");
        searchButton.addActionListener(e -> filterTemplates());

        searchPanel.add(headerBar, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // 필터 패널
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel categoryLabel = new JLabel("카테고리:");
        categoryCombo = new JComboBox<>();
        categoryCombo.addItem("전체");
        categoryCombo.addActionListener(e -> filterTemplates());

        favoriteCheckBox = new JCheckBox("즐겨찾기만 보기");
        favoriteCheckBox.addActionListener(e -> filterTemplates());

        filterPanel.add(categoryLabel);
        filterPanel.add(categoryCombo);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(favoriteCheckBox);

        searchPanel.add(filterPanel, BorderLayout.SOUTH);

        panel.add(searchPanel, BorderLayout.CENTER);

        // 검색 필드에 실시간 검색 리스너 추가
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterTemplates(); }
            public void removeUpdate(DocumentEvent e) { filterTemplates(); }
            public void insertUpdate(DocumentEvent e) { filterTemplates(); }
        });

        return panel;
    }

    private JScrollPane createCenterPanel() {
        String[] columnNames = {"제목", "카테고리", "즐겨찾기", ""};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // 즐겨찾기 버튼 컬럼만 편집 가능
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return Boolean.class;
                return Object.class;
            }
        };

        templateTable = new JTable(tableModel);
        templateTable.setRowHeight(40);
        templateTable.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        templateTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 컬럼 너비 설정
        TableColumnModel columnModel = templateTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(400);  // 제목
        columnModel.getColumn(1).setPreferredWidth(150);  // 카테고리
        columnModel.getColumn(2).setPreferredWidth(80);   // 즐겨찾기 상태
        columnModel.getColumn(3).setPreferredWidth(100);  // 즐겨찾기 버튼

        // 즐겨찾기 버튼 렌더러 및 에디터
        columnModel.getColumn(3).setCellRenderer(new StarButtonRenderer());
        columnModel.getColumn(3).setCellEditor(new StarButtonEditor());

        // 더블클릭 이벤트
        templateTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = templateTable.getSelectedRow();
                    if (row >= 0) {
                        String title = (String) tableModel.getValueAt(row, 0);
                        EmailTemplate template = findTemplateByTitle(title);
                        if (template != null) {
                            showPreviewDialog(template);
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(templateTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("템플릿 목록"));

        return scrollPane;
    }

    // 즐겨찾기 버튼 렌더러
    private class StarButtonRenderer extends JButton implements TableCellRenderer {
        public StarButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Dialog", Font.PLAIN, 20));
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            Boolean isFavorite = (Boolean) table.getModel().getValueAt(row, 2);
            setText(isFavorite ? "★" : "☆");
            setForeground(isFavorite ? new Color(255, 193, 7) : Color.GRAY);

            if (isDarkMode) {
                setBackground(isSelected ? DARK_SELECTION : DARK_BG);
            } else {
                setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            }

            return this;
        }
    }

    // 즐겨찾기 버튼 에디터
    private class StarButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean isFavorite;
        private int currentRow;

        public StarButtonEditor() {
            super(new JCheckBox());
            button = new JButton();
            button.setOpaque(true);
            button.setFont(new Font("Dialog", Font.PLAIN, 20));
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            button.addActionListener(e -> {
                fireEditingStopped();
                SwingUtilities.invokeLater(() -> toggleFavorite(currentRow));
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            isFavorite = (Boolean) table.getModel().getValueAt(row, 2);
            button.setText(isFavorite ? "★" : "☆");
            button.setForeground(isFavorite ? new Color(255, 193, 7) : Color.GRAY);

            if (isDarkMode) {
                button.setBackground(DARK_BG);
            } else {
                button.setBackground(table.getBackground());
            }

            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return isFavorite;
        }
    }

    private void toggleFavorite(int row) {
        String title = (String) tableModel.getValueAt(row, 0);
        EmailTemplate template = findTemplateByTitle(title);
        if (template != null) {
            boolean newFavoriteStatus = !template.isFavorite();
            template.setFavorite(newFavoriteStatus);
            templateManager.updateTemplate(template);
            // onTemplatesChanged가 호출되어 테이블이 자동으로 새로고침되므로 수동 업데이트 불필요
        }
    }

    private EmailTemplate findTemplateByTitle(String title) {
        return templateManager.getAllTemplates().stream()
                .filter(t -> t.getTitle().equals(title))
                .findFirst()
                .orElse(null);
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton newButton = createStyledButton("새 템플릿", new Color(46, 204, 113));
        JButton editButton = createStyledButton("수정", new Color(52, 152, 219));
        JButton deleteButton = createStyledButton("삭제", new Color(231, 76, 60));

        panel.add(newButton);
        panel.add(editButton);
        panel.add(deleteButton);

        // 이벤트 리스너
        newButton.addActionListener(e -> showTemplateDialog(null));
        editButton.addActionListener(e -> editSelectedTemplate());
        deleteButton.addActionListener(e -> deleteSelectedTemplate());

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(color.brighter());
                } else {
                    g2.setColor(color);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        button.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(100, 40));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void loadTemplates() {
        updateCategoryCombo();
        filterTemplates(); // [수정] 필터링 메소드를 직접 호출하여 UI를 일관되게 업데이트
    }

    private void updateCategoryCombo() {
        String selected = (String) categoryCombo.getSelectedItem();

        // [수정] 리스너를 잠시 제거하여 불필요한 이벤트 방지
        ActionListener listener = categoryCombo.getActionListeners().length > 0 ? categoryCombo.getActionListeners()[0] : null;
        if (listener != null) {
            categoryCombo.removeActionListener(listener);
        }

        categoryCombo.removeAllItems();
        categoryCombo.addItem("전체");

        for (String category : templateManager.getAllCategories()) {
            categoryCombo.addItem(category);
        }

        if (selected != null) {
            categoryCombo.setSelectedItem(selected);
        }

        // [수정] 리스너를 다시 추가
        if (listener != null) {
            categoryCombo.addActionListener(listener);
        }
    }

    private void filterTemplates() {
        String searchText = searchField.getText().toLowerCase();
        String selectedCategory = (String) categoryCombo.getSelectedItem();
        boolean onlyFavorites = favoriteCheckBox.isSelected();

        List<EmailTemplate> filtered = templateManager.getAllTemplates().stream()
                .filter(t -> {
                    boolean matchesSearch = searchText.isEmpty() ||
                            t.getTitle().toLowerCase().contains(searchText) ||
                            t.getContent().toLowerCase().contains(searchText);

                    boolean matchesCategory = selectedCategory == null || "전체".equals(selectedCategory) ||
                            t.getCategory().equals(selectedCategory);

                    boolean matchesFavorite = !onlyFavorites || t.isFavorite();

                    return matchesSearch && matchesCategory && matchesFavorite;
                })
                .collect(Collectors.toList());

        tableModel.setRowCount(0);

        for (EmailTemplate template : filtered) {
            tableModel.addRow(new Object[]{
                    template.getTitle(),
                    template.getCategory(),
                    template.isFavorite(),
                    template.isFavorite() ? "★" : "☆"
            });
        }
    }

    private void showTemplateDialog(EmailTemplate template) {
        if (isDialogOpen) return;
        isDialogOpen = true;

        TemplateDialog dialog = new TemplateDialog(this, template, templateManager);

        // [수정] WindowListener에서 loadTemplates() 호출 제거
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                isDialogOpen = false;
                // onTemplatesChanged 리스너가 새로고침을 처리하므로 여기서는 아무것도 하지 않음
            }
        });

        dialog.setVisible(true);
    }

    // 다이얼로그 열림 상태 추적
    private boolean isDialogOpen = false;

    private void showPreviewDialog(EmailTemplate template) {
        PreviewDialog dialog = new PreviewDialog(this, template);
        dialog.setVisible(true);
    }

    private void editSelectedTemplate() {
        int selectedRow = templateTable.getSelectedRow();
        if (selectedRow >= 0) {
            String title = (String) tableModel.getValueAt(selectedRow, 0);
            EmailTemplate template = findTemplateByTitle(title);
            if (template != null) {
                showTemplateDialog(template);
            }
        } else {
            JOptionPane.showMessageDialog(this, "수정할 템플릿을 선택해주세요.");
        }
    }

    private void deleteSelectedTemplate() {
        int selectedRow = templateTable.getSelectedRow();
        if (selectedRow >= 0) {
            String title = (String) tableModel.getValueAt(selectedRow, 0);
            EmailTemplate template = findTemplateByTitle(title);

            if (template != null) {
                int result = JOptionPane.showConfirmDialog(
                        this,
                        "정말로 이 템플릿을 삭제하시겠습니까?",
                        "삭제 확인",
                        JOptionPane.YES_NO_OPTION
                );

                if (result == JOptionPane.YES_OPTION) {
                    templateManager.deleteTemplate(template.getId());
                    // onTemplatesChanged가 호출되어 테이블이 자동으로 새로고침됨
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "삭제할 템플릿을 선택해주세요.");
        }
    }

    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        darkModeButton.setText(isDarkMode ? "☀️" : "🌙");
        applyTheme();
        saveSettings();
    }

    private void applyTheme() {
        if (isDarkMode) {
            // 다크모드 적용
            getContentPane().setBackground(DARK_BG);
            applyDarkModeToComponent(getContentPane());

            // 테이블 다크모드
            templateTable.setBackground(DARK_BG);
            templateTable.setForeground(DARK_TEXT);
            templateTable.setSelectionBackground(DARK_SELECTION);
            templateTable.setSelectionForeground(DARK_TEXT);
            templateTable.setGridColor(DARK_BORDER);
            templateTable.getTableHeader().setBackground(DARK_PANEL);
            templateTable.getTableHeader().setForeground(DARK_TEXT);

            // 스크롤 페인 다크모드
            JScrollPane scrollPane = (JScrollPane) templateTable.getParent().getParent();
            scrollPane.getViewport().setBackground(DARK_BG);
            scrollPane.setBackground(DARK_BG);

        } else {
            // 라이트모드 적용
            getContentPane().setBackground(Color.WHITE);
            applyLightModeToComponent(getContentPane());

            // 테이블 라이트모드
            templateTable.setBackground(Color.WHITE);
            templateTable.setForeground(Color.BLACK);
            templateTable.setSelectionBackground(new Color(184, 207, 229));
            templateTable.setSelectionForeground(Color.BLACK);
            templateTable.setGridColor(Color.LIGHT_GRAY);
            templateTable.getTableHeader().setBackground(new Color(240, 240, 240));
            templateTable.getTableHeader().setForeground(Color.BLACK);

            // 스크롤 페인 라이트모드
            JScrollPane scrollPane = (JScrollPane) templateTable.getParent().getParent();
            scrollPane.getViewport().setBackground(Color.WHITE);
            scrollPane.setBackground(Color.WHITE);
        }

        // 테이블 새로고침
        templateTable.repaint();
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void applyDarkModeToComponent(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(DARK_BG);
                if (((JPanel) comp).getBorder() instanceof TitledBorder) {
                    ((TitledBorder) ((JPanel) comp).getBorder()).setTitleColor(DARK_TEXT);
                }
            } else if (comp instanceof JLabel) {
                comp.setForeground(DARK_TEXT);
            } else if (comp instanceof JTextField) {
                comp.setBackground(DARK_PANEL);
                comp.setForeground(DARK_TEXT);
                ((JTextField) comp).setCaretColor(DARK_TEXT);
            } else if (comp instanceof JComboBox) {
                comp.setBackground(DARK_PANEL);
                comp.setForeground(DARK_TEXT);
            } else if (comp instanceof JCheckBox) {
                comp.setBackground(DARK_BG);
                comp.setForeground(DARK_TEXT);
            } else if (comp instanceof JButton && !comp.equals(darkModeButton)) {
                if (comp.getParent() instanceof JPanel &&
                        ((JButton) comp).getText() != null &&
                        !((JButton) comp).getText().matches("[★☆]")) {
                    comp.setBackground(DARK_PANEL);
                    comp.setForeground(DARK_TEXT);
                }
            }

            if (comp instanceof Container) {
                applyDarkModeToComponent((Container) comp);
            }
        }
    }

    private void applyLightModeToComponent(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(Color.WHITE);
                if (((JPanel) comp).getBorder() instanceof TitledBorder) {
                    ((TitledBorder) ((JPanel) comp).getBorder()).setTitleColor(Color.BLACK);
                }
            } else if (comp instanceof JLabel) {
                comp.setForeground(Color.BLACK);
            } else if (comp instanceof JTextField) {
                comp.setBackground(Color.WHITE);
                comp.setForeground(Color.BLACK);
                ((JTextField) comp).setCaretColor(Color.BLACK);
            } else if (comp instanceof JComboBox) {
                comp.setBackground(Color.WHITE);
                comp.setForeground(Color.BLACK);
            } else if (comp instanceof JCheckBox) {
                comp.setBackground(Color.WHITE);
                comp.setForeground(Color.BLACK);
            } else if (comp instanceof JButton && !comp.equals(darkModeButton)) {
                if (comp.getParent() instanceof JPanel &&
                        ((JButton) comp).getText() != null &&
                        !((JButton) comp).getText().matches("[★☆]")) {
                    comp.setBackground(UIManager.getColor("Button.background"));
                    comp.setForeground(Color.BLACK);
                }
            }

            if (comp instanceof Container) {
                applyLightModeToComponent((Container) comp);
            }
        }
    }

    private void saveSettings() {
        Properties props = new Properties();
        props.setProperty("darkMode", String.valueOf(isDarkMode));

        try (FileOutputStream out = new FileOutputStream(SETTINGS_FILE)) {
            props.store(out, "Smart Template Assistant Settings");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSettings() {
        Properties props = new Properties();
        File settingsFile = new File(SETTINGS_FILE);

        if (settingsFile.exists()) {
            try (FileInputStream in = new FileInputStream(settingsFile)) {
                props.load(in);
                this.isDarkMode = Boolean.parseBoolean(props.getProperty("darkMode", "false"));

                if (this.isDarkMode) {
                    darkModeButton.setText("☀️");
                    applyTheme();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}