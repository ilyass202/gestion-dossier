import { useState, useEffect } from 'react';
import { AppSelector } from './components/AppSelector';
import { Login } from './components/Login';
import { Layout } from './components/Layout';
import { Dashboard } from './components/Dashboard';
import { Dossiers } from './components/Dossiers';
import { DossierDetail } from './components/DossierDetail';
import { Utilisateurs } from './components/Utilisateurs';
import { Parametres } from './components/Parametres';
import { Toaster } from './components/ui/sonner';
import { isAuthenticated, logout as authLogout } from './services/auth';

export default function App() {
  const [selectedApp, setSelectedApp] = useState<'admin' | 'mobile' | null>(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [currentPage, setCurrentPage] = useState('dashboard');
  const [selectedDossier, setSelectedDossier] = useState<string | null>(null);

  // Vérifier l'authentification au chargement
  useEffect(() => {
    if (selectedApp === 'admin') {
      setIsLoggedIn(isAuthenticated());
    }
  }, [selectedApp]);

  const handleSelectApp = (app: 'admin' | 'mobile') => {
    setSelectedApp(app);
    if (app === 'mobile') {
      setIsLoggedIn(true); // Auto-login for mobile demo
      setCurrentPage('home');
    }
  };

  const handleLogin = () => {
    setIsLoggedIn(true);
  };

  const handleLogout = () => {
    authLogout();
    setIsLoggedIn(false);
    setCurrentPage(selectedApp === 'admin' ? 'dashboard' : 'home');
    setSelectedDossier(null);
    setSelectedApp(null);
  };

  const handleNavigate = (page: string) => {
    setCurrentPage(page);
    setSelectedDossier(null);
  };

  const handleViewDossierDetail = (dossierId: string) => {
    setSelectedDossier(dossierId);
  };

  const handleBackToDossiers = () => {
    setSelectedDossier(null);
  };

  // App selector screen
  if (!selectedApp) {
    return <AppSelector onSelectApp={handleSelectApp} />;
  }

  // Admin platform
  if (selectedApp === 'admin') {
    if (!isLoggedIn) {
      return <Login onLogin={handleLogin} />;
    }

    const renderAdminPage = () => {
      if (selectedDossier) {
        return <DossierDetail dossierId={selectedDossier} onBack={handleBackToDossiers} />;
      }

      switch (currentPage) {
        case 'dashboard':
          return <Dashboard />;
        case 'dossiers':
          return <Dossiers onViewDetail={handleViewDossierDetail} />;
        case 'utilisateurs':
          return <Utilisateurs />;
        case 'parametres':
          return <Parametres />;
        default:
          return <Dashboard />;
      }
    };

    return (
      <>
        <Layout
          currentPage={currentPage}
          onNavigate={handleNavigate}
          onLogout={handleLogout}
        >
          {renderAdminPage()}
        </Layout>
        <Toaster />
      </>
    );
  }

  return null;
}
