import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import {
  Flame,
  Trophy,
  CheckCircle2,
  Clock,
  Sparkles,
  Plus,
  ArrowUpRight,
  Shield,
  Layers,
} from 'lucide-react';
import { tasksApi } from '../api/tasks';
import { gamificationApi } from '../api/gamification';
import { analyticsApi } from '../api/analytics';
import {
  Category,
  DailyMetric,
  HeatmapDay,
  PointsSummary,
  Task,
  TaskCompletionResult,
} from '../types';
import { QuickAddTask } from '../components/tasks/QuickAddTask';
import { TaskCard } from '../components/tasks/TaskCard';
import { TaskModal } from '../components/tasks/TaskModal';
import { TaskCompletionModal } from '../components/tasks/TaskCompletionModal';
import { WeeklyChart } from '../components/analytics/WeeklyChart';
import { CalendarHeatmap } from '../components/analytics/CalendarHeatmap';

interface DashboardPageProps {
  onNavigateToTasks: () => void;
  onNavigateToAchievements: () => void;
  onNavigateToAnalytics: () => void;
}

export const DashboardPage: React.FC<DashboardPageProps> = ({
  onNavigateToTasks,
  onNavigateToAchievements,
  onNavigateToAnalytics,
}) => {
  const { user } = useAuth();
  const [tasks, setTasks] = useState<Task[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [summary, setSummary] = useState<PointsSummary | null>(null);
  const [weeklyMetrics, setWeeklyMetrics] = useState<DailyMetric[]>([]);
  const [heatmapData, setHeatmapData] = useState<HeatmapDay[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  // Modals state
  const [isTaskModalOpen, setIsTaskModalOpen] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | null>(null);
  const [completionResult, setCompletionResult] = useState<TaskCompletionResult | null>(null);

  const loadDashboardData = useCallback(async () => {
    try {
      setIsLoading(true);
      const [tasksRes, categoriesRes, summaryRes, weeklyRes, heatmapRes] = await Promise.all([
        tasksApi.getTasks({ status: 'PENDING', size: 10 }),
        tasksApi.getCategories(),
        gamificationApi.getSummary(),
        analyticsApi.getWeekly(),
        analyticsApi.getHeatmap(60),
      ]);

      setTasks(tasksRes.content || []);
      setCategories(categoriesRes || []);
      setSummary(summaryRes);
      setWeeklyMetrics(weeklyRes?.dailyMetrics || []);
      setHeatmapData(heatmapRes || []);
    } catch (err) {
      console.error('Failed to load dashboard data', err);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    loadDashboardData();
  }, [loadDashboardData]);

  const handleTaskComplete = async (taskId: string) => {
    try {
      const result = await tasksApi.completeTask(taskId);
      setCompletionResult(result);
      await loadDashboardData();
    } catch (err) {
      console.error('Failed to complete task', err);
    }
  };

  const handleCheckIn = async (taskId: string, mins: number) => {
    try {
      await tasksApi.checkIn(taskId, mins);
      await loadDashboardData();
    } catch (err) {
      console.error('Failed to check in', err);
    }
  };

  const handleDelete = async (taskId: string) => {
    try {
      await tasksApi.deleteTask(taskId);
      await loadDashboardData();
    } catch (err) {
      console.error('Failed to delete task', err);
    }
  };

  const handleEditTask = (task: Task) => {
    setEditingTask(task);
    setIsTaskModalOpen(true);
  };

  const handleOpenCreateModal = () => {
    setEditingTask(null);
    setIsTaskModalOpen(true);
  };

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Welcome Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-gradient-to-r from-primary-600 via-indigo-600 to-primary-700 rounded-2xl p-6 text-white shadow-lg shadow-primary-500/15">
        <div className="space-y-1">
          <div className="flex items-center gap-2">
            <h1 className="text-2xl font-black">
              Welcome back, {user?.fullName || 'Productivity Hero'}! 👋
            </h1>
          </div>
          <p className="text-white/80 text-sm">
            You're currently on a{' '}
            <strong className="text-amber-300 font-bold">{summary?.currentStreak || 0}-day streak</strong>. Keep the momentum going!
          </p>
        </div>

        <button
          onClick={handleOpenCreateModal}
          className="inline-flex items-center justify-center gap-2 px-5 py-2.5 bg-white text-primary-700 font-semibold text-sm rounded-xl hover:bg-white/90 transition-all shadow-md shrink-0"
        >
          <Plus className="w-4 h-4" />
          <span>Detailed Task</span>
        </button>
      </div>

      {/* Gamification Stats Overview */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Streak Widget */}
        <div className="bg-white dark:bg-gray-800 p-5 rounded-2xl border border-gray-100 dark:border-gray-700 shadow-sm relative overflow-hidden group">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-gray-500 dark:text-gray-400">
              Daily Streak
            </span>
            <div className="w-9 h-9 rounded-xl bg-amber-50 dark:bg-amber-950/40 flex items-center justify-center text-amber-500">
              <Flame className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-black text-gray-900 dark:text-white">
              {summary?.currentStreak || 0}
            </span>
            <span className="text-xs text-gray-500">days active</span>
          </div>
          <div className="mt-3 flex items-center justify-between text-xs text-gray-500 pt-3 border-t border-gray-100 dark:border-gray-700">
            <span>Best: {summary?.longestStreak || 0} days</span>
            <span className="flex items-center gap-1 text-blue-600 dark:text-blue-400">
              <Shield className="w-3 h-3" /> {summary?.freezeCredits || 0} freezes
            </span>
          </div>
        </div>

        {/* Level & Points Widget */}
        <div className="bg-white dark:bg-gray-800 p-5 rounded-2xl border border-gray-100 dark:border-gray-700 shadow-sm relative overflow-hidden">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-gray-500 dark:text-gray-400">
              Level & Balance
            </span>
            <div className="w-9 h-9 rounded-xl bg-purple-50 dark:bg-purple-950/40 flex items-center justify-center text-purple-500">
              <Trophy className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-black text-gray-900 dark:text-white">
              Level {summary?.userLevel || 1}
            </span>
            <span className="text-xs text-purple-600 font-semibold">
              {summary?.currentBalance || 0} pts
            </span>
          </div>
          <div className="mt-3 space-y-1 pt-3 border-t border-gray-100 dark:border-gray-700">
            <div className="flex justify-between text-[11px] text-gray-500">
              <span>Next Level</span>
              <span>{summary?.pointsToNextLevel || 0} pts needed</span>
            </div>
            <div className="w-full h-1.5 bg-gray-100 dark:bg-gray-700 rounded-full overflow-hidden">
              <div
                className="h-full bg-purple-500 rounded-full"
                style={{
                  width: `${Math.min(
                    100,
                    Math.max(
                      10,
                      100 - ((summary?.pointsToNextLevel || 100) / (summary?.userLevel ? summary.userLevel * 200 : 200)) * 100
                    )
                  )}%`,
                }}
              />
            </div>
          </div>
        </div>

        {/* Points Earned Today */}
        <div className="bg-white dark:bg-gray-800 p-5 rounded-2xl border border-gray-100 dark:border-gray-700 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-gray-500 dark:text-gray-400">
              Earned Today
            </span>
            <div className="w-9 h-9 rounded-xl bg-emerald-50 dark:bg-emerald-950/40 flex items-center justify-center text-emerald-500">
              <Sparkles className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-black text-emerald-600 dark:text-emerald-400">
              +{summary?.pointsEarnedToday || 0}
            </span>
            <span className="text-xs text-gray-500">points</span>
          </div>
          <div className="mt-3 text-xs text-gray-500 pt-3 border-t border-gray-100 dark:border-gray-700 flex justify-between">
            <span>Lifetime Earned</span>
            <span className="font-semibold text-gray-700 dark:text-gray-300">
              {summary?.lifetimePoints || 0} pts
            </span>
          </div>
        </div>

        {/* Pending Focus Queue */}
        <div className="bg-white dark:bg-gray-800 p-5 rounded-2xl border border-gray-100 dark:border-gray-700 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-gray-500 dark:text-gray-400">
              Active Focus
            </span>
            <div className="w-9 h-9 rounded-xl bg-blue-50 dark:bg-blue-950/40 flex items-center justify-center text-blue-500">
              <Clock className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-black text-gray-900 dark:text-white">{tasks.length}</span>
            <span className="text-xs text-gray-500">tasks remaining</span>
          </div>
          <div className="mt-3 text-xs text-primary-600 dark:text-primary-400 pt-3 border-t border-gray-100 dark:border-gray-700 flex justify-between items-center cursor-pointer hover:underline" onClick={onNavigateToTasks}>
            <span>Manage All Tasks</span>
            <ArrowUpRight className="w-3.5 h-3.5" />
          </div>
        </div>
      </div>

      {/* Quick Add Bar */}
      <QuickAddTask categories={categories} onTaskCreated={loadDashboardData} />

      {/* Main Grid: Tasks List & Analytics Preview */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left 2 Cols: Priority Tasks */}
        <div className="lg:col-span-2 space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-base font-bold text-gray-900 dark:text-white flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-primary-600" />
              <span>Today's Priority Tasks</span>
              <span className="text-xs font-normal text-gray-400">({tasks.length})</span>
            </h2>
            <button
              onClick={onNavigateToTasks}
              className="text-xs font-semibold text-primary-600 dark:text-primary-400 hover:underline flex items-center gap-1"
            >
              <span>View All</span>
              <ArrowUpRight className="w-3.5 h-3.5" />
            </button>
          </div>

          {isLoading ? (
            <div className="p-8 text-center text-gray-400 bg-white dark:bg-gray-800 rounded-2xl border border-gray-100 dark:border-gray-700">
              Loading tasks...
            </div>
          ) : tasks.length === 0 ? (
            <div className="p-10 text-center bg-white dark:bg-gray-800 rounded-2xl border border-gray-100 dark:border-gray-700 space-y-3">
              <div className="w-12 h-12 rounded-full bg-emerald-50 dark:bg-emerald-950/40 text-emerald-500 flex items-center justify-center mx-auto">
                <CheckCircle2 className="w-6 h-6" />
              </div>
              <h3 className="text-base font-bold text-gray-900 dark:text-white">All caught up!</h3>
              <p className="text-sm text-gray-500 max-w-sm mx-auto">
                No active pending tasks. Add a new task above to maintain your daily streak!
              </p>
            </div>
          ) : (
            <div className="space-y-3">
              {tasks.map((task) => (
                <TaskCard
                  key={task.id}
                  task={task}
                  onComplete={handleTaskComplete}
                  onCheckIn={handleCheckIn}
                  onEdit={handleEditTask}
                  onDelete={handleDelete}
                />
              ))}
            </div>
          )}
        </div>

        {/* Right 1 Col: Weekly Productivity & Activity Heatmap */}
        <div className="space-y-6">
          {/* Weekly Bar Chart Widget */}
          <div className="bg-white dark:bg-gray-800 p-5 rounded-2xl border border-gray-100 dark:border-gray-700 shadow-sm space-y-3">
            <div className="flex items-center justify-between">
              <h3 className="text-sm font-bold text-gray-900 dark:text-white flex items-center gap-2">
                <Layers className="w-4 h-4 text-primary-600" />
                <span>Weekly Activity</span>
              </h3>
              <button
                onClick={onNavigateToAnalytics}
                className="text-xs text-primary-600 dark:text-primary-400 hover:underline flex items-center gap-0.5"
              >
                <span>Analytics</span>
                <ArrowUpRight className="w-3 h-3" />
              </button>
            </div>
            <WeeklyChart data={weeklyMetrics} />
          </div>

          {/* Activity Heatmap Widget */}
          <div className="bg-white dark:bg-gray-800 p-5 rounded-2xl border border-gray-100 dark:border-gray-700 shadow-sm space-y-3">
            <h3 className="text-sm font-bold text-gray-900 dark:text-white">
              60-Day Consistency Grid
            </h3>
            <CalendarHeatmap data={heatmapData} />
          </div>
        </div>
      </div>

      {/* Task Edit/Create Modal */}
      <TaskModal
        isOpen={isTaskModalOpen}
        onClose={() => setIsTaskModalOpen(false)}
        categories={categories}
        taskToEdit={editingTask}
        onTaskSaved={loadDashboardData}
      />

      {/* Task Completion Celebration Modal */}
      <TaskCompletionModal
        isOpen={!!completionResult}
        onClose={() => setCompletionResult(null)}
        result={completionResult}
      />
    </div>
  );
};
