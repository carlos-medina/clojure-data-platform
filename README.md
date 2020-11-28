# Plataforma

## Buildar as imagens

- Na pasta fiel/cassandra, executar o comando

  docker build -t c/cassandra .

## Cassandra

- Subir um contêiner do Cassandra

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

- Executar o comando cqlsh no contêiner do Cassandra

  docker exec -it cassandra-1 cqlsh

- Criar o keyspace do ingestor

  CREATE KEYSPACE ingestor
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};

- Criar a tabela do comandor_por_owner

  CREATE TABLE ingestor.comandos_por_owner (
    owner text,
    id int,
    version int,
    tags text,
    PRIMARY KEY ((owner), id)
  );
