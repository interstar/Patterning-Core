## Pixel Art in Patterning

"Pixel art" images are low resolution, made with coloured blocks.

We now have the `pixel` function in Patterning to help make these images.

`pixel` takes three arguments. The width/height of the grid in pixels. (All pixel grids are square). A vector of numbers representing the colour of each pixel. And a map from those numbers to a colour.

----
:patterning

(set-standard-colors)

(def invader1
(pixel 13
  [
   1 1 1 1 1 1 1 1 1 1 1 1 1
   1 1 1 1 1 1 1 1 1 1 1 1 1
   1 1 1 2 1 1 1 1 1 2 1 1 1
   1 1 1 1 2 1 1 1 2 1 1 1 1
   1 1 1 2 2 2 2 2 2 2 1 1 1
   1 1 2 2 1 2 2 2 1 2 2 1 1
   1 2 2 2 2 2 2 2 2 2 2 2 1
   1 2 1 2 2 2 2 2 2 2 1 2 1
   1 1 1 2 1 1 1 1 1 2 1 1 1
   1 1 1 1 2 2 1 2 2 1 1 1 1
   1 1 1 1 1 1 1 1 1 1 1 1 1
   1 1 1 1 1 1 1 1 1 1 1 1 1
   1 1 1 1 1 1 1 1 1 1 1 1 1
   ]
  {1 black 2 green}
 )
)

(def invader2
(pixel 12
  [
   1 1 1 1 1 1 1 1 1 1 1 1 
   1 1 1 1 1 1 1 1 1 1 1 1
   1 1 1 1 1 2 2 1 1 1 1 1
   1 1 1 1 2 2 2 2 1 1 1 1 
   1 1 1 2 2 2 2 2 2 1 1 1 
   1 1 2 2 1 2 2 1 2 2 1 1
   1 1 2 2 2 2 2 2 2 2 1 1
   1 1 1 1 2 1 1 2 1 1 1 1 
   1 1 1 2 1 2 2 1 2 1 1 1 
   1 1 2 1 2 1 1 2 1 2 1 1
   1 1 1 1 1 1 1 1 1 1 1 1
   1 1 1 1 1 1 1 1 1 1 1 1 
   ]
  {1 black 2 green}
 )
)


(grid 6 (cycle [invader1 invader2]))
