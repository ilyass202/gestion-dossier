# Requêtes cURL pour tester l'API

## Configuration de base

```bash
# Définir les variables
BASE_URL="http://localhost:8000"
TOKEN=""  # Sera rempli après le login
```

## 1. Authentification

### Login
```bash
curl -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "votre_mot_de_passe"
  }' \
  -w "\n\nHTTP Status: %{http_code}\n"

# Sauvegarder le token (Linux/Mac)
TOKEN=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "votre_mot_de_passe"
  }' | grep -o '"token":"[^"]*' | cut -d'"' -f4)

echo "Token: $TOKEN"
```

**Windows PowerShell**:
```powershell
$response = Invoke-RestMethod -Uri "http://localhost:8000/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"email":"admin@example.com","password":"votre_mot_de_passe"}'
$token = $response.token
Write-Host "Token: $token"
```

## 2. Endpoints Publics

### Envoyer une demande
```bash
curl -X POST "$BASE_URL/demande/envoyerDemande" \
  -F "typeAutorisation=AUTORISATION_OUVERTURE" \
  -F "cinDemandeur=AB123456" \
  -F "latitude=33.5731" \
  -F "longitude=-7.5898" \
  -F "files=@/chemin/vers/fichier.pdf" \
  -w "\n\nHTTP Status: %{http_code}\n"
```

### Suivre une demande
```bash
curl -X GET "$BASE_URL/demande/track?idDemande=DEMANDE_ID&cinDemandeur=AB123456" \
  -w "\n\nHTTP Status: %{http_code}\n"
```

### Télécharger un document
```bash
curl -X GET "$BASE_URL/demande/telecharger/DOCUMENT_ID?cin=AB123456&demandeId=DEMANDE_ID" \
  -o "document_telecharge.pdf" \
  -w "\n\nHTTP Status: %{http_code}\n"
```

## 3. Endpoints Admin (nécessitent le token)

### Lister les demandes
```bash
curl -X GET "$BASE_URL/admin/demandes?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\n\nHTTP Status: %{http_code}\n"

# Avec filtres
curl -X GET "$BASE_URL/admin/demandes?page=0&size=10&status=EN_COURS&type=AUTORISATION_OUVERTURE" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\n\nHTTP Status: %{http_code}\n"
```

### Détails d'une demande
```bash
curl -X GET "$BASE_URL/admin/details/DEMANDE_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\n\nHTTP Status: %{http_code}\n"
```

### Mettre à jour le statut - Accepter
```bash
curl -X PATCH "$BASE_URL/admin/demande/DEMANDE_ID/status" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "ACCEPTEE",
    "motifRejet": null
  }' \
  -w "\n\nHTTP Status: %{http_code}\n"
```

### Mettre à jour le statut - Rejeter
```bash
curl -X PATCH "$BASE_URL/admin/demande/DEMANDE_ID/status" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "REJETE",
    "motifRejet": "Documents incomplets"
  }' \
  -w "\n\nHTTP Status: %{http_code}\n"
```

### Mettre à jour le statut - Avis Favorable
```bash
curl -X PATCH "$BASE_URL/admin/demande/DEMANDE_ID/status" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "AVIS_FAVORABLE",
    "motifRejet": null
  }' \
  -w "\n\nHTTP Status: %{http_code}\n"
```

### Mettre à jour le statut - Avis Défavorable
```bash
curl -X PATCH "$BASE_URL/admin/demande/DEMANDE_ID/status" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "AVIS_DEFAVORABLE",
    "motifRejet": "Non conforme aux réglementations"
  }' \
  -w "\n\nHTTP Status: %{http_code}\n"
```

### Obtenir les statistiques
```bash
curl -X GET "$BASE_URL/stats/getStats" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\n\nHTTP Status: %{http_code}\n"
```

## 4. Script de test complet (Linux/Mac)

```bash
#!/bin/bash

BASE_URL="http://localhost:8000"
EMAIL="admin@example.com"
PASSWORD="votre_mot_de_passe"

echo "=== 1. Login ==="
RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}")

TOKEN=$(echo $RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "❌ Erreur: Impossible de récupérer le token"
  echo "Réponse: $RESPONSE"
  exit 1
fi

echo "✅ Token récupéré: ${TOKEN:0:20}..."
echo ""

echo "=== 2. Lister les demandes ==="
curl -s -X GET "$BASE_URL/admin/demandes?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

echo "=== 3. Obtenir les statistiques ==="
curl -s -X GET "$BASE_URL/stats/getStats" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

echo "✅ Tests terminés"
```

## 5. Script de test complet (Windows PowerShell)

```powershell
$baseUrl = "http://localhost:8000"
$email = "admin@example.com"
$password = "votre_mot_de_passe"

Write-Host "=== 1. Login ==="
$loginBody = @{
    email = $email
    password = $password
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginBody
    
    $token = $response.token
    Write-Host "✅ Token récupéré: $($token.Substring(0, [Math]::Min(20, $token.Length)))..."
    Write-Host ""
} catch {
    Write-Host "❌ Erreur: Impossible de récupérer le token"
    Write-Host $_.Exception.Message
    exit 1
}

$headers = @{
    "Authorization" = "Bearer $token"
}

Write-Host "=== 2. Lister les demandes ==="
try {
    $demandes = Invoke-RestMethod -Uri "$baseUrl/admin/demandes?page=0&size=10" `
        -Method GET `
        -Headers $headers
    $demandes | ConvertTo-Json -Depth 10
    Write-Host ""
} catch {
    Write-Host "❌ Erreur lors de la récupération des demandes"
    Write-Host $_.Exception.Message
}

Write-Host "=== 3. Obtenir les statistiques ==="
try {
    $stats = Invoke-RestMethod -Uri "$baseUrl/stats/getStats" `
        -Method GET `
        -Headers $headers
    $stats | ConvertTo-Json -Depth 10
    Write-Host ""
} catch {
    Write-Host "❌ Erreur lors de la récupération des statistiques"
    Write-Host $_.Exception.Message
}

Write-Host "✅ Tests terminés"
```

## Notes

- Remplacez `DEMANDE_ID`, `DOCUMENT_ID`, `AB123456`, etc. par les valeurs réelles de votre base de données
- Sur Windows, utilisez PowerShell pour les scripts ou adaptez les commandes curl pour Git Bash
- Pour un affichage JSON formaté, utilisez `jq` (Linux/Mac) ou `ConvertTo-Json` (PowerShell)

