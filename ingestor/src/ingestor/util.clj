(ns ingestor.util
  (:require [jsonista.core :as json]
            [clojure.string :as str]))

(def mapper-key
  (json/object-mapper
   {:decode-key-fn true
    :encode-key-fn true}))

(def mapper-key-underscore
  (json/object-mapper
   {:decode-key-fn (fn [s] (keyword (str/replace s \_ \-)))
    :encode-key-fn (fn [kw] (str/replace (name kw) \- \_))}))

(defn coll->str
  ([obj]
   (coll->str obj true))
  ([obj underscore?]
   (json/write-value-as-string obj (if underscore? mapper-key-underscore mapper-key))))

(defn str->coll
  ([s]
   (str->coll s true))
  ([s underscore?]
   (json/read-value s (if underscore? mapper-key-underscore mapper-key))))