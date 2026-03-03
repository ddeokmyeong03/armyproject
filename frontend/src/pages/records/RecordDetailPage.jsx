import { useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { useRecord, useDeleteRecord, useRecordStar, useCreateStar, useUpdateStar } from '../../hooks/useRecords';
import { PageSpinner } from '../../components/ui/Spinner';
import { CategoryBadge, TagBadge } from '../../components/ui/Badge';
import { formatDate } from '../../utils/date';
import { extractApiError } from '../../utils/apiError';

function StarForm({ recordId, existing }) {
  const { register, handleSubmit, formState: { errors } } = useForm({
    defaultValues: existing
      ? { situation: existing.situation, task_desc: existing.taskDesc, action: existing.action, result: existing.result }
      : {},
  });
  const createStar = useCreateStar();
  const updateStar = useUpdateStar();
  const isEdit = !!existing;

  function onSubmit(data) {
    const payload = { situation: data.situation, task_desc: data.task_desc, action: data.action, result: data.result };
    if (isEdit) {
      updateStar.mutate({ recordId, data: payload });
    } else {
      createStar.mutate({ recordId, data: payload });
    }
  }

  const isPending = createStar.isPending || updateStar.isPending;
  const error = createStar.error || updateStar.error;

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      {[
        { field: 'situation', label: 'S — Situation (상황)', placeholder: '어떤 상황이었나요?' },
        { field: 'task_desc', label: 'T — Task (역할)', placeholder: '내가 맡은 역할은 무엇이었나요?' },
        { field: 'action', label: 'A — Action (행동)', placeholder: '어떤 행동을 했나요?' },
        { field: 'result', label: 'R — Result (결과)', placeholder: '어떤 결과가 있었나요?' },
      ].map(({ field, label, placeholder }) => (
        <div key={field}>
          <label className="block text-sm font-semibold text-military-700 mb-1">{label}</label>
          <textarea
            className="input-base resize-none"
            rows={3}
            placeholder={placeholder}
            {...register(field, { required: '필수 항목입니다' })}
          />
          {errors[field] && <p className="text-xs text-red-500 mt-0.5">{errors[field].message}</p>}
        </div>
      ))}
      {error && <p className="text-xs text-red-500">{extractApiError(error)}</p>}
      <div className="flex justify-end">
        <button type="submit" disabled={isPending} className="btn-primary">
          {isEdit ? 'STAR 수정' : 'STAR 작성'}
        </button>
      </div>
    </form>
  );
}

export default function RecordDetailPage() {
  const { recordId } = useParams();
  const navigate = useNavigate();
  const [showStarForm, setShowStarForm] = useState(false);

  const { data: record, isLoading } = useRecord(recordId);
  const { data: star } = useRecordStar(recordId);
  const deleteRecord = useDeleteRecord();

  if (isLoading) return <PageSpinner />;
  if (!record) return <p className="p-6 text-gray-400">기록을 찾을 수 없습니다.</p>;

  function handleDelete() {
    if (!window.confirm('이 기록을 삭제하시겠습니까?')) return;
    deleteRecord.mutate(record.id, { onSuccess: () => navigate('/records') });
  }

  return (
    <div className="p-6 max-w-2xl mx-auto">
      {/* Header */}
      <button
        onClick={() => navigate('/records')}
        className="text-sm text-gray-400 hover:text-gray-600 mb-4"
      >
        ← 목록으로
      </button>

      <div className="card mb-4">
        <div className="flex items-start justify-between gap-3 mb-3">
          <div className="min-w-0">
            <div className="flex items-center gap-2 mb-1 flex-wrap">
              <CategoryBadge category={record.category} />
              <span className="text-xs text-gray-400">{formatDate(record.activityDate)}</span>
            </div>
            <h1 className="text-lg font-bold text-gray-900">{record.title}</h1>
          </div>
          <div className="flex gap-2 shrink-0">
            <Link to={`/records/${recordId}/edit`} className="btn-ghost text-xs py-1.5">
              수정
            </Link>
            <button onClick={handleDelete} className="btn-danger text-xs py-1.5">
              삭제
            </button>
          </div>
        </div>

        {record.content && (
          <p className="text-sm text-gray-700 whitespace-pre-wrap mb-3">{record.content}</p>
        )}

        {record.tags?.length > 0 && (
          <div className="flex flex-wrap gap-1">
            {record.tags.map((tag) => <TagBadge key={tag.id} name={tag.name} />)}
          </div>
        )}
      </div>

      {/* STAR */}
      <section className="card">
        <div className="flex items-center justify-between mb-4">
          <h2 className="font-semibold text-gray-800">STAR 기록</h2>
          {star && !showStarForm && (
            <button
              onClick={() => setShowStarForm(true)}
              className="text-xs text-military-600 hover:underline"
            >
              수정
            </button>
          )}
        </div>

        {star && !showStarForm ? (
          <div className="space-y-4">
            {[
              { label: 'S — Situation', value: star.situation },
              { label: 'T — Task', value: star.taskDesc },
              { label: 'A — Action', value: star.action },
              { label: 'R — Result', value: star.result },
            ].map(({ label, value }) => (
              <div key={label}>
                <p className="text-xs font-semibold text-military-700 mb-1">{label}</p>
                <p className="text-sm text-gray-700 whitespace-pre-wrap">{value}</p>
              </div>
            ))}
          </div>
        ) : (
          <StarForm
            recordId={recordId}
            existing={showStarForm ? star : undefined}
          />
        )}
      </section>
    </div>
  );
}
