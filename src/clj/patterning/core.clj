(ns patterning.core
  (:require [patterning.maths :as maths :refer [PI clock-points distance atan2]]

            [patterning.sshapes
             :refer [->SShape to-triangles ]
             :as sshapes]

            [patterning.strings :as strings]
            [patterning.groups :as groups :refer [translate scale stretch reframe rotate]]
            [patterning.layouts
             :refer [framed clock-rotate stack grid-layout diamond-layout
                     four-mirror four-round nested-stack checked-layout
                     half-drop-grid-layout random-turn-groups h-mirror ring
                     sshape-as-layout]]

            [patterning.library.std
             :refer [poly star nangle spiral diamond
                     horizontal-line square drunk-line rect]]
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

(defn a-round [n] (clock-rotate n (poly n 0.3 0 0.5 {:stroke (p-color 0 0 0) :stroke-weight 2 :fill (p-color 0 0 0)} )))


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
               (poly 12 0.3 0 0 {:fill (p-color 50 50 220)
                                 :stroke-weight 0})
               (->> (poly 5 0.06 0 0.1 yellow)
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
                (->> (poly 5 0.2 0 0.3
                           {:fill (p-color 255 255 255)
                            :stroke-weight 0})
                     (clock-rotate 7)

                     )
                (poly 8 0.2 0 0 {:fill (p-color 0 0 200)}))

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


(defn a-nangle [n] (nangle n 0.6 0 0 {:stroke (p-color 0 0 0) :stroke-weight 2 }   ))
(defn randomize-color [p] (let [c (rand-col)] (groups/over-style {:fill (darker-color c)
                                                                  :stroke c} p ) ))

(def p4 '(ring 8 0.5 (map randomize-color (cycle (map a-nangle [5 7 9])))))

(def p5 '(let [l (drunk-line 10 0.1 :angle-range 0.4)
              s (map randomize-color (cycle (map a-nangle [5 7 9])))   ]
          (clock-rotate 12 (stack l (sshape-as-layout (first l) s 0.1)))))


(def p6 '(let [p (poly 9 0.8 0 0 {:fill (p-color 200 100 100 100) :stroke-weight 2 :stroke (p-color 100 200 150)})
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
          (poly 8 0.03 x y
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

(def pelican-pattern
  '(let [sky      (p-color 220 240 255)
         dark     (p-color  25  30  35)
         tire     (p-color  20  22  26)
         rimc     (p-color 185 190 200)
         framec   (p-color 220  60  60)
         beakc    (p-color 255 180  40)
         bodyc    (p-color 240 240 235)
         wingc    (p-color 225 225 220)
         footc    (p-color 255 170   0)
         seatc    (p-color  40  40  50)
         barc     (p-color  40  45  60)
         eyew     (p-color 255 255 255)
         pupilc   (p-color  10  10  10)

         ;; Helpers that return patterns
         circle (fn [n r s] [(->SShape s (clock-points n r))])
         centered-rod (fn [L t s] (rect (- (/ L 2)) (- (/ t 2)) L t s))

         wheel (fn [cx cy r]
                 (let [tire   (circle 48 r             {:fill tire})
                       rim    (circle 32 (* 0.7 r)     {:fill rimc})
                       spokes (clock-rotate 8 (centered-rod (* 0.4 r) 0.01 {:stroke rimc :stroke-weight 0.01}))]
                   (stack tire rim spokes)))

         bike-frame (fn [rear front wheel-r]
                      (let [base-x (first rear)
                            base-y (second rear)
                            front-x (first front)
                            front-y (second front)
                            seat-x (+ base-x (* 0.1 wheel-r))
                            seat-y (- base-y (* 1.5 wheel-r))
                            bar-x (+ front-x (* 0.1 wheel-r))
                            bar-y (- front-y (* 1.2 wheel-r))
                            seat (rect (- seat-x (* 0.15 wheel-r)) (- seat-y (* 0.1 wheel-r)) (* 0.3 wheel-r) (* 0.2 wheel-r) {:fill seatc})
                            bar (centered-rod (distance [seat-x seat-y] [bar-x bar-y]) (* 0.05 wheel-r) {:fill barc})
                            bar-rot (atan2 (- bar-y seat-y) (- bar-x seat-x))
                            bar-rotated (rotate bar-rot bar)
                            bar-positioned (translate (- bar-x seat-x) (- bar-y seat-y) bar-rotated)]
                        (stack seat bar-positioned)))

         pelican (fn [base-x base-y wheel-r]
                   (let [body0 (rect -0.15 -0.4 0.3 0.8 {:fill bodyc
                                                         :stroke (p-color 210 210 210)
                                                         :stroke-weight 0.01})
                         body1 (stretch 1.7 1.2 body0)
                         body (translate (+ base-x (* 0.1 wheel-r)) (- base-y (* 1.5 wheel-r)) body1)

                         wing0 [(->SShape {:fill wingc}
                                         [[0.0 0.0] [0.6 0.15] [0.9 0.0] [0.6 -0.25] [0.15 -0.35] [-0.2 -0.2]])]
                         wing1 (scale (* 1.2 wheel-r) wing0)
                         wing2 (rotate -0.2 wing1)
                         wing (translate (+ base-x (* 0.15 wheel-r)) (- base-y (* 1.55 wheel-r)) wing2)

                         neck0 (rect -0.03 -0.25 0.06 0.5 {:fill bodyc})
                         neck1 (stretch 1.2 1.5 neck0)
                         neck2 (rotate 0.3 neck1)
                         neck (translate (+ base-x (* 0.25 wheel-r)) (- base-y (* 1.8 wheel-r)) neck2)

                         head0 (rect -0.08 -0.15 0.16 0.3 {:fill bodyc})
                         head1 (stretch 1.3 1.1 head0)
                         head2 (rotate 0.2 head1)
                         head (translate (+ base-x (* 0.35 wheel-r)) (- base-y (* 2.0 wheel-r)) head2)

                         beakU0 [(->SShape {:fill beakc}
                                           [[0 0] [0.6 0.08] [0.95 0.02] [0.6 -0.02]])]
                         beakL0 [(->SShape {:fill (p-color 240 160 40)}
                                           [[0 0] [0.58 -0.05] [0.95 -0.09] [0.58 -0.08]])]
                         beakU1 (scale (* 0.9 wheel-r) beakU0)
                         beakL1 (scale (* 0.9 wheel-r) beakL0)
                         beakU2 (rotate 0.1 beakU1)
                         beakL2 (rotate 0.1 beakL1)
                         beakU (translate (+ base-x (* 0.45 wheel-r)) (- base-y (* 1.95 wheel-r)) beakU2)
                         beakL (translate (+ base-x (* 0.45 wheel-r)) (- base-y (* 1.95 wheel-r)) beakL2)

                         eye0 (rect -0.02 -0.02 0.04 0.04 {:fill eyew})
                         eye1 (translate (+ base-x (* 0.32 wheel-r)) (- base-y (* 2.05 wheel-r)) eye0)

                         pupil0 (rect -0.01 -0.01 0.02 0.02 {:fill pupilc})
                         pupil1 (translate (+ base-x (* 0.32 wheel-r)) (- base-y (* 2.05 wheel-r)) pupil0)

                         foot0 [(->SShape {:fill footc} [[-0.05 0.0] [0.08 0.03] [0.1 -0.03]])]
                         foot1 (scale (* 0.8 wheel-r) foot0)
                         foot2 (rotate -0.1 foot1)
                         foot3 (translate (+ base-x (* 0.05 wheel-r)) (- base-y (* 0.8 wheel-r)) foot2)
                         foot4 (translate (+ base-x (* 0.15 wheel-r)) (- base-y (* 0.8 wheel-r)) foot2)]

                     (stack body wing neck head beakU beakL eye1 pupil1 foot3 foot4)))

         pelican-riding-bike (fn []
                               (let [wheel-r 0.25
                                     rear    [-0.5 -0.2]
                                     front   [ 0.2 -0.2]
                                     bg      (rect -1.05 -1.05 2.1 2.1 {:fill sky})
                                     ground  (rect -1.2 -0.47 2.4 0.02 {:fill dark})
                                     w1      (wheel (first rear)  (second rear)  wheel-r)
                                     w2      (wheel (first front) (second front) wheel-r)
                                     frame   (bike-frame rear front wheel-r)
                                     bird    (pelican (first rear) (second rear) wheel-r)]
                                 ;; z-order: later args drawn on top
                                 (stack bg ground w1 w2 frame bird)))]
    (-> (pelican-riding-bike) reframe)))

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
    ["p11 Pelican on a Bicycle" pelican-pattern]
    ])  )

