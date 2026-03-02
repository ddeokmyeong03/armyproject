export const CATEGORIES = {
  CERT:      { label: '자격증', color: 'bg-blue-100 text-blue-700',    dot: 'bg-blue-500' },
  ENGLISH:   { label: '영어',   color: 'bg-indigo-100 text-indigo-700', dot: 'bg-indigo-500' },
  FITNESS:   { label: '체력',   color: 'bg-red-100 text-red-700',      dot: 'bg-red-500' },
  READING:   { label: '독서',   color: 'bg-amber-100 text-amber-700',  dot: 'bg-amber-500' },
  PORTFOLIO: { label: '포트폴리오', color: 'bg-purple-100 text-purple-700', dot: 'bg-purple-500' },
  ETC:       { label: '기타',   color: 'bg-gray-100 text-gray-700',    dot: 'bg-gray-400' },
};

export function getCategoryLabel(category) {
  return CATEGORIES[category]?.label ?? category;
}

export function getCategoryColor(category) {
  return CATEGORIES[category]?.color ?? 'bg-gray-100 text-gray-700';
}

export const CATEGORY_OPTIONS = Object.entries(CATEGORIES).map(([value, { label }]) => ({
  value,
  label,
}));
