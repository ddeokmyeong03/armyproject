import { Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { useRegister } from '../../hooks/useAuth';
import { extractApiError } from '../../utils/apiError';
import { CATEGORY_OPTIONS } from '../../utils/categories';
import Spinner from '../../components/ui/Spinner';

export default function RegisterPage() {
  const { register, handleSubmit, formState: { errors } } = useForm({
    defaultValues: { dailyMinutes: 60, goalPriorities: [] },
  });
  const registerMutation = useRegister();

  function onSubmit(data) {
    registerMutation.mutate({
      email: data.email,
      password: data.password,
      nickname: data.nickname,
      discharge_date: data.dischargeDate,
      daily_minutes: Number(data.dailyMinutes),
      goal_priorities: data.goalPriorities ?? [],
    });
  }

  return (
    <div className="min-h-screen bg-military-50 flex items-center justify-center p-4">
      <div className="w-full max-w-sm">
        <div className="text-center mb-8">
          <h1 className="text-2xl font-bold text-military-800">병사 자기개발 관리</h1>
          <p className="text-sm text-gray-500 mt-1">새 계정을 만드세요</p>
        </div>

        <div className="card">
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">닉네임</label>
              <input
                type="text"
                className="input-base"
                placeholder="홍길동"
                {...register('nickname', { required: '닉네임을 입력해주세요' })}
              />
              {errors.nickname && <p className="text-xs text-red-500 mt-1">{errors.nickname.message}</p>}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">이메일</label>
              <input
                type="email"
                className="input-base"
                placeholder="example@army.mil.kr"
                {...register('email', { required: '이메일을 입력해주세요' })}
              />
              {errors.email && <p className="text-xs text-red-500 mt-1">{errors.email.message}</p>}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">비밀번호</label>
              <input
                type="password"
                className="input-base"
                placeholder="8자 이상"
                {...register('password', { required: '비밀번호를 입력해주세요', minLength: { value: 8, message: '8자 이상 입력해주세요' } })}
              />
              {errors.password && <p className="text-xs text-red-500 mt-1">{errors.password.message}</p>}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">전역일</label>
              <input
                type="date"
                className="input-base"
                {...register('dischargeDate', { required: '전역일을 입력해주세요' })}
              />
              {errors.dischargeDate && <p className="text-xs text-red-500 mt-1">{errors.dischargeDate.message}</p>}
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
              <label className="block text-sm font-medium text-gray-700 mb-1">
                관심 분야 (복수 선택 가능)
              </label>
              <div className="flex flex-wrap gap-2 mt-1">
                {CATEGORY_OPTIONS.map(({ value, label }) => (
                  <label key={value} className="flex items-center gap-1 text-sm cursor-pointer">
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

            {registerMutation.isError && (
              <p className="text-xs text-red-500">{extractApiError(registerMutation.error)}</p>
            )}

            <button
              type="submit"
              disabled={registerMutation.isPending}
              className="btn-primary w-full"
            >
              {registerMutation.isPending ? <Spinner className="mr-2" /> : null}
              회원가입
            </button>
          </form>

          <p className="text-center text-sm text-gray-500 mt-4">
            이미 계정이 있으신가요?{' '}
            <Link to="/login" className="text-military-600 font-medium hover:underline">
              로그인
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
