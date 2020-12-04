(ns api.db
  (:require [api.util :as util])
  (:import [com.datastax.driver.core Cluster ConsistencyLevel HostDistance PoolingOptions QueryOptions]
           [com.datastax.driver.core.policies LatencyAwarePolicy Policies]))

(def session (atom nil))

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

(defn start []
  (swap! session
    (fn [session] (.connect
                   (-> (Cluster/builder)
                       (configs-cassandra false)
                       (.build)) "ingestor"))))

(defn qry-select-comandos-owner [session]
  (.prepare @session (str "SELECT *"
                          "  FROM comandos_por_owner"
                          " WHERE owner = :owner")))

(defn select-comandos-owner [session request]
  (let [stmt (-> session qry-select-comandos-owner .bind)]
    (.setString stmt "owner" ((request :params) "Owner"))
    ;; TODO: trocar o first para pegar todos os comandos
    (when-let [result (first (.execute @session stmt))]
      (.getInt result "version"))))


; owner = "00948406900504"
; id = 95971
; version = 6

(defn qry-select-version [session]
  (.prepare @session (str "SELECT version"
                         "  FROM comandos_por_owner"
                         " WHERE owner = :owner"
                         "   AND id = :id")))

(defn select-version [session]
  (let [stmt (-> session qry-select-version .bind)]
    (.setString stmt "owner" "00948406900504")
    (.setInt stmt "id" 95971)
    (when-let [result (first (.execute @session stmt))]
      (.getInt result "version"))))