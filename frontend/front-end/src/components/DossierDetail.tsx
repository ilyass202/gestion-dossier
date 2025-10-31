import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Badge } from './ui/badge';
import { Button } from './ui/button';
import { Separator } from './ui/separator';
import { Textarea } from './ui/textarea';
import { 
  ArrowLeft, 
  FileText, 
  User, 
  MapPin, 
  Calendar, 
  Download,
  CheckCircle2,
  XCircle,
  RotateCcw,
  Building2,
  Phone,
  Mail
} from 'lucide-react';
import { Alert, AlertDescription } from './ui/alert';
import { toast } from 'sonner';
import { useState } from 'react';

interface DossierDetailProps {
  dossierId: string;
  onBack: () => void;
}

const mockDossierData: Record<string, any> = {
  'DOS-2025-001': {
    id: 'DOS-2025-001',
    demandeur: 'Mohamed Alami',
    type: 'Permis de construire',
    date: '2025-01-15',
    statut: 'En attente',
    email: 'mohamed.alami@example.com',
    telephone: '+212 6 12 34 56 78',
    adresseProjet: 'Avenue Hassan II, Quartier Les Orangers, Casablanca',
    coordinates: { lat: 33.5731, lng: -7.5898 },
    surfaceTerrain: '450 m²',
    surfaceBatie: '320 m²',
    nombreEtages: '3',
    description: 'Construction d\'un immeuble résidentiel R+2 avec commerce au rez-de-chaussée',
    documents: [
      { nom: 'Plan architectural', type: 'PDF', taille: '2.4 MB' },
      { nom: 'Titre foncier', type: 'PDF', taille: '1.1 MB' },
      { nom: 'Note de calcul', type: 'PDF', taille: '3.2 MB' },
      { nom: 'Photos du terrain', type: 'ZIP', taille: '8.5 MB' },
    ],
  },
};

export function DossierDetail({ dossierId, onBack }: DossierDetailProps) {
  const [commentaire, setCommentaire] = useState('');
  const dossier = mockDossierData[dossierId] || mockDossierData['DOS-2025-001'];

  const handleAction = (action: string) => {
    toast.success(`Dossier ${action} avec succès`, {
      description: `Le dossier ${dossier.id} a été ${action}.`,
    });
  };

  const getStatusBadge = (statut: string) => {
    const statusConfig: Record<string, { className: string }> = {
      'Validé': { className: 'bg-green-100 text-green-700' },
      'En attente': { className: 'bg-orange-100 text-orange-700' },
      'En cours': { className: 'bg-blue-100 text-blue-700' },
      'Rejeté': { className: 'bg-red-100 text-red-700' },
    };
    
    const config = statusConfig[statut] || { className: '' };
    return <Badge className={config.className}>{statut}</Badge>;
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <Button variant="ghost" size="icon" onClick={onBack}>
          <ArrowLeft className="w-5 h-5" />
        </Button>
        <div>
          <h1 className="text-3xl">Détail du dossier</h1>
          <p className="text-gray-600">{dossier.id}</p>
        </div>
        {getStatusBadge(dossier.statut)}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main Info */}
        <div className="lg:col-span-2 space-y-6">
          {/* Informations générales */}
          <Card>
            <CardHeader>
              <CardTitle>Informations générales</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="flex gap-3">
                  <User className="w-5 h-5 text-gray-400 mt-0.5" />
                  <div>
                    <p className="text-sm text-gray-600">Demandeur</p>
                    <p>{dossier.demandeur}</p>
                  </div>
                </div>
                <div className="flex gap-3">
                  <FileText className="w-5 h-5 text-gray-400 mt-0.5" />
                  <div>
                    <p className="text-sm text-gray-600">Type d'autorisation</p>
                    <p>{dossier.type}</p>
                  </div>
                </div>
                <div className="flex gap-3">
                  <Calendar className="w-5 h-5 text-gray-400 mt-0.5" />
                  <div>
                    <p className="text-sm text-gray-600">Date de dépôt</p>
                    <p>{dossier.date}</p>
                  </div>
                </div>
                <div className="flex gap-3">
                  <Mail className="w-5 h-5 text-gray-400 mt-0.5" />
                  <div>
                    <p className="text-sm text-gray-600">Email</p>
                    <p>{dossier.email}</p>
                  </div>
                </div>
                <div className="flex gap-3">
                  <Phone className="w-5 h-5 text-gray-400 mt-0.5" />
                  <div>
                    <p className="text-sm text-gray-600">Téléphone</p>
                    <p>{dossier.telephone}</p>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Informations du projet */}
          <Card>
            <CardHeader>
              <CardTitle>Informations du projet</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex gap-3">
                <MapPin className="w-5 h-5 text-gray-400 mt-0.5" />
                <div>
                  <p className="text-sm text-gray-600">Adresse du projet</p>
                  <p>{dossier.adresseProjet}</p>
                </div>
              </div>
              <Separator />
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <p className="text-sm text-gray-600">Surface du terrain</p>
                  <p>{dossier.surfaceTerrain}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Surface bâtie</p>
                  <p>{dossier.surfaceBatie}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Nombre d'étages</p>
                  <p>{dossier.nombreEtages}</p>
                </div>
              </div>
              <Separator />
              <div>
                <p className="text-sm text-gray-600 mb-2">Description du projet</p>
                <p>{dossier.description}</p>
              </div>
            </CardContent>
          </Card>

          {/* Carte géographique */}
          <Card>
            <CardHeader>
              <CardTitle>Géolocalisation du projet</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="bg-gray-100 rounded-lg h-64 flex items-center justify-center relative overflow-hidden">
                <div className="absolute inset-0 bg-gradient-to-br from-blue-100 to-green-100">
                  <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2">
                    <MapPin className="w-12 h-12 text-red-500 drop-shadow-lg" />
                  </div>
                  <div className="absolute top-4 left-4 bg-white px-3 py-2 rounded-lg shadow text-sm">
                    <p className="text-gray-600">Lat: {dossier.coordinates.lat}</p>
                    <p className="text-gray-600">Lng: {dossier.coordinates.lng}</p>
                  </div>
                </div>
              </div>
              <Alert className="mt-4">
                <Building2 className="h-4 w-4" />
                <AlertDescription>
                  Carte interactive simulée - Intégration Google Maps ou OpenStreetMap recommandée
                </AlertDescription>
              </Alert>
            </CardContent>
          </Card>

          {/* Actions et commentaires */}
          <Card>
            <CardHeader>
              <CardTitle>Commentaires et décision</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <label className="text-sm text-gray-600 mb-2 block">Ajouter un commentaire</label>
                <Textarea
                  placeholder="Saisissez vos observations..."
                  value={commentaire}
                  onChange={(e) => setCommentaire(e.target.value)}
                  rows={4}
                />
              </div>
              <div className="flex flex-wrap gap-3">
                <Button 
                  className="bg-green-600 hover:bg-green-700"
                  onClick={() => handleAction('validé')}
                >
                  <CheckCircle2 className="w-4 h-4 mr-2" />
                  Valider le dossier
                </Button>
                <Button 
                  variant="destructive"
                  onClick={() => handleAction('rejeté')}
                >
                  <XCircle className="w-4 h-4 mr-2" />
                  Rejeter le dossier
                </Button>
                <Button 
                  variant="outline"
                  onClick={() => handleAction('renvoyé')}
                >
                  <RotateCcw className="w-4 h-4 mr-2" />
                  Renvoyer pour correction
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Sidebar - Documents */}
        <div className="lg:col-span-1">
          <Card>
            <CardHeader>
              <CardTitle>Documents joints</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                {dossier.documents.map((doc: any, index: number) => (
                  <div 
                    key={index}
                    className="flex items-start justify-between p-3 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
                  >
                    <div className="flex gap-3 flex-1 min-w-0">
                      <FileText className="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5" />
                      <div className="min-w-0 flex-1">
                        <p className="text-sm truncate">{doc.nom}</p>
                        <p className="text-xs text-gray-500">{doc.type} - {doc.taille}</p>
                      </div>
                    </div>
                    <Button variant="ghost" size="icon" className="flex-shrink-0">
                      <Download className="w-4 h-4" />
                    </Button>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
