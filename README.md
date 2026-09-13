# Daily Mini Projects

일상의 작은 불편을 핵심 기능 하나로 해결하는 100일 미니 프로젝트 챌린지입니다.

## 원칙

- 하루 안에 완성할 수 있는 크기로 만든다.
- 프로젝트마다 핵심 기능은 하나만 선명하게 둔다.
- 회원가입 없이 바로 써볼 수 있게 만든다.
- 모바일 화면과 접근성을 기본으로 챙긴다.
- 완성한 날에는 짧게라도 회고를 남긴다.

## 진행 현황

| Day | 프로젝트 | 해결하는 불편 | 상태 |
| --- | --- | --- | --- |
| 001 | [오늘 하나](./day-001-one-thing) · [웹 실행](https://junhongkim95.github.io/daily-mini-projects/day-001-one-thing/) · [Android APK](https://github.com/JUNHONGKIM95/daily-mini-projects/releases/download/day-001-v1.0.1/app-debug.apk) | 해야 할 일이 많아 정작 중요한 한 가지를 놓침 | ✅ 배포 |

전체 주제는 [100가지 프로젝트 아이디어](./IDEAS.md)에서 확인할 수 있습니다.

배포된 프로젝트 모음: <https://junhongkim95.github.io/daily-mini-projects/>

## 배포 규칙

`main` 브랜치에 반영된 정적 파일은 GitHub Pages로 자동 배포됩니다. 각 프로젝트는 `day-NNN-project-name/` 폴더에 추가하며, 같은 경로의 공개 URL을 갖습니다.

## 실행

첫 프로젝트는 빌드 과정이 없는 정적 웹앱입니다.

```bash
cd day-001-one-thing
python -m http.server 4173
```

브라우저에서 `http://localhost:4173`을 열면 됩니다.
