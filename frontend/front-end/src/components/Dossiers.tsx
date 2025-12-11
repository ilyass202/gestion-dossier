import { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from './ui/table';
import { Badge } from './ui/badge';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Eye, Filter, ChevronLeft, ChevronRight } from 'lucide-react';
import { listerDemandes } from '../services/api';
import type { AdminDemande } from '../services/api';
import { toast } from 'sonner';

interface DossiersProps {
  onViewDetail: (dossierId: string) => void;
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

export function Dossiers({ onViewDetail }: DossiersProps) {
  const [dossiers, setDossiers] = useState<AdminDemande[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [typeFilter, setTypeFilter] = useState<string>('');
  const [nomCommuneFilter, setNomCommuneFilter] = useState<string>('');

  const fetchDossiers = async () => {
    try {
      setIsLoading(true);
      const filters: any = {};
      if (statusFilter) filters.status = statusFilter;
      if (typeFilter) filters.type = typeFilter;
      if (nomCommuneFilter) filters.nomCommune = nomCommuneFilter;

      const response = await listerDemandes(page, size, filters);
      setDossiers(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (error) {
      toast.error('Erreur lors du chargement des dossiers', {
        description: error instanceof Error ? error.message : 'Une erreur est survenue',
      });
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchDossiers();
  }, [page, statusFilter, typeFilter, nomCommuneFilter]);

  const getStatusBadge = (statut: string) => {
    const statusConfig: Record<string, { variant: 'default' | 'secondary' | 'destructive' | 'outline', className: string }> = {
      'ACCEPTEE': { variant: 'default', className: 'bg-green-100 text-green-700 hover:bg-green-100' },
      'EN_COURS': { variant: 'outline', className: 'bg-blue-100 text-blue-700 hover:bg-blue-100' },
      'AVIS_FAVORABLE': { variant: 'default', className: 'bg-green-100 text-green-700 hover:bg-green-100' },
      'REJETE': { variant: 'destructive', className: 'bg-red-100 text-red-700 hover:bg-red-100' },
      'AVIS_DEFAVORABLE': { variant: 'destructive', className: 'bg-red-100 text-red-700 hover:bg-red-100' },
    };
    
    const config = statusConfig[statut] || { variant: 'default' as const, className: 'bg-gray-100 text-gray-700 hover:bg-gray-100' };
    return <Badge variant={config.variant} className={config.className}>{translateStatus(statut)}</Badge>;
  };

  const handleStatusFilterChange = (value: string) => {
    setStatusFilter(value === 'all' ? '' : value);
    setPage(0);
  };

  const handleTypeFilterChange = (value: string) => {
    setTypeFilter(value === 'all' ? '' : value);
    setPage(0);
  };

  const handleCommuneFilterChange = (value: string) => {
    setNomCommuneFilter(value);
    setPage(0);
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl mb-2">Gestion des Dossiers</h1>
        <p className="text-gray-600">Rechercher et consulter les demandes d'autorisation</p>
      </div>

      <Card>
        <CardHeader>
          <div className="flex flex-col gap-4">
            <CardTitle>Liste des dossiers</CardTitle>
            <div className="flex flex-col sm:flex-row gap-3">
              <Select value={statusFilter || 'all'} onValueChange={handleStatusFilterChange}>
                <SelectTrigger className="w-full sm:w-48">
                  <Filter className="w-4 h-4 mr-2" />
                  <SelectValue placeholder="Statut" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">Tous les statuts</SelectItem>
                  <SelectItem value="EN_COURS">En cours</SelectItem>
                  <SelectItem value="ACCEPTEE">Acceptée</SelectItem>
                  <SelectItem value="REJETE">Rejetée</SelectItem>
                  <SelectItem value="AVIS_FAVORABLE">Avis favorable</SelectItem>
                  <SelectItem value="AVIS_DEFAVORABLE">Avis défavorable</SelectItem>
                </SelectContent>
              </Select>
              <Select value={typeFilter || 'all'} onValueChange={handleTypeFilterChange}>
                <SelectTrigger className="w-full sm:w-48">
                  <SelectValue placeholder="Type" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">Tous les types</SelectItem>
                  <SelectItem value="PERMIS_CONSTRUCTION">Permis de construire</SelectItem>
                  <SelectItem value="AUTORISATION_OUVERTURE">Autorisation d'ouverture</SelectItem>
                  <SelectItem value="CERTIFICAT_URBANISME">Certificat d'urbanisme</SelectItem>
                </SelectContent>
              </Select>
              <Input
                placeholder="Filtrer par commune..."
                value={nomCommuneFilter}
                onChange={(e) => handleCommuneFilterChange(e.target.value)}
                className="flex-1"
              />
            </div>
          </div>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="text-center py-8 text-gray-500">Chargement des dossiers...</div>
          ) : (
            <>
              <div className="overflow-x-auto">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>ID Dossier</TableHead>
                      <TableHead>CIN</TableHead>
                      <TableHead>Type d'autorisation</TableHead>
                      <TableHead>Date de dépôt</TableHead>
                      <TableHead>Statut</TableHead>
                      <TableHead className="text-right">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {dossiers.map((dossier) => (
                      <TableRow key={dossier.id}>
                        <TableCell className="font-medium">{dossier.id}</TableCell>
                        <TableCell>{dossier.cin}</TableCell>
                        <TableCell>{translateType(dossier.typeAutorization)}</TableCell>
                        <TableCell>{new Date(dossier.temps).toLocaleDateString('fr-FR')}</TableCell>
                        <TableCell>{getStatusBadge(dossier.status)}</TableCell>
                        <TableCell className="text-right">
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => onViewDetail(dossier.id)}
                          >
                            <Eye className="w-4 h-4 mr-2" />
                            Voir détails
                          </Button>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
              {dossiers.length === 0 && !isLoading && (
                <div className="text-center py-8 text-gray-500">
                  Aucun dossier trouvé
                </div>
              )}
              {/* Pagination */}
              {totalPages > 1 && (
                <div className="flex items-center justify-between mt-4">
                  <div className="text-sm text-gray-600">
                    Page {page + 1} sur {totalPages} ({totalElements} dossiers)
                  </div>
                  <div className="flex gap-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setPage(p => Math.max(0, p - 1))}
                      disabled={page === 0}
                    >
                      <ChevronLeft className="w-4 h-4 mr-1" />
                      Précédent
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                      disabled={page >= totalPages - 1}
                    >
                      Suivant
                      <ChevronRight className="w-4 h-4 ml-1" />
                    </Button>
                  </div>
                </div>
              )}
            </>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
