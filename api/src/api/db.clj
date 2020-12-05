(ns api.db
  (:require [api.util :as util])
  (:import [com.datastax.driver.core Cluster ConsistencyLevel HostDistance PoolingOptions QueryOptions]
           [com.datastax.driver.core.policies LatencyAwarePolicy Policies]))

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

(def session (atom nil))

(defn start []
  (reset! session
    (.connect
     (-> (Cluster/builder)
         (configs-cassandra false)
         (.build)) "ingestor")))

(defn filtra-comandos-por-tag [comandos tags]
  (let [tags (set tags)
        filtro (fn [filtrados comando]
                 (conj filtrados
                       (when
                        (clojure.set/subset? tags (set (util/str->coll (.getString comando "tags"))))
                        comando)))]
    (remove nil? (reduce filtro [] comandos))))

(defn formata-resposta [comandos]
  ;; TODO: Versão final tem que ter a saída como JSON
  ;; comandos por ser vazio ou pode ser um vetor de objetos do cassandra
  (let [formata-documento (fn [comando] {"ID" (.getInt comando "id")
                                         "Version" (.getInt comando "version")
                                         "Tag" (util/str->coll (.getString comando "tags"))})
        documentos (reduce #(conj %1 (formata-documento %2)) [] comandos)]
    {"Documents" documentos}))

(defn qry-select-comandos-owner []
  (.prepare @session (str "SELECT *"
                          "  FROM comandos_por_owner"
                          " WHERE owner = :owner")))

(defn select-comandos-owner-por-tag [request]
  (let [stmt (.bind (qry-select-comandos-owner))]
    (.setString stmt "owner" (-> request (get :params) (get "Owner")))
    (let [result (.execute @session stmt)]
      (if (empty? result)
        ;; TODO: Tirar o do com o print e deixar só o segundo comando
        (do
         (println "Select vazio")
         (formata-resposta result))
        (let [tags (-> request (get :params) (get "Filters") (get "Tags"))]
          (if (empty? tags)
            (formata-resposta result)
            (formata-resposta (filtra-comandos-por-tag result tags))))))))

