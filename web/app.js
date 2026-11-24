"use strict";

const form = document.querySelector("#search-form");
const place = document.querySelector("#place");
const locate = document.querySelector("#locate");
const locateLabel = document.querySelector("#locate-label");
const status = document.querySelector("#status");
const map = document.querySelector("#map");
const mapLabel = document.querySelector("#map-label");
const mapLink = document.querySelector("#map-link");

const locationErrors = {
  1: "Location access is blocked. Allow it in this browser’s site settings and try again.",
  2: "Your location is unavailable. Turn on location services and try again.",
  3: "Finding your location timed out. Try again when you have a signal."
};

let requestNumber = 0;

function showMap(query, label, zoom) {
  const embed = new URL("https://www.google.com/maps");
  embed.search = new URLSearchParams({ q: query, z: zoom, output: "embed" });

  const link = new URL("https://www.google.com/maps/search/");
  link.search = new URLSearchParams({ api: "1", query });

  mapLabel.textContent = label;
  map.title = "Google Maps – " + label;
  mapLink.href = link.href;
  map.src = embed.href;
}

function setLocating(isLocating) {
  locate.disabled = isLocating;
  locateLabel.textContent = isLocating ? "Finding your location…" : "Use my location";
}

form.addEventListener("submit", (event) => {
  event.preventDefault();
  const query = place.value.trim();
  if (!query) {
    status.textContent = "Enter a place or address to search.";
    place.focus();
    return;
  }

  requestNumber++;
  setLocating(false);
  status.textContent = "";
  showMap(query, query, "13");
});

locate.addEventListener("click", () => {
  if (!window.isSecureContext) {
    status.textContent = "Open Kipita over HTTPS to use your location.";
    return;
  }
  if (!navigator.geolocation) {
    status.textContent = "This browser does not support location access. You can still search for a place.";
    return;
  }

  const request = ++requestNumber;
  setLocating(true);
  status.textContent = "Allow access if your browser asks for your location.";

  navigator.geolocation.getCurrentPosition(
    ({ coords }) => {
      if (request !== requestNumber) return;
      setLocating(false);
      const latitude = coords.latitude.toFixed(5);
      const longitude = coords.longitude.toFixed(5);
      showMap(latitude + "," + longitude, "Your location", coords.accuracy > 1000 ? "12" : "16");
      activeLocation.textContent = latitude + ", " + longitude;
      position = { lat: latitude, lng: longitude };
      loadCatalog();
      status.textContent = latitude + ", " + longitude + " · Within about " +
        Math.max(1, Math.round(coords.accuracy)).toLocaleString() + " m";
    },
    ({ code }) => {
      if (request !== requestNumber) return;
      setLocating(false);
      status.textContent = locationErrors[code] || "Could not find your location. Try again or search for a place.";
    },
    { enableHighAccuracy: true, maximumAge: 30000, timeout: 15000 }
  );
});

const activeLocation = document.querySelector("#active-location");
const categoryPicker = document.querySelector("#categories");
const sectionTitle = document.querySelector("#section-title");
const catalogStatus = document.querySelector("#catalog-status");
const catalogList = document.querySelector("#catalog-list");
const retryCatalog = document.querySelector("#retry-catalog");
const sectionLinks = [...document.querySelectorAll("[data-section]")];
const categoryButtons = [...document.querySelectorAll("[data-category]")];
const titles = { places: "Places", advisory: "Advisory", travel: "Travel", perks: "KIPITA Perks" };
let section = "places";
let category = "restaurant";
let position = null;
let catalogRequest;

function safeLink(value) {
  try {
    const link = new URL(value);
    return ["https:", "http:"].includes(link.protocol) && !link.username && !link.password
      ? link.href : null;
  } catch {
    return null;
  }
}

function renderItem(item, currentSection) {
  const title = item.title || item.name;
  if (!title) return;

  const card = document.createElement("article");
  const heading = document.createElement("h2");
  const url = safeLink(item.link || item.url);
  heading.textContent = title;
  if (url || currentSection === "places") {
    const link = document.createElement("a");
    const query = title + (position ? " near " + position.lat + "," + position.lng : "");
    link.textContent = title;
    link.href = url || "https://www.google.com/maps/search/?" +
      new URLSearchParams({ api: "1", query });
    link.target = "_blank";
    link.rel = "noopener noreferrer";
    heading.replaceChildren(link);
  }
  card.append(heading);
  if (item.description) {
    const description = document.createElement("p");
    description.textContent = item.description;
    card.append(description);
  }
  catalogList.append(card);
}

async function loadCatalog() {
  catalogRequest?.abort();
  const request = new AbortController();
  catalogRequest = request;
  const currentSection = section;
  const params = new URLSearchParams(position || {});
  if (section === "places") params.set("category", category);
  catalogStatus.textContent = "Loading " + titles[section] + "…";
  catalogList.replaceChildren();
  retryCatalog.hidden = true;

  try {
    const response = await fetch("/api/catalog/" + section + "?" + params, { signal: request.signal });
    if (!response.ok) throw new Error("Catalog unavailable");
    const { items } = await response.json();
    if (request.signal.aborted) return;
    for (const item of items) renderItem(item, currentSection);
    catalogStatus.textContent = catalogList.childElementCount ? "" : "No data found.";
  } catch {
    if (request.signal.aborted) return;
    catalogStatus.textContent = "Kipita could not load " + titles[currentSection] + ".";
    retryCatalog.hidden = false;
  }
}

function selectSection() {
  const route = window.location.hash.slice(1);
  if (route && !Object.hasOwn(titles, route)) return;
  section = route || "places";
  sectionTitle.textContent = titles[section];
  categoryPicker.hidden = section !== "places";
  for (const link of sectionLinks) {
    if (link.dataset.section === section) link.setAttribute("aria-current", "page");
    else link.removeAttribute("aria-current");
  }
  loadCatalog();
}

for (const button of categoryButtons) {
  button.addEventListener("click", () => {
    category = button.dataset.category;
    for (const option of categoryButtons) option.setAttribute("aria-pressed", String(option === button));
    loadCatalog();
  });
}
retryCatalog.addEventListener("click", loadCatalog);
window.addEventListener("hashchange", selectSection);
if (window.location.hash === "#location") loadCatalog();
else selectSection();

const accountDialog = document.querySelector("#account-dialog");
const profileButton = document.querySelector("#profile-button");
const loginForm = document.querySelector("#login-form");
const signOut = document.querySelector("#sign-out");
const accountStatus = document.querySelector("#account-status");
let accountPending = false;

function renderProfile(profile) {
  const name = [profile?.firstName, profile?.lastName].filter(Boolean).join(" ") || "Guest";
  document.querySelector("#greeting").textContent = "Hi, " + name;
  document.querySelector("#avatar").textContent = Array.from(name)[0].toUpperCase();
  document.querySelector("#account-title").textContent = profile ? "Account" : "Login";
  loginForm.hidden = Boolean(profile);
  signOut.hidden = !profile;
}

async function updateAccount(method, credentials) {
  if (accountPending) return;
  accountPending = true;
  accountStatus.textContent = method === "GET" ? "" : "Please wait…";
  try {
    const response = await fetch("/api/account", {
      method,
      headers: { "Content-Type": "application/json", "X-Kipita-Request": "account" },
      body: credentials ? JSON.stringify(credentials) : undefined
    });
    const result = await response.json();
    if (!response.ok) throw new Error(result.error);
    renderProfile(result.profile);
    accountStatus.textContent = "";
    if (method !== "GET" && accountDialog.open) accountDialog.close();
  } catch (error) {
    accountStatus.textContent = error.message || "Kipita account service is unavailable.";
  } finally {
    accountPending = false;
  }
}

profileButton.addEventListener("click", () => accountDialog.showModal());
loginForm.addEventListener("submit", (event) => {
  event.preventDefault();
  const credentials = Object.fromEntries(new FormData(loginForm));
  if (accountPending) return;
  loginForm.reset();
  updateAccount("POST", credentials);
});
signOut.addEventListener("click", () => updateAccount("DELETE"));
updateAccount("GET");
