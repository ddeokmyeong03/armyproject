import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import * as api from '../api/tags';

export function useTags() {
  return useQuery({
    queryKey: ['tags'],
    queryFn: api.getTags,
  });
}

export function useCreateTag() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.createTag,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['tags'] }),
  });
}

export function useDeleteTag() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.deleteTag,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['tags'] }),
  });
}
