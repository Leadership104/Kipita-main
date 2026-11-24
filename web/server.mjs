import { createServer } from "node:http";
import { readFile } from "node:fs/promises";
import { account } from "./account.mjs";

const files = {
  "/": ["index.html", "text/html; charset=utf-8"],
  "/app.js": ["app.js", "text/javascript; charset=utf-8"],
  "/styles.css": ["styles.css", "text/css; charset=utf-8"]
};

const sections = {
  places: "placesSections",
  advisory: "advisorySections",
  travel: "plannerSections",
  perks: "affiliates"
};

function coordinate(raw, bound) {
  if (raw === null || raw.trim() === "") return null;
  const value = Number(raw);
  return Number.isFinite(value) && Math.abs(value) <= bound ? value : null;
}

async function catalogAt(section, params) {
  const category = params.get("category");
  if (section === "places" && !["restaurant", "entertainment", "shopping"].includes(category)) {
    return { status: 400, body: { error: "Select a valid category." } };
  }

  const headers = { "Content-Type": "application/json", Accept: "application/json" };
  const latitude = coordinate(params.get("lat"), 90);
  const longitude = coordinate(params.get("lng"), 180);
  if (latitude !== null && longitude !== null && section !== "perks") {
    headers.lat = String(latitude);
    headers.lng = String(longitude);
  }

  const payload = { action: section === "perks" ? "GetAllList" : "GetMyList" };
  if (section === "places") payload.parentType = category;

  const upstream = await fetch("https://api.dwaat.com/" + sections[section], {
    method: "POST",
    body: JSON.stringify(payload),
    headers,
    signal: AbortSignal.timeout(10000),
    redirect: "error"
  });
  if (!upstream.ok) throw new Error("Kipita API returned " + upstream.status);

  const response = await upstream.json();
  const items = section === "perks" ? response.data : response.data?.section;
  if (!Array.isArray(items)) throw new Error("Kipita API returned no catalog");
  return { status: 200, body: { items } };
}

function json(response, status, body) {
  response.writeHead(status, {
    "Content-Type": "application/json; charset=utf-8",
    "Cache-Control": "no-store",
    "X-Content-Type-Options": "nosniff"
  });
  response.end(JSON.stringify(body));
}

const server = createServer(async (request, response) => {
  try {
    const url = new URL(request.url, "http://localhost");
    if (url.pathname === "/api/account") return await account(request, response, json);
    if (request.method !== "GET") return json(response, 405, { error: "Method not allowed." });

    const file = Object.hasOwn(files, url.pathname) && files[url.pathname];
    if (file) {
      const [name, type] = file;
      const content = await readFile(new URL(name, import.meta.url));
      response.writeHead(200, { "Content-Type": type, "X-Content-Type-Options": "nosniff" });
      return response.end(content);
    }

    const match = /^\/api\/catalog\/(places|advisory|travel|perks)$/.exec(url.pathname);
    if (!match) return json(response, 404, { error: "Page not found." });
    const result = await catalogAt(match[1], url.searchParams);
    json(response, result.status, result.body);
  } catch {
    json(response, 502, { error: "Kipita could not complete the request. Please try again." });
  }
});

const port = Number(process.env.PORT || "8000");
server.listen(port, process.env.HOST || "127.0.0.1", () => {
  console.log("Kipita is listening on port " + port);
});
