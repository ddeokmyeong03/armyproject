import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getMe, updateMe } from '../api/user';
import { useAuthStore } from '../store/authStore';

export function useMe() {
  return useQuery({
    queryKey: ['me'],
    queryFn: getMe,
  });
}

export function useUpdateMe() {
  const qc = useQueryClient();
  const setUser = useAuthStore((s) => s.setUser);
  return useMutation({
    mutationFn: updateMe,
    onSuccess: (user) => {
      setUser(user);
      qc.invalidateQueries({ queryKey: ['me'] });
    },
  });
}
