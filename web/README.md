# Kipita location finder

The browser version finds your current location and searches Google Maps. It works on desktop and mobile browsers. The Android app is in the `Kipita-main/` directory.

Serve this folder over HTTPS for location access. For local development, use a loopback address.

```sh
python3 -m http.server 8000 --bind 127.0.0.1 --directory web
```

Open `http://127.0.0.1:8000`. Select **Use my location** and allow the browser permission, or enter a place or address. The map and the **Open Google Maps** link update for either action. The browser sends a location to Google Maps only after you use the location control and grant access.
