import { Link } from 'react-router-dom';

const FEATURES = [
  { icon: '⊞', title: '대시보드', desc: '전역 D-day · 스트릭 · 오늘 할 일을 한눈에' },
  { icon: '◻', title: '주간 계획', desc: '주차별 목표와 날짜별 할 일을 체계적으로' },
  { icon: '≡', title: '활동 기록', desc: '자기계발 활동을 일지로 남기고 STAR 작성' },
  { icon: '◈', title: '로드맵', desc: '자격증·영어·체력 등 12주 완성 커리큘럼' },
];

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-military-900 text-white flex flex-col">
      {/* Hero */}
      <div className="flex-1 flex flex-col items-center justify-center px-6 py-16 text-center">
        <p className="text-military-400 text-sm font-semibold tracking-widest uppercase mb-3">
          대한민국 군 장병을 위한
        </p>
        <h1 className="text-4xl sm:text-5xl font-extrabold tracking-tight mb-4">
          millog
        </h1>
        <p className="text-military-300 text-base sm:text-lg max-w-sm mb-10 leading-relaxed">
          복무 중 자기계발을 계획하고 기록하세요.<br />
          전역까지의 성장을 함께 추적합니다.
        </p>

        <div className="flex flex-col sm:flex-row gap-3 w-full max-w-xs">
          <Link
            to="/register"
            className="flex-1 bg-military-500 hover:bg-military-400 text-white font-semibold py-3 px-6 rounded-lg text-center transition-colors"
          >
            시작하기
          </Link>
          <Link
            to="/login"
            className="flex-1 border border-military-600 hover:border-military-400 text-military-300 hover:text-white font-semibold py-3 px-6 rounded-lg text-center transition-colors"
          >
            로그인
          </Link>
        </div>
      </div>

      {/* Features */}
      <div className="bg-military-800 px-6 py-12">
        <div className="max-w-2xl mx-auto grid grid-cols-2 sm:grid-cols-4 gap-6 text-center">
          {FEATURES.map(({ icon, title, desc }) => (
            <div key={title}>
              <div className="text-3xl mb-2">{icon}</div>
              <p className="text-sm font-semibold text-white mb-1">{title}</p>
              <p className="text-xs text-military-400 leading-relaxed">{desc}</p>
            </div>
          ))}
        </div>
      </div>

      <footer className="text-center text-xs text-military-600 py-4">
        millog.kr
      </footer>
    </div>
  );
}
