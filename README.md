🚀 JobTracker

JobTracker è un'applicazione backend sviluppata con Spring Boot per la gestione delle candidature lavorative.

Il progetto simula un sistema reale di tracking delle job application, includendo gestione utenti, tag e candidature, 
con architettura enterprise e best practice backend.

🧰 Tecnologie utilizzate

- Java 25
- Spring Boot
- Spring Web
- Spring Data JPA (Hibernate ORM)
- MySQL
- Maven
- Lombok
- SLF4J (Logback)

🏛️ Architettura del progetto

Il progetto segue un’architettura a livelli (layered architecture) ispirata a contesti enterprise:

- Controller Layer → esposizione API REST
- Service Layer → logica di business
- Repository Layer → accesso ai dati (JPA)
- DTO Layer → separazione tra entità e API
- Mapper Layer → conversione Entity ↔ DTO
- Exception Layer → gestione centralizzata degli errori
- Config Layer → configurazioni applicative

⚙️ Principi di design applicati

- Separazione delle responsabilità (SoC)
- DTO pattern per isolamento delle entity
- Controller “thin”, service “fat”
- Mapping centralizzato (no logica nei controller)
- Gestione centralizzata delle eccezioni
- Validazione input con Bean Validation
- REST API design coerente

🔥 Funzionalità principali

👤 User Management
- Creazione utente
- Recupero utente per ID
- Lista utenti
- Eliminazione utente

🏷️ Tag Management
- Creazione tag
- Lista tag
- Recupero tag per ID

📄 Job Application Management
- Creazione candidatura
- Recupero candidatura per ID
- Lista candidature
- Ricerca candidature per utente
- Aggiornamento parziale (PATCH)
- Eliminazione candidatura

📊 Logging Strategy

Logging strutturato tramite SLF4J + Logback:

- DEBUG → flusso interno applicativo (service layer)
- INFO → operazioni completate con successo
- WARN → eventi di business non critici (es. tentativi duplicati)
- ERROR → errori inattesi o eccezioni gestite

Obiettivo: rendere il sistema osservabile e debug-friendly in ottica produzione.

⚠️ Gestione delle eccezioni

La gestione degli errori è centralizzata tramite @RestControllerAdvice.

Errori gestiti
- 404 Not Found
 -> User non trovato
 / Tag non trovato
 / Job Application non trovata
- 409 Conflict
 -> Email duplicata
 / Tag duplicato
 / Candidatura duplicata (stesso user/company/position)
- 400 Bad Request
 -> Validazione input (Bean Validation)
 / JSON malformato o request non leggibile
- 500 Internal Server Error
 -> Errori inattesi del server

📦 Struttura del progetto

```text
com.manolo.jobtracker
├── controller
├── service
│   └── impl
├── repository
├── model
├── dto
│   ├── request
│   └── response
├── mapper
├── exception
├── enums
└── config
```

📈 Obiettivi del progetto

- Simulare un backend reale enterprise-level
- Applicare best practice di Spring Boot
- Implementare logging e osservabilità
- Gestire errori in modo centralizzato e scalabile
- Costruire una base solida per progetti full-stack futuri

🧪 Avvio del progetto

- Prerequisiti
 -> Java 21+
 / MySQL
 / Maven
- Avvio
 -> mvn clean install
 / mvn spring-boot:run

Oppure eseguire direttamente da IntelliJ tramite:
JobTrackerApplication.java

📌 Note tecniche

- Le entità non vengono mai esposte direttamente nelle API
- Tutte le risposte sono basate su DTO
- Le validazioni sono gestite con Bean Validation
- Le eccezioni sono centralizzate e uniformi
- Architettura pronta per evoluzione a microservizi (separazione logica già presente)

👨‍💻 Autore

Manolo Sainas

GitHub: https://github.com/ManoloSainas / LinkedIn: https://www.linkedin.com/in/manolosainas/