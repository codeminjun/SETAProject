package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 이메일 템플릿 데이터를 담는 모델 클래스
 */
public class EmailTemplate implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String content;
    private String category;
    private boolean isFavorite;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 변수 패턴: {변수명} 형태
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{([^}]+)\\}");

    public EmailTemplate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isFavorite = false;
    }

    public EmailTemplate(String title, String content, String category) {
        this();
        this.title = title;
        this.content = content;
        this.category = category;
    }

    /**
     * 템플릿 내의 변수 목록을 추출
     * @return 변수명 리스트
     */
    public List<String> extractVariables() {
        List<String> variables = new ArrayList<>();
        Matcher matcher = VARIABLE_PATTERN.matcher(content);

        while (matcher.find()) {
            String variable = matcher.group(1);
            if (!variables.contains(variable)) {
                variables.add(variable);
            }
        }

        return variables;
    }

    /**
     * 템플릿 내용에서 변수를 치환
     * @param variableName 변수명
     * @param value 치환할 값
     * @return 치환된 내용
     */
    public String replaceVariable(String variableName, String value) {
        return content.replace("{" + variableName + "}", value);
    }

    /**
     * 여러 변수를 한번에 치환
     * @param replacements 변수명-값 쌍
     * @return 치환된 내용
     */
    public String replaceAllVariables(java.util.Map<String, String> replacements) {
        String result = content;
        for (java.util.Map.Entry<String, String> entry : replacements.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailTemplate that = (EmailTemplate) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}