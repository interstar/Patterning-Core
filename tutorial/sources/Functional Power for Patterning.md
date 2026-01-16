 

Functional Programming has many tools and tricks for writing elegant code. Here we see how they can be applied to make interesting patterns.

### A round of regular polygons

Let's start with a simple function that makes a ring of n copies of an n-sided regular polygon. 

```
(defn a-round [n style]
   (->>
     (poly n 0.4 0 0.5 style)
     (clock-rotate n)
   ))
```
----
:patterning-small

(defn a-round [n style]
   (->>
     (poly n 0.4 0 0.5 style)
     (clock-rotate n)))
   
(a-round 8 (paint (p-color 140 220 180) (p-color 190 255 200 100) 3))

----
As described previously, we can place this pattern into a grid.

----
:patterning-small

(defn a-round [n style]
   (->>
     (poly n 0.4 0 0.5 style)
     (clock-rotate n)))


(->> 
  (a-round 8 
   (paint (p-color 140 220 180) (p-color 190 255 200 100)))
   (grid 4)
)


----

### Sequences of Patterns

In the previous example we are sending a single pattern as argument to grid. 

But grid (and similar layouts) can also accept sequences of patterns to fill the grid with.

The algorithm starts by generating a sequence of positions to place each 'tile' at. And then runs through the list of positions and the list of patterns and places one pattern at one position. The number of positions is finite, so in Clojure we can pass in an infinite, lazily evaluated list of patterns.

`repeat` is a function that takes a single item and returns an infinite list of them. So it's OK to write 

    (grid 4 (repeat my-pattern))

But we can also use Clojure's `cycle` to turn finite vector of patterns into an infinite list :

    (cycle [(a-round 4) (a-round 8)])

(We also note here that the grid is filled column-wise from left to right.)
----
:patterning-small
(defn a-round [n style]
   (->>
     (poly n 0.4 0 0.5 style)
     (clock-rotate n)))
     
(let
 [style (paint (p-color 140 220 180) (p-color 190 255 200 100))]
 (grid 4 (cycle [(a-round 4 style) (a-round 8 style)])))
----
### Sequence tricks

Because the grid is filled from a sequence, we can use any functions that operate on the sequence abstraction to generate or process the patterns that feed it.

Here we generate our sequence by mapping a-round to a vector of integers.
----
:patterning

(defn a-round [n style]
   (->>
     (poly n 0.4 0 0.5 style)
     (clock-rotate n)))


(let
 [style (paint (p-color 140 220 180) (p-color 190 255 200 100) 3)]
 (grid 4
  (cycle
   (map (fn [n] (a-round n style)) [3 4 5 6]))))

----
Or we can generate the sequence using Clojure's `iterate` to constantly apply a transformation to an initial pattern. Such as shrinking :
----
:patterning
(defn a-round [n style]
   (->>
     (poly n 0.4 0 0.5 style)
     (clock-rotate n)))

(let
 [style (paint (p-color 220 140 180) (p-color 255 190 200 100) 2)]
 (grid 4 
  (iterate (partial scale 0.9) (a-round 9 style))))
----
We can even add random transformations, such as assigning each pattern an arbitrary colour.
----
:patterning

(defn a-round [n]
   (->>
     (poly n 0.4 0 0.5 {})
     (clock-rotate n)))

(let
 [rand-color
  (fn [p]
   (let [c (rand-col)]
    (over-style
     {:fill c :stroke-weight 2, :stroke (darker-color c)}
     p)))]
 (grid 4 
  (map rand-color (map a-round (cycle [3 4 5 6 7])))))
----
### Clojure mapping can take multiple arguments

Clojure's map function can actually map a function across multiple lists. `(map f xs ys)` will call (f x y) for each corresponding element of xs and ys.

We can use this to apply evolving transforms to a stream of evolving patterns. For example, this rotating / shrinking trianlgle.

----
:patterning


(let
 [rand-color
  (fn [p]
   (let [c (rand-col)]
    (over-style
     {:fill c, :stroke-weight 2, :stroke (darker-color c)}
     p)))
  t
  (stack
   (poly 3 0.6 0 0 {:stroke-weight 1})
   (horizontal-line 0 {:stroke-weight 2}))]
 (grid 6
  (map
   rand-color
   (map rotate
    (iterate (partial + 0.2) 0)
    (iterate (partial scale 0.97) t)))))

----
### iterate-stack

Outside of grids, we often find ourselves wanting to make a pattern by stacking the result of iterating a function on them.

Because the result of iterating is a sequence. And stack takes multiple arguments, we would typically use Clojure's `apply` to turn the sequence into an argument list. 

This ends up looking something like this.

    (apply stack (take 10 (iterate some-fn my-pattern)))
    
Which is hard to remember. Especially to take a finite number of patterns from the infinite output of iterate.

For this reason we have a `iterate-stack` in Patterning to make this more convenient.

    (iterate-stack n some-fn my-pattern)

In this example, we make a triangle in the top left corner. And then use iterate to repeatedly translate it downwards and to the right.
----
:patterning-small

(iterate-stack
   6 (partial translate 0.2 0.2)
   (poly 3 0.3 -0.6 -0.6 ))

----
We can apply any transformations in the function.

----
:patterning-small

(iterate-stack
  35 #(->> % (scale 0.85) (stretch 0.95 1) (rotate 0.2))
  (poly 4 1 (paint (p-color 100) (p-color 0 0 0 20))))




----
### The Black Square

In this pattern we define a function, p1 whose job is to take an existing pattern and stack a drunk-line of a random colour over it. Then apply four-round to the whole thing. And then rotate it by a random offset.
 
 
When we apply Clojure's iterate function to this, we build up and increasingly complex pattern of overlayed and rotated rotation-patterns of drunk-lines. 

Finally we four-mirror the whole thing to give a pleasing symmetry. If you try this in the workbench you'll notice that there is a lot of randomness here so every version is very different.

----
:patterning

(defn rcol []
   "Return a style with colour chosen randomly from our palette"
   {:stroke 
   (rand-nth [
     (p-color 127 255 127)
     (p-color 255 )
     (p-color 200 100 200)
     (p-color 200 200 100)
     
     (p-color 102 255 178)
     (p-color 255 51 255)  ; pink
     (p-color 0 204 204)
     (p-color 255 51 51)
     (p-color 255 255 204   )

   ])
   :stroke-weight 2
   }
)


(defn t1
   ([p] (t1 p (rcol))) 
   ([p style]
      (->> p
        (stack
          (drunk-line 10 0.2 style)
        )
        four-round
        (rotate (/ (rand-int 10) 10))
     )
   ) 
)

(on-background (p-color 0)
   (->> 
    (apply stack 
      (take 4 
        (iterate t1 (drunk-line 10 0.1 (rcol)))
      ))
      four-mirror
    )
)
