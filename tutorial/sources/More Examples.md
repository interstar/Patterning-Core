Some more examples of Patterning patterns.

### The Black Square

This simple black square with some coloured lines illustrates a couple of important points.

Firstly this pattern can exhibit a wide variety of variations based on the several randomized parameters it contains. For this reason it's illustrated with two examples here.

----
:patterning


(defn rcol []
   {:stroke 
   (rand-nth [
     (p-color 127 255 127)
     (p-color 255 )
     (p-color 200 100 200)
     (p-color 200 200 100)

     (p-color 102 255 178)
     (p-color  255 51 255)  ; pink
     (p-color 0 204 204)
     (p-color  255 51 51)
     (p-color   255 255 204   )

   ])
   :stroke-weight 2
   }
)

(defn dark-fill [style]
 (conj style {:fill (darker-color (:stroke style))})
)

(defn t1
   ([p] (t1 p (rcol))) 
   ([p s]
      (->> p
        (stack
          (drunk-line 10 0.2 s)
        )
        four-round
        (rotate (/ (rand-int 10) 10))
     )
   ) 
)

(stack
   (rect -1 -1 2 2 {:fill (p-color 0)})

   (grid-layout 5 
     (repeat (t1 []  {:stroke (p-color 250 180 200)
                            :stroke-weight 2}))
   ) 

   (->>
   (apply stack 
      (take 3
        (iterate t1 (drunk-line 10 0.1 (rcol)))
      ))
    four-mirror
   )
)

----
:patterning


(defn rcol []
   {:stroke 
   (rand-nth [
     (p-color 127 255 127)
     (p-color 255 )
     (p-color 200 100 200)
     (p-color 200 200 100)

     (p-color 102 255 178)
     (p-color  255 51 255)  ; pink
     (p-color 0 204 204)
     (p-color  255 51 51)
     (p-color   255 255 204   )

   ])
   :stroke-weight 2
   }
)

(defn dark-fill [style]
 (conj style {:fill (darker-color (:stroke style))})
)

(defn t1
   ([p] (t1 p (rcol))) 
   ([p s]
      (->> p
        (stack
          (drunk-line 10 0.2 s)
        )
        four-round
        (rotate (/ (rand-int 10) 10))
     )
   ) 
)

(stack
   (rect -1 -1 2 2 {:fill (p-color 0)})

   (grid-layout 5 
     (repeat (t1 []  {:stroke (p-color 250 180 200)
                            :stroke-weight 2}))
   ) 

   (->>
   (apply stack 
      (take 3
        (iterate t1 (drunk-line 10 0.1 (rcol)))
      ))
    four-mirror
   )
)
----
### Chita

Brazilian floral kitsch "Chita" patterns are common for textiles, table coverings etc. Here's a demonstration of how to approximate the style in Patterning.

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
		   (poly 0 0 0.3 12 {:fill (p-color 50 50 220)
							 :stroke-weight 0})
		   (->> (poly 0 0.1 0.06 5 yellow)
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
			(poly 0 0.3 0.2 5
			  {:fill (p-color 255 255 255)
			   :stroke-weight 0})
			(clock-rotate 7))
		(poly 0 0 0.2 8 {:fill (p-color 0 0 200)}))

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
 (half-drop-grid-layout
  17 (map rand-rot
	   (map rand-nth
		 (repeat [whites []
		   small-yellow small-yellow ]))))

 (half-drop-grid-layout 6 leafy-seq)))


----
### The City We Invent

This complex tiling pattern can generate a number of stylized "cities". Useful for engraved patterns.

----
:patterning

(def engrave {:stroke-weight 1 :stroke (p-color 0 0 0)})

(def roof-a 
   [(->SShape engrave 
          [ [-0.9 1] [0 -0.4] [0.9 1] ] 
   )]
)

(def roof-b 
   [(->SShape engrave 
          [ [-0.9 1] [0 0.2] [0.9 1] ] 
   )]
)

(def roof-c   
   [(->SShape engrave 
          [ [-0.9 1] 
            [-0.6 0.78] [-0.6 0.3] 
            [-0.4 0.3] [-0.4 0.62] 
            [0 0.2] [0.9 1] ] 
   )]
)

(def roof1  
   (fn [] 
      (let [r (rand-nth [roof-a roof-b roof-c ])]
         (rand-nth [r (h-reflect r)])
      )
   )
)

(def roof2 
   (stack 
      (roof1)
      [(->SShape engrave [[-0.9 -1] [-0.9 0.4 ]])]
      [(->SShape engrave [[0.9 -1] [0.9 0.4 ]])]  
   )
)

(defn narrow_ [p]
   (stack
     (h-mirror [(->SShape engrave [[-0.8 -1] [-0.4 -0.8] [-0.4 0.8] [-0.8 1]])])
     (scale 0.8 p)
   )
)

(defn block_ [p]
  (stack
    [(->SShape engrave [[-0.9 -1] [-0.9 1]])]
    [(->SShape engrave [[0.9 -1] [0.9 1]])]
    p
  )
)

(defn block [p]
  #((rand-nth [block_ block_ block_ narrow_ narrow_]) p)
)

(def blank [])

(defn person []
   (let [x (rand-nth [-0.5 -0.1 0 0.2 0.6])
          p (stack
               (poly x -0.3 0.1 10 engrave)
               [
       (->SShape engrave [[x -0.2] [x 0.1] [(- x 0.1) 0.6]])
       (->SShape engrave [[x 0.1] [(+ x 0.1) 0.6]])
       (->SShape engrave [[(- x 0.1) -0.1] [(+ x 0.1) 0]])
              ]
            )
         ]
       (rand-nth [p (h-reflect p)])
   )
)

(defn street []
  (fn [] 
    (rand-nth [blank blank (person )])
  )
) 

(def bottom
    (stack
       [(->SShape engrave [[-0.9 0.8] [0.9 0.8]])]
       (rect -0.3 -0.2 0.6 1 engrave)
       [(->SShape engrave [[-0.9 -1] [-0.9 0.8]])]
       [(->SShape engrave [[0.9 -1] [0.9 0.8]])]
    )
  
)

(def round-window
   (poly 0 0 0.65 50 engrave)
)

(def arched-window
   (let [half 
      [(->SShape engrave
      [   [0 0.4]
          [-0.6 0.4] [-0.6 -0.2] 
          [-0.5 -0.27] [-0.4 -0.34] [-0.3 -0.4 ] 
          [-0.2 -0.42] [-0.1 -0.43] [0 -0.435]
      ]
     )] ]
     (stack
       half
       (h-reflect half)
     )
   )
)

(def thin-arched   (stretch 0.4 1 arched-window)   ) 

(def four-arched
   (translate 0 0.5
   (stretch 0.7 1.2
   (reframe
   (stack
     (translate -0.9 0 thin-arched)
     (translate -0.3 0 thin-arched)
     (translate  0.3 0 thin-arched)
     (translate  0.9 0 thin-arched)
   ))))
)

(def clock
 (stack
      (poly 0 0 0.65 50 engrave)
      (clock-rotate 12 [(->SShape engrave [[0.5 0] [0.6 0]])])
      [(->SShape engrave [[0 0.35] [0 0] [0.3 -0.3]])]
   )
)

(def segmented-window
    (stack
      (poly 0 0 0.65 50 engrave)
      (clock-rotate
         8
         [(->SShape engrave [[0 0] [0 0.7]])]
     )
   )
)

(def three-windows
 (let [h 0.9 w 0.3 l -0.8]
     (apply stack 
       (map #(rect (- (* 0.5 %) 0.65) -0.6 w h) (range 3))
     )
   )
)

(def bars
  (let [bar (fn [y] [(->SShape engrave [[-0.4 y] [0.4 y]])]) ]
    (apply stack (map bar [-0.5 -0.2 0.1]))
  )
)


(def grid-windows
  (scale 0.8
       (grid-layout 3 (repeat (rect -0.6 -0.6 1.2 1.2 engrave)))
    )
)


(def tiles
 {
  :street (street)
  :roof1  roof1
  :roof2 roof2
  :bottom bottom
  :round-window (block round-window )
  :clock  (block clock) 
 :segmented-window (block segmented-window)
 :three-windows (block three-windows)
 :grid-windows (block grid-windows )
 :bars (block bars)
 :arched-window (block arched-window)
 :thin-arched (block thin-arched)
 :four-arched (block_ four-arched)
}
)



(def ok-tops (into [] 
      (remove #(condp = % :street true :roof1 :roof2 true true false)) (keys tiles )))

(def ok-bottoms (into [] 
     (remove #(condp = % :street true :bottom true false)) (keys tiles)))


(defn random-key [previous] 
    (let [rk (rand-nth (filter #(not (some #{%} [:roof1 :street])) (keys tiles)) )]
       (cond 
         (= previous rk) (random-key previous) 
         :else rk)
    )
)

(defn good-random-key [previous]
   (condp = previous
      :bottom  :street
      :street (rand-nth [:roof1 :roof1 :street])
      ; otherwise
      (random-key previous)
   )
)


(defn process-key [k]
   (let [v (get tiles k)]
     (if (fn? v) (v) v)
   )
)



(defn make-batch [size dummy]
   (let [r2 (- size 2)
          primo (rand-nth ok-tops )

          mid (loop [count r2 previous primo build []]
              (if (= 0 count) build
                 (let [rk (good-random-key previous)]
 
                     (recur (- count 1) rk (conj build rk ))
                 )
              )
          )
   
          fin (if (= (last mid) :street) :roof1
                  (rand-nth ok-bottoms))
        ]
       (concat [primo] mid [fin])
   )
)

(defn tile-stream [rows]
   (map process-key
      (apply concat
         (iterate #(make-batch rows %) []) 
      )
   )
)

(stack
(rect -1 -1 2 2)
(grid-layout 11
   (tile-stream 11)
)
)

