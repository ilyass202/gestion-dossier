import { Card, CardContent } from './ui/card';
import { Button } from './ui/button';
import { Building2, Smartphone } from 'lucide-react';

interface AppSelectorProps {
  onSelectApp: (app: 'admin' | 'mobile') => void;
}

export function AppSelector({ onSelectApp }: AppSelectorProps) {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 via-white to-blue-50 p-4">
      <div className="max-w-4xl w-full">
        <div className="text-center mb-8">
          <h1 className="text-4xl mb-3">Plateforme d'Autorisation Urbaine</h1>
          <p className="text-gray-600">Sélectionnez l'interface que vous souhaitez utiliser</p>
        </div>

        <div className="grid md:grid-cols-2 gap-6">
          {/* Admin Platform */}
          <Card className="hover:shadow-xl transition-shadow cursor-pointer border-2 hover:border-blue-500">
            <CardContent className="p-8 text-center">
              <div className="w-20 h-20 bg-gradient-to-br from-blue-500 to-blue-600 rounded-2xl mx-auto mb-4 flex items-center justify-center">
                <Building2 className="w-10 h-10 text-white" />
              </div>
              <h2 className="text-2xl mb-2">Plateforme Administration</h2>
              <p className="text-gray-600 mb-6">
                Interface pour les agents et administrateurs des Agences Urbaines
              </p>
              <div className="text-sm text-gray-500 mb-6 space-y-2">
                <p>✓ Gestion des dossiers</p>
                <p>✓ Validation et rejet</p>
                <p>✓ Statistiques et rapports</p>
                <p>✓ Gestion des utilisateurs</p>
              </div>
              <Button 
                onClick={() => onSelectApp('admin')}
                className="w-full bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700"
                size="lg"
              >
                Accéder à l'administration
              </Button>
            </CardContent>
          </Card>

          {/* Mobile App */}
          <Card className="hover:shadow-xl transition-shadow cursor-pointer border-2 hover:border-green-500">
            <CardContent className="p-8 text-center">
              <div className="w-20 h-20 bg-gradient-to-br from-green-500 to-green-600 rounded-2xl mx-auto mb-4 flex items-center justify-center">
                <Smartphone className="w-10 h-10 text-white" />
              </div>
              <h2 className="text-2xl mb-2">Mon Dossier Urbain</h2>
              <p className="text-gray-600 mb-6">
                Application mobile pour les citoyens
              </p>
              <div className="text-sm text-gray-500 mb-6 space-y-2">
                <p>✓ Déposer une demande</p>
                <p>✓ Suivre mes dossiers</p>
                <p>✓ Notifications en temps réel</p>
                <p>✓ Géolocalisation du projet</p>
              </div>
              <Button 
                onClick={() => onSelectApp('mobile')}
                className="w-full bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700"
                size="lg"
              >
                Accéder à l'app citoyenne
              </Button>
            </CardContent>
          </Card>
        </div>

        <div className="text-center mt-8 text-sm text-gray-500">
          <p>© 2025 Maroc Digital - Agence Urbaine - Tous droits réservés</p>
        </div>
      </div>
    </div>
  );
}
