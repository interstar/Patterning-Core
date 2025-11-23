### Kira Patterns


[Christian Flaccus](https://www.linkedin.com/feed/update/urn:li:activity:7397355183736786944/) has a wonderful app called [Kira Patterns](https://www.kirapatterns.com/) which are based on some Truchet-like lined tiles that have some beautiful curves and straight lines. Largely based on four parallel lines or four quarter circles.

I loved the vibe so much I wanted to experiment with it in Patterning. These are just the first rough experiments with the simplest elements of these tiles.

----
:patterning

(defcolor black 0)
(defcolor c 255)
(def s1 (paint c :black 2))

(def step 2/6)

(def k1
  (on-background black
    (let [steps (take 5 (iterate #(+ % step) (- step 1)))]
      (apply stack 
         (map #(horizontal-line % s1) steps )))))

      
(defn circle [r] (poly 80 r s1)) 

(defn circles [n]
  (apply stack
      (map circle (take n (iterate #(- % step) (* step n))))))

(def k3 
  (on-background black
    (stack k1 (circles 3))))


(defn arcs [n]
  (->>
    (apply stack
      (map 
       (fn [r] (arc r 0 d180 s1))
       (take n (iterate #(- % step) (* step n)))))
    (translate -1 -1)
    ))

(def k5 
  (on-background black
    (stack k1 (arcs 5))))

(def k6 (h-reflect k5))

(def half-arc 
  (arc 0.5 0 d180 s1))

(def k9
  (let [h1
        (->> half-arc 
          (rotate d270)
          (scale 0.67)
          (translate -0.8 (- step)))
        h2 (v-reflect h1)        
        ]
    (stack k1 h1 h2)
    ))

(defn m2 [p] [p (rotate d90 p)])
(defn m4 [p] (into [] (map #(rotate % p) [0 d90 d180 d270])))

(def all-tiles 
  (concat
    (m2 k1) (m2 k3) (m4 k5) (m4 k6) (m4 k9)
   ))


(grid-layout 5
 (iterate (fn [_] (rand-nth all-tiles)) k1)
)

----
Or we can run through `four-round`

----
:patterning

(defcolor black 0)
(defcolor c "ffaa00")
(def s1 (paint c :black 2))

(def step 2/6)

(def k1
  (on-background black
    (let [steps (take 5 (iterate #(+ % step) (- step 1)))]
      (apply stack 
         (map #(horizontal-line % s1) steps )))))

      
(defn circle [r] (poly 80 r s1)) 

(defn circles [n]
  (apply stack
      (map circle (take n (iterate #(- % step) (* step n))))))

(def k3 
  (on-background black
    (stack k1 (circles 3))))


(defn arcs [n]
  (->>
    (apply stack
      (map 
       (fn [r] (arc r 0 d180 s1))
       (take n (iterate #(- % step) (* step n)))))
    (translate -1 -1)
    ))

(def k5 
  (on-background black
    (stack k1 (arcs 5))))

(def k6 (h-reflect k5))

(def half-arc 
  (arc 0.5 0 d180 s1))

(def k9
  (let [h1
        (->> half-arc 
          (rotate d270)
          (scale 0.67)
          (translate -0.8 (- step)))
        h2 (v-reflect h1)        
        ]
    (stack k1 h1 h2)
    ))

(defn m2 [p] [p (rotate d90 p)])
(defn m4 [p] (into [] (map #(rotate % p) [0 d90 d180 d270])))

(def all-tiles 
  (concat
    (m2 k1) (m2 k3) (m4 k5) (m4 k6) (m4 k9)
   ))
   
(four-round
 (grid-layout 3
 (iterate (fn [_] (rand-nth all-tiles)) k1)
))
----
Or [Douat](Douat.html) 
----
:patterning

(defcolor black 0)
(defcolor c "ffaa00")
(def s1 (paint c :black 2))

(def step 2/6)

(def k1
  (on-background black
    (let [steps (take 5 (iterate #(+ % step) (- step 1)))]
      (apply stack 
         (map #(horizontal-line % s1) steps )))))

      
(defn circle [r] (poly 80 r s1)) 

(defn circles [n]
  (apply stack
      (map circle (take n (iterate #(- % step) (* step n))))))

(def k3 
  (on-background black
    (stack k1 (circles 3))))


(defn arcs [n]
  (->>
    (apply stack
      (map 
       (fn [r] (arc r 0 d180 s1))
       (take n (iterate #(- % step) (* step n)))))
    (translate -1 -1)
    ))

(def k5 
  (on-background black
    (stack k1 (arcs 5))))

(def k6 (h-reflect k5))

(def half-arc 
  (arc 0.5 0 d180 s1))

(def k9
  (let [h1
        (->> half-arc 
          (rotate d270)
          (scale 0.67)
          (translate -0.8 (- step)))
        h2 (v-reflect h1)        
        ]
    (stack k1 h1 h2)
    ))

(defn m2 [p] [p (rotate d90 p)])
(defn m4 [p] (into [] (map #(rotate % p) [0 d90 d180 d270])))

(def all-tiles 
  (concat
    (m2 k1) (m2 k3) (m4 k5) (m4 k6) (m4 k9)
   ))


(->> 
 (grid-layout 3
 (iterate (fn [_] (rand-nth all-tiles)) k1)
 )
 (Douat)
 A C A B 
 A E G F
 (:p)
)

