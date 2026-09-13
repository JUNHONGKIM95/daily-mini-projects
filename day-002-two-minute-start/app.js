import { FIVE_MINUTES_MS, TWO_MINUTES_MS, createSession, formatRemaining, isValidSession, progressRatio, remainingMs } from "./model.js";

const SESSION_KEY = "dmp-day002-session";
const COUNT_KEY = "dmp-day002-count";
const CIRCUMFERENCE = 2 * Math.PI * 54;

const views = {
  idle: document.querySelector("#introView"),
  running: document.querySelector("#timerView"),
  done: document.querySelector("#doneView"),
};
const startForm = document.querySelector("#startForm");
const taskInput = document.querySelector("#taskInput");
const currentTask = document.querySelector("#currentTask");
const doneTask = document.querySelector("#doneTask");
const timeDisplay = document.querySelector("#timeDisplay");
const progressRing = document.querySelector("#progressRing");
const resetButton = document.querySelector("#resetButton");
const moreButton = document.querySelector("#moreButton");
const finishButton = document.querySelector("#finishButton");
const startCount = document.querySelector("#startCount");
const toast = document.querySelector("#toast");
const installButton = document.querySelector("#installButton");

let session = readSession();
let timerId = 0;
let deferredInstallPrompt = null;

function readSession() {
  try {
    const value = JSON.parse(localStorage.getItem(SESSION_KEY));
    return isValidSession(value) ? value : null;
  } catch {
    return null;
  }
}

function saveSession(value) {
  session = value;
  localStorage.setItem(SESSION_KEY, JSON.stringify(value));
}

function clearSession() {
  session = null;
  localStorage.removeItem(SESSION_KEY);
  window.clearInterval(timerId);
}

function setView(name) {
  document.body.dataset.state = name;
  Object.entries(views).forEach(([key, element]) => { element.hidden = key !== name; });
}

function showToast(message) {
  toast.textContent = message;
  toast.classList.add("visible");
  window.setTimeout(() => toast.classList.remove("visible"), 2200);
}

function updateCount(increment = false) {
  let count = Number(localStorage.getItem(COUNT_KEY)) || 0;
  if (increment) {
    count += 1;
    localStorage.setItem(COUNT_KEY, String(count));
  }
  startCount.textContent = `시동 ${count}회`;
}

function completeTimer() {
  window.clearInterval(timerId);
  currentTask.textContent = session.task;
  doneTask.textContent = `“${session.task}”`;
  setView("done");
  if (navigator.vibrate) navigator.vibrate([120, 70, 180]);
}

function renderTimer() {
  if (!session) return;
  const remaining = remainingMs(session.deadline);
  timeDisplay.textContent = formatRemaining(remaining);
  progressRing.style.strokeDashoffset = String(CIRCUMFERENCE * (1 - progressRatio(remaining, session.duration)));
  document.title = `${formatRemaining(remaining)} · ${session.task}`;
  if (remaining === 0) completeTimer();
}

function runTimer() {
  setView("running");
  currentTask.textContent = session.task;
  renderTimer();
  timerId = window.setInterval(renderTimer, 250);
}

function start(task, duration) {
  saveSession(createSession(task, duration));
  updateCount(true);
  runTimer();
}

startForm.addEventListener("submit", (event) => {
  event.preventDefault();
  try {
    start(taskInput.value, TWO_MINUTES_MS);
  } catch (error) {
    showToast(error.message);
    taskInput.focus();
  }
});

resetButton.addEventListener("click", () => {
  clearSession();
  document.title = "2분 시동";
  setView("idle");
  taskInput.focus();
});

moreButton.addEventListener("click", () => {
  const task = session.task;
  saveSession(createSession(task, FIVE_MINUTES_MS));
  runTimer();
});

finishButton.addEventListener("click", () => {
  clearSession();
  document.title = "2분 시동";
  taskInput.value = "";
  setView("idle");
  showToast("오늘의 시동, 충분해요.");
});

window.addEventListener("beforeinstallprompt", (event) => {
  event.preventDefault();
  deferredInstallPrompt = event;
  installButton.hidden = false;
});

installButton.addEventListener("click", async () => {
  if (!deferredInstallPrompt) return;
  deferredInstallPrompt.prompt();
  await deferredInstallPrompt.userChoice;
  deferredInstallPrompt = null;
  installButton.hidden = true;
});

window.addEventListener("appinstalled", () => { installButton.hidden = true; });

updateCount();
if (session) {
  if (remainingMs(session.deadline) === 0) completeTimer();
  else runTimer();
} else {
  setView("idle");
}

if ("serviceWorker" in navigator) {
  window.addEventListener("load", () => navigator.serviceWorker.register("./sw.js"));
}
