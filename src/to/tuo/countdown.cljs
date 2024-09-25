(ns to.tuo.countdown
  (:require
   [clojure.string :as str]
   [goog.dom :as gdom]
   [goog.string :as gstring]
   [goog.string.format]))

(defn things []
  [{:what "Class Start" :hour 8 :minute 0}
   {:what "Break Time" :hour 10 :minute 0}
   {:what "Back from Break" :hour 10 :minute 10}
   {:what "Lunch Starts" :hour 11 :minute 15}
   {:what "Lunch Ends" :hour 12 :minute 30}
   {:what "End 🍐" :hour 16 :minute 0}
   {:what "Class Ends" :hour 17 :minute 0}])

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

(defn format-thing [{:keys [what hour minute]}]
  (str "<div class=\"countdown-row\"><span class=\"timeleft\">"
       (format-countdown (time-until-pacific-time hour minute))
       "</span><span class=\"description\">"
       what
       "</span></div>"))

(defn update-countdown []
  (let [app (gdom/getElement "app")]
    (set!
     (.-innerHTML app)
     (clojure.string/join (map format-thing (things)))
     )))

(defn start-clock []
  (js/setInterval update-countdown 100))

(defn ^:export init []
  (update-countdown)
  (start-clock))

(defn ^:after-load mount []
  (init))

(mount)
