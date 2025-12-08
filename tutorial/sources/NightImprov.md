Some improvisations made at a gallery opening event in Portugal.

----
:patterning
(let
 [orange (p-color 129 64 254 )]
 (stack
  (square {:fill (p-color  100 50 80)})
  (checkered-grid
5
   (cycle
    [(clock-rotate
      3
      (drunk-line 20 0.1 {:stroke (p-color 255 100 150) :stroke-weight 4}))
     (clock-rotate
      6
      (drunk-line 20 0.1 {:stroke orange, :stroke-weight 4}))])
   (random-turn-groups
    (repeat
     [(->SShape
       {:fill (p-color 130 180 250), :stroke orange}
       [[-1 -1] [1 -1] [-1 1]])])))))----
:patterning
(let
 [orange (p-color 129 64  254 )]
 (stack
  (square {:fill (p-color  100 50 80)})
  (checkered-grid 7
   (cycle
    [
  
   (clock-rotate 5
    (->> (diamond {:stroke (p-color 100 255 150)}) 
       (translate 0 0.3)
    )
)
    (clock-rotate
      8
      (drunk-line 30 0.1 
         {:stroke (p-color 255 100 150) 
           :stroke-weight 4}))
     (clock-rotate
      6
      (drunk-line 20 0.1 {:stroke orange, :stroke-weight 4}))])
   (random-turn-groups
    (repeat
     (->>
     [(->SShape
       {:fill (p-color 130 180 250), :stroke orange}
       [[-1 -1] [1 -1] [-1 1]])] 
     (scale 0.9)

     (rotate (/ (rand-int 10) 10) )
     (four-mirror)
      (h-mirror)
      (wobble [0.1 0.1])
     )

) ))))

----
:patterning
(defn rand10 [] (/ ( rand-int 10) 10))

(defn rand-rot [p]
  (rotate (rand10) p))

(let
 [orange (p-color 129 64  254 )]
 (stack
  (square {:fill (p-color  100 50 80)})
  (checkered-grid 7
   (cycle
    [
  
   (clock-rotate 5
    (->> (diamond {:stroke (p-color 100 255 150) 
                             :fill (p-color 100 255 200 50)}) 
       (translate -0.4 0.3)
       rand-rot
       
    )
)
    (clock-rotate
      8
      (drunk-line 30 0.1 
         {:stroke (p-color 255 100 150) 
           :stroke-weight 4}))
     (clock-rotate
      6
      (drunk-line 20 0.1 {:stroke orange, :stroke-weight 4}))])
   (random-turn-groups
    (repeat
     (->>
     [(->SShape
       {:fill (p-color 130 180 250), :stroke orange}
       [[-1 -1] [1 -1] [-1 1]])] 
     (scale 0.9)

     (rotate (/ (rand-int 10) 10) )
     (four-mirror)
      (h-mirror)
      (wobble [0.1 0.1])
     )

) ))))----
:patterning
(defn rand-coords []
    (let [rc (fn [] (- (/ (rand-int 40) 20) 1))]
       [(rc) (rc)]
    )
)

(defn rint [start end]
   (+ start (rand-int (- end start)))
)

(def st1 {:stroke (p-color 180 180 220)
             :fill (p-color 100 160 200)
             :stroke-weight 2})

(def st2 {:stroke (p-color 220 220 180)
             :fill (p-color 220 255 180)
             :stroke-weight 2})

(def st3 {:stroke (p-color 255 100 150)
             :fill (p-color 240 80 120)
             :stroke-weight 2})

(defn unfilled [style]
    (dissoc style :fill)
)

(defn addtran [style]
   (conj {:fill (conj (:fill style) 100)} style )
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(->>

(let [[s1 s2 s3] (shuffle [st1 st2 st3])
      gridsize (rint 3 7)
      bgcols [ (p-color 70 70 140 ) (p-color 140 100 180 ) (p-color 70 100 70 ) ]
]

(stack
;; 
;; 
(rect -1 -1 2 2 {:fill (rand-nth bgcols)})

;; Checked one
(checkered-grid gridsize
   (repeat
      (clock-rotate (rint 3 13) (poly (rint 3 10) 0.3 0 0.7 s1))
   )
  (repeat
      (clock-rotate (rint 4 12) 
         (poly 4 0.5 0 0.7 s2)
      )
   )
)
;; checked one ends

;; Grid
(grid gridsize
(repeat
(multiline
(into [] (map (fn [_] (rand-coords)) (range 6)))
 ((rand-nth [unfilled addtran]) s3))
)
)
)
)

  four-round

)----
:patterning
(defn squares [x]
   (map
    #(->>
       (stack
         (rect -1 0 1 1 {:fill (p-color 255 255 0)})
         [(->SShape {} [[-1 0] [1 0]])]
       )
       (scale 0.7)
       (rotate %)
       four-mirror
      )
  (range 0 5 0.1)
))

(defn triangles [x]
   (map 
     (fn [_] 
       (->>
        (poly 3 0.6 0 1.1 {:fill (p-color 205 100 255)})
        (rotate (/ 3.14159 x))
      ))
     (range 0 100)
  )
)

(->>
    (stack 
    (->>
        (grid 4
          (repeat (rect -1 -1 2 2 
            {:fill (p-color 180 250 180)
             :stroke-weight 3
             :stroke (p-color 200 255 200) }))
         
        )
    )
    (checkered-grid 2
      (squares 0)
      (triangles 1)
    ))

  (scale 0.9)
  (rotate (/ 3.15 -6) )
  (h-mirror)
  (four-round)
  (grid 1)

)
