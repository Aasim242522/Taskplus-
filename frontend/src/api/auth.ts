import apiClient from './client';
import { AuthResponse } from '../types';

export const authApi = {
  login: async (credentials: { email: string; password: string }): Promise<AuthResponse> => {
    return (await apiClient.post('/auth/login', credentials)) as unknown as AuthResponse;
  },

  register: async (userData: { email: string; password: string; fullName: string; timezone?: string }): Promise<AuthResponse> => {
    return (await apiClient.post('/auth/register', userData)) as unknown as AuthResponse;
  },
};
