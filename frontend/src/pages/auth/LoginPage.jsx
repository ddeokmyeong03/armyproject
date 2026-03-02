import { Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { useLogin } from '../../hooks/useAuth';
import { extractApiError } from '../../utils/apiError';
import Spinner from '../../components/ui/Spinner';

export default function LoginPage() {
  const { register, handleSubmit, formState: { errors } } = useForm();
  const login = useLogin();

  function onSubmit(data) {
    login.mutate(data);
  }

  return (
    <div className="min-h-screen bg-military-50 flex items-center justify-center p-4">
      <div className="w-full max-w-sm">
        <div className="text-center mb-8">
          <h1 className="text-2xl font-bold text-military-800">병사 자기개발 관리</h1>
          <p className="text-sm text-gray-500 mt-1">로그인하여 자기개발을 시작하세요</p>
        </div>

        <div className="card">
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
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
                placeholder="비밀번호"
                {...register('password', { required: '비밀번호를 입력해주세요' })}
              />
              {errors.password && <p className="text-xs text-red-500 mt-1">{errors.password.message}</p>}
            </div>

            {login.isError && (
              <p className="text-xs text-red-500">{extractApiError(login.error)}</p>
            )}

            <button
              type="submit"
              disabled={login.isPending}
              className="btn-primary w-full"
            >
              {login.isPending ? <Spinner className="mr-2" /> : null}
              로그인
            </button>
          </form>

          <p className="text-center text-sm text-gray-500 mt-4">
            계정이 없으신가요?{' '}
            <Link to="/register" className="text-military-600 font-medium hover:underline">
              회원가입
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
