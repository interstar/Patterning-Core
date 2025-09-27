Some examples to get you started with Patterning.

### Basic Polygon
----
:patterning

(poly 0 0 0.7 5
   {:stroke (p-color 0 255 0),
    :fill (p-color 150 150 255), 
    :stroke-weight 4})

----
### Five red triangles in a ring

Note the definition of style. A style is represented as a mapping with keyword keys. For example :

    {:stroke (p-color 255 100 100) :stroke-weight 2 }

Commonly used entries are :stroke, :stroke-weight and :fill. :stroke and :fill are colours defined with (p-color r g b).

Now let's make a simple pattern.

    (def triangles (clock-rotate 5 (poly 0.5 0.5 0.3 3 {:stroke (p-color 255 100 100) :stroke-weight 2 }) ) )

poly creates a regular polygon. Its arguments are x-centre, y-centre, radius, number-of-sides and, optionally, style.

clock-rotate is a Layout, a function which takes an existing pattern and returns a new one.

In this case, clock-rotate takes a number, n, and a pattern, and makes the new pattern by rotating n copies of the input around the centre point.

----
:patterning

(clock-rotate
 5
 (poly
  0.5 
  0.5 
  0.3 
  3 
  {:stroke (p-color 255 100 100), :stroke-weight 2}))
----

### Stack the five triangles on a blue pentagon

----
:patterning

(stack
 (poly 0 0 0.7 5 {:stroke (p-color 0 0 255), :stroke-weight 2})
 (clock-rotate
  5
  (poly
   0.5 
   0.5 
   0.3 
   3 
   {:stroke (p-color 255 100 100), :stroke-weight 2})))


----
### Let's make some grids of these

Note that grid-layout takes a list of the patterns we want to lay out on it.

Here we just use (repeat pattern) to make an infinite lazy list of them.

    (grid-layout 8 (repeat a-pat))

----
:patterning

(grid-layout
 6
 (repeat
  (stack
   (poly 0 0 0.7 5 {:stroke (p-color 0 0 255), :stroke-weight 2})
   (clock-rotate
    5
    (poly
     0.5
     0.5 
     0.3 
     3 
     {:stroke (p-color 255 100 100), :stroke-weight 2})))))


----

### Chequered Grid

Not massively exciting, instead let's do a chequered pattern.

The checked-layout takes two streams of patterns and interpolates between them when laying on a grid

    (checked-layout 8 (repeat pentagon) (repeat triangles))

----
:patterning

(let
 [triangles
  (clock-rotate
   5
   (poly
    0.5
    0.5
    0.3
    3
    {:stroke (p-color 255 100 100), :stroke-weight 2}))
  pentagon
  (poly 0 0 0.7 5 {:stroke (p-color 0 0 255), :stroke-weight 2})]
 (checked-layout 6 (repeat pentagon) (repeat triangles)))

----

### A Drunk Line

OK. change of direction, a ''drunkards walk'' is a series of points each of which is a move in a random direction from the previous one.

Patterning has a function for that, drunk-line, which takes a number of steps, a step-length and an option style

    (drunk-line 10 0.1 {:stroke (p-color 100 255 100) :stroke-weight 3})


----
:patterning

(drunk-line
 10 
 0.1 
 {:stroke (p-color 100 255 100), :stroke-weight 3})

----

Why do we want a random wiggle? Well, they look ''a lot'' cooler when we do some more things to them.

Like clock-rotate them

    (clock-rotate 12 (drunk-line 10 0.1 {:stroke (p-color 100 255 100) :stroke-weight 3}) )


----
:patterning

(clock-rotate
 12
 (drunk-line
  10 
  0.1 
  {:stroke (p-color 100 255 100), :stroke-weight 3}))


----


Or mirror them

    (four-mirror (drunk-line 10 0.1 {:stroke (p-color 100 255 100) :stroke-weight 3}))


----
:patterning

(four-mirror
 (drunk-line
  10 
  0.1 
  {:stroke (p-color 100 255 100), :stroke-weight 3}))


----

Or both.

----
:patterning

(->>
 (drunk-line
  10
  0.1
  {:stroke (p-color 100 255 100), :stroke-weight 3})
 (four-mirror) 
 (clock-rotate 12))

----

And did you want that mixed with our other shapes?

----
:patterning

(let
 [dline
  (drunk-line
   10
   0.1
   {:stroke (p-color 100 255 100), :stroke-weight 3})
  triangles
  (clock-rotate
   5
   (poly
    0.5
    0.5
    0.3
    3
    {:stroke (p-color 255 100 100), :stroke-weight 2}))]
 (->>
  (stack dline (scale 0.4 triangles))
  (four-mirror) 
  (clock-rotate 5)))

----

And perhaps on a staggered grid? The half-drop-grid-layout gives us that.

----
:patterning

(let
 [dline
  (drunk-line
   10
   0.1
   {:stroke (p-color 100 255 100), :stroke-weight 2})
  triangles
  (clock-rotate
   5
   (poly
    0.5
    0.5
    0.3
    3
    {:stroke (p-color 255 100 100), :stroke-weight 1}))]
 (->>
  (stack dline (scale 0.4 triangles))
  (four-mirror)
  (clock-rotate 5) 
  repeat 
  (half-drop-grid-layout 3)))

----

Maybe bring back a bit of blue, every other.

----
:patterning


(let
 [dline
  (drunk-line
   10
   0.1
   {:stroke (p-color 100 255 100), :stroke-weight 2})
  triangles
  (clock-rotate
   5
   (poly
    0.5
    0.5
    0.3
    3
    {:stroke (p-color 255 100 100), :stroke-weight 1}))
  pentagon
  (poly
   0
   0
   0.7
   5
   {:stroke (p-color 0 0 255),
    :fill (p-color 100 100 255 100),
    :stroke-weight 1})]
 (->>
  (stack dline (scale 0.4 triangles))
  (four-mirror)
  (clock-rotate 5)
  ((fn* [p1__43559#] (cycle [pentagon p1__43559#])))
  (half-drop-grid-layout 6)))

----

Continue to [[Tutorial Part 2]]
