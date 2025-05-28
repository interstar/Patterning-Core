;; Replace "your-pattern-name" with your actual pattern name
(ns your-pattern-name
  (:require [patterning.color :refer [p-color]]
            [patterning.groups :refer [rect]]
            [patterning.library.std :refer [square]]
            [patterning.maths :as maths]))

;; Import the FX(hash) random generator
(def fxhash-random (js/require "./fxhash_random_generator.js"))

;; Define your pattern parameters here
(def default-params
  {:width 800
   :height 800
   ;; Add your pattern-specific parameters here
   })

;; Main pattern generation function
(defn ^:export main [params]
  (let [merged-params (merge default-params (js->clj params :keywordize-keys true))
        random (.-fxhashRandom fxhash-random)]
    ;; Your pattern generation code here
    (clj->js (square {:fill (p-color 255 0 0)}))))

;; Export the default parameters for reference
(defn ^:export get-default-params []
  (clj->js default-params)) 