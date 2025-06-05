(ns chita
  (:require [patterning.layouts :refer [stack clock-rotate half-drop-grid-layout]]
            [patterning.groups :refer [translate stretch scale rotate over-style]]
            [patterning.library.std :refer [poly diamond square]]
            [patterning.library.complex_elements :refer [petal-group]]
            [patterning.color :refer [p-color]]
            [patterning.maths :refer [PI]]
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
        random (:random merged-params)
        canvas (:canvas merged-params)
        
        blue {:fill (p-color 150 150 255)
              :stroke (p-color 50 50 255)
              :stroke-weight 2}
        
        yellow {:fill (p-color 180 250 50)
                :stroke-weight 1
                :stroke (p-color 150 120 20)}
        
        green {:fill (p-color 20 100 30)}
        
        rand-rot #(rotate (* (mod (.randomInt random 100) 8) (/ PI 4)) %)
        
        inner (stack
               (poly 0 0 0.3 12 {:fill (p-color 50 50 220)
                                 :stroke-weight 0})
               (->> (poly 0 0.1 0.06 5 yellow)
                    (clock-rotate 5)
                    (translate -0.09 -0.07)))
        
        flower (stack
                (clock-rotate 15 (petal-group blue 0.3 0.8))
                (let [y-stream
                      (clock-rotate 15 (petal-group yellow 0.3 0.8))]
                  (stack (list (nth y-stream 5))
                         (take 2 y-stream)))
                inner)
        
        leafy (fn [n]
                (stack
                 (->> (diamond green)
                      (stretch 0.5 0.25)
                      (translate -0.6 0)
                      (clock-rotate 4)
                      (drop 2)
                      (take n))
                 (->> (diamond {:fill (p-color 200 255 200)
                               :stroke-weight 0})
                      (stretch 0.2 0.1)
                      (translate -0.6 0)
                      (clock-rotate 4)
                      (drop 2)
                      (take n))
                 flower))
        
        whites (stack
                (->> (poly 0 0.3 0.2 5
                          {:fill (p-color 255 255 255)
                           :stroke-weight 0})
                     (clock-rotate 7))
                (poly 0 0 0.2 8 {:fill (p-color 0 0 200)}))
        
        small-yellow (let [all (->> (diamond yellow)
                                   (stretch 0.4 0.5)
                                   (translate 0 -0.6)
                                   (clock-rotate 5)
                                   (scale 0.6))
                          blues (over-style blue all)]
                      (stack
                       all
                       (list (nth blues 0))
                       (list (nth blues 2))))
        
        leafy-seq (->> (iterate (fn [x] (+ 1 (mod (.randomInt random 100) 2))) 0)
                       (map leafy)
                       (map rand-rot))
        
        pattern (stack
                 (square {:fill (p-color 255 10 10)})
                 (half-drop-grid-layout
                  17 (map rand-rot
                         (map #(.randomNth random %)
                              (repeat [whites []
                                       small-yellow small-yellow]))))
                 (half-drop-grid-layout 6 leafy-seq))]
    
    (js/console.log "Canvas received:" canvas)
    (js/console.log "Random generator received:" random)
    (js/console.log "Pattern generated:" pattern)
    
    (when canvas
      (js/console.log "Setting up responsive canvas...")
      (canvasview/setupResponsiveCanvas canvas pattern))
    
    pattern)) 