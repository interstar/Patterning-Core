### Door

Inspired by the panels of a door

----
:patterning
(def backblue (p-color 132 207 242) )

(def bgcol {:fill (darker-color backblue) :stroke-weight 0
                 :stroke backblue})
(def gold (p-color 248 235 193))
(def gcol {:fill gold :stroke-weight 3 :stroke (darker-color gold)})

(defn shrink [n styles f pat]
    (take n  (nested-stack  styles pat f ) )
)

(defn shift-to-shrink [n dx dy styles f p] 
     (->> p (translate dx dy) (shrink n styles f) (translate (- 0 dx) (- 0 dy)))
)



(def corner 
   (stack 
      (rect -1 -1 2 2 bgcol)
      (shift-to-shrink 4 0.15 0.4 (repeat gcol)
            #(- %  0.1)
             [(->SShape gcol 
                  [[-0.7 -0.8] [0.7 -0.8] [0.7 -0.5] [-0.7 0.5]  [-0.7 -0.8] ] 
             )]
      )
    
   )
)

(def darker-gold (darker-color gold))
(def dgcol {:fill darker-gold :stroke-weight 5
                   :stroke (p-color 0 0 255)})

(stretch 0.6 1
  (stack 
     (four-mirror corner)
     (->> [(->SShape gcol [[-1 0] [0 -1] [1 0] [0 1] [-1 0] ])]
               (stretch 0.8 0.55)  
               (shift-to-shrink 3 0 0 (repeat gcol) #(* % 0.8))
     )
  )
)
