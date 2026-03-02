import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import * as api from '../api/records';

export function useRecords(params) {
  return useQuery({
    queryKey: ['records', params],
    queryFn: () => api.getRecords(params),
  });
}

export function useRecord(recordId) {
  return useQuery({
    queryKey: ['records', recordId],
    queryFn: () => api.getRecord(recordId),
    enabled: !!recordId,
  });
}

export function useCreateRecord() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.createRecord,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['records'] }),
  });
}

export function useUpdateRecord() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ recordId, data }) => api.updateRecord(recordId, data),
    onSuccess: (_, { recordId }) => {
      qc.invalidateQueries({ queryKey: ['records'] });
      qc.invalidateQueries({ queryKey: ['records', recordId] });
    },
  });
}

export function useDeleteRecord() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.deleteRecord,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['records'] }),
  });
}

export function useRecordStar(recordId) {
  return useQuery({
    queryKey: ['star', recordId],
    queryFn: () => api.getStar(recordId),
    enabled: !!recordId,
    retry: false,
  });
}

export function useCreateStar() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ recordId, data }) => api.createStar(recordId, data),
    onSuccess: (_, { recordId }) => {
      qc.invalidateQueries({ queryKey: ['star', recordId] });
    },
  });
}

export function useUpdateStar() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ recordId, data }) => api.updateStar(recordId, data),
    onSuccess: (_, { recordId }) => {
      qc.invalidateQueries({ queryKey: ['star', recordId] });
    },
  });
}
