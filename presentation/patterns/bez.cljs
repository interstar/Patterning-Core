;; Title: Bezier Curves
;; Description: Bezier Curves

(ns bez
  (:require [patterning.layouts :as l :refer [stack clock-rotate grid checkered-grid]]
            [patterning.groups :as p]
            [patterning.library.std :as std :refer [poly drunk-line bez-curve]]
            [patterning.library.complex_elements :as complex]
            [patterning.color :as color :refer [p-color]]
            [patterning.maths :as maths :refer [PI]]
            [patterning.canvasview :as canvasview]))

;; Define your pattern parameters here
(def default-params
  {:width 600
   :height 600
   ;; Add your pattern-specific parameters here
   })

;; PATTERN START

(defn the-pattern [params]
  (bez-curve
   [[0 1] [-1.5 -0.5] [-0.5 -1.5] [0 0]]
   {:stroke (p-color 255 128 64), :stroke-weight 4}))

;; PATTERN END

;; Define your pattern generation function
(defn ^:export main [params]
  (let [merged-params (merge default-params (js->clj params :keywordize-keys true))
        canvas (:canvas merged-params)
        pattern (the-pattern merged-params)]
    
    (when canvas
      (js/console.log "Setting up responsive canvas...")
      (canvasview/setupResponsiveCanvas canvas pattern))
    
    pattern)) 