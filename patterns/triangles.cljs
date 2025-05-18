(ns triangles
  (:require [patterning.layouts :refer [clock-rotate checked-layout]
             :as layouts]
            [patterning.library.std :refer [poly]]
            [patterning.view :refer [make-svg]]
            [patterning.color :refer [p-color]]))

;; Define your pattern parameters here
(def default-params
  {:width 800
   :height 800
   ;; Add your pattern-specific parameters here
   })

;; Define your pattern generation function
(defn ^:export main [params]
  (let [merged-params (merge default-params (js->clj params :keywordize-keys true))]
    ;; Your pattern generation code here
    ;; Example:
    ;;(make-svg 800 800
              (let
               [triangles
                (layouts/clock-rotate
                 5
                 (poly
                  0.5
                  0.5
                  0.3
                  3
                  {:stroke (p-color 255 100 100), :stroke-weight 2}))
                pentagon
                (poly 0 0 0.7 5 {:stroke (p-color 0 0 255), :stroke-weight 2})]
                (clj->js (layouts/checked-layout 6 (repeat pentagon) (repeat triangles))))
  ;;            )
  )
) 