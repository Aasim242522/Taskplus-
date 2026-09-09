import apiClient from './client';
import { HeatmapDay, MonthlyAnalytics, WeeklyAnalytics } from '../types';

export const analyticsApi = {
  getWeekly: async (): Promise<WeeklyAnalytics> => {
    return (await apiClient.get('/analytics/weekly')) as unknown as WeeklyAnalytics;
  },

  getMonthly: async (year?: number, month?: number): Promise<MonthlyAnalytics> => {
    return (await apiClient.get('/analytics/monthly', { params: { year, month } })) as unknown as MonthlyAnalytics;
  },

  getHeatmap: async (days = 60): Promise<HeatmapDay[]> => {
    return (await apiClient.get('/analytics/heatmap', { params: { days } })) as unknown as HeatmapDay[];
  },
};
