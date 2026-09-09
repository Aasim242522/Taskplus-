import React, { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { Flame, Award, LogOut, CheckCircle2 } from 'lucide-react';
import { gamificationApi } from '../../api/gamification';
import { PointsSummary } from '../../types';

export const Navbar: React.FC = () => {
  const { user, logout } = useAuth();
  const [summary, setSummary] = useState<PointsSummary | null>(null);

  useEffect(() => {
    if (user) {
      gamificationApi.getSummary()
        .then(setSummary)
        .catch(() => {});
    }
  }, [user]);

  return (
    <header className="bg-white border-b border-slate-200 sticky top-0 z-30">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 rounded-xl bg-indigo-600 flex items-center justify-center text-white shadow-md shadow-indigo-100">
            <CheckCircle2 className="w-6 h-6" />
          </div>
          <div>
            <h1 className="text-xl font-bold text-slate-900 tracking-tight">TaskPulse</h1>
            <p className="text-xs text-slate-500 font-medium hidden sm:block">Track. Achieve. Excel.</p>
          </div>
        </div>

        {user && (
          <div className="flex items-center space-x-4">
            {/* Streak Counter Pill */}
            <div className="flex items-center space-x-1.5 px-3 py-1.5 bg-amber-50 border border-amber-200 text-amber-800 rounded-full text-xs font-semibold shadow-sm">
              <Flame className="w-4 h-4 text-amber-600 fill-amber-500" />
              <span>{summary?.currentStreak ?? 0} Day Streak</span>
            </div>

            {/* Points Summary Pill */}
            <div className="flex items-center space-x-1.5 px-3 py-1.5 bg-indigo-50 border border-indigo-200 text-indigo-800 rounded-full text-xs font-semibold shadow-sm">
              <Award className="w-4 h-4 text-indigo-600" />
              <span>{summary?.currentBalance ?? 0} Pts</span>
              <span className="text-indigo-400 text-[10px] hidden sm:inline">(Lvl {summary?.userLevel ?? 1})</span>
            </div>

            {/* User Profile & Logout */}
            <div className="flex items-center space-x-2 pl-2 border-l border-slate-200">
              <span className="text-sm font-medium text-slate-700 hidden md:inline">
                {user.fullName}
              </span>
              <button
                onClick={logout}
                title="Logout"
                className="p-2 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
              >
                <LogOut className="w-4 h-4" />
              </button>
            </div>
          </div>
        )}
      </div>
    </header>
  );
};
