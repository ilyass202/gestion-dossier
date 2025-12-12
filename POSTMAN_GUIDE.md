# Guide d'utilisation Postman - API Gestion de Dossiers

## 📋 Prérequis

1. **Importer la collection Postman**
   - Ouvrir Postman
   - Cliquer sur **Import**
   - Sélectionner le fichier `POSTMAN_COLLECTION.json`

2. **Vérifier les variables de collection**
   - `baseUrl`: `http://localhost:8000` (par défaut)
   - `token`: Sera automatiquement rempli après la connexion

## 🔐 Authentification

### 1. Login (POST /auth/login)

**Endpoint**: `POST {{baseUrl}}/auth/login`

**Headers**:
```
Content-Type: application/json
```

**Body (JSON)**:
```json
{
    "email": "admin@example.com",
    "password": "votre_mot_de_passe"
}
```

**Réponse attendue**:
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**⚠️ Important**: Le token est automatiquement sauvegardé dans la variable de collection `token` grâce au script de test inclus. Vous n'avez pas besoin de le copier manuellement.

## 📝 Endpoints Publics (sans authentification)

### 2. Envoyer une Demande (POST /demande/envoyerDemande)

**Endpoint**: `POST {{baseUrl}}/demande/envoyerDemande`

**Type**: `multipart/form-data`

**Paramètres**:
- `typeAutorisation` (string): Type d'autorisation
- `cinDemandeur` (string): CIN du demandeur (ex: "AB123456")
- `latitude` (double): Latitude (ex: 33.5731)
- `longitude` (double): Longitude (ex: -7.5898)
- `files` (file, optionnel): Fichiers à joindre

**Exemple de valeurs**:
```
typeAutorisation: AUTORISATION_OUVERTURE
cinDemandeur: AB123456
latitude: 33.5731
longitude: -7.5898
files: [sélectionner un ou plusieurs fichiers]
```

### 3. Suivre une Demande (GET /demande/track)

**Endpoint**: `GET {{baseUrl}}/demande/track?idDemande=DEMANDE_ID&cinDemandeur=AB123456`

**Paramètres de requête**:
- `idDemande` (string): ID de la demande
- `cinDemandeur` (string): CIN du demandeur

**Exemple**:
```
GET {{baseUrl}}/demande/track?idDemande=12345&cinDemandeur=AB123456
```

### 4. Télécharger un Document (GET /demande/telecharger/{documentId})

**Endpoint**: `GET {{baseUrl}}/demande/telecharger/DOCUMENT_ID?cin=AB123456&demandeId=DEMANDE_ID`

**Paramètres**:
- `documentId` (path): ID du document
- `cin` (query): CIN du demandeur
- `demandeId` (query): ID de la demande

## 👨‍💼 Endpoints Admin (nécessitent authentification)

**⚠️ Tous les endpoints admin nécessitent le header d'autorisation**:
```
Authorization: Bearer {{token}}
```

### 5. Lister les Demandes (GET /admin/demandes)

**Endpoint**: `GET {{baseUrl}}/admin/demandes`

**Paramètres de requête (tous optionnels)**:
- `page` (int, défaut: 0): Numéro de page
- `size` (int, défaut: 10): Taille de la page
- `status` (string): Filtre par statut
  - Valeurs possibles: `ACCEPTEE`, `REJETE`, `EN_COURS`, `AVIS_FAVORABLE`, `AVIS_DEFAVORABLE`
- `type` (string): Filtre par type d'autorisation
- `nomCommune` (string): Filtre par nom de commune

**Exemples**:
```
# Toutes les demandes
GET {{baseUrl}}/admin/demandes?page=0&size=10

# Demandes en cours
GET {{baseUrl}}/admin/demandes?page=0&size=10&status=EN_COURS

# Demandes acceptées d'un type spécifique
GET {{baseUrl}}/admin/demandes?page=0&size=10&status=ACCEPTEE&type=AUTORISATION_OUVERTURE

# Demandes rejetées d'une commune
GET {{baseUrl}}/admin/demandes?page=0&size=10&status=REJETE&nomCommune=Casablanca
```

**Réponse**: Paginée avec la structure suivante:
```json
{
    "content": [...],
    "totalElements": 100,
    "totalPages": 10,
    "number": 0,
    "size": 10
}
```

### 6. Détails d'une Demande (GET /admin/details/{id})

**Endpoint**: `GET {{baseUrl}}/admin/details/DEMANDE_ID`

**Paramètres**:
- `id` (path): ID de la demande

### 7. Mettre à jour le Statut (PATCH /admin/demande/{id}/status)

**Endpoint**: `PATCH {{baseUrl}}/admin/demande/DEMANDE_ID/status`

**Headers**:
```
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Body (JSON)**:
```json
{
    "status": "ACCEPTEE",
    "motifRejet": null
}
```

**Statuts possibles**:
- `ACCEPTEE`: Demande acceptée
- `REJETE`: Demande rejetée (nécessite `motifRejet`)
- `EN_COURS`: Demande en cours de traitement
- `AVIS_FAVORABLE`: Avis favorable donné
- `AVIS_DEFAVORABLE`: Avis défavorable (peut inclure `motifRejet`)

**Exemples de body**:

**Accepter**:
```json
{
    "status": "ACCEPTEE",
    "motifRejet": null
}
```

**Rejeter**:
```json
{
    "status": "REJETE",
    "motifRejet": "Documents incomplets ou informations manquantes"
}
```

**Avis favorable**:
```json
{
    "status": "AVIS_FAVORABLE",
    "motifRejet": null
}
```

**Avis défavorable**:
```json
{
    "status": "AVIS_DEFAVORABLE",
    "motifRejet": "Non conforme aux réglementations en vigueur"
}
```

**En cours**:
```json
{
    "status": "EN_COURS",
    "motifRejet": null
}
```

### 8. Obtenir les Statistiques (GET /stats/getStats)

**Endpoint**: `GET {{baseUrl}}/stats/getStats`

**Réponse**: Retourne les statistiques globales de l'application.

## 🔄 Ordre recommandé de test

1. **Login** → Récupérer le token
2. **Envoyer Demande** (public) → Créer une demande de test
3. **Lister Demandes** → Vérifier que la demande apparaît
4. **Détails Demande** → Vérifier les détails (utiliser l'ID de l'étape précédente)
5. **Mettre à jour Statut** → Changer le statut de la demande
6. **Obtenir Statistiques** → Vérifier les stats globales

## 🐛 Dépannage

### Erreur 401 Unauthorized
- Vérifier que vous avez bien exécuté la requête **Login** en premier
- Vérifier que le token est bien sauvegardé dans les variables de collection
- Vérifier que le header `Authorization: Bearer {{token}}` est présent

### Erreur 403 Forbidden
- Vérifier que l'utilisateur connecté a le rôle **ADMIN**
- Vérifier que le token n'est pas expiré (expiration par défaut: 24h)
- Relancer la requête **Login** pour obtenir un nouveau token

### Erreur 400 Bad Request
- Vérifier le format JSON du body
- Vérifier que tous les paramètres requis sont fournis
- Vérifier que les valeurs des enums (statut, type) sont correctes

### Erreur CORS
- Vérifier que le backend est bien démarré sur le port 8000
- Vérifier que l'URL dans `baseUrl` est correcte

## 📌 Notes importantes

1. **Token automatique**: La requête Login contient un script de test qui sauvegarde automatiquement le token. Vous n'avez pas besoin de le copier manuellement.

2. **Variables**: Les variables `{{baseUrl}}` et `{{token}}` sont utilisées automatiquement dans toutes les requêtes.

3. **CORS**: Le backend autorise les requêtes depuis `http://localhost:*` et `http://127.0.0.1:*`.

4. **Statuts**: Les statuts doivent être en majuscules et correspondre exactement aux valeurs de l'enum: `ACCEPTEE`, `REJETE`, `EN_COURS`, `AVIS_FAVORABLE`, `AVIS_DEFAVORABLE`.

5. **Pagination**: Les endpoints de liste utilisent une pagination basée sur Spring Data. La première page est `page=0`.

