;; Title: Frame Layout
;; Description: Takes 3 args. Sequences for corners and edges. And a single central pattern.

(ns frame
  (:require [patterning.layouts :as l :refer [stack clock-rotate grid-layout checked-layout framed]]
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
  (let
   [corner
    (stack
     (std/square {:fill (p-color 255 0 0 100)})
     (poly 0 0 0.9 4 {:fill (p-color 240 100 240)}))
    edge
    (stack
     (std/square {:fill (p-color 0 0 255)})
     (poly 0 0 0.5 8 {:fill (p-color 150 150 255)}))
    centre
    (poly 0 0 0.9 30 {:fill (p-color 150 255 140)})]
    (framed 7 (repeat corner) (repeat edge) centre)) 
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