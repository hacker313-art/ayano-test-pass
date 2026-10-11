# 📱 AYANO TEST PASS — Complete Setup Guide
### Everything fixed. Just follow steps 1 to 3. Done.

---

## ✅ What was fixed in this zip

| Bug | What was wrong | Fixed |
|-----|----------------|-------|
| Login skipped | App was opening directly to Test List — no login shown | ✅ Fixed |
| Hardcoded user | A fake default user was bypassing Firebase auth | ✅ Fixed |
| Admin bug | Admin role was always reset to "student" after Firestore read | ✅ Fixed |
| Firebase crash | Old deprecated API was causing warnings/crashes on new SDK | ✅ Fixed |
| Build error | compileSdk used wrong syntax that failed on most Gradle versions | ✅ Fixed |

---

# PART 1 — Firebase Setup 🔥
> **What is Firebase?** Google's free server that stores your app's data and handles user login. Runs 24/7 automatically.

---

### Step 1 — Open Firebase
👉 Open Chrome on your phone and go to:
**https://console.firebase.google.com**

💡 *Hint: Sign in with your Gmail account*

---

### Step 2 — Create a project
- Tap **"Add project"**
- Name it: **ayano-app**
- Tap **Continue**
- Turn off Google Analytics (not needed)
- Tap **Create project**
- Wait 10 seconds → tap **Continue**

---

### Step 3 — Enable Firestore (your database)
- In the left menu tap **"Firestore Database"**
- Tap **"Create database"**
- Choose **"Start in test mode"** ← important!
- Tap **Next** → choose any location → tap **Enable**
- Wait 30 seconds ✅

💡 *Hint: "Test mode" means anyone can read/write for 30 days — perfect for testing. You can add security rules later.*

---

### Step 4 — Enable Phone Login
- In the left menu tap **"Authentication"**
- Tap **"Get started"**
- Tap **"Phone"**
- Toggle it **ON**
- Tap **Save**

---

### Step 5 — Register your Android app
- Tap the **gear icon** (top left) → **"Project settings"**
- Scroll down to **"Your apps"** section
- Tap the **Android icon** ( looks like a robot 🤖 )
- **Package name:** type exactly → `com.aistudio.ayano.testpass`
- **App nickname:** type → `Ayano Test Pass`
- Tap **"Register app"**

---

### Step 6 — Download google-services.json ⭐
- Tap **"Download google-services.json"**
- **Save this file** — you will need it in Part 2!
- Tap **Next** → **Next** → **Continue to console**

💡 *Hint: This file connects your app to Firebase. Without it the app will crash.*

---

# PART 2 — GitHub Setup 🐙
> **What is GitHub?** Free website that stores your code AND automatically builds your APK. No PC needed.

---

### Step 7 — Create GitHub account
👉 Go to: **https://github.com**
- Tap **"Sign up"**
- Enter email, password, username
- Verify your email

---

### Step 8 — Create a new repository
- Tap the **"+"** button (top right)
- Tap **"New repository"**
- **Repository name:** `ayano-test-pass`
- Keep it **Public**
- Tap **"Create repository"**

---

### Step 9 — Upload this entire zip
- Inside your new repo tap **"Add file"** → **"Upload files"**
- Upload this **ayano-test-pass-complete.zip** file
- Scroll down → tap **"Commit changes"**
- Wait for upload to finish ✅

💡 *Hint: GitHub will extract the zip automatically*

---

### Step 10 — Upload google-services.json ⭐
This is the most important step!

- In your repo, click on the **"app"** folder
- Tap **"Add file"** → **"Upload files"**
- Upload the **google-services.json** you downloaded in Step 6
- Tap **"Commit changes"**

💡 *Hint: Make sure it goes inside the `app/` folder, not the root folder*

---

### Step 11 — Add your API keys (Secrets)
- Go to your repo → **Settings** tab
- In left menu → **"Secrets and variables"** → **"Actions"**
- Tap **"New repository secret"** for each one below:

#### Secret 1 — Cloudinary (for image uploads)
First sign up free at **https://cloudinary.com**
After signup, your Dashboard shows all 3 values:

| Secret Name | Value |
|-------------|-------|
| `CLOUDINARY_CLOUD_NAME` | shown on your Cloudinary dashboard |
| `CLOUDINARY_API_KEY` | shown on your Cloudinary dashboard |
| `CLOUDINARY_API_SECRET` | shown on your Cloudinary dashboard |

#### Secret 2 — Gemini AI
Sign up free at **https://aistudio.google.com** → tap "Get API key"

| Secret Name | Value |
|-------------|-------|
| `GEMINI_API_KEY` | your key from AI Studio |

💡 *Hint: Secrets are private — GitHub hides them from everyone including you after saving*

---

# PART 3 — Build Your APK ⚙️

---

### Step 12 — Trigger the build
- Go to your GitHub repo
- Tap **"Actions"** tab
- You should see **"Build APK"** already running (triggered by your upload)
- If not: tap **"Build APK"** → **"Run workflow"** → **"Run workflow"**

---

### Step 13 — Wait 5 minutes ⏳
- Green tick ✅ = build successful
- Red cross ❌ = something went wrong (see troubleshooting below)

---

### Step 14 — Download your APK
- Tap the finished build (green tick)
- Scroll down to **"Artifacts"** section
- Tap **"ayano-test-pass-apk"**
- A zip file downloads to your phone
- Extract the zip → you get an **.apk file**

---

### Step 15 — Install the APK
- Tap the .apk file
- Your phone asks **"Allow install from unknown sources"**
- Go to Settings → allow it → come back and install
- Open **AYANO TEST PASS** ✅

---

# PART 4 — First Login & Admin Setup 👑

---

### Step 16 — First Login
- Open the app
- You will see the **Login screen** (it's fixed now!)
- Enter your **name** and **phone number**
- Tap **"Send Phone OTP"**
- Enter the SMS code
- You're in! 🎉

---

### Step 17 — Set yourself as Admin
- After logging in, go to **Firebase Console**
- Tap **Authentication** → **Users** tab
- Find your account → copy your **UID** (long code like `BO98jRolQ0e...`)
- Open this file in your GitHub repo:
  `app/src/main/java/com/example/model/User.kt`
- Tap the pencil ✏️ icon to edit
- Replace `BO98jRolQ0eueqnwZJ2D8PxSiae2` with **your UID**
- Tap **Commit changes**
- Go back to Actions → wait for new APK to build → reinstall

💡 *Hint: After setting your UID, you'll see an "Admin" button on the home screen*

---

# 🔧 Troubleshooting

### ❌ Build failed: "google-services.json not found"
→ Make sure you uploaded google-services.json to the **app/** folder (Step 10)

### ❌ Build failed: "compileSdk error"
→ Make sure you're using the fixed `app/build.gradle.kts` from this zip

### ❌ Phone OTP not working (real SMS)
→ You need to add your app's SHA-256 fingerprint to Firebase:
- Firebase Console → Project Settings → Your Android app → **"Add fingerprint"**
- The fingerprint appears in your GitHub Actions build log
- Search for "SHA" in the build log

### ❌ App crashes on launch
→ Check that your google-services.json package name matches `com.aistudio.ayano.testpass`

### ❌ Admin panel not showing
→ Make sure your UID in User.kt matches exactly what Firebase shows

---

# 🎉 Done! Your App is Live 24/7

| What | Where it runs |
|------|--------------|
| Database | Google Firebase servers (free, 24/7) |
| Images | Cloudinary servers (free, 24/7) |
| APK builds | GitHub Actions (free) |
| The app itself | Installed on your phone |

**No monthly cost. No server to manage. Everything automatic.**

---

*Rebuild the APK any time you change the code — just push to GitHub and Actions builds it automatically.*
