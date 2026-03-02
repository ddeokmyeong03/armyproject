import client from './client';

export async function getPlans() {
  const res = await client.get('/plans');
  return res.data.data;
}

export async function createPlan(data) {
  const res = await client.post('/plans', data);
  return res.data.data;
}

export async function getPlan(planId) {
  const res = await client.get(`/plans/${planId}`);
  return res.data.data;
}

export async function updatePlan(planId, data) {
  const res = await client.patch(`/plans/${planId}`, data);
  return res.data.data;
}

export async function deletePlan(planId) {
  await client.delete(`/plans/${planId}`);
}
