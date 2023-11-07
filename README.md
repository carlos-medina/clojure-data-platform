# Data Plataform

## Build the images

- Execute the following commands

```
  docker build -t c/cassandra ./fiel/cassandra
  docker build -t c/ingestor ./ingestor
  docker build -t c/api ./api
```

## Cassandra

- Run a Cassandra container

```
  docker run --rm -d
    --name cassandra-1
    -e CASSANDRA_BROADCAST_ADDRESS=host.docker.internal
    -e CASSANDRA_CLUSTER_NAME='ingestor'
    -p 7000:7000
    -p 9042:9042
    -v cdata-ingestor:/var/lib/cassandra
    -v clogs-ingestor:/var/log/cassandra
    -v %cd%:/work
    c/cassandra
```

- Execute cqlsh in Cassandra's container

```
  docker exec -it cassandra-1 cqlsh
```

- Create Ingestor's keyspace

```
  CREATE KEYSPACE ingestor
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};
```

- Create table **comando_por_owner**

```
  CREATE TABLE ingestor.comandos_por_owner (
    tags text,
    version int,
    owner text,
    id int,
    PRIMARY KEY ((owner), id)
  );
```

- Create test table that saves all commands

```
  CREATE TABLE ingestor.comandos_por_owner_teste (
    particao int,
    offset int,
    tags text,
    version int,
    owner text,
    id int,
    PRIMARY KEY ((owner), id, version)
  );
```

- Create test table that saves all owners

```
  CREATE TABLE ingestor.owners (
    particao int,
    owner text,
    PRIMARY KEY ((particao), owner)
  );
```

## Ingestor

- Run Ingestor container

```
  docker run --rm -it -v %cd%/ingestor:/work -w /work --name ingestor c/ingestor bash
```

- Command that creates a kafka consumer on test mode

```
  lein run teste
```

- Command that creates a consumer in production mode

```
  lein run prd
```

## API

- Run API container

```
  docker run --rm -it -v %cd%/api:/work -w /work --name api -p 3000:3000 c/api lein repl
```

- Initialize API server on 3000

```
  (def s (start))
```

- Command to stop API server

```
  (stop s)
```

- The API Request must have the following format in its body:

```
  {
    "Owner": "owner1", <- Mandatory
    "Filters": {       <- Optional
      "Tags": [
        "tag1"
      ]
    }
  }
```