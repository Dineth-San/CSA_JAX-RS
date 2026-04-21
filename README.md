# CSA_JAX-RS
A scalable JAX-RS RESTful API designed to manage a university's Smart Campus IoT infrastructure. Features hierarchical resource routing, custom HTTP exception handling, and thread-safe in-memory data management.

# Smart Campus Sensor API

A RESTful API built with Java JAX-RS (Jersey) for managing rooms and environmental sensors across a smart campus. The API supports room management, sensor registration, sensor reading history, and real-time filtering — all served over a clean, self-documenting REST interface.

---

## API Design Overview

The API follows REST principles with a base path of `/api/v1`. Resources are modelled hierarchically:

```
/api/v1
│
├── /                        → Discovery endpoint (metadata, resource map)
├── /rooms                   → Room collection
│   └── /{roomId}            → Individual room operations
└── /sensors                 → Sensor collection (supports ?type= filtering)
    └── /{sensorId}/readings → Sub-resource: reading history per sensor
```

### Design decisions

- **JAX-RS (Jersey)** is used as the REST framework, deployed on GlassFish.
- **In-memory storage** is handled by a thread-safe `DataStore` singleton using `ConcurrentHashMap`.
- **Sub-resource locator pattern** is used for sensor readings — `SensorResource` delegates to a dedicated `SensorReadingResource` class rather than handling everything in one controller.
- **Custom exception mappers** handle all error scenarios, ensuring every response — including unexpected errors — returns a structured JSON body with no stack traces exposed.
- **HTTP status codes** are used semantically: `201` on creation, `204` on deletion, `400` for bad input, `403` for forbidden state, `404` for missing resources, `409` for conflicts, `422` for payload reference errors, `500` for unexpected server errors.

### Resources

| Resource | Endpoint | Methods |
|---|---|---|
| Discovery | `/api/v1/` | GET |
| Rooms | `/api/v1/rooms` | GET, POST |
| Room | `/api/v1/rooms/{roomId}` | GET, DELETE |
| Sensors | `/api/v1/sensors` | GET, POST |
| Readings | `/api/v1/sensors/{sensorId}/readings` | GET, POST |

---

## Prerequisites

Before building and running the project, ensure you have the following installed:

- **JDK 8 or later** — [Download](https://www.oracle.com/java/technologies/downloads/)
- **Apache NetBeans IDE** (recommended) — [Download](https://netbeans.apache.org/)
- **GlassFish Server 5.x** — [Download](https://glassfish.org/download)
- **Maven** (bundled with NetBeans, or install separately)

---

## Build & Launch Instructions

### Step 1 — Clone the repository

```bash
git clone https://github.com/Dineth-San/CSA_JAX-RS.git
cd CSA_JAX-RS
```

### Step 2 — Open the project in NetBeans

1. Open NetBeans
2. Go to **File → Open Project**
3. Navigate to the cloned folder and select it
4. Click **Open Project**

### Step 3 — Configure GlassFish

1. In the **Services** tab (Window → Services), right-click **Servers → Add Server**
2. Select **GlassFish Server**, click **Next**
3. Browse to your GlassFish installation directory and click **Finish**

### Step 4 — Build the project

1. Right-click the project in the **Projects** panel
2. Select **Clean and Build**
3. Confirm there are no build errors in the **Output** panel

### Step 5 — Run the project

1. Right-click the project → **Properties → Run**
2. Ensure the correct GlassFish server is selected and the context path is set to `/CSA_JAX_RS`
3. Right-click the project → **Run**
4. NetBeans will deploy the application to GlassFish and open a browser window

### Step 6 — Verify the server is running

Open your browser or a terminal and call the discovery endpoint:

```
http://localhost:8080/CSA_JAX_RS/api/v1/
```

You should receive a JSON response like:

```json
{
  "version": "v1.0",
  "contact": "dineth.s.kaluarachchi@gmail.com",
  "description": "Smart Campus Sensor API",
  "resources": {
    "rooms": "/api/v1/rooms",
    "sensors": "/api/v1/sensors"
  }
}
```

---

## Sample curl Commands

All commands below assume the server is running at `http://localhost:8080/CSA_JAX_RS`.

---

### 1. Create a new room

```bash
curl -X POST http://localhost:8080/CSA_JAX_RS/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"room1","name":"Lab A","capacity":30,"sensorIds":[]}'
```

**Expected response:** `201 Created` with the room object in the body and a `Location` header pointing to `/api/v1/rooms/room1`.

---

### 2. Register a sensor in a room

```bash
curl -X POST http://localhost:8080/CSA_JAX_RS/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"s1","type":"temperature","status":"active","currentValue":0.0,"roomId":"room1"}'
```

**Expected response:** `201 Created` with the sensor object in the body. The sensor is linked to `room1` automatically.

---

### 3. Filter sensors by type

```bash
curl -X GET "http://localhost:8080/CSA_JAX_RS/api/v1/sensors?type=temperature"
```

**Expected response:** `200 OK` with a JSON array containing only sensors whose type is `temperature`. Filtering is case-insensitive.

---

### 4. Post a sensor reading and verify currentValue updates

```bash
curl -X POST http://localhost:8080/CSA_JAX_RS/api/v1/sensors/s1/readings \
  -H "Content-Type: application/json" \
  -d '{"id":"r1","timestamp":1700000000,"value":23.7}'
```

**Expected response:** `201 Created`. Then verify the parent sensor's `currentValue` has updated:

```bash
curl -X GET http://localhost:8080/CSA_JAX_RS/api/v1/sensors/s1
```

The `currentValue` field should now be `23.7`.

---

### 5. Attempt to delete a room occupied by an active sensor (409 Conflict)

```bash
curl -X DELETE http://localhost:8080/CSA_JAX_RS/api/v1/rooms/room1
```

**Expected response:** `409 Conflict` with a structured JSON error body:

```json
{
  "error": "Conflict",
  "message": "Cannot delete room room1. It is occupied by sensors"
}
```

---

### 6. Attempt to post a reading to a sensor under maintenance (403 Forbidden)

First, create a sensor with status `maintenance`:

```bash
curl -X POST http://localhost:8080/CSA_JAX_RS/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"s2","type":"humidity","status":"maintenance","currentValue":0.0,"roomId":"room1"}'
```

Then try to post a reading to it:

```bash
curl -X POST http://localhost:8080/CSA_JAX_RS/api/v1/sensors/s2/readings \
  -H "Content-Type: application/json" \
  -d '{"id":"r2","timestamp":1700000100,"value":55.0}'
```

**Expected response:** `403 Forbidden`:

```json
{
  "error": "Forbidden",
  "message": "The selected sensor is under maintenance"
}
```

---

### 7. Trigger the global 500 error handler (no stack trace exposed)

Send malformed JSON to any POST endpoint:

```bash
curl -X POST http://localhost:8080/CSA_JAX_RS/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "broken'
```

**Expected response:** `500 Internal Server Error` with a clean JSON body — no stack trace, no internal paths:

```json
{
  "error": "Internal Server Error",
  "message": "An unexpected system error occurred."
}
```

---

## Project Structure

```
src/main/java/com/mycompany/csa_jax/rs/
├── JAXRSConfiguration.java          # @ApplicationPath("/api/v1") bootstrap
├── data/
│   └── DataStore.java               # Singleton in-memory store (ConcurrentHashMap)
├── models/
│   ├── Room.java
│   ├── Sensor.java
│   └── SensorReading.java
├── resource/
│   ├── DiscoveryResource.java       # GET /api/v1/
│   ├── RoomResource.java            # GET, POST /rooms | GET, DELETE /rooms/{id}
│   ├── SensorResource.java          # GET, POST /sensors | sub-resource locator
│   └── SensorReadingResource.java   # GET, POST /sensors/{id}/readings
├── exceptions/
│   ├── LinkedResourceNotFoundException.java
│   ├── LinkedResourceNotFoundExceptionMapper.java   # → 422
│   ├── RoomNotEmptyException.java
│   ├── RoomNotEmptyExceptionMapper.java             # → 409
│   ├── SensorUnavailableException.java
│   ├── SensorUnavailableExceptionMapper.java        # → 403
│   └── AllExceptionMapper.java                      # → 500 (catch-all Throwable)
└── filter/
    └── LoggingFilter.java           # Logs all incoming requests and outgoing responses
```

---

# CSA Coursework — Report Question Answers
 
---
 
## Part 1: Setup & Discovery
 
### 1.1 — JAX-RS Resource Lifecycle & Thread Safety
**Q: Analyse the JAX-RS lifecycle (request-scoped vs singleton) and provide strategies for synchronising in-memory data.**

By default, the JAX-RS framework utilizes a request-scoped lifecycle, instantiating a new resource controller object for every incoming HTTP request. This renders the controllers inherently thread-safe, as concurrent requests are handled by isolated objects that share no internal state.
However, this application implements a Singleton DataStore to simulate a centralized database. As a result, all request-scoped controllers concurrently access the exact same shared data structures. This introduces a significant risk of race conditions, such as lost data if multiple threads simultaneously invoke the addReading() method and overwrite each other's inputs.
To ensure data integrity, ConcurrentHashMap was utilized for all core collections. It employs bucket-level write-locking, allowing multiple threads to safely execute individual read and write operations simultaneously without locking the entire map. While this prevents basic concurrency crashes, composite read-then-write sequences (like those in addReading()) are not strictly atomic. A fully enterprise-grade system would utilize ConcurrentHashMap.compute() to guarantee absolute atomicity, but the current implementation provides robust and sufficient thread-safety for the coursework's anticipated scope.

### 1.2 — HATEOAS & Self-Documenting APIs
**Q: Justify HATEOAS, clearly articulating the benefits of self-documenting APIs over static documentation.**
 
HATEOAS (Hypermedia as the Engine of Application State) suggests that API responses include hypermedia links to guide client navigation. This application implements a form of HATEOAS via a discovery endpoint (GET /api/v1/), which returns a resource map of available services.
The primary architectural benefit is the decoupling of the client from the server’s internal URL routing. By forcing clients to discover endpoints at runtime, the backend can safely restructure or version its URLs without breaking existing integrations.
On the other hand, static documentation rapidly becomes useless as the API evolves, leading to integration failures. A self-documenting API eliminates this risk by acting as a real-time source of truth. For a smart campus system, HATEOAS ensures introducing new resource types or upgrading API versions—without requiring manual documentation updates across development teams.

---

## Part 2: Room Management
 
### 2.1 — POST Response: ID-Only vs Full Object
**Q: Analyse the trade-off between returning only the resource ID versus the full object on a POST response, considering bandwidth and payload overhead.**

When processing a POST request, returning only the newly generated resource ID minimizes the response payload, conserving bandwidth under high traffic loads. However, this approach increases latency by forcing the client to execute a secondary GET request to retrieve the complete object state.
On the other hand, returning the full object—the approach implemented in this application—increases the initial payload size but completely eliminates the need for a follow-up request. This strategy optimizes client-side performance and aligns with REST conventions. It allows the consumer to immediately verify the server's saved state without additional network round-trips, offering a more efficient and robust integration.

### 2.2 — DELETE Idempotency
**Q: Justify idempotency, explaining the exact server state across multiple identical DELETE calls.**

A method is idempotent if executing it multiple times produces the identical server state as a single execution. In this application, the first DELETE request for a valid room removes it from the DataStore and returns a 204 No Content. Any subsequent identical requests will return a 404 Not Found.
While the HTTP response codes differ (204 and 404), the final server state remains unchanged across all calls. This satisfies idempotency, which governs server state rather than response uniformity. Conversely, POST is inherently non-idempotent; repeated identical requests attempt to instantiate duplicate resources, a scenario this architecture actively rejects with a 400 Bad Request.

---

## Part 3: Sensors & Filtering
 
### 3.1 — Content-Type Mismatches & 415 Unsupported Media Type
**Q: Explain the technical consequences of content-type mismatches in JAX-RS — specifically what happens when a client sends the wrong Content-Type.**

Every POST endpoint in this API utilizes the `@Consumes(MediaType.APPLICATION_JSON)` annotation to restrict incoming requests strictly to JSON payloads. If a client submits an unsupported Content-Type (e.g., XML or plain text), the JAX-RS framework automatically intercepts the request and returns an `HTTP 415 Unsupported Media Type error`. This framework-level validation prevents malformed data from reaching the deserialization layer, and enforces strict API contracts.
Similarly, the `@Produces(MediaType.APPLICATION_JSON)` annotation governs output formatting. If a client's Accept header requests an unsupported data format, JAX-RS automatically yields an `HTTP 406 Not Acceptable response`. Together, these annotations create automated content negotiation layer that secures the API against format-based errors and ensures predictable client-server communication.

### 3.2 — Query Parameters vs Path Parameters for Filtering
**Q: Contrast QueryParams and PathParams, justifying why query strings are superior for collection filtering.**

Path parameters are utilized to identify specific, uniquely addressable resources, forming a URL (e.g., /sensors/123). Query parameters are optional modifiers used to filter or sort a broader collection without changing the base resource being addressed (e.g., /sensors?type=temperature).
Using a path parameter for filtering (e.g., /sensors/temperature) is incorrect within REST architecture. "Temperature" is a descriptive attribute, not an entity. Treating an attribute as a path parameter pollutes the URL namespace.
Query parameters correctly communicate to clients and network intermediaries that the request is producing a filtered view of a single collection. This aligns with REST semantics and allows the API to scale without requiring URL changes as new sensor types are introduced.

---

## Part 4: Sub-Resources
 
### 4.1 — Sub-Resource Locator Pattern & API Complexity Management
**Q: Discuss managing complexity and delegation in large APIs using the sub-resource locator pattern.**

As REST APIs scale, centralizing all endpoint logic within a single resource class violates the Single Responsibility Principle and creates unmanageable monolithic controllers. The sub-resource locator pattern resolves this by delegating URL subtrees to specialized classes. In this application, SensorResource hands off all reading-specific logic to a dedicated SensorReadingResource.
This architectural delegation enforces strict separation of concerns, ensuring that modifications to reading logic do not impact the parent sensor controller. It improves testability by isolating dependencies and guarantees seamless scalability; introducing new nested resources requires only a new class and a single locator method, leaving existing code untouched. Furthermore, this pattern maps the API's nested URL semantics directly to the codebase's structural hierarchy, reflecting enterprise-level design standards.

---

## Part 5: Error Handling
 
### 5.1 — Why 422 Unprocessable Entity is Superior to 404 for Payload Reference Errors
**Q: Analyse why 422 is superior to 404 for payload reference issues.**

When a client submits a POST request containing an invalid reference (a non-existent roomId), the requested endpoint (e.g., /api/v1/sensors) physically exists and successfully receives the payload. Returning an HTTP 404 Not Found in this scenario is semantically misleading, as it falsely indicates to the client that the target URL itself is incorrect or unavailable.
On the other hand, HTTP 422 Unprocessable Entity accurately communicates that the server located the endpoint and parsed the JSON payload, but could not fulfill the request due to semantic errors within the data itself. This precise distinction is critical for automated clients; a 404 instructs the system to correct its routing, whereas a 422 correctly instructs the system to rectify its payload data.
 
### 5.2 — Cybersecurity Risks of Stack Trace Exposure
**Q: Analyse the cybersecurity risks of exposing stack traces, detailing how they reveal internal paths, library versions, and logic flaws.**

By default, unhandled exceptions in JAX-RS return raw Java stack traces, presenting a critical information disclosure vulnerability across three primary attack surfaces. Stack traces reveal internal package structures, exact class names, and line numbers, allowing attackers to map the underlying architecture. They also expose specific library and framework versions, enabling malicious actors to cross-reference known CVEs (Common Vulnerabilities and Exposures) and deploy highly targeted exploits. The visible method call chain exposes the execution order of business logic, providing information to reverse-engineer validation flows and identify potential bypass opportunities.
To mitigate these risks, this application implements a global AllExceptionMapper (or equivalent "catch-all" exception mapper) that intercepts all unhandled runtime errors. It strictly sanitizes the output by returning a generic JSON 500 Internal Server Error to the external client, while securely recording the detailed, sensitive stack trace in internal server logs for safe developer review.

### 5.3 — JAX-RS Filters for Cross-Cutting Concerns vs Manual Logging
**Q: Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?**

Manually inserting logging statements into individual resource methods violates the Single Responsibility Principle. This approach generates excessive code duplication, rendering global format changes tedious.
On the other hand, using a JAX-RS filter (annotated with @Provider) intercepts all incoming requests and outgoing responses globally at the framework level. This pattern centralizes maintenance into a single class, guarantees logging consistency across all current and future endpoints, and ensures resource controllers remain strictly focused on their designated data processing tasks.

---

## Author

**Dineth Kaluarachchi**  
