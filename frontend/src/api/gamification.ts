import apiClient from './client';
import { Badge, LeaderboardEntry, PointsLedgerItem, PointsSummary } from '../types';

export const gamificationApi = {
  getSummary: async (): Promise<PointsSummary> => {
    return (await apiClient.get('/gamification/summary')) as unknown as PointsSummary;
  },

  getLedger: async (page = 0, size = 20): Promise<{ content: PointsLedgerItem[]; totalPages: number }> => {
    return (await apiClient.get('/gamification/ledger', { params: { page, size } })) as unknown as {
      content: PointsLedgerItem[];
      totalPages: number;
    };
  },

  getBadges: async (): Promise<Badge[]> => {
    return (await apiClient.get('/gamification/badges')) as unknown as Badge[];
  },

  getLeaderboard: async (limit = 10): Promise<LeaderboardEntry[]> => {
    return (await apiClient.get('/gamification/leaderboard', { params: { limit } })) as unknown as LeaderboardEntry[];
  },
};
