(ns patterning.core
  (:require [patterning.maths :as maths])
  (:require [patterning.sshapes :refer [->SShape ]])
  (:require [patterning.strings :as strings])
  (:require [patterning.groups :as groups])
  (:require [patterning.layouts :refer [framed clock-rotate stack grid-layout diamond-layout
                                        four-mirror four-round nested-stack checked-layout
                                        half-drop-grid-layout random-turn-groups h-mirror]])

  (:require [patterning.library.std :refer [poly spiral horizontal-line]])
  (:require [patterning.library.turtle :refer [basic-turtle]])
  (:require [patterning.library.complex_elements :refer [vase zig-zag]])
  (:require [patterning.view :refer [make-txpt make-svg]])
  (:require [patterning.color :refer [p-color remove-transparency] ])

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

(defn a-round [n] (clock-rotate n (poly 0 0.5 0.3 n {:stroke (p-color 0 0 0) :stroke-weight 2 :fill (p-color 0 0 0)} )))


(defn rand-col [] (p-color (rand-int 255) (rand-int 255) (rand-int 255) ))
(defn darker-color [c] (apply p-color (map (partial * 0.7) c)))
(defn randomize-color [p] (let [c (rand-col)] (groups/over-style {:fill c
                                                                  :stroke (darker-color c)} p ) ))

(def final-pattern (grid-layout
                    6
                    (map randomize-color (cycle (map a-round [3 4 5 6 7])))) )




(defn -main [& args]

  ;; Write the pattern data-structure to a file
  (spit "out.patdat" (into [] final-pattern) )

  ;; AND THIS IS WHERE WE WRITE IT TO out.svg
  (let [svg (make-svg 800 800 final-pattern) ]
    (spit "out.svg" svg ) )
  )
