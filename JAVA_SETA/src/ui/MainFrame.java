package ui;

import model.EmailTemplate;
import model.TemplateManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

public class MainFrame extends JFrame implements TemplateManager.TemplateChangeListener {
    private TemplateManager templateManager;
    private JTable templateTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private JCheckBox favoriteCheckBox;
    private boolean isDialogOpen = false;

    public MainFrame() {
        templateManager = new TemplateManager();
        templateManager.addChangeListener(this); // 데이터 변경 감지 리스너 등록

        initUI();
        loadTemplates();
    }

    @Override
    public void onTemplatesChanged() {
        // 데이터(템플릿)가 변경될 때마다 이 메소드가 호출됨
        SwingUtilities.invokeLater(this::filterTemplates);
    }

    private void initUI() {
        setTitle("Smart Template Assistant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // UI 컴포넌트 생성 및 배치
        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new BorderLayout(10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("검색 및 필터"));

        searchField = new JTextField();
        searchField.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        searchPanel.add(searchField, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JLabel categoryLabel = new JLabel("카테고리:");
        categoryCombo = new JComboBox<>();
        categoryCombo.addItem("전체");
        categoryCombo.addActionListener(e -> filterTemplates());

        favoriteCheckBox = new JCheckBox("즐겨찾기만 보기");
        favoriteCheckBox.addActionListener(e -> filterTemplates());

        filterPanel.add(categoryLabel);
        filterPanel.add(categoryCombo);
        filterPanel.add(favoriteCheckBox);

        searchPanel.add(filterPanel, BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterTemplates(); }
            public void removeUpdate(DocumentEvent e) { filterTemplates(); }
            public void insertUpdate(DocumentEvent e) { filterTemplates(); }
        });

        return topPanel;
    }

    private JScrollPane createCenterPanel() {
        String[] columnNames = {"제목", "카테고리", "즐겨찾기", ""};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // 즐겨찾기 버튼 컬럼만 수정 가능
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
        templateTable.setShowGrid(true);

        // 컬럼 너비 설정
        TableColumnModel columnModel = templateTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(450);
        columnModel.getColumn(1).setPreferredWidth(150);
        columnModel.getColumn(2).setMinWidth(80);
        columnModel.getColumn(2).setMaxWidth(80);
        columnModel.getColumn(3).setMinWidth(50);
        columnModel.getColumn(3).setMaxWidth(50);

        // 즐겨찾기 버튼 렌더러 및 에디터 설정
        columnModel.getColumn(3).setCellRenderer(new StarButtonRenderer());
        columnModel.getColumn(3).setCellEditor(new StarButtonEditor());

        // 테이블 행 더블클릭 시 미리보기 창 열기
        templateTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = templateTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        int modelRow = templateTable.convertRowIndexToModel(selectedRow);
                        String title = (String) tableModel.getValueAt(modelRow, 0);
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

    // 즐겨찾기 버튼(★)의 모양을 담당
    private static class StarButtonRenderer extends JButton implements TableCellRenderer {
        public StarButtonRenderer() {
            super();
            setOpaque(true);
            setFont(new Font("Dialog", Font.PLAIN, 20));
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            boolean isFavorite = (Boolean) table.getModel().getValueAt(row, 2);
            setText(isFavorite ? "★" : "☆");
            setForeground(isFavorite ? new Color(255, 193, 7) : Color.GRAY);
            return this;
        }
    }

    // 즐겨찾기 버튼(★)의 클릭 이벤트를 처리
    private class StarButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int currentRow;

        public StarButtonEditor() {
            super(new JCheckBox()); // 생성자 인자 필요
            button = new JButton();
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setFont(new Font("Dialog", Font.PLAIN, 20));

            button.addActionListener(e -> {
                // 버튼이 클릭되면, 현재 행의 템플릿을 찾아 즐겨찾기 상태를 변경하고 저장
                String title = (String) tableModel.getValueAt(currentRow, 0);
                EmailTemplate template = findTemplateByTitle(title);
                if (template != null) {
                    template.setFavorite(!template.isFavorite());
                    templateManager.updateTemplate(template); // 이 메소드가 onTemplatesChanged()를 호출
                }
                fireEditingStopped(); // 테이블 에디팅 모드 종료
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.currentRow = row;
            boolean isFavorite = (Boolean) table.getModel().getValueAt(row, 2);
            button.setText(isFavorite ? "★" : "☆");
            button.setForeground(isFavorite ? new Color(255, 193, 7) : Color.GRAY);
            return button;
        }
    }

    private EmailTemplate findTemplateByTitle(String title) {
        return templateManager.getAllTemplates().stream()
                .filter(t -> title.equals(t.getTitle()))
                .findFirst()
                .orElse(null);
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton newButton = createStyledButton("새 템플릿", new Color(46, 204, 113));
        JButton editButton = createStyledButton("수정", new Color(52, 152, 219));
        JButton deleteButton = createStyledButton("삭제", new Color(231, 76, 60));

        bottomPanel.add(newButton);
        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);

        newButton.addActionListener(e -> showTemplateDialog(null));
        editButton.addActionListener(e -> editSelectedTemplate());
        deleteButton.addActionListener(e -> deleteSelectedTemplate());

        return bottomPanel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(100, 40));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadTemplates() {
        updateCategoryCombo();
        filterTemplates();
    }

    private void updateCategoryCombo() {
        String selectedItem = (String) categoryCombo.getSelectedItem();

        ActionListener listener = categoryCombo.getActionListeners().length > 0 ? categoryCombo.getActionListeners()[0] : null;
        if (listener != null) categoryCombo.removeActionListener(listener);

        categoryCombo.removeAllItems();
        categoryCombo.addItem("전체");
        templateManager.getAllCategories().forEach(categoryCombo::addItem);

        categoryCombo.setSelectedItem(selectedItem);
        if (listener != null) categoryCombo.addActionListener(listener);
    }

    private void filterTemplates() {
        String searchText = searchField.getText().toLowerCase();
        String selectedCategory = (String) categoryCombo.getSelectedItem();
        boolean onlyFavorites = favoriteCheckBox.isSelected();

        List<EmailTemplate> filteredList = templateManager.getAllTemplates().stream()
                .filter(t -> (searchText.isEmpty() || t.getTitle().toLowerCase().contains(searchText) || t.getContent().toLowerCase().contains(searchText)))
                .filter(t -> (selectedCategory == null || "전체".equals(selectedCategory) || t.getCategory().equals(selectedCategory)))
                .filter(t -> (!onlyFavorites || t.isFavorite()))
                .collect(Collectors.toList());

        tableModel.setRowCount(0); // 테이블 초기화
        for (EmailTemplate template : filteredList) {
            tableModel.addRow(new Object[]{template.getTitle(), template.getCategory(), template.isFavorite(), null});
        }
    }

    private void showTemplateDialog(EmailTemplate template) {
        if (isDialogOpen) return;
        isDialogOpen = true;
        TemplateDialog dialog = new TemplateDialog(this, template, templateManager);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                isDialogOpen = false;
            }
        });
        dialog.setVisible(true);
    }

    private void showPreviewDialog(EmailTemplate template) {
        PreviewDialog dialog = new PreviewDialog(this, template);
        dialog.setVisible(true);
    }

    private void editSelectedTemplate() {
        int selectedRow = templateTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = templateTable.convertRowIndexToModel(selectedRow);
            String title = (String) tableModel.getValueAt(modelRow, 0);
            EmailTemplate template = findTemplateByTitle(title);
            if (template != null) showTemplateDialog(template);
        } else {
            JOptionPane.showMessageDialog(this, "수정할 템플릿을 선택해주세요.");
        }
    }

    private void deleteSelectedTemplate() {
        int selectedRow = templateTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = templateTable.convertRowIndexToModel(selectedRow);
            String title = (String) tableModel.getValueAt(modelRow, 0);
            EmailTemplate template = findTemplateByTitle(title);
            if (template != null) {
                int result = JOptionPane.showConfirmDialog(this, "정말로 이 템플릿을 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    templateManager.deleteTemplate(template.getId());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "삭제할 템플릿을 선택해주세요.");
        }
    }
}