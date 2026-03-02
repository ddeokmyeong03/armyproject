export function extractApiError(error) {
  return (
    error?.response?.data?.error?.message ??
    error?.message ??
    '알 수 없는 오류가 발생했습니다.'
  );
}
