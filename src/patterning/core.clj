(ns patterning.core
  (:require [patterning.maths :as maths])
  (:require [patterning.sshapes :refer [->SShape ]])

  (:require [patterning.groups :as groups])
  (:require [patterning.layouts :refer [framed clock-rotate stack grid-layout diamond-layout
                                        four-mirror four-round nested-stack checked-layout
                                        half-drop-grid-layout random-turn-groups h-mirror]])

  (:require [patterning.library.std :refer [poly spiral horizontal-line]])
  (:require [patterning.library.turtle :refer [basic-turtle]])
  (:require [patterning.library.complex_elements :refer [vase zig-zag]])
  (:require [patterning.view :refer [make-txpt make-svg]])
  (:require [patterning.color :refer [p-color]])

  (:require [patterning.examples.tutorial :as tutorial])
  (:require [patterning.examples.framedplant :as framedplant])
  (:require [patterning.examples.design_language1 :as design-language])
  (:require [patterning.library.symbols :as symbols])

  (:require [patterning.examples.interactive :as interactive])
  (:require [patterning.examples.testing :as testing])
  (:require [patterning.api :refer :all])


)


;; +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
;; THIS IS THE CURRENT PATTERN
;; assign to "final-pattern" the result of creating a pattern,

(def col (p-color 140 220 180))
(def col-f (p-color 190 255 200 100))

(defn a-round [n] (clock-rotate n (poly 0 0.5 0.3 n {:stroke col :stroke-weight 2 :fill col-f} )))


(defn rand-col [] (p-color (rand-int 255) (rand-int 255) (rand-int 255) (rand-int 255)))
(defn darker-color [c] (apply p-color (map (partial * 0.7) c)))

(defn random-color [p] (let [c (rand-col)] (groups/over-style {:fill c :stroke (darker-color c)} p ) ))

(def t (stack
        (poly 0 0 0.6 3 {:stroke col :fill (p-color 255 0 0) :stroke-weight 2})
        (horizontal-line 0 {:stroke col :stroke-weight 2})
        )
  )

(def final-pattern (grid-layout 6 (map (fn [a p] (groups/rotate a p))
                                       (iterate (partial + 0.15) 0)
                                       (iterate (partial groups/scale 0.97) t))))

(comment
  (defn f-seq [fs patterns] (map (fn [[f p]] (f p)) (map vector fs patterns)))

  (defn fill-seq [cols] (map (fn [c] (partial groups/over-style {:stroke c :fill (darker-color c)})) cols) )

  (def final-pattern (grid-layout
                      6
                      (cycle (f-seq
                              (fill-seq (cycle [(p-color 240 100 100 100 )
                                                (p-color 100 255 200 100 )
                                                (p-color 150 100 255 100)]))
                              (map a-round [3 4 5 6 7])))) )
  )




(defn -main [& args]

  ;; Write the pattern data-structure to a file
  (spit "out.patdat" final-pattern )

  ;; AND THIS IS WHERE WE WRITE IT TO out.svg
  (spit "out.svg" (make-svg 800 800 final-pattern))
  )
