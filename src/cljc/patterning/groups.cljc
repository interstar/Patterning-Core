(ns patterning.groups
  (:require [patterning.maths :as maths]
            [patterning.sshapes :as sshapes]
            [patterning.color :refer [p-color]]
            [clojure.spec.alpha :as s]
            [orchestra.spec.test :as stest]

            [clojure.set :refer [union]]))


;; A Pattern is nothing but a sequence of SShapes
;;
;; Patterns can represent ordinary patterns that require several sshapes
;; (because they have disjoint geometric forms, or multiple colors
;; etc.
;; Patterns are also the flattened results of combining multiple groups
;; together, eg. when running them through a layout.


;;; Making patterns
(defn APattern "a vector of sshapes" [& sshapes] (lazy-seq sshapes) )


(defn empty-pattern [] [])


(defn triangle-list-to-pattern [trs]
  (map #(sshapes/->SShape {:fill (p-color 100 100 100 100)
                           :stroke (p-color 0)}
                          (maths/triangle-points %)) trs ))


(s/fdef triangle-list-to-pattern
        :args (s/cat :trs (s/coll-of  ::maths/Triangle))
        :ret ::sshapes/Pattern
        )

(stest/instrument `triangle-list-to-pattern)

;;; Simple transforms
(defn scale ([val pattern] (lazy-seq (map (partial sshapes/scale val) pattern )))   )

(defn translate  [dx dy pattern] (lazy-seq (map (partial sshapes/translate dx dy) pattern))  )

(defn translate-to [x y pattern] (translate (- x) (- y) pattern) )

(defn h-reflect [pattern] (lazy-seq (map sshapes/h-reflect pattern) ) )
(defn v-reflect [pattern] (lazy-seq (map sshapes/v-reflect pattern) ) )

(defn stretch [sx sy pattern] (lazy-seq (map (partial sshapes/stretch sx sy) pattern)))
(defn rotate [da pattern] (lazy-seq (map (partial sshapes/rotate da) pattern)))

(defn wobble [noise pattern] (lazy-seq (map (partial sshapes/wobble noise) pattern)))

(defn over-style "Changes the style of a pattern" [style pattern]
  (lazy-seq (map (partial sshapes/add-style style) pattern)))

(defn extract-points [{:keys [style points]}] points)

(defn style-attribute-set [pattern attribute]
  (reduce (fn [atts sshape]
            (let [style (get sshape :style) ]
              (if (contains? style attribute)
                (conj atts (get style :stroke) )
                atts)) )
          (set []) pattern) )

(defn color-set [pattern] (union (style-attribute-set pattern :stroke) (style-attribute-set pattern :fill)))

(defn flatten-pattern "Flatten all sshapes into a single sshape"
  ([pattern] (flatten-pattern {} pattern))
  ([style pattern]
     (let [all-points (mapcat extract-points pattern) ]
       (sshapes/->SShape style all-points) )  ) )

(defn reframe-scaler "Takes a sshape and returns a scaler to reduce it to usual viewport coords [-1 -1][1 1] "
  [sshape] (/ 2.0 (max (sshapes/width sshape) (sshapes/height sshape))))

(defn leftmost [pattern] (apply min (map sshapes/leftmost pattern)))
(defn rightmost [pattern] (apply max (map sshapes/rightmost pattern)))
(defn width [pattern] (- (rightmost pattern) (leftmost pattern)))
(defn top [pattern] (apply min (map sshapes/top pattern)))
(defn bottom [pattern] (apply max (map sshapes/bottom pattern)))
(defn height [pattern] (- (bottom pattern) (top pattern)))

(defn h-centre "Assumes pattern is taller than wide so move it to horizontal centre" [pattern]
  (let [lb (leftmost pattern)
        rb (rightmost pattern)
        width (- rb lb)
        target-left  (/ (- width) 2)
        shift (- target-left lb)
       ]
    (translate shift 0 pattern)))

(defn reframe [pattern]
  (let [sshape (flatten-pattern {} pattern)
        scaled (scale (reframe-scaler sshape) pattern)
        f-scaled (flatten-pattern {} scaled)
        dx (- (- 1) (sshapes/leftmost f-scaled))
        dy (- (- 1) (sshapes/top f-scaled))
        ]
    (let []
      (translate dx dy scaled))))

(defn filter-pattern [p? pattern] (map (partial sshapes/ss-filter p?) pattern))

(defn filter-sshapes-in-pattern "this removes entire sshapes from the pattern that have points that don't match the criteria"
  [p? pattern]
  (let [all-ok? (fn [{:keys [style points]}] (every? p? points) )]
    (filter all-ok? pattern) ) )


(defn clip-sshape "takes a predicate and a sshape, splits the sshape at any point which doesn't meet the predicate, return pattern"
  [p? {:keys [style points]}]
  (let []
    (if (empty? points) {:style style :points points}
        (loop [p (first points) ps (rest points) acc-sshape-ps [] acc-pattern []]
          (cond
           (and (empty? ps) (not (p? p))) (conj acc-pattern {:style style :points acc-sshape-ps})
           (empty? ps) (conj acc-pattern {:style style :points (conj acc-sshape-ps p)})
           (p? p) (recur (first ps) (rest ps) (conj acc-sshape-ps p) acc-pattern)
           :else (recur (first ps) (rest ps) [] (conj acc-pattern {:style style :points acc-sshape-ps}))
           )
          )
        )
    ))

(defn clip "clips all sshapes in a pattern"
  [p? pattern] (mapcat (partial clip-sshape p?) pattern) )


(defn mol= "more or less equal patterns"
  [pattern1 pattern2]
  (if (not= (count pattern1) (count pattern2)) false
      (let [ssmol= (fn [[ss1 ss2]] (sshapes/mol= ss1 ss2))]
        (every? ssmol= (map vector pattern1 pattern2) )))  )
