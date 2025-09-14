(ns patterning.library.douat
  (:require [patterning.layouts :refer [stack]]
            [patterning.groups :refer [scale translate rotate h-reflect v-reflect group]]
            [patterning.sshapes :refer [->SShape]]
            [patterning.maths :refer [PI]]
            [patterning.library.std :refer [square]]
            [patterning.color :refer [p-color]]))


(defn Douat [p] 
  {:p p
   :ps []
   :depth 0}
  )


(defn add-douat [f]
  (fn [{:keys [p ps depth]}]
    (if (< (count ps) 3)
      {:p p
       :ps (concat ps [(f p)])
       :depth depth
       }
      (let [q
            (stack
      	     (->> (nth ps 0) (scale 0.5) (translate -0.5 -0.5))
      	     (->> (nth ps 1) (scale 0.5) (translate 0.5 -0.5))
             (->> (nth ps 2) (scale 0.5) (translate 0.5 0.5))
             (->> (f p) (scale 0.5) (translate -0.5 0.5))
             )]
        {:ps []
         :p q
         :depth (+ 1 depth)
         }))))

(def A (add-douat identity))
(def B (add-douat (fn [p] (rotate (/ PI 2) p))))
(def C (add-douat (fn [p] (rotate PI p))))
(def D (add-douat (fn [p] (rotate (* 3 (/ PI 2)) p))))
(def E (add-douat (fn [p] (h-reflect p))))
(def F (add-douat (fn [p] (v-reflect p))))
(def G (add-douat (fn [p] (-> p (h-reflect) (v-reflect)))))
(defn R [douat]
  ((rand-nth [A B C D]) douat))
(defn Q [douat]
  ((rand-nth [A B C D E F G]) douat))


(defn truchet []
  (stack
   (square {:fill 255})
   (group
    (->SShape
     {:fill (p-color 0) :stroke (p-color 0)}
     [[-1 1] [-1 -1] [1 1]]))))



(comment "An Example"
(->
  (Douat 
   (stack
    (square {:fill (p-color 90 0 0)})
    [(->SShape
       {:fill (p-color 0) 
        :stroke-weight 1
        :stroke (p-color 255 255 0)}
       [[1 1] [-1 1] [-1 -1] [1 0]])]
    ))
 
   A E E A
   A B C D
   A E E A
   B E E B
  (:p)

  ))
