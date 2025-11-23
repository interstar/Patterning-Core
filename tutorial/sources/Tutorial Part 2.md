### Bezier Curves

What if you need something smoother than a line of straight segments?

Bezier curves? We got 'em

----
:patterning

(bez-curve
 [[0 1] [-1.5 -0.5] [-0.5 -1.5] [0 0]]
 {:stroke (p-color 255 128 64), :stroke-weight 4})

----

Perhaps we can use this to make smoothly curved corners for a frame

Note : `framed` is a Layout that takes three arguments :

* a list of corner pieces (which it reflects appropriately),
* a list of edge pieces (which it rotates appropriately)
* and a single centre (NOT a list, just a single group which it fills the middle.)

----
:patterning

(let
 [orange
  {:stroke (p-color 255 128 64), :stroke-weight 2}
  blue
  {:stroke (p-color 100 100 200), :stroke-weight 2}]
 (framed 9
  (repeat
   (bez-curve
    [[0.9 -0.9] [-1.4 -0.9] [-0.9 -1.4] [-0.9 0.9]]
    orange))
  (repeat [{:style orange, :points [[-0.9 -1] [-0.9 1]]}])
  (->> (drunk-line 10 0.1 blue) (clock-rotate 7))))

----
Framed can be used to generate various bordered patterns

----
:patterning

(let
 [corner
  (stack
   (square {:fill (p-color 255 0 0 100)})
   (poly 4 0.9 0 0 {:fill (p-color 240 100 240)}))
  edge
  (stack
   (square {:fill (p-color 0 0 255)})
   (poly 8 0.5 0 0 {:fill (p-color 150 150 255)}))
  centre
  (poly 30 0.9 0 0 {:fill (p-color 150 255 140)})]
 (framed 7 (repeat corner) (repeat edge) centre))
----
Which can be tiled together with grid

----
:patterning

(let
 [corner
  (stack
   (square {:fill (p-color 255 0 0 100)})
   (poly 4 0.9 0 0 {:fill (p-color 240 100 240)}))
  edge
  (stack
   (square {:fill (p-color 0 0 255)})
   (poly 8 0.5 0 0 {:fill (p-color 150 150 255)}))
  centre
  (poly 30 0.9 0 0 {:fill (p-color 150 255 140)})]
 (grid 5 
  (repeat (framed 7 (repeat corner) (repeat edge) centre))))

----
Random transformations

Note how we fill grids from lazy, infinite lists of shapes made with repeat.

We can map other functions to these streams, for example to randomly rotate them.

----
:patterning

(let
 [orange (p-color 254 129 64)]
 (stack
  (square {:fill (p-color 50 80 100)})
  (grid 10
   (random-turn-groups
    (repeat
     [(->SShape
       {:fill (p-color 230 180 90), :stroke orange}
       [[-1 -1] [1 -1] [-1 1]])])))))

----
We can increase the complexity of the pattern, mixing streams of various sub-patterns.

For more information on transforming streams of sub-patterns with functional programming tools, see [[Functional Power for Patterning]]

----
:patterning

(let
 [orange (p-color 254 129 64)]
 (stack
  (square {:fill (p-color 50 80 100)})
  (checkered-grid 18
   (cycle
    [(clock-rotate 8
      (drunk-line 10 0.1 {:stroke orange, :stroke-weight 1}))
     (clock-rotate 5
      (drunk-line 10 0.1 {:stroke orange, :stroke-weight 2}))])
   (random-turn-groups
    (repeat
     [(->SShape
       {:fill (p-color 230 180 90), :stroke orange}
       [[-1 -1] [1 -1] [-1 1]])])))))

----

Continue to [[Color]]