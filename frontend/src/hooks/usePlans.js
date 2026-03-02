import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import * as api from '../api/plans';

export function usePlans() {
  return useQuery({ queryKey: ['plans'], queryFn: api.getPlans });
}

export function usePlan(planId) {
  return useQuery({
    queryKey: ['plans', planId],
    queryFn: () => api.getPlan(planId),
    enabled: !!planId,
  });
}

export function useCreatePlan() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.createPlan,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['plans'] }),
  });
}

export function useUpdatePlan() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ planId, data }) => api.updatePlan(planId, data),
    onSuccess: (_, { planId }) => {
      qc.invalidateQueries({ queryKey: ['plans'] });
      qc.invalidateQueries({ queryKey: ['plans', planId] });
    },
  });
}

export function useDeletePlan() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.deletePlan,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['plans'] }),
  });
}
