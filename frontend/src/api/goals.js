import client from './client';

export async function getGoals(planId) {
  const res = await client.get(`/plans/${planId}/goals`);
  return res.data.data;
}

export async function createGoal(planId, data) {
  const res = await client.post(`/plans/${planId}/goals`, data);
  return res.data.data;
}

export async function updateGoal(planId, goalId, data) {
  const res = await client.patch(`/plans/${planId}/goals/${goalId}`, data);
  return res.data.data;
}

export async function deleteGoal(planId, goalId) {
  await client.delete(`/plans/${planId}/goals/${goalId}`);
}
