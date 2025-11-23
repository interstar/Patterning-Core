;; Title: Clock Rotate
;; Description: Five Red Triangles in a circle using Clock Rotate 

(ns redtriangles
  (:require [patterning.layouts :as l :refer [clock-rotate grid checkered-grid]]
           [patterning.groups :as p]
           [patterning.library.std :as std :refer [poly]]
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

(clock-rotate
 5
 (poly
  0.5 0.5
  0.3 3
  {:stroke (p-color 255 100 100) 
   :stroke-weight 2}))
)

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