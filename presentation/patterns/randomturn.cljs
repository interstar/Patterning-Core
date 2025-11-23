;; Title: Random Turn Layout
;; Description: Map random turns onto a stream of patterns, then lay this out in a grid

(ns randomturn
  (:require [patterning.layouts :as l :refer [stack clock-rotate grid checkered-grid framed]]
            [patterning.groups :as p :refer [rotate]] 
            [patterning.sshapes :refer [->SShape]]
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

(defn q1-rot-group [group] 
  (rotate (float (/ maths/PI 2)) group))
(defn q2-rot-group [group] 
  (rotate maths/PI group))
(defn q3-rot-group [group] 
  (rotate (-  (float (/ maths/PI 2))) group))

(defn random-turn-groups 
  [groups]
  (let [random-turn
        (fn [group]
          (case (rand-int 4)
            0 group
            1 (q1-rot-group group)
            2 (q2-rot-group group)
            3 (q3-rot-group group)))]
    (map random-turn groups)))


(defn the-pattern [params]
  (let
   [orange (p-color 254 129 64)]
    (stack
     (std/square 
      {:fill (p-color 50 80 100)})
     (grid 10
      (random-turn-groups
       (repeat
        [(->SShape
          {:fill (p-color 230 180 90)
           :stroke orange}
          [[-1 -1] [1 -1] [-1 1]])]) 
       
       )))))

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