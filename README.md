# Payment Processing Platform

A learning project to explore Kafka, PostgreSQL, Redis, and event-driven architecture with producers and consumers using **Spring Boot 3.x** and **Java 17**.

## Architecture Overview

This project demonstrates:
- **Kafka**: Event streaming platform with 3 partitions (using KRaft mode, no Zookeeper!)
- **Producer**: Sends payment events with user-based partitioning via REST API
- **3 Consumers**: Each in a different consumer group
  - Payment Processor (PostgreSQL)
  - Notification Service (Redis)
  - Analytics Service (PostgreSQL - overall & user-level stats)
- **PostgreSQL**: Stores payment data and analytics
- **Redis**: Stores notifications
- **React UI**: Beautiful interface for sending payments & viewing data
- **Docker Compose**: Orchestrates all services

## Key Concepts Demonstrated

### Partitioning
- Payments are partitioned by `userId` using a hash function
- Same user's payments always go to the same partition
- This ensures ordering per user

### Sequencing
- Each partition maintains message order
- Offset tracking ensures messages are processed in order
- Each consumer group processes messages independently

### Consumer Groups
- Each consumer is in its own group
- All consumers receive all messages (fan-out pattern)
- Each consumer has its own offset management

## Services

| Service | Description |
|---------|-------------|
| kafka | Message broker with 3 partitions (KRaft mode) |
| postgres | Database for payments and analytics |
| redis | Cache for notifications |
| producer | Spring Boot app with REST API to send payment events |
| consumer-processor | Processes payments and stores in PostgreSQL |
| consumer-notification | Sends notifications stored in Redis |
| consumer-analytics | Tracks transaction metrics (overall & user-level) |
| ui | React UI for interacting with the platform |

## Getting Started

### Prerequisites
- Docker
- Docker Compose

### Run the Platform

#### Option 1: Full Docker Setup (Everything in Docker)
```bash
docker compose up --build
```

Then access the UI at `http://localhost:3000`

#### Option 2: UI Locally, Backend in Docker
First, start backend services:
```bash
docker compose up --build --no-deps zookeeper kafka postgres redis producer consumer-processor consumer-notification consumer-analytics
```

Then run the UI locally:
```bash
cd ui
npm install
npm start
```

The UI will be available at `http://localhost:3000`

### View Logs

```bash
# Producer logs
docker logs -f payment-producer

# Processor consumer logs
docker logs -f payment-consumer-processor

# Notification consumer logs
docker logs -f payment-consumer-notification

# Analytics consumer logs
docker logs -f payment-consumer-analytics
```

### Connect to PostgreSQL

```bash
docker exec -it postgres psql -U payment -d payment_db
```

Useful queries:
```sql
-- View all payments
SELECT * FROM payments ORDER BY kafka_partition, kafka_offset;

-- View analytics
SELECT * FROM analytics;

-- View user-level analytics
SELECT * FROM user_analytics;
```

### Connect to Redis

```bash
docker exec -it redis redis-cli
```

Useful commands:
```bash
# List all notification keys
KEYS notification:*

# Get user notifications
LRANGE user:notifications:user-101 0 -1
```

### Test APIs Directly

```bash
# Send a payment event
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user-101",
    "amount": 123.45,
    "currency": "USD"
  }'

# Get all payments
curl http://localhost:8081/api/payments

# Get overall analytics
curl http://localhost:8083/api/analytics

# Get user-level analytics
curl http://localhost:8083/api/analytics/users
```

## Stop the Platform

```bash
docker compose down
```

To remove volumes (delete all data):
```bash
docker compose down -v
```

## How Partitioning Works

The producer uses a custom Spring Kafka partitioner based on `userId`:
```java
public class UserIdPartitioner implements Partitioner {
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        
        String userId = (String) key;
        int hash = userId.hashCode();
        return Math.abs(hash) % numPartitions;
    }
}
```

This ensures:
- All payments from `user-101` go to partition X
- All payments from `user-102` go to partition Y
- Order is preserved within each user's payment stream

## How Sequencing Works

- Each partition maintains strict ordering
- Offset is incremented for each message in the partition
- Consumers track their own offsets per partition
- Messages are processed in offset order within each partition
