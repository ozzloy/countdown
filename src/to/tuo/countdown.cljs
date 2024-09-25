(ns to.tuo.countdown
  (:require
   [clojure.string :as str]
   [goog.dom :as gdom]
   [goog.string :as gstring]
   [goog.string.format]
   [reagent.core :as r]
   [reagent.dom :as rdom]))

(def now_ (r/atom (js/Date.)))

(defn things []
  [{:what "Class Start"     :hour  8 :minute  0}
   {:what "Break Time"      :hour 10 :minute  0}
   {:what "Back from Break" :hour 10 :minute 10}
   {:what "Lunch Starts"    :hour 11 :minute 15}
   {:what "Lunch Ends"      :hour 12 :minute 30}
   {:what "End 🍐"          :hour 16 :minute  0}
   {:what "Class Ends"      :hour 17 :minute  0}])

(defn local-offset [] (.getTimezoneOffset (js/Date.)))

(defn pacific-offset [] 420)

(defn time-until-pacific-time [now hour minute]
  (let [pacific-time (doto (js/Date. (.getTime now))
                       (.setHours hour (- (+ minute (pacific-offset))
                                          (local-offset)) 0 0))
        today? (< (.getTime now) (.getTime pacific-time))
        pacific-time (if today?
                       pacific-time
                       (doto pacific-time
                         (.setDate (inc (.getDate pacific-time)))))
        delta (- (.getTime pacific-time) (.getTime now))
        hours (quot delta (* 1000 60 60))
        minutes (quot (rem delta (* 1000 60 60)) (* 1000 60))
        seconds (quot (rem delta (* 1000 60)) 1000)]
    {:hours hours, :minutes minutes, :seconds seconds}))

(defn make-row [now thing]
  (let [{:keys [what hour minute]} thing

        {:keys [hours minutes seconds]}
        (time-until-pacific-time now hour minute)]
    {:what what
     :hours hours
     :minutes minutes
     :seconds seconds}))

(defn format-countdown [{:keys [hours minutes seconds]}]
  (gstring/format "%02d:%02d:%02d" hours minutes seconds))

(defn countdown-row [{:keys [what] :as row}]
  [:div.countdown-row
   [:span.timeleft (format-countdown row)]
   [:span.description what]])

(defn countdown-app []
  (fn []
    [:div
     (for [row (sort-by (juxt :hours :minutes :seconds)
                        (map #(make-row @now_ %) (things)))]
       ^{:key row} [countdown-row row])]))

(defn start-clock []
  (js/setInterval #(reset! now_ (js/Date.)) 100))

(defn ^:export init []
  (start-clock)
  (rdom/render [countdown-app]
               (.getElementById js/document "app")))

(defn ^:after-load mount []
  (init))

(mount)
