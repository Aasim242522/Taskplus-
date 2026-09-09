import React from 'react';
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts';
import { DailyTrendPoint } from '../../types';

interface MonthlyTrendChartProps {
  data: DailyTrendPoint[];
}

export const MonthlyTrendChart: React.FC<MonthlyTrendChartProps> = ({ data }) => {
  if (!data || data.length === 0) {
    return (
      <div className="h-64 flex items-center justify-center text-sm text-gray-400">
        No monthly trend data available yet.
      </div>
    );
  }

  const chartData = data.map((d) => ({
    date: d.date.slice(5), // MM-DD
    fullDate: d.date,
    completed: d.completedCount,
    points: d.pointsEarned,
  }));

  return (
    <div className="w-full h-72">
      <ResponsiveContainer width="100%" height="100%">
        <AreaChart data={chartData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
          <defs>
            <linearGradient id="colorPoints" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#8B5CF6" stopOpacity={0.8} />
              <stop offset="95%" stopColor="#8B5CF6" stopOpacity={0.0} />
            </linearGradient>
            <linearGradient id="colorTasks" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#10B981" stopOpacity={0.8} />
              <stop offset="95%" stopColor="#10B981" stopOpacity={0.0} />
            </linearGradient>
          </defs>
          <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#E5E7EB" />
          <XAxis
            dataKey="date"
            stroke="#9CA3AF"
            fontSize={11}
            tickLine={false}
            axisLine={false}
          />
          <YAxis stroke="#9CA3AF" fontSize={11} tickLine={false} axisLine={false} />
          <Tooltip
            contentStyle={{
              backgroundColor: '#1F2937',
              borderRadius: '8px',
              border: 'none',
              color: '#F9FAFB',
              fontSize: '12px',
              boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)',
            }}
            formatter={(value: any, name: string) => {
              if (name === 'points') return [value, 'Points Earned'];
              if (name === 'completed') return [value, 'Tasks Completed'];
              return [value, name];
            }}
            labelFormatter={(label, payload) => {
              return payload?.[0]?.payload?.fullDate || label;
            }}
          />
          <Area
            type="monotone"
            dataKey="points"
            stroke="#8B5CF6"
            strokeWidth={2}
            fillOpacity={1}
            fill="url(#colorPoints)"
            name="points"
          />
          <Area
            type="monotone"
            dataKey="completed"
            stroke="#10B981"
            strokeWidth={2}
            fillOpacity={1}
            fill="url(#colorTasks)"
            name="completed"
          />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );
};
