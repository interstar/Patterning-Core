(ns patterning.library.std
  (:require [patterning.maths :as maths
             :refer [default-random get-time]]
            [patterning.sshapes :refer [rotate-shape close-shape ->SShape set-color tie-together ]]
            [patterning.sshapes :as sshapes]
            [patterning.groups :refer [APattern]]
            [patterning.layouts :refer [stack four-mirror clock-rotate]]
            [patterning.macros :refer [optional-styled-primitive]]))

;;; Some basic sshapes

(def rect (optional-styled-primitive [x y w h]
                                     (let [x2 (+ x w) y2 (+ y h)]
                                       [[x y] [x2 y] [x2 y2] [x y2] [x y]] ) ))

(defn centered-rect
  [cx cy w h style]
  (let [w2 (/ w 2)
        h2 (/ h 2)]
    (rect (- cx w2) (- cy h2) w h style))
  )

(def square (optional-styled-primitive [] [[-1 -1] [-1 1] [1 1] [1 -1] [-1 -1]]  ))

(def poly (optional-styled-primitive [cx cy radius no-sides]
             (let [ make-point (fn [a] (maths/add-points [cx cy] (maths/pol-to-rec [radius a])))]
                (close-shape (into [] (map make-point (maths/clock-angles no-sides))))  )
             ))



(def multiline (optional-styled-primitive [ps] ps))

(def star (optional-styled-primitive
           [cx cy rads n]
           (close-shape (sshapes/translate-shape
                         cx cy
                         (map maths/pol-to-rec (map vector (cycle rads) (maths/clock-angles n)) ))) ))

(def nangle (optional-styled-primitive
             [cx cy rad n]
             (let [dropped (maths/take-every (int (/ n 2)) (cycle (maths/clock-points n rad)))
                   finite (maths/map-until-repeat (fn [x] x) maths/molp= dropped) ]
               (close-shape (sshapes/translate-shape
                             cx cy finite)))))

(defn random-rect [style & {:keys [random] :or {random default-random}}]
  (let [rr (fn [l] (.randomFloat random))
        m1 (fn [x] (- x 1))]
    (rect (m1 (rr 1)) (m1 (rr 1)) (rr 1) (rr 1) style)))

(def horizontal-line (optional-styled-primitive [y] [[-1 y] [1 y] [-1 y] [1 y]] ))
(def vertical-line (optional-styled-primitive [x] [[x -1] [x 1] [x -1] [x 1]]))

(defn drunk-line-internal [steps stepsize random]
  (let [offs (map (fn [a] [stepsize a]) 
                (take steps (iterate #(.randomAngle random %) 0))) ]
    (loop [pps offs current [0 0] acc []]
      (if (empty? pps) acc
          (let [p (maths/add-points current (maths/pol-to-rec (first pps))) ]
            (recur (rest pps) p (conj acc p))  )) )  ))

(defn drunk-line [steps stepsize & args]
  (let [[style & rest-args] args
        {:keys [random] :or {random default-random}} (apply hash-map rest-args)]
    (APattern (->SShape (or style {}) 
                        (drunk-line-internal steps stepsize random)))))

(def h-sin (optional-styled-primitive [] (into [] (map (fn [a] [a (maths/sin (* maths/PI a))]  ) (range (- 1) 1 0.05)) ) ))

(def diamond (optional-styled-primitive [] (close-shape [[-1 0] [0 -1] [1 0] [0 1]] )))

(def quarter-ogee (optional-styled-primitive [resolution stretch]
                                     (let [ogee (fn [x] (/ x (maths/sqrt (+ 1 (* x x)))))
                                           points (into []
                                                        (map (fn [x] [x (ogee (* stretch x))])
                                                             (range (- 1) 1.0001 resolution) ) )]
                                       (rotate-shape (/ maths/PI 2) points)   ) ))


(defn spiral-points [a da r dr]
  (map maths/pol-to-rec
       (map vector
            (iterate (partial + da) a)
            (iterate (partial + dr) r))))

(def spiral (optional-styled-primitive [n a da r dr]
                                       (take n (spiral-points a da r dr))))




;; Complex patterns made as patterns (these have several disjoint sshapes)

(defn cross "A cross, can only be made as a pattern (because sshapes are continuous lines) which is why we only define it now"
  [color x y] (stack (horizontal-line y {:stroke color})  (vertical-line x {:stroke color}))  )


(defn ogee "An ogee shape" [resolution stretch style]
  (let [o-group (into [] (four-mirror (quarter-ogee resolution stretch style)))
        o0 (get (get o-group 0) :points)
        o1 (get (get o-group 1) :points)
        o2 (get (get o-group 2) :points)
        o3 (get (get o-group 3) :points)
        top (tie-together o0 o1)
        bottom (tie-together o2 o3) ]
    (APattern (->SShape style ( tie-together top bottom))) )  )


(defn bez-curve
  ([points style] (APattern (sshapes/s-bez-curve style points))  )
  ([points] (bez-curve points {})) )


;; Others
(defn background [color pattern]
  (stack (square {:fill color}) pattern))



;; Clock-related functions
(defn clock-hands [time-map]
  (let [{:keys [hours minutes]} time-map
        ;; Convert to 12-hour format and get decimal hours for smoother movement
        hour-angle (* (/ (+ (mod hours 12) (/ minutes 60)) 12) maths/TwoPI)
        minute-angle (* (/ minutes 60) maths/TwoPI)
        ;; Hour hand is shorter (0.35) than minute hand (0.4)
        hour-hand [(+ (* 0.35 (maths/sin hour-angle)))
                  (- (* 0.35 (maths/cos hour-angle)))]
        minute-hand [(+ (* 0.4 (maths/sin minute-angle)))
                    (- (* 0.4 (maths/cos minute-angle)))]]
    [(->SShape {} [hour-hand [0 0] minute-hand])]))

(defn clock-face [style]
  (stack
   (poly 0 0 0.65 50 style)  ; Circular face
   (clock-rotate 12 [(->SShape style [[0.5 0] [0.6 0]])])  ; 12 hour markers
   ))

(defn clock [time-map style]
  (stack
   (clock-face style)
   (clock-hands time-map)))
