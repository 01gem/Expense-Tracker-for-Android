<div style="text-align: center;">

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:FFB703,100:121212&height=200&section=header&text=Expense%20Tracker&fontSize=48&fontColor=FFFFFF&animation=fadeIn&fontAlignY=38&desc=Track%20every%20peso%2C%20one%20tap%20at%20a%20time&descAlignY=58&descSize=18" width="100%" alt="Expense Tracker Header"/>

<img src="https://readme-typing-svg.demolab.com/?font=Fira+Code&size=22&pause=1000&color=FFB703&center=true&vCenter=true&width=460&lines=%F0%9F%92%B8+Log+it.;%F0%9F%93%8A+See+it.;%F0%9F%8F%86+Rank+it.;%F0%9F%93%A5+Export+it." alt="Typing SVG animation" />

<br/>
<br/>

<img src="https://img.shields.io/badge/Kotlin-2.2.10-FFB703?style=for-the-badge&logo=kotlin&logoColor=121212" alt="Kotlin Badge"/>
<img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-FFB703?style=for-the-badge&logo=jetpackcompose&logoColor=121212" alt="Jetpack Compose Badge"/>
<img src="https://img.shields.io/badge/Room-SQLite-FFB703?style=for-the-badge&logo=sqlite&logoColor=121212" alt="Room SQLite Badge"/>
<img src="https://img.shields.io/badge/Min%20SDK-29-FFB703?style=for-the-badge&logo=android&logoColor=121212" alt="Min SDK Badge"/>

**A native Android app for logging daily expenses, watching the year add up, and exporting everything to Excel — no account, no cloud, no ads.**

</div>

---

## ✨ Features

| | |
|---|---|
| 🏠 **Dashboard** | Big animated (count-up, tabular-figure) yearly total, a glowing monthly line chart, and a 4×3 month grid — three ways to see and jump into your year. |
| ➕ **Add Expense** | Amount, a free-text category field (type anything — "Gym", "Software", whatever fits your life), an optional note, and a native date picker. |
| 🎨 **Auto-colored categories** | No fixed category list — each typed category gets its own consistent color, generated algorithmically, carried through the list, the breakdown bar, and the charts. |
| 📅 **Calendar** | Pick any date on a Material 3 calendar (always opens on today, Asia/Manila-aware) and see exactly what you spent that day. |
| 🏆 **Leaderboard** | Triple views: Rank by **Category**, **Year**, or **Month**. Filter category and month stats by specific years or view all-time stats — top 3 are highlighted. |
| 📁 **Month Detail** | Every expense for a given month, one tap away from the dashboard or the grid. |
| 🗑️ **Swipe to delete** | Swipe an entry left and confirm before it's gone — no accidental deletes. |
| 📥 **Excel Export** | Its own tab: one tap writes a real `.xlsx` (via `fastexcel`) straight to `Downloads/ExpenseTracker/` — an *Expenses* sheet and a *Monthly Summary* sheet, ready to open in Excel or Sheets. |
| 🌓 **Fintech-dark theme** | Deep charcoal background, amber accent, a floating pill-shaped top bar, category colors carried through charts, list rows, and the breakdown bar. |
| 💾 **100% local** | Room (SQLite) database — everything stays on your device until *you* export it. |

<div style="text-align: center;">
<img src="https://media.giphy.com/media/xT9IgG50Fb7Mi0prBC/giphy.gif" width="220" alt="money counting animation"/>
</div>

---

## 🛠 Tech Stack

- **Language:** Kotlin (2.2.10)
- **UI:** Jetpack Compose + Material 3
- **Database:** Room (SQLite), KSP for codegen
- **Excel export:** [`org.dhatim:fastexcel`](https://github.com/dhatim/fastexcel) — lightweight, AWT-free, Android-friendly
- **Architecture:** MVVM — `ExpenseViewModel` exposes `StateFlow`s that the Compose screens collect
- **Navigation:** No nav library — a 5-tab `AppNavigationBar` (Home, Calendar, Add, Leaderboard, Export) + local `remember` state, crossfaded with `AnimatedContent`
- **Min SDK:** 29 · **Target SDK:** 37

---

## 📂 Project structure

```
app/src/main/java/com/gem/expensetracker/
├── MainActivity.kt              # Floating top bar + bottom nav + AnimatedContent screen switching
├── data/                        # Room entity, DAO, database, repository
│   ├── Expense.kt                    # category is a free-text String, not an enum
│   ├── ExpenseDao.kt                 # SQL queries (totals, monthly grouping, leaderboard, etc.)
│   ├── ExpenseDatabase.kt
│   └── ExpenseRepository.kt
├── viewmodel/
│   └── ExpenseViewModel.kt      # All app state lives here
├── export/
│   └── ExcelExporter.kt         # The .xlsx writer
└── ui/
    ├── screens/                 # DashboardScreen, AddExpenseScreen, CalendarScreen,
    │                            # LeaderboardScreen, ExportScreen, MonthDetailScreen
    ├── components/              # GlowingLineChart, MonthGrid, ExpenseListItem (swipe + confirm delete),
    │                            # CategoryBreakdownBar, AppNavigationBar, EmptyState
    └── theme/                   # Color.kt, Theme.kt, Type.kt (fintech-dark palette)
                                  # + CategoryColor.kt — hashes any typed category name to a stable color
```

---

## 🧑‍💻 How to edit it yourself

1. **Open it up.** Clone or unzip the project, then open the root folder in **Android Studio** (Hedgehog or newer). Let Gradle sync — it'll pull Room, Compose, and `fastexcel` automatically.

2. **Find the right file for what you want to change:**
   - *Change category colors* → `ui/theme/CategoryColor.kt`. Categories are free text now, so colors are generated from the name itself (a hash → hue), not picked from a fixed list.
   - *Change what a screen looks like* → its file in `ui/screens/`. Each screen is a single `@Composable fun`.
   - *Change app-wide colors/fonts* → `ui/theme/Color.kt` and `Type.kt`. `FintechAccent` is the one color used almost everywhere for emphasis.
   - *Change how totals or the leaderboard are calculated* → `data/ExpenseDao.kt` (the SQL) and `viewmodel/ExpenseViewModel.kt` (how it's exposed as state).
   - *Change what gets exported, or the sheet layout* → `export/ExcelExporter.kt`.
   - *Add a new tab* → add an entry to the `NavigationTab` enum in `ui/components/AppNavigationBar.kt`, then wire it into the `when` block and the `topBarTitle` map in `MainActivity.kt`.

3. **Rebuild and run.** `Shift+F10` in Android Studio, or from a terminal:
   ```bash
   ./gradlew assembleDebug
   ```
   Install on a connected device/emulator with:
   ```bash
   ./gradlew installDebug
   ```

4. **If Gradle complains about a version** (Room, KSP, lifecycle, etc.), check `gradle/libs.versions.toml` first — nearly every dependency version lives there in one place.

5. **No navigation library, no DI framework, no ViewModel injection library** — this project deliberately stays simple. If you add a new screen, follow the existing pattern: a `Composable` that takes `viewModel: ExpenseViewModel` and a couple of lambdas for navigation, nothing fancier.

<div style="text-align: center;">

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:121212,100:FFB703&height=120&section=footer" alt="Footer waving animation"/>

Made with 🐛☕ and a stubborn refusal to use a spreadsheet by hand.

</div>
