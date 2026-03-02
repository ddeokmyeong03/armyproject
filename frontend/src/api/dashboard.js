import client from './client';

export async function getDashboard() {
  const res = await client.get('/dashboard');
  return res.data.data;
}
