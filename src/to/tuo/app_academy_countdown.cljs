(ns to.tuo.app-academy-countdown
  (:require [goog.dom :as gdom]
            [goog.string :as gstring]
            [goog.string.format]))

(defn pacific-date []
  (let [current-date (js/Date.)]
    (js/Date. (.toLocaleString
               current-date "en-US"
               #js {:timeZone "America/Los_Angeles"}))))

(defn get-pacific-time []
  (let [date (js/Date.)
        utc-offset (.getTimezoneOffset date)
        pacific-offset (.getTimezoneOffset (pacific-date))]
    (js/Date. (- (.getTime date)
                 (* (- pacific-offset utc-offset)
                    60000)))))

(defn get-time-until-pacific-time [hour minute]
  (let [now (get-pacific-time)
        today-at-hour-minute (doto (js/Date. (.getTime now))
                               (.setHours hour minute 0 0))
        target-time (if (< (.getTime today-at-hour-minute)
                           (.getTime now))
                      (doto (js/Date. (.getTime today-at-hour-minute))
                        (.setDate
                         (inc
                          (.getDate today-at-hour-minute))))
                      today-at-hour-minute)
        time-diff (- (.getTime target-time) (.getTime now))
        hours (quot time-diff (* 1000 60 60))
        minutes (quot (rem time-diff (* 1000 60 60)) (* 1000 60))
        seconds (quot (rem time-diff (* 1000 60)) 1000)]
    {:hours hours
     :minutes minutes
     :seconds seconds}))

(defn format-countdown [{:keys [hours minutes seconds]}]
  (gstring/format "%02dh%02dm%02ds" hours minutes seconds))

(defn update-countdown [what hour minute]
  (let [app (gdom/getElement "app")
        countdown (get-time-until-pacific-time hour minute)]
    (set!
     (.-innerHTML app)
     (str
      "<h1>" what ": " (format-countdown countdown) "</h1>"

      "<h1> end of day: "
      (format-countdown (get-time-until-pacific-time 17 0))
      "</h1>"))))

(defn start-clock []
  (js/setInterval #(update-countdown "class starts" 8 0) 100))

(defn ^:export init []
  (update-countdown "class starts" 8 0)
  (start-clock))

(defn ^:after-load mount []
  (init))

(mount)
