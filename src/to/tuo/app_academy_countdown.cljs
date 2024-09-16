(ns to.tuo.app-academy-countdown
  (:require [goog.dom :as gdom]))

(defn hello-world []
  (let [app (gdom/getElement "app")]
    (set! (.-innerHTML app) "<h1>hello, clojurescript</h1>")))

(defn ^:export init []
  (hello-world))

(defn ^:after-load mount []
  (init))

(mount)
