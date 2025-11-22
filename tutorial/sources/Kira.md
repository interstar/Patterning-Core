### Kira Patterns


[Christian Flaccus](https://www.linkedin.com/feed/update/urn:li:activity:7397355183736786944/) has a wonderful app called [Kira Patterns](https://www.kirapatterns.com/) which are based on some Truchet-like lined tiles that have some beautiful curves and straight lines. Largely based on four parallel lines or four quarter circles.

I loved the vibe so much I wanted to experiment with it in Patterning. These are just the first rough experiments with the simplest elements of these tiles.

----
:patterning 

(def s1 (paint :white :black 2)) 
(def black (p-color 0))

(def step 2/6)
(def rB (/ PI 2))
(def rC PI)
(def rD (/ (* 3 PI) 2))


(def k1
  (on-background black
    (let [steps (take 5 (iterate #(+ % step) (- step 1)))]
      (apply stack 
         (map #(horizontal-line % s1) steps )))))

(def k2 (rotate (/ PI 2) k1))
      
(defn circle [r] (poly 80 r s1))
 
(defn circles [n] 
  (apply stack
      (map circle (take n (iterate #(- % step) (* step n))))))

(def k3 
  (on-background black
    (stack k1 (circles 3))))

(def k4 (rotate (/ PI 2) k3))   

(def k5 
  (on-background black 
      (clip (fn [[x y]](and (> x -1) (> y -1))) 
       (translate -1 -1 (circles 5)))))
(def k6 (rotate rB k5))
(def k7 (rotate rC k5))
(def k8 (rotate rD k5)) 

(grid-layout 5
 (iterate (fn [_] (rand-nth [k1 k2 k3 k4 k5 k6 k7 k8])) k1)
)

----
Or we can run through `four-round`

----
:patterning

(def s1 (paint :white :black 2)) 
(def black (p-color 0))

(def step 2/6)
(def rB (/ PI 2))
(def rC PI)
(def rD (/ (* 3 PI) 2))


(def k1
  (on-background black
    (let [steps (take 5 (iterate #(+ % step) (- step 1)))]
      (apply stack 
         (map #(horizontal-line % s1) steps )))))

(def k2 (rotate (/ PI 2) k1))
      
(defn circle [r] (poly 80 r s1))
 
(defn circles [n] 
  (apply stack
      (map circle (take n (iterate #(- % step) (* step n))))))

(def k3 
  (on-background black
    (stack k1 (circles 3))))

(def k4 (rotate (/ PI 2) k3))   

(def k5 
  (on-background black 
      (clip (fn [[x y]](and (> x -1) (> y -1))) 
       (translate -1 -1 (circles 5)))))
(def k6 (rotate rB k5))
(def k7 (rotate rC k5))
(def k8 (rotate rD k5)) 

(four-round
 (grid-layout 3
 (iterate (fn [_] (rand-nth [k1 k2 k3 k4 k5 k6 k7 k8])) k1)
))
----
Or `four-mirror`
----
:patterning
(def s1 (paint :white :black 2)) 
(def black (p-color 0))

(def step 2/6)
(def rB (/ PI 2))
(def rC PI)
(def rD (/ (* 3 PI) 2))


(def k1
  (on-background black
    (let [steps (take 5 (iterate #(+ % step) (- step 1)))]
      (apply stack 
         (map #(horizontal-line % s1) steps )))))

(def k2 (rotate (/ PI 2) k1))
      
(defn circle [r] (poly 80 r s1))
 
(defn circles [n] 
  (apply stack
      (map circle (take n (iterate #(- % step) (* step n))))))

(def k3 
  (on-background black
    (stack k1 (circles 3))))

(def k4 (rotate (/ PI 2) k3))   

(def k5 
  (on-background black 
      (clip (fn [[x y]](and (> x -1) (> y -1))) 
       (translate -1 -1 (circles 5)))))
(def k6 (rotate rB k5))
(def k7 (rotate rC k5))
(def k8 (rotate rD k5)) 

(four-mirror 
 (grid-layout 3
 (iterate (fn [_] (rand-nth [k1 k2 k3 k4 k5 k6 k7 k8])) k1)
))
----
Or [Douat](Douat.html) 
----
:patterning

(def s1 (paint :white :black 2)) 
(def black (p-color 0))

(def step 2/6)
(def rB (/ PI 2))
(def rC PI)
(def rD (/ (* 3 PI) 2))


(def k1
  (on-background black
    (let [steps (take 5 (iterate #(+ % step) (- step 1)))]
      (apply stack 
         (map #(horizontal-line % s1) steps )))))

(def k2 (rotate (/ PI 2) k1))
      
(defn circle [r] (poly 80 r s1))
 
(defn circles [n] 
  (apply stack
      (map circle (take n (iterate #(- % step) (* step n))))))

(def k3 
  (on-background black
    (stack k1 (circles 3))))

(def k4 (rotate (/ PI 2) k3))   

(def k5 
  (on-background black 
      (clip (fn [[x y]](and (> x -1) (> y -1))) 
       (translate -1 -1 (circles 5)))))
(def k6 (rotate rB k5))
(def k7 (rotate rC k5))
(def k8 (rotate rD k5)) 


(->> 
 (grid-layout 3
 (iterate (fn [_] (rand-nth [k1 k2 k3 k4 k5 k6 k7 k8])) k1)
 )
 (Douat)
 A C A B 
 A E G F
 (:p)
)

