(ns ingestor.log
  (:gen-class))

(defn start [modo]
  (println "Modo " modo ": Começando a leitura das mensagens."))

(defn insert [record]
  (println
   (format "Fazendo insert no BD do comando:\noffset = %s\nkey = %s\nvalue = %s\npartition = %s\n"
           (.offset record)
           (.key record)
           (.value record)
           (.partition record))))