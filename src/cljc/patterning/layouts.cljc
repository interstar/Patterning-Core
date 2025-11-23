(ns patterning.layouts
  (:require [patterning.maths :as maths
             :refer [default-random]]
            [patterning.groups :as groups]
            [patterning.sshapes :as sshapes]))


;; Layouts
;; Note layouts combine and multiply groups to make larger groups

(defn superimpose-layout "simplest layout, two groups located on top of each other "
  [group1 group2] (lazy-seq (concat group1 group2))   )

(defn stack "superimpose a number of groups"
  [& groups] (reduce superimpose-layout groups))

(defn nested-stack "superimpose smaller copies of a shape"
  [reducer n group & [styles]]
  (if styles
    ;; With styles: apply different style to each scaled copy
    (let [gen-next (fn [[style x]] (groups/over-style style (groups/scale x group)))]
      (apply stack (map gen-next (map vector (take n styles) (take n (iterate reducer 1))))))
    ;; Without styles: just scale, keeping original styles
    (let [gen-next (fn [x] (groups/scale x group))]
      (apply stack (map gen-next (take n (iterate reducer 1)))))))


(defn cart "Cartesian Product of two collections" [colls]
  (if (empty? colls)
    '(())
    (for [x (first colls) more (cart (rest colls))]
      (cons x more))))

(defn grid-layout-positions "calculates the positions for a grid layout"
  [number]
  (let [ offset (/ 2 number)
         inc (fn [x] (+ offset x))
         ino (float (/ offset 2))
         init (- ino 1)
         h-iterator (take number (iterate inc init))
         v-iterator (take number (iterate inc init)) ]
  (cart [h-iterator v-iterator])  ) )



(defn half-drop-grid-layout-positions "Like a grid but with a half-drop every other column"
  [number]
  (let [ offset (/ 2 number)
        n2 (int  (/ number 2))
        inc-x (fn [x] (+ (* 2 offset) x))
        inc-y (fn [y] (+ offset y))
        in-x (float (/ offset 2))
        in-y (float (/ offset 2))

        init-x1 (- in-x 1)
        init-x2 (- in-x (- 1 offset))
        init-y1 (- in-y 1)
        init-y2 (- in-y (+ 1 ( / offset 2)))

        h1-iterator (take (if (even? n2) n2 (+ 1 n2)) (iterate inc-x init-x1))
        v1-iterator (take number (iterate inc-y init-y1))
        h2-iterator (take n2 (iterate inc-x init-x2))
        v2-iterator (take (+ 1 number) (iterate inc-y init-y2))
        h-iterator (interleave h1-iterator h2-iterator)
        v-iterator (interleave v1-iterator v2-iterator)
        ]
    (concat (cart [h1-iterator v1-iterator]) (cart [h2-iterator v2-iterator]))))


(defn diamond-layout-positions "Diamond grid, actually created like a half-drop"
  [number]
  (let [
        offset (/ 2 number)
        n2 (int  (/ number 1))
        inc-x (fn [x] (+ (* 1 offset) x))
        inc-y (fn [y] (+ offset y))
        in-x (float (/ offset 2))
        in-y (float (/ offset 2))

        init-x1 (- in-x (+ 1 (/ offset 2)))
        ;;        init-x2 (- in-x (- 1 offset))
        init-x2 (+ init-x1 (/ offset 2) )
        init-y1 (- in-y 1)
        init-y2 (- in-y (+ 1 ( / offset 2)))

        h1-iterator (take (+ 0 (if (even? n2) n2 (+ 1 n2))) (iterate inc-x init-x1))
        v1-iterator (take number (iterate inc-y init-y1))
        h2-iterator (take n2 (iterate inc-x init-x2))
        v2-iterator (take (+ 1 number) (iterate inc-y init-y2))
        h-iterator (interleave h1-iterator h2-iterator)
        v-iterator (interleave v1-iterator v2-iterator)
        ]
    (concat (cart [h1-iterator v1-iterator]) (cart [h2-iterator v2-iterator]))))




(defn ensure-sequence
  "If xs is a single group/pattern (sequence of SShapes), repeat it.
   If xs is already a sequence of groups/patterns, return as-is."
  [xs]
  (if (and (seq xs)
           (sshapes/is-sshape? (first xs)))
    ;; It's a single group/pattern (sequence of SShapes)
    (repeat xs)
    ;; It's already a sequence of groups/patterns
    xs))

(defn place-groups-at-positions "Takes a list of groups and a list of positions and puts one of the groups at each position"
  [groups positions]
  (concat ( mapcat (fn [[ [x y] group]] (groups/translate x y group)) (map vector positions groups) ) ))

(defn scale-group-stream [n groups] (map (partial groups/scale (/ 1 n)) groups))

(defn grid-layout "Takes an n and a group-stream and returns items from the group-stream in an n X n grid "
  [n groups] (place-groups-at-positions (scale-group-stream n (ensure-sequence groups)) (grid-layout-positions n))  )

(defn half-drop-grid-layout "Like grid but with half-drop"
  [n groups] (place-groups-at-positions (scale-group-stream n (ensure-sequence groups)) (half-drop-grid-layout-positions n)))

(defn diamond-layout "Like half-drop"
  [n groups] (place-groups-at-positions (scale-group-stream n (ensure-sequence groups)) (diamond-layout-positions n)))

(defn q1-rot-group [group] (groups/rotate (float (/ maths/PI 2)) group ) )
(defn q2-rot-group [group] (groups/rotate maths/PI group))
(defn q3-rot-group [group] (groups/rotate (-  (float (/ maths/PI 2))) group))


(defn random-turn-groups [groups & {:keys [random] :or {random default-random}}]
  (let [random-turn (fn [group]
                      (case (.randomInt random 4)
                        0 group
                        1 (q1-rot-group group)
                        2 (q2-rot-group group)
                        3 (q3-rot-group group)  ) ) ]
    (map random-turn groups) ))



(defn random-grid-layout  "Takes a group and returns a grid with random quarter rotations"
  [n groups & {:keys [random] :or {random default-random}}] 
  (grid-layout n (random-turn-groups groups :random random)))


(defn drop-every [n xs] (lazy-seq (if (seq xs) (concat (take (dec n) xs) (drop-every n (drop n xs))))))


(defn check-seq "returns the appropriate lazy seq of groups for constructing a checked-layout"
  [n groups1 groups2]
  (let [ together (interleave groups1 groups2 ) ]
    (if (= 0 (mod n 2))
      (drop-every (+ 1 n) together)
      together ) ) )



(defn checked-layout "does checks using grid layout"
  [number groups1 groups2]
  (grid-layout number (check-seq number groups1 groups2)))


(defn one-x-layout
  "Takes a total number of rows, an index i and two group-streams.
   Makes an n X n square where row or col i is from group-stream2 and everything else is group-stream1"
  [n i f groups1 groups2]
  (let [the-seq (concat (take (* n i) groups1) (take n groups2) (take (* n (- n i)) groups1) )
        layout-positions (map vector (scale-group-stream n the-seq) (grid-layout-positions n))
        ]
     (concat (mapcat f layout-positions))
    )
  )

(defn one-row-layout "uses one-x-layout with rows"
  [n i groups1 groups2] (one-x-layout n i (fn [[group [x y]]] (groups/translate y x group)) groups1 groups2  ))


(defn one-col-layout "uses one-x-layout with rows"
  [n i groups1 groups2] (one-x-layout n i (fn [[group [x y]]] (groups/translate x y group)) groups1 groups2 ) )

(defn alt-cols "Fills a group-stream with cols from alternative group-streams"
  [n groups1 groups2]
  (cycle (concat (take n groups1) (take n groups2)))  )

(defn alt-rows "Fills a group-stream with rows from alternative group-streams"
  [n groups1 groups2]
  (interleave groups1 groups2))

(defn alt-cols-grid-layout "Every other column from two streams" [n groups1 groups2]
  (grid-layout n (alt-cols n groups1 groups2)))

(defn alt-rows-grid-layout "Every other row from two streams" [n groups1 groups2]
  (grid-layout n (alt-rows n groups1 groups2)))



(defn four-mirror "Four-way mirroring. Returns the group repeated four times reflected vertically and horizontall" [group]
  (let [nw (groups/translate (- 0.5) (- 0.5) (groups/scale (float (/ 1 2)) group))
        ne (groups/h-reflect nw)
        sw (groups/v-reflect nw)
        se (groups/h-reflect sw) ]
    (concat nw ne sw se)))

(defn h-mirror "Reflect horizontally and stretch"  [group]
  (let [left  (groups/translate -0.5 0 (groups/stretch 0.5 1 group))
        right (groups/h-reflect left)]
    (stack left right)))

(defn v-mirror "Reflect vertically and stretch" [group]
  (let [top    (groups/translate 0 -0.5 (groups/stretch 1 0.5 group))
        bottom (groups/v-reflect top)]
    (stack top bottom)))

(defn clock-rotate "Circular layout. Returns n copies in a rotation"
  [n group]
  (let [angs (maths/clock-angles n)
        groups (ensure-sequence group)]
    (concat (mapcat (fn [a g] (groups/rotate a g)) angs groups ))
    ))

(defn ring "Better clock-rotate" [n offset groups]
  (let [shift-f (fn [g] (groups/translate
                        (* offset 2)
                        0
                        (groups/scale
                         (/ (- 1 offset) 2)
                         (groups/rotate maths/PI (groups/h-centre (groups/reframe g))))))
        groups-seq (ensure-sequence groups)]

    (groups/reframe (groups/rotate (- (/ maths/PI 2))
                                    (mapcat (fn [a g]
                                              (groups/rotate a (shift-f g)))
                                            (iterate (partial + (* 2 (/ maths/PI n))) 0 )
                                            (take n (cycle groups-seq)))))))


(defn four-round "Four squares rotated" [group]
  (let [scaled (groups/scale (float (/ 1 2)) group)
        p2 (float (/ maths/PI 2))
        nw (groups/translate (- 0.5) (- 0.5) scaled )
        ne (groups/translate 0.5 (- 0.5) (q1-rot-group scaled))
        se (groups/translate (- 0.5) 0.5 (q3-rot-group scaled))
        sw (groups/translate 0.5 0.5 (q2-rot-group scaled) )
        ]
    (concat nw ne se sw )  )  )

(defn frame "Frames consist of corners and edges. " [grid-size corners edges]
  (let [
        gs2 (- grid-size 2)
        [nw b c d] (into [] (take 4 corners))
        ne ( groups/h-reflect b)
        se ( groups/h-reflect (groups/v-reflect c ))
        sw ( groups/v-reflect d)
        edge (first edges)
        col (concat [ (q1-rot-group edge)]
                    (repeat gs2 (groups/empty-pattern))
                    [ (q3-rot-group edge)]
            )     ]
    (grid-layout grid-size (concat [nw] (repeat gs2 edge) [sw]
                                   (mapcat identity (repeat gs2 col))
                                   [ne] (repeat gs2 (q2-rot-group edge)) [se] ))
    )  )

(defn framed "Puts a frame around the other group" [grid-size corners edges inner]
  (let [gs2 (- grid-size 2)
        shrink (float (/ gs2 grid-size))]
    (stack (groups/scale shrink inner)
            (frame grid-size corners edges) ) ))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Human aspect ratio

(defn calc-dims [cols rows]
  (let [maxdim (if (< rows cols) cols rows)
        
        ;; The scaling factor for a pattern that is 2 units wide 
        ;; to fit in a cell that is (2/maxdim) wide.
        scalar (/ 1 maxdim)
        
        ;; The size of a single cell.
        size (* 2 scalar)
        half-size scalar

        ;; dimensions of the whole frame
        width (* size cols)
        height (* size rows)
        half-width (/ width 2)
        half-height (/ height 2)

        cols-2 (- cols 2)
        rows-2 (- rows 2)
        ]
    {:maxdim maxdim
     :scalar scalar
     :size size
     :topleft    [(- half-size half-width) (- half-size half-height)]
     :topright   [(- half-width half-size) (- half-size half-height)]
     :bottomleft [(- half-size half-width) (- half-height half-size)]
     :bottomright [(- half-width half-size) (- half-height half-size)]
     :cols-2 cols-2
     :rows-2 rows-2
     :cols-1 (- cols 1)
     :rows-1 (- rows 1)
     }))

(defn corners [cols rows corner-pattern]
  (let [dims (calc-dims cols rows)
        corners
        [corner-pattern
         (groups/h-reflect corner-pattern)
         (-> corner-pattern (groups/h-reflect) (groups/v-reflect))
         (groups/v-reflect corner-pattern)]
        ]
    (place-groups-at-positions
     (map #(groups/scale (:scalar dims) %) corners)
     [(:topleft dims)
      (:topright dims)
      (:bottomright dims)
      (:bottomleft dims)])))


(defn edges [cols rows edge-pattern]
  (let
    [dims (calc-dims cols rows)
     tx (fn [pat da]
          (->> pat (groups/rotate da)
               (groups/scale (:scalar dims))))
     itersize
     (fn [start len]
       (take len 
             (drop 1
                   (iterate (fn [x] (+ x (:size dims))) start )
                   )))

     top-positions
     (map vector
           (itersize (first (:topleft dims)) (:cols-2 dims))
           (repeat (second (:topleft dims))))

     right-positions
     (map vector
           (repeat (first (:topright dims)))
           (itersize (second (:topright dims)) (:rows-2 dims)))

     bottom-positions
     (map vector
           (itersize (first (:bottomleft dims)) (:cols-2 dims))
           (repeat (second (:bottomleft dims))))

     left-positions
     (map vector
           (repeat (first (:topleft dims)))
           (itersize (second (:topleft dims)) (:rows-2 dims)))

     tops (place-groups-at-positions
           (repeat (tx edge-pattern 0))
           top-positions)

     rights (place-groups-at-positions
             (repeat (tx edge-pattern (/ maths/PI 2)))
             right-positions)

     bottoms (place-groups-at-positions
              (repeat (tx edge-pattern maths/PI))
              bottom-positions)

     lefts (place-groups-at-positions
            (repeat (tx edge-pattern (* 3 (/ maths/PI 2))))
            left-positions)
     ]
    (concat tops rights bottoms lefts)
    )
  )

(defn aspect-ratio-frame [cols rows corner-pattern edge-pattern]
  ;; Validate inputs - prevent invalid dimensions that would cause calculation errors
  (when (or (<= cols 0) (<= rows 0))
    (throw (ex-info "cols and rows must be positive" 
                    {:cols cols :rows rows})))
  (stack
   (corners cols rows corner-pattern)
   (edges cols rows edge-pattern))
  )



;;; Aspect-ratio-aware framing functions

(defn inner-stretch
  "Stretches content to exactly fill the inner rectangle"
  [inner-w inner-h inner-content]
  (let [shrink-x (/ inner-w 2.0)
        shrink-y (/ inner-h 2.0)]
    (groups/stretch shrink-x shrink-y inner-content)))

(defn inner-min
  "Shrinks content to fit within inner rectangle (letterbox/pillarbox)"
  [inner-w inner-h inner-content]
  (let [shrink (min (/ inner-w 2.0) (/ inner-h 2.0))]
    (groups/scale shrink inner-content)))

(defn inner-max
  "Fills inner rectangle and clips excess (crop to fit)"
  [inner-w inner-h inner-content]
  (let [shrink (max (/ inner-w 2.0) (/ inner-h 2.0))
        scaled-content (groups/scale shrink inner-content)
        half-w (/ inner-w 2.0)
        half-h (/ inner-h 2.0)
        clip-predicate (fn [[x y]]
                         (and (<= (- half-w) x half-w)
                              (<= (- half-h) y half-h)))]
    (groups/clip clip-predicate scaled-content)))


(defn aspect-ratio-framed
  ([cols rows corner-pattern edge-pattern inner-pattern fit-fn]
   (let [dims (calc-dims cols rows)
         inner-w (* (:cols-2 dims) (:size dims))
         inner-h (* (:rows-2 dims) (:size dims))
         scaled-inner (fit-fn inner-w inner-h inner-pattern)]
     (stack
      scaled-inner
      (aspect-ratio-frame cols rows corner-pattern edge-pattern))))
  ([cols rows corner-pattern edge-pattern inner-pattern]
   (aspect-ratio-framed cols rows corner-pattern edge-pattern inner-pattern inner-min))
  )


;; Flower of Life layout ... these are recursive developments of circles
(defn flower-of-life-positions [r depth [cx cy]]
  (if (= depth 0) [[cx cy]]
      (let [round-points (map (fn [a] (maths/rotate-point a [(+ cx 0) (+ cy r)])) (maths/clock-angles 6) )
            rec-points (mapcat (partial flower-of-life-positions r (- depth 1)) round-points)]
        (set (conj rec-points [cx cy]))
        ))  )

(defn sshape-to-positions [{:keys [style points] :as sshape}] points )

(defn sshape-as-layout [sshape group-stream scalar]
  (place-groups-at-positions (map #(groups/scale scalar %) group-stream) (sshape-to-positions sshape )  ))

