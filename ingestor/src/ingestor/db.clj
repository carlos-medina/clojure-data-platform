(ns ingestor.db
  (:require [ingestor.util :as util]))

(defn qry-select-version [session]
  (.prepare session (str "SELECT version"
                         "  FROM comandos_por_owner"
                         " WHERE owner = :owner"
                         "   AND id = :id")))

(defn select-version [cmd session]
  (let [cmd (util/str->coll cmd)
        stmt (-> session qry-select-version .bind)]
    (.setString stmt "owner" (:Owner cmd))
    (.setInt stmt "id" (:ID cmd))
    (when-let [result (first (.execute session stmt))]
      (.getInt result "version"))))

(defn qry-upsert-cmd [session]
  (.prepare session (str "UPDATE comandos_por_owner"
                         "   SET tags = :tags,"
                         "       version = :version"
                         " WHERE owner = :owner"
                         "   AND id = :id")))

(defn upsert-cmd [cmd session]
  (let [cmd (util/str->coll cmd)
        stmt (-> session qry-upsert-cmd .bind)]
    (.setString stmt "tags" (-> cmd :Tags util/coll->str))
    (.setInt stmt "version" (:Version cmd))
    (.setString stmt "owner" (:Owner cmd))
    (.setInt stmt "id" (:ID cmd))
    (.executeAsync session stmt)))

(defn qry-upsert-cmd-teste [session]
  (.prepare session (str "UPDATE comandos_por_owner_teste"
                         "   SET offset = :offset,"
                         "       tags = :tags"
                         " WHERE owner = :owner"
                         "   AND id = :id"
                         "   AND version = :version")))

(defn upsert-cmd-teste [cmd session offset]
  (let [cmd (util/str->coll cmd)
        stmt (-> session qry-upsert-cmd-teste .bind)]
    (.setInt stmt "offset" offset)
    (.setString stmt "tags" (-> cmd :Tags util/coll->str))
    (.setInt stmt "version" (:Version cmd))
    (.setString stmt "owner" (:Owner cmd))
    (.setInt stmt "id" (:ID cmd))
    (.executeAsync session stmt)))

(defn qry-upsert-owner-teste [session]
  (.prepare session (str "INSERT INTO owners"
                         "       (particao,owner)"
                         "       VALUES "
                         "       (:particao,:owner)")))

(defn upsert-owner-teste [cmd session]
  (let [cmd (util/str->coll cmd)
        stmt (-> session qry-upsert-owner-teste .bind)]
    (.setInt stmt "particao" 1)
    (.setString stmt "owner" (:Owner cmd))
    (.executeAsync session stmt)))