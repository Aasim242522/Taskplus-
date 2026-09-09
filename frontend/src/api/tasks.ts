import apiClient from './client';
import { Category, CreateTaskInput, Task, TaskCompletionResult } from '../types';

export const tasksApi = {
  getTasks: async (params?: {
    status?: string;
    categoryId?: string;
    priority?: string;
    page?: number;
    size?: number;
  }): Promise<{ content: Task[]; totalElements: number; totalPages: number }> => {
    return (await apiClient.get('/tasks', { params })) as unknown as {
      content: Task[];
      totalElements: number;
      totalPages: number;
    };
  },

  getTaskById: async (id: string): Promise<Task> => {
    return (await apiClient.get(`/tasks/${id}`)) as unknown as Task;
  },

  createTask: async (input: CreateTaskInput): Promise<Task> => {
    return (await apiClient.post('/tasks', input)) as unknown as Task;
  },

  updateTask: async (id: string, input: Partial<CreateTaskInput> & { status?: string }): Promise<Task> => {
    return (await apiClient.put(`/tasks/${id}`, input)) as unknown as Task;
  },

  deleteTask: async (id: string): Promise<void> => {
    await apiClient.delete(`/tasks/${id}`);
  },

  checkIn: async (id: string, additionalMinutes: number, note?: string): Promise<Task> => {
    return (await apiClient.post(`/tasks/${id}/check-in`, { additionalMinutes, note })) as unknown as Task;
  },

  completeTask: async (id: string): Promise<TaskCompletionResult> => {
    return (await apiClient.post(`/tasks/${id}/complete`)) as unknown as TaskCompletionResult;
  },

  getCategories: async (): Promise<Category[]> => {
    return (await apiClient.get('/categories')) as unknown as Category[];
  },

  createCategory: async (category: { name: string; colorHex: string; icon?: string }): Promise<Category> => {
    return (await apiClient.post('/categories', category)) as unknown as Category;
  },
};
