# Payment Processing Platform

A learning project to explore Kafka, PostgreSQL, Redis, and event-driven architecture with producers and consumers using **Spring Boot 3.x** and **Java 17**.

## Architecture Overview

This project demonstrates:
- **Kafka**: Event streaming platform with 3 partitions
- **Producer**: Sends payment events with user-based partitioning
- **3 Consumers**: Each in a different consumer group
  - Payment Processor (PostgreSQL)
  - Notification Service (Redis)
  - Analytics Service (PostgreSQL)
- **PostgreSQL**: Stores payment data and analytics
- **Redis**: Stores notifications
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
| zookeeper | Kafka coordination service |
| kafka | Message broker with 3 partitions |
| postgres | Database for payments and analytics |
| redis | Cache for notifications |
| producer | Generates payment events every 2 seconds |
| consumer-processor | Processes payments and stores in PostgreSQL |
| consumer-notification | Sends notifications stored in Redis |
| consumer-analytics | Tracks transaction metrics |

## Getting Started

### Prerequisites
- Docker
- Docker Compose

### Run the Platform

```bash
docker-compose up --build
```

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
SELECT * FROM payments ORDER BY partition, offset;

-- View analytics
SELECT * FROM analytics;
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

## Stop the Platform

```bash
docker-compose down
```

To remove volumes (delete all data):
```bash
docker-compose down -v
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

