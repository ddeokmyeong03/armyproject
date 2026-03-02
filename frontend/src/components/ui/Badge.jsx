import { getCategoryColor, getCategoryLabel } from '../../utils/categories';

export function CategoryBadge({ category }) {
  const colorClass = getCategoryColor(category);
  return (
    <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${colorClass}`}>
      {getCategoryLabel(category)}
    </span>
  );
}

export function TagBadge({ name, onRemove }) {
  return (
    <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-700">
      {name}
      {onRemove && (
        <button
          type="button"
          onClick={onRemove}
          className="text-gray-400 hover:text-gray-600 leading-none"
          aria-label={`${name} 태그 제거`}
        >
          ×
        </button>
      )}
    </span>
  );
}
