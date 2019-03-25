# Dr Schedular


Dr Schedular est une application (REST) sécurité pour de prise de rendez-vous pour clinique médical, composé d'une partie public (Api public) et partie privé dédié au medecins.

  - Permet au client de consulter la disponibilité d'un medecin
  - Faire des réservation
  - Le medecin peut consulter l'ensemble des réservation faite (daily appointments)
  - Le medecin peut supprimer la demande de rendez-vous (Un email d'information sera envoyer au client)
  - Le medecin peut activier/disactiver un rendez-vous (Un email d'information sera envoyer au client)
  - Le medecin dans l'espace privé, a la possibilité d'accéder a la page (Register) pour créer d'autre compte de type "medecin" a ces collègue.
  - Le medecin/patient on accès a une rubrique Feedback, qui sert a envoyer un feedback a l'administrateur.

# Tech

  - Spring Boot
  - Spring Security
  - System log Sl4j
  - java Mail
  - JWT
  - les Mappers
  - Angular 7
  - Twitter Bootstrap 4
  - NPM

# Prérequis 

  - Dans le fichier application.properties, modifier : spring.datasource.username & spring.datasource.password=admin avec vos accès de bdd et assurez vous que la BDD est lancé.
  - Inscrivez vous sur mailtrap.io et modifier les parametres la section SMTP pour recevoir des notification par mail (Feedback, changement de status)

# Envirement de developpement 

  - Apache Maven 3.6.0.
  - npm 6.4.1
  - node v10.15.0
  - Spring boot 2.1.0
  - Java 11
  - MySQL DB

### Lancer l'application

Exécutez à partir du répertoire racine du projet. Cela générera un fichier war dans le répertoire api-server/target. Il peut être déployé sur le serveur Tomcat et l'application peut être visualisée.
```sh
$ mvn clean install 
```
Pour exécuter l'application Spring Boot à l'aide de Maven, exécutez la commande suivante à partir du répertoire api-server.
```sh
$ mvn spring-boot:run
```
Une fois l'application lancée, nous devrions pouvoir voir la page d'accueil avec http://localhost:8080/

License
----

MIT


**Merci !**

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

   [screenshot1]: <https://github.com/Alilat-imad/dr-schedular/screenshots/screenshot1.png>
   [screenshot2]: <https://github.com/Alilat-imad/dr-schedular/screenshots/screenshot2.png>
   [screenshot3]: <https://github.com/Alilat-imad/dr-schedular/screenshots/screenshot3.png>
   [screenshot4]: <https://github.com/Alilat-imad/dr-schedular/screenshots/screenshot4.png>
   [screenshot5]: <https://github.com/Alilat-imad/dr-schedular/screenshots/screenshot5.png>
   [screenshot6]: <https://github.com/Alilat-imad/dr-schedular/screenshots/screenshot6.png>
   