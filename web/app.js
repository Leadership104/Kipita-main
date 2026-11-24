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
