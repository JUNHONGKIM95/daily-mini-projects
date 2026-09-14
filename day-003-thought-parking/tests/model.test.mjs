import test from "node:test";
import assert from "node:assert/strict";
import { MAX_THOUGHTS, addThought, createThought, formatParkedTime, removeThought, restoreThoughts } from "../model.js";

test("생각을 정리해 새 항목을 만든다", () => {
  assert.deepEqual(createThought("  우유   사기  ", 1000, "id-1"), { id: "id-1", text: "우유 사기", createdAt: 1000 });
  assert.throws(() => createThought("   ", 1000, "id-2"), /적어주세요/);
});

test("새 생각은 목록 맨 앞에 주차한다", () => {
  const oldThought = createThought("기존 생각", 1000, "old");
  const newThought = createThought("새 생각", 2000, "new");
  assert.deepEqual(addThought([oldThought], newThought).map(({ id }) => id), ["new", "old"]);
});

test("최대 보관 개수를 넘으면 오래된 생각부터 제외한다", () => {
  const thoughts = Array.from({ length: MAX_THOUGHTS }, (_, index) => createThought(String(index), index, String(index)));
  const result = addThought(thoughts, createThought("새 생각", 999, "new"));
  assert.equal(result.length, MAX_THOUGHTS);
  assert.equal(result[0].id, "new");
  assert.equal(result.some(({ id }) => id === String(MAX_THOUGHTS - 1)), false);
});

test("선택한 생각만 정리한다", () => {
  const thoughts = [createThought("하나", 1, "one"), createThought("둘", 2, "two")];
  assert.deepEqual(removeThought(thoughts, "one").map(({ id }) => id), ["two"]);
});

test("저장 데이터 복구 시 잘못된 항목을 걸러낸다", () => {
  const valid = createThought("정상", 1, "valid");
  assert.deepEqual(restoreThoughts([valid, { id: "bad", text: "", createdAt: 1 }, null]), [valid]);
});

test("오늘 주차한 시각을 읽기 쉽게 표시한다", () => {
  const now = new Date(2026, 8, 15, 18, 0);
  const parked = new Date(2026, 8, 15, 9, 5).getTime();
  assert.match(formatParkedTime(parked, now), /^오늘 09:05$/);
});
