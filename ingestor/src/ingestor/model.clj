(ns ingestor.model
  (:require [ingestor.db :as db]
            [ingestor.util :as util]))

;; TODO: remover offset. Estou salvando no momento só para garantir que não estou perdendo dados
(defn upsert-cmd [cmd session offset]
  "Atualiza o comando na tabela comandos_por_owner caso não exista um ou o comando anterior possua
  uma version menor que o comando atual. Caso o comando anterior possuir uma version maior, retorna
  nil."
  (let [version-anterior (db/select-version cmd session)
        version-atual (-> cmd util/str->coll :Version)
        _ (println "Version anterior:\n" version-anterior "\n")
        _ (println "Version atual:\n" version-atual "\n")]
    (when (or (= nil version-anterior)
            (< version-anterior version-atual))
      (db/upsert-cmd cmd session offset))))

;; Tabela de testes: Todos os comandos são salvos
(defn upsert-cmd-teste [cmd session offset]
  (db/upsert-cmd-teste cmd session offset))

;; Tabela de testes: Lista dos owners
(defn upsert-owner-teste [cmd session]
  (db/upsert-owner-teste cmd session))