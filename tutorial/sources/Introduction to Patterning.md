Some examples to get you started with Patterning.

### Basic Polygon

The function `poly` creates a polygon.

In its simplest version it takes 3 arguments: *number of sides*, *radius*, and the *style*

`(poly 5 0.7 {:stroke (p-color 0 255 0)})`

This creates a polygon centred at the coordinates 0,0 on the canvas, with a particular number of sides, radius and style. Note that the visible canvas is always considered to be in a coordinate system between -1,-1 and 1,1, with 0,0 at the centre.

The style is a map or dictionary with keys for attributes corresponding to similar properties you might have seen in SVG files or in Processing / P5. Eg

    {:stroke (p-color 255 100 100) :stroke-weight 2 }


Commonly used entries are :stroke, :stroke-weight and :fill. :stroke and :fill are colours defined with either `(p-color r g b)`. Or now a string such as 
`(p-color "ff3399")` 


----
:patterning

(poly 5 0.7
   {:stroke (p-color 0 255 0),
    :fill (p-color 150 150 255), 
    :stroke-weight 4})


----
We can also create the polygon with a different centre. In this case, we put the x and y coordinates **after the number of sides and radius, before the style**. 

----
:patterning

(poly 5 0.7 0.3 0
   {:stroke (p-color 0 255 0),
    :fill (p-color 150 150 255), 
    :stroke-weight 4})


----
### Five red triangles rotated

Take a polygon offset from the centre. And pass it through a *layout* function called clock-rotate

Now let's make a simple pattern.

    (def triangles (clock-rotate 5 (poly 3 0.3 0.5 0.5 {:stroke (p-color 255 100 100) :stroke-weight 2 }) ) )

*poly* creates the triangle

*clock-rotate* is a Layout, a function which takes an existing pattern and returns a new one.

In this case, clock-rotate takes a number, *n*, and a pattern, and makes the new pattern by rotating n copies of the input around the centre point.

----
:patterning

(clock-rotate 5
 (poly 3 0.3 0.5 0.5 
  {:stroke (p-color 255 100 100), :stroke-weight 2}))
----

### Stack the five triangles on a blue pentagon

*Stack* is a simple layout that takes multiple patterns as arguments and stacks them on top of each other.
----
:patterning

(stack
 (poly 5 0.7 0 0 {:stroke (p-color 0 0 255), :stroke-weight 2})
 (clock-rotate 5
  (poly 3 0.3 0.5 0.5 
   {:stroke (p-color 255 100 100), :stroke-weight 2})))


----
### Let's make some grids of these

Note that grid takes a list of the patterns we want to lay out on it.

Here we just use (repeat pattern) to make an infinite lazy list of them.

    (grid 8 (repeat a-pat))

----
:patterning

(grid 6
 (repeat
  (stack
   (poly 5 0.7 0 0 {:stroke (p-color 0 0 255), :stroke-weight 2})
   (clock-rotate 5
    (poly 3 0.3 0.5 0.5 
     {:stroke (p-color 255 100 100), :stroke-weight 2})))))


----

### Chequered Grid

Not massively exciting, instead let's do a chequered pattern.

The checkered-grid takes two streams of patterns and interpolates between them when laying on a grid

    (checkered-grid 8 (repeat pentagon) (repeat triangles))

----
:patterning

(let
 [triangles
  (clock-rotate 5
   (poly 3 0.3 0.5 0.5
    {:stroke (p-color 255 100 100), :stroke-weight 2}))
  pentagon
  (poly 5 0.7 0 0 {:stroke (p-color 0 0 255), :stroke-weight 2})]
 (checkered-grid 6 (repeat pentagon) (repeat triangles)))

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

(clock-rotate 12
 (drunk-line 10 0.1 
  {:stroke (p-color 100 255 100), :stroke-weight 3}))


----


Or mirror them

    (four-mirror (drunk-line 10 0.1 {:stroke (p-color 100 255 100) :stroke-weight 3}))


----
:patterning

(four-mirror
 (drunk-line 10 0.1 
  {:stroke (p-color 100 255 100), :stroke-weight 3}))


----

Or both.

----
:patterning

(->>
 (drunk-line 10 0.1
  {:stroke (p-color 100 255 100), :stroke-weight 3})
 (four-mirror) 
 (clock-rotate 12))

----

And did you want that mixed with our other shapes?

----
:patterning

(let
 [dline
  (drunk-line 10 0.1
   {:stroke (p-color 100 255 100), :stroke-weight 3})
  triangles
  (clock-rotate 5
   (poly 0.5 0.5 0.3 3
    {:stroke (p-color 255 100 100), :stroke-weight 2}))]
 (->>
  (stack dline (scale 0.4 triangles))
  (four-mirror) 
  (clock-rotate 5)))

----

And perhaps on a staggered grid? The half-drop-grid gives us that.

----
:patterning

(let
 [dline
  (drunk-line 10 0.1
   {:stroke (p-color 100 255 100), :stroke-weight 2})
  triangles
  (clock-rotate 5
   (poly 0.5 0.5 0.3 3
    {:stroke (p-color 255 100 100), :stroke-weight 1}))]
 (->>
  (stack dline (scale 0.4 triangles))
  (four-mirror)
  (clock-rotate 5) 
  repeat 
  (half-drop-grid 3)))

----

Maybe bring back a bit of blue, every other.

----
:patterning


(let
 [dline
  (drunk-line 10 0.1
   {:stroke (p-color 100 255 100), :stroke-weight 2})
  triangles
  (clock-rotate 5
   (poly 3 0.3 0.5 0.5
    {:stroke (p-color 255 100 100), :stroke-weight 1}))
  pentagon
  (poly 5 0.7
   {:stroke (p-color 0 0 255),
    :fill (p-color 100 100 255 100),
    :stroke-weight 1})]
 (->>
  (stack dline (scale 0.4 triangles))
  (four-mirror)
  (clock-rotate 5)
  ((fn* [p1__43559#] (cycle [pentagon p1__43559#])))
  (half-drop-grid 6)))

----


Continue to [[Tutorial Part 2]]
