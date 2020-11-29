(ns ingestor.core
  (:require [ingestor.db :as db])
  (:import [com.datastax.driver.core Cluster ConsistencyLevel HostDistance PoolingOptions QueryOptions]
           [com.datastax.driver.core.policies LatencyAwarePolicy Policies]
           [org.apache.kafka.clients.consumer KafkaConsumer]))

(defn configs-cassandra [builder quorum?]
  (-> builder
      (.addContactPoint "host.docker.internal")
      (.withCredentials "cassandra" "cassandra")
      (.withLoadBalancingPolicy (.build (LatencyAwarePolicy/builder (Policies/defaultLoadBalancingPolicy))))
      (.withPoolingOptions (-> (PoolingOptions.)
                               (.setMaxQueueSize 1024)
                               (.setMaxRequestsPerConnection HostDistance/LOCAL 2048)))
      (.withQueryOptions (-> (QueryOptions.)
                             (.setFetchSize 2000)
                             (.setConsistencyLevel (if quorum?
                                                     ConsistencyLevel/QUORUM
                                                     ConsistencyLevel/ONE))
                             (.setDefaultIdempotence true)))))

(defn new-consumer [group-id]
  (KafkaConsumer.
   {"bootstrap.servers"                     "kafka-teste.dev-arquivei.com.br:29092"
    "group.id"                              group-id
    "enable.auto.commit"                    "false"
    "key.deserializer"                      "org.apache.kafka.common.serialization.StringDeserializer"
    "value.deserializer"                    "org.apache.kafka.common.serialization.StringDeserializer"
    "auto.offset.reset"                     "earliest"}))

(defn -main [& args]
  (let [interface (first args)
        cluster (-> (Cluster/builder)
                    (configs-cassandra false)
                    (.build))
        session (.connect cluster "ingestor")]
    (case interface
      "consumer" (let [grupo-de-consumo "1" ;(str (rand))
                       consumer (new-consumer grupo-de-consumo)]
                   (println "Começando a leitura das mensagens")
                   (.subscribe consumer ["documents"])
                   (while true ; doseq [i (range 1 5)]
                     (let [records (.poll consumer (java.time.Duration/ofMillis 5000))]
                       (doseq [record records]
                         (println
                          (format "Fazendo insert no BD do comando:\noffset = %s\nkey = %s\nvalue = %s\npartition = %s\n"
                                  (.offset record)
                                  (.key record)
                                  (.value record)
                                  (.partition record)))
                         (db/upsert-cmd (.value record) session (.offset record)))))))))