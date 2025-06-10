;; Title: Stack
;; Description: Supperimpose Red Triangles and a Pentagon with Stack 

(ns stack1
   (:require [patterning.layouts :as l :refer [stack clock-rotate grid-layout checked-layout]]
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
(stack
 (poly 0 0 0.7 5 {:stroke (p-color 0 0 255), :stroke-weight 2})
 (clock-rotate
  5
  (poly
   0.5 0.5
   0.3 3
   {:stroke (p-color 255 100 100)
    :stroke-weight 2})))

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