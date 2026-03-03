import { useParams, useNavigate } from 'react-router-dom';
import { useRoadmap, useMyRoadmaps, useApplyRoadmap } from '../../hooks/useRoadmaps';
import { PageSpinner } from '../../components/ui/Spinner';
import { CategoryBadge } from '../../components/ui/Badge';
import { extractApiError } from '../../utils/apiError';

export default function RoadmapDetailPage() {
  const { roadmapId } = useParams();
  const navigate = useNavigate();

  const { data: roadmap, isLoading } = useRoadmap(roadmapId);
  const { data: myRoadmaps = [] } = useMyRoadmaps();
  const apply = useApplyRoadmap();

  if (isLoading) return <PageSpinner />;
  if (!roadmap) return <p className="p-6 text-gray-400">로드맵을 찾을 수 없습니다.</p>;

  const alreadyApplied = myRoadmaps.some((ur) => ur.roadmap?.id === roadmap.id);

  function handleApply() {
    const today = new Date().toISOString().slice(0, 10);
    apply.mutate({ roadmap_id: roadmap.id, started_at: today }, {
      onSuccess: () => navigate('/roadmaps'),
    });
  }

  return (
    <div className="p-6 max-w-2xl mx-auto">
      <button
        onClick={() => navigate('/roadmaps')}
        className="text-sm text-gray-400 hover:text-gray-600 mb-4"
      >
        ← 목록으로
      </button>

      {/* Header */}
      <div className="card mb-4">
        <div className="flex items-start justify-between gap-3">
          <div>
            <div className="flex items-center gap-2 mb-2 flex-wrap">
              <CategoryBadge category={roadmap.category} />
              <span className="text-xs text-gray-400">{roadmap.durationWeeks}주 과정</span>
            </div>
            <h1 className="text-lg font-bold text-gray-900 mb-1">{roadmap.title}</h1>
            {roadmap.description && (
              <p className="text-sm text-gray-600">{roadmap.description}</p>
            )}
          </div>
          {!alreadyApplied ? (
            <button
              onClick={handleApply}
              disabled={apply.isPending}
              className="btn-primary shrink-0"
            >
              시작하기
            </button>
          ) : (
            <span className="text-xs text-green-600 bg-green-50 px-3 py-1 rounded-full font-medium shrink-0">
              진행 중
            </span>
          )}
        </div>
        {apply.isError && (
          <p className="text-xs text-red-500 mt-2">{extractApiError(apply.error)}</p>
        )}
      </div>

      {/* Weeks */}
      <section>
        <h2 className="font-semibold text-gray-800 mb-3">주차별 커리큘럼</h2>
        {roadmap.weeks?.length > 0 ? (
          <div className="space-y-2">
            {[...roadmap.weeks]
              .sort((a, b) => a.weekNumber - b.weekNumber)
              .map((week) => (
                <div key={week.id} className="card flex gap-3">
                  <div className="w-10 h-10 rounded-full bg-military-100 text-military-700 flex items-center justify-center font-bold text-sm shrink-0">
                    {week.weekNumber}
                  </div>
                  <div className="flex-1 min-w-0">
                    <h3 className="text-sm font-medium text-gray-800">{week.goalTitle}</h3>
                    {week.taskTitles?.length > 0 && (
                      <p className="text-xs text-gray-500 mt-0.5">{week.taskTitles.join(', ')}</p>
                    )}
                  </div>
                </div>
              ))}
          </div>
        ) : (
          <p className="text-sm text-gray-400">주차 정보가 없습니다.</p>
        )}
      </section>
    </div>
  );
}
