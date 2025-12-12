# 🔍 Guide de Diagnostic - Erreur 403 Forbidden

## 📋 Checklist de diagnostic

### 1. ✅ Vérifier que le backend a été redémarré

**Important:** Les modifications nécessitent un **redémarrage complet** du serveur Spring Boot.

```bash
# Arrêter le serveur (Ctrl+C)
# Puis le redémarrer
```

### 2. 🔑 Obtenir un NOUVEAU token

**Le problème principal:** Vous utilisez probablement un **token expiré**.

#### Étape 1: Login via Postman
```
POST http://localhost:8000/auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "votre_mot_de_passe"
}
```

#### Étape 2: Copier le token de la réponse
La réponse sera :
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Étape 3: Utiliser le token IMMÉDIATEMENT
Le token expire dans **1 heure** (3600000 ms).

Dans Postman, ajoutez dans les Headers :
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**⚠️ Important:** 
- Pas d'espace avant "Bearer"
- Un seul espace après "Bearer"
- Pas d'espace à la fin du token

### 3. 📝 Vérifier les logs du backend

Avec les niveaux de logs en DEBUG, vous devriez voir :

#### Si le token est valide :
```
DEBUG - Requête /admin/details/XXX: Token présent: true
DEBUG - Validation du token pour la requête: /admin/details/XXX
DEBUG - Token JWT valide
DEBUG - Sujet extrait du token: admin@example.com
DEBUG - Utilisateur chargé: admin@example.com, Rôles: [ROLE_ADMIN]
INFO - Authentification réussie pour l'utilisateur: admin@example.com sur /admin/details/XXX
```

#### Si le token est expiré :
```
WARN - Token JWT expiré: JWT expired X milliseconds ago
WARN - Token invalide pour la requête: /admin/details/XXX
```

#### Si le token est manquant :
```
DEBUG - Requête /admin/details/XXX: Token présent: false
DEBUG - Aucun token trouvé dans la requête: /admin/details/XXX
```

### 4. 🔐 Vérifier que l'utilisateur a le rôle ADMIN

Le endpoint `/admin/**` nécessite le rôle `ROLE_ADMIN`.

Vérifiez dans la base de données que votre utilisateur a bien ce rôle.

### 5. 🐛 Erreurs courantes

#### Erreur: "Token est expiré"
**Cause:** Token généré il y a plus d'1 heure
**Solution:** Faites un nouveau login

#### Erreur: "Token est malformé"
**Cause:** Format du header incorrect
**Solution:** Vérifiez que le header est exactement: `Authorization: Bearer <token>`

#### Erreur: "Pre-authenticated entry point called. Rejecting access"
**Cause:** Le token n'a pas été validé OU l'utilisateur n'a pas le bon rôle
**Solution:** 
1. Vérifiez les logs pour voir quelle étape échoue
2. Vérifiez que l'utilisateur a le rôle ADMIN

#### Erreur: "JWT_SECRET n'est pas défini"
**Cause:** La propriété JWT_SECRET n'est pas chargée
**Solution:** Vérifiez que `application.properties` contient `JWT_SECRET=...`

### 6. 🧪 Test complet dans Postman

#### Test 1: Login
```
POST http://localhost:8000/auth/login
Headers:
  Content-Type: application/json
Body:
{
  "email": "admin@example.com",
  "password": "votre_mot_de_passe"
}
```
**Attendu:** Réponse 200 avec un token

#### Test 2: Requête admin avec token
```
GET http://localhost:8000/admin/demandes?page=0&size=10
Headers:
  Authorization: Bearer <token_obtenu_dans_test_1>
```
**Attendu:** Réponse 200 avec les demandes

### 7. 🔄 Si l'erreur persiste

1. **Vérifiez les logs complets du backend** (console ou fichier de log)
2. **Copiez-collez les logs d'erreur** pour analyse
3. **Vérifiez que le JWT_SECRET est correct** dans `application.properties`
4. **Vérifiez la connexion à la base de données** et que l'utilisateur existe

### 8. 📊 Format attendu du token

Un token JWT valide a la structure :
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsImlhdCI6MTYzOTU2NzI5MCwiZXhwIjoxNjM5NTcwODkwLCJyb2xlcyI6WyJST0xFX0FETUlOIl19.signature
```

Vous pouvez décoder le token sur https://jwt.io pour vérifier son contenu.

### 9. ⚡ Commandes utiles pour diagnostiquer

#### Vérifier que le serveur écoute sur le bon port
```bash
netstat -ano | findstr :8000
```

#### Tester avec curl (Windows PowerShell)
```powershell
# Login
$response = Invoke-RestMethod -Uri "http://localhost:8000/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"admin@example.com","password":"votre_mot_de_passe"}'
$token = $response.token

# Test avec le token
Invoke-RestMethod -Uri "http://localhost:8000/admin/demandes?page=0&size=10" -Headers @{Authorization="Bearer $token"}
```

---

## 📞 Support

Si le problème persiste après avoir suivi ce guide, fournissez :
1. Les logs complets du backend (niveau DEBUG)
2. La requête exacte envoyée (URL, headers, body)
3. La réponse exacte reçue (code HTTP, body)

