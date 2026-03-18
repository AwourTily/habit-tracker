# HabitForge — Android Studio (Java)

A full-featured habit tracking app with **Login / Register** pages, SQLite persistence, streak tracking, and statistics.

---

## 🔑 Login Screen Features
- Full-screen background image (gradient forest theme)
- Dark overlay for text readability
- Animated entrance (logo slides down, form slides up)
- Email + password fields with validation
- Show/hide password toggle
- "Forgot password" placeholder
- Link to Register screen
- SHA-256 password hashing

## 📱 Screens
| Screen | Description |
|---|---|
| `LoginActivity` | Login with background image, animations |
| `RegisterActivity` | Sign up with background image |
| `MainActivity` | Daily habit checklist with greeting + progress |
| `AddEditHabitActivity` | Create/edit with color chips & emoji picker |
| `HabitDetailActivity` | Streak, history, toggle completion |
| `StatsActivity` | Overview stats + leaderboard |

## 🏗 Project Structure
```
app/src/main/
├── java/com/habitforge/
│   ├── activities/       LoginActivity, RegisterActivity, MainActivity,
│   │                     AddEditHabitActivity, HabitDetailActivity, StatsActivity
│   ├── adapters/         HabitAdapter, LogAdapter, HabitStatsAdapter
│   ├── database/         DatabaseHelper (SQLite - users + habits + logs)
│   ├── models/           User, Habit, HabitLog
│   └── utils/            SessionManager (SharedPreferences), PasswordUtils (SHA-256)
└── res/
    ├── layout/           6 activity layouts + 3 item layouts
    ├── drawable/         login_background, login_overlay, logo_bg, badge, btn
    ├── menu/             menu_main (Stats + Logout)
    └── values/           colors, strings, themes
```

## 🖼 Adding a Real Background Photo
Replace the background image in one of two ways:

**Option A** — Add a photo to drawable:
1. Place your image as `res/drawable/login_background.jpg` (or .png, .webp)
2. In `activity_login.xml`, the `<ImageView>` already references `@drawable/login_background`
3. Your photo replaces the gradient automatically

**Option B** — Use a URL (Glide):
1. Add Glide to `build.gradle`: `implementation 'com.github.bumptech.glide:glide:4.16.0'`
2. In `LoginActivity.onCreate()` add:
   ```java
   Glide.with(this).load("YOUR_IMAGE_URL").centerCrop().into(findViewById(R.id.iv_bg));
   ```

## 🚀 Setup
1. Open **Android Studio** → File → Open → select `HabitForge` folder
2. Wait for Gradle sync
3. Add `ic_launcher` icons (or use Image Asset Studio)
4. Run on device/emulator (API 21+)

## 📦 Dependencies
- Material Components 1.11.0
- AppCompat 1.6.1
- RecyclerView 1.3.2 / CardView 1.0.0
- CoordinatorLayout 1.2.0
