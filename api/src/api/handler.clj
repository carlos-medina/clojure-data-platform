(ns api.handler
  (:require [ring.middleware.json :refer :all]
            [api.db :as db]))

(defn handler-200-request [request]
  {:status 200
   :headers {"Content-type" "application/json; charset=utf-8"}
   :body (db/select-comandos-owner-por-tag request)})

(def app
  (-> handler-200-request
      (wrap-json-params)
      (wrap-json-response)))