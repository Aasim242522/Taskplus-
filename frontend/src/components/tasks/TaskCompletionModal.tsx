import React from 'react';
import { Trophy, Award, Flame, CheckCircle, ArrowRight, Zap } from 'lucide-react';
import { TaskCompletionResult } from '../../types';

interface TaskCompletionModalProps {
  isOpen: boolean;
  onClose: () => void;
  result: TaskCompletionResult | null;
}

export const TaskCompletionModal: React.FC<TaskCompletionModalProps> = ({
  isOpen,
  onClose,
  result,
}) => {
  if (!isOpen || !result) return null;

  const { breakdown, pointsAwarded, currentStreak, newTotalPoints, badgesUnlocked } = result;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm animate-fade-in">
      <div className="bg-white dark:bg-gray-800 rounded-2xl shadow-2xl w-full max-w-md overflow-hidden border border-gray-100 dark:border-gray-700 text-center animate-scale-up">
        {/* Header Ribbon */}
        <div className="bg-gradient-to-r from-amber-500 to-primary-600 p-6 text-white relative">
          <div className="w-16 h-16 bg-white/20 backdrop-blur-md rounded-full flex items-center justify-center mx-auto mb-3 shadow-inner">
            <Trophy className="w-8 h-8 text-amber-200" />
          </div>
          <h2 className="text-xl font-bold">Task Completed!</h2>
          <p className="text-white/80 text-sm mt-1">Excellent productivity today!</p>

          <div className="mt-4 inline-flex items-center gap-2 bg-white/20 backdrop-blur-md px-4 py-1.5 rounded-full text-base font-bold shadow-sm">
            <Zap className="w-4 h-4 text-amber-300" />
            <span>+{pointsAwarded} Points Awarded</span>
          </div>
        </div>

        <div className="p-6 space-y-4">
          {/* Points Breakdown */}
          <div className="bg-gray-50 dark:bg-gray-700/50 rounded-xl p-4 text-left space-y-2 border border-gray-100 dark:border-gray-600">
            <h3 className="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider mb-2">
              Scoring Calculation
            </h3>
            <div className="flex justify-between text-sm text-gray-700 dark:text-gray-300">
              <span>Base Points</span>
              <span className="font-medium">+{breakdown.basePoints}</span>
            </div>
            <div className="flex justify-between text-sm text-gray-700 dark:text-gray-300">
              <span>Difficulty Multiplier</span>
              <span className="font-medium">×{breakdown.difficultyMultiplier.toFixed(1)}</span>
            </div>
            {breakdown.onTimeBonus > 0 && (
              <div className="flex justify-between text-sm text-emerald-600 dark:text-emerald-400">
                <span className="flex items-center gap-1">
                  <CheckCircle className="w-3.5 h-3.5" /> On-time Bonus
                </span>
                <span className="font-medium">+{breakdown.onTimeBonus}</span>
              </div>
            )}
            {breakdown.streakBonus > 0 && (
              <div className="flex justify-between text-sm text-amber-600 dark:text-amber-400">
                <span className="flex items-center gap-1">
                  <Flame className="w-3.5 h-3.5" /> Streak Bonus
                </span>
                <span className="font-medium">+{breakdown.streakBonus}</span>
              </div>
            )}
            {breakdown.durationEfficiencyBonus > 0 && (
              <div className="flex justify-between text-sm text-blue-600 dark:text-blue-400">
                <span>Efficiency Bonus</span>
                <span className="font-medium">+{breakdown.durationEfficiencyBonus}</span>
              </div>
            )}
            {breakdown.overduePenalty > 0 && (
              <div className="flex justify-between text-sm text-red-500">
                <span>Overdue Penalty</span>
                <span className="font-medium">-{breakdown.overduePenalty}</span>
              </div>
            )}
          </div>

          {/* Quick Metrics */}
          <div className="grid grid-cols-2 gap-3">
            <div className="p-3 bg-amber-50 dark:bg-amber-950/30 rounded-xl border border-amber-200 dark:border-amber-800">
              <div className="flex items-center justify-center gap-1 text-amber-600 dark:text-amber-400 mb-1">
                <Flame className="w-4 h-4" />
                <span className="text-xs font-semibold uppercase">Streak</span>
              </div>
              <p className="text-lg font-bold text-gray-900 dark:text-white">
                {currentStreak} <span className="text-xs font-normal text-gray-500">days</span>
              </p>
            </div>
            <div className="p-3 bg-primary-50 dark:bg-primary-950/30 rounded-xl border border-primary-200 dark:border-primary-800">
              <div className="flex items-center justify-center gap-1 text-primary-600 dark:text-primary-400 mb-1">
                <Trophy className="w-4 h-4" />
                <span className="text-xs font-semibold uppercase">Total Points</span>
              </div>
              <p className="text-lg font-bold text-gray-900 dark:text-white">{newTotalPoints}</p>
            </div>
          </div>

          {/* Badges Unlocked Section */}
          {badgesUnlocked && badgesUnlocked.length > 0 && (
            <div className="p-3 bg-gradient-to-r from-amber-100 to-yellow-100 dark:from-amber-950/50 dark:to-yellow-950/50 border border-amber-300 dark:border-amber-700 rounded-xl text-left">
              <div className="flex items-center gap-2 text-amber-800 dark:text-amber-300 font-semibold text-sm mb-2">
                <Award className="w-5 h-5 text-amber-600" />
                <span>New Badge Unlocked!</span>
              </div>
              {badgesUnlocked.map((b) => (
                <div key={b.id} className="flex items-center gap-2 text-xs text-gray-700 dark:text-gray-300">
                  <span className="text-base">{b.iconUrl || '🏆'}</span>
                  <div>
                    <p className="font-bold text-gray-900 dark:text-white">{b.name}</p>
                    <p className="text-gray-500">{b.description}</p>
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* Dismiss Button */}
          <button
            onClick={onClose}
            className="w-full mt-2 py-2.5 px-4 bg-primary-600 hover:bg-primary-700 text-white text-sm font-semibold rounded-xl transition-all shadow-md flex items-center justify-center gap-2"
          >
            <span>Continue Crushing Goals</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
};
