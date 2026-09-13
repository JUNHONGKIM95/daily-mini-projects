# Day 001 — 오늘 하나

해야 할 일이 많을수록 가장 중요한 한 가지를 놓치는 문제를 해결하는 설치형 모바일 웹앱입니다.

## 핵심 기능

오늘 반드시 끝낼 일 하나를 적고, 완료하면 체크합니다. 날짜가 바뀌면 이전 기록은 보관되고 새로운 하루가 시작됩니다.

## 특징

- 회원가입과 서버 없이 브라우저에만 저장
- 모바일 우선 반응형 화면
- 홈 화면 설치 지원(PWA)
- 설치 후 오프라인 사용 가능
- 키보드 및 스크린 리더 접근성 지원
- Android 홈 화면 위젯에서 할 일 확인 및 완료 체크

## Android 앱과 위젯

`android/`는 Android 8.0 이상을 지원하는 네이티브 앱 프로젝트입니다. Android Studio에서 해당 폴더를 열어 실행할 수 있습니다.

1. 앱에서 오늘의 한 가지를 입력하고 저장합니다.
2. 홈 화면을 길게 누르고 **위젯**을 선택합니다.
3. **오늘 하나** 위젯을 홈 화면에 추가합니다.
4. 위젯 오른쪽 동그라미를 눌러 완료 상태를 바꿉니다.

[Android APK 다운로드](https://github.com/JUNHONGKIM95/daily-mini-projects/releases/download/day-001-v1.0.0/app-debug.apk)

이 APK는 기능 확인을 위한 디버그 서명 버전입니다. Android에서 처음 설치할 때 다운로드에 사용한 브라우저의 **알 수 없는 앱 설치** 권한을 한 번 허용해야 할 수 있습니다.

## 실행

```bash
python -m http.server 4173
```

`http://localhost:4173`에서 확인하세요. 서비스 워커는 `localhost` 또는 HTTPS에서 동작합니다.

## 테스트

```bash
node --test tests/model.test.mjs
```

## 기술

HTML, CSS, Vanilla JavaScript만 사용했습니다. 빌드와 외부 의존성이 없습니다.
