# Thryvii — Android (Jetpack Compose)

Generated from the Figma file's **Welcome**, **Email**, and **Sign In** frames
(nodes `1:921`, `1:931`, `1:955`).

## What's wired up

- **Welcome → Email**: tapping "CREATE ACCOUNT" navigates to the Email screen.
- **Welcome → Sign In**: tapping "LOGIN" navigates to the Sign In screen.

Navigation lives in `app/src/main/java/com/thryvii/app/navigation/ThryviiNavHost.kt`.

## Open it

Open the `ThryviiApp` folder directly in Android Studio (Hedgehog or newer). It's a
standard Gradle project — Android Studio will generate the Gradle wrapper and sync
automatically on first open. No manual setup required beyond that.

## Known gaps to fill in

1. **Character illustration & logo** — the Figma Dev Mode connection only exposes
   image assets as temporary `localhost` URLs on your machine, which this generation
   environment couldn't reach. Each screen has a placeholder `Box` marked
   `// TODO: swap for Image(...)`. Export the artwork from Figma (select the layer →
   `Shift+Ctrl/Cmd+E`), drop the PNGs into `app/src/main/res/drawable/`, and swap in
   the `Image(painterResource(...))` calls.
2. **"Jua" font** — the design uses Google's "Jua" font. This build falls back to the
   device's built-in `sans-serif-rounded` (visually close) since downloading fonts
   wasn't possible in this environment. See the comment at the top of
   `ui/theme/Type.kt` for the two ways to swap in the real font.
3. **Orientation** — the Figma frames are landscape (926×428), so `MainActivity` is
   locked to landscape via the manifest. Remove `android:screenOrientation="landscape"`
   if you want the app to rotate freely instead.
4. **"CONTINUE" (Email) and "LOG IN" (Sign In)** — not covered by the original
   request, so both are left as open callbacks (`onContinueClick`, `onLoginClick`) with
   `TODO` comments in `ThryviiNavHost.kt` for you to wire up next.
# https-github.com-fionaxian-ThryviiApp
# https-github.com-fionaxian-ThryviiApp
# https-github.com-fionaxian-ThryviiApp
