export const TWO_MINUTES_MS = 2 * 60 * 1000;
export const FIVE_MINUTES_MS = 5 * 60 * 1000;

export function normalizeTask(value) {
  const task = String(value ?? "").trim().replace(/\s+/g, " ");
  if (!task) throw new Error("미루고 있는 일을 적어주세요.");
  return task.slice(0, 80);
}

export function remainingMs(deadline, now = Date.now()) {
  const end = Number(deadline);
  const current = Number(now);
  if (!Number.isFinite(end) || !Number.isFinite(current)) return 0;
  return Math.max(0, end - current);
}

export function formatRemaining(milliseconds) {
  const seconds = Math.max(0, Math.ceil(Number(milliseconds) / 1000) || 0);
  const minutes = Math.floor(seconds / 60);
  return `${String(minutes).padStart(2, "0")}:${String(seconds % 60).padStart(2, "0")}`;
}

export function progressRatio(milliseconds, duration) {
  const total = Number(duration);
  if (!Number.isFinite(total) || total <= 0) return 0;
  return Math.min(1, Math.max(0, Number(milliseconds) / total || 0));
}

export function createSession(task, duration = TWO_MINUTES_MS, now = Date.now()) {
  const safeDuration = Number(duration);
  if (!Number.isFinite(safeDuration) || safeDuration <= 0) throw new Error("올바른 타이머 시간이 필요합니다.");
  return { task: normalizeTask(task), duration: safeDuration, deadline: Number(now) + safeDuration };
}

export function isValidSession(value) {
  return Boolean(value && typeof value.task === "string" && value.task.trim() && Number.isFinite(value.duration) && value.duration > 0 && Number.isFinite(value.deadline));
}
