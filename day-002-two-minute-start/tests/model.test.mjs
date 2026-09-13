import test from "node:test";
import assert from "node:assert/strict";
import { TWO_MINUTES_MS, createSession, formatRemaining, isValidSession, normalizeTask, progressRatio, remainingMs } from "../model.js";

test("입력 앞뒤와 연속 공백을 정리한다", () => {
  assert.equal(normalizeTask("  보고서   파일 열기  "), "보고서 파일 열기");
  assert.throws(() => normalizeTask("   "), /적어주세요/);
});

test("종료 시각을 기준으로 남은 시간을 계산한다", () => {
  assert.equal(remainingMs(121_000, 1_000), TWO_MINUTES_MS);
  assert.equal(remainingMs(1_000, 2_000), 0);
});

test("남은 시간을 올림해 MM:SS로 표시한다", () => {
  assert.equal(formatRemaining(120_000), "02:00");
  assert.equal(formatRemaining(60_001), "01:01");
  assert.equal(formatRemaining(0), "00:00");
});

test("진행 비율은 0과 1 사이로 제한한다", () => {
  assert.equal(progressRatio(60_000, 120_000), 0.5);
  assert.equal(progressRatio(150_000, 120_000), 1);
  assert.equal(progressRatio(-1, 120_000), 0);
});

test("복구 가능한 세션을 생성한다", () => {
  const session = createSession("파일 열기", TWO_MINUTES_MS, 10_000);
  assert.deepEqual(session, { task: "파일 열기", duration: TWO_MINUTES_MS, deadline: 130_000 });
  assert.equal(isValidSession(session), true);
  assert.equal(isValidSession({ task: "", duration: 1, deadline: 1 }), false);
});
