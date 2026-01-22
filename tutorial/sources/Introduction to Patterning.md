Some examples to get you started with Patterning.

### Basic Polygon

The function `poly` creates a polygon.

In its simplest version it takes 3 arguments: *number of sides*, *radius*, and the *style*


    (poly 5 0.7 style)


This creates a polygon centred at the coordinates 0,0 on the canvas, with a particular number of sides, radius and style. Note that the visible canvas is always considered to be in a coordinate system between -1,-1 and 1,1, with 0,0 at the centre.

The style is a map or dictionary with keys for attributes corresponding to similar properties you might have seen in SVG files or in Processing / P5. Eg

    {:stroke (p-color 255 100 100) :stroke-weight 2 }

But we can also use the `(paint stroke fill stroke-weight)` function as a shorter way to create this map. (See [Color](Color.html)). 

Commonly used entries are :stroke, :stroke-weight and :fill. :stroke and :fill are colours defined with either `(p-color r g b)`. Or now a hex string such as `(hex-color "ff3399")` 


----
:patterning-small

(poly 5 0.7 
   (paint (p-color 0 255 0) (p-color 150 150 255) 4)
)

----
We can also create the polygon with a different centre. In this case, we put the x and y coordinates **after the number of sides and radius, before the style**. 

    (poly 5 0.7 0.3 0 ...)


----
### Five red triangles rotated

Now let's make a simple pattern.

First, we actually have a palette of standard colours for Patterning now. These are no enabled by default, but you can define them with `(set-standard-colors)`. Calling this will make our code less verbose.


Now, we'll
Take a polygon offset from the centre. And pass it through a *layout* function called clock-rotate

    (clock-rotate 5 (poly 3 0.3 0 -0.6 {:stroke red :stroke-weight 2 }))

`poly` creates the triangle

`clock-rotate` is a Layout, a function which takes an existing pattern and returns a new one.

In this case, clock-rotate takes a number, *n*, and a pattern, and makes the new pattern by rotating n copies of the input around the centre point.

Note that we can also use Clojure's threading macros to organise patterns more intuitively. So instead of nesting the call to poly inside the arguments to the clock-rotate we write this
----
:patterning-small

(set-standard-colors)

(->>
  (poly 3 0.3 0 -0.6
    (paint red :nofill 2))
  (clock-rotate 5))

----
### Stack the five triangles on a blue pentagon

*Stack* is a simple layout that takes multiple patterns as arguments and stacks them on top of each other.
----
:patterning

(set-standard-colors)

(stack
 (poly 5 0.7 0 0 
   {:stroke blue :stroke-weight 2})
 (->>
   (poly 3 0.3 0 -0.6
     (paint red :nofill 2))
   (clock-rotate 5))
)


----
### Let's make some grids of these

`grid` is a layout which organises patterns into a grid. It takes a number and a pattern and makes an nXn grid of that pattern.

----
:patterning

(set-standard-colors)

(grid 6
  (stack
   (poly 5 0.7  {:stroke blue :stroke-weight 2})
   (clock-rotate 5
    (poly 3 0.3 0 -0.6 
     {:stroke red :stroke-weight 2}))))


----

### Chequered Grid

We can also do a chequered pattern.

The `checkered-grid` takes two streams of patterns and interpolates between them when laying on a grid

    (checkered-grid 8 (repeat pentagon) (repeat triangles))

----
:patterning-small

(set-standard-colors)

(let
 [t-style {:stroke brown :stroke-weight 2}
  triangles (clock-rotate 5 (poly 3 0.3 0 -0.6 t-style))
  p-style {:stroke teal :stroke-weight 2}
  pentagon
  (poly 5 0.7 0 0 p-style)]
 (checkered-grid 5 (repeat pentagon) (repeat triangles)))
----
### A Drunk Line

OK. change of direction, a ''drunkards walk'' is a series of points each of which is a move in a random direction from the previous one.

Patterning has a function for that, drunk-line, which takes a number of steps, a step-length and an option style

    (drunk-line 10 0.1 {:stroke purple :stroke-weight 3})


----
:patterning-small

(set-standard-colors)

(drunk-line
 10 0.1 
 {:stroke purple :stroke-weight 3})

----

Why do we want a random wiggle? Well, they look ''a lot'' cooler when we do some more things to them.

Like clock-rotate them


----
:patterning

(->> (drunk-line 10 0.1 {:stroke (p-color 100 255 100) :stroke-weight 3}) (clock-rotate 12))

----


Or mirror them

    (four-mirror (drunk-line 10 0.1 {:stroke (p-color 100 255 100) :stroke-weight 3}))


----
:patterning

(->>
 (drunk-line 10 0.1 
  {:stroke (p-color 100 255 100), :stroke-weight 3})
  (four-mirror))


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
   (poly 3 0.3 0.5 0.5
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
   (poly 3 0.3 0.5 0.5
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
  ((fn* [p1] (cycle [pentagon p1])))
  (half-drop-grid 6)))

----


Continue to [[Tutorial Part 2]]
