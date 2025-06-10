;; Title: A Simple Polygon
;; Description: Pentagon centred at 0,0, radius 0.5, 5 sides. And a style
;; Tags:

(ns poly1
  (:require [patterning.layouts :as l]
            [patterning.groups :as p]
            [patterning.library.std :as std]
            [patterning.library.complex_elements :as complex]
            [patterning.color :refer [p-color]]
            [patterning.color :as color]            
            [patterning.maths :refer [PI]]
            [patterning.maths :as maths]

            [patterning.canvasview :as canvasview]))

;; Define your pattern parameters here
(def default-params
  {:width 600
   :height 600
   ;; Add your pattern-specific parameters here
   })

;; PATTERN START

(defn the-pattern [params]
    (std/poly 0 0 0.5 5 
       {:stroke (p-color 100 255 150) 
       :fill (p-color 100 255 200)})
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
