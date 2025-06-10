;; Title: Turtle
;; Description: A Simple Turtle that turns strings into lines

(ns turtle
  (:require [patterning.layouts :as l :refer [stack clock-rotate grid-layout checked-layout framed]]
            [patterning.groups :as p]
            [patterning.library.turtle :refer [basic-turtle]]
            [patterning.library.l_systems :refer [l-system]]
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
  (stack
   (basic-turtle
    [-0.4 -0.8]
    0.2 0 (/ PI 4)
    "FFFF+F+F+F+FFFFF+F+F+F+F" {}
    {:stroke-weight 2
     :stroke (p-color 100 50 100)})

   (basic-turtle
    [0 0.8] 0.15
    (/ PI -2) (/ PI 4)
    "FFF[-F[-FF]FF]FF+F" {}
    {:stroke-weight 2, :stroke (p-color 50 150 100)})))

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