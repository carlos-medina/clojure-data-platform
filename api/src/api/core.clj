(ns api.core
  (:use ring.adapter.jetty)
  (:require [api.handler :as handler]
            [api.db :as db]))

(defn start []
  (db/start)
  (let [server (run-jetty handler/app {:port  3000
                                       :join? false})]
    (println "Inicializando servidor da API na porta 3000")
    server))

(defn stop [server]
  (.stop server)
  (println  "Servidor da API encerrado"))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
