import React, { useState, useEffect } from 'react';
import {
  Trophy,
  Award,
  Flame,
  Shield,
  Clock,
  Sparkles,
  ArrowUpRight,
  TrendingUp,
} from 'lucide-react';
import { gamificationApi } from '../api/gamification';
import { Badge, LeaderboardEntry, PointsLedgerItem, PointsSummary } from '../types';
import { BadgeGrid } from '../components/achievements/BadgeGrid';

export const AchievementsPage: React.FC = () => {
  const [summary, setSummary] = useState<PointsSummary | null>(null);
  const [badges, setBadges] = useState<Badge[]>([]);
  const [leaderboard, setLeaderboard] = useState<LeaderboardEntry[]>([]);
  const [ledger, setLedger] = useState<PointsLedgerItem[]>([]);
  const [activeTab, setActiveTab] = useState<'BADGES' | 'LEADERBOARD' | 'LEDGER'>('BADGES');
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setIsLoading(true);
        const [summaryRes, badgesRes, leaderboardRes, ledgerRes] = await Promise.all([
          gamificationApi.getSummary(),
          gamificationApi.getBadges(),
          gamificationApi.getLeaderboard(10),
          gamificationApi.getLedger(0, 20),
        ]);
        setSummary(summaryRes);
        setBadges(badgesRes || []);
        setLeaderboard(leaderboardRes || []);
        setLedger(ledgerRes?.content || []);
      } catch (err) {
        console.error('Failed to load achievements data', err);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, []);

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Hero Level & Points Card */}
      <div className="bg-gradient-to-r from-amber-500 via-orange-500 to-primary-600 rounded-3xl p-6 sm:p-8 text-white shadow-xl shadow-amber-500/10 relative overflow-hidden">
        <div className="absolute -right-8 -bottom-8 w-48 h-48 bg-white/10 rounded-full blur-2xl pointer-events-none" />

        <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 relative z-10">
          <div className="flex items-center gap-5">
            <div className="w-16 h-16 sm:w-20 sm:h-20 bg-white/20 backdrop-blur-md rounded-2xl flex items-center justify-center text-amber-200 text-3xl sm:text-4xl shadow-inner border border-white/20">
              👑
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="px-2.5 py-0.5 rounded-full bg-white/20 backdrop-blur-md text-xs font-bold uppercase tracking-wider text-amber-100">
                  Level {summary?.userLevel || 1}
                </span>
                <span className="text-white/80 text-xs flex items-center gap-1">
                  <Shield className="w-3.5 h-3.5" /> {summary?.freezeCredits || 0} Streak Freezes
                </span>
              </div>
              <h1 className="text-2xl sm:text-3xl font-black mt-1">
                {summary?.currentBalance || 0}{' '}
                <span className="text-lg sm:text-xl font-normal text-white/80">Points</span>
              </h1>
              <p className="text-white/80 text-xs sm:text-sm mt-0.5">
                {summary?.pointsToNextLevel || 0} points needed to reach Level {(summary?.userLevel || 1) + 1}
              </p>
            </div>
          </div>

          {/* Mini Stats */}
          <div className="flex items-center gap-4 bg-black/10 backdrop-blur-md px-5 py-3 rounded-2xl border border-white/10 self-start md:self-auto">
            <div className="text-center px-2">
              <div className="flex items-center justify-center gap-1 text-amber-200 text-xs font-semibold">
                <Flame className="w-3.5 h-3.5" /> Streak
              </div>
              <p className="text-xl font-black mt-0.5">{summary?.currentStreak || 0}d</p>
            </div>
            <div className="w-px h-8 bg-white/20" />
            <div className="text-center px-2">
              <div className="flex items-center justify-center gap-1 text-amber-200 text-xs font-semibold">
                <Trophy className="w-3.5 h-3.5" /> Lifetime
              </div>
              <p className="text-xl font-black mt-0.5">{summary?.lifetimePoints || 0}</p>
            </div>
          </div>
        </div>
      </div>

      {/* Navigation Tabs */}
      <div className="flex items-center gap-2 border-b border-gray-200 dark:border-gray-700 pb-2">
        <button
          onClick={() => setActiveTab('BADGES')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-bold transition-all ${
            activeTab === 'BADGES'
              ? 'bg-primary-600 text-white shadow-sm'
              : 'text-gray-500 hover:text-gray-900 dark:hover:text-white hover:bg-gray-100 dark:hover:bg-gray-800'
          }`}
        >
          <Award className="w-4 h-4" />
          <span>Badges & Trophies ({badges.filter((b) => b.unlocked).length}/{badges.length})</span>
        </button>

        <button
          onClick={() => setActiveTab('LEADERBOARD')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-bold transition-all ${
            activeTab === 'LEADERBOARD'
              ? 'bg-primary-600 text-white shadow-sm'
              : 'text-gray-500 hover:text-gray-900 dark:hover:text-white hover:bg-gray-100 dark:hover:bg-gray-800'
          }`}
        >
          <Trophy className="w-4 h-4" />
          <span>Leaderboard</span>
        </button>

        <button
          onClick={() => setActiveTab('LEDGER')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-bold transition-all ${
            activeTab === 'LEDGER'
              ? 'bg-primary-600 text-white shadow-sm'
              : 'text-gray-500 hover:text-gray-900 dark:hover:text-white hover:bg-gray-100 dark:hover:bg-gray-800'
          }`}
        >
          <Clock className="w-4 h-4" />
          <span>Points Audit Ledger</span>
        </button>
      </div>

      {/* Tab Content */}
      {isLoading ? (
        <div className="p-12 text-center text-gray-400 bg-white dark:bg-gray-800 rounded-2xl border border-gray-100 dark:border-gray-700">
          Loading gamification details...
        </div>
      ) : activeTab === 'BADGES' ? (
        <BadgeGrid badges={badges} />
      ) : activeTab === 'LEADERBOARD' ? (
        /* Leaderboard Table */
        <div className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-100 dark:border-gray-700 shadow-sm overflow-hidden">
          <div className="p-5 border-b border-gray-100 dark:border-gray-700">
            <h3 className="text-base font-bold text-gray-900 dark:text-white flex items-center gap-2">
              <Trophy className="w-5 h-5 text-amber-500" />
              <span>Top Achievers</span>
            </h3>
            <p className="text-xs text-gray-500">
              Ranked by total lifetime points earned across all completed tasks and streaks
            </p>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-gray-50 dark:bg-gray-700/50 text-xs uppercase text-gray-500 font-semibold border-b border-gray-100 dark:border-gray-700">
                <tr>
                  <th className="px-6 py-3">Rank</th>
                  <th className="px-6 py-3">User</th>
                  <th className="px-6 py-3">Streak</th>
                  <th className="px-6 py-3 text-right">Lifetime Points</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100 dark:divide-gray-700">
                {leaderboard.map((entry) => (
                  <tr
                    key={entry.userId}
                    className="hover:bg-gray-50/50 dark:hover:bg-gray-700/30 transition-colors"
                  >
                    <td className="px-6 py-4 font-bold">
                      {entry.rank === 1 ? (
                        <span className="text-lg">🥇</span>
                      ) : entry.rank === 2 ? (
                        <span className="text-lg">🥈</span>
                      ) : entry.rank === 3 ? (
                        <span className="text-lg">🥉</span>
                      ) : (
                        <span className="text-gray-500">#{entry.rank}</span>
                      )}
                    </td>
                    <td className="px-6 py-4 font-semibold text-gray-900 dark:text-white">
                      {entry.fullName}
                    </td>
                    <td className="px-6 py-4">
                      <span className="inline-flex items-center gap-1 text-xs font-semibold text-amber-600 dark:text-amber-400 bg-amber-50 dark:bg-amber-950/40 px-2 py-0.5 rounded-full">
                        <Flame className="w-3 h-3" /> {entry.streak} days
                      </span>
                    </td>
                    <td className="px-6 py-4 text-right font-black text-primary-600 dark:text-primary-400">
                      {entry.totalPoints.toLocaleString()} pts
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      ) : (
        /* Immutable Points Ledger Audit Trail */
        <div className="bg-white dark:bg-gray-800 rounded-2xl border border-gray-100 dark:border-gray-700 shadow-sm overflow-hidden">
          <div className="p-5 border-b border-gray-100 dark:border-gray-700">
            <h3 className="text-base font-bold text-gray-900 dark:text-white flex items-center gap-2">
              <Clock className="w-5 h-5 text-primary-600" />
              <span>Immutable Points Audit Trail</span>
            </h3>
            <p className="text-xs text-gray-500">
              Complete, verified transaction ledger of every point awarded or penalized
            </p>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-gray-50 dark:bg-gray-700/50 text-xs uppercase text-gray-500 font-semibold border-b border-gray-100 dark:border-gray-700">
                <tr>
                  <th className="px-6 py-3">Timestamp</th>
                  <th className="px-6 py-3">Type</th>
                  <th className="px-6 py-3">Description</th>
                  <th className="px-6 py-3 text-right">Points Delta</th>
                  <th className="px-6 py-3 text-right">Balance After</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100 dark:divide-gray-700">
                {ledger.map((item) => (
                  <tr
                    key={item.id}
                    className="hover:bg-gray-50/50 dark:hover:bg-gray-700/30 transition-colors"
                  >
                    <td className="px-6 py-4 text-xs text-gray-500">
                      {new Date(item.createdAt).toLocaleString()}
                    </td>
                    <td className="px-6 py-4">
                      <span className="text-xs font-mono font-medium px-2 py-0.5 rounded bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300">
                        {item.transactionType}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-800 dark:text-gray-200">
                      {item.description}
                    </td>
                    <td
                      className={`px-6 py-4 text-right font-bold ${
                        item.pointsDelta > 0
                          ? 'text-emerald-600 dark:text-emerald-400'
                          : 'text-red-500'
                      }`}
                    >
                      {item.pointsDelta > 0 ? `+${item.pointsDelta}` : item.pointsDelta}
                    </td>
                    <td className="px-6 py-4 text-right font-semibold text-gray-700 dark:text-gray-300">
                      {item.balanceAfter}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};
