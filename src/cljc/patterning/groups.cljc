(ns patterning.groups
  (:require [patterning.maths :as maths]
            [patterning.sshapes :refer [->SShape] :as sshapes]
            [patterning.color :refer [p-color]]
            [clojure.set :refer [union]]
            [malli.core :as m]
            [malli.error :as me]
            [patterning.macros :refer [optional-styled-primitive]]
            ))


;; Malli schemas for validation
(def Point
  [:tuple number? number?])

(def Style
  [:map
   [:fill {:optional true} any?]
   [:stroke {:optional true} any?]
   [:stroke-weight {:optional true} any?]
   [:bezier {:optional true} any?]
   [:hidden {:optional true} any?]])

(def SShape
  [:map
   [:style {:optional true} Style]
   [:points [:sequential Point]]])

(def Group
  [:sequential SShape])

;; Validation functions
(defn validate-group
  "Validates if data is a valid group (sequence of SShapes)"
  [data]
  (m/validate Group data))

(defn explain-group
  "Returns detailed explanation if data is not a valid group"
  [data]
  (when-not (validate-group data)
    {:points (me/humanize (m/explain Group data))}))

(defn explain-sshape
  "Returns detailed explanation if data is not a valid SShape"
  [data]
  (when-not (m/validate SShape data)
    (me/humanize (m/explain SShape data))))


;; A Pattern is nothing but a sequence of SShapes
;;
;; Patterns can represent ordinary patterns that require several sshapes
;; (because they have disjoint geometric forms, or multiple colors
;; etc.
;; Patterns are also the flattened results of combining multiple groups
;; together, eg. when running them through a layout.


;;; Making patterns
(defn APattern "a vector of sshapes" [& sshapes] (lazy-seq sshapes) )

(defn group [& sshapes] sshapes)

(defn empty-pattern [] [])


(defn triangle-list-to-pattern [trs]
  (map #(sshapes/->SShape {:fill (p-color 100 100 100 100)
                           :stroke (p-color 0)}
                          (maths/triangle-points %)) trs ))


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




;; Centering

(def rect (optional-styled-primitive [x y w h]
                                     (let [x2 (+ x w) y2 (+ y h)]
                                       [[x y] [x2 y] [x2 y2] [x y2] [x y]])))

(defn box [x y w h] {:x x :y y :width w :height h})
(defn box-flip [{:keys [x y width height]}] {:x x :y y :width height :height width})
(defn box->rect [{:keys [x y width height]} style]  (rect x y width height style) )


(defn horizontal-centre-box [inner outer]
   (let [h-off (+ (:x outer) (-> (- (:width outer) (:width inner)) (/ 2)))]
       (box h-off (:y inner) (:width inner) (:height inner))
   )
)

(defn vertical-centre-box [inner outer]
  (let [v-off (+ (:y outer) (-> (- (:height outer) (:height inner)) (/ 2)))]
     (box (:x inner) v-off (:width inner) (:height inner))
  )
)

(defn pattern->box [pattern]
  (box (leftmost pattern) (top pattern) (width pattern) (height pattern) ))


(defn horizontal-centre-pattern-in-pattern [inner-pattern outer-pattern]
  (let [inner-box (pattern->box inner-pattern)
        outer-box (pattern->box outer-pattern)
        new-inner-box (-> inner-box (horizontal-centre-box outer-box))
        dx (- (:x new-inner-box) (leftmost inner-pattern))]
    (translate dx 0 inner-pattern)))

(defn vertical-centre-pattern-in-pattern [inner-pattern outer-pattern]
  (let [inner-box (pattern->box inner-pattern)
        outer-box (pattern->box outer-pattern)
        new-inner-box (-> inner-box (vertical-centre-box outer-box))
        dy (- (:y new-inner-box) (top inner-pattern))]
    (translate 0 dy inner-pattern)))


(defn centre-pattern-in-pattern [inner-pattern outer-pattern]
  (-> inner-pattern
      (horizontal-centre-pattern-in-pattern outer-pattern)
      (vertical-centre-pattern-in-pattern outer-pattern))
  )


;; Tile set functions for creating rotated and reflected variants

(defn rotate-tile-set
  "Creates a vector of rotated variants of patterns.
   Each argument can be either:
   - A pattern (defaults to 4 rotations: 0°, 90°, 180°, 270°)
   - A vector [pattern n] where n is 2 or 4 (for 2-fold or 4-fold symmetry)
   
   Examples:
   (rotate-tile-set p1 p2 p3) ; 12 tiles (4 rotations each)
   (rotate-tile-set [p1 2] p2 p3) ; 10 tiles (2 rotations for p1, 4 for p2 and p3)"
  [& args]
  (into []
        (mapcat (fn [arg]
                  (if (vector? arg)
                    ;; Vector form: [pattern n]
                    (let [[pattern n] arg]
                      (case n
                        2 [pattern (rotate maths/d90 pattern)]
                        4 [pattern 
                           (rotate maths/d90 pattern)
                           (rotate maths/d180 pattern)
                           (rotate maths/d270 pattern)]
                        (throw (ex-info "Rotation count must be 2 or 4" {:n n :arg arg}))))
                    ;; Plain pattern: default to 4 rotations
                    [arg
                     (rotate maths/d90 arg)
                     (rotate maths/d180 arg)
                     (rotate maths/d270 arg)]))
                args)))

(defn reflect-tile-set
  "Creates a vector of reflected variants of patterns.
   For each pattern, returns: [original, h-reflect, v-reflect, both]
   
   Examples:
   (reflect-tile-set p1 p2 p3) ; 12 tiles (4 reflections each)"
  [& patterns]
  (into []
        (mapcat (fn [pattern]
                  [pattern
                   (h-reflect pattern)
                   (v-reflect pattern)
                   (h-reflect (v-reflect pattern))])
                patterns)))
