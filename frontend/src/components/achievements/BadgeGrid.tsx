import React, { useState } from 'react';
import { Award, Lock, Sparkles, CheckCircle2 } from 'lucide-react';
import { Badge, BadgeType } from '../../types';

interface BadgeGridProps {
  badges: Badge[];
}

export const BadgeGrid: React.FC<BadgeGridProps> = ({ badges }) => {
  const [filterType, setFilterType] = useState<string>('ALL');

  const filteredBadges = badges.filter((b) => {
    if (filterType === 'ALL') return true;
    if (filterType === 'UNLOCKED') return b.unlocked;
    if (filterType === 'LOCKED') return !b.unlocked;
    return b.badgeType === filterType;
  });

  const unlockedCount = badges.filter((b) => b.unlocked).length;

  return (
    <div className="space-y-6">
      {/* Badges Overview & Filter Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white dark:bg-gray-800 p-4 rounded-xl border border-gray-100 dark:border-gray-700 shadow-sm">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-amber-50 dark:bg-amber-950/50 flex items-center justify-center text-amber-600 dark:text-amber-400">
            <Award className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-sm font-bold text-gray-900 dark:text-white">
              Badges & Achievements
            </h3>
            <p className="text-xs text-gray-500">
              Unlocked {unlockedCount} of {badges.length} badges
            </p>
          </div>
        </div>

        {/* Filters */}
        <div className="flex flex-wrap items-center gap-1.5 bg-gray-100 dark:bg-gray-700/50 p-1 rounded-lg text-xs">
          {['ALL', 'UNLOCKED', 'LOCKED', 'STREAK', 'TASK_COUNT', 'CATEGORY_MASTERY'].map((tab) => (
            <button
              key={tab}
              onClick={() => setFilterType(tab)}
              className={`px-3 py-1.5 rounded-md font-medium transition-all ${
                filterType === tab
                  ? 'bg-white dark:bg-gray-800 text-primary-600 dark:text-primary-400 shadow-sm'
                  : 'text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white'
              }`}
            >
              {tab.replace('_', ' ')}
            </button>
          ))}
        </div>
      </div>

      {/* Grid of Badges */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {filteredBadges.map((badge) => {
          const progressPercent = Math.min(100, Math.round((badge.progress / (badge.target || 1)) * 100));

          return (
            <div
              key={badge.id || badge.code}
              className={`relative overflow-hidden rounded-xl p-5 border transition-all ${
                badge.unlocked
                  ? 'bg-gradient-to-br from-white to-amber-50/30 dark:from-gray-800 dark:to-amber-950/20 border-amber-200 dark:border-amber-800 shadow-sm hover:shadow-md'
                  : 'bg-white dark:bg-gray-800/60 border-gray-100 dark:border-gray-700/60 opacity-80'
              }`}
            >
              {badge.unlocked && (
                <div className="absolute -top-6 -right-6 w-16 h-16 bg-amber-400/20 rounded-full blur-xl pointer-events-none" />
              )}

              <div className="flex items-start gap-4">
                {/* Badge Icon */}
                <div
                  className={`w-14 h-14 rounded-2xl flex items-center justify-center text-2xl shrink-0 transition-transform ${
                    badge.unlocked
                      ? 'bg-gradient-to-tr from-amber-400 to-amber-200 text-amber-900 shadow-md shadow-amber-500/20'
                      : 'bg-gray-100 dark:bg-gray-700 text-gray-400 grayscale'
                  }`}
                >
                  {badge.iconUrl || '🏆'}
                </div>

                {/* Details */}
                <div className="flex-1 min-w-0">
                  <div className="flex items-center justify-between gap-1 mb-1">
                    <h4 className="text-sm font-bold text-gray-900 dark:text-gray-100 truncate">
                      {badge.name}
                    </h4>
                    {badge.unlocked ? (
                      <span className="flex items-center gap-1 text-[11px] font-semibold text-emerald-600 dark:text-emerald-400 bg-emerald-50 dark:bg-emerald-950/50 px-2 py-0.5 rounded-full shrink-0">
                        <CheckCircle2 className="w-3 h-3" /> Unlocked
                      </span>
                    ) : (
                      <span className="flex items-center gap-1 text-[11px] font-semibold text-gray-400 bg-gray-100 dark:bg-gray-700 px-2 py-0.5 rounded-full shrink-0">
                        <Lock className="w-3 h-3" /> Locked
                      </span>
                    )}
                  </div>

                  <p className="text-xs text-gray-500 dark:text-gray-400 line-clamp-2 mb-3">
                    {badge.description}
                  </p>

                  {/* Progress or Reward */}
                  {badge.unlocked ? (
                    <div className="flex items-center justify-between text-xs text-amber-700 dark:text-amber-400 pt-1 border-t border-amber-100 dark:border-amber-900/50">
                      <span className="flex items-center gap-1">
                        <Sparkles className="w-3.5 h-3.5" /> +{badge.pointsReward} pts awarded
                      </span>
                      {badge.unlockedAt && (
                        <span className="text-gray-400 text-[10px]">
                          {new Date(badge.unlockedAt).toLocaleDateString()}
                        </span>
                      )}
                    </div>
                  ) : (
                    <div className="space-y-1.5 pt-1 border-t border-gray-100 dark:border-gray-700">
                      <div className="flex justify-between text-[11px] text-gray-500 dark:text-gray-400">
                        <span>Progress</span>
                        <span className="font-semibold">
                          {badge.progress} / {badge.target} ({progressPercent}%)
                        </span>
                      </div>
                      <div className="w-full h-1.5 bg-gray-100 dark:bg-gray-700 rounded-full overflow-hidden">
                        <div
                          className="h-full bg-primary-500 rounded-full transition-all duration-500"
                          style={{ width: `${progressPercent}%` }}
                        />
                      </div>
                    </div>
                  )}
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
