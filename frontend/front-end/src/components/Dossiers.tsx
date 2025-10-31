import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from './ui/table';
import { Badge } from './ui/badge';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Search, Eye, Filter } from 'lucide-react';

const mockDossiers = [
  { id: 'DOS-2025-001', demandeur: 'Mohamed Alami', type: 'Permis de construire', date: '2025-01-15', statut: 'En attente' },
  { id: 'DOS-2025-002', demandeur: 'Fatima Bennani', type: 'Autorisation de lotir', date: '2025-01-18', statut: 'Validé' },
  { id: 'DOS-2025-003', demandeur: 'Ahmed Tahiri', type: 'Permis de construire', date: '2025-01-20', statut: 'En cours' },
  { id: 'DOS-2025-004', demandeur: 'Samira Idrissi', type: 'Autorisation de morceler', date: '2025-01-22', statut: 'Rejeté' },
  { id: 'DOS-2025-005', demandeur: 'Karim Ziani', type: 'Permis de construire', date: '2025-01-25', statut: 'Validé' },
  { id: 'DOS-2025-006', demandeur: 'Nadia Fassi', type: 'Autorisation de lotir', date: '2025-01-28', statut: 'En attente' },
  { id: 'DOS-2025-007', demandeur: 'Omar Benjelloun', type: 'Permis de construire', date: '2025-02-01', statut: 'En cours' },
  { id: 'DOS-2025-008', demandeur: 'Latifa Elkhaldi', type: 'Autorisation de morceler', date: '2025-02-03', statut: 'En attente' },
  { id: 'DOS-2025-009', demandeur: 'Youssef Amrani', type: 'Permis de construire', date: '2025-02-05', statut: 'Validé' },
  { id: 'DOS-2025-010', demandeur: 'Rachida Tazi', type: 'Autorisation de lotir', date: '2025-02-08', statut: 'En cours' },
];

interface DossiersProps {
  onViewDetail: (dossierId: string) => void;
}

export function Dossiers({ onViewDetail }: DossiersProps) {
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');

  const getStatusBadge = (statut: string) => {
    const statusConfig: Record<string, { variant: 'default' | 'secondary' | 'destructive' | 'outline', className: string }> = {
      'Validé': { variant: 'default', className: 'bg-green-100 text-green-700 hover:bg-green-100' },
      'En attente': { variant: 'secondary', className: 'bg-orange-100 text-orange-700 hover:bg-orange-100' },
      'En cours': { variant: 'outline', className: 'bg-blue-100 text-blue-700 hover:bg-blue-100' },
      'Rejeté': { variant: 'destructive', className: 'bg-red-100 text-red-700 hover:bg-red-100' },
    };
    
    const config = statusConfig[statut] || { variant: 'default' as const, className: '' };
    return <Badge variant={config.variant} className={config.className}>{statut}</Badge>;
  };

  const filteredDossiers = mockDossiers.filter(dossier => {
    const matchesSearch = 
      dossier.id.toLowerCase().includes(searchTerm.toLowerCase()) ||
      dossier.demandeur.toLowerCase().includes(searchTerm.toLowerCase()) ||
      dossier.type.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesStatus = statusFilter === 'all' || dossier.statut === statusFilter;
    
    return matchesSearch && matchesStatus;
  });

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl mb-2">Gestion des Dossiers</h1>
        <p className="text-gray-600">Rechercher et consulter les demandes d'autorisation</p>
      </div>

      <Card>
        <CardHeader>
          <div className="flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between">
            <CardTitle>Liste des dossiers</CardTitle>
            <div className="flex flex-col sm:flex-row gap-3 w-full sm:w-auto">
              <div className="relative flex-1 sm:w-64">
                <Search className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                <Input
                  placeholder="Rechercher..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10"
                />
              </div>
              <Select value={statusFilter} onValueChange={setStatusFilter}>
                <SelectTrigger className="w-full sm:w-40">
                  <Filter className="w-4 h-4 mr-2" />
                  <SelectValue placeholder="Statut" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">Tous</SelectItem>
                  <SelectItem value="En attente">En attente</SelectItem>
                  <SelectItem value="En cours">En cours</SelectItem>
                  <SelectItem value="Validé">Validé</SelectItem>
                  <SelectItem value="Rejeté">Rejeté</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>ID Dossier</TableHead>
                  <TableHead>Nom du demandeur</TableHead>
                  <TableHead>Type d'autorisation</TableHead>
                  <TableHead>Date de dépôt</TableHead>
                  <TableHead>Statut</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredDossiers.map((dossier) => (
                  <TableRow key={dossier.id}>
                    <TableCell>{dossier.id}</TableCell>
                    <TableCell>{dossier.demandeur}</TableCell>
                    <TableCell>{dossier.type}</TableCell>
                    <TableCell>{dossier.date}</TableCell>
                    <TableCell>{getStatusBadge(dossier.statut)}</TableCell>
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
          {filteredDossiers.length === 0 && (
            <div className="text-center py-8 text-gray-500">
              Aucun dossier trouvé
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
