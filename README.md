# TP — Passerelle API REST / Service SOAP (CRUD Personne)


Le tutoriel complet, avec les commandes PowerShell.
Projet Java Spring Boot prêt à importer dans IntelliJ IDEA et à démarrer avec **une commande Docker**.
Deux applications indépendantes : **SOAP 8081** (base H2 + cinq opérations) et **REST 8080** (JSON + cinq routes, sans base locale).

## 1. Le principe, très simplement

```text
Postman / navigateur (JSON)
               |
               v
   REST : http://localhost:8080
      GET/POST/PUT/DELETE
               |
          client SOAP
        WebServiceTemplate
               |
          enveloppe XML
               v
   SOAP : http://localhost:8081/ws
          5 opérations
               |
        Spring Data JPA
               v
         Base H2 locale
```

> REST ne contient **ni Entity, ni Repository, ni base de données**. Il transmet les opérations à SOAP.

## 2. Démarrer sur Windows avec Docker Desktop (méthode conseillée)

1. Extraire le ZIP dans un dossier, par exemple `C:\TP\TP_SPRING_SOAP_REST`.
2. Lancer **Docker Desktop** et attendre que le moteur soit opérationnel.
3. Ouvrir PowerShell **dans le dossier contenant `compose.yaml`**.
4. Exécuter :

```powershell
docker compose up --build -d
```

Au premier lancement, Docker télécharge les images Maven/Java et Maven récupère les dépendances ; une connexion Internet est donc nécessaire. Les deux services sont ensuite démarrés. Le service REST peut être démarré quelques secondes avant que SOAP ne soit prêt : attendre que le WSDL s'ouvre.

**Contrôler :**

```powershell
docker compose ps
docker compose logs -f soap-service
# Dans un autre terminal :
docker compose logs -f rest-client
```

- **WSDL du fournisseur** : http://localhost:8081/ws/personnes.wsdl
- **Liste REST** : http://localhost:8080/api/personnes
- **Console H2** (développement uniquement) : http://localhost:8081/h2-console

Pour la console H2 : JDBC URL `jdbc:h2:file:./data/personnes`, utilisateur `sa`, mot de passe vide. La base est conservée sur le volume Docker `soap-donnees`.

**Arrêter (en gardant les données)** : `docker compose down`

**Effacer aussi la base** : `docker compose down -v` (destructif).

## 3. Tester dans Postman — les 5 requêtes

**URL de base :** `http://localhost:8080/api/personnes`

| Opération | Méthode | Adresse | Corps (JSON) | Succès |
|---|---|---|---|---|
| Ajouter | POST | `/api/personnes` | `{"nom":"Diop","prenom":"Aminata","age":25}` | 201 |
| Récupérer | GET | `/api/personnes/1` | aucun | 200 |
| Lister | GET | `/api/personnes` | aucun | 200 |
| Modifier | PUT | `/api/personnes/1` | `{"nom":"Diop","prenom":"Awa","age":26}` | 200 |
| Supprimer | DELETE | `/api/personnes/1` | aucun | 204 |

Pour POST et PUT, choisir **Body > raw > JSON**, ce qui place `Content-Type: application/json`.
L'`id` est généré dans H2 : **ne pas le mettre dans le POST**. Reprendre l'ID réellement retourné, qui n'est pas toujours `1`.
Pour un identifiant inexistant, GET / PUT / DELETE renvoient **404**. Une entrée invalide renvoie **400**. Si SOAP est inaccessible, REST signale un problème de passerelle (**503** ou **502** selon le cas).

### Collection Postman prête à l'emploi

Importer `postman/TP_Personnes_SOAP_REST.postman_collection.json` dans Postman, puis exécuter les requêtes dans l'ordre 01 à 08. La première crée une personne et enregistre automatiquement `personneId` dans les variables de collection. Le test 08 interroge le service SOAP directement en XML.

### Script PowerShell automatique

Dans le dossier racine du projet :

```powershell
powershell -ExecutionPolicy Bypass -File .\tests\test-crud.ps1
```

Il teste création → lecture → liste → modification → suppression → absence (404).

## 4. Comprendre le fonctionnement de SOAP

Le contrat principal est dans `soap-service/src/main/resources/personnes.xsd`. Le fournisseur expose son WSDL en direct sur :

`http://localhost:8081/ws/personnes.wsdl`

Les opérations : `addPersonne`, `getPersonne`, `getAllPersonnes`, `updatePersonne`, `deletePersonne`.

Une requête SOAP passe par une enveloppe XML. Pour l'envoyer avec Postman :
- Méthode **POST** et URL `http://localhost:8081/ws`.
- En-tête `Content-Type: text/xml; charset=UTF-8`.
- Corps : **Body > raw > XML**, prendre un fichier du dossier `exemples-soap/`.

Par exemple pour lister les personnes :

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:per="http://tp.sn/personnes">
  <soapenv:Header/>
  <soapenv:Body>
    <per:getAllPersonnesRequest/>
  </soapenv:Body>
</soapenv:Envelope>
```

Le XSD prévoit `minOccurs=0` sur la personne des réponses de lecture/modification : une réponse sans `personne` représente un identifiant absent. `deletePersonneResponse` utilise un booléen `deleted`. Le client REST transforme ces réponses en statut **404** si nécessaire.

## 5. Génération des classes Java

**SOAP fournisseur** : le plugin `jaxb2-maven-plugin` génère dans `soap-service/target/generated-sources/jaxb` les objets `AddPersonneRequest`, `GetPersonneResponse`, `PersonneType`… à partir de `personnes.xsd`.

**Client REST** : `cxf-codegen-plugin` lit la copie autonome du contrat `rest-client/src/main/resources/wsdl/personnes.wsdl` pendant le build et génère dans `rest-client/target/generated-sources/cxf` les classes JAXB ainsi que les interfaces JAX-WS. La passerelle les utilise via **Spring WebServiceTemplate**, comme demandé dans le sujet.

Le WSDL embarqué dans le client décrit les mêmes cinq opérations, messages, types et espace de noms que celui généré dynamiquement par le service. **Les classes générées ne sont pas écrites à la main** ; lancer `mvn clean package` pour les obtenir. Il n'est pas nécessaire que SOAP fonctionne pendant cette génération, puisque le WSDL est inclus dans le projet.

Pour changer le contrat ultérieurement, faire correspondre `personnes.xsd` et le WSDL client, puis relancer une compilation propre. Dans ce TP, les deux sont déjà cohérents.

## 6. Démarrer sans Docker (Java 21 + Maven 3.9+)

Ouvrir le dossier racine dans **IntelliJ IDEA Community** en important le `pom.xml` parent (Maven doit être reconnu ; Java SDK 21).
Dans le terminal à la racine :

```powershell
mvn clean package
```

Terminal 1 :

```powershell
java -jar soap-service/target/soap-service-1.0.0.jar
```

Terminal 2 (après démarrage de SOAP) :

```powershell
java -jar rest-client/target/rest-client-1.0.0.jar
```

Pour forcer l'adresse SOAP sous Windows PowerShell :

```powershell
$env:SOAP_SERVICE_URL = "http://localhost:8081/ws"
```

(Dans Docker Compose, l'adresse est `http://soap-service:8081/ws`, car les conteneurs communiquent par leurs noms de service ; `localhost` depuis REST désignerait REST lui-même.)

## 7. Arborescence — le rôle des fichiers

```text
TP_SPRING_SOAP_REST/
├── pom.xml                         Maven parent / modules
├── compose.yaml                    Lance les 2 services ensemble
├── soap-service/
│   ├── pom.xml                     SOAP, JPA, H2, JAXB
│   ├── Dockerfile
│   └── src/main/
│       ├── resources/
│       │   ├── personnes.xsd        CONTRAT SOAP
│       │   └── application.yml      Port 8081 + H2
│       └── java/sn/tp/soapservice/
│           ├── SoapServiceApplication.java
│           ├── config/WebServiceConfig.java
│           ├── entity/PersonneEntity.java
│           ├── repository/PersonneRepository.java
│           ├── service/PersonneService.java
│           └── endpoint/PersonneEndpoint.java
├── rest-client/
│   ├── pom.xml                     REST + client CXF/JAXB
│   ├── Dockerfile
│   └── src/main/
│       ├── resources/
│       │   ├── wsdl/personnes.wsdl  Contrat client pour génération
│       │   └── application.yml      Port 8080 + URL SOAP
│       └── java/sn/tp/restclient/
│           ├── RestClientApplication.java
│           ├── config/SoapClientConfig.java
│           ├── soap/PersonneSoapClient.java
│           ├── controller/PersonneController.java
│           ├── controller/GlobalExceptionHandler.java
│           └── dto/ (PersonneInput, PersonneDto)
├── postman/ (collection de 8 requêtes)
├── exemples-soap/ (5 enveloppes XML)
└── tests/ (test-crud.ps1)
```

## 8. Dépannage

| Problème | Vérification |
|---|---|
| Port 8080/8081 occupé | Fermer l'ancien serveur ou le conteneur qui utilise le port. |
| REST signale SOAP indisponible | Vérifier `docker compose ps`, puis `docker compose logs soap-service`. |
| Les imports `generated.*` sont en rouge dans IntelliJ | Lancer `mvn clean package`, puis recharger les projets Maven. |
| `mvn` inconnu | Utiliser Docker Desktop, ou installer Maven/JDK et configurer le PATH. |
| H2 console n'ouvre pas | Vérifier l'URL du port **8081**, pas 8080. |
| Anciennes données après redémarrage | Normal : la base H2 est persistante. `docker compose down -v` pour la réinitialiser. |
| Première compilation longue | Maven doit télécharger ses dépendances une seule fois (connexion Internet requise). |
