package model;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 템플릿 관리 클래스
 * 템플릿의 CRUD 및 파일 저장/로드 기능 제공
 */
public class TemplateManager {
    private static final String TEMPLATES_FILE = "templates.dat";
    private List<EmailTemplate> templates;
    private Long nextId;
    private Set<String> categories;

    // 리스너 패턴을 위한 인터페이스
    public interface TemplateChangeListener {
        void onTemplatesChanged();
    }

    private List<TemplateChangeListener> listeners = new ArrayList<>();

    public TemplateManager() {
        templates = new ArrayList<>();
        categories = new HashSet<>();
        nextId = 1L;
        loadTemplates();
    }

    /**
     * 템플릿 변경 리스너 추가
     */
    public void addChangeListener(TemplateChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * 리스너들에게 변경 알림
     */
    private void notifyListeners() {
        for (TemplateChangeListener listener : listeners) {
            listener.onTemplatesChanged();
        }
    }

    /**
     * 새 템플릿 추가
     */
    public synchronized void addTemplate(EmailTemplate template) {
        if (template == null) return;

        // ID 설정
        template.setId(nextId++);

        // 중복 체크 (제목과 내용이 모두 같은 경우)
        boolean isDuplicate = templates.stream()
                .anyMatch(t -> t != null &&
                        t.getTitle().equals(template.getTitle()) &&
                        t.getContent().equals(template.getContent()));

        if (!isDuplicate) {
            templates.add(template);
            categories.add(template.getCategory());
            saveTemplates();
            notifyListeners();
            System.out.println("템플릿 추가됨: " + template.getTitle() + " (ID: " + template.getId() + ")");
        } else {
            System.out.println("중복 템플릿 감지, 추가 취소: " + template.getTitle());
        }
    }

    /**
     * 템플릿 수정
     */
    public void updateTemplate(EmailTemplate template) {
        for (int i = 0; i < templates.size(); i++) {
            if (templates.get(i).getId().equals(template.getId())) {
                templates.set(i, template);
                categories.add(template.getCategory());
                saveTemplates();
                notifyListeners();
                break;
            }
        }
    }

    /**
     * 템플릿 삭제
     */
    public void deleteTemplate(Long id) {
        templates.removeIf(t -> t.getId().equals(id));
        saveTemplates();
        notifyListeners();
    }

    /**
     * ID로 템플릿 찾기
     */
    public EmailTemplate getTemplateById(Long id) {
        return templates.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * 모든 템플릿 가져오기
     */
    public List<EmailTemplate> getAllTemplates() {
        // 중복 제거된 리스트 반환
        Set<Long> seenIds = new HashSet<>();
        List<EmailTemplate> uniqueTemplates = new ArrayList<>();

        for (EmailTemplate template : templates) {
            if (template != null && template.getId() != null && !seenIds.contains(template.getId())) {
                seenIds.add(template.getId());
                uniqueTemplates.add(template);
            }
        }

        return new ArrayList<>(uniqueTemplates);
    }

    /**
     * 카테고리별 템플릿 가져오기
     */
    public List<EmailTemplate> getTemplatesByCategory(String category) {
        return templates.stream()
                .filter(t -> t.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    /**
     * 중복 템플릿 제거
     */
    public synchronized void removeDuplicates() {
        Map<String, EmailTemplate> uniqueMap = new LinkedHashMap<>();

        for (EmailTemplate template : templates) {
            if (template != null) {
                String key = template.getTitle() + "|" + template.getContent();
                uniqueMap.putIfAbsent(key, template);
            }
        }

        templates = new ArrayList<>(uniqueMap.values());
        saveTemplates();
    }

    /**
     * 템플릿 검색
     */
    public List<EmailTemplate> searchTemplates(String keyword) {
        String lowercaseKeyword = keyword.toLowerCase();
        return templates.stream()
                .filter(t -> t.getTitle().toLowerCase().contains(lowercaseKeyword) ||
                        t.getContent().toLowerCase().contains(lowercaseKeyword) ||
                        t.getCategory().toLowerCase().contains(lowercaseKeyword))
                .collect(Collectors.toList());
    }

    /**
     * 모든 카테고리 가져오기
     */
    public Set<String> getAllCategories() {
        // 현재 템플릿들의 카테고리 수집
        Set<String> currentCategories = templates.stream()
                .map(EmailTemplate::getCategory)
                .collect(Collectors.toSet());
        categories.addAll(currentCategories);
        return new TreeSet<>(categories); // 정렬된 Set 반환
    }

    /**
     * 템플릿 저장
     */
    private void saveTemplates() {
        // 저장 전 중복 제거
        Set<Long> seenIds = new HashSet<>();
        List<EmailTemplate> uniqueTemplates = new ArrayList<>();

        for (EmailTemplate template : templates) {
            if (template != null && template.getId() != null && !seenIds.contains(template.getId())) {
                seenIds.add(template.getId());
                uniqueTemplates.add(template);
            }
        }

        templates = uniqueTemplates;

        // 파일에 저장
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TEMPLATES_FILE))) {
            oos.writeObject(templates);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 저장된 템플릿 불러오기
     */
    @SuppressWarnings("unchecked")
    private void loadTemplates() {
        File file = new File(TEMPLATES_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                templates = (List<EmailTemplate>) ois.readObject();

                // 중복 제거
                Set<Long> seenIds = new HashSet<>();
                List<EmailTemplate> uniqueTemplates = new ArrayList<>();

                for (EmailTemplate template : templates) {
                    if (template != null && template.getId() != null && !seenIds.contains(template.getId())) {
                        seenIds.add(template.getId());
                        uniqueTemplates.add(template);
                    }
                }

                templates = uniqueTemplates;

                // 다음 ID 설정
                nextId = templates.stream()
                        .mapToLong(EmailTemplate::getId)
                        .max()
                        .orElse(0L) + 1;

                // 카테고리 수집
                templates.forEach(t -> categories.add(t.getCategory()));
            } catch (Exception e) {
                e.printStackTrace();
                templates = new ArrayList<>();
            }
        }
    }
}