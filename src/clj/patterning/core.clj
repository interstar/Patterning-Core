(ns patterning.core
  (:require [patterning.maths :as maths])
  (:require [patterning.sshapes :refer [->SShape ]])
  (:require [patterning.strings :as strings])
  (:require [patterning.groups :as groups])
  (:require [patterning.layouts :refer [framed clock-rotate stack grid-layout diamond-layout
                                        four-mirror four-round nested-stack checked-layout
                                        half-drop-grid-layout random-turn-groups h-mirror ring
                                        sshape-as-layout]])

  (:require [patterning.library.std :refer [poly star nangle spiral diamond
                                            horizontal-line square drunk-line]])
  (:require [patterning.library.turtle :refer [basic-turtle]])
  (:require [patterning.library.complex_elements :refer [vase zig-zag]])
  (:require [patterning.view :refer [make-txpt make-svg]])
  (:require [patterning.color :refer [p-color remove-transparency] ])


  (:require [patterning.examples.framedplant :as framedplant])
  (:require [patterning.examples.design_language1 :as design-language])

  (:require [patterning.library.symbols :as symbols])



  (:require [patterning.api :refer :all])

  (:require [patterning.library.complex_elements :refer [petal-pair-group petal-group]])


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


(def p2
  (let [blue {:fill (p-color 150 150 255)
              :stroke (p-color 50 50 255)
              :stroke-weight 2}

        yellow {:fill (p-color 180 250 50)
                :stroke-weight 1
                :stroke (p-color 150 120 20)}

        green {:fill (p-color 20 100 30)}

        rand-rot #(groups/rotate (* (mod (rand-int 100) 8) (/ maths/PI 4)) %)

        inner (stack
               (poly 0 0 0.3 12 {:fill (p-color 50 50 220)
                                 :stroke-weight 0})
               (->> (poly 0 0.1 0.06 5 yellow)
                    (clock-rotate 5)
                    (groups/translate -0.09 -0.07)
                    ))

        flower (stack
                (clock-rotate 15 (petal-group blue 0.3 0.8 ))
                (let [y-stream
                      (clock-rotate 15 (petal-group yellow 0.3 0.8 ))]
                  (stack (list (nth y-stream 5))
                         (take 2 y-stream)
                         ))
                inner)


        leafy (fn [n]
                (stack
                 (->> (diamond green)
                      (groups/stretch 0.5 0.25)
                      (groups/translate -0.6 0)
                      (clock-rotate 4)
                      (drop 2)
                      (take n))
                 (->> (diamond {:fill (p-color 200 255 200)
                                :stroke-weight 0})
                      (groups/stretch 0.2 0.1)
                      (groups/translate -0.6 0)
                      (clock-rotate 4)
                      (drop 2)
                      (take n))
                 flower))

        whites (stack
                (->> (poly 0 0.3 0.2 5
                           {:fill (p-color 255 255 255)
                            :stroke-weight 0})
                     (clock-rotate 7)

                     )
                (poly 0 0 0.2 8 {:fill (p-color 0 0 200)}))

        small-yellow (let [all (->> (diamond yellow)
                                    (groups/stretch 0.4 0.5)
                                    (groups/translate 0 -0.6 )
                                    (clock-rotate 5)
                                    (groups/scale 0.6 ))
                           blues (groups/over-style blue all)]

                       (stack
                        all
                        (list (nth blues 0))
                        (list (nth blues 2))))

        leafy-seq (->> (iterate (fn [x] (+ 1 (mod (rand-int 100) 2))) 0)
                       (map leafy)
                       (map rand-rot))
        ]
    (stack
     (square {:fill (p-color 255 10 10)})
     (half-drop-grid-layout
      17 (map rand-rot
              (map rand-nth
                   (repeat [whites []
                            small-yellow small-yellow ]))))

     (half-drop-grid-layout 6 leafy-seq)
     ) ))


(defn a-nangle [n] (nangle 0 0 0.6 n {:stroke (p-color 0 0 0) :stroke-weight 2 }   ))
(defn randomize-color [p] (let [c (rand-col)] (groups/over-style {:fill (darker-color c)
                                                                  :stroke c} p ) ))

(def p4 (ring 8 0.5 (map randomize-color (cycle (map a-nangle [5 7 9])))))

(def p5 (let [l (drunk-line 10 0.1)
              s (map randomize-color (cycle (map a-nangle [5 7 9])))   ]
          (clock-rotate 12 (stack l (sshape-as-layout (first l) s 0.1)))))


(defn finals [ps]
  (doseq [[n p] ps]
    (println n)
    (spit (str "outs/" n ".patdat") p)
;    (println p)
    (spit (str "outs/" n ".svg") (make-svg 800 800 p) ))
  )

(defn -main [& args]
  (finals
   [["p1" (framedplant/framed-plant)]
    ["p2" p2]
    ["p3" (groups/scale 0.8 (symbols/ringed-flower-of-life {:fill (p-color 160 120 180 20)
                                                            :stroke-weight 3
                                                            :stroke (p-color 0 100 255)}))]
    ["p4" p4]
    ["p5" p5]])  )
