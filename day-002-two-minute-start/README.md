# Day 002 · 2분 시동

시작이 부담스러워 미루는 일을 한 줄로 적고, 딱 2분만 움직이게 돕는 타이머입니다.

## 핵심 기능

- 일을 입력하면 바로 2분 카운트다운 시작
- 화면이 백그라운드에 있어도 실제 종료 시각 기준으로 정확히 계산
- 2분 뒤 `5분 더` 또는 `여기까지` 선택
- 진행 상태를 브라우저에 저장해 새로고침 뒤에도 복구
- PWA 설치 및 오프라인 실행 지원

## 실행

빌드와 외부 패키지가 필요 없는 정적 웹앱입니다.

```bash
python -m http.server 4173
```

`http://localhost:4173/day-002-two-minute-start/`에서 확인할 수 있습니다.

## 테스트

```bash
node --test day-002-two-minute-start/tests/*.test.mjs
```

## 배포

<https://junhongkim95.github.io/daily-mini-projects/day-002-two-minute-start/>

## 오늘의 회고

타이머가 백그라운드에서 느려져도 남은 시간이 틀어지지 않도록 숫자를 1초씩 줄이는 대신 종료 시각과 현재 시각의 차이를 사용했습니다.
