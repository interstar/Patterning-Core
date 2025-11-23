## Welcome to Patterning

Patterning is a Clojure / ClojureScript library to make certain kinds of decorative patterns. It's opinionated about how to produce patterns and about the kinds of patterns to produce.

But it's the easiest way to generate what it does.



----
### Get Patterning

The easiest way to play with Patterning is now directly in the [workbench](https://alchemyislands.com/assets/patterning-tutorial/workbench/index.html).

For other ways of getting and using Patterning, see [[Getting Started with Patterning]]

Once you have either a an online workspace, or the library available from within your programming environment, you can start to play.
----
### Quick Example

----
:patterning

(let
 [triangles
  (clock-rotate 5 ; we are making 5 copies of a pattern in a circle
   (poly
    3 ; the number of sides of this poly, it's a triangle
    0.3 ; radius
    0.5 0.5 ; centre of the polygon
    {:stroke (p-color 255 100 100), :stroke-weight 2})) ; the colour.
  pentagon
  (poly 5 0.7 0 0 {:stroke (p-color 0 0 255), :stroke-weight 2})] ; this makes a blue pentagon
 (checkered-grid 6 (repeat pentagon) (repeat triangles))) ; we layout a "checker-board" of alternating pentagons and bouquets of triangles

----

* [[Introduction to Patterning]] - Start Here
* [[Tutorial Part 2]] - More standard operations.
* [[Functional Power for Patterning]] - use FP techniques to build complex patterns.
* [[The Turtorial]] - a Turtle and an L-System which work well together.
* [[Douat]] a tiling technique inspired by Truchet and Douat's research
* [[Color]] - a word on colour

Other Patterns

* [[Kira]] Patterns
* [[Chita]] - Brazilian floral kitsch
* [[Systems of Seeds]] - L-System plants in a framed labyrinth. 
* [[Door]] - Pattern inspired by a door
* [[The City we Invent]] - A stylised city pattern.
* [[Pelican]] - Simon Willison's infamous "Pelican on a Bicycle"
