;; Template for creating a new pattern
(ns pattern-template
  (:require [patterning.color :refer [p-color]]
            [patterning.groups :refer [rect]]
            [patterning.library.std :refer [square]]
            [patterning.maths :as maths]
            [patterning.canvasview :as canvasview]))

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
        canvas (:canvas merged-params)
        random (:random merged-params)
        
        ;; Your pattern generation code here
        pattern (square {:fill (p-color 255 0 0)})]
    
    ;; Set up the canvas if provided
    (when canvas
      (canvasview/setupResponsiveCanvas canvas pattern))
    
    ;; Return the pattern
    pattern))

;; Export the default parameters for reference
(defn ^:export get-default-params []
  (clj->js default-params)) 