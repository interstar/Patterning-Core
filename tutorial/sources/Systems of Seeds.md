### Systems of Seeds


L-System plants in a framed labyrinth. 

This pattern was laser-engraved onto wood, creating a kind of imaginary board-game.

----
:patterning

(def engrave {:stroke-weight 1 :stroke (p-color 0 0 0)})

(defn plant [d s]
    (scale 0.8 (reframe
        (let
          [grow (l-system [["F" s]])]
          (basic-turtle
            [0 1] 0.2 (/ PI -2) (/ PI d) (grow 3 "F")  {} 
            engrave))
        )))

(def plants
  (map plant 
      (cycle (range 3 9)) 
      (iterate 
        (fn [_] 
          (rand-nth ["F[+F]F[-F][FF]"  
                     "F[-F]F[+F][F-F]"
                     "F[-F+F]+FF"]))
       "F[-F+F]+FF")))


(def triangle
  (stack
    [(->SShape engrave [[-1 1] [1 -1]])]
    (nested-stack 
      #(- % 0.2) 4
      [(->SShape engrave
         [ [-1 1] [-1 -1] [1 -1] ] )]
      (repeat engrave)
    )
  )
) 

(defn crdl [_ ]
  (clock-rotate (rand-nth [0 0 5 8 11])
    (drunk-line 10 0.1 engrave ))) 

(defn inner [n]
  (stack
    (square engrave)
    (checked-layout
       n
       plants
       (random-turn-groups
         (repeat triangle))
    )
  )
)



(defn knot []
   (rotate (/ PI 2)
     (stack
       [(->SShape engrave [[-1 0.6] [-0.4 0.6] [0.4 -0.6] [1 -0.6]])]
       [(->SShape engrave [[-1 -0.6] [-0.4 -0.6] [0.4 0.6] [1 0.6]])]
       (poly 8 0.3 1 0 engrave)
     )
   )
)

(stack
  (rect -1 -1 2 2)
  (framed 14
    (repeat [])
    (repeat (knot))
    (scale 0.95
      (inner 9)
    )
  )
)
