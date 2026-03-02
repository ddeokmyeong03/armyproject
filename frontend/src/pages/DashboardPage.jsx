import { Link } from 'react-router-dom';
import { useDashboard } from '../hooks/useDashboard';
import { useAuthStore } from '../store/authStore';
import { PageSpinner } from '../components/ui/Spinner';
import { CategoryBadge } from '../components/ui/Badge';
import { formatDate, formatDDay } from '../utils/date';

function StatCard({ label, value, sub, colorClass = 'text-military-700' }) {
  return (
    <div className="card flex flex-col gap-1">
      <span className="text-xs text-gray-500">{label}</span>
      <span className={`text-2xl font-bold ${colorClass}`}>{value}</span>
      {sub && <span className="text-xs text-gray-400">{sub}</span>}
    </div>
  );
}

function GoalProgress({ goal }) {
  const pct = Math.min(100, Math.round((goal.doneCount / goal.targetCount) * 100));
  return (
    <div>
      <div className="flex items-center justify-between mb-1">
        <span className="text-sm text-gray-700 truncate">{goal.title}</span>
        <span className="text-xs text-gray-500 shrink-0 ml-2">
          {goal.doneCount}/{goal.targetCount}
        </span>
      </div>
      <div className="h-1.5 bg-gray-100 rounded-full overflow-hidden">
        <div
          className="h-full bg-military-500 rounded-full transition-all"
          style={{ width: `${pct}%` }}
        />
      </div>
    </div>
  );
}

export default function DashboardPage() {
  const user = useAuthStore((s) => s.user);
  const { data, isLoading } = useDashboard();

  if (isLoading) return <PageSpinner />;

  const {
    dDay,
    dischargeDate,
    streak = {},
    weeklyCompletionRate = 0,
    todayTasks = [],
    weeklyGoals = [],
    recentRecords = [],
  } = data ?? {};

  const completedCount = todayTasks.filter((t) => t.done).length;

  return (
    <div className="p-6 max-w-4xl mx-auto">
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-xl font-bold text-gray-900">
          안녕하세요, {user?.nickname ?? ''}님 👋
        </h1>
        {dischargeDate && (
          <p className="text-sm text-gray-500 mt-0.5">
            전역일 {formatDate(dischargeDate)} —{' '}
            <span className="font-medium text-military-600">{formatDDay(dDay)}</span>
          </p>
        )}
      </div>

      {/* Stat cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mb-6">
        <StatCard
          label="연속 달성"
          value={`${streak.current ?? 0}일`}
          sub={`최고 ${streak.max ?? 0}일`}
          colorClass="text-amber-600"
        />
        <StatCard
          label="이번 주 달성률"
          value={`${Math.round(weeklyCompletionRate * 100)}%`}
          colorClass="text-military-700"
        />
        <StatCard
          label="오늘 할 일"
          value={`${completedCount}/${todayTasks.length}`}
          sub="완료"
        />
        <StatCard
          label="주간 목표"
          value={weeklyGoals.length}
          sub="개 목표"
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {/* Today's tasks */}
        <section className="card">
          <div className="flex items-center justify-between mb-3">
            <h2 className="font-semibold text-gray-800">오늘의 할 일</h2>
            <Link to="/plans" className="text-xs text-military-600 hover:underline">
              전체 보기
            </Link>
          </div>
          {todayTasks.length === 0 ? (
            <p className="text-sm text-gray-400">오늘 예정된 할 일이 없습니다.</p>
          ) : (
            <ul className="space-y-2">
              {todayTasks.map((task) => (
                <li key={task.id} className="flex items-center gap-2">
                  <span
                    className={`w-4 h-4 rounded-full border-2 flex items-center justify-center shrink-0 ${
                      task.done
                        ? 'bg-military-500 border-military-500'
                        : 'border-gray-300'
                    }`}
                  >
                    {task.done && (
                      <svg className="w-2.5 h-2.5 text-white" fill="none" viewBox="0 0 10 8">
                        <path stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" d="M1 4l3 3 5-6" />
                      </svg>
                    )}
                  </span>
                  <span className={`text-sm ${task.done ? 'line-through text-gray-400' : 'text-gray-700'}`}>
                    {task.title}
                  </span>
                </li>
              ))}
            </ul>
          )}
        </section>

        {/* Weekly goals */}
        <section className="card">
          <div className="flex items-center justify-between mb-3">
            <h2 className="font-semibold text-gray-800">이번 주 목표</h2>
          </div>
          {weeklyGoals.length === 0 ? (
            <p className="text-sm text-gray-400">이번 주 목표가 없습니다.</p>
          ) : (
            <div className="space-y-3">
              {weeklyGoals.map((goal) => (
                <GoalProgress key={goal.id} goal={goal} />
              ))}
            </div>
          )}
        </section>

        {/* Recent records */}
        <section className="card md:col-span-2">
          <div className="flex items-center justify-between mb-3">
            <h2 className="font-semibold text-gray-800">최근 활동 기록</h2>
            <Link to="/records" className="text-xs text-military-600 hover:underline">
              전체 보기
            </Link>
          </div>
          {recentRecords.length === 0 ? (
            <p className="text-sm text-gray-400">아직 활동 기록이 없습니다.</p>
          ) : (
            <ul className="divide-y divide-gray-100">
              {recentRecords.map((rec) => (
                <li key={rec.id}>
                  <Link
                    to={`/records/${rec.id}`}
                    className="flex items-center justify-between py-2.5 hover:bg-gray-50 -mx-1 px-1 rounded transition-colors"
                  >
                    <div className="flex items-center gap-2 min-w-0">
                      <CategoryBadge category={rec.category} />
                      <span className="text-sm text-gray-700 truncate">{rec.title}</span>
                    </div>
                    <span className="text-xs text-gray-400 shrink-0 ml-2">
                      {formatDate(rec.activityDate)}
                    </span>
                  </Link>
                </li>
              ))}
            </ul>
          )}
        </section>
      </div>
    </div>
  );
}
