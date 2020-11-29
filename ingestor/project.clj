(defproject ingestor "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.apache.kafka/kafka_2.12 "2.4.1"]
                 [com.fzakaria/slf4j-timbre "0.3.17"]
                 [com.datastax.cassandra/cassandra-driver-core "3.8.0"]
                 [metosin/jsonista "0.2.5"]
                 ;; [io.netty/netty "3.4.5.Final"]
                 ;; [io.netty/netty "3.9.9.Final"]
                 ;; [io.netty/netty-tcnative-boringssl-static "2.0.9.Final"]
                 [io.netty/netty-transport-native-epoll "4.1.9.Final"]]
  :main ^:skip-aot ingestor.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
