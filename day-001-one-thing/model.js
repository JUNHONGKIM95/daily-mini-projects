export const STORAGE_KEY = "one-thing-days-v1";

export function toDateKey(date = new Date()) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

export function readDays(storage) {
  try {
    const parsed = JSON.parse(storage.getItem(STORAGE_KEY) ?? "{}");
    return parsed && typeof parsed === "object" && !Array.isArray(parsed) ? parsed : {};
  } catch {
    return {};
  }
}

export function saveTask(storage, dateKey, text) {
  const days = readDays(storage);
  const cleanText = text.trim().replace(/\s+/g, " ").slice(0, 80);
  if (!cleanText) throw new Error("할 일을 입력해주세요.");

  days[dateKey] = {
    text: cleanText,
    completed: days[dateKey]?.completed ?? false,
    updatedAt: new Date().toISOString(),
  };
  storage.setItem(STORAGE_KEY, JSON.stringify(days));
  return days[dateKey];
}

export function toggleTask(storage, dateKey) {
  const days = readDays(storage);
  if (!days[dateKey]) return null;
  days[dateKey].completed = !days[dateKey].completed;
  days[dateKey].updatedAt = new Date().toISOString();
  storage.setItem(STORAGE_KEY, JSON.stringify(days));
  return days[dateKey];
}

export function recentDays(today = new Date(), count = 7) {
  return Array.from({ length: count }, (_, index) => {
    const date = new Date(today);
    date.setHours(12, 0, 0, 0);
    date.setDate(date.getDate() - (count - 1 - index));
    return { key: toDateKey(date), day: new Intl.DateTimeFormat("ko-KR", { weekday: "narrow" }).format(date) };
  });
}

export function completionCount(days, range) {
  return range.filter(({ key }) => days[key]?.completed).length;
}

