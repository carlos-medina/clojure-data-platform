# Plataforma

## Buildar as imagens

- Na pasta fiel/cassandra, executar o comando

```
docker build -t c/cassandra .
```

## Cassandra

- Subir um contêiner do Cassandra

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

- Executar o comando cqlsh no contêiner do Cassandra

```
docker exec -it cassandra-1 cqlsh
```

- Criar o keyspace do ingestor

```
  CREATE KEYSPACE ingestor
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};
```

- Criar a tabela do comandor_por_owner

```
  CREATE TABLE ingestor.comandos_por_owner (
    offset int,
    tags text,
    version int,
    owner text,
    id int,
    PRIMARY KEY ((owner), id)
  );
```

- Tabela de testes que armazena todos os comandos

```
  CREATE TABLE ingestor.comandos_por_owner_teste (
    offset int,
    tags text,
    version int,
    owner text,
    id int,
    PRIMARY KEY ((owner), id, version)
  );
```

- Tabela de testes que armazena todos os owners

```
CREATE TABLE ingestor.owners (
  particao int,
  owner text,
  PRIMARY KEY ((particao), owner)
);
```

## Ingestor

- Na pasta plataforma/ingestor, rodar o comando

```
docker run --rm -it -v %cd%:/work -w /work --name ingestor clojure:lein-2.9.3 bash
```

- Comando para subir um consumidor em modo teste

```
lein run teste
```

- Comando para subir um consumidor em modo produção

```
lein run prd
```

## API

- Na pasta plataforma/api, rodar o comando

```
docker run --rm -it -v %cd%:/work -w /work --name api -p 3000:3000 clojure:lein-2.9.3 lein repl
```

- Comando para inicializar o servidor da API na porta 3000

```
(def s (start))
```

- Comando para encerrar o servidor da API

```
(stop s)
```

- A requisição para a API deve ter o formato abaixo informado no body:

```
{
	"Owner": "owner1", <- Obrigatório
	"Filters": {       <- Opcional
		"Tags": [
			"tag1"
		]
	}
}
```