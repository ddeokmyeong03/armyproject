import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { usePlan, useDeletePlan } from '../../hooks/usePlans';
import { useGoals, useCreateGoal, useDeleteGoal } from '../../hooks/useGoals';
import { useTasks, useCreateTask, useToggleTask, useDeleteTask } from '../../hooks/useTasks';
import { PageSpinner } from '../../components/ui/Spinner';
import { CategoryBadge } from '../../components/ui/Badge';
import Modal from '../../components/ui/Modal';
import { formatDate, getWeekDays } from '../../utils/date';
import { CATEGORY_OPTIONS } from '../../utils/categories';
import { extractApiError } from '../../utils/apiError';

/* ──────────────────────────── Goals section ─────────────────────────────── */

function GoalItem({ planId, goal }) {
  const deleteGoal = useDeleteGoal(planId);
  const pct = Math.min(100, Math.round((goal.doneCount / goal.targetCount) * 100));
  return (
    <div className="flex items-center gap-3 py-2 group">
      <div className="flex-1 min-w-0">
        <div className="flex items-center gap-2 mb-1">
          <CategoryBadge category={goal.category} />
          <span className="text-sm text-gray-700 truncate">{goal.title}</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="flex-1 h-1.5 bg-gray-100 rounded-full overflow-hidden">
            <div className="h-full bg-military-500 rounded-full" style={{ width: `${pct}%` }} />
          </div>
          <span className="text-xs text-gray-400 shrink-0">
            {goal.doneCount}/{goal.targetCount}
          </span>
        </div>
      </div>
      <button
        onClick={() => deleteGoal.mutate(goal.id)}
        className="opacity-0 group-hover:opacity-100 text-gray-300 hover:text-red-400 transition-opacity text-lg leading-none"
        aria-label="목표 삭제"
      >
        ×
      </button>
    </div>
  );
}

function AddGoalModal({ planId, open, onClose }) {
  const { register, handleSubmit, reset } = useForm({
    defaultValues: { category: 'ETC', targetCount: 1 },
  });
  const createGoal = useCreateGoal(planId);

  function onSubmit(data) {
    createGoal.mutate({ ...data, targetCount: Number(data.targetCount) }, {
      onSuccess: () => { reset(); onClose(); },
    });
  }

  return (
    <Modal open={open} onClose={onClose} title="목표 추가">
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">목표 내용</label>
          <input className="input-base" {...register('title', { required: true })} placeholder="예: TOEIC 공부 5회" />
        </div>
        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">카테고리</label>
            <select className="input-base" {...register('category')}>
              {CATEGORY_OPTIONS.map(({ value, label }) => (
                <option key={value} value={value}>{label}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">목표 횟수</label>
            <input type="number" min="1" className="input-base" {...register('targetCount')} />
          </div>
        </div>
        {createGoal.isError && (
          <p className="text-xs text-red-500">{extractApiError(createGoal.error)}</p>
        )}
        <div className="flex gap-2 justify-end">
          <button type="button" onClick={onClose} className="btn-ghost">취소</button>
          <button type="submit" disabled={createGoal.isPending} className="btn-primary">추가</button>
        </div>
      </form>
    </Modal>
  );
}

/* ──────────────────────────── Tasks section ─────────────────────────────── */

function TaskItem({ planId, task }) {
  const toggle = useToggleTask(planId);
  const deleteTask = useDeleteTask(planId);
  return (
    <li className="flex items-center gap-2 py-1.5 group">
      <button
        onClick={() => toggle.mutate({ taskId: task.id, done: !task.done })}
        className={`w-5 h-5 rounded-full border-2 flex items-center justify-center shrink-0 transition-colors ${
          task.done ? 'bg-military-500 border-military-500' : 'border-gray-300 hover:border-military-400'
        }`}
        aria-label={task.done ? '완료 취소' : '완료'}
      >
        {task.done && (
          <svg className="w-3 h-3 text-white" fill="none" viewBox="0 0 10 8">
            <path stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" d="M1 4l3 3 5-6" />
          </svg>
        )}
      </button>
      <span className={`text-sm flex-1 ${task.done ? 'line-through text-gray-400' : 'text-gray-700'}`}>
        {task.title}
      </span>
      <button
        onClick={() => deleteTask.mutate(task.id)}
        className="opacity-0 group-hover:opacity-100 text-gray-300 hover:text-red-400 transition-opacity text-lg leading-none"
        aria-label="삭제"
      >
        ×
      </button>
    </li>
  );
}

function AddTaskForm({ planId, date }) {
  const { register, handleSubmit, reset } = useForm();
  const createTask = useCreateTask(planId);

  function onSubmit(data) {
    createTask.mutate({ ...data, scheduledDate: date }, {
      onSuccess: () => reset(),
    });
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="flex gap-2 mt-2">
      <input
        className="input-base text-sm flex-1"
        placeholder="할 일 추가..."
        {...register('title', { required: true })}
      />
      <button type="submit" disabled={createTask.isPending} className="btn-primary shrink-0 py-1.5">
        추가
      </button>
    </form>
  );
}

/* ──────────────────────────── Main page ─────────────────────────────────── */

export default function PlanDetailPage() {
  const { planId } = useParams();
  const navigate = useNavigate();
  const [showAddGoal, setShowAddGoal] = useState(false);

  const { data: plan, isLoading: planLoading } = usePlan(planId);
  const { data: goals = [], isLoading: goalsLoading } = useGoals(planId);
  const { data: tasks = [], isLoading: tasksLoading } = useTasks(planId);
  const deletePlan = useDeletePlan();

  if (planLoading || goalsLoading || tasksLoading) return <PageSpinner />;
  if (!plan) return <p className="p-6 text-gray-400">계획을 찾을 수 없습니다.</p>;

  const weekDays = getWeekDays(plan.weekStart);
  const DAY_LABELS = ['월', '화', '수', '목', '금', '토', '일'];

  function handleDelete() {
    if (!window.confirm('이 주간 계획을 삭제하시겠습니까?')) return;
    deletePlan.mutate(plan.id, { onSuccess: () => navigate('/plans') });
  }

  return (
    <div className="p-6 max-w-3xl mx-auto">
      {/* Header */}
      <div className="flex items-start justify-between mb-6">
        <div>
          <button
            onClick={() => navigate('/plans')}
            className="text-sm text-gray-400 hover:text-gray-600 mb-1"
          >
            ← 목록으로
          </button>
          <h1 className="text-xl font-bold text-gray-900">
            {formatDate(plan.weekStart)} 주간 계획
          </h1>
          {plan.memo && <p className="text-sm text-gray-500 mt-1">{plan.memo}</p>}
        </div>
        <button onClick={handleDelete} className="btn-danger text-xs py-1.5">
          삭제
        </button>
      </div>

      {/* Goals */}
      <section className="card mb-4">
        <div className="flex items-center justify-between mb-3">
          <h2 className="font-semibold text-gray-800">주간 목표</h2>
          <button onClick={() => setShowAddGoal(true)} className="text-xs text-military-600 hover:underline">
            + 추가
          </button>
        </div>
        {goals.length === 0 ? (
          <p className="text-sm text-gray-400">목표를 추가해보세요.</p>
        ) : (
          <div className="divide-y divide-gray-100">
            {goals.map((g) => <GoalItem key={g.id} planId={planId} goal={g} />)}
          </div>
        )}
      </section>

      {/* Tasks by day */}
      <section>
        <h2 className="font-semibold text-gray-800 mb-3">요일별 할 일</h2>
        <div className="space-y-3">
          {weekDays.map((date, idx) => {
            const dayTasks = tasks.filter((t) => t.scheduledDate === date);
            return (
              <div key={date} className="card">
                <h3 className="text-sm font-medium text-gray-600 mb-2">
                  {DAY_LABELS[idx]}요일{' '}
                  <span className="text-gray-400 font-normal">{date}</span>
                </h3>
                {dayTasks.length > 0 ? (
                  <ul className="divide-y divide-gray-50">
                    {dayTasks.map((t) => <TaskItem key={t.id} planId={planId} task={t} />)}
                  </ul>
                ) : (
                  <p className="text-xs text-gray-300">할 일 없음</p>
                )}
                <AddTaskForm planId={planId} date={date} />
              </div>
            );
          })}
        </div>
      </section>

      <AddGoalModal planId={planId} open={showAddGoal} onClose={() => setShowAddGoal(false)} />
    </div>
  );
}
