import { addThought, createThought, formatParkedTime, removeThought, restoreThoughts } from "./model.js";

const STORAGE_KEY = "dmp-day003-thoughts";
const form = document.querySelector("#thoughtForm");
const input = document.querySelector("#thoughtInput");
const list = document.querySelector("#thoughtList");
const emptyState = document.querySelector("#emptyState");
const count = document.querySelector("#thoughtCount");
const toast = document.querySelector("#toast");
const installButton = document.querySelector("#installButton");

let thoughts = loadThoughts();
let toastTimer = 0;
let deferredInstallPrompt = null;

function loadThoughts() {
  try {
    return restoreThoughts(JSON.parse(localStorage.getItem(STORAGE_KEY)));
  } catch {
    return [];
  }
}

function saveThoughts() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(thoughts));
}

function showToast(message) {
  window.clearTimeout(toastTimer);
  toast.textContent = message;
  toast.classList.add("visible");
  toastTimer = window.setTimeout(() => toast.classList.remove("visible"), 2200);
}

function createThoughtElement(thought) {
  const item = document.createElement("li");
  item.className = "thought-item";
  item.dataset.id = thought.id;

  const copy = document.createElement("div");
  copy.className = "thought-copy";
  const text = document.createElement("p");
  text.textContent = thought.text;
  const time = document.createElement("time");
  time.dateTime = new Date(thought.createdAt).toISOString();
  time.textContent = formatParkedTime(thought.createdAt);
  copy.append(text, time);

  const button = document.createElement("button");
  button.className = "resolve-button";
  button.type = "button";
  button.textContent = "✓";
  button.setAttribute("aria-label", `${thought.text} 정리 완료`);
  button.addEventListener("click", () => resolveThought(thought.id));

  item.append(copy, button);
  return item;
}

function render() {
  list.replaceChildren(...thoughts.map(createThoughtElement));
  count.textContent = `${thoughts.length}대`;
  emptyState.hidden = thoughts.length > 0;
}

function resolveThought(id) {
  const target = thoughts.find((thought) => thought.id === id);
  thoughts = removeThought(thoughts, id);
  saveThoughts();
  render();
  showToast(target ? `“${target.text}” 정리 완료` : "정리했어요.");
}

form.addEventListener("submit", (event) => {
  event.preventDefault();
  try {
    thoughts = addThought(thoughts, createThought(input.value));
    saveThoughts();
    input.value = "";
    render();
    showToast("안전하게 주차했어요. 하던 일로 돌아가요.");
    input.focus();
  } catch (error) {
    showToast(error.message);
    input.focus();
  }
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

render();

if ("serviceWorker" in navigator) {
  window.addEventListener("load", () => navigator.serviceWorker.register("./sw.js"));
}
