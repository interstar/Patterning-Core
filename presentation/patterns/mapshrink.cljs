;; Title: Mapping Transformations
;; Description: We map a "shrinking" function to a pattern

(ns mapshrink
  (:require [patterning.layouts :as l :refer [stack clock-rotate grid checkered-grid framed]]
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

(defn a-round
  ([n] (a-round n (p-color 0) (p-color 255)))
  ([n lc fc]
   (clock-rotate n
    (poly 0 0.5 0.4 n
          {:stroke lc
           :fill fc
           :stroke-weight 3}))))

(defn the-pattern [params]
  (let
   [lc (p-color 220 140 180) 
    fc (p-color 255 190 200 100)]
    (grid 4
     (iterate (partial p/scale 0.9) 
              (a-round 9 lc fc)))))

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