(ns patterning.color
  (:require [patterning.strings :as strings]
            [patterning.maths :as maths
             :refer [default-random parse-int-radix]]))

;; Now we'll use a custom vector as a color
(defn p-color
  ([r g b a] [r g b a])
  ([r g b] (p-color r g b 255))
  ([x] (p-color x x x 255))  )

;; Default style for shapes - ensures visibility in P5 rendering
(def default-style {:stroke (p-color 0) :stroke-weight 1})

;; Color helpers
(defn color-seq "handy for creating sequences of color changes"
  [colors] (into [] (map (fn [c] {:stroke c}) colors )))


(defn lightened-color [c]
  (let [light (fn [c] (* 1.2 c))]
    (p-color (light (get c 0))  (light (get c 1))  (light (get c 2))  (get c 3)) ))

(defn color-to-fill [style] (conj  {:fill (get style :stroke)} style))

(defn lighten [style] (conj {:stroke (lightened-color (get style :stroke))} style))

(defn mod-styles [f styles] (into [] (map f styles)))


(defn edge-col [c] (comp #(conj % {:stroke c}) color-to-fill) )

(defn setup-colors [colors c] (map (edge-col c) (color-seq colors)  ))


(defn remove-transparency [c] (conj (take 3 c) 255))

(defn faint
  "Return the same RGB color with a new alpha value (0-255).
   Alpha may be a number or a 1-2 digit hex string."
  [c a]
  (let [alpha (if (string? a)
                (let [alpha-str a]
                  (if (re-matches #"[0-9a-fA-F]{1,2}" alpha-str)
                    (parse-int-radix alpha-str 16)
                    (throw (ex-info (str "Invalid alpha string: " alpha-str)
                                    {:alpha alpha-str}))))
                a)]
    (conj (vec (take 3 c)) alpha)))

(defn rand-col [& {:keys [random] :or {random default-random}}] 
  (p-color (.randomInt random 255) 
           (.randomInt random 255) 
           (.randomInt random 255)))

(defn darker-color [c] (apply p-color (map (partial * 0.7) c)))



;; Colours for SVG
(defn transparent-gen
  ([name [r g b a]]
   (let [tx (fn [x] (maths/tx 0 255 0 1 x))]
     (if (= a 255) (strings/gen-format "%s='rgb(%s,%s,%s)'" name (int r) (int g) (int b))
         (strings/gen-format "%s='rgb(%s,%s,%s)' %s-opacity='%.2f' "name (int r) (int g) (int b)  name (tx a) )
         )))  )

(defn stroke-gen [c] (transparent-gen "stroke" c))
(defn fill-gen [c] (transparent-gen "fill" c))

;; Hex color conversion functions

(defn hex-char-to-int
  "Convert a single hex character to integer"
  [c]
  (let [c (strings/lower-case (str c))]
    (case c
      "0" 0  "1" 1  "2" 2  "3" 3  "4" 4  "5" 5  "6" 6  "7" 7
      "8" 8  "9" 9  "a" 10 "b" 11 "c" 12 "d" 13 "e" 14 "f" 15
      (throw (ex-info (str "Invalid hex character: " c) {:char c})))))

(defn hex-pair-to-int
  "Convert a pair of hex characters to integer (0-255)"
  [hex-str]
  (let [chars (take 2 hex-str)
        high (hex-char-to-int (first chars))
        low (hex-char-to-int (second chars))]
    (+ (* high 16) low)))

(defn parse-hex-string
  "Parse a hex color string to RGB or RGBA values.
   Supports formats: #RGB, #RRGGBB, #RGBA, #RRGGBBAA, RGB, RRGGBB, RGBA, RRGGBBAA
   Returns [r g b] for 3/6 digit formats, [r g b a] for 4/8 digit formats"
  [hex-str]
  (let [clean-str (strings/strip-leading-hash hex-str)
        len (count clean-str)]
    (cond
      (= len 3) ; #RGB format
      (let [r (hex-char-to-int (nth clean-str 0))
            g (hex-char-to-int (nth clean-str 1))
            b (hex-char-to-int (nth clean-str 2))]
        [(* r 17) (* g 17) (* b 17)]) ; Convert to 0-255 range
      
      (= len 4) ; #RGBA format
      (let [r (hex-char-to-int (nth clean-str 0))
            g (hex-char-to-int (nth clean-str 1))
            b (hex-char-to-int (nth clean-str 2))
            a (hex-char-to-int (nth clean-str 3))]
        [(* r 17) (* g 17) (* b 17) (* a 17)]) ; Convert to 0-255 range
      
      (= len 6) ; #RRGGBB format
      (let [r (hex-pair-to-int (take 2 clean-str))
            g (hex-pair-to-int (drop 2 (take 4 clean-str)))
            b (hex-pair-to-int (drop 4 clean-str))]
        [r g b])
      
      (= len 8) ; #RRGGBBAA format (CSS standard with alpha)
      (let [r (hex-pair-to-int (take 2 clean-str))
            g (hex-pair-to-int (drop 2 (take 4 clean-str)))
            b (hex-pair-to-int (drop 4 (take 6 clean-str)))
            a (hex-pair-to-int (drop 6 clean-str))]
        [r g b a])
      
      :else
      (throw (ex-info (str "Invalid hex string length: " len " (expected 3, 4, 6, or 8 characters)") 
                      {:hex-str hex-str :length len})))))

(defn hex-color
  "Convert a hex color string to p-color format.
   Supports formats: #RGB, #RRGGBB, #RGBA, #RRGGBBAA, RGB, RRGGBB, RGBA, RRGGBBAA
   If hex string includes alpha (4 or 8 digits), it will be used.
   Otherwise, optional alpha parameter (0-255, defaults to 255)"
  ([hex-str] 
   (let [parsed (parse-hex-string hex-str)]
     (if (= (count parsed) 4)
       ;; Alpha included in hex string
       (apply p-color parsed)
       ;; No alpha, use default
       (apply p-color (conj parsed 255)))))
  ([hex-str alpha]
   (let [parsed (parse-hex-string hex-str)]
     (if (= (count parsed) 4)
       ;; Alpha included in hex string, but user provided one - use user's value
       (apply p-color (conj (take 3 parsed) alpha))
       ;; No alpha in hex string, use provided value
       (apply p-color (conj parsed alpha))))))


(defn resolve-color [c]
  (let [palette
    {:black (p-color 0)
    :white (p-color 255)
    :grey (p-color 128) 
    :red (p-color 255 0 0)
    :green (p-color 0 255 0)
    :blue (p-color 0 0 255)
    :yellow (p-color 255 255 0)
    :magenta (p-color 255 0 255)
    :cyan (p-color 0 255 255)
    :purple (p-color 128 0 128)
    :orange (p-color 255 165 0)
    :brown (p-color 165 42 42)}]
    (cond 
      (contains? #{:black :red :green :blue :yellow :magenta :cyan :purple :orange :brown :white} c)
      (get palette c)

      (string? c)
      (hex-color c)

      :else
      c)))

(defn resolve-thickness [t]
  (get {:thin 1 :solid 3 :thick 5} t t)
)

(defn paint 
  "Creates a style map with stroke, optional fill, and stroke-weight.
   
   Arities:
   (paint stroke) - sets stroke only, no fill, default stroke-weight 1
   (paint stroke :nofill) - sets stroke only, no fill, default stroke-weight 1
   (paint stroke :nofill weight) - sets stroke only, no fill, explicit stroke-weight
   (paint stroke fill) - sets stroke and fill, default stroke-weight 1
   (paint stroke fill weight) - sets stroke, fill, and stroke-weight"
  ([stroke fill thick]
   (if (= fill :nofill)
     ;; Second arg is :nofill, so third arg is stroke-weight
     {:stroke (resolve-color stroke)
      :stroke-weight (resolve-thickness thick)}
     ;; Normal case: stroke, fill, weight
     {:stroke (resolve-color stroke)
      :fill (resolve-color fill)
      :stroke-weight (resolve-thickness thick)}))
  ([stroke fill]
   (if (= fill :nofill)
     ;; Second arg is :nofill, use default stroke-weight
     {:stroke (resolve-color stroke)
      :stroke-weight 1}
     ;; Normal case: stroke and fill, default stroke-weight
     {:stroke (resolve-color stroke)
      :fill (resolve-color fill)
      :stroke-weight 1}))
  ([stroke]
   ;; Single arg: stroke only, no fill, default stroke-weight
   {:stroke (resolve-color stroke)
    :stroke-weight 1}))
