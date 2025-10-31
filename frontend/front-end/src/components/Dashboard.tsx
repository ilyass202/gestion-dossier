import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { FileText, CheckCircle2, XCircle, Clock } from 'lucide-react';
import { BarChart, Bar, LineChart, Line, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const statsData = [
  { title: 'Dossiers déposés', value: '324', change: '+12%', icon: FileText, color: 'bg-blue-500' },
  { title: 'Dossiers validés', value: '187', change: '+8%', icon: CheckCircle2, color: 'bg-green-500' },
  { title: 'Dossiers rejetés', value: '45', change: '-3%', icon: XCircle, color: 'bg-red-500' },
  { title: 'En attente', value: '92', change: '+5%', icon: Clock, color: 'bg-orange-500' },
];

const monthlyData = [
  { mois: 'Jan', deposes: 45, valides: 30, rejetes: 8 },
  { mois: 'Fev', deposes: 52, valides: 35, rejetes: 10 },
  { mois: 'Mar', deposes: 48, valides: 32, rejetes: 7 },
  { mois: 'Avr', deposes: 61, valides: 40, rejetes: 12 },
  { mois: 'Mai', deposes: 55, valides: 38, rejetes: 9 },
  { mois: 'Juin', deposes: 63, valides: 42, rejetes: 11 },
];

const typeData = [
  { name: 'Permis de construire', value: 145, color: '#3b82f6' },
  { name: 'Autorisation de lotir', value: 89, color: '#10b981' },
  { name: 'Autorisation de morceler', value: 54, color: '#f59e0b' },
  { name: 'Autres autorisations', value: 36, color: '#8b5cf6' },
];

export function Dashboard() {
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