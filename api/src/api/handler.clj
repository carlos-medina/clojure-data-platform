(ns api.handler
  (:require [ring.middleware.json :refer :all]
            [api.db :as db]))

(defn handler-200-request [request]
  {:status 200
   ;; TODO: Tipo do conteúdo e o conteúdo do body não deveria ser JSON?
   :headers {"Content-type" "text/html"}
   ;; TODO: Deve ter um wrapper que transforma a response em JSON
   :body (str (db/select-comandos-owner-por-tag request))})

(def app
  (-> handler-200-request
      (wrap-json-params)))