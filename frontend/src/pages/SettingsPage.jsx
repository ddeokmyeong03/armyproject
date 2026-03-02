import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useMe, useUpdateMe } from '../hooks/useUser';
import { useTags, useCreateTag, useDeleteTag } from '../hooks/useTags';
import { PageSpinner } from '../components/ui/Spinner';
import { CATEGORY_OPTIONS } from '../utils/categories';
import { extractApiError } from '../utils/apiError';
import { useState } from 'react';

function ProfileSection() {
  const { data: me, isLoading } = useMe();
  const updateMe = useUpdateMe();
  const { register, handleSubmit, reset, formState: { errors } } = useForm();

  useEffect(() => {
    if (me) {
      reset({
        nickname: me.nickname,
        dischargeDate: me.dischargeDate,
        dailyMinutes: me.dailyMinutes,
        goalPriorities: me.goalPriorities ?? [],
      });
    }
  }, [me, reset]);

  if (isLoading) return <PageSpinner />;

  function onSubmit(data) {
    updateMe.mutate({
      ...data,
      dailyMinutes: Number(data.dailyMinutes),
      goalPriorities: data.goalPriorities ?? [],
    });
  }

  return (
    <section className="card mb-6">
      <h2 className="font-semibold text-gray-800 mb-4">프로필 설정</h2>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">닉네임</label>
          <input
            className="input-base"
            {...register('nickname', { required: '닉네임을 입력해주세요' })}
          />
          {errors.nickname && <p className="text-xs text-red-500 mt-1">{errors.nickname.message}</p>}
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">전역일</label>
          <input
            type="date"
            className="input-base"
            {...register('dischargeDate', { required: '전역일을 입력해주세요' })}
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            하루 목표 시간 (분)
          </label>
          <input
            type="number"
            min="10"
            max="480"
            className="input-base"
            {...register('dailyMinutes', { required: true, min: 10 })}
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            관심 분야
          </label>
          <div className="flex flex-wrap gap-3">
            {CATEGORY_OPTIONS.map(({ value, label }) => (
              <label key={value} className="flex items-center gap-1.5 text-sm cursor-pointer">
                <input
                  type="checkbox"
                  value={value}
                  {...register('goalPriorities')}
                  className="accent-military-500"
                />
                {label}
              </label>
            ))}
          </div>
        </div>

        {updateMe.isError && (
          <p className="text-xs text-red-500">{extractApiError(updateMe.error)}</p>
        )}
        {updateMe.isSuccess && (
          <p className="text-xs text-green-600">저장되었습니다.</p>
        )}

        <div className="flex justify-end">
          <button type="submit" disabled={updateMe.isPending} className="btn-primary">
            저장
          </button>
        </div>
      </form>
    </section>
  );
}

function TagsSection() {
  const { data: tags = [] } = useTags();
  const createTag = useCreateTag();
  const deleteTag = useDeleteTag();
  const [newName, setNewName] = useState('');

  function handleAdd() {
    if (!newName.trim()) return;
    createTag.mutate(newName.trim(), { onSuccess: () => setNewName('') });
  }

  return (
    <section className="card">
      <h2 className="font-semibold text-gray-800 mb-4">태그 관리</h2>

      <div className="flex gap-2 mb-4">
        <input
          type="text"
          className="input-base flex-1 text-sm"
          placeholder="새 태그 이름..."
          value={newName}
          onChange={(e) => setNewName(e.target.value)}
          onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); handleAdd(); } }}
        />
        <button
          type="button"
          onClick={handleAdd}
          disabled={!newName.trim() || createTag.isPending}
          className="btn-primary shrink-0"
        >
          추가
        </button>
      </div>

      {tags.length === 0 ? (
        <p className="text-sm text-gray-400">태그가 없습니다.</p>
      ) : (
        <ul className="divide-y divide-gray-100">
          {tags.map((tag) => (
            <li key={tag.id} className="flex items-center justify-between py-2">
              <span className="text-sm text-gray-700">{tag.name}</span>
              <button
                onClick={() => deleteTag.mutate(tag.id)}
                disabled={deleteTag.isPending}
                className="text-xs text-gray-400 hover:text-red-400 transition-colors"
              >
                삭제
              </button>
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}

export default function SettingsPage() {
  return (
    <div className="p-6 max-w-xl mx-auto">
      <h1 className="text-xl font-bold text-gray-900 mb-6">설정</h1>
      <ProfileSection />
      <TagsSection />
    </div>
  );
}
