import client from './client';

export async function getRecords(params) {
  const res = await client.get('/records', { params });
  // Returns full response (data[] + meta) for pagination
  return res.data;
}

export async function createRecord(data) {
  const res = await client.post('/records', data);
  return res.data.data;
}

export async function getRecord(recordId) {
  const res = await client.get(`/records/${recordId}`);
  return res.data.data;
}

export async function updateRecord(recordId, data) {
  const res = await client.patch(`/records/${recordId}`, data);
  return res.data.data;
}

export async function deleteRecord(recordId) {
  await client.delete(`/records/${recordId}`);
}

export async function createStar(recordId, data) {
  const res = await client.post(`/records/${recordId}/star`, data);
  return res.data.data;
}

export async function getStar(recordId) {
  const res = await client.get(`/records/${recordId}/star`);
  return res.data.data;
}

export async function updateStar(recordId, data) {
  const res = await client.patch(`/records/${recordId}/star`, data);
  return res.data.data;
}
