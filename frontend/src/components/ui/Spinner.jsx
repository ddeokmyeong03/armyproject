export default function Spinner({ className = '' }) {
  return (
    <div
      className={`inline-block h-5 w-5 animate-spin rounded-full border-2 border-gray-300 border-t-military-500 ${className}`}
      role="status"
      aria-label="로딩 중"
    />
  );
}

export function PageSpinner() {
  return (
    <div className="flex items-center justify-center min-h-[200px]">
      <Spinner className="h-8 w-8" />
    </div>
  );
}
