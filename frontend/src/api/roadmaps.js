import client from './client';

export async function getRoadmaps(category) {
  const res = await client.get('/roadmaps', {
    params: category ? { category } : undefined,
  });
  return res.data.data;
}

export async function getRoadmap(roadmapId) {
  const res = await client.get(`/roadmaps/${roadmapId}`);
  return res.data.data;
}
