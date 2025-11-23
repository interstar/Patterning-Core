Some more examples of Patterning patterns.

### Stars and N-Angles

A `nangle` (n-angle) is like a polygon, but joins distant point, eg. (nangle 5 ... ) is a pentangle or pentacle

A `star` is like a polygon but instead of a single radius, takes a vector of two, the inner and outer radius. This allows us to draw a star shape.

The `rotate-tile-set` is a convenience function that takes a number of patterns, and returns a longer vector with each pattern rotated in four orientations. Eg. if I call `(rotate-tile-set s1 s2)` I'll get back a vector of 8 patterns, s1, s1 rotated 90 degrees, s1 rotated 180 degrees, s1 rotated 270 degrees. And then the same for s2.

----
:patterning

(let 
  [s
   (star 5 [0.2 0.6] 
     {:fill (p-color 100 0 100)
      :stroke (p-color 255 90 180)})
   s2
   (nangle 7 0.5 (paint :red :yellow 2))
   tiles (rotate-tile-set s s2)
   ]
  (on-background (p-color 0 0 80) 
   (diamond-grid 5 (cycle tiles))))

----

### The Black Square

This simple black square with some coloured lines illustrates a couple of important points.

Firstly this pattern can exhibit a wide variety of variations based on the several randomized parameters it contains. For this reason it's illustrated with two examples here.

----
:patterning


(defn rcol []
   {:stroke 
   (rand-nth [
     (p-color 127 255 127)
     (p-color 255 )
     (p-color 200 100 200)
     (p-color 200 200 100)

     (p-color 102 255 178)
     (p-color  255 51 255)  ; pink
     (p-color 0 204 204)
     (p-color  255 51 51)
     (p-color   255 255 204   )

   ])
   :stroke-weight 2
   }
)

(defn dark-fill [style]
 (conj style {:fill (darker-color (:stroke style))})
)

(defn t1
   ([p] (t1 p (rcol))) 
   ([p s]
      (->> p
        (stack
          (drunk-line 10 0.2 s)
        )
        four-round
        (rotate (/ (rand-int 10) 10))
     )
   ) 
)

(stack
   (rect -1 -1 2 2 {:fill (p-color 0)})

   (grid 5 
     (repeat (t1 []  {:stroke (p-color 250 180 200)
                            :stroke-weight 2}))
   ) 

   (->>
   (apply stack 
      (take 3
        (iterate t1 (drunk-line 10 0.1 (rcol)))
      ))
    four-mirror
   )
)

----
:patterning


(defn rcol []
   {:stroke 
   (rand-nth [
     (p-color 127 255 127)
     (p-color 255 )
     (p-color 200 100 200)
     (p-color 200 200 100)

     (p-color 102 255 178)
     (p-color  255 51 255)  ; pink
     (p-color 0 204 204)
     (p-color  255 51 51)
     (p-color   255 255 204   )

   ])
   :stroke-weight 2
   }
)

(defn dark-fill [style]
 (conj style {:fill (darker-color (:stroke style))})
)

(defn t1
   ([p] (t1 p (rcol))) 
   ([p s]
      (->> p
        (stack
          (drunk-line 10 0.2 s)
        )
        four-round
        (rotate (/ (rand-int 10) 10))
     )
   ) 
)

(stack
   (rect -1 -1 2 2 {:fill (p-color 0)})

   (grid 5 
     (repeat (t1 []  {:stroke (p-color 250 180 200)
                            :stroke-weight 2}))
   ) 

   (->>
   (apply stack 
      (take 3
        (iterate t1 (drunk-line 10 0.1 (rcol)))
      ))
    four-mirror
   )
)
