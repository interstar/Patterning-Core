### Welcome to the Turtorial

Patterning comes with a bult in 'turtle' to create lines.

Make sure you have the turtle included in your requirements :

    [patterning.library.turtle :refer [basic-turtle]]

The turtle is set up with a default angle to turn, a default distance to move, and takes a string containing a sequence of moves and turns.

(basic-turtle start-position step initial-angle turning angle program-string leaf-map style)

For example :

    (basic-turtle [-0.4 -0.4] 0.2 0 (/ PI 4) "FFFF+F+F+F+FFFFF+F+F+F+F" 
       {}  {:stroke-weight 2 
            :stroke(p-color 100 50 100)})

In the program string "F" is a move-forward command, "+" is turn-right, "-" is turn left.
----
:patterning-small


(basic-turtle
 [-0.4 -0.4]
 0.2 0 (/ PI 4)
 "FFFF+F+F+F+FFFFF+F+F+F+F"
 {} 
 {:stroke-weight 2, :stroke (p-color 100 50 100)})

----
The result of a turtle's drawing is a pattern that can be processed and combined just like any other in Patterning.
----
:patterning


(four-mirror
 (clock-rotate 6
  (basic-turtle
   [-0.7 -0.4]
   0.1 0 (/ PI 4)
   "FFFF+F+F+F+FFFFF+F+F+F+F"
   {}
   {:stroke-weight 2,
    :stroke (p-color 100 50 100), 
    :fill (p-color 200 150 200)})))

----
### Recursion

The turtle also allows recursive branching.

Commands inside square-brackets are run as a "sub-turtle". When the square-brackets close, the turtle returns to the position before the sub-branch started.

In this example, the branch off to the left is drawn inside the square-brackets, then we return to the original
----
:patterning


(basic-turtle
 [0 0.5]
 0.2
 (/ PI -2)
 (/ PI 4)
 "FFF[-F-F-F]F+F+F"
 {} 
 {:stroke-weight 2, :stroke (p-color 100 50 100)})

----
And, of course, we can recursively branch inside a recursive branch.
----
:patterning


(basic-turtle
 [0 0.5]
 0.2
 (/ PI -2)
 (/ PI 4)
 "FFF[-F[-FF]FF]FF+F"
 {} 
 {:stroke-weight 2, :stroke (p-color 50 150 100)})

----
### L-Systems

If the turtle program-string looks familiar, it's because you've probably seen Lindenmayer (or L) systems.

These are sets of string rewriting rules to "grow" more complex patterns out of basic "seeds".

The simplest L-system is just a collection of rules for substituting one character in a string by another (perhaps more complex) pattern.

For example, rules could be

<blockquote>
<code><pre>A -> BA

B -> C</pre></code>
</blockquote>

Applied once to the string "A" and you get "BA"

Apply the rules again and you get "CBA"

Apply again and you get "CCBA" etc.

In Patterning we can use the built-in L-System functions to create programs that we can then feed to the turtle.

To use the l-systems functions make sure you require them in your program :

    [patterning.library.l_systems :refer [l-system]]

The function l-system takes a vector of substitution rules and returns a new function which, given a seed and a number of iterations, returns the result of applying the rules, that number of times, starting with the seed.

For example :

    (l-system [["F" "F[+F]F[-F][F]"]])

Creates an L-system that replaces all instances of the string "F" with the string "F[+F]F[-F][F]".

Run this l-system on a seed "F" for 4 iterations, and pass the result to the turtle and we can produce a very organic-looking "tree".
----
:patterning


(let
 [grow (l-system [["F" "F[+F]F[-F][F]"]])]
 (basic-turtle
  [0 1] 0.1 
  (/ PI -2) (/ PI 9) 
  (grow 4 "F") 
  {} 
  {:stroke (p-color 0 155 50)}))

----
### Putting a cherry on it

Often we'd like to decorate our trees as the turtle is drawing them.

The leaf-map allows us to add extra patterns that can be attached to certain points in the turtle's parambulations.

In the following adaptation of the previous example, we've added a custom "leaf" to our leaf-map. On discovering the character Z in its program, the turtle calls the attached custom function with three arguments : its x and y position and its orientation. The custom function can do whatever it likes. Here, drawing a small poly to represent a fruit.

We don't want this fruit to appear everywhere in our tree, so we've created a two step rule to add it. The character "Y" in a string is transformed into "Z" and "Z" triggers the custom leaf.
----
:patterning

(let
 [grow (l-system [["F" "F[+F]F[-F]Y[F]"] ["Y" "Z"]])]
 (basic-turtle
  [0 1] 0.1
  (/ PI -2) (/ PI 9)
  (grow 4 "F")
  {\Z
   (fn
    [x y a]
     (poly 8 0.03 x y
        {:fill (p-color 255 0 0) :stroke-weight 0})
    )}
  {:stroke (p-color 0 155 50)}))

 
