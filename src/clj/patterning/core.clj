(ns patterning.core
  (:require [patterning.maths :as maths :refer [PI]]

            [patterning.sshapes
             :refer [->SShape to-triangles ]
             :as sshapes]

            [patterning.strings :as strings]
            [patterning.groups :as groups]
            [patterning.layouts
             :refer [framed clock-rotate stack grid-layout diamond-layout
                     four-mirror four-round nested-stack checked-layout
                     half-drop-grid-layout random-turn-groups h-mirror ring
                     sshape-as-layout]]

            [patterning.library.std
             :refer [poly star nangle spiral diamond
                     horizontal-line square drunk-line]]
            [patterning.library.turtle :refer [basic-turtle]]
            [patterning.library.l_systems :refer [l-system]]
            [patterning.library.complex-elements :refer [vase zig-zag]]
            [patterning.view :refer [make-txpt make-svg]]
            [patterning.color :refer [p-color remove-transparency] ]
            [patterning.examples.framedplant :as framedplant]
            [patterning.examples.design_language1 :as design-language]
            [patterning.examples.city :as city]
            [patterning.library.symbols :as symbols]
            [patterning.library.complex-elements
             :refer [petal-pair-group petal-group]]
            [patterning.library.machines :refer [make-cog spaced-engaged cog->pattern ]]
            
            
            [patterning.api :refer :all]

            [clojure.pprint :as pp]

            [malli.core :as m]

            ))


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
  '(let [blue {:fill (p-color 150 150 255)
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

(def p4 '(ring 8 0.5 (map randomize-color (cycle (map a-nangle [5 7 9])))))

(def p5 '(let [l (drunk-line 10 0.1 :angle-range 0.4)
              s (map randomize-color (cycle (map a-nangle [5 7 9])))   ]
          (clock-rotate 12 (stack l (sshape-as-layout (first l) s 0.1)))))


(def p6 '(let [p (poly 0 0 0.8 9 {:fill (p-color 200 100 100 100) :stroke-weight 2 :stroke (p-color 100 200 150)})
              ss (first p)
              p2 (groups/triangle-list-to-pattern (to-triangles ss))
              p3 (apply stack (map #(-> % vector randomize-color) p2))
              ]
          p3)  )

(def p7
  '(let
      [grow (l-system [["F" "F[+F]F[-F]Y[F]"] ["Y" "Z"]])]
    (basic-turtle
     [0 1]
     0.1
     (/ PI -2)
     (/ PI 9)
     (grow 4 "F")
     {\Z
      (fn [x y a]
        (do
          (poly x y 0.03 8
                {:fill (p-color 255 0 0) :stroke-weight 0})))
      }
     {:stroke (p-color 0 155 50)})) )


(def p8
  '(let [A (make-cog 0 0 10 0.8 0.15 0.15 0.04 0.4 0.15 :square 0.08 8 0.5 )
         B (spaced-engaged A 2/1)   ]
     (stack
      (groups/rect -1 -1 2 2 {:fill (p-color 255)})
      (groups/reframe
       (stack       
        (cog->pattern A false)
        (cog->pattern B true)
        )))))

(defn rcol []
  {:stroke 
   (rand-nth
    [
     (p-color 127 255 127)
     (p-color 255 )
     (p-color 200 100 200)
     (p-color 200 200 100)
     
     (p-color 102 255 178)
     (p-color  255 51 255)     ; pink
     (p-color 0 204 204)
     (p-color  255 51 51)
     (p-color   255 255 204   )
     
     ])
   :stroke-weight 2})

(defn t1
  ([p] (t1 p (rcol))) 
  ([p s]
   (->>
    p
    (stack
     (drunk-line 10 0.2 :angle-range 0.3 s)
     )
    four-round
    (groups/rotate (/ (rand-int 10) 10))
    )))

(def p9
  '(let [dark-fill
         (fn [style]
           (conj style {:fill (darker-color (:stroke style))})
           )
         ]

     (stack
      (groups/rect -1 -1 2 2 {:fill (p-color 0)})
      
      (grid-layout
       5 
       (repeat (t1 []  {:stroke (p-color 250 180 200)
                        :stroke-weight 2}))
       ) 

      (->>
       (apply stack 
              (take 3
                    (iterate t1 (drunk-line 10 0.1 :angle-range 0.5 (rcol)))
                    ))
       four-mirror
       )
      )
     ))

(defn finals [ps]
  (println "Producing Example Outputs in outs/")
  (doseq [[n qp] ps]
    (println "-----------------------------------------------------------------------")
    (println n)
    (let [p (binding [*ns* (find-ns 'patterning.core) ] (eval qp))]
      (pp/pprint p (clojure.java.io/writer (str "outs/" n ".patdat")) )
      (when-not (m/validate groups/Group p)
        (println "Invalid pattern:" (m/explain groups/Group p)))
      (spit (str "outs/" n ".svg") (make-svg 800 800 p) )))
  )
(defn -main [& args]
  (finals
   [

    ["p1 Framed Plant" '(framedplant/framed-plant)]
    ["p2 Chita" p2]
    ["p3 Ringed Flower of Life"
     '(stack
       (groups/rect -1 -1 2 2 {:fill (p-color 255)})
       (groups/scale 0.8 (symbols/ringed-flower-of-life
                          {:fill (p-color 160 120 180 10)
                           :stroke-weight 3
                           :stroke (p-color 0 100 255)})))]
    ["p4 Ring of n-angles" p4]
    ["p5 n-angles following clock-rotated drunklines" p5]
    ["p6" p6]
    ["p7 L-System" p7]
    ["p8 Cog Pair" p8]    
    ["p9 Black Square" p9]
    ["p10 The City We Invent" '(city/city 11)]    
    ])  )

