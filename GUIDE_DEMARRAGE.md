# Guide de démarrage de l'application

Ce guide explique comment lancer l'application complète (backend + frontend).

## Prérequis

Avant de commencer, assurez-vous d'avoir installé :

1. **Java 17** ou supérieur
2. **Maven** (pour le backend)
3. **Node.js** (version 18 ou supérieure) et **npm**
4. **PostgreSQL** (base de données)
5. **PostgreSQL** doit être démarré avec une base de données nommée `app-dossier`

## Configuration de la base de données

1. Assurez-vous que PostgreSQL est installé et en cours d'exécution
2. Créez une base de données nommée `app-dossier` :
   ```sql
   CREATE DATABASE "app-dossier";
   ```
3. Vérifiez les paramètres de connexion dans `backend/fichier/src/main/resources/application.properties` :
   - Username: `postgres`
   - Password: `ilyass` (modifiez si nécessaire)
   - Database: `app-dossier`
   - Port: `5432`

## Étape 1 : Démarrer le Backend (Spring Boot)

1. Ouvrez un terminal et naviguez vers le dossier backend :
   ```bash
   cd backend/fichier
   ```

2. Compilez et lancez l'application Spring Boot :
   
   **Sur Windows :**
   ```bash
   .\mvnw.cmd spring-boot:run
   ```
   
   **Sur Linux/Mac :**
   ```bash
   ./mvnw spring-boot:run
   ```
   
   Ou si Maven est installé globalement :
   ```bash
   mvn spring-boot:run
   ```

3. Attendez que le backend démarre. Vous devriez voir :
   ```
   Started FichierApplication in X.XXX seconds
   ```

4. Le backend sera accessible sur : **http://localhost:8000**

## Étape 2 : Démarrer le Frontend (React + Vite)

1. Ouvrez un **nouveau terminal** (gardez le backend en cours d'exécution)

2. Naviguez vers le dossier frontend :
   ```bash
   cd frontend/front-end
   ```

3. Installez les dépendances (si ce n'est pas déjà fait) :
   ```bash
   npm install
   ```

4. Lancez le serveur de développement :
   ```bash
   npm run dev
   ```

5. Le frontend sera accessible sur : **http://localhost:5173** (ou un autre port si 5173 est occupé)

## Étape 3 : Accéder à l'application

1. Ouvrez votre navigateur et allez sur : **http://localhost:5173**

2. Vous verrez l'écran de sélection d'application :
   - Cliquez sur **"Admin"** pour accéder à l'interface administrateur

3. Connectez-vous avec vos identifiants administrateur :
   - L'endpoint de login est : `http://localhost:8000/auth/login`
   - Assurez-vous d'avoir un compte administrateur dans la base de données

## Commandes utiles

### Backend
- **Démarrer** : `mvn spring-boot:run` (dans `backend/fichier/`)
- **Arrêter** : `Ctrl + C` dans le terminal

### Frontend
- **Démarrer** : `npm run dev` (dans `frontend/front-end/`)
- **Arrêter** : `Ctrl + C` dans le terminal
- **Build de production** : `npm run build`
- **Prévisualiser le build** : `npm run preview`

## Dépannage

### Le backend ne démarre pas
- Vérifiez que PostgreSQL est en cours d'exécution
- Vérifiez les paramètres de connexion dans `application.properties`
- Vérifiez que le port 8000 n'est pas utilisé par une autre application

### Le frontend ne démarre pas
- Vérifiez que Node.js est installé : `node --version`
- Supprimez `node_modules` et réinstallez : 
  ```bash
  rm -rf node_modules
  npm install
  ```

### Erreur CORS
- Le backend doit être configuré pour accepter les requêtes depuis `http://localhost:5173`
- Vérifiez la configuration CORS dans le backend Spring Boot

### Erreur d'authentification
- Vérifiez que l'endpoint de login est correct dans `src/services/auth.ts`
- Vérifiez que le token JWT est correctement stocké dans le localStorage
- Vérifiez que vous avez un compte avec le rôle ADMIN dans la base de données

## Structure des ports

- **Backend** : `http://localhost:8000`
- **Frontend** : `http://localhost:5173` (ou autre port Vite)
- **PostgreSQL** : `localhost:5432`

## Notes importantes

1. **Les deux serveurs doivent être en cours d'exécution simultanément**
2. **Le backend doit démarrer avant le frontend** (pour éviter les erreurs de connexion)
3. **Assurez-vous que la base de données est accessible** avant de démarrer le backend

