
## Douat

Sebastien Truchet is credited with formalising attempts to create complex patterns from very simple square tiles divided into a single black and white triangle.

Dominique Douat created a notation consisting of A, B, C and D for each of the 4 standard rotations of such tiles. This made it possible to define a complex pattern simply in terms of a string using this alphabet.

You can learn more here : <https://alpaca.pubpub.org/pub/b0ikfd74/release/1> and <https://ericlord.neocities.org/ericsfiles/pdfs/68.pdf>

I've wanted to get these ideas into Patterning for a while. But while watching a new talk (<https://2025.algorithmicpattern.org/proceedings/QBK79E/paper.html>) by Rocco Lorenzo Modugno, Marco Buiani and Amedeo Bonini at the Alpaca2025 festival, I had a new idea how to do it.

I'm defining a Douat as a data-structure that contains a pattern and a list of transformation functions. And I define A, B, C and D not as simply rotated versions of a basic pattern, but as functions which perform the rotation on a pattern. Or rather on a Douat that contains the pattern. When we apply the functions to the Douat, if the list inside the Douat has built up fewer than 4 functions, we simply append the new function to this internal list. But when we hit 4, we make four copies of the inner pattern, each with one of the functions in the list. We then create a new pattern with one of these transformations in each quadrant, and the process restarts.

What this means is that we can write code like this to make a simple 4 tile pattern with each of the A, B, C and D rotations in each corner. It's similar to the Patterning function `(four-round)`


----
:patterning-small
(->
  (Douat 
   (stack
    (square {:fill (p-color 0)})
    (truchet
     {:fill (p-color 255) 
      :stroke-weight 1
      :stroke (p-color 255)})
    ))
 
   A B C D


  (:p)

  )

----

However we can go further. By continuing to add rows of four letters to our pattern we can build up larger recursive structures. Two rows of 4 letters gives us a 16x16 grid where each 4x4 quadrant is a transformation of the original 4x4 grid.  
----
:patterning

(->
  (Douat 
   (stack
    (square {:fill (p-color 0 0 0)})
    (truchet
     {:fill (p-color 255) 
      :stroke-weight 1
      :stroke (p-color 255 )})
    ))
 
   A B D C
   A C C A
   
  (:p)

  )

-----

And the process can continue as much as we like. Every new row of 4 letters quadrouples the previous pattern. With 12 letters we get a 64x64 grid.


Furthermore we can add more transformations. As well as A (the identity), B (rotate 90 degrees), C (rotate 180) and D (rotate 270), we can have E (horizontal reflection), F (vertical reflection), and G (both horizontal and vertical reflections). And even R which represents choosing randomly from one of the 4 rotations. And Q which is a random choice from one of the rotations or reflections. Finally this is standard Patterning with its focus on composability. So there's no restriction on what type of pattern we can treat this way. We can change the shape of the initial triangle tile.

----
:patterning

(->
  (Douat 
   (stack
    (square {:fill (p-color 50 150 120)})
    [(->SShape
       {:fill (p-color 0) 
        :stroke-weight 2 
        :stroke (p-color 0 255 255)}
       [[1 1] [-1 0.5] [-1 -1] [1 1] ])]
    ))
 
   A B E F 
   
   A C D A
   G F F G
   A E F G
   
  (:p)

  )



----
:patterning

(->
  (Douat 
   (stack
    (square {:fill (p-color 90 0 0)})
    [(->SShape
       {:fill (p-color 0) 
        :stroke-weight 1
        :stroke (p-color 255 255 0)}
       [[1 1] [-1 1] [-1 -1] [1 0]])]
    ))
 
   A E E A
   A B C D
   A E E A
   B E E B
  (:p)

  )
  
----
### Combine Douat with other Patterns

----
:patterning

(let 
  [tri
   (poly 3 0.7 0 0.2
     {:fill (p-color 250 200 100)
      :stroke (p-color 250 50 150)})
   tri2
   (poly 3 0.7 0 0.2
     {:fill (p-color 50 180 250)
      :stroke (p-color 250 50 150)})

    hex
   (poly 6 0.7 0 0 
     {:fill (p-color 100 150 100) 
      :stroke (p-color 200 255 50) 
      })]
	(->
      (Douat
	   (stack 
   		(square {:fill (p-color 80 0 0) 
                 :stroke-weight 1})
   		(grid 3 (cycle [tri2 hex tri]))
       ))
       A B D D
       A E G F
	   :p
       )
    )
