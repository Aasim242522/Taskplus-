import React, { useState, useEffect } from 'react';
import {
  TrendingUp,
  Award,
  CheckCircle2,
  Clock,
  Calendar as CalendarIcon,
  Zap,
} from 'lucide-react';
import { analyticsApi } from '../api/analytics';
import { HeatmapDay, MonthlyAnalytics, WeeklyAnalytics } from '../types';
import { WeeklyChart } from '../components/analytics/WeeklyChart';
import { MonthlyTrendChart } from '../components/analytics/MonthlyTrendChart';
import { CalendarHeatmap } from '../components/analytics/CalendarHeatmap';

export const AnalyticsPage: React.FC = () => {
  const [weeklyData, setWeeklyData] = useState<WeeklyAnalytics | null>(null);
  const [monthlyData, setMonthlyData] = useState<MonthlyAnalytics | null>(null);
  const [heatmapData, setHeatmapData] = useState<HeatmapDay[]>([]);
  const [activeTab, setActiveTab] = useState<'WEEKLY' | 'MONTHLY'>('WEEKLY');
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchAnalytics = async () => {
      try {
        setIsLoading(true);
        const [weeklyRes, monthlyRes, heatmapRes] = await Promise.all([
          analyticsApi.getWeekly(),
          analyticsApi.getMonthly(),
          analyticsApi.getHeatmap(60),
        ]);
        setWeeklyData(weeklyRes);
        setMonthlyData(monthlyRes);
        setHeatmapData(heatmapRes || []);
      } catch (err) {
        console.error('Failed to fetch analytics', err);
      } finally {
        setIsLoading(false);
      }
    };

    fetchAnalytics();
  }, []);

  const currentRate =
    activeTab === 'WEEKLY'
      ? weeklyData?.completionRate || 0
      : monthlyData?.completionRate || 0;

  const totalCompleted =
    activeTab === 'WEEKLY'
      ? weeklyData?.totalCompleted || 0
      : monthlyData?.totalCompleted || 0;

  const totalCreated =
    activeTab === 'WEEKLY'
      ? weeklyData?.totalCreated || 0
      : monthlyData?.totalCreated || 0;

  const totalPoints =
    activeTab === 'WEEKLY'
      ? weeklyData?.totalPointsEarned || 0
      : monthlyData?.totalPointsEarned || 0;

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-black text-gray-900 dark:text-white">
            Performance & Analytics
          </h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">
            Deep dive into your daily completion habits, point earnings, and momentum
          </p>
        </div>

        {/* Tab Selector */}
        <div className="bg-gray-100 dark:bg-gray-800 p-1 rounded-xl flex items-center gap-1 border border-gray-200 dark:border-gray-700 self-start sm:self-auto">
          <button
            onClick={() => setActiveTab('WEEKLY')}
            className={`px-4 py-1.5 rounded-lg text-xs font-semibold transition-all ${
              activeTab === 'WEEKLY'
                ? 'bg-white dark:bg-gray-700 text-primary-600 dark:text-primary-400 shadow-sm'
                : 'text-gray-500 hover:text-gray-900 dark:hover:text-white'
            }`}
          >
            Weekly Rollup
          </button>
          <button
            onClick={() => setActiveTab('MONTHLY')}
            className={`px-4 py-1.5 rounded-lg text-xs font-semibold transition-all ${
              activeTab === 'MONTHLY'
                ? 'bg-white dark:bg-gray-700 text-primary-600 dark:text-primary-400 shadow-sm'
                : 'text-gray-500 hover:text-gray-900 dark:hover:text-white'
            }`}
          >
            Monthly Trend
          </button>
        </div>
      </div>

      {/* Summary KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Completion Rate */}
        <div className="bg-white dark:bg-gray-800 p-5 rounded-2xl border border-gray-100 dark:border-gray-700 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-gray-500 dark:text-gray-400">
              Completion Rate
            </span>
            <div className="w-8 h-8 rounded-xl bg-emerald-50 dark:bg-emerald-950/40 text-emerald-600 flex items-center justify-center">
              <CheckCircle2 className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-black text-gray-900 dark:text-white">
              {currentRate.toFixed(1)}%
            </span>
          </div>
          <p className="mt-2 text-xs text-gray-500">
            {totalCompleted} of {totalCreated} tasks completed
          </p>
        </div>

        {/* Total Points Earned */}
        <div className="bg-white dark:bg-gray-800 p-5 rounded-2xl border border-gray-100 dark:border-gray-700 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-gray-500 dark:text-gray-400">
              Points Accrued
            </span>
            <div className="w-8 h-8 rounded-xl bg-amber-50 dark:bg-amber-950/40 text-amber-600 flex items-center justify-center">
              <Zap className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-black text-amber-600 dark:text-amber-400">
              +{totalPoints}
            </span>
            <span className="text-xs text-gray-500">pts</span>
          </div>
          <p className="mt-2 text-xs text-gray-500">
            {activeTab === 'WEEKLY'
              ? `Avg ${(weeklyData?.avgPointsPerDay || 0).toFixed(0)} pts/day`
              : 'Cumulative in 30-day window'}
          </p>
        </div>

        {/* Tasks Completed */}
        <div className="bg-white dark:bg-gray-800 p-5 rounded-2xl border border-gray-100 dark:border-gray-700 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-gray-500 dark:text-gray-400">
              Tasks Completed
            </span>
            <div className="w-8 h-8 rounded-xl bg-primary-50 dark:bg-primary-950/40 text-primary-600 flex items-center justify-center">
              <TrendingUp className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-black text-gray-900 dark:text-white">
              {totalCompleted}
            </span>
          </div>
          <p className="mt-2 text-xs text-gray-500">Successfully closed</p>
        </div>

        {/* Focus / Duration Metric */}
        <div className="bg-white dark:bg-gray-800 p-5 rounded-2xl border border-gray-100 dark:border-gray-700 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-gray-500 dark:text-gray-400">
              Total Focus Time
            </span>
            <div className="w-8 h-8 rounded-xl bg-indigo-50 dark:bg-indigo-950/40 text-indigo-600 flex items-center justify-center">
              <Clock className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-black text-gray-900 dark:text-white">
              {activeTab === 'WEEKLY'
                ? Math.round(
                    (weeklyData?.dailyMetrics.reduce(
                      (acc, d) => acc + d.totalDurationMinutes,
                      0
                    ) || 0) / 60
                  )
                : Math.round((monthlyData?.totalFocusMinutes || 0) / 60)}
            </span>
            <span className="text-xs text-gray-500">hours</span>
          </div>
          <p className="mt-2 text-xs text-gray-500">Actual tracked duration</p>
        </div>
      </div>

      {/* Main Chart Card */}
      <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl border border-gray-100 dark:border-gray-700 shadow-sm space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-base font-bold text-gray-900 dark:text-white">
              {activeTab === 'WEEKLY' ? 'Weekly Daily Breakdown' : 'Monthly Performance Trend'}
            </h3>
            <p className="text-xs text-gray-500">
              {activeTab === 'WEEKLY'
                ? 'Tasks completed vs created across days of current week'
                : '30-day trajectory of completion velocity & gamification points'}
            </p>
          </div>
        </div>

        {isLoading ? (
          <div className="h-64 flex items-center justify-center text-sm text-gray-400">
            Loading analytics...
          </div>
        ) : activeTab === 'WEEKLY' ? (
          <WeeklyChart data={weeklyData?.dailyMetrics || []} />
        ) : (
          <MonthlyTrendChart data={monthlyData?.trendPoints || []} />
        )}
      </div>

      {/* Heatmap Consistency Card */}
      <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl border border-gray-100 dark:border-gray-700 shadow-sm space-y-4">
        <div>
          <h3 className="text-base font-bold text-gray-900 dark:text-white flex items-center gap-2">
            <CalendarIcon className="w-4 h-4 text-primary-600" />
            <span>Consistency Heatmap</span>
          </h3>
          <p className="text-xs text-gray-500">
            Visual activity record across the past 60 days. Consistency fuels streak multipliers!
          </p>
        </div>
        <CalendarHeatmap data={heatmapData} />
      </div>
    </div>
  );
};
