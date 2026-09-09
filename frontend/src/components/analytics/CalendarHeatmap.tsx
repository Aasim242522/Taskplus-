import React from 'react';
import { HeatmapDay } from '../../types';

interface CalendarHeatmapProps {
  data: HeatmapDay[];
}

export const CalendarHeatmap: React.FC<CalendarHeatmapProps> = ({ data }) => {
  if (!data || data.length === 0) {
    return (
      <div className="h-28 flex items-center justify-center text-sm text-gray-400">
        No activity history available.
      </div>
    );
  }

  const getColorClass = (level: number) => {
    switch (level) {
      case 1:
        return 'bg-emerald-200 dark:bg-emerald-900 border-emerald-300 dark:border-emerald-800';
      case 2:
        return 'bg-emerald-400 dark:bg-emerald-700 border-emerald-500 dark:border-emerald-600';
      case 3:
        return 'bg-emerald-500 dark:bg-emerald-500 border-emerald-600 dark:border-emerald-400';
      case 4:
        return 'bg-emerald-700 dark:bg-emerald-300 border-emerald-800 dark:border-emerald-200';
      default:
        return 'bg-gray-100 dark:bg-gray-800/60 border-gray-200 dark:border-gray-700';
    }
  };

  const totalCompletions = data.reduce((acc, curr) => acc + curr.count, 0);

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between text-xs text-gray-500 dark:text-gray-400">
        <span>{totalCompletions} tasks completed in the last 60 days</span>
        <div className="flex items-center gap-1.5">
          <span>Less</span>
          <div className="w-2.5 h-2.5 rounded-sm bg-gray-100 dark:bg-gray-800 border border-gray-200 dark:border-gray-700" />
          <div className="w-2.5 h-2.5 rounded-sm bg-emerald-200 dark:bg-emerald-900 border border-emerald-300" />
          <div className="w-2.5 h-2.5 rounded-sm bg-emerald-400 dark:bg-emerald-700 border border-emerald-500" />
          <div className="w-2.5 h-2.5 rounded-sm bg-emerald-500 border border-emerald-600" />
          <div className="w-2.5 h-2.5 rounded-sm bg-emerald-700 dark:bg-emerald-300 border border-emerald-800" />
          <span>More</span>
        </div>
      </div>

      <div className="flex flex-wrap gap-1.5 p-3 bg-gray-50/50 dark:bg-gray-900/30 rounded-xl border border-gray-100 dark:border-gray-800 overflow-x-auto">
        {data.map((day) => (
          <div
            key={day.date}
            title={`${day.date}: ${day.count} tasks completed`}
            className={`w-3.5 h-3.5 rounded-sm border transition-all hover:scale-125 cursor-pointer ${getColorClass(
              day.level
            )}`}
          />
        ))}
      </div>
    </div>
  );
};
