(ns build
  (:require [clojure.tools.build.api :as b]))

(defn clean [_]
  (b/delete {:path "target"})
  (b/delete {:path ".cpcache"})
  (b/delete {:path "resources/public/cljs-out"})
  (b/delete {:path "resources/public/js"}))
