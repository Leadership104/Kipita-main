# Kipita web port

This directory contains the browser port of the Android app in `Kipita-main/`. The current implementation covers the Android guest shell, catalog navigation, and email/password sign-in. Its same-origin server fetches the catalogs from Kipita's existing API.

## Run locally

Use Node.js 22 or newer. No dependencies need to be installed.

```sh
node web/server.mjs
```

Open `http://127.0.0.1:8000`. When serving the app outside localhost, put the Node server behind HTTPS so browsers can request device location. For local testing on a browser that requires HTTPS for secure cookies, run the server with `KIPITA_INSECURE_LOCAL=1` on loopback only. The account button opens email/password sign-in. Web sessions expire after eight hours and are cleared on restart. **Change Location** opens the Google Maps search and location controls. Selecting **Use my location** updates the map and sends the selected coordinates to Kipita when loading a location-based catalog.

## Android parity status

| Android flow | Web status |
| --- | --- |
| Guest home and Places, Advisory, Travel, and Perks navigation | Implemented using the Android navigation order and labels. Device-level visual comparison is pending. |
| Places categories, nearby results, filters, and saved sections | Loads Android category configuration. Categories open Google Maps searches. Native results, details, filters, and saved preferences are pending. |
| Advisory, restrictions, and weather | Advisory uses the live catalog endpoint. The endpoint returned HTTP 500 for a sample guest request. Restrictions, detail screens, and weather are pending. |
| Travel catalog and affiliate links | Live catalog and links load. Travel tools, settings, and offline maps are pending. |
| Perks | Live names, descriptions, and links load. Android styling and detail interactions are pending. |
| Current location and search | Browser location and Google Maps search are implemented. Android's Google Places autosuggest and browser/device validation are pending. |
| Accounts | Email/password sign-in and sign-out are implemented. A fixture backend verified the successful session flow. A live request verified rejection of invalid credentials. Real-account validation, registration, password reset, and social sign-in are pending. |
| Groups, invitations, member location sharing, messages, and notifications | Pending a backend authorization contract and authorized end-to-end verification. The native calls send a user ID header. |
| Gallery, image capture and upload, media editing, and contact selection | Pending browser flows and integration verification. |

The production API does not supply browser CORS headers for the sampled login preflight. The web server exposes four catalog reads and an account session backed by the existing login endpoint. The browser receives a session cookie and the display name. Group and gallery endpoints need their server-side authorization verified before the web server exposes them. Native permissions and capabilities such as push, contacts, and offline maps require browser-specific implementations and testing on supported devices.
