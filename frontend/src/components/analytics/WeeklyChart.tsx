import React from 'react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend,
} from 'recharts';
import { DailyMetric } from '../../types';

interface WeeklyChartProps {
  data: DailyMetric[];
}

export const WeeklyChart: React.FC<WeeklyChartProps> = ({ data }) => {
  if (!data || data.length === 0) {
    return (
      <div className="h-64 flex items-center justify-center text-sm text-gray-400">
        No activity recorded this week yet.
      </div>
    );
  }

  const chartData = data.map((item) => ({
    name: item.dayOfWeek.slice(0, 3), // Mon, Tue, etc.
    completed: item.tasksCompleted,
    created: item.tasksCreated,
    points: item.pointsEarned,
    date: item.date,
  }));

  return (
    <div className="w-full h-72">
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={chartData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
          <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#E5E7EB" />
          <XAxis
            dataKey="name"
            stroke="#9CA3AF"
            fontSize={12}
            tickLine={false}
            axisLine={false}
          />
          <YAxis stroke="#9CA3AF" fontSize={12} tickLine={false} axisLine={false} />
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
              if (name === 'completed') return [value, 'Tasks Completed'];
              if (name === 'created') return [value, 'Tasks Created'];
              if (name === 'points') return [value, 'Points Earned'];
              return [value, name];
            }}
            labelFormatter={(label, payload) => {
              const date = payload?.[0]?.payload?.date || '';
              return `${label} (${date})`;
            }}
          />
          <Legend
            iconType="circle"
            wrapperStyle={{ paddingTop: '10px', fontSize: '12px' }}
            formatter={(val) => {
              if (val === 'completed') return 'Completed Tasks';
              if (val === 'created') return 'Created Tasks';
              return val;
            }}
          />
          <Bar dataKey="completed" fill="#6366F1" radius={[4, 4, 0, 0]} maxBarSize={32} />
          <Bar dataKey="created" fill="#E0E7FF" radius={[4, 4, 0, 0]} maxBarSize={32} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};
