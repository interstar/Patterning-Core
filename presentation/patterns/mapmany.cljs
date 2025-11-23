;; Title: Map Multiple Transformations
;; Description: We can map more then one transformation across a stream

(ns mapmany
  (:require [patterning.layouts :as l :refer [stack clock-rotate grid checkered-grid framed]]
            [patterning.groups :as p :refer [over-style rotate scale ]]
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
  (let
   [rand-color
    (fn
      [p]
      (let
       [c (color/rand-col)]
        (over-style
         {:fill c, :stroke-weight 2
          :stroke (color/darker-color c)}
         p)))
    t
    (stack
     (poly 0 0 0.6 3 {:stroke-weight 1})
     (std/horizontal-line 0 {:stroke-weight 2}))]
    (grid
     6
     (map
      rand-color
      (map
       rotate
       (iterate (partial + 0.2) 0)
       (iterate (partial scale 0.97) t))))))

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