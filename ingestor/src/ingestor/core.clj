(ns ingestor.core
  (:require [ingestor.log :as log]
            [ingestor.model :as model])
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
   {"bootstrap.servers" "kafka-teste.dev-arquivei.com.br:29092"
    "group.id" group-id
    "enable.auto.commit" "true"
    "auto.commit.interval.ms" "1000"
    "key.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"
    "value.deserializer" "org.apache.kafka.common.serialization.StringDeserializer"
    "auto.offset.reset" "earliest"}))

(defn -main [& args]
  (let [modo (first args)
        cluster (-> (Cluster/builder)
                    (configs-cassandra false)
                    (.build))
        session (.connect cluster "ingestor")
        grupo-de-consumo "2020-12-08.01"
        consumer (new-consumer grupo-de-consumo)
        _ (.subscribe consumer ["documents"])]
    (case modo
      "teste"
      (do
       (log/start modo)
       (while true
         (let [records (.poll consumer (java.time.Duration/ofMillis 5000))]
           (doseq [record records]
             (log/insert record)
             (model/upsert-cmd (.value record) session)
             (model/upsert-cmd-teste (.value record) session (.offset record))
             (model/upsert-owner-teste (.value record) session)))))
              
      "prd"
      (do
       (log/start modo)
       (while true
         (let [records (.poll consumer (java.time.Duration/ofMillis 5000))]
           (doseq [record records]
             (log/insert record)
             (model/upsert-cmd (.value record) session))))))))