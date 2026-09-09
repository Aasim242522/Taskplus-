export type Priority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type TaskStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED' | 'OVERDUE';
export type RecurrenceType = 'NONE' | 'DAILY' | 'WEEKLY' | 'MONTHLY';
export type BadgeType = 'STREAK' | 'TASK_COUNT' | 'CATEGORY_MASTERY' | 'SPEED' | 'SPECIAL';

export interface User {
  id: string;
  email: string;
  fullName: string;
  timezone: string;
  role: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  user: User;
}

export interface Category {
  id: string;
  name: string;
  colorHex: string;
  icon: string;
  isSystem: boolean;
  createdAt: string;
}

export interface Task {
  id: string;
  title: string;
  description?: string;
  category?: Category;
  priority: Priority;
  status: TaskStatus;
  recurrenceType: RecurrenceType;
  dueDateTime?: string;
  completedAt?: string;
  estimatedDurationMinutes: number;
  actualDurationMinutes: number;
  difficultyLevel: number;
  tags?: string[];
  version: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateTaskInput {
  title: string;
  description?: string;
  categoryId?: string;
  priority?: Priority;
  recurrenceType?: RecurrenceType;
  dueDateTime?: string;
  estimatedDurationMinutes?: number;
  difficultyLevel?: number;
  tags?: string[];
}

export interface TaskCompletionBreakdown {
  basePoints: number;
  difficultyMultiplier: number;
  onTimeBonus: number;
  streakBonus: number;
  durationEfficiencyBonus: number;
  overduePenalty: number;
}

export interface TaskCompletionResult {
  taskId: string;
  status: string;
  completedAt: string;
  pointsAwarded: number;
  breakdown: TaskCompletionBreakdown;
  newTotalPoints: number;
  currentStreak: number;
  badgesUnlocked: Badge[];
}

export interface PointsSummary {
  currentBalance: number;
  lifetimePoints: number;
  pointsEarnedToday: number;
  currentStreak: number;
  longestStreak: number;
  lastActivityDate?: string;
  freezeCredits: number;
  userLevel: number;
  pointsToNextLevel: number;
}

export interface PointsLedgerItem {
  id: string;
  pointsDelta: number;
  balanceAfter: number;
  transactionType: string;
  referenceType: string;
  referenceId?: string;
  description: string;
  createdAt: string;
}

export interface Badge {
  id: string;
  code: string;
  name: string;
  description: string;
  iconUrl?: string;
  badgeType: BadgeType;
  pointsReward: number;
  unlocked: boolean;
  unlockedAt?: string;
  progress: number;
  target: number;
}

export interface LeaderboardEntry {
  rank: number;
  userId: string;
  fullName: string;
  totalPoints: number;
  streak: number;
}

export interface DailyMetric {
  date: string;
  dayOfWeek: string;
  tasksCreated: number;
  tasksCompleted: number;
  tasksOverdue: number;
  pointsEarned: number;
  totalDurationMinutes: number;
}

export interface WeeklyAnalytics {
  dailyMetrics: DailyMetric[];
  totalCreated: number;
  totalCompleted: number;
  totalOverdue: number;
  completionRate: number;
  totalPointsEarned: number;
  avgPointsPerDay: number;
}

export interface DailyTrendPoint {
  date: string;
  completedCount: number;
  pointsEarned: number;
}

export interface MonthlyAnalytics {
  year: number;
  month: number;
  totalCreated: number;
  totalCompleted: number;
  totalOverdue: number;
  completionRate: number;
  totalPointsEarned: number;
  totalFocusMinutes: number;
  trendPoints: DailyTrendPoint[];
}

export interface HeatmapDay {
  date: string;
  count: number;
  level: number;
}

export interface NotificationItem {
  id: string;
  title: string;
  message: string;
  notificationType: string;
  status: 'UNREAD' | 'READ';
  readAt?: string;
  createdAt: string;
}
