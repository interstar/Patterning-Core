### A word about colour.

Colours can be defined with the function p-color which takes 1, 3 or 4 arguments. A single argument will give you a shade of grey between black (0) and white (255). Three arguments will get mapped to red, green and blue components. Four arguments will get mapped to red, green, blue and alpha (transparency) components.

A transparent green :

    (p-color 150 255 150 150)

We can over-ride or supplement the styling of an existing group using the over-style function. Here we're going to supplement an already bright green clock-rated drunk-line with a transparent green fill. Note that the drunk-line is not closed but can still be filled.

And to see the transparency we can stack it on top of a basic grid of squares. (This may not work in all browsers.)

----
:patterning

(stack
 (square {:fill (p-color 0)})
 (grid-layout
  10
  (repeat
   (square {:stroke-weight 2, :stroke (p-color 100 50 100)})))
 (over-style
  {:fill (p-color 150 255 150 150)}
  (clock-rotate
   12
   (drunk-line 20 0.05 
    {:stroke (p-color 100 255 100), :stroke-weight 3}))))
