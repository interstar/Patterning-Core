(ns pattern.core
  (:require [patterning.core :as p]
            [patterning.maths :as m]))

;; Define your pattern parameters here
(def default-params
  {:width 800
   :height 800
   ;; Add your pattern-specific parameters here
   })

;; Define your pattern generation function
(defn make-pattern [params]
  (let [merged-params (merge default-params params)]
    ;; Your pattern generation code here
    ;; Example:
    (p/APattern
     (p/->SShape
      {:stroke "black"
       :fill "none"}
      (p/line [[0 0] [100 100]])))))

;; Export the function for JavaScript
(defn ^:export generate [params]
  (make-pattern (clj->js params)))

;; Export the default parameters for reference
(defn ^:export get-default-params []
  (clj->js default-params)) 