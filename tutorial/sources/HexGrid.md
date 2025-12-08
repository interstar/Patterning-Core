## Hexagonal Grid

It turns out that hexagonal grids are way more complicated than I imagined.

For a start, tiles on a hexagonal grid have 6 sides. And any "connection" enterering on any side can either be a dead-end or link to any of the 5 other sides. That gives a lot more possible combinations of patterns than you intuitively think of with a square grid.

Patterning now has two functions to support making hexagonal grids patterns.

The first is the basic `hex-grid` to lay hexagonal tiles out.

----
:patterning

(def blue (paint :blue (p-color 100 100 255)))

(hex-grid 4 (poly 6 1 blue))

----

Note that hex spacing is a little bit larger than the size for a hex poly with radius 1.

The second function Patterning provides is `hex-side-center`. This is a convenience function to calculate the centre point of one of the sides of a hexagon of a particular radius.

For example, here we draw another polygon at this point for the 1st, 3rd and 5th sides of a hexagon. Note that `hex-side-center`returns a point, ie a vector containing the x and y coordinates. That's why we define the `pip` function here to take a single argument with the two coordinates. We can use Clojure's destructuring assignment to unpack the x and y from the vector in the argument list. Ie. `(defn pip [ [x y]] (poly 15 0.05 x y pink))`

----
:patterning

(def blue (paint :blue (p-color 100 100 255)))
(def pink (paint :magenta (p-color 255 100 255)))

(defn pip [ [x y]] (poly 15 0.05 x y pink)) 

(stack
  (poly 6 1 blue)
  (pip (hex-side-center 0.8 1))
  (pip (hex-side-center 0.8 3))
  (pip (hex-side-center 0.8 5))
 
)
----
`hex-side-center` helps us create hexagonal tiles with patterns that join the centres of different edges. This allows interesting emergent Truchet-like patterns to emerge between the hexagonal tiles.

----
:patterning 

(def blue (paint :blue (p-color 100 100 255)))
 
(def yellow (paint :yellow :nofill 3))

(defn pip [[x y]] 
  (poly 15 0.05 x y (paint (p-color 255 255 0) (p-color 200 200 100) 2))) 

(def tile
(stack
  (poly 6 1.1 blue)
  (bez-curve [(hex-side-center 0.9 1) 
              [0 0] [0.1 0.1] 
              (hex-side-center 0.9 2)]
             yellow)
   (bez-curve [(hex-side-center 0.9 4) 
              [0 0] [0.1 0.1] 
              (hex-side-center 0.9 6)]
             yellow)
 
   (multiline [(hex-side-center 0.9 3) 
               (hex-side-center 0.7 3)] yellow)
   (multiline [(hex-side-center 0.9 5)
               (hex-side-center 0.7 5)] yellow)
 
  (pip (hex-side-center 0.7 3))
  (pip (hex-side-center 0.7 5))
   
 ))

(hex-grid 7 (map #(rotate (rand-nth [0 d60 (* 2 d60)]) %) (repeat tile)))

----
We are already getting a lot of complexity from a single tile rotated in different ways.

But we can also add more types of types for more complex patterns.

However it's worth writing some helper functions to generate them, as in the `make-a-tile` function here.

----
:patterning


(def yellow (paint :yellow :nofill 2))
(def l1 (multiline [[1 0] [0.1 0]] yellow))
(def blue (paint :blue (p-color 50 50 255)))
(def hex    
  (poly 6 1.1 0 0 blue))

(defn bob [[x y]] (poly 10 0.05 x y yellow))
(defn bob-at [n] 
  (stack
   (multiline [(hex-side-center 0.7 n)
               (hex-side-center 0.9 n)]
              yellow)
   (bob (hex-side-center 0.7 n)))) 

(def hextile1
  (stack hex
    (apply stack (map bob-at [1 2 3 4 5 6]))))

(def hextile2 
  (stack hex
    (multiline [(hex-side-center 0.9 1) 
                (hex-side-center 0.9 4)] yellow)
    (multiline [(hex-side-center 0.9 2) 
                (hex-side-center 0.9 5)] yellow)
    (multiline [(hex-side-center 0.9 3) 
                (hex-side-center 0.9 6)] yellow)))

(def hextile3
  (stack hex
    (bez-curve [(hex-side-center 0.9 1) [0 0] [0.1 0.1]
              (hex-side-center 0.9 2)] yellow)
    (bez-curve [(hex-side-center 0.9 3) [0 0] [0.1 0.1]
                (hex-side-center 0.9 5)] yellow)
    (bob-at 4)
    (bob-at 6)
  )
)

(defn make-a-tile [back col n da xs]
  (let [pair
        (fn [[a b type]]
				(condp = type
                  :bob
                  (stack (bob-at a) (bob-at b))
                  :line
                  (multiline 
                   [(hex-side-center 0.9 a)
                    (hex-side-center 0.9 b)] yellow)
                  :curve
                  (bez-curve 
                   [(hex-side-center 0.9 a)
                    [0 0] [0.1 0.1]
                    (hex-side-center 0.9 b)] yellow)
                    )	
          )
         tile
          (stack back
    	    (apply stack (map pair xs)))]
    (take n (iterate #(rotate da %) tile))
))

(def all-tiles 
  (concat [hextile1 hextile2]
      (make-a-tile
       hex yellow 2 d60
       [[1 2 :bob] [3 4 :bob] [5 6 :bob]])
      (make-a-tile
       hex yellow 2 d60
       [[1 4 :line] [2 5 :line] [3 6 :line]])

          
      (make-a-tile 
       hex yellow 6 d60 
       [[1 2 :curve]
        [3 4 :curve]
        [5 6 :bob]])
      (make-a-tile
       hex yellow 6 d60
       [[1 3 :curve]
        [4 5 :bob]
        [6 2 :curve]
        ]
       )
      (make-a-tile
       hex yellow 6 d60
       [[1 3 :curve]
        [6 4 :curve]
        [5 2 :bob]])
      (make-a-tile
       hex yellow 6 d60
       [[1 3 :curve]
        [4 6 :curve]
        [2 5 :line]])
 ))

(on-background (p-color 0)
  (hex-grid 9
     (iterate (fn [_] (rand-nth all-tiles)) (first all-tiles) )
  )
)


