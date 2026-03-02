import client from './client';

export async function getTags() {
  const res = await client.get('/tags');
  return res.data.data;
}

export async function createTag(name) {
  const res = await client.post('/tags', { name });
  return res.data.data;
}

export async function deleteTag(tagId) {
  await client.delete(`/tags/${tagId}`);
}
