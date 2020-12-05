(ns api.handler
  (:require [ring.middleware.json :refer :all]
            [api.db :as db]))

(defn handler-200-request [request]
  {:status 200
   ;; TODO: Tipo do conteúdo e o conteúdo do body não deveria ser JSON?
   :headers {"Content-type" "text/html"}
   :body (str (db/select-comandos-owner request))})

(def app
  (-> handler-200-request
      (wrap-json-params)))