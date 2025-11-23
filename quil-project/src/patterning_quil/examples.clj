(ns patterning_quil.examples
  (:require [patterning.maths :as maths])
  (:require [patterning.sshapes :refer [->SShape ]])
  (:require [patterning.groups :as groups])
  (:require [patterning.layouts
             :refer [framed clock-rotate stack grid diamond-grid
                     four-mirror four-round nested-stack checkered-grid
                     half-drop-grid random-turn-groups h-mirror]])
  (:require [patterning.library.std :refer [poly spiral horizontal-line drunk-line]])
  (:require [patterning.library.turtle :refer [basic-turtle]])
  (:require [patterning.library.complex_elements :refer [vase zig-zag]])
  (:require [patterning.view :refer [make-txpt ]])
  (:require [patterning.color :refer [p-color]])
  (:require [patterning.examples.city :as city])
  (:require [patterning.examples.framedplant :as framedplant])
  (:require [patterning.examples.design_language1 :as design-language])
  (:require [patterning.library.symbols :as symbols]
            [patterning.library.l_systems :refer [l-system]]))

;; Color definitions
(def col (p-color 140 220 180))
(def col-f (p-color 190 255 200 100))

;; Pattern generation functions
(defn a-round [n] (clock-rotate n (poly 0 0.5 0.3 n {:stroke col :stroke-weight 2 :fill col-f})))

(defn rand-col [] (p-color (rand-int 255) (rand-int 255) (rand-int 255) (rand-int 255)))
(defn darker-color [c] (apply p-color (map (partial * 0.7) c)))

(defn random-color [p] (let [c (rand-col)] (groups/over-style {:fill c :stroke (darker-color c)} p)))

(def t (stack
        (poly 0 0 0.6 3 {:stroke col :fill (p-color 255 0 0) :stroke-weight 2})
        (horizontal-line 0 {:stroke col :stroke-weight 2})))

(defn rcol []
  {:stroke
   (rand-nth [
     (p-color 127 255 127)
     (p-color 255)
     (p-color 200 100 200)
     (p-color 200 200 100)
     (p-color 102 255 178)
     (p-color 255 51 255)  ; pink
     (p-color 0 204 204)
     (p-color 255 51 51)
     (p-color 255 255 204)
     ])
   :stroke-weight 2})

(defn t1
  ([p] (t1 p (rcol)))
  ([p s]
   (->> p
        (stack
         (drunk-line 10 0.2 s))
        four-round
        (groups/rotate (/ (rand-int 10) 10)))))

(defn blksqr [x]
  (stack
   (groups/rect -1 -1 2 2 {:fill (p-color 0)})
   (grid 5
                (repeat (t1 [] {:stroke (p-color 250 180 200)
                               :stroke-weight 2})))
   (->>
    (apply
     stack
     (take 3
           (iterate t1 (drunk-line 10 0.1 (rcol)))))
    four-mirror)))

(def l-system-2 (l-system [["F" "F[+F]F[-F][GF]"] ["G" "H"] ["H" "IZ"]]))

(defn sys-g2 [] 
  (basic-turtle [0 0] 0.1 (/ maths/PI -2) (/ maths/PI (+ 7 (rand-int 4)))
                (l-system-2 (+ 2 (rand-int 3)) "F")
                {\Z (fn [x y a]
                      (let []
                        (poly x y 0.05 8
                              {:fill design-language/my-red})))}
                {:stroke design-language/my-green :stroke-weight 2}))

(defn sprey [] 
  (groups/translate -0.6 0 (groups/h-reflect (groups/reframe (sys-g2)))))

(defn four [p1 p2 p3 p4]
  (stack
   (->> p1 (groups/scale 0.5) (groups/translate -0.5 -0.5))
   (->> p2 (groups/scale 0.5) (groups/translate 0.5 -0.5))
   (->> p3 (groups/scale 0.5) (groups/translate -0.5 0.5))
   (->> p4 (groups/scale 0.5) (groups/translate 0.5 0.5))))

;; Example patterns
(defn city-pattern []
  (city/city 9))

(defn round-pattern []
  (random-color (a-round (+ 3 (rand-int 3)))))

(defn spray-pattern []
  (sprey))

(defn block-pattern []
  (blksqr 1))

(defn default-pattern []
  (four
   (city-pattern)
   (round-pattern)
   (spray-pattern)
   (block-pattern))) 