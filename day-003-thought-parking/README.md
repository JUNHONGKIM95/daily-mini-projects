# Day 003 · 딴생각 주차장

집중 중 갑자기 떠오른 생각을 한 줄로 빠르게 보관하고 하던 일로 돌아가게 돕는 웹앱입니다.

## 핵심 기능

- 한 줄 입력 후 Enter로 즉시 보관
- 브라우저 로컬 저장소에 자동 저장
- 저장 시각 표시 및 개별 생각 정리
- PWA 설치와 오프라인 실행 지원

## 실행

저장소 루트에서 아래 명령을 실행합니다.

```bash
python -m http.server 4173
```

`http://localhost:4173/day-003-thought-parking/`에서 확인할 수 있습니다.

## 테스트

```bash
node --test day-003-thought-parking/tests/*.test.mjs
```

## 배포

<https://junhongkim95.github.io/daily-mini-projects/day-003-thought-parking/>

### Android 앱

[Android APK 다운로드](https://github.com/JUNHONGKIM95/daily-mini-projects/releases/download/day-003-v1.0.0/thought-parking-v1.0.0.apk)

- 웹뷰가 아닌 Android 네이티브 화면으로 구현했습니다.
- 미정리 생각이 있으면 최신 생각과 전체 개수를 고정 알림으로 표시합니다.
- 알림에서 최근 생각 하나를 바로 정리할 수 있습니다.
- 재부팅 및 앱 업데이트 후에도 남은 생각의 알림을 복원합니다.
- Android 13 이상에서는 알림 권한을 허용해야 고정 알림이 표시됩니다.

Android Studio에서 `android/` 폴더를 열거나 다음 명령으로 빌드할 수 있습니다.

```bash
cd android
gradle assembleRelease
```

## 오늘의 회고

집중을 덜 방해하는 것이 목적이라 입력부터 저장까지 클릭을 추가하지 않았습니다. 생각을 안전하게 맡겼다는 짧은 피드백만 보여주고 입력창으로 바로 돌아옵니다.
