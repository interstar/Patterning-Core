(ns patterning.sshapes
  (:require [patterning.maths :as maths]
            [clojure.data :refer [diff]])
  #?(:clj (:require [patterning.macros :refer [optional-styled-primitive]])
     :cljs (:require-macros [patterning.macros :refer [optional-styled-primitive]] )
     ))


;; Shapes
;; Shape is a list of points

(defn scale-shape [val shape]
  (into [] (map (fn [[x y]] [(* val x) (* val y)]) shape )) )

(defn translate-shape [dx dy shape]
  (into [] (map (fn [[x y]] [(+ dx x) (+ dy y)]) shape ))  )

(defn stretch-shape [sx sy shape]
  (into [] (map (fn [[x y]] [(* sx x) (* sy y)]) shape ))  )

(defn h-reflect-shape [shape]
  (into [] (map (fn [[x y]] [(- x) y]) shape )) )

(defn v-reflect-shape [shape]
  (into [] (map (fn [[x y]] [x (- y)]) shape )) )

(defn close-shape "Closes an open shape" [points] (conj points (first points)))

(defn points-to-polars [points] (into [] (map maths/rec-to-pol points)))
(defn polars-to-points [polars] (into [] (map maths/pol-to-rec polars)))

(defn rotate-shape [da shape] (map (partial maths/rotate-point da) shape))


(defn wobble-shape [noise shape] (map (partial maths/wobble-point noise) shape))

(defn ends "the start and end point of a shape" [shape] [(first shape) (last shape)] )

(defn tie-together "Merge two open shapes together, by choosing the ends that are closest" [shape1 shape2]
  (let [[e1 e2] (ends shape1)
        [e3 e4] (ends shape2)
        o1 (maths/distance e1 e3)
        o2 (maths/distance e1 e4)
        o3 (maths/distance e2 e3)
        o4 (maths/distance e2 e4)
        dists [ o1 o2 o3 o4 ]
        m (apply min dists)]
    (cond
      (= m o1) (concat (reverse shape1) shape2)
      (= m o2) (concat (reverse shape1) (reverse shape2))
      (= m o3) (concat shape1 shape2)
      (= m o4) (concat shape1 (reverse shape2)))  ) )

(defn h-glue-shape "repeats a shape horizontally" [shape1 shape2]
  (let [e1 (last shape1)
        add (fn [[x y]] [(+ 1 (first e1) x) y])]
    (concat shape1 (into [] (map add shape2)))))

(defn filter-shape [f? ps] (filter f? ps))

(defn mol=shapes "more or less equal shapes"
  [shape1 shape2] (cond (and (empty? shape1) (empty? shape2)) true
                        (not= (count shape1) (count shape2)) false
                        :else
                      (and
                       (maths/molp= (first shape1) (first shape2))
                       (mol=shapes (rest shape1) (rest shape2)))  ))



;; Specs

(defrecord SShape [style points])
(defn empty-sshape [] (->SShape {} []))


;; Adding styles to sshapes
(defn add-style [new-style {:keys [style points]} ] {:points points :style (conj style new-style)})
(defn set-color "Give new color to a sshape " [c sshape] (add-style {:stroke c} sshape))
(defn set-weight "Give new strokeWeight to a sshape" [weight sshape] (add-style {:stroke-weight weight} sshape))
(defn set-fill "Give a fill-color to a sshape" [fill sshape] (add-style {:fill fill} sshape))
(defn hide "Keep this sshape in the pattern but disable it from rendering" [sshape] (add-style {:hidden true} sshape))
(defn unhide "Remove the hidden label from a sshape, so it's shown" [sshape] (dissoc sshape :hidden))
(defn hidden? "Test if a sshape is hidden" [{:keys [style points]}] (contains? style :hidden) )


(defn bez-curve [style points] (add-style {:bezier true} (->SShape style points )))

;; SShape Transforms
(defn scale [val sshape] (->SShape (get sshape :style) (scale-shape val (get sshape :points)))  )

(defn translate [dx dy {:keys [style points]}] (->SShape style (translate-shape dx dy points)))

(defn h-reflect [{:keys [style points]}] (->SShape style (h-reflect-shape points)))

(defn v-reflect [{:keys [style points]}] (->SShape style (v-reflect-shape points)))

(defn stretch [sx sy sshape] (->SShape (get sshape :style) (stretch-shape sx sy (get sshape :points))))

(defn rotate [da sshape] (->SShape (get sshape :style) (rotate-shape da (get sshape :points))) )

(defn wobble [noise {:keys [style points]}] (->SShape style (wobble-shape noise points)) )


;; Other processing
(defn reverse-order [{:keys [style points]}] (->SShape style (reverse points)))

(defn flat-point-list [{:keys [style points] :as sshape}] (flatten points))

(defn xs [{:keys [style points]}] (map first points))
(defn ys [{:keys [style points]}] (map second points))

(defn top [sshape] (apply min (ys sshape)))
(defn bottom [sshape] (apply max (ys sshape)))
(defn leftmost [sshape] (apply min (xs sshape)))
(defn rightmost [sshape] (apply max (xs sshape)))

(defn width [sshape] (- (rightmost sshape) (leftmost sshape)))
(defn height [sshape] (- (bottom sshape) (top sshape)))


(defn ss-filter [p? {:keys [style, points]}] (->SShape style (filter-shape p? points)))

(defn mol= "more or less equal sshapes"
  [sshape1 sshape2] (and (= (get sshape1 :style) (get sshape2 :style) )
                         (mol=shapes (get sshape1 :points) (get sshape2 :points))) )

(defn triple-list [points] (partition 3 1 (cycle points))  )
(defn triangles-list [points] (map (fn [pts3] (apply maths/triangle (flatten pts3))) (triple-list points ) ))
(defn triangles-in-sshape [{:keys [style points]}] (triangles-list points))

(defn is-ear [{:keys [style points]} t]
  (let [tpts (maths/triangle-points t)
        not-in-tri? (fn [p] (not  (maths/point-in-list p tpts)))
        other (filter not-in-tri? points)
        ]
    (not-any? (partial maths/contains-point t) other)    )  )




(defn to-triangles [{:keys [style points] :as original-shape}]
  (loop [shape original-shape saved-ears [] depth 0]
    (println "to-triangles: depth" depth "points" (count (:points shape)) "saved-ears" (count saved-ears))
    (println "Current points:" (:points shape))
    (let [pts (:points shape)
          count-pts (count pts)]
      (if (= count-pts 3)
        (let [triangle (apply maths/triangle pts)]
          (when-not (maths/validate-triangle triangle)
            (throw (ex-info "Invalid triangle generated"
                            {:triangle triangle
                             :explanation (maths/explain-triangle triangle)})))
          (println "Base case returning:" (conj saved-ears triangle))
          (conj saved-ears triangle))
        (let [tl (take count-pts (triangles-list pts))
              ears (filter (partial is-ear shape) tl)
              no-ears (count ears)]
          (println "Potential triangles:" (count tl))
          (println "Found ears:" no-ears)
          (when (pos? no-ears)
            (println "First ear:" (first ears)))
          (if (< no-ears 1)
            (do
              (println "No ears found, returning saved-ears")
              saved-ears)
            (let [ear (first ears)
                  new-list (remove (fn [p] (maths/molp= p (:B ear))) (:points shape))]
              (println "Removing ear:" ear)
              (println "New point list:" new-list)
              (if (< (count new-list) 3)
                (do
                  (println "New list too small, returning saved-ears")
                  saved-ears)
                (let [pruned (->SShape style new-list)]
                  (println "Recursing with pruned shape")
                  (recur pruned (conj saved-ears ear) (inc depth)))))))))))

;; Styles (dictionaries of style info)
(defn add-property [style prop val] (conj style {prop val}))
