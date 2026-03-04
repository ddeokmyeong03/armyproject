import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuthStore } from './store/authStore';
import Layout from './components/layout/Layout';
import LandingPage from './pages/LandingPage';
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
    return <Navigate to="/" replace />;
  }
  return children;
}

function LandingOrDashboard() {
  const accessToken = useAuthStore((s) => s.accessToken);
  const refreshToken = useAuthStore.getState().getRefreshToken();
  if (accessToken || refreshToken) {
    return <Navigate to="/dashboard" replace />;
  }
  return <LandingPage />;
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<LandingOrDashboard />} />
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
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
