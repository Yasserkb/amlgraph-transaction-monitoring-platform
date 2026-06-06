import { Navigate, Route, Routes } from 'react-router-dom';
import { BrowserRouter } from 'react-router-dom';
import AppShell from './components/layout/AppShell';
import DashboardPage from './pages/DashboardPage';
import TransactionsPage from './pages/TransactionsPage';
import AlertsPage from './pages/AlertsPage';
import CasesPage from './pages/CasesPage';
import CustomersPage from './pages/CustomersPage';
import AiAssistantPage from './pages/AiAssistantPage';

export default function App() {
  return (
    <BrowserRouter>
      <AppShell>
        <Routes>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/transactions" element={<TransactionsPage />} />
          <Route path="/alerts" element={<AlertsPage />} />
          <Route path="/cases" element={<CasesPage />} />
          <Route path="/customers" element={<CustomersPage />} />
          <Route path="/ai-assistant" element={<AiAssistantPage />} />
        </Routes>
      </AppShell>
    </BrowserRouter>
  );
}
