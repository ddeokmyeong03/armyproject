import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useRecords } from '../../hooks/useRecords';
import { PageSpinner } from '../../components/ui/Spinner';
import { CategoryBadge, TagBadge } from '../../components/ui/Badge';
import { formatDate } from '../../utils/date';
import { CATEGORY_OPTIONS } from '../../utils/categories';

function RecordCard({ record }) {
  return (
    <Link
      to={`/records/${record.id}`}
      className="card block hover:shadow-md transition-shadow"
    >
      <div className="flex items-start justify-between gap-2">
        <div className="min-w-0 flex-1">
          <div className="flex items-center gap-2 mb-1 flex-wrap">
            <CategoryBadge category={record.category} />
            <h3 className="text-sm font-semibold text-gray-800 truncate">{record.title}</h3>
          </div>
          {record.content && (
            <p className="text-xs text-gray-500 line-clamp-2 mb-2">{record.content}</p>
          )}
          {record.tags?.length > 0 && (
            <div className="flex flex-wrap gap-1">
              {record.tags.map((tag) => (
                <TagBadge key={tag.id} name={tag.name} />
              ))}
            </div>
          )}
        </div>
        <span className="text-xs text-gray-400 shrink-0">{formatDate(record.activityDate)}</span>
      </div>
    </Link>
  );
}

export default function RecordsPage() {
  const [category, setCategory] = useState('');
  const { data, isLoading } = useRecords(category ? { category } : undefined);

  const records = data?.data ?? [];

  if (isLoading) return <PageSpinner />;

  return (
    <div className="p-6 max-w-2xl mx-auto">
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-xl font-bold text-gray-900">활동 기록</h1>
        <Link to="/records/new" className="btn-primary">
          + 새 기록
        </Link>
      </div>

      {/* Filter */}
      <div className="flex gap-2 flex-wrap mb-4">
        <button
          onClick={() => setCategory('')}
          className={`px-3 py-1 rounded-full text-xs font-medium transition-colors ${
            category === ''
              ? 'bg-military-500 text-white'
              : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
          }`}
        >
          전체
        </button>
        {CATEGORY_OPTIONS.map(({ value, label }) => (
          <button
            key={value}
            onClick={() => setCategory(value)}
            className={`px-3 py-1 rounded-full text-xs font-medium transition-colors ${
              category === value
                ? 'bg-military-500 text-white'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
            }`}
          >
            {label}
          </button>
        ))}
      </div>

      {records.length === 0 ? (
        <div className="text-center py-16 text-gray-400">
          <p className="text-lg mb-2">아직 활동 기록이 없습니다</p>
          <Link to="/records/new" className="btn-primary mt-2 inline-flex">
            첫 기록 작성
          </Link>
        </div>
      ) : (
        <div className="space-y-3">
          {records.map((record) => (
            <RecordCard key={record.id} record={record} />
          ))}
        </div>
      )}
    </div>
  );
}
