import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { login, register, logout } from '../api/auth';
import { getMe } from '../api/user';
import { useAuthStore } from '../store/authStore';
import { queryClient } from '../utils/queryClient';

export function useLogin() {
  const { setAuth, setUser } = useAuthStore();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: login,
    onSuccess: async (tokens) => {
      setAuth(tokens, null);
      try {
        const user = await getMe();
        setUser(user);
      } catch {}
      navigate('/dashboard');
    },
  });
}

export function useRegister() {
  const { setAuth, setUser } = useAuthStore();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: register,
    onSuccess: async (tokens) => {
      setAuth(tokens, null);
      try {
        const user = await getMe();
        setUser(user);
      } catch {}
      navigate('/dashboard');
    },
  });
}

export function useLogout() {
  const { clearAuth } = useAuthStore();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: logout,
    onSettled: () => {
      clearAuth();
      queryClient.clear();
      navigate('/login');
    },
  });
}
