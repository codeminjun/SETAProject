<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Smart Template Assistant - README</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
            line-height: 1.6;
            background-color: #f6f8fa;
            color: #24292e;
            margin: 0;
            padding: 0;
        }
        .container {
            max-width: 900px;
            margin: 40px auto;
            background-color: #ffffff;
            border: 1px solid #d1d5da;
            border-radius: 6px;
            padding: 45px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.04);
        }
        h1, h2, h3 {
            border-bottom: 1px solid #eaecef;
            padding-bottom: 0.3em;
            margin-top: 24px;
            margin-bottom: 16px;
            font-weight: 600;
        }
        h1 {
            font-size: 2.25em;
            text-align: center;
        }
        h2 {
            font-size: 1.75em;
        }
        h3 {
            font-size: 1.25em;
        }
        p {
            margin-bottom: 16px;
        }
        ul {
            padding-left: 20px;
        }
        li {
            margin-bottom: 8px;
        }
        code {
            font-family: "SFMono-Regular", Consolas, "Liberation Mono", Menlo, Courier, monospace;
            background-color: rgba(27,31,35,0.05);
            padding: 0.2em 0.4em;
            margin: 0;
            font-size: 85%;
            border-radius: 3px;
        }
        pre {
            background-color: #f6f8fa;
            border-radius: 3px;
            padding: 16px;
            overflow: auto;
        }
        pre code {
            padding: 0;
            margin: 0;
            font-size: 100%;
            background-color: transparent;
        }
        a {
            color: #0366d6;
            text-decoration: none;
        }
        a:hover {
            text-decoration: underline;
        }
        .text-center {
            text-align: center;
        }
        .badges img {
            margin: 0 5px;
        }
        .screenshot {
            width: 100%;
            max-width: 700px;
            border: 1px solid #d1d5da;
            border-radius: 6px;
            display: block;
            margin: 20px auto;
        }
        .tech-tag {
            display: inline-block;
            background-color: #e1ecf4;
            color: #39739d;
            padding: 5px 10px;
            border-radius: 3px;
            font-size: 0.9em;
            margin: 3px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1 class="text-center">✨ Smart Template Assistant</h1>
        <p class="text-center">
            <strong>나만의 이메일 템플릿을 스마트하게 관리하고 발송하세요!</strong>
        </p>
        <div class="badges text-center">
            <img src="https://img.shields.io/badge/Language-Java-orange?style=for-the-badge&logo=java" alt="Java"/>
            <img src="https://img.shields.io/badge/Framework-Java%20Swing-blue?style=for-the-badge" alt="Java Swing"/>
            <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="License: MIT"/>
        </div>
        <p class="text-center" style="margin-top: 1em;">
            <em>단국대학교 소프트웨어학과 '자바프로그래밍' 기말 프로젝트</em>
        </p>
        
        <img src="https://github.com/user-attachments/assets/751d3b01-576e-44d4-95ea-7a3ed14f77c5" alt="App Screenshot" class="screenshot">

        <h2>📌 프로젝트 소개</h2>
        <p><strong>Smart Template Assistant</strong>는 자주 사용하는 이메일이나 문서의 템플릿을 효율적으로 관리하기 위해 개발된 Java Swing 기반의 데스크톱 애플리케이션입니다. 반복적인 텍스트 작성을 자동화하고, 동적 변수를 사용해 개인화된 내용을 손쉽게 생성할 수 있으며, Gmail 계정과 연동하여 바로 이메일을 발송할 수 있는 기능을 제공합니다.</p>
        <p>이 프로젝트는 2025년 1학기 단국대학교 자바프로그래밍 수업의 기말 프로젝트로 진행되었습니다.</p>

        <h2>🚀 주요 기능</h2>
        <ul>
            <li><strong>✍️ 템플릿 관리 (CRUD)</strong>: 나만의 템플릿을 자유롭게 생성, 수정, 삭제할 수 있습니다.</li>
            <li><strong>🔍 실시간 검색 및 필터링</strong>: 제목, 내용, 카테고리, 즐겨찾기 여부로 원하는 템플릿을 빠르게 찾을 수 있습니다.</li>
            <li><strong>🔄 동적 변수 치환</strong>: <code>{name}</code>, <code>{date}</code> 와 같은 변수를 템플릿에 포함시키고, 미리보기에서 실제 값으로 쉽게 치환할 수 있습니다.</li>
            <li><strong>⭐ 즐겨찾기</strong>: 자주 사용하는 템플릿을 즐겨찾기로 등록하여 따로 모아볼 수 있습니다.</li>
            <li><strong>🎨 테마 지원</strong>: 사용자의 취향에 맞춰 라이트 모드와 다크 모드를 선택할 수 있습니다.</li>
            <li><strong>✉️ 이메일 발송</strong>: 완성된 템플릿을 Gmail 계정을 통해 즉시 발송할 수 있습니다. (2단계 인증 및 앱 비밀번호 필요)</li>
            <li><strong>💾 데이터 영속성</strong>: 생성한 템플릿과 설정은 파일(<code>templates.dat</code>, <code>settings.properties</code>)로 저장되어 프로그램이 종료되어도 유지됩니다.</li>
        </ul>

        <h2>🛠 기술 스택</h2>
        <div>
            <span class="tech-tag">Java</span>
            <span class="tech-tag">Java Swing</span>
            <span class="tech-tag">javax.mail (Jakarta Mail)</span>
            <span class="tech-tag">IntelliJ IDEA</span>
        </div>

        <h2>⚙️ 실행 방법</h2>
        <h3>사전 요구 사항</h3>
        <ul>
            <li><code>JDK 11</code> 버전 이상 설치</li>
        </ul>
        <h3>IDE에서 실행</h3>
        <ol>
            <li>이 프로젝트를 컴퓨터에 클론(Clone)합니다.</li>
            <li>IntelliJ IDEA 또는 Eclipse와 같은 자바 IDE에서 프로젝트를 엽니다.</li>
            <li><code>src/Main.java</code> 파일을 찾아 실행합니다.</li>
        </ol>
        <h3>터미널에서 실행 (JAR 파일이 없을 경우)</h3>
        <ol>
            <li>프로젝트의 최상위 디렉토리로 이동합니다.</li>
            <li>아래 명령어로 소스 코드를 컴파일합니다.
                <pre><code>javac -d out JAVA_SETA/src/**/*.java</code></pre>
            </li>
            <li>아래 명령어로 프로그램을 실행합니다.
                <pre><code>java -cp out Main</code></pre>
            </li>
        </ol>
        <h3>애플리케이션 초기화</h3>
        <p>저장된 모든 템플릿과 설정을 지우고 싶을 경우, 아래의 인자(argument)와 함께 프로그램을 실행할 수 있습니다.</p>
        <pre><code>java -cp out Main --reset</code></pre>

        <h2>💡 앞으로 개선하고 싶은 점</h2>
        <ul>
            <li><strong>리치 텍스트 편집기 도입</strong>: 볼드, 이탤릭, 색상 등 서식을 지원하는 편집기(JEditorPane 등) 적용</li>
            <li><strong>다양한 이메일 프로바이더 지원</strong>: Gmail 외에 Naver, Outlook 등 다른 SMTP 서버 연동 기능 추가</li>
            <li><strong>템플릿 가져오기/내보내기</strong>: JSON 또는 CSV 형식으로 템플릿 데이터를 백업하고 복원하는 기능</li>
            <li><strong>사용 통계</strong>: 각 템플릿이 얼마나 자주 사용되었는지 추적하는 통계 기능</li>
        </ul>

        <h2>🧑‍💻 만든 사람</h2>
        <ul>
            <li><strong>김민준 (MinJun Kim)</strong> - 단국대학교 소프트웨어학과</li>
            <li><strong>GitHub</strong>: <a href="https://github.com/codeminjun" target="_blank">codeminjun</a></li>
        </ul>

        <h2>📄 라이선스</h2>
        <p>이 프로젝트는 MIT 라이선스를 따릅니다. 자세한 내용은 <code>LICENSE</code> 파일을 참고하세요.</p>
    </div>
</body>
</html>
