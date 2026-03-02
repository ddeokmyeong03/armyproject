import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { useRecord, useCreateRecord, useUpdateRecord } from '../../hooks/useRecords';
import { useTags, useCreateTag } from '../../hooks/useTags';
import { PageSpinner } from '../../components/ui/Spinner';
import { TagBadge } from '../../components/ui/Badge';
import { CATEGORY_OPTIONS } from '../../utils/categories';
import { extractApiError } from '../../utils/apiError';
import { toISODate } from '../../utils/date';

export default function RecordFormPage() {
  const { recordId } = useParams();
  const isEdit = !!recordId;
  const navigate = useNavigate();

  const { data: existing, isLoading: existingLoading } = useRecord(recordId);
  const { data: allTags = [] } = useTags();
  const createRecord = useCreateRecord();
  const updateRecord = useUpdateRecord();
  const createTag = useCreateTag();

  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    defaultValues: { category: 'ETC', activityDate: toISODate(new Date()) },
  });

  const [selectedTagIds, setSelectedTagIds] = useState([]);
  const [newTagName, setNewTagName] = useState('');

  useEffect(() => {
    if (existing) {
      reset({
        title: existing.title,
        content: existing.content ?? '',
        category: existing.category,
        activityDate: existing.activityDate,
      });
      setSelectedTagIds(existing.tags?.map((t) => t.id) ?? []);
    }
  }, [existing, reset]);

  function toggleTag(id) {
    setSelectedTagIds((prev) =>
      prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
    );
  }

  async function handleAddTag() {
    if (!newTagName.trim()) return;
    createTag.mutate(newTagName.trim(), {
      onSuccess: (tag) => {
        setSelectedTagIds((prev) => [...prev, tag.id]);
        setNewTagName('');
      },
    });
  }

  function onSubmit(data) {
    const payload = { ...data, tagIds: selectedTagIds };
    if (isEdit) {
      updateRecord.mutate({ recordId, data: payload }, {
        onSuccess: () => navigate(`/records/${recordId}`),
      });
    } else {
      createRecord.mutate(payload, {
        onSuccess: (rec) => navigate(`/records/${rec.id}`),
      });
    }
  }

  if (isEdit && existingLoading) return <PageSpinner />;

  const isPending = createRecord.isPending || updateRecord.isPending;
  const error = createRecord.error || updateRecord.error;

  return (
    <div className="p-6 max-w-xl mx-auto">
      <button
        onClick={() => navigate(isEdit ? `/records/${recordId}` : '/records')}
        className="text-sm text-gray-400 hover:text-gray-600 mb-4"
      >
        ← 뒤로
      </button>
      <h1 className="text-xl font-bold text-gray-900 mb-6">
        {isEdit ? '기록 수정' : '새 활동 기록'}
      </h1>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">제목 *</label>
          <input
            className="input-base"
            placeholder="활동 제목"
            {...register('title', { required: '제목을 입력해주세요' })}
          />
          {errors.title && <p className="text-xs text-red-500 mt-1">{errors.title.message}</p>}
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">카테고리</label>
            <select className="input-base" {...register('category')}>
              {CATEGORY_OPTIONS.map(({ value, label }) => (
                <option key={value} value={value}>{label}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">활동 날짜</label>
            <input type="date" className="input-base" {...register('activityDate', { required: true })} />
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">내용</label>
          <textarea
            className="input-base resize-none"
            rows={5}
            placeholder="활동 내용을 자세히 적어보세요..."
            {...register('content')}
          />
        </div>

        {/* Tags */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">태그</label>
          <div className="flex flex-wrap gap-1.5 mb-2">
            {allTags.map((tag) => (
              <button
                key={tag.id}
                type="button"
                onClick={() => toggleTag(tag.id)}
                className={`px-2.5 py-0.5 rounded-full text-xs font-medium border transition-colors ${
                  selectedTagIds.includes(tag.id)
                    ? 'bg-military-500 text-white border-military-500'
                    : 'bg-white text-gray-600 border-gray-300 hover:border-military-400'
                }`}
              >
                {tag.name}
              </button>
            ))}
          </div>
          <div className="flex gap-2">
            <input
              type="text"
              className="input-base text-sm flex-1"
              placeholder="새 태그 이름..."
              value={newTagName}
              onChange={(e) => setNewTagName(e.target.value)}
              onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); handleAddTag(); } }}
            />
            <button
              type="button"
              onClick={handleAddTag}
              disabled={!newTagName.trim() || createTag.isPending}
              className="btn-ghost shrink-0 py-1.5"
            >
              + 태그
            </button>
          </div>
          {selectedTagIds.length > 0 && (
            <div className="flex flex-wrap gap-1 mt-2">
              {allTags
                .filter((t) => selectedTagIds.includes(t.id))
                .map((t) => (
                  <TagBadge key={t.id} name={t.name} onRemove={() => toggleTag(t.id)} />
                ))}
            </div>
          )}
        </div>

        {error && <p className="text-xs text-red-500">{extractApiError(error)}</p>}

        <div className="flex gap-2 justify-end">
          <button
            type="button"
            onClick={() => navigate(isEdit ? `/records/${recordId}` : '/records')}
            className="btn-ghost"
          >
            취소
          </button>
          <button type="submit" disabled={isPending} className="btn-primary">
            {isEdit ? '저장' : '작성'}
          </button>
        </div>
      </form>
    </div>
  );
}
