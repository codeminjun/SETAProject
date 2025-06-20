# ✨ Smart Email Template Assistant

**나만의 이메일 템플릿을 스마트하게 관리하고 발송하는 데스크톱 애플리케이션**

<p align="center">
  <img src="https://img.shields.io/badge/Language-Java-orange?style=for-the-badge&logo=java" alt="Java"/>
  <img src="https://img.shields.io/badge/Framework-Java%20Swing-blue?style=for-the-badge" alt="Java Swing"/>
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="License: MIT"/>
</p>

*단국대학교 모바일시스템공학과 2학년 전공선택 자바프로그래밍 Final 프로젝트*


## 📖 프로젝트 개요

**Smart Template Assistant**는 반복적으로 작성하는 이메일, 보고서, 코드 스니펫 등의 텍스트를 템플릿으로 효율적으로 관리하기 위해 개발된 Java Swing 기반의 데스크톱 애플리케이션입니다. 반복적인 텍스트 작성을 자동화하고, 동적 변수를 사용해 개인화된 내용을 손쉽게 생성할 수 있으며, Gmail 계정과 연동하여 바로 이메일을 발송할 수 있는 기능을 제공합니다.

### 기획 의도 및 목적
저는 평소 이메일이나 과제 제출 시 비슷한 형식의 글을 반복해서 작성하는 일이 많았습니다. 이러한 비효율을 줄이고자, 자주 사용하는 문구를 저장하고 쉽게 꺼내 쓸 수 있는 프로그램을 직접 만들고 싶다는 생각에서 이 프로젝트를 시작하게 되었습니다.

본 프로젝트를 통해 다음과 같은 학습 목표를 달성하고자 했습니다.
* **Java Swing**을 이용한 GUI 프로그래밍 역량 강화
* 파일 입출력을 통한 데이터 영속성 처리 능력 습득
* **객체지향 프로그래밍(OOP)** 원칙에 기반한 체계적인 프로그램 설계
* 외부 라이브러리(Jakarta Mail) 연동 및 활용 경험

## 🚀 주요 기능 상세 설명

* **✍️ 템플릿 관리 (CRUD)**: 나만의 템플릿을 자유롭게 생성, 수정, 삭제할 수 있습니다.
* **🔍 실시간 검색 및 필터링**: 제목, 내용, 카테고리, 즐겨찾기 여부로 원하는 템플릿을 빠르게 찾을 수 있습니다.
* **🔄 동적 변수 치환**: `{name}`, `{date}` 와 같은 변수를 템플릿에 포함시키고, 미리보기에서 실제 값으로 쉽게 치환할 수 있습니다.
* **⭐ 즐겨찾기**: 자주 사용하는 템플릿을 즐겨찾기로 등록하여 따로 모아볼 수 있습니다.
* **✉️ Gmail 연동 이메일 발송**: 완성된 템플릿을 Gmail 계정을 통해 즉시 발송할 수 있습니다. (2단계 인증 및 앱 비밀번호 필요)
* **💾 데이터 영속성**: 생성한 템플릿과 이메일 계정 정보는 파일(`templates.dat`, `email_credentials.properties`)로 저장되어 프로그램이 종료되어도 유지됩니다.

## ⚙️ 실행 가이드

이 프로젝트를 로컬 컴퓨터에서 원활하게 실행하시기 위한 가이드입니다.

### ✅ 사전 요구 사항

1.  **JDK (Java Development Kit) 11 이상**: 자바 컴파일 및 실행 환경
2.  **Git**: 소스 코드 다운로드

### 💻 방법 1: IntelliJ IDEA 사용 (권장)

가장 간단하고 확실한 방법입니다.

1.  **프로젝트 클론**
    터미널 또는 Git Bash를 열고 아래 명령어를 실행하여 프로젝트를 다운로드합니다.
    ```bash
    git clone [https://github.com/codeminjun/SETAProject.git](https://github.com/codeminjun/SETAProject.git)
    ```

2.  **프로젝트 열기**
    IntelliJ IDEA를 실행하고 `File > Open...` 메뉴를 통해 다운로드한 `SETAProject` 폴더를 선택하여 엽니다.

3.  **라이브러리 설정 (가장 중요)**
    이메일 발송 기능을 위해 `jakarta.mail-1.6.7.jar` 파일을 프로젝트 라이브러리로 설정해야 합니다.
    * `File > Project Structure...` 메뉴로 들어갑니다. (단축키: `Cmd + ;` 또는 `Ctrl+Alt+Shift+S`)
    * 왼쪽 탭에서 `Modules`를 선택합니다.
    * 중앙 패널에서 `Dependencies` 탭을 선택합니다.
    * `+` 아이콘을 클릭하고 `JARs or directories...`를 선택합니다.
    * 프로젝트 폴더 내의 `lib/jakarta.mail-1.6.7.jar` 파일을 찾아 선택하고 `OK`를 누릅니다.
    * `OK`를 눌러 설정을 완료합니다.

4.  **프로그램 실행**
    왼쪽 프로젝트 탐색기에서 `JAVA_SETA/src/Main.java` 파일을 찾아 우클릭한 후, `Run 'Main.main()'`을 선택하여 프로그램을 실행합니다.

### ⌨️ 방법 2: 터미널(CLI) 사용

1.  **프로젝트 클론**: 방법 1과 동일하게 프로젝트를 다운로드합니다.

2.  **디렉토리 이동**: 터미널에서 프로젝트 폴더로 이동합니다.
    ```bash
    cd SETAProject
    ```

3.  **컴파일**
    아래 명령어를 실행하여 소스 코드를 컴파일합니다. `out` 폴더가 생성됩니다. (macOS/Linux 기준, Windows는 `:`를 `;`로 변경)
    ```bash
    javac -cp "lib/jakarta.mail-1.6.7.jar" -d out JAVA_SETA/src/**/*.java
    ```

4.  **실행**
    아래 명령어로 프로그램을 실행합니다. (macOS/Linux 기준, Windows는 `:`를 `;`로 변경)
    ```bash
    java -cp "out:lib/jakarta.mail-1.6.7.jar" Main
    ```

### ♻️ 애플리케이션 초기화
저장된 모든 템플릿과 설정을 지우고 싶을 경우, 아래의 인자(`--reset`)와 함께 프로그램을 실행할 수 있습니다.
```bash
java -cp "out:lib/jakarta.mail-1.6.7.jar" Main --reset