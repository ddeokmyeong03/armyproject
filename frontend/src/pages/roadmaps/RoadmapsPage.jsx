import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useRoadmaps, useMyRoadmaps, useUpdateMyRoadmap } from '../../hooks/useRoadmaps';
import { PageSpinner } from '../../components/ui/Spinner';
import { CategoryBadge } from '../../components/ui/Badge';
import { CATEGORY_OPTIONS } from '../../utils/categories';

const STATUS_LABELS = {
  IN_PROGRESS: { label: '진행 중', color: 'text-blue-600 bg-blue-50' },
  COMPLETED:   { label: '완료',   color: 'text-green-600 bg-green-50' },
  PAUSED:      { label: '일시정지', color: 'text-gray-500 bg-gray-50' },
};

function RoadmapCard({ roadmap }) {
  return (
    <Link
      to={`/roadmaps/${roadmap.id}`}
      className="card block hover:shadow-md transition-shadow"
    >
      <div className="flex items-start gap-3">
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 mb-1 flex-wrap">
            <CategoryBadge category={roadmap.category} />
            <span className="text-xs text-gray-400">{roadmap.totalWeeks}주 과정</span>
          </div>
          <h3 className="font-semibold text-gray-800 text-sm">{roadmap.title}</h3>
          {roadmap.description && (
            <p className="text-xs text-gray-500 mt-0.5 line-clamp-2">{roadmap.description}</p>
          )}
        </div>
        <span className="text-xs text-military-600 shrink-0">보기 →</span>
      </div>
    </Link>
  );
}

function MyRoadmapItem({ ur }) {
  const update = useUpdateMyRoadmap();
  const statusInfo = STATUS_LABELS[ur.status] ?? STATUS_LABELS.IN_PROGRESS;

  return (
    <div className="card flex items-center justify-between gap-3">
      <div className="min-w-0">
        <div className="flex items-center gap-2 mb-0.5">
          <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${statusInfo.color}`}>
            {statusInfo.label}
          </span>
          <span className="text-sm font-medium text-gray-800 truncate">
            {ur.roadmap?.title ?? '로드맵'}
          </span>
        </div>
        <p className="text-xs text-gray-400">{ur.currentWeek ?? 1}주차 진행 중</p>
      </div>
      <select
        value={ur.status}
        onChange={(e) => update.mutate({ id: ur.id, status: e.target.value })}
        className="text-xs border border-gray-200 rounded px-2 py-1 text-gray-600 shrink-0"
      >
        <option value="IN_PROGRESS">진행 중</option>
        <option value="COMPLETED">완료</option>
        <option value="PAUSED">일시정지</option>
      </select>
    </div>
  );
}

export default function RoadmapsPage() {
  const [filterCategory, setFilterCategory] = useState('');
  const { data: roadmaps = [], isLoading } = useRoadmaps(filterCategory || undefined);
  const { data: myRoadmaps = [] } = useMyRoadmaps();

  if (isLoading) return <PageSpinner />;

  return (
    <div className="p-6 max-w-3xl mx-auto">
      <h1 className="text-xl font-bold text-gray-900 mb-6">로드맵</h1>

      {/* My roadmaps */}
      {myRoadmaps.length > 0 && (
        <section className="mb-8">
          <h2 className="font-semibold text-gray-700 mb-3">내 로드맵</h2>
          <div className="space-y-2">
            {myRoadmaps.map((ur) => <MyRoadmapItem key={ur.id} ur={ur} />)}
          </div>
        </section>
      )}

      {/* Template roadmaps */}
      <section>
        <h2 className="font-semibold text-gray-700 mb-3">로드맵 탐색</h2>

        {/* Category filter */}
        <div className="flex gap-2 flex-wrap mb-4">
          <button
            onClick={() => setFilterCategory('')}
            className={`px-3 py-1 rounded-full text-xs font-medium transition-colors ${
              filterCategory === ''
                ? 'bg-military-500 text-white'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
            }`}
          >
            전체
          </button>
          {CATEGORY_OPTIONS.map(({ value, label }) => (
            <button
              key={value}
              onClick={() => setFilterCategory(value)}
              className={`px-3 py-1 rounded-full text-xs font-medium transition-colors ${
                filterCategory === value
                  ? 'bg-military-500 text-white'
                  : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
              }`}
            >
              {label}
            </button>
          ))}
        </div>

        {roadmaps.length === 0 ? (
          <p className="text-sm text-gray-400 py-8 text-center">등록된 로드맵이 없습니다.</p>
        ) : (
          <div className="grid gap-3 sm:grid-cols-2">
            {roadmaps.map((rm) => <RoadmapCard key={rm.id} roadmap={rm} />)}
          </div>
        )}
      </section>
    </div>
  );
}
