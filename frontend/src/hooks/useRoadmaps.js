import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import * as roadmapApi from '../api/roadmaps';
import * as userApi from '../api/user';

export function useRoadmaps(category) {
  return useQuery({
    queryKey: ['roadmaps', category],
    queryFn: () => roadmapApi.getRoadmaps(category),
  });
}

export function useRoadmap(roadmapId) {
  return useQuery({
    queryKey: ['roadmaps', roadmapId],
    queryFn: () => roadmapApi.getRoadmap(roadmapId),
    enabled: !!roadmapId,
  });
}

export function useMyRoadmaps() {
  return useQuery({
    queryKey: ['myRoadmaps'],
    queryFn: userApi.getMyRoadmaps,
  });
}

export function useApplyRoadmap() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: userApi.applyRoadmap,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['myRoadmaps'] }),
  });
}

export function useUpdateMyRoadmap() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, status }) => userApi.updateMyRoadmap(id, status),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['myRoadmaps'] }),
  });
}
