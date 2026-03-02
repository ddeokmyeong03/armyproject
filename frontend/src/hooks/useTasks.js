import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import * as api from '../api/tasks';

export function useTasks(planId, date) {
  return useQuery({
    queryKey: ['tasks', planId, date],
    queryFn: () => api.getTasks(planId, date),
    enabled: !!planId,
  });
}

export function useCreateTask(planId) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data) => api.createTask(planId, data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['tasks', planId] }),
  });
}

export function useUpdateTask(planId) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ taskId, data }) => api.updateTask(planId, taskId, data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['tasks', planId] }),
  });
}

export function useDeleteTask(planId) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (taskId) => api.deleteTask(planId, taskId),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['tasks', planId] }),
  });
}

export function useToggleTask(planId) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ taskId, done }) => api.toggleTask(planId, taskId, done),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['tasks', planId] });
      qc.invalidateQueries({ queryKey: ['dashboard'] });
    },
  });
}
