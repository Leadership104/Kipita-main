import { randomBytes } from "node:crypto";

const sessions = new Map();
const sessionLifetime = 8 * 60 * 60;
const cookieName = "kipita_session";

function getSessionId(request) {
  return request.headers.cookie?.match(/(?:^|;\s*)kipita_session=([a-f0-9]{64})(?:;|$)/)?.[1];
}

function cookie(value, age) {
  const secure = process.env.KIPITA_INSECURE_LOCAL === "1" ? "" : "; Secure";
  return cookieName + "=" + value + "; Path=/; HttpOnly; SameSite=Strict; Max-Age=" + age + secure;
}

async function readCredentials(request) {
  const chunks = [];
  let size = 0;
  for await (const chunk of request) {
    size += chunk.length;
    if (size > 4096) return null;
    chunks.push(chunk);
  }
  try {
    const { email, password } = JSON.parse(Buffer.concat(chunks).toString("utf8"));
    if (typeof email !== "string" || typeof password !== "string" ||
        !email.trim() || email.length > 254 || !password || password.length > 512) return null;
    return { username: email.trim(), password, firebasetoken: "" };
  } catch {
    return null;
  }
}

export async function account(request, response, json) {
  const id = getSessionId(request);
  const session = sessions.get(id);
  if (request.method === "GET") {
    return json(response, 200, { profile: session?.expiresAt > Date.now() ? session.profile : null });
  }

  if (!["POST", "DELETE"].includes(request.method)) {
    return json(response, 405, { error: "Method not allowed." });
  }
  if (request.headers["x-kipita-request"] !== "account" ||
      request.headers["sec-fetch-site"] === "cross-site") {
    return json(response, 403, { error: "Use the Kipita account screen." });
  }
  if (request.method === "DELETE") {
    sessions.delete(id);
    response.setHeader("Set-Cookie", cookie("", 0));
    return json(response, 200, { profile: null });
  }

  const credentials = await readCredentials(request);
  if (!credentials) return json(response, 400, { error: "Enter your email and password." });

  const upstream = await fetch("https://api.dwaat.com/login", {
    method: "POST",
    headers: { "Content-Type": "application/json", Accept: "application/json" },
    body: JSON.stringify(credentials),
    redirect: "error",
    signal: AbortSignal.timeout(10000)
  });
  if (!upstream.ok) throw new Error("Account backend unavailable");
  const { status, data } = await upstream.json();
  if (status === "fail") {
    return json(response, 401, { error: "Check your email and password and try again." });
  }
  if (status !== "success" || !data?.userID) throw new Error("Invalid login response");

  const now = Date.now();
  for (const [key, value] of sessions) {
    if (value.expiresAt <= now) sessions.delete(key);
  }
  if (sessions.size >= 10000) {
    return json(response, 503, { error: "Too many active sessions. Try again later." });
  }
  sessions.delete(id);
  const sessionId = randomBytes(32).toString("hex");
  const profile = { firstName: String(data.firstName || ""), lastName: String(data.lastName || "") };
  sessions.set(sessionId, { profile, expiresAt: now + sessionLifetime * 1000 });
  response.setHeader("Set-Cookie", cookie(sessionId, sessionLifetime));
  return json(response, 200, { profile });
}
