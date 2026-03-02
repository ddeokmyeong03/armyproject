import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuthStore } from './store/authStore';
import Layout from './components/layout/Layout';
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import PlansPage from './pages/plans/PlansPage';
import PlanDetailPage from './pages/plans/PlanDetailPage';
import RecordsPage from './pages/records/RecordsPage';
import RecordFormPage from './pages/records/RecordFormPage';
import RecordDetailPage from './pages/records/RecordDetailPage';
import RoadmapsPage from './pages/roadmaps/RoadmapsPage';
import RoadmapDetailPage from './pages/roadmaps/RoadmapDetailPage';
import SettingsPage from './pages/SettingsPage';

function RequireAuth({ children }) {
  const accessToken = useAuthStore((s) => s.accessToken);
  const refreshToken = useAuthStore.getState().getRefreshToken();
  if (!accessToken && !refreshToken) {
    return <Navigate to="/login" replace />;
  }
  return children;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route
        path="/"
        element={
          <RequireAuth>
            <Layout />
          </RequireAuth>
        }
      >
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<DashboardPage />} />
        <Route path="plans" element={<PlansPage />} />
        <Route path="plans/:planId" element={<PlanDetailPage />} />
        <Route path="records" element={<RecordsPage />} />
        <Route path="records/new" element={<RecordFormPage />} />
        <Route path="records/:recordId" element={<RecordDetailPage />} />
        <Route path="records/:recordId/edit" element={<RecordFormPage />} />
        <Route path="roadmaps" element={<RoadmapsPage />} />
        <Route path="roadmaps/:roadmapId" element={<RoadmapDetailPage />} />
        <Route path="settings" element={<SettingsPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}
