import client from './client';

export async function getTasks(planId, date) {
  const res = await client.get(`/plans/${planId}/tasks`, {
    params: date ? { date } : undefined,
  });
  return res.data.data;
}

export async function createTask(planId, data) {
  const res = await client.post(`/plans/${planId}/tasks`, data);
  return res.data.data;
}

export async function updateTask(planId, taskId, data) {
  const res = await client.patch(`/plans/${planId}/tasks/${taskId}`, data);
  return res.data.data;
}

export async function deleteTask(planId, taskId) {
  await client.delete(`/plans/${planId}/tasks/${taskId}`);
}

export async function toggleTask(planId, taskId, done) {
  const res = await client.patch(`/plans/${planId}/tasks/${taskId}/toggle`, { done });
  return res.data.data;
}
