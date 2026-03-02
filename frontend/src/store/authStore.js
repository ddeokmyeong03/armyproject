import { create } from 'zustand';

const REFRESH_KEY = 'selfdev_refresh_token';

export const useAuthStore = create((set) => ({
  accessToken: null,
  user: null,

  setAuth: (tokens, user) => {
    localStorage.setItem(REFRESH_KEY, tokens.refreshToken);
    set({ accessToken: tokens.accessToken, user });
  },

  updateTokens: (tokens) => {
    localStorage.setItem(REFRESH_KEY, tokens.refreshToken);
    set({ accessToken: tokens.accessToken });
  },

  setUser: (user) => set({ user }),

  clearAuth: () => {
    localStorage.removeItem(REFRESH_KEY);
    set({ accessToken: null, user: null });
  },

  getRefreshToken: () => localStorage.getItem(REFRESH_KEY),
}));
