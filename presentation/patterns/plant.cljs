;; Title: L-Systems
;; Description: Two "Plants" Grown With L-Systems 

(ns plant
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
   (let
    [grow (l-system [["F" "F[+F]F[-F][F]"]])]
     (basic-turtle
      [-0.5 1] 0.1
      (/ PI -2) (/ PI 9)
      (grow 4 "F") {}
      {:stroke (p-color 0 155 50)})) 
   
   (let
    [grow 
     (l-system [["F" "FF[--F]F[+FZ]Y[FF]"] ["Y" "Z"]])]
     (basic-turtle
      [0.6 1] 0.08
      (/ PI -2) (/ PI 9)
      (grow 4 "F")
      {Z
       (fn
         [x y a]
         (poly x y 0.03 8
               {:fill (p-color 255 0 0)
                :stroke-weight 0}))}
      {:stroke (p-color 0 155 50)}))
   ))

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