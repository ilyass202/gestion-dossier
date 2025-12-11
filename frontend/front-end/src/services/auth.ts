// Service d'authentification
const API_BASE_URL = 'http://localhost:8000';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  type: string;
  id: number;
  username: string;
  email: string;
  roles: string[];
}

// Fonction de connexion
export const login = async (credentials: LoginRequest): Promise<LoginResponse> => {
  const response = await fetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(credentials),
  });

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error('Email ou mot de passe incorrect.');
    }
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || `Erreur ${response.status}: ${response.statusText}`);
  }

  const data = await response.json();
  
  // Stocker le token dans localStorage
  if (data.token) {
    localStorage.setItem('authToken', data.token);
  }

  return data;
};

// Fonction de déconnexion
export const logout = (): void => {
  localStorage.removeItem('authToken');
};

// Vérifier si l'utilisateur est connecté
export const isAuthenticated = (): boolean => {
  return !!localStorage.getItem('authToken');
};

// Obtenir le token
export const getToken = (): string | null => {
  return localStorage.getItem('authToken');
};

