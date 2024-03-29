(ns patterning.maths
  (:require [clojure.spec.alpha :as s]

            ))

;; My maths library (to factor out all the maths functions that will
;; need to be different in Clojure / ClojureScript cljx

(def PI #?(:clj (Math/PI) :cljs js/Math.PI) )
(def TwoPI (* PI 2))
(def half-PI (float (/ PI 2)))
(def q-PI (float (/ PI 4)))

(defn sqrt [x] #?(:clj (Math/sqrt x) :cljs (js/Math.sqrt x)) )
(defn abs [n] (max n (- n)))
(defn atan2 [x y] #?(:clj (Math/atan2 x y) :cljs (js/Math.atan2 x y) ) )
(defn cos [a] #?(:clj (Math/cos a) :cljs (js/Math.cos a)) )
(defn sin [a] #?(:clj (Math/sin a) :cljs (js/Math.sin a)) )


(defn clock-angles [number]
  (let [da (float(/ TwoPI number))]
    (take number (iterate #(+ da %) (- half-PI)))) )


(defn rec-to-pol [[x y]] [(sqrt (+ (* x x) (* y y))) (atan2  x y)])
(defn pol-to-rec [[r a]] [(* r (cos a)) (* r (sin a))])


(defn clock-angles-as-point-spiral [n]
  (map pol-to-rec (map vector (iterate #(+ % 0.05) 0.05) (clock-angles n) )))

(defn tx
  "transform a scalar from one space to another. o1 is origin min, o2 is origin max, t1 is target min, t2 is target max"
  [o1 o2 t1 t2 x]
  (+ (* (float (/ (- x o1) (- o2 o1))) (- t2 t1)) t1) )


(defn mol= "more or less equal" [x y] (< (abs (- x y)) 0.000001) )
(defn molp= "more or less equal points" [[x1 y1] [x2 y2]] (and (mol= x1 x2) (mol= y1 y2)))
(defn mol=v "more or less equal vectors" [xs ys] (every? (fn [[x y]] (mol= x y) ) (map vector xs ys) ) )



;;(defn mol=s "more or less equal vectors" [v1 v2] (and (mol= (first v1) (first v2)) (molv= (rest v1) (rest v2))))

;; Point geometry

(defn f-eq "floating point equality" [a b]  (<= (abs (- a b)) 0.00001))
(defn p-eq "point equality" [[x1 y1] [x2 y2]] (and (f-eq x1 x2) (f-eq y1 y2)))

(defn add-points [[x1 y1] [x2 y2]] [(+ x1 x2) (+ y1 y2)])
(defn diff [[x1 y1] [x2 y2]] (let [dx (- x2 x1) dy (- y2 y1)]  [dx dy]) )
(defn magnitude [[dx dy]] (sqrt (+ (* dx dx) (* dy dy) )))
(defn distance [p1 p2] ( (comp magnitude diff) p1 p2) )
(defn unit [[dx dy]] (let [m (magnitude [dx dy])] [(float (/ dx m)) (float (/ dy m))]) )

(defn rev [[dx dy]] [(- dx) (- dy)])

(defn line-to-segments [points]
  (if (empty? points) []
      (loop [p (first points) ps (rest points) acc []]
        (if (empty? ps) acc
            (recur (first ps) (rest ps) (conj acc [p (first ps)]) )  )
        )))

(defn rotate-point [a [x y]]
  (let [cos-a (cos a) sin-a (sin a)]
    [(- (* x cos-a) (* y sin-a))
     (+ (* x sin-a) (* y cos-a))]))


(defn wobble-point "add some noise to a point, qx and qy are the x and y ranges of noise"
  [[qx qy]  [x y]]
  (let [wob (fn [n qn] (+ n (- (rand qn) (/ qn 2))))]
     [(wob x qx) (wob y qy)]  ) )

(defn x-in-list [x my= xs]
  (if (empty? xs) false
      (reduce (fn [a b] (or a b)) (map (fn [y] (my= y x)) xs) )))

(defn point-in-list [p ps] (x-in-list p molp= ps))

(defn clock-points [n r]
  (let [ make-point (fn [a] (pol-to-rec [r a]))]
    (into [] (map make-point (clock-angles n)))  ))


;; Triangle geometry

(s/def ::point (s/coll-of number? :kind vector? :count 2))
(s/def ::Triangle (s/keys :req-un [::A ::B ::C ::a ::b ::c ::ax ::ay ::bx ::by ::cx ::cy]))

(defn triangle
  ([[ax ay] [bx by] [cx cy]] (triangle ax ay bx by cx cy))
  ([ax ay bx by cx cy]
   {:A [ax ay] :B [bx by] :C [cx cy]
    :a [[bx by] [cx cy]] :b [[ax ay] [cx cy]] :c [[ax ay] [bx by]]
    :ax ax :ay ay :bx bx :by by :cx cx :cy cy}))

(s/fdef triangle
        :args (s/alt :pairs (s/cat :A ::point :B ::point :C ::point)
                     :scalars (s/cat :ax number? :ay number?
                                     :bx number? :by number?
                                     :cx number? :cy number?))
        :ret ::Triangle)



(defn perimeter [t] (+ (apply distance (:a t)) (apply distance (:b t)) (apply distance (:c t))))

(defn area [t]
  (let [ axby (* (:ax t) (:by t)) bxcy (* (:bx t) (:cy t)) cxay (* (:cx t) (:ay t))
         axcy (* (:ax t) (:cy t)) cxby (* (:cx t) (:by t)) bxay (* (:bx t) (:ay t)) ]
    (/ (abs (- (- (- (+ axby bxcy cxay) axcy) cxby) bxay)) 2 )  ))

(defn contains-point [t [x y]]
  (let [pab (triangle x y (:ax t) (:ay t) (:bx t) (:by t))
        pbc (triangle x y (:bx t) (:by t) (:cx t) (:cy t))
        pac (triangle x y (:ax t) (:ay t) (:cx t) (:cy t))        ]
    (mol= (+ (area pab) (area pbc) (area pac)) (area t))
    ))

(s/fdef contains-point :args (s/cat :t ::Triangle :p ::point) :ret boolean?)


(defn triangle-points [t] [(:A t) (:B t) (:C t)])



(defn tri= [t1 t2]
  (= (set (triangle-points t1)) (set (triangle-points t2))))

;; Useful functions
(defn drop-every [n xs] (lazy-seq (if (seq xs) (concat (take (dec n) xs) (drop-every n (drop n xs))))))

(defn take-every [n xs] (lazy-seq (if (seq xs) (concat (take 1 xs) (take-every n (drop n xs))) )))


(defn map-until-repeat [f eq-test ins]
  (loop [xs ins build []]
    (if (empty? xs) build
        (let [fx (f (first xs))]
          (if (x-in-list fx eq-test build) build
              (recur (rest xs) (conj build fx)))))))
