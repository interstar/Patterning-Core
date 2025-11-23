(ns patterning.examples.city
  (:require [patterning.maths :as maths]
            [patterning.sshapes :refer [->SShape ]]
            [patterning.groups :refer
             [translate h-reflect reframe scale rotate over-style
              rect stretch]]
            [patterning.layouts :refer
             [stack h-mirror clock-rotate
              grid checkered-grid framed]]
            [patterning.library.std :refer [poly square clock]]
            [patterning.library.turtle :refer [basic-turtle]]
            [patterning.library.l_systems :refer [l-system]]

            [patterning.color :refer [p-color]]
            [patterning.examples.design_language1 :refer [square2]]
            [patterning.examples.design_language1 :as design-language])
  )



(def engrave {:stroke-weight 1 :stroke (p-color 0 0 0)})

(def roof-a 
   [(->SShape engrave 
          [ [-0.9 1] [0 -0.4] [0.9 1] ] 
   )]
)

(def roof-b 
   [(->SShape engrave 
          [ [-0.9 1] [0 0.2] [0.9 1] ] 
   )]
)

(def roof-c   
   [(->SShape engrave 
          [ [-0.9 1] 
            [-0.6 0.78] [-0.6 0.3] 
            [-0.4 0.3] [-0.4 0.62] 
            [0 0.2] [0.9 1] ] 
   )]
)

(defn roof1 [random]
   (let [r (.randomNth random [roof-a roof-b roof-c])]
      (.randomNth random [r (h-reflect r)])))

(defn roof2 [random]
   (stack 
      (roof1 random)
      [(->SShape engrave [[-0.9 -1] [-0.9 0.4 ]])]
      [(->SShape engrave [[0.9 -1] [0.9 0.4 ]])]  
   )
)

(defn narrow_ [p]
   (stack
     (h-mirror [(->SShape engrave [[-0.8 -1] [-0.4 -0.8] [-0.4 0.8] [-0.8 1]])])
     (scale 0.8 p)
   )
)

(defn block_ [p]
  (stack
    [(->SShape engrave [[-0.9 -1] [-0.9 1]])]
    [(->SShape engrave [[0.9 -1] [0.9 1]])]
    p
  )
)

(defn block [p random]
  #((.randomNth random [block_ block_ block_ narrow_ narrow_]) p))

(def blank [])

(defn person [random]
   (let [x (.randomNth random [-0.5 -0.1 0 0.2 0.6])
          p (stack
               (poly 10 0.1 x -0.3 engrave)
               [
       (->SShape engrave [[x -0.2] [x 0.1] [(- x 0.1) 0.6]])
       (->SShape engrave [[x 0.1] [(+ x 0.1) 0.6]])
       (->SShape engrave [[(- x 0.1) -0.1] [(+ x 0.1) 0]])
              ]
            )
         ]
       (.randomNth random [p (h-reflect p)])
   )
)

(defn street [random]
  (fn [] 
    (.randomNth random [blank blank (person random)])))

(def bottom
    (stack
       [(->SShape engrave [[-0.9 0.8] [0.9 0.8]])]
       (rect -0.3 -0.2 0.6 1 engrave)
       [(->SShape engrave [[-0.9 -1] [-0.9 0.8]])]
       [(->SShape engrave [[0.9 -1] [0.9 0.8]])]
    )
  
)

(def round-window
   (poly 50 0.65 0 0 engrave)
)

(def arched-window
   (let [half 
      [(->SShape engrave
      [   [0 0.4]
          [-0.6 0.4] [-0.6 -0.2] 
          [-0.5 -0.27] [-0.4 -0.34] [-0.3 -0.4 ] 
          [-0.2 -0.42] [-0.1 -0.43] [0 -0.435]
      ]
     )] ]
     (stack
       half
       (h-reflect half)
     )
   )
)

(def thin-arched   (stretch 0.4 1 arched-window)   ) 

(def four-arched
   (translate 0 0.5
   (stretch 0.7 1.2
   (reframe
   (stack
     (translate -0.9 0 thin-arched)
     (translate -0.3 0 thin-arched)
     (translate  0.3 0 thin-arched)
     (translate  0.9 0 thin-arched)
   ))))
)

(def segmented-window
    (stack
      (poly 50 0.65 0 0 engrave)
      (clock-rotate
         8
         [(->SShape engrave [[0 0] [0 0.7]])]
     )
   )
)

(def three-windows
 (let [h 0.9 w 0.3 l -0.8]
     (apply stack 
       (map #(rect (- (* 0.5 %) 0.65) -0.6 w h) (range 3))
     )
   )
)

(def bars
  (let [bar (fn [y] [(->SShape engrave [[-0.4 y] [0.4 y]])]) ]
    (apply stack (map bar [-0.5 -0.2 0.1]))
  )
)


(def grid-windows
  (scale 0.8
       (grid 3 (repeat (rect -0.6 -0.6 1.2 1.2 engrave)))
    )
)


(defn tiles [random time-map]
  (let [tiles-map {
    :street (street random)
    :roof1  #(roof1 random)
    :roof2 (roof2 random)
    :bottom bottom
    :round-window (block round-window random)
    :clock  (block (clock time-map {}) random) 
    :segmented-window (block segmented-window random)
    :three-windows (block three-windows random)
    :grid-windows (block grid-windows random)
    :bars (block bars random)
    :arched-window (block arched-window random)
    :thin-arched (block thin-arched random)
    :four-arched (block_ four-arched)
  }]
    tiles-map))

(defn ok-tops [random time-map]
  (let [tiles-map (tiles random time-map)
        keys (keys tiles-map)
        filtered (into [] 
          (remove #(condp = % :street true :roof1 :roof2 true true false)) 
          keys)]
    filtered))

(defn ok-bottoms [random time-map]
  (let [tiles-map (tiles random time-map)
        keys (keys tiles-map)
        filtered (into [] 
          (remove #(condp = % :street true :bottom true false)) 
          keys)]
    filtered))

(defn random-key [previous random time-map]
  (let [valid-keys (filter #(and (not= % previous)
                                (not= % :roof1)
                                (not= % :street))
                          (keys (tiles random time-map)))]
    (if (empty? valid-keys)
      :bottom
      (.randomNth random valid-keys))))

(defn good-random-key [previous random time-map]
   (condp = previous
      :bottom  :street
      :street (.randomNth random [:roof1 :roof1 :street])
      ; otherwise
      (random-key previous random time-map)))

(defn process-key [k random time-map]
   (let [v (get (tiles random time-map) k)]
     (if (fn? v) 
       (let [result (v)]
         result)
       v)))

(defn make-batch [size dummy random time-map]
   (let [r2 (- size 2)
         tiles-map (tiles random time-map)
         tops (ok-tops random time-map)
         bottoms (ok-bottoms random time-map)
         primo (.randomNth random tops)
         mid (loop [count r2 previous primo build []]
              (if (= 0 count) build
                 (let [rk (good-random-key previous random time-map)]
                     (recur (- count 1) rk (conj build rk)))))
         fin (if (= (last mid) :street) 
               :roof1
               (.randomNth random bottoms))]
       (concat [primo] mid [fin])))

(defn tile-stream [rows random time-map]
   (let [tiles-map (tiles random time-map)]
     (map #(process-key % random time-map)
          (apply concat
                 (iterate #(make-batch rows % random time-map) [])))))

(defn city [size & {:keys [random time-map] :or {random maths/default-random time-map (maths/get-time)}}]
  (stack
   (rect -1 -1 2 2 {:fill (p-color 255)})
   (grid size
                (tile-stream size random time-map)
                )
   ))
