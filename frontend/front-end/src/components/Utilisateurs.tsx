import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from './ui/table';
import { Badge } from './ui/badge';
import { Button } from './ui/button';
import { Avatar, AvatarFallback } from './ui/avatar';
import { UserPlus, Edit, Trash2 } from 'lucide-react';

const mockUtilisateurs = [
  { id: 1, nom: 'Ahmed Bennani', email: 'ahmed.bennani@agence.ma', role: 'Administrateur', statut: 'Actif' },
  { id: 2, nom: 'Fatima Alami', email: 'fatima.alami@agence.ma', role: 'Agent', statut: 'Actif' },
  { id: 3, nom: 'Karim Idrissi', email: 'karim.idrissi@agence.ma', role: 'Agent', statut: 'Actif' },
  { id: 4, nom: 'Samira Tazi', email: 'samira.tazi@agence.ma', role: 'Superviseur', statut: 'Actif' },
  { id: 5, nom: 'Omar Fassi', email: 'omar.fassi@agence.ma', role: 'Agent', statut: 'Inactif' },
];

export function Utilisateurs() {
  const getInitials = (nom: string) => {
    return nom
      .split(' ')
      .map(n => n[0])
      .join('')
      .toUpperCase();
  };

  const getRoleBadge = (role: string) => {
    const roleConfig: Record<string, string> = {
      'Administrateur': 'bg-purple-100 text-purple-700',
      'Superviseur': 'bg-blue-100 text-blue-700',
      'Agent': 'bg-gray-100 text-gray-700',
    };
    return <Badge className={roleConfig[role] || ''}>{role}</Badge>;
  };

  const getStatusBadge = (statut: string) => {
    return statut === 'Actif' 
      ? <Badge className="bg-green-100 text-green-700">Actif</Badge>
      : <Badge className="bg-gray-100 text-gray-700">Inactif</Badge>;
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-start">
        <div>
          <h1 className="text-3xl mb-2">Gestion des Utilisateurs</h1>
          <p className="text-gray-600">Gérer les agents et administrateurs de la plateforme</p>
        </div>
        <Button className="bg-blue-600 hover:bg-blue-700">
          <UserPlus className="w-4 h-4 mr-2" />
          Nouvel utilisateur
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Liste des utilisateurs</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Utilisateur</TableHead>
                  <TableHead>Email</TableHead>
                  <TableHead>Rôle</TableHead>
                  <TableHead>Statut</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {mockUtilisateurs.map((user) => (
                  <TableRow key={user.id}>
                    <TableCell>
                      <div className="flex items-center gap-3">
                        <Avatar>
                          <AvatarFallback className="bg-blue-100 text-blue-700">
                            {getInitials(user.nom)}
                          </AvatarFallback>
                        </Avatar>
                        <span>{user.nom}</span>
                      </div>
                    </TableCell>
                    <TableCell>{user.email}</TableCell>
                    <TableCell>{getRoleBadge(user.role)}</TableCell>
                    <TableCell>{getStatusBadge(user.statut)}</TableCell>
                    <TableCell className="text-right">
                      <div className="flex justify-end gap-2">
                        <Button variant="ghost" size="icon">
                          <Edit className="w-4 h-4" />
                        </Button>
                        <Button variant="ghost" size="icon">
                          <Trash2 className="w-4 h-4 text-red-600" />
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
