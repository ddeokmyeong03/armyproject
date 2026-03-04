import { useState } from 'react';
import { Outlet, NavLink } from 'react-router-dom';
import { useLogout } from '../../hooks/useAuth';
import { useAuthStore } from '../../store/authStore';

const NAV_ITEMS = [
  { to: '/dashboard', label: '대시보드', icon: '⊞' },
  { to: '/plans',     label: '주간 계획', icon: '◻' },
  { to: '/records',   label: '활동 기록', icon: '≡' },
  { to: '/roadmaps',  label: '로드맵',   icon: '◈' },
  { to: '/settings',  label: '설정',     icon: '⚙' },
];

export default function Layout() {
  const [mobileOpen, setMobileOpen] = useState(false);
  const user = useAuthStore((s) => s.user);
  const logout = useLogout();

  const closeMobile = () => setMobileOpen(false);

  return (
    <div className="flex h-screen bg-gray-50">
      {/* 모바일 오버레이 */}
      {mobileOpen && (
        <div
          className="fixed inset-0 bg-black/50 z-20 md:hidden"
          onClick={closeMobile}
        />
      )}

      {/* Sidebar */}
      <aside
        className={`
          fixed md:static inset-y-0 left-0 z-30
          w-52 bg-military-800 text-white flex flex-col shrink-0
          transition-transform duration-200 ease-in-out
          ${mobileOpen ? 'translate-x-0' : '-translate-x-full md:translate-x-0'}
        `}
      >
        <div className="px-4 py-5 border-b border-military-700">
          <h1 className="text-sm font-bold tracking-wide">millog</h1>
          {user && (
            <p className="text-xs text-military-300 mt-1 truncate">{user.nickname}</p>
          )}
        </div>

        <nav className="flex-1 py-2 overflow-y-auto">
          {NAV_ITEMS.map(({ to, label, icon }) => (
            <NavLink
              key={to}
              to={to}
              onClick={closeMobile}
              className={({ isActive }) =>
                `flex items-center gap-3 px-4 py-2.5 text-sm transition-colors ${
                  isActive
                    ? 'bg-military-600 text-white font-medium'
                    : 'text-military-200 hover:bg-military-700 hover:text-white'
                }`
              }
            >
              <span className="text-base leading-none">{icon}</span>
              <span>{label}</span>
            </NavLink>
          ))}
        </nav>

        <div className="px-4 py-3 border-t border-military-700">
          <button
            onClick={() => logout.mutate()}
            disabled={logout.isPending}
            className="w-full text-left text-xs text-military-300 hover:text-white transition-colors py-1"
          >
            로그아웃
          </button>
        </div>
      </aside>

      {/* Main area */}
      <div className="flex-1 flex flex-col min-h-0 overflow-hidden">
        {/* 모바일 상단 헤더 */}
        <header className="md:hidden flex items-center gap-3 px-4 py-3 bg-military-800 text-white shrink-0">
          <button
            onClick={() => setMobileOpen(true)}
            className="text-xl leading-none text-military-200 hover:text-white"
            aria-label="메뉴 열기"
          >
            ☰
          </button>
          <span className="text-sm font-bold tracking-wide">millog</span>
        </header>

        <main className="flex-1 overflow-y-auto">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
