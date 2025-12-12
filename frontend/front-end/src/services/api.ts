// API Service - Connexion avec le backend
const API_BASE_URL = 'http://localhost:8000';

// Types pour les réponses API
export interface DocumentAdmin {
  idFichier: string;
  NomFichier: string;
}

export interface AdminDemande {
  id: string;
  status: string;
  temps: string;
  documents: DocumentAdmin[];
  cin: string;
  typeAutorization: string;
  motif: string | null;
}

export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      sorted: boolean;
      unsorted: boolean;
    };
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  size: number;
  number: number;
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}

export interface AdminDetailsDemande {
  id: string;
  cin: string;
  date: string;
  documents: DocumentAdmin[];
  status: string;
  typeAuthorization: string;
  motif: string | null;
  nomCommune: string | null;
}

export interface UpdateStatusRequest {
  status: 'ACCEPTEE' | 'REJETE' | 'EN_COURS' | 'AVIS_FAVORABLE' | 'AVIS_DEFAVORABLE';
  motifRejet?: string | null;
}

export interface StatsResponse {
  total: number;
  deposees: number;
  enCours: number;
  acceptees: number;
  rejetees: number;
  parCommune: Record<string, number>;
  parType: Record<string, number>;
}

export interface DemandeFilters {
  status?: string;
  type?: string;
  nomCommune?: string;
}

// Gestion des erreurs
const handleResponse = async (response: Response) => {
  if (!response.ok) {
    if (response.status === 401) {
      // Token invalide ou expiré
      localStorage.removeItem('authToken');
      throw new Error('Session expirée. Veuillez vous reconnecter.');
    }
    if (response.status === 403) {
      throw new Error('Accès refusé. Vous devez être administrateur.');
    }
    if (response.status === 404) {
      throw new Error('Ressource introuvable.');
    }
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || `Erreur ${response.status}: ${response.statusText}`);
  }
  return response.json();
};

// 1. Lister les demandes (paginé)
export const listerDemandes = async (
  page: number = 0,
  size: number = 10,
  filters: DemandeFilters = {}
): Promise<PageResponse<AdminDemande>> => {
  const params = new URLSearchParams({
    page: page.toString(),
    size: size.toString(),
    ...(filters.status && { status: filters.status }),
    ...(filters.type && { type: filters.type }),
    ...(filters.nomCommune && { nomCommune: filters.nomCommune }),
  });

  const token = localStorage.getItem('authToken');
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  };
  
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE_URL}/admin/demandes?${params}`, {
    method: 'GET',
    headers: headers,
  });

  return handleResponse(response);
};

// 2. Obtenir les détails d'une demande
export const getDetailsDemande = async (id: string): Promise<AdminDetailsDemande> => {
  const token = localStorage.getItem('authToken');
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  };
  
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE_URL}/admin/details/${id}`, {
    method: 'GET',
    headers: headers,
  });

  return handleResponse(response);
};

// 3. Mettre à jour le statut d'une demande
export const updateStatusDemande = async (
  id: string,
  status: UpdateStatusRequest['status'],
  motifRejet?: string | null
): Promise<AdminDetailsDemande> => {
  const body: UpdateStatusRequest = {
    status,
    ...(status === 'REJETE' && motifRejet && { motifRejet }),
  };

  const token = localStorage.getItem('authToken');
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  };
  
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE_URL}/admin/demande/${id}/status`, {
    method: 'PATCH',
    headers: headers,
    body: JSON.stringify(body),
  });

  return handleResponse(response);
};

// 4. Obtenir les statistiques
export const getStats = async (): Promise<StatsResponse> => {
  const token = localStorage.getItem('authToken');
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  };
  
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE_URL}/stats/getStats`, {
    method: 'GET',
    headers: headers,
  });

  return handleResponse(response);
};


