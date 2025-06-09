(ns patterning_quil.dev
  (:require [clojure.tools.namespace.repl :refer [refresh]]
            [patterning.library.std :as std]
            [patterning.groups :as groups]
            [patterning.layouts :as l]
            [patterning.color :refer [p-color]]
            [patterning_quil.core :as core]
            [patterning_quil.quilview :refer [draw-group]]
            [patterning.view :refer [make-txpt]]
            [clojure.math :as math]
            [quil.core :as q]))

(def sketch (atom nil))


(defn map-mouse [x y w h fc]
  {:x (q/map-range x 0 w -1 1)
   :y (q/map-range y 0 h -1 1)
   :x01 (q/map-range x 0 w 0 1)
   :y01 (q/map-range y 0 h 0 1)
   :width w
   :height h
   :frame-count fc
   })

(defn p1 [{:keys [x y x01 y01 frame-count]}]
  (l/four-mirror
   (l/grid-layout
    4
    (repeat
     (l/four-round
      (std/poly x y x01 (+ 3 (int (* y01 10)))
                {:stroke-wight 4
                 :stroke (p-color 255 0 0)
                 :fill (p-color (mod frame-count 255) 200 200)}))))) 
  )

(defn my-pattern [{:keys [x y x01 y01 frame-count]}]
  (let [max-size 24]
    (groups/scale
     0.9
     (l/checked-layout
      9
      (repeat (std/poly 0 0 x01 4 {:fill (p-color 0 255 255)}))
      (cycle
       (map #(std/poly 0 0 (q/map-range
                            (math/log
                             (+ 1 (mod (+ frame-count %1) max-size)))
                            0
                            (math/log max-size)
                            0 1 )
                       9 {:fill (p-color 255 0 0)})
            (range max-size)
            
            ))
      )))
  )

(defn dev-setup []
  (q/frame-rate 5)
  )

(defn dev-draw []
  (q/background 0)
  (let [mm (map-mouse (q/mouse-x) (q/mouse-y) (q/width) (q/height) (q/frame-count) )]
    (println mm)
    (draw-group
     (make-txpt [-1 -1 1 1] [0 0 core/my-width core/my-height ])
     (my-pattern mm))))




(defn start-sketch []
  (when @sketch
    (q/exit))
  (reset! sketch (q/sketch
                  :title "Patterning Live"
                  :setup dev-setup
                  :draw dev-draw
                  :size [core/my-width core/my-height]
                  :on-close #(System/exit 0))))

(defn stop-sketch []
  (when @sketch
    (q/exit)
    (reset! sketch nil)))

(defn refresh-all []
  (stop-sketch)
  (refresh)
  (start-sketch))

;; Start the sketch when this namespace is loaded
(start-sketch)

;; You can now customize dev-setup and dev-draw for live coding! 
