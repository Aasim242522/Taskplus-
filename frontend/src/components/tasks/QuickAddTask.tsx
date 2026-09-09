import React, { useState } from 'react';
import { Plus, Sparkles, AlertCircle } from 'lucide-react';
import { Category, Priority, RecurrenceType } from '../../types';
import { tasksApi } from '../../api/tasks';

interface QuickAddTaskProps {
  categories: Category[];
  onTaskCreated: () => void;
}

export const QuickAddTask: React.FC<QuickAddTaskProps> = ({ categories, onTaskCreated }) => {
  const [title, setTitle] = useState('');
  const [categoryId, setCategoryId] = useState<string>('');
  const [priority, setPriority] = useState<Priority>('MEDIUM');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) return;

    try {
      setIsSubmitting(true);
      setError(null);

      await tasksApi.createTask({
        title: title.trim(),
        categoryId: categoryId || undefined,
        priority,
        recurrenceType: 'NONE' as RecurrenceType,
        estimatedDurationMinutes: 30,
        difficultyLevel: 2,
      });

      setTitle('');
      onTaskCreated();
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Failed to create task');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-100 dark:border-gray-700 p-4 transition-all hover:shadow-md">
      <form onSubmit={handleSubmit} className="space-y-3">
        {error && (
          <div className="flex items-center gap-2 text-sm text-red-600 dark:text-red-400 bg-red-50 dark:bg-red-950/30 p-2.5 rounded-lg">
            <AlertCircle className="w-4 h-4 shrink-0" />
            <span>{error}</span>
          </div>
        )}

        <div className="flex items-center gap-3">
          <div className="relative flex-1">
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="What do you want to accomplish today? (e.g. Finish Sprint Review #work)"
              className="w-full pl-4 pr-10 py-2.5 text-sm bg-gray-50 dark:bg-gray-700/50 border border-gray-200 dark:border-gray-600 rounded-lg text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all"
              disabled={isSubmitting}
            />
            <Sparkles className="w-4 h-4 text-amber-500 absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none opacity-60" />
          </div>

          <button
            type="submit"
            disabled={!title.trim() || isSubmitting}
            className="flex items-center gap-1.5 px-4 py-2.5 bg-primary-600 hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed text-white text-sm font-medium rounded-lg transition-colors shadow-sm shrink-0"
          >
            <Plus className="w-4 h-4" />
            <span>Add Task</span>
          </button>
        </div>

        <div className="flex flex-wrap items-center gap-2 pt-1 text-xs text-gray-500 dark:text-gray-400">
          <span className="font-medium text-gray-700 dark:text-gray-300">Quick options:</span>
          
          {/* Priority selector */}
          <select
            value={priority}
            onChange={(e) => setPriority(e.target.value as Priority)}
            className="bg-gray-50 dark:bg-gray-700 border border-gray-200 dark:border-gray-600 rounded-md px-2 py-1 text-xs text-gray-700 dark:text-gray-300 focus:outline-none focus:border-primary-500"
          >
            <option value="LOW">🟢 Low Priority</option>
            <option value="MEDIUM">🟡 Medium Priority</option>
            <option value="HIGH">🟠 High Priority</option>
            <option value="CRITICAL">🔴 Critical Priority</option>
          </select>

          {/* Category selector */}
          {categories.length > 0 && (
            <select
              value={categoryId}
              onChange={(e) => setCategoryId(e.target.value)}
              className="bg-gray-50 dark:bg-gray-700 border border-gray-200 dark:border-gray-600 rounded-md px-2 py-1 text-xs text-gray-700 dark:text-gray-300 focus:outline-none focus:border-primary-500"
            >
              <option value="">📁 No Category</option>
              {categories.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))}
            </select>
          )}

          <span className="text-gray-400 ml-auto hidden sm:inline">Press Enter to submit</span>
        </div>
      </form>
    </div>
  );
};
