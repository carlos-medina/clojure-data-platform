(ns ingestor.db
  (:require [ingestor.util :as util]))

;; TODO: remover offset. Estou salvando no momento só para garantir que não estou perdendo dados
(defn qry-upsert-cmd [session]
  (.prepare session (str "UPDATE comandos_por_owner"
                         "   SET offset = :offset,"
                         "       tags = :tags,"
                         "       version = :version"
                         " WHERE owner = :owner"
                         "   AND id = :id")))

;; TODO: o upsert deve fazer primeiro um select pra verificar se já existe um documento igual
;; TODO: que tenha uma version superior. Se não tiver nenhum documento ou a version for anterior
;; TODO: deve ser feito o upsert
(defn upsert-cmd [cmd session offset]
  (let [cmd (util/str->coll cmd)
        stmt (-> session qry-upsert-cmd .bind)]
    (.setInt stmt "offset" offset)
    (.setString stmt "tags" (-> cmd :Tags util/coll->str))
    (.setInt stmt "version" (:Version cmd))
    (.setString stmt "owner" (:Owner cmd))
    (.setInt stmt "id" (:ID cmd))
    #_(.executeAsync session stmt)
    (.execute session stmt)))