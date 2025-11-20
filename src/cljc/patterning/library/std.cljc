(ns patterning.library.std
  (:require [patterning.maths :as maths
             :refer [default-random get-time]]
            [patterning.sshapes :refer [rotate-shape close-shape ->SShape set-color tie-together]]
            [patterning.sshapes :as sshapes]
            [patterning.groups :refer [APattern]]
            [patterning.layouts :refer [stack four-mirror clock-rotate]]
            [patterning.macros :refer [optional-styled-primitive]]))

;;; Some basic sshapes

(def rect (optional-styled-primitive [x y w h]
                                     (let [x2 (+ x w) y2 (+ y h)]
                                       [[x y] [x2 y] [x2 y2] [x y2] [x y]])))

(defn centered-rect
  [cx cy w h style]
  (let [w2 (/ w 2)
        h2 (/ h 2)]
    (rect (- cx w2) (- cy h2) w h style)))

(def square (optional-styled-primitive [] [[-1 -1] [-1 1] [1 1] [1 -1] [-1 -1]]))



(defn poly
  ([n radius cx cy style]
   (let [make-point (fn [a] (maths/add-points [cx cy] (maths/pol-to-rec [radius a])))]
     (APattern (->SShape style (close-shape (into [] (map make-point (maths/clock-angles n))))))))
  ([n radius cx cy] (poly n radius cx cy {}))
  ([n radius style] (poly n radius 0 0 style))
  ([n radius] (poly n radius 0 0 {})))


(def multiline (optional-styled-primitive [ps] ps))

(defn star
  ([n rads cx cy style]
   ;; Double n internally so that n represents the number of outer points
   ;; e.g., n=5 creates a 5-pointed star with 10 total points (5 outer, 5 inner)
   (let [total-points (* 2 n)]
     (APattern (->SShape style (close-shape (sshapes/translate-shape
                                              cx cy
                                              (map maths/pol-to-rec (map vector (cycle rads) (maths/clock-angles total-points)))))))))
  ([n rads cx cy] (star n rads cx cy {}))
  ([n rads style] (star n rads 0 0 style))
  ([n rads] (star n rads 0 0 {})))

(defn find-cycle
  "Find a single cycle starting from start-idx, returning [cycle-points new-visited]"
  [point-vec step start-idx visited]
  (loop [current-idx start-idx
         cycle-points []
         visited-set visited]
    (let [point (nth point-vec current-idx)
          new-cycle-points (conj cycle-points point)
          new-visited (conj visited-set current-idx)
          next-idx (mod (+ current-idx step) (count point-vec))]
      (if (= next-idx start-idx)
        [new-cycle-points new-visited]
        (recur next-idx new-cycle-points new-visited)))))

(defn find-all-cycles
  "Find all distinct cycles when stepping through points by step size"
  [points step]
  (let [point-vec (vec points)
        n (count point-vec)]
    (loop [start-idx 0
           visited #{}
           all-cycles []]
      (if (>= start-idx n)
        all-cycles
        (if (contains? visited start-idx)
          (recur (inc start-idx) visited all-cycles)
          (let [[cycle-points new-visited] (find-cycle point-vec step start-idx visited)]
            (if (>= (count cycle-points) 3)
              (recur (inc start-idx) new-visited (conj all-cycles cycle-points))
              (recur (inc start-idx) new-visited all-cycles))))))))

(defn nangle
  ([n rad cx cy style]
   (let [step (if (even? n)
                (- (int (/ n 2)) 1)  ; For even n, use n/2 - 1 to avoid degenerate case
                (int (/ n 2)))       ; For odd n, use n/2 as before
         all-points (maths/clock-points n rad)
         cycles (find-all-cycles all-points step)]
     (if (empty? cycles)
       ;; Fallback: if no cycles found, return empty pattern
       (APattern)
       (let [translated-cycles (map (fn [cycle-points]
                                     (sshapes/translate-shape cx cy cycle-points))
                                   cycles)
             sshapes (map (fn [translated-points]
                           (->SShape style (close-shape translated-points)))
                         translated-cycles)]
         (apply APattern sshapes)))))
  ([n rad cx cy] (nangle n rad cx cy {}))
  ([n rad style] (nangle n rad 0 0 style))
  ([n rad] (nangle n rad 0 0 {})))

(defn random-rect [style & {:keys [random] :or {random default-random}}]
  (let [rr (fn [l] (.randomFloat random))
        m1 (fn [x] (- x 1))]
    (rect (m1 (rr 1)) (m1 (rr 1)) (rr 1) (rr 1) style)))

(def horizontal-line (optional-styled-primitive [y] [[-1 y] [1 y] [-1 y] [1 y]]))
(def vertical-line (optional-styled-primitive [x] [[x -1] [x 1] [x -1] [x 1]]))

(defn drunk-line-internal [steps stepsize random angle-range]
  (let [angle-changes (take steps (maths/random-angles angle-range random))
        cumulative-angles (reductions + 0 angle-changes)
        offs (map (fn [a] [stepsize a]) cumulative-angles)]
    (loop [pps offs current [0 0] acc []]
      (if (empty? pps) acc
          (let [p (maths/add-points current (maths/pol-to-rec (first pps)))]
            (recur (rest pps) p (conj acc p)))))))

(defn drunk-line [steps stepsize & args]
  (let [style (when (and (not (empty? args)) (map? (first args))) (first args))
        rest-args (if style (rest args) args)
        {:keys [random angle-range] :or {random default-random angle-range 0.3}}
        (when (even? (count rest-args))
          (apply hash-map rest-args))]
    (APattern (->SShape (or style {})
                        (drunk-line-internal steps stepsize random angle-range)))))

(def h-sin (optional-styled-primitive [] (into [] (map (fn [a] [a (maths/sin (* maths/PI a))]) (range (- 1) 1 0.05)))))

(def diamond (optional-styled-primitive [] (close-shape [[-1 0] [0 -1] [1 0] [0 1]])))

(def quarter-ogee (optional-styled-primitive [resolution stretch]
                                             (let [ogee (fn [x] (/ x (maths/sqrt (+ 1 (* x x)))))
                                                   points (into []
                                                                (map (fn [x] [x (ogee (* stretch x))])
                                                                     (range (- 1) 1.0001 resolution)))]
                                               (rotate-shape (/ maths/PI 2) points))))


(defn spiral-points [r a dr da]
   (map maths/pol-to-rec
        (map vector
             (iterate (partial + dr) r)
             (iterate (partial + da) a))))

(defn spiral
  "Creates a spiral shape.
   nopoints: number of points per full cycle (like poly, nangle, star)
   dr-per-cycle: how much the radius increases over one full cycle
   num-cycles: number of full cycles to draw
   
   The parameters are independent:
   - Resolution: nopoints controls how smooth the spiral is
   - Tightness: dr-per-cycle controls how tightly the spiral coils (independent of resolution)
   - Length: num-cycles controls how many full rotations (independent of resolution)
   
   Supports multiple arities:
   (spiral nopoints dr-per-cycle num-cycles) - starts at r=0, a=0, default style
   (spiral nopoints dr-per-cycle num-cycles style) - starts at r=0, a=0
   (spiral nopoints dr-per-cycle num-cycles r a) - explicit start position, default style
   (spiral nopoints dr-per-cycle num-cycles r a style) - all parameters"
  ([nopoints dr-per-cycle num-cycles] (spiral nopoints dr-per-cycle num-cycles 0 0 {}))
  ([nopoints dr-per-cycle num-cycles style] (spiral nopoints dr-per-cycle num-cycles 0 0 style))
  ([nopoints dr-per-cycle num-cycles r a] (spiral nopoints dr-per-cycle num-cycles r a {}))
  ([nopoints dr-per-cycle num-cycles r a style]
   (let [da (/ maths/TwoPI nopoints)           ; angle increment per step
         dr (/ dr-per-cycle nopoints)            ; radius increment per step
         n (* num-cycles nopoints)]              ; total number of points
     (APattern (->SShape style (take n (spiral-points r a dr da)))))))




;; Complex patterns made as patterns (these have several disjoint sshapes)

(defn cross "A cross, can only be made as a pattern (because sshapes are continuous lines) which is why we only define it now"
  [color x y] (stack (horizontal-line y {:stroke color})  (vertical-line x {:stroke color})))


(defn ogee "An ogee shape" [resolution stretch style]
  (let [o-group (into [] (four-mirror (quarter-ogee resolution stretch style)))
        o0 (get (get o-group 0) :points)
        o1 (get (get o-group 1) :points)
        o2 (get (get o-group 2) :points)
        o3 (get (get o-group 3) :points)
        top (tie-together o0 o1)
        bottom (tie-together o2 o3)]
    (APattern (->SShape style (tie-together top bottom)))))


(defn bez-curve
  ([points style] (APattern (sshapes/s-bez-curve style points)))
  ([points] (bez-curve points {})))


;; Others
(defn on-background [color pattern]
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
   (poly 50 0.65 0 0 style)  ; Circular face
   (clock-rotate 12 [(->SShape style [[0.5 0] [0.6 0]])])  ; 12 hour markers
   ))

(defn clock [time-map style]
  (stack
   (clock-face style)
   (clock-hands time-map)))
