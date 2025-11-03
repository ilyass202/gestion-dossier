# Guide Test Postman - Endpoints DemandeController

**URL de base:** `http://localhost:8000`

---

## 🔍 **1. GET /demande/track**

### **Étape 1: Créer la requête**
- Méthode: `GET`
- URL: `http://localhost:8000/demande/track`

### **Étape 2: Ajouter les paramètres (onglet Params)**
| Key | Value |
|-----|-------|
| `idDemande` | `DEM12345678` |
| `cinDemandeur` | `AB123456` |

### **Étape 3: Envoyer**
- Cliquez sur **Send**
- Vérifiez le status code et la réponse JSON

### **Réponses possibles:**
- ✅ **200 OK** - Demande trouvée
- ❌ **404 NOT FOUND** - Demande introuvable
- ❌ **400 BAD REQUEST** - CIN incorrect

---

## 📥 **2. GET /demande/telecharger/{documentId}**

### **Étape 1: Créer la requête**
- Méthode: `GET`
- URL: `http://localhost:8000/demande/telecharger/{documentId}`
- Remplacez `{documentId}` par l'ID réel (ex: `doc-123-abc`)

### **Étape 2: Ajouter les paramètres (onglet Params)**
| Key | Value |
|-----|-------|
| `cin` | `AB123456` |
| `demandeId` | `DEM12345678` |

### **Étape 3: Envoyer et télécharger**
- Cliquez sur **Send**
- **⚠️ C'est normal de voir le fichier en binaire dans la réponse** - C'est le comportement attendu

### **Comment sauvegarder le fichier:**
1. Après avoir reçu la réponse (200 OK), cliquez sur le bouton **Save Response** (à droite)
2. Sélectionnez **Save to a file**
3. Choisissez un emplacement et un nom de fichier
4. Le fichier sera sauvegardé avec son format original (PDF, JPG, etc.)

### **Vérification:**
- Dans l'onglet **Headers** de la réponse, vérifiez:
  - `Content-Type: application/octet-stream`
  - `Content-Disposition: attachment; filename="nom-du-fichier.pdf"`

### **Réponses possibles:**
- ✅ **200 OK** - Fichier téléchargé (affiché en binaire dans Postman)
- ❌ **404 NOT FOUND** - Document introuvable
- ❌ **403 FORBIDDEN** - Accès refusé (CIN ou demandeId incorrect)

---

## 📋 **Workflow complet**

1. **Créer une demande** avec `POST /demande/envoyerDemande` pour obtenir:
   - `idDemande` (ex: `DEMabc1234`)
   - `documents[].id` (ex: `doc-id-1`)

2. **Tester Track:**
   ```
   GET http://localhost:8000/demande/track?idDemande=DEMabc1234&cinDemandeur=AA123456
   ```

3. **Tester Téléchargement:**
   ```
   GET http://localhost:8000/demande/telecharger/doc-id-1?cin=AA123456&demandeId=DEMabc1234
   ```
