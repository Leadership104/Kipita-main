# Kipita web port

This directory contains the browser port of the Android app in `Kipita-main/`. The current implementation covers the Android guest shell and catalog navigation. Its same-origin server fetches the catalogs from Kipita's existing API.

## Run locally

Use Node.js 22 or newer. No dependencies need to be installed.

```sh
node web/server.mjs
```

Open `http://127.0.0.1:8000`. When serving the app outside localhost, put the Node server behind HTTPS so browsers can request device location. **Change Location** opens the Google Maps search and location controls. Selecting **Use my location** updates the map and sends the selected coordinates to Kipita when loading a location-based catalog.

## Android parity status

| Android flow | Web status |
| --- | --- |
| Guest home and Places, Advisory, Travel, and Perks navigation | Implemented using the Android navigation order and labels. Device-level visual comparison is pending. |
| Places categories, nearby results, filters, and saved sections | Loads Android category configuration. Categories open Google Maps searches. Native results, details, filters, and saved preferences are pending. |
| Advisory, restrictions, and weather | Advisory uses the live catalog endpoint. The endpoint returned HTTP 500 for a sample guest request. Restrictions, detail screens, and weather are pending. |
| Travel catalog and affiliate links | Live catalog and links load. Travel tools, settings, and offline maps are pending. |
| Perks | Live names, descriptions, and links load. Android styling and detail interactions are pending. |
| Current location and search | Browser location and Google Maps search are implemented. Android's Google Places autosuggest and browser/device validation are pending. |
| Accounts, groups, invitations, member location sharing, messages, and notifications | Pending web authentication and authorized end-to-end verification against the Kipita backend. |
| Gallery, image capture and upload, media editing, and contact selection | Pending browser flows and integration verification. |

The production API does not supply browser CORS headers for the sampled login preflight. The web server exposes only the four catalog reads. Account and group operations require a web session backed by the existing authentication system. Native permissions and capabilities such as push, contacts, and offline maps require browser-specific implementations and testing on supported devices.
