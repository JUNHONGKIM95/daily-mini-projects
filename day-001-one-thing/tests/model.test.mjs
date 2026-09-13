import test from "node:test";
import assert from "node:assert/strict";
import { completionCount, readDays, recentDays, saveTask, toDateKey, toggleTask } from "../model.js";

class MemoryStorage {
  data = new Map();
  getItem(key) { return this.data.get(key) ?? null; }
  setItem(key, value) { this.data.set(key, value); }
}

test("local date key is stable", () => {
  assert.equal(toDateKey(new Date(2026, 8, 13)), "2026-09-13");
});

test("task is trimmed, saved, and toggled", () => {
  const storage = new MemoryStorage();
  const saved = saveTask(storage, "2026-09-13", "  병원   예약하기  ");
  assert.equal(saved.text, "병원 예약하기");
  assert.equal(saved.completed, false);
  assert.equal(toggleTask(storage, "2026-09-13").completed, true);
  assert.equal(readDays(storage)["2026-09-13"].completed, true);
});

test("empty task is rejected", () => {
  assert.throws(() => saveTask(new MemoryStorage(), "2026-09-13", "  "), /입력/);
});

test("recent completion count only includes requested range", () => {
  const range = recentDays(new Date(2026, 8, 13), 3);
  const days = {
    [range[0].key]: { completed: true },
    [range[1].key]: { completed: false },
    [range[2].key]: { completed: true },
    "2020-01-01": { completed: true },
  };
  assert.equal(completionCount(days, range), 2);
});

