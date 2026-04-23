# auth-spring-boot

Backend d'authentification Spring Boot (Java 25, Maven, H2, JWT) avec Docker Compose et pipeline CI/CD GitHub Actions.

## Prerequis

- Java 25
- Maven 3.9+
- Docker + Docker Compose

## Demarrage local (sans Docker)

```bash
mvn clean test
mvn spring-boot:run
```

Application disponible sur http://localhost:8081

## Demarrage avec Docker Compose

1. Copier le fichier d'exemple:

```bash
cp .env.example .env
```

2. Lancer:

```bash
docker compose up --build
```

3. Arreter:

```bash
docker compose down
```

## Variables d'environnement

- `APP_PORT`: port publie localement (defaut `8081`)
- `MASTER_KEY`: cle maitre applicative
- `JWT_SECRET`: secret JWT

## CI/CD

Le workflow est defini dans `.github/workflows/ci.yml` et execute:

1. Build + tests (`mvn -B clean verify`) sur Java 25
2. Analyse SonarCloud conditionnelle si `SONAR_TOKEN` est configure

### Configuration GitHub requise

- Secret: `SONAR_TOKEN`
- Variable repository (optionnelle mais recommandee): `SONAR_ORGANIZATION`

Si `SONAR_TOKEN` est absent, le build et les tests continuent a passer; seule l'analyse Sonar est sautee.

## Fichiers essentiels

- `.git/`
- `Dockerfile`
- `docker-compose.yml`
- `.env.example`
- `.github/workflows/ci.yml`
- `sonar-project.properties`
- `README.md`
