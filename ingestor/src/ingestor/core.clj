(ns ingestor.core
  (:import [org.apache.kafka.clients.consumer KafkaConsumer]))

(defn new-consumer [group-id]
  (KafkaConsumer.
   {"bootstrap.servers"                     "kafka-teste.dev-arquivei.com.br:29092"
    "group.id"                              group-id
    "enable.auto.commit"                    "false"
    "key.deserializer"                      "org.apache.kafka.common.serialization.StringDeserializer"
    "value.deserializer"                    "org.apache.kafka.common.serialization.StringDeserializer"
    "auto.offset.reset"                     "earliest"}))

(defn -main [& args]
  (let [interface (first args)]
    (case interface
      "consumer" (let [grupo-de-consumo "1" ;(str (rand))
                       consumer (new-consumer grupo-de-consumo)]
                   (println "Começando a leitura das mensagens")
                   (.subscribe consumer ["documents"])
                   (doseq [i (range 1 5)] ; while true
                     (let [records (.poll consumer (java.time.Duration/ofMillis 5000))]
                       (doseq [record records]
                         (println (format "offset = %s\nkey = %s\nvalue = %s\n" (.offset record) (.key record) (.value record))))))))))