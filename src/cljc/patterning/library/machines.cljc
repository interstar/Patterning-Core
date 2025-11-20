(ns patterning.library.machines
  (:require
   [patterning.maths :refer [PI sin cos distance] ]
   [patterning.library.std :refer [poly rect centered-rect]]
   [patterning.layouts :refer [stack]]
   [patterning.groups :refer [APattern]]
   [patterning.color :refer [p-color]]
   [patterning.sshapes :refer [->SShape close-shape  translate]]
   )
  #?(:clj (:require [patterning.macros :refer [optional-styled-primitive]])
     :cljs (:require-macros [patterning.macros :refer [optional-styled-primitive]] )
     )
  )

(defn tap [x]
  (println x)
  x
  )


(defn centre-range [lo hi inner-proportion]
  "Takes a larger range between lo and hi
   and a proportion (eg. 0.2)
   and then calculates the lo and hi of a range
   that is the inner-proportion of the larger range
   centred within it

   Eg.
   let's say that the outer range is 0 to 10
   and inner-proportion is 0.2
   That means the inner range has a size of 2
   But we will centre it within the larger
   So the result will be 4 to 6
   or [4 6]
  "
  (let [outer-len (- hi lo)
        inner (* outer-len inner-proportion)
        margin (/ (- outer-len inner) 2)]
    [(+ lo margin) (+ lo margin inner) ]
    ))

(def PI2 (* 2 PI))

(defn circumference [radius] (* 2 PI radius))


(defprotocol IWheel
  "It's basically a circle"
  (cx [this] "Centre X")
  (cy [this] "Centry Y")
  (dist-to-point [this [x y]] "Distance from centre to point x,y")
  (distance-to [this other-wheel] "Distance between the centres of the two wheels")
  (hit? [this [x y]] "Does point x y fall inside the circle?" )
  )


(defrecord BasicWheel [cx cy r]
  IWheel
  (dist-to-point [this [x y]] (distance [cx cy] [x y]))
  (distance-to [this other-wheel]
    (.dist-to-point this [(.cx other-wheel) (.cy other-wheel)]))
  (hit? [this [x y]] (< (.dist-to-point this [x y]) r))

  )

(defn translate-basic-wheel [cog dx dy]
  (->BasicWheel (+ (:cx cog) dx) (+ (:cy cog) dy) (:r cog) ))

(defprotocol ICog
  "A wheel with teeth"
  (engages? [this other-cog] "Is this and the other cog at the right distance to engage without crashing?")
  )

(defprotocol ICrashable
  "Two things that collide"
  (crash? [this other] "Do this and the other intersect?"))





(defrecord Cog [basic-wheel ;; we re-use the basic wheel as a wheelish component

                no-teeth ; number of teeth there are
                circular-pitch ; distance between teeth (circumf / no-teeth)
                tooth-angle ; angle between one tooth and next (2PI / no-teeth)

                pitch-radius ; radius of the "pitch" or point of contact of teeth
                addendum ; height of tooth above pitch
                addendum-radius ; addendum-radius is pitch+addendum radius of outer circle
                dedendum ; hight of foot of tooth below pitch
                dedendum-radius ; dedendum-radius is pitch-dedendum
                clearance
                clearance-radius
                tooth-thickness
                top-thickness
                hole-type
                hole-radius
                no-hollows
                hollow-radius
               ]

  )

(defn make-cog [cx cy no-teeth pitch-radius addendum dedendum clearance
                tooth-thickness top-thickness hole-type hole-radius
                no-hollows hollow-radius]
  (let [
        addendum-radius  (+ pitch-radius addendum)
        dedendum-radius  (- pitch-radius dedendum)
        clearance-radius (-> pitch-radius (#(- % dedendum)) (#(+ % clearance)))

        circ (circumference pitch-radius)
        tooth-angle (/ PI2 no-teeth)
        circular-pitch (/ circ no-teeth)

        basic-wheel (->BasicWheel cx cy addendum-radius)
        ]
    (->Cog basic-wheel no-teeth circular-pitch tooth-angle
           pitch-radius
           addendum addendum-radius
           dedendum dedendum-radius clearance clearance-radius
           tooth-thickness top-thickness hole-type hole-radius
           no-hollows hollow-radius
           )
    ))



(defn spawn-connected [{:keys [no-teeth tooth-thickness top-thickness
                               addendum dedendum clearance circular-pitch] :as cog}
                       a-ratio hole-type hole-radius
                       no-hollows hollow-radius]
  "Makes a new cog-wheel to fit the first argument (in terms of addendum / dedendum / circular-pith etc)
+ a-ratio is the teeth (or speed) of the new wheel relative to this wheel.
a-ratio should be a Clojure ratio"
  (let [
        no-teeth2 (int (* no-teeth a-ratio))
        circ2 (* circular-pitch no-teeth2)
        pitch-radius2 (/ circ2 PI2)
        ]
    (make-cog 0 0 no-teeth2 pitch-radius2 addendum dedendum clearance
              tooth-thickness top-thickness hole-type hole-radius
              no-hollows hollow-radius)
    )
  )


(defn translate-cog [cog dx dy]
  (conj cog {:basic-wheel (translate-basic-wheel (:basic-wheel cog) dx dy)})

  )

(defrecord CogTooth [cog-center start-angle da
                     start foot-start mid-start top-start
                     foot-end mid-end top-end end
                     ])


(defn make-cog-tooth [{:keys [basic-wheel pitch-radius addendum-radius
                              dedendum-radius
                              tooth-thickness ; in terms of angle
                              top-thickness ; in terms of angle
                              tooth-angle
                              ] :as cog-wheel}
                      a]

  (let [cx (:cx basic-wheel)
        cy (:cy basic-wheel)
        start [(+ cx (* (cos a) dedendum-radius))
               (+ cy (* (sin a) dedendum-radius))]

        footA (centre-range a (+ a tooth-angle) tooth-thickness)

        foot-start [(+ cx (* (cos (first footA)) dedendum-radius))
                    (+ cy (* (sin (first footA)) dedendum-radius))]

        foot-end [(+ cx (* (cos (second footA)) dedendum-radius))
                  (+ cy (* (sin (second footA)) dedendum-radius))]


        midA (centre-range a (+ a tooth-angle) tooth-thickness)

        mid-start [(+ cx (* (cos (first midA)) pitch-radius))
                   (+ cy (* (sin (first midA)) pitch-radius))]

        mid-end [(+ cx (* (cos (second midA)) pitch-radius))
                 (+ cy (* (sin (second midA)) pitch-radius))]


        topA (centre-range a (+ a tooth-angle) top-thickness)

        top-start [(+ cx (* (cos (first topA)) addendum-radius))
                   (+ cy (* (sin (first topA)) addendum-radius))]
        top-end [(+ cx (* (cos (second topA)) addendum-radius))
                 (+ cy (* (sin (second topA)) addendum-radius))]


        end [(+ cx (* (cos (+ a tooth-angle)) dedendum-radius))
             (+ cy (* (sin (+ a tooth-angle)) dedendum-radius))]

        ]
    (->CogTooth [cx cy] a tooth-angle
                start foot-start mid-start top-start
                foot-end mid-end top-end end
                ))
  )

(defn cog-tooth->points [{:keys [start foot-start mid-start top-start top-end mid-end foot-end end]}]
  [start  foot-start mid-start top-start top-end mid-end foot-end end
   ]
  )

(defn crude-circle
  ([cx cy r s]
   (poly 80 r cx cy s))
  ([r s] (crude-circle 0 0 r s)))

(def multiline
  (optional-styled-primitive
   [ps]
   ps)
)


(def edge-style {:stroke-weight 1 :stroke (p-color 0 0 0) :type :edge})
(def spindle-style {:stroke-weight 1 :stroke (p-color 0 0 100) :type :spindle})
(def hollow-style {:stroke-weight 1 :stroke (p-color 0 100 0) :type :hollow})
(def guide-style {:stroke-weight 1 :stroke (p-color 30 30 100 50) :type :guide})


(defn cog->pattern [{:keys
                     [addendum-radius dedendum-radius pitch-radius tooth-angle
                      basic-wheel hole-type hole-radius no-hollows hollow-radius]
                     :as cog}
                    guides?]
  (let [cx (:cx basic-wheel)
        cy (:cy basic-wheel)
        
        angles (take-while #(< % (* 2 PI)) (iterate #(+ % tooth-angle ) 0))
        teeth (map
               #(make-cog-tooth cog %)
               angles
               )
        teeth-points (map cog-tooth->points teeth)        
        hollow-angle (/ PI2 no-hollows)
        hollow-angles (take-while #(< % PI2) (iterate #(+ % hollow-angle) 0))
        hrr (centre-range hole-radius dedendum-radius hollow-radius ) ; hollow radius range
        hollow-circular-range #(centre-range % (+ % hollow-angle) 0.7 )
        hollow-ranges (map hollow-circular-range hollow-angles)
        hollows (map
                   (fn [[a1 a2]]
                     (let [da (/ (- a2 a1) 15)]
                       (close-shape
                        (concat
                         [[(+ cx (* (first hrr) (cos a1)) )
                           (+ cy (* (first hrr) (sin a1)))]
                          [(+ cx (* (second hrr) (cos a1)))
                           (+ cy (* (second hrr) (sin a1)))]]
                         (map (fn [a] [(+ cx (* (cos a) (second hrr)))
                                       (+ cy (* (sin a) (second hrr)))
                                       ])
                              (range a1 a2 da))
                         [[(+ cx (* (second hrr) (cos a2)))
                           (+ cy (* (second hrr) (sin a2)))]
                          [(+ cx (* (first hrr) (cos a2)))
                           (+ cy (* (first hrr) (sin a2)))]]
                         (map (fn [a] [(+ cx (* (cos a) (first hrr)))
                                       (+ cy (* (sin a) (first hrr)))
                                       ])
                              (range a2 a1 (- 0 da)))

                         ))))
                   hollow-ranges)
 
        ]
    (stack

     (if guides?
       (stack
        (crude-circle cx cy pitch-radius guide-style)
        (crude-circle cx cy addendum-radius guide-style)
        (crude-circle cx cy dedendum-radius guide-style))
       [])
     (if (= :round hole-type)
       (crude-circle cx cy hole-radius spindle-style)
       (centered-rect cx cy (* hole-radius 2) (* hole-radius 2) spindle-style)
       )

     ;; teeth
     (multiline (apply concat teeth-points) edge-style)

     ;; hollows
     (apply stack (map #(multiline % hollow-style) hollows) )

     ))
  )



(defn spaced-engaged
  ([cog ratio] (spaced-engaged cog ratio (:hole-type cog) (:hole-radius cog)
                               (:no-hollows cog) (:hollow-radius cog)))
  ([cog ratio hole-type hole-radius no-hollows hollow-radius]
   (->
    (spawn-connected cog ratio hole-type hole-radius no-hollows hollow-radius )
    (#(translate-cog % (+ (:addendum-radius cog) (:addendum-radius %)) 0 ))
    )
   ))
