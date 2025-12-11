import { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { FileText, CheckCircle2, XCircle, Clock } from 'lucide-react';
import { BarChart, Bar, LineChart, Line, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { getStats } from '../services/api';
import type { StatsResponse } from '../services/api';
import { toast } from 'sonner';

export function Dashboard() {
  const [stats, setStats] = useState<StatsResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        setIsLoading(true);
        const data = await getStats();
        setStats(data);
      } catch (error) {
        toast.error('Erreur lors du chargement des statistiques', {
          description: error instanceof Error ? error.message : 'Une erreur est survenue',
        });
      } finally {
        setIsLoading(false);
      }
    };

    fetchStats();
  }, []);

  // Préparer les données pour les cartes de statistiques
  const statsData = stats ? [
    { title: 'Dossiers déposés', value: stats.deposees.toString(), change: '', icon: FileText, color: 'bg-blue-500' },
    { title: 'Dossiers validés', value: stats.acceptees.toString(), change: '', icon: CheckCircle2, color: 'bg-green-500' },
    { title: 'Dossiers rejetés', value: stats.rejetees.toString(), change: '', icon: XCircle, color: 'bg-red-500' },
    { title: 'En cours', value: stats.enCours.toString(), change: '', icon: Clock, color: 'bg-orange-500' },
  ] : [];

  // Préparer les données pour le graphique par type
  const typeData = stats ? Object.entries(stats.parType).map(([name, value], index) => {
    const colors = ['#3b82f6', '#10b981', '#f59e0b', '#8b5cf6', '#ef4444', '#06b6d4'];
    return {
      name,
      value,
      color: colors[index % colors.length],
    };
  }) : [];

  // Données mensuelles simulées (car l'API ne fournit pas ces données)
  const monthlyData = [
    { mois: 'Jan', deposes: 45, valides: 30, rejetes: 8 },
    { mois: 'Fev', deposes: 52, valides: 35, rejetes: 10 },
    { mois: 'Mar', deposes: 48, valides: 32, rejetes: 7 },
    { mois: 'Avr', deposes: 61, valides: 40, rejetes: 12 },
    { mois: 'Mai', deposes: 55, valides: 38, rejetes: 9 },
    { mois: 'Juin', deposes: 63, valides: 42, rejetes: 11 },
  ];
  if (isLoading) {
    return (
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl mb-2">Dashboard</h1>
          <p className="text-gray-600">Vue d'ensemble de la gestion des dossiers</p>
        </div>
        <div className="text-center py-12 text-gray-500">Chargement des statistiques...</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl mb-2">Dashboard</h1>
        <p className="text-gray-600">Vue d'ensemble de la gestion des dossiers</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {statsData.map((stat, index) => {
          const Icon = stat.icon;
          return (
            <Card key={index}>
              <CardContent className="p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-gray-600">{stat.title}</p>
                    <p className="text-3xl mt-2">{stat.value}</p>
                    <p className={`text-sm mt-1 ${stat.change.startsWith('+') ? 'text-green-600' : 'text-red-600'}`}>
                      {stat.change} ce mois
                    </p>
                  </div>
                  <div className={`${stat.color} w-12 h-12 rounded-lg flex items-center justify-center`}>
                    <Icon className="w-6 h-6 text-white" />
                  </div>
                </div>
              </CardContent>
            </Card>
          );
        })}
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Bar Chart */}
        <Card>
          <CardHeader>
            <CardTitle>Évolution mensuelle des dossiers</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={monthlyData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="mois" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="deposes" fill="#3b82f6" name="Déposés" />
                <Bar dataKey="valides" fill="#10b981" name="Validés" />
                <Bar dataKey="rejetes" fill="#ef4444" name="Rejetés" />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        {/* Pie Chart */}
        <Card>
          <CardHeader>
            <CardTitle>Répartition par type d'autorisation</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={typeData}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, value }) => {
                    const total = typeData.reduce((sum, item) => sum + item.value, 0);
                    const percent = ((value as number / total) * 100).toFixed(0);
                    return `${name}: ${percent}%`;
                  }}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {typeData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>
      </div>

      {/* Line Chart */}
      <Card>
        <CardHeader>
          <CardTitle>Tendance de traitement des dossiers</CardTitle>
        </CardHeader>
        <CardContent>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={monthlyData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="mois" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Line type="monotone" dataKey="deposes" stroke="#3b82f6" name="Déposés" strokeWidth={2} />
              <Line type="monotone" dataKey="valides" stroke="#10b981" name="Validés" strokeWidth={2} />
            </LineChart>
          </ResponsiveContainer>
        </CardContent>
      </Card>
    </div>
  );
}