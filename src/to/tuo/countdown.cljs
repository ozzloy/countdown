(ns to.tuo.countdown
  (:require
   [clojure.string :as str]
   [goog.dom :as gdom]
   [goog.string :as gstring]
   [goog.string.format]))

(defn local-offset [] (.getTimezoneOffset (js/Date.)))

(defn pacific-offset [] 420)

(defn time-until-pacific-time [hour minute]
  (let [local-now (js/Date.)
        pacific-time (doto (js/Date. (.getTime local-now))
                       (.setHours hour (- (+ minute (pacific-offset))
                                          (local-offset)) 0 0))
        today? (< (.getTime local-now) (.getTime pacific-time))
        pacific-time (if today?
                       pacific-time
                       (doto pacific-time
                         (.setDate (inc (.getDate pacific-time)))))
        delta (- (.getTime pacific-time) (.getTime local-now))
        hours (quot delta (* 1000 60 60))
        minutes (quot (rem delta (* 1000 60 60)) (* 1000 60))
        seconds (quot (rem delta (* 1000 60)) 1000)]
    {:hours hours, :minutes minutes, :seconds seconds}))

(defn format-countdown [{:keys [hours minutes seconds]}]
  (gstring/format "%02dH%02dM%02dS" hours minutes seconds))

(defn update-countdown []
  (let [app (gdom/getElement "app")]
    (set!
     (.-innerHTML app)
     (str
      "<h1>" what ": " (format-countdown countdown) "</h1>"

      "<h1> end of day: "
      (format-countdown (time-until-pacific-time 17 0))
      "</h1>"))))

(defn start-clock []
  (js/setInterval update-countdown 100))

(defn ^:export init []
  (update-countdown)
  (start-clock))

(defn ^:after-load mount []
  (init))

(mount)
