import client from './client';

export async function register(data) {
  const res = await client.post('/auth/register', data);
  return res.data.data;
}

export async function login(data) {
  const res = await client.post('/auth/login', data);
  return res.data.data;
}

export async function refresh(refreshToken) {
  const res = await client.post('/auth/refresh', { refresh_token: refreshToken });
  return res.data.data;
}

export async function logout() {
  await client.post('/auth/logout');
}
