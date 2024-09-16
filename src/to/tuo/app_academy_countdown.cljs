(ns to.tuo.app-academy-countdown
  (:require [goog.dom :as gdom]
            [goog.string :as gstring]
            [goog.string.format]))

(defn get-timezone []
  (.. (js/Intl.DateTimeFormat) (resolvedOptions) -timeZone))

(defn format-time [date]
  (gstring/format "%02d:%02d:%02d"
                  (.getHours date)
                  (.getMinutes date)
                  (.getSeconds date)))

(defn update-time []
  (let [app (gdom/getElement "app")
        current-time (js/Date.)
        timezone (get-timezone)]
    (set! (.-innerHTML app)
          (str "<h1>current time: " (format-time current-time) "</h1>"
               "<h2>timezone: " timezone))))

(defn start-clock []
  (js/setInterval update-time 100))

(defn ^:export init []
  (update-time)
  (start-clock))

(defn ^:after-load mount []
  (init))

(mount)
