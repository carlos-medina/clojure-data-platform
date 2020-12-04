(defproject api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.datastax.cassandra/cassandra-driver-core "3.8.0"]
                 [metosin/jsonista "0.2.5"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [ring/ring-json "0.5.0"]]
  :main ^:skip-aot api.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
