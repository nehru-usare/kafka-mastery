# Apache Kafka Mastery Guide 🚀
*From Genesis to Senior Architect - The Complete Manual*

Welcome to the definitive guide for mastering Apache Kafka. This repository is a masterclass designed to take you from a Java developer to a Senior Architect who can design massive-scale distributed systems.

---

## 📑 Table of Contents
1. [Chapter 1: The Genesis (History & Why)](#-chapter-1-the-genesis-history--the-why)
2. [Chapter 2: The Core Engine (Architecture)](#-chapter-2-the-core-engine-architecture)
3. [Chapter 3: The "Parcel Delivery" Analogy](#-chapter-3-the-parcel-delivery-analogy)
4. [Chapter 4: Explicit System Configurations](#-chapter-4-explicit-system-configurations)
5. [Chapter 5: Setting Up Your Environment](#-chapter-5-setting-up-your-environment)
6. [Chapter 6: The Hands-on Master Project](#-chapter-6-the-hands-on-master-project)
7. [Chapter 7: Demystifying "Confusion" (For Freshers)](#-chapter-7-demystifying-confusion-for-freshers)
8. [Chapter 8: The Architect's Circle (Advanced)](#-chapter-8-the-architects-circle-advanced)
9. [Chapter 9: Testing & Verification](#-chapter-9-testing--verification)
10. [Chapter 10: Postman & WebSocket Testing](#-chapter-10-postman--websocket-testing)

---

## 📜 Chapter 1: The Genesis (History & "The Why")

### 1. The LinkedIn Story
Around 2011, LinkedIn faced a "Spaghetti Architecture" problem. Traditional Message Queues (RabbitMQ) couldn't handle their scale. 
- **The Solution**: They created **Kafka**, named after author Franz Kafka. It's not just a queue; it's a **Distributed Commit Log**.

### 2. Why Kafka?
| Feature | Traditional MQ | Apache Kafka |
| :--- | :--- | :--- |
| **Logic** | Deleted after consumption. | Stored in a durable log. |
| **Scale** | Limited throughput. | Millions of msg/sec. |
| **Replay** | Once gone, it's gone. | Rewind and re-read data. |

---

## 🏗️ Chapter 2: The Core Engine (Architecture)

- **Broker**: A Kafka server.
- **Topic**: A category (folder).
- **Partition**: Topics are split into pieces for scaling.
- **KRaft Mode**: Modern Kafka (no Zookeeper). Uses Raft for internal consensus.

---

## 📦 Chapter 3: The "Parcel Delivery" Analogy

Think of Kafka as a **Courier Office**:
1. **Parcel**: The Data/Message.
2. **Delivery Hub**: The Topic.
3. **Sorting Line**: The Partition (multiple lines = faster sorting).
4. **Sender/Receiver**: Producer/Consumer.
5. **Truck Fleet**: Consumer Group (sharing the work).
6. **Receipt**: Offset (knowing where you left off).

---

## 🛠️ Chapter 4: Explicit System Configurations

| Category | Property | Value | Purpose |
| :--- | :--- | :--- | :--- |
| **Connection** | `spring.kafka.bootstrap-servers` | `localhost:9092` | Broker address |
| **Security** | `acks` | `all` | Zero data loss |
| **Reliability** | `enable.idempotence` | `true` | No duplicate writes |

---

## ⚙️ Chapter 5: Setting Up Your Environment

To run this project, you need three key tools installed on your machine. Follow these steps if you are starting from zero:

### 1. Java 21 (The Engine)
We use **Java 21** because it is the latest LTS (Long Term Support) version and is required for Spring Boot 3.4.
- **Check version**: Open terminal and type `java -version`.
- **Install**: If not found, download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or use [SDKMAN](https://sdkman.io/).

### 2. Docker & Docker Desktop (The Infrastructure)
Kafka is difficult to install directly on Windows or Mac. **Docker** allows us to run a "Container" (a mini virtual computer) that has Kafka perfectly pre-configured.
- **Install**: Download [Docker Desktop](https://www.docker.com/products/docker-desktop/).
- **Starting Kafka**: Once Docker is running, navigate to the project root and run:
  ```bash
  docker-compose up -d
  ```
  - `up`: Starts the containers.
  - `-d`: "Detached" mode (runs in the background).
- **Verify**: Check Docker Desktop dashboard; you should see a container named `kafka-broker` running.

### 3. Maven (The Build Tool)
Maven downloads all the libraries (Spring, Kafka Client, etc.) and compiles your Java code.
- **Check version**: Run `mvn -version`.
- **Install**: Most IDEs (IntelliJ, VS Code) come with Maven, but you can download it from [Apache Maven](https://maven.apache.org/download.cgi).
- **Build Command**: To build and install the entire project, run this from the **root directory**:
  ```bash
  mvn clean install -DskipTests
  ```

---

## 💻 Chapter 6: The Hands-on Master Project

1. **Order Service (Producer)**: High-performance, transactional.
2. **Broker (Kafka)**: The durable heart.
3. **Payment & Notification (Consumers)**: Independent groups.
4. **Real-time UI (WebSocket)**: Premium dashboard at `localhost:8083`.

---

## ❓ Chapter 7: Demystifying "Confusion" (For Freshers)

- **Topic** = A Book.
- **Partition** = A Chapter.
- **Offset** = A Page Number.
- **GroupId** = A Bookmark.
- **Rebalance** = Musical Chairs (re-assigning chapters).

---

## 🧐 Chapter 8: The Architect's Circle (Advanced)

- **EOS (Exactly-Once Semantics)**: Achieved via Idempotence + Transactions.
- **Failure Handling**: Retries with Exponential Backoff + DLT (Dead Letter Topic).
- **Log Compaction**: Keeps only the latest state for a key.

---

## 🧪 Chapter 9: Testing & Verification

1. **Start Cluster**: `docker-compose up -d`
2. **Build Root**: `mvn clean install -DskipTests` (Must run from root!)
3. **Launch Services**: Run the JARs or Use IDE.
4. **Trigger Event**:
```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8081/orders" -ContentType "application/json" -Body '{"customerId": "user1", "amount": 149.99}'
```

---

## 📡 Chapter 10: Postman & WebSocket Testing

### A. Real-time Dashboard
Open your browser to: **[http://localhost:8083/index.html](http://localhost:8083/index.html)**. Watch alerts pop up instantly!

### B. Postman WebSocket Setup
1. **URL**: `ws://localhost:8083/ws-notification/websocket`
2. **Connect Frame**:
```text
CONNECT
accept-version:1.2,1.1,1.0
heart-beat:10000,10000

\0
```
3. **Subscribe Frame**:
```text
SUBSCRIBE
id:sub-0
destination:/topic/notifications

\0
```

---

---

**Author**: Nehru Usare
**Summary**: Use this project to master the backbone of modern event-driven architecture. Happy Coding! 🌟
