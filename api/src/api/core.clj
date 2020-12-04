(ns api.core
  (:use ring.adapter.jetty)
  (:require [api.handler :as handler]))

(defn start []
  (println "Inicializando API na porta 3000")
  (run-jetty handler/app {:port  3000
                          :join? false}))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
