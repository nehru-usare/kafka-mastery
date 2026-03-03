# Kafka Topic Creation and Testing Guide (Docker Setup)

## Prerequisites

-   Kafka is running inside Docker
-   Container name: `kafka`
-   Port `9092` exposed
-   Image: `apache/kafka:latest`

------------------------------------------------------------------------

## Step 1: Enter Kafka Container

Open **Terminal 1** and run:

``` bash
docker exec -it kafka bash
```

------------------------------------------------------------------------

## Step 2: Create a Topic

Run inside the container:

``` bash
/opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic my-test-topic --partitions 1 --replication-factor 1
```

### Verify the Topic

``` bash
/opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
```

------------------------------------------------------------------------

## Step 3: Start Consumer (Terminal 2)

Open a **NEW terminal** and run:

``` bash
docker exec -it kafka bash
```

Start the consumer:

``` bash
/opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic my-test-topic --from-beginning
```

Leave this terminal running. It is now subscribed to the topic.

------------------------------------------------------------------------

## Step 4: Start Producer (Terminal 1)

In Terminal 1, run:

``` bash
/opt/kafka/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic my-test-topic
```

Type messages and press **Enter** after each message.

Messages will immediately appear in Terminal 2.

------------------------------------------------------------------------

## Testing Flow Summary

1.  Create topic\
2.  Start consumer (subscriber)\
3.  Start producer\
4.  Send messages\
5.  Messages appear in consumer

------------------------------------------------------------------------

## Troubleshooting

### UNKNOWN_TOPIC_OR_PARTITION

-   Make sure topic is created first.
-   Verify using the `--list` command.
-   Ensure Kafka container is running:

``` bash
docker ps
```

------------------------------------------------------------------------

## Useful Commands

### Describe Topic

``` bash
/opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic my-test-topic
```

### List Consumer Groups

``` bash
/opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
```
