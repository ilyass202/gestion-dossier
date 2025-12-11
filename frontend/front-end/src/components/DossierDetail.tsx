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
  Loader2
} from 'lucide-react';
import { Alert, AlertDescription } from './ui/alert';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from './ui/dialog';
import { toast } from 'sonner';
import { useState, useEffect } from 'react';
import { getDetailsDemande, updateStatusDemande } from '../services/api';
import type { AdminDetailsDemande } from '../services/api';

interface DossierDetailProps {
  dossierId: string;
  onBack: () => void;
}

// Fonction pour traduire les statuts
const translateStatus = (status: string): string => {
  const translations: Record<string, string> = {
    'ACCEPTEE': 'Acceptée',
    'REJETE': 'Rejetée',
    'EN_COURS': 'En cours',
    'AVIS_FAVORABLE': 'Avis favorable',
    'AVIS_DEFAVORABLE': 'Avis défavorable',
  };
  return translations[status] || status;
};

// Fonction pour traduire les types
const translateType = (type: string): string => {
  const translations: Record<string, string> = {
    'PERMIS_CONSTRUCTION': 'Permis de construire',
    'AUTORISATION_OUVERTURE': 'Autorisation d\'ouverture',
    'CERTIFICAT_URBANISME': 'Certificat d\'urbanisme',
  };
  return translations[type] || type;
};

export function DossierDetail({ dossierId, onBack }: DossierDetailProps) {
  const [dossier, setDossier] = useState<AdminDetailsDemande | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isUpdating, setIsUpdating] = useState(false);
  const [rejectDialogOpen, setRejectDialogOpen] = useState(false);
  const [motifRejet, setMotifRejet] = useState('');

  useEffect(() => {
    const fetchDossier = async () => {
      try {
        setIsLoading(true);
        const data = await getDetailsDemande(dossierId);
        setDossier(data);
      } catch (error) {
        toast.error('Erreur lors du chargement du dossier', {
          description: error instanceof Error ? error.message : 'Une erreur est survenue',
        });
        onBack();
      } finally {
        setIsLoading(false);
      }
    };

    fetchDossier();
  }, [dossierId, onBack]);

  const getStatusBadge = (statut: string) => {
    const statusConfig: Record<string, { className: string }> = {
      'ACCEPTEE': { className: 'bg-green-100 text-green-700' },
      'EN_COURS': { className: 'bg-blue-100 text-blue-700' },
      'AVIS_FAVORABLE': { className: 'bg-green-100 text-green-700' },
      'REJETE': { className: 'bg-red-100 text-red-700' },
      'AVIS_DEFAVORABLE': { className: 'bg-red-100 text-red-700' },
    };
    
    const config = statusConfig[statut] || { className: 'bg-gray-100 text-gray-700' };
    return <Badge className={config.className}>{translateStatus(statut)}</Badge>;
  };

  const handleUpdateStatus = async (status: 'ACCEPTEE' | 'REJETE' | 'EN_COURS' | 'AVIS_FAVORABLE' | 'AVIS_DEFAVORABLE') => {
    if (status === 'REJETE' && !motifRejet.trim()) {
      toast.error('Le motif de rejet est obligatoire');
      return;
    }

    try {
      setIsUpdating(true);
      const updatedDossier = await updateStatusDemande(
        dossierId,
        status,
        status === 'REJETE' ? motifRejet : null
      );
      setDossier(updatedDossier);
      setRejectDialogOpen(false);
      setMotifRejet('');
      toast.success('Statut mis à jour avec succès', {
        description: `Le dossier a été ${translateStatus(status).toLowerCase()}.`,
      });
    } catch (error) {
      toast.error('Erreur lors de la mise à jour', {
        description: error instanceof Error ? error.message : 'Une erreur est survenue',
      });
    } finally {
      setIsUpdating(false);
    }
  };

  const handleRejectClick = () => {
    setRejectDialogOpen(true);
  };

  const handleRejectConfirm = () => {
    if (!motifRejet.trim()) {
      toast.error('Le motif de rejet est obligatoire');
      return;
    }
    handleUpdateStatus('REJETE');
  };

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div className="flex items-center gap-4">
          <Button variant="ghost" size="icon" onClick={onBack}>
            <ArrowLeft className="w-5 h-5" />
          </Button>
          <div>
            <h1 className="text-3xl">Détail du dossier</h1>
            <p className="text-gray-600">{dossierId}</p>
          </div>
        </div>
        <div className="flex items-center justify-center py-12">
          <Loader2 className="w-8 h-8 animate-spin text-gray-400" />
          <span className="ml-3 text-gray-600">Chargement des détails...</span>
        </div>
      </div>
    );
  }

  if (!dossier) {
    return (
      <div className="space-y-6">
        <div className="flex items-center gap-4">
          <Button variant="ghost" size="icon" onClick={onBack}>
            <ArrowLeft className="w-5 h-5" />
          </Button>
          <div>
            <h1 className="text-3xl">Détail du dossier</h1>
            <p className="text-gray-600">{dossierId}</p>
          </div>
        </div>
        <Alert>
          <AlertDescription>
            Dossier introuvable
          </AlertDescription>
        </Alert>
      </div>
    );
  }

  const formatDate = (dateString: string) => {
    try {
      return new Date(dateString).toLocaleString('fr-FR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      });
    } catch {
      return dateString;
    }
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
        {getStatusBadge(dossier.status)}
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
                    <p className="text-sm text-gray-600">CIN</p>
                    <p className="font-medium">{dossier.cin}</p>
                  </div>
                </div>
                <div className="flex gap-3">
                  <FileText className="w-5 h-5 text-gray-400 mt-0.5" />
                  <div>
                    <p className="text-sm text-gray-600">Type d'autorisation</p>
                    <p className="font-medium">{translateType(dossier.typeAuthorization)}</p>
                  </div>
                </div>
                <div className="flex gap-3">
                  <Calendar className="w-5 h-5 text-gray-400 mt-0.5" />
                  <div>
                    <p className="text-sm text-gray-600">Date de dépôt</p>
                    <p className="font-medium">{formatDate(dossier.date)}</p>
                  </div>
                </div>
                {dossier.nomCommune && (
                  <div className="flex gap-3">
                    <MapPin className="w-5 h-5 text-gray-400 mt-0.5" />
                    <div>
                      <p className="text-sm text-gray-600">Commune</p>
                      <p className="font-medium">{dossier.nomCommune}</p>
                    </div>
                  </div>
                )}
              </div>
              {dossier.motif && (
                <>
                  <Separator />
                  <div>
                    <p className="text-sm text-gray-600 mb-2">Motif de rejet</p>
                    <p className="text-red-600">{dossier.motif}</p>
                  </div>
                </>
              )}
            </CardContent>
          </Card>

          {/* Actions et commentaires */}
          <Card>
            <CardHeader>
              <CardTitle>Actions sur le dossier</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex flex-wrap gap-3">
                <Button 
                  className="bg-green-600 hover:bg-green-700"
                  onClick={() => handleUpdateStatus('ACCEPTEE')}
                  disabled={isUpdating || dossier.status === 'ACCEPTEE'}
                >
                  <CheckCircle2 className="w-4 h-4 mr-2" />
                  Accepter le dossier
                </Button>
                <Button 
                  variant="destructive"
                  onClick={handleRejectClick}
                  disabled={isUpdating || dossier.status === 'REJETE'}
                >
                  <XCircle className="w-4 h-4 mr-2" />
                  Rejeter le dossier
                </Button>
                <Button 
                  variant="outline"
                  onClick={() => handleUpdateStatus('EN_COURS')}
                  disabled={isUpdating || dossier.status === 'EN_COURS'}
                >
                  Mettre en cours
                </Button>
                <Button 
                  variant="outline"
                  onClick={() => handleUpdateStatus('AVIS_FAVORABLE')}
                  disabled={isUpdating || dossier.status === 'AVIS_FAVORABLE'}
                >
                  Avis favorable
                </Button>
                <Button 
                  variant="outline"
                  onClick={() => handleUpdateStatus('AVIS_DEFAVORABLE')}
                  disabled={isUpdating || dossier.status === 'AVIS_DEFAVORABLE'}
                >
                  Avis défavorable
                </Button>
              </div>
              {isUpdating && (
                <div className="flex items-center gap-2 text-sm text-gray-600">
                  <Loader2 className="w-4 h-4 animate-spin" />
                  Mise à jour en cours...
                </div>
              )}
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
              {dossier.documents && dossier.documents.length > 0 ? (
                <div className="space-y-3">
                  {dossier.documents.map((doc, index) => (
                    <div 
                      key={index}
                      className="flex items-start justify-between p-3 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
                    >
                      <div className="flex gap-3 flex-1 min-w-0">
                        <FileText className="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5" />
                        <div className="min-w-0 flex-1">
                          <p className="text-sm truncate">{doc.NomFichier}</p>
                          <p className="text-xs text-gray-500">ID: {doc.idFichier}</p>
                        </div>
                      </div>
                      <Button variant="ghost" size="icon" className="flex-shrink-0">
                        <Download className="w-4 h-4" />
                      </Button>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-center py-8 text-gray-500">
                  Aucun document joint
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      </div>

      {/* Dialog pour le rejet */}
      <Dialog open={rejectDialogOpen} onOpenChange={setRejectDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Rejeter le dossier</DialogTitle>
            <DialogDescription>
              Veuillez indiquer le motif de rejet. Ce champ est obligatoire.
            </DialogDescription>
          </DialogHeader>
          <div className="py-4">
            <Textarea
              placeholder="Saisissez le motif de rejet..."
              value={motifRejet}
              onChange={(e) => setMotifRejet(e.target.value)}
              rows={4}
            />
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => {
              setRejectDialogOpen(false);
              setMotifRejet('');
            }}>
              Annuler
            </Button>
            <Button 
              variant="destructive" 
              onClick={handleRejectConfirm}
              disabled={!motifRejet.trim() || isUpdating}
            >
              {isUpdating ? (
                <>
                  <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                  En cours...
                </>
              ) : (
                'Confirmer le rejet'
              )}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
