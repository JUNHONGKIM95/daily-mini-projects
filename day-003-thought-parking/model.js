export const MAX_THOUGHT_LENGTH = 120;
export const MAX_THOUGHTS = 50;

export function normalizeThought(value) {
  const thought = String(value ?? "").trim().replace(/\s+/g, " ");
  if (!thought) throw new Error("주차할 생각을 적어주세요.");
  return thought.slice(0, MAX_THOUGHT_LENGTH);
}

export function createThought(text, now = Date.now(), id = globalThis.crypto?.randomUUID?.()) {
  return {
    id: id || `${Number(now)}-${Math.random().toString(36).slice(2)}`,
    text: normalizeThought(text),
    createdAt: Number(now),
  };
}

export function addThought(thoughts, thought) {
  const current = Array.isArray(thoughts) ? thoughts : [];
  return [thought, ...current].slice(0, MAX_THOUGHTS);
}

export function removeThought(thoughts, id) {
  return (Array.isArray(thoughts) ? thoughts : []).filter((thought) => thought.id !== id);
}

export function isValidThought(value) {
  return Boolean(value && typeof value.id === "string" && typeof value.text === "string" && value.text.trim() && Number.isFinite(value.createdAt));
}

export function restoreThoughts(value) {
  return (Array.isArray(value) ? value : []).filter(isValidThought).slice(0, MAX_THOUGHTS);
}

export function formatParkedTime(timestamp, now = new Date()) {
  const date = new Date(timestamp);
  if (Number.isNaN(date.getTime())) return "시간 미상";
  const isToday = date.getFullYear() === now.getFullYear() && date.getMonth() === now.getMonth() && date.getDate() === now.getDate();
  const time = new Intl.DateTimeFormat("ko-KR", { hour: "2-digit", minute: "2-digit", hour12: false }).format(date);
  if (isToday) return `오늘 ${time}`;
  return new Intl.DateTimeFormat("ko-KR", { month: "short", day: "numeric", hour: "2-digit", minute: "2-digit", hour12: false }).format(date);
}
