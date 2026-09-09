# 🚀 TaskPulse: Daily Task Monitoring & Tracking System

An end-to-end, production-grade daily task monitoring and productivity tracking system with advanced **gamification** (points ledger, streaks, badges) and **analytics** (weekly/monthly charts, 60-day consistency heatmap).

---

## 🏗️ Architecture & Technology Stack

```mermaid
graph TD
    Client[React 18 + Vite + Tailwind CSS] -->|REST + JWT| API[Spring Boot 3.3.3 REST API]
    API --> Security[Spring Security + Stateless JWT]
    API --> TaskSvc[Task Service]
    API --> PointSvc[Point Scoring Engine]
    API --> StreakSvc[Timezone-Aware Streak Service]
    API --> BadgeSvc[Achievement & Badge Engine]
    API --> AnalyticsSvc[Analytics & Rollup Service]
    
    PointSvc --> Ledger[(Points Ledger - Append Only)]
    TaskSvc --> DB[(PostgreSQL / H2 Dev)]
    StreakSvc --> DB
    BadgeSvc --> DB
    AnalyticsSvc --> Metrics[(Daily Metrics Rollups)]

    Scheduler[Spring Schedulers] -.->|Midnight UTC/Local| StreakSvc
    Scheduler -.->|Overdue Check| TaskSvc
    Scheduler -.->|Nightly Aggregation| AnalyticsSvc
```

### Backend Stack
- **Java 17+ / 21+** (Clean standard Java models, 100% JDK 26 compatible without Lombok bytecode hooks)
- **Spring Boot 3.3.3** (Web, Data JPA, Security, Validation, Scheduling)
- **Spring Security 6** with stateless JWT authentication & BCrypt password hashing
- **Database**: H2 (dev mode with PostgreSQL compatibility mode) & PostgreSQL 16 (production mode)
- **OpenAPI 3 / Swagger** via Springdoc OpenAPI 2.5.0 (`http://localhost:8080/swagger-ui/index.html`)

### Frontend Stack
- **React 18** with **TypeScript** & **Vite**
- **Tailwind CSS** with responsive layout & glassmorphic accents
- **Recharts** (Bar charts, Area charts, Trend lines)
- **Lucide-React** icons
- **Axios** with centralized JWT interceptors & auto token handling

---

## 🎮 Gamification Engine & Rules

### 1. Point Calculation Formula
When a task is marked completed, the point calculation engine computes:

$$\text{Total Points} = \max\left(1, \lfloor (\text{Base} \times \text{Difficulty} + \text{OnTimeBonus} + \text{StreakBonus} + \text{EfficiencyBonus}) - \text{OverduePenalty} \rfloor\right)$$

- **Base Points**: 10 points
- **Difficulty Multiplier**:
  - Level 1: $1.0\times$
  - Level 2: $1.2\times$
  - Level 3: $1.5\times$
  - Level 4: $1.8\times$
  - Level 5: $2.0\times$
- **On-Time Completion Bonus**: $+5$ points if completed at or before due date/time.
- **Streak Bonus**: $+2$ points per day of active streak (capped at $+30$ points).
- **Efficiency Bonus**: $+3$ points if completed within $\le 75\%$ of estimated duration.
- **Overdue Penalty**: $-5$ points if completed after due date.

### 2. Timezone-Aware Streak Engine
- Tracks consecutive days of task completion.
- Boundary transitions occur at midnight in the user's configured timezone.
- **Freeze Credits**: Users earn 1 streak freeze credit for every 7 days of streak to preserve progress during missed days or emergencies.

### 3. Achievement Badges (Seed Data)
| Badge Code | Name | Target | Points Reward | Type |
|---|---|---|---|---|
| `STREAK_3` | Spark | 3-day streak | 25 pts | `STREAK` |
| `STREAK_7` | Unstoppable | 7-day streak | 100 pts | `STREAK` |
| `STREAK_30` | Centurion of Habit | 30-day streak | 500 pts | `STREAK` |
| `TASKS_10` | Getting Started | 10 tasks completed | 50 pts | `TASK_COUNT` |
| `TASKS_50` | Focused Crusher | 50 tasks completed | 200 pts | `TASK_COUNT` |
| `TASKS_100`| Century Club | 100 tasks completed | 500 pts | `TASK_COUNT` |
| `WORK_MASTERY_25`| Deep Worker | 25 Work tasks completed | 150 pts | `CATEGORY_MASTERY` |
| `FIT_MASTERY_25` | Iron Will | 25 Health tasks completed | 150 pts | `CATEGORY_MASTERY` |

---

## 📊 Analytics & Reporting

1. **Weekly Rollup**: Daily counts of completed vs. created tasks, completion rate %, points earned, and average points per day.
2. **Monthly Trend**: 30-day velocity trajectory, total focus hours, and points progression.
3. **60-Day Consistency Grid**: GitHub-style activity heatmap visualizing completion volume.

---

## 🚦 Getting Started

### Option A: Local Dev Mode (No PostgreSQL required)
Uses embedded H2 in PostgreSQL mode, automatically seeding demo categories and badges.

1. **Start Backend**:
   ```bash
   cd backend
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```
   *Backend starts at `http://localhost:8080`*
   *Swagger UI available at `http://localhost:8080/swagger-ui/index.html`*

2. **Start Frontend**:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   *Frontend starts at `http://localhost:3000` or `http://localhost:5173`*

3. **Log In**:
   - Click **"Use Demo Account"** on the login page, or register a new user.
   - Demo User: `demo@tasktracker.local` / `Password123!`

### Option B: Docker Compose (PostgreSQL + Backend + Frontend)
```bash
docker-compose up --build -d
```
- Frontend UI: `http://localhost:3000`
- Backend REST API: `http://localhost:8080`
- PostgreSQL Database: `localhost:5432`

---

## 🔌 Core REST API Endpoints

### Authentication (`/api/auth`)
- `POST /register`: Create a new user with timezone
- `POST /login`: Authenticate and receive JWT access token
- `GET /me`: Get authenticated user profile

### Tasks (`/api/tasks`)
- `GET /`: Paginated list of tasks with filters (`status`, `categoryId`, `priority`, `search`)
- `POST /`: Create task (title, description, recurrence, priority, due date, duration, difficulty)
- `GET /{id}`: Retrieve task details
- `PUT /{id}`: Update task fields
- `DELETE /{id}`: Soft/Hard delete task
- `POST /{id}/check-in`: Log focused duration (minutes)
- `POST /{id}/complete`: Complete task, trigger scoring formula, update streak, unlock badges

### Categories (`/api/categories`)
- `GET /`: List all categories (system & user-defined)
- `POST /`: Create custom category with color & icon

### Gamification (`/api/gamification`)
- `GET /summary`: Current points balance, lifetime points, level, streak, freeze credits
- `GET /ledger`: Immutable append-only audit trail of all point adjustments
- `GET /badges`: User's badge list with unlock status and target progress
- `GET /leaderboard`: Community ranking by lifetime points

### Analytics (`/api/analytics`)
- `GET /weekly`: 7-day daily metrics, completion rate, avg points/day
- `GET /monthly`: 30-day velocity, focus minutes, monthly completion rate
- `GET /heatmap?days=60`: Day-by-day contribution intensity
