import api from './api';
import {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  User,
  UpdateProfileRequest,
  ChangePasswordRequest,
  ForgotPasswordRequest,
  ResetPasswordRequest,
} from '../types/user.types';

export const authService = {
  login: async (credentials: LoginRequest): Promise<LoginResponse> => {
    return await api.post<LoginResponse>('/auth/login', credentials);
  },

  register: async (userData: RegisterRequest): Promise<{ message: string }> => {
    return await api.post<{ message: string }>('/auth/register', userData);
  },

  logout: async (): Promise<void> => {
    await api.post('/auth/logout');
  },

  getCurrentUser: async (): Promise<User> => {
    return await api.get<User>('/auth/me');
  },

  updateProfile: async (userData: UpdateProfileRequest): Promise<User> => {
    return await api.put<User>('/auth/profile', userData);
  },

  changePassword: async (passwordData: ChangePasswordRequest): Promise<void> => {
    await api.put('/auth/change-password', passwordData);
  },

  forgotPassword: async (emailData: ForgotPasswordRequest): Promise<void> => {
    await api.post('/auth/forgot-password', emailData);
  },

  resetPassword: async (resetData: ResetPasswordRequest): Promise<void> => {
    await api.post('/auth/reset-password', resetData);
  },

  refreshToken: async (): Promise<{ token: string; refreshToken: string }> => {
    const refreshToken = localStorage.getItem('refreshToken');
    return await api.post<{ token: string; refreshToken: string }>('/auth/refresh-token', { refreshToken });
  },
};
