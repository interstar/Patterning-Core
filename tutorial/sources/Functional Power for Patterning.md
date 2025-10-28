 

Functional Programming has many tools and tricks for writing elegant code. Here we see how they can be applied to make interesting patterns.

### A round of regular polygons

Let's start with a simple function that makes a ring of n copies of an n-sided regular polygon. 

```
(defn a-round
  ([n] (a-round n (p-color 0) (p-color 255)))
  ([n lc fc]
   (clock-rotate n (poly 0 0.5 0.4 n
       {:stroke lc
        :fill fc 
        :stroke-weight 3}))))
```
----
:patterning

(defn a-round
  ([n] (a-round n (p-color 0) (p-color 255)))
  ([n lc fc]
   (clock-rotate n (poly 0 0.5 0.4 n
       {:stroke lc
        :fill fc 
        :stroke-weight 3}))))
(let
 [lc (p-color 140 220 180) fc (p-color 190 255 200 100)]
 (a-round 8 lc fc))
----
As described previously, we can place this pattern into a grid.
----
:patterning

(defn a-round
  ([n] (a-round n (p-color 0) (p-color 255)))
  ([n lc fc]
   (clock-rotate n (poly 0 0.5 0.4 n
       {:stroke lc
        :fill fc 
        :stroke-weight 3}))))

(let
 [lc (p-color 140 220 180) fc (p-color 190 255 200 100)]
 (grid-layout 4 (repeat (a-round 8 lc fc))))

----

### The nature of `repeat`

The meaning of this code should be self-evident. But what about the call to repeat?

Layouts such as grid-layout take not just a single pattern to fill the grid, but a list of them. The algorithm starts by generating a sequence of positions to place each 'tile' at. And then runs through the list of positions and the list of patterns and places one pattern at one position. The number of positions is finite, so in Clojure we can pass in an infinite, lazily evaluated list of patterns.

repeat is a function that takes a single item and returns an infinite list of them.

But we can also use cycle to turn finite vector of patterns into an infinite list :

    (cycle [(a-round 4) (a-round 8)])

(We also note here that the grid is filled column-wise from left to right.)
----
:patterning
(defn a-round
  ([n] (a-round n (p-color 0) (p-color 255)))
  ([n lc fc]
   (clock-rotate n (poly 0 0.5 0.4 n
       {:stroke lc
        :fill fc 
        :stroke-weight 3}))))

(let
 [lc (p-color 140 220 180) fc (p-color 190 255 200 100)]
 (grid-layout 4 (cycle [(a-round 4 lc fc) (a-round 8 lc fc)])))
----
### Sequence tricks

Because the grid is filled from a sequence, we can use any functions that operate on the sequence abstraction to generate or process the patterns that feed it.

Here we generate our sequence by mapping a-round to a vector of integers.
----
:patterning

(defn a-round
  ([n] (a-round n (p-color 0) (p-color 255)))
  ([n lc fc]
   (clock-rotate n (poly 0 0.5 0.4 n
       {:stroke lc
        :fill fc 
        :stroke-weight 3}))))

(let
 [lc (p-color 140 220 180) fc (p-color 190 255 200 100)]
 (grid-layout 4
  (cycle
   (map (fn* [n] (a-round n lc fc)) [3 4 5 6]))))

----
Or we can generate the sequence using Clojure's iterate to constantly apply a transformation to an initial pattern. Such as shrinking :
----
:patterning
(defn a-round
  ([n] (a-round n (p-color 0) (p-color 255)))
  ([n lc fc]
   (clock-rotate n (poly 0 0.5 0.4 n
       {:stroke lc
        :fill fc 
        :stroke-weight 3}))))

(let
 [lc (p-color 220 140 180) fc (p-color 255 190 200 100)]
 (grid-layout 4 
  (iterate (partial scale 0.9) (a-round 9 lc fc))))
----
We can even add random transformations, such as assigning each pattern an arbitrary colour.
----
:patterning


(defn a-round
  ([n] (a-round n (p-color 0) (p-color 255)))
  ([n lc fc]
   (clock-rotate n (poly 0 0.5 0.4 n
       {:stroke lc
        :fill fc 
        :stroke-weight 3}))))

(let
 [rand-color
  (fn [p]
   (let [c (rand-col)]
    (over-style
     {:fill c, :stroke-weight 2, :stroke (darker-color c)}
     p)))]
 (grid-layout 4 
  (map rand-color (map a-round (cycle [3 4 5 6 7])))))
----
### Clojure mapping can take multiple arguments

Clojure's map function can actually map a function across multiple lists. (map f xs ys) will call (f x y) for each corresponding element of xs and ys.

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
   (poly 0 0 0.6 3 {:stroke-weight 1})
   (horizontal-line 0 {:stroke-weight 2}))]
 (grid-layout 6
  (map
   rand-color
   (map rotate
    (iterate (partial + 0.2) 0)
    (iterate (partial scale 0.97) t)))))

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

(stack
   (square {:fill (p-color 0)})
   (->> 
    (apply stack 
      (take 4 
        (iterate t1 (drunk-line 10 0.1 (rcol)))
      ))
      four-mirror
    )
)
