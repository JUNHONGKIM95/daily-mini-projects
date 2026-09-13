import { completionCount, readDays, recentDays, saveTask, toDateKey, toggleTask } from "./model.js";

const $ = (selector) => document.querySelector(selector);
const today = new Date();
const todayKey = toDateKey(today);
let installPrompt = null;

const elements = {
  todayLabel: $("#todayLabel"),
  form: $("#taskForm"),
  input: $("#taskInput"),
  counter: $("#counter"),
  saved: $("#savedTask"),
  text: $("#taskText"),
  complete: $("#completeButton"),
  edit: $("#editButton"),
  encouragement: $("#encouragement"),
  week: $("#weekList"),
  streakCopy: $("#streakCopy"),
  install: $("#installButton"),
};

elements.todayLabel.textContent = new Intl.DateTimeFormat("ko-KR", {
  month: "long",
  day: "numeric",
  weekday: "long",
}).format(today);

function render() {
  const days = readDays(localStorage);
  const task = days[todayKey];
  elements.form.hidden = Boolean(task);
  elements.saved.hidden = !task;

  if (task) {
    elements.text.textContent = task.text;
    elements.complete.setAttribute("aria-pressed", String(task.completed));
    elements.complete.classList.toggle("is-complete", task.completed);
    elements.encouragement.textContent = task.completed ? "좋아요. 오늘의 하나를 끝냈어요!" : "한 가지에만 집중해요.";
  }

  const range = recentDays(today);
  const completed = completionCount(days, range);
  elements.streakCopy.textContent = completed ? `최근 7일 동안 ${completed}개의 중요한 일을 끝냈어요.` : "오늘부터 가볍게 시작해요.";
  elements.week.replaceChildren(
    ...range.map(({ key, day }) => {
      const item = document.createElement("li");
      const state = days[key]?.completed ? "done" : days[key] ? "planned" : "empty";
      item.className = state;
      item.setAttribute("aria-label", `${key}, ${state === "done" ? "완료" : state === "planned" ? "진행 중" : "기록 없음"}`);
      item.innerHTML = `<span>${day}</span><i aria-hidden="true"></i>`;
      return item;
    }),
  );
}

elements.input.addEventListener("input", () => {
  elements.counter.textContent = `${elements.input.value.length} / 80`;
});

elements.form.addEventListener("submit", (event) => {
  event.preventDefault();
  saveTask(localStorage, todayKey, elements.input.value);
  elements.input.value = "";
  elements.counter.textContent = "0 / 80";
  render();
});

elements.complete.addEventListener("click", () => {
  toggleTask(localStorage, todayKey);
  render();
});

elements.edit.addEventListener("click", () => {
  const task = readDays(localStorage)[todayKey];
  elements.saved.hidden = true;
  elements.form.hidden = false;
  elements.input.value = task?.text ?? "";
  elements.counter.textContent = `${elements.input.value.length} / 80`;
  elements.input.focus();
});

window.addEventListener("beforeinstallprompt", (event) => {
  event.preventDefault();
  installPrompt = event;
  elements.install.hidden = false;
});

elements.install.addEventListener("click", async () => {
  if (!installPrompt) return;
  await installPrompt.prompt();
  installPrompt = null;
  elements.install.hidden = true;
});

if ("serviceWorker" in navigator) {
  window.addEventListener("load", () => navigator.serviceWorker.register("./sw.js"));
}

render();

