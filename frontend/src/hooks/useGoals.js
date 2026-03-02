import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import * as api from '../api/goals';

export function useGoals(planId) {
  return useQuery({
    queryKey: ['goals', planId],
    queryFn: () => api.getGoals(planId),
    enabled: !!planId,
  });
}

export function useCreateGoal(planId) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data) => api.createGoal(planId, data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['goals', planId] }),
  });
}

export function useUpdateGoal(planId) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ goalId, data }) => api.updateGoal(planId, goalId, data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['goals', planId] }),
  });
}

export function useDeleteGoal(planId) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (goalId) => api.deleteGoal(planId, goalId),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['goals', planId] }),
  });
}
