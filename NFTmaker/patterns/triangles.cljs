(ns triangles
  (:require [patterning.layouts :refer [clock-rotate checked-layout]
             :as layouts]
            [patterning.library.std :refer [poly]]
            [patterning.view :refer [make-svg]]
            [patterning.color :refer [p-color]]
            [patterning.canvasview :as canvasview]))

;; Define your pattern parameters here
(def default-params
  {:width 800
   :height 800
   ;; Add your pattern-specific parameters here
   })

;; Define your pattern generation function
(defn ^:export main [params]
  (let [merged-params (merge default-params (js->clj params :keywordize-keys true))
        canvas (:canvas merged-params)
        random (:random merged-params)
        
        triangles (layouts/clock-rotate
                   5
                   (poly
                    0.5
                    0.5
                    0.3
                    3
                    {:stroke (p-color 255 100 100), :stroke-weight 2}))
        
        pentagon (poly 0 0 0.7 5 {:stroke (p-color 0 0 255), :stroke-weight 2})
        
        pattern (layouts/checked-layout 6 (repeat pentagon) (repeat triangles))]
    
    (when canvas
      (canvasview/setupResponsiveCanvas canvas pattern))
    
    pattern)) 