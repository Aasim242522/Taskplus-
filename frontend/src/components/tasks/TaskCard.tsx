import React, { useState } from 'react';
import { Task, Priority } from '../../types';
import { Check, Clock, AlertCircle, Edit2, Trash2, Plus, Zap } from 'lucide-react';
import { format, isPast, parseISO } from 'date-fns';

interface TaskCardProps {
  task: Task;
  onComplete: (taskId: string) => Promise<void>;
  onCheckIn: (taskId: string, mins: number) => Promise<void>;
  onEdit: (task: Task) => void;
  onDelete: (taskId: string) => Promise<void>;
}

export const TaskCard: React.FC<TaskCardProps> = ({
  task,
  onComplete,
  onCheckIn,
  onEdit,
  onDelete,
}) => {
  const [completing, setCompleting] = useState(false);
  const [checkingIn, setCheckingIn] = useState(false);

  const isCompleted = task.status === 'COMPLETED';
  const isOverdue = task.status === 'OVERDUE' || (task.dueDateTime && isPast(parseISO(task.dueDateTime)) && !isCompleted);

  const priorityColors: Record<Priority, string> = {
    LOW: 'bg-slate-100 text-slate-700 border-slate-200',
    MEDIUM: 'bg-blue-50 text-blue-700 border-blue-200',
    HIGH: 'bg-amber-50 text-amber-700 border-amber-200',
    CRITICAL: 'bg-rose-50 text-rose-700 border-rose-200',
  };

  const handleCompleteClick = async () => {
    if (isCompleted || completing) return;
    setCompleting(true);
    try {
      await onComplete(task.id);
    } finally {
      setCompleting(false);
    }
  };

  const handleQuickCheckIn = async (mins: number) => {
    if (checkingIn) return;
    setCheckingIn(true);
    try {
      await onCheckIn(task.id, mins);
    } finally {
      setCheckingIn(false);
    }
  };

  return (
    <div
      className={`bg-white rounded-2xl border transition-all p-4 relative group ${
        isCompleted
          ? 'border-emerald-200 bg-emerald-50/20 opacity-75'
          : isOverdue
          ? 'border-rose-300 shadow-sm'
          : 'border-slate-200 hover:border-indigo-300 shadow-sm hover:shadow-md'
      }`}
    >
      <div className="flex items-start space-x-3.5">
        {/* Complete Checkbox Button */}
        <button
          onClick={handleCompleteClick}
          disabled={isCompleted || completing}
          className={`w-6 h-6 rounded-lg flex items-center justify-center transition-all mt-0.5 shrink-0 ${
            isCompleted
              ? 'bg-emerald-600 text-white shadow-sm'
              : 'border-2 border-slate-300 hover:border-indigo-600 hover:bg-indigo-50 text-transparent hover:text-indigo-600'
          }`}
          title={isCompleted ? 'Completed' : 'Mark as Complete (+Points)'}
        >
          <Check className="w-4 h-4 stroke-[3]" />
        </button>

        {/* Task Details */}
        <div className="flex-1 min-w-0">
          <div className="flex items-center space-x-2 flex-wrap gap-y-1 mb-1">
            <h3
              className={`text-base font-semibold truncate ${
                isCompleted ? 'line-through text-slate-400' : 'text-slate-900'
              }`}
            >
              {task.title}
            </h3>

            {/* Category Pill */}
            {task.category && (
              <span
                className="px-2 py-0.5 rounded-md text-xs font-medium"
                style={{
                  backgroundColor: `${task.category.colorHex}15`,
                  color: task.category.colorHex,
                }}
              >
                {task.category.name}
              </span>
            )}

            {/* Priority Pill */}
            <span className={`px-2 py-0.5 rounded-md text-[11px] font-medium border ${priorityColors[task.priority]}`}>
              {task.priority}
            </span>

            {/* Difficulty Badge */}
            <span className="flex items-center text-xs text-amber-500 font-medium" title={`Difficulty: Level ${task.difficultyLevel}`}>
              <Zap className="w-3.5 h-3.5 fill-amber-400" />
              <span className="ml-0.5">L{task.difficultyLevel}</span>
            </span>
          </div>

          {task.description && (
            <p className="text-xs text-slate-500 mb-2 line-clamp-2">{task.description}</p>
          )}

          {/* Meta Info: Due Date, Durations */}
          <div className="flex items-center space-x-4 text-xs text-slate-500 flex-wrap gap-y-1">
            {task.dueDateTime && (
              <span className={`flex items-center space-x-1 ${isOverdue && !isCompleted ? 'text-rose-600 font-semibold' : ''}`}>
                {isOverdue && !isCompleted ? <AlertCircle className="w-3.5 h-3.5" /> : <Clock className="w-3.5 h-3.5" />}
                <span>{format(parseISO(task.dueDateTime), 'MMM d, h:mm a')}</span>
              </span>
            )}

            <span>
              Spent: <strong className="text-slate-700">{task.actualDurationMinutes}m</strong>
              {task.estimatedDurationMinutes > 0 && ` / ${task.estimatedDurationMinutes}m est.`}
            </span>

            {!isCompleted && (
              <button
                onClick={() => handleQuickCheckIn(15)}
                disabled={checkingIn}
                className="text-xs text-indigo-600 hover:text-indigo-800 font-medium flex items-center space-x-0.5 bg-indigo-50 px-2 py-0.5 rounded hover:bg-indigo-100 transition-colors"
                title="Add 15 minutes of focus time"
              >
                <Plus className="w-3 h-3" />
                <span>15m Focus</span>
              </button>
            )}
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex items-center space-x-1 opacity-0 group-hover:opacity-100 transition-opacity">
          {!isCompleted && (
            <button
              onClick={() => onEdit(task)}
              className="p-1.5 text-slate-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-md transition-colors"
              title="Edit Task"
            >
              <Edit2 className="w-4 h-4" />
            </button>
          )}
          <button
            onClick={() => onDelete(task.id)}
            className="p-1.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-md transition-colors"
            title="Delete Task"
          >
            <Trash2 className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
};
