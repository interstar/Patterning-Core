Chita is a typical colourful floral pattern from Brazil.

<https://fredericmagazine.com/2020/09/chita-bonita-textile-brazil/>

As part of an exhibition focused on this style, I live-coded some patterns inspired by real examples, using Patterning. Here's one example.
----
:patterning

(defn add-style [new-style {:keys [style points]}] 
   {:points points :style (conj style new-style)})

(defn bez-curve [style points] 
   (add-style {:bezier true} (->SShape style points )))

(defn petal-group  [style dx dy]
  (let [ep [0 0]] [ 
     (bez-curve style [ep [(- dx) (- dy)] 
      [(- (*  -2 dx) dx) (- dy)] ep])]))

(defn petal-pair-group "reflected petals" [style dx dy]
  (let [petal (petal-group style dx dy)] 
     (stack petal ( h-reflect petal))))



(let [blue {:fill (p-color 150 150 255)
		  :stroke (p-color 50 50 255)
		  :stroke-weight 2}

	yellow {:fill (p-color 180 250 50)
			:stroke-weight 1
			:stroke (p-color 150 120 20)}

	green {:fill (p-color 20 100 30)}

	rand-rot 
	#(rotate (* (mod (rand-int 100) 8) (/ PI 4)) %)

	inner (stack
		   (poly 12 0.3 0 0 {:fill (p-color 50 50 220)
							 :stroke-weight 0})
		   (->> (poly 5 0.06 0 0.1 yellow)
				(clock-rotate 5)
				(translate -0.09 -0.07)
				))

	flower 
	(stack
		(clock-rotate 15 (petal-group blue 0.3 0.8 ))
		(let [y-stream
			  (clock-rotate 15 (petal-group yellow 0.3 0.8 ))]
		  (stack (list (nth y-stream 5))
				 (take 2 y-stream)
				 ))
		inner)


	leafy 
	(fn [n]
		(stack
		 (->> (diamond green)
			  (stretch 0.5 0.25)
			  (translate -0.6 0)
			  (clock-rotate 4)
			  (drop 2)
			  (take n))
		 (->> (diamond 
			{:fill (p-color 200 255 200)
			:stroke-weight 0})
			(stretch 0.2 0.1)
			(translate -0.6 0)
			(clock-rotate 4)
			(drop 2)
			(take n))
			flower))

	whites 
	(stack
		(->> 
			(poly 5 0.2 0 0.3
			  {:fill (p-color 255 255 255)
			   :stroke-weight 0})
			(clock-rotate 7))
		(poly 8 0.2 0 0 {:fill (p-color 0 0 200)}))

	small-yellow 
	  (let [all (->> (diamond yellow)
				(stretch 0.4 0.5)
				(translate 0 -0.6 )
				(clock-rotate 5)
				(scale 0.6 ))

	blues (over-style blue all)]
	   (stack
			all
			(list (nth blues 0))
			(list (nth blues 2))))

	leafy-seq (->> 
	   (iterate (fn [x] (+ 1 (mod (rand-int 100) 2))) 0)
		   (map leafy)
		   (map rand-rot))
	]
(stack
 (square {:fill (p-color 255 10 10)})
 (half-drop-grid
  17 (map rand-rot
	   (map rand-nth
		 (repeat [whites []
		   small-yellow small-yellow ]))))

 (half-drop-grid 6 leafy-seq)))

