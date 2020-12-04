(ns api.handler
  (:require [ring.middleware.json :refer :all]))

;; TODO: Passar a session como parâmetro do handler?
(defn handler-200-request [request]
  {:status 200
   :headers {"Content-type" "text/html"}
   :body (str request)})

(def app
  (-> handler-200-request
      (wrap-json-params)))