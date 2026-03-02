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
  const user = useAuthStore((s) => s.user);
  const logout = useLogout();

  return (
    <div className="flex h-screen bg-gray-50">
      {/* Sidebar */}
      <aside className="w-52 bg-military-800 text-white flex flex-col shrink-0">
        <div className="px-4 py-5 border-b border-military-700">
          <h1 className="text-sm font-bold tracking-wide">병사 자기개발</h1>
          {user && (
            <p className="text-xs text-military-300 mt-1 truncate">{user.nickname}</p>
          )}
        </div>

        <nav className="flex-1 py-2 overflow-y-auto">
          {NAV_ITEMS.map(({ to, label, icon }) => (
            <NavLink
              key={to}
              to={to}
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

      {/* Main content */}
      <main className="flex-1 overflow-y-auto">
        <Outlet />
      </main>
    </div>
  );
}
