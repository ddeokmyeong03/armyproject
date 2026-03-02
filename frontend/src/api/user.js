import client from './client';

export async function getMe() {
  const res = await client.get('/me');
  return res.data.data;
}

export async function updateMe(data) {
  const res = await client.patch('/me', data);
  return res.data.data;
}

export async function getStreak() {
  const res = await client.get('/me/streak');
  return res.data.data;
}

export async function getMyRoadmaps() {
  const res = await client.get('/me/roadmaps');
  return res.data.data;
}

export async function applyRoadmap(data) {
  const res = await client.post('/me/roadmaps', data);
  return res.data.data;
}

export async function updateMyRoadmap(id, status) {
  const res = await client.patch(`/me/roadmaps/${id}`, { status });
  return res.data.data;
}
