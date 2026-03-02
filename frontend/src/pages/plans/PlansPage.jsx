import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { usePlans, useCreatePlan } from '../../hooks/usePlans';
import { PageSpinner } from '../../components/ui/Spinner';
import Modal from '../../components/ui/Modal';
import { formatDate, getMondayOfCurrentWeek } from '../../utils/date';
import { extractApiError } from '../../utils/apiError';

function PlanCard({ plan }) {
  return (
    <Link
      to={`/plans/${plan.id}`}
      className="card block hover:shadow-md transition-shadow"
    >
      <div className="flex items-start justify-between">
        <div>
          <p className="text-xs text-gray-400 mb-0.5">주간 계획</p>
          <h3 className="font-semibold text-gray-800">
            {formatDate(plan.weekStart)} 주
          </h3>
          {plan.memo && (
            <p className="text-sm text-gray-500 mt-1 line-clamp-2">{plan.memo}</p>
          )}
        </div>
        <span className="text-xs text-military-600 font-medium shrink-0 ml-2">
          보기 →
        </span>
      </div>
    </Link>
  );
}

function CreatePlanModal({ open, onClose }) {
  const { register, handleSubmit, reset } = useForm({
    defaultValues: { weekStart: getMondayOfCurrentWeek() },
  });
  const createPlan = useCreatePlan();

  function onSubmit(data) {
    createPlan.mutate({ week_start: data.weekStart, memo: data.memo }, {
      onSuccess: () => {
        reset();
        onClose();
      },
    });
  }

  return (
    <Modal open={open} onClose={onClose} title="새 주간 계획 만들기">
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            시작일 (월요일)
          </label>
          <input
            type="date"
            className="input-base"
            {...register('weekStart', { required: true })}
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            메모 (선택)
          </label>
          <textarea
            className="input-base resize-none"
            rows={3}
            placeholder="이번 주 계획에 대한 메모..."
            {...register('memo')}
          />
        </div>

        {createPlan.isError && (
          <p className="text-xs text-red-500">{extractApiError(createPlan.error)}</p>
        )}

        <div className="flex gap-2 justify-end">
          <button type="button" onClick={onClose} className="btn-ghost">
            취소
          </button>
          <button type="submit" disabled={createPlan.isPending} className="btn-primary">
            만들기
          </button>
        </div>
      </form>
    </Modal>
  );
}

export default function PlansPage() {
  const [showCreate, setShowCreate] = useState(false);
  const { data: plans = [], isLoading } = usePlans();

  if (isLoading) return <PageSpinner />;

  return (
    <div className="p-6 max-w-2xl mx-auto">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-xl font-bold text-gray-900">주간 계획</h1>
        <button onClick={() => setShowCreate(true)} className="btn-primary">
          + 새 계획
        </button>
      </div>

      {plans.length === 0 ? (
        <div className="text-center py-16 text-gray-400">
          <p className="text-lg mb-2">아직 주간 계획이 없습니다</p>
          <button onClick={() => setShowCreate(true)} className="btn-primary mt-2">
            첫 계획 만들기
          </button>
        </div>
      ) : (
        <div className="space-y-3">
          {[...plans]
            .sort((a, b) => b.weekStart.localeCompare(a.weekStart))
            .map((plan) => (
              <PlanCard key={plan.id} plan={plan} />
            ))}
        </div>
      )}

      <CreatePlanModal open={showCreate} onClose={() => setShowCreate(false)} />
    </div>
  );
}
