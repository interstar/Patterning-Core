(ns patterning.color
  (:require [patterning.strings :as strings]
            [patterning.maths :as maths
             :refer [default-random]]))

;; Now we'll use a custom vector as a color
(defn p-color
  ([r g b a] [r g b a])
  ([r g b] (p-color r g b 255))
  ([x] (p-color x x x 255))  )

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
  (let [c #?(:clj (clojure.string/lower-case (str c))
             :cljs (-> (str c) .toLowerCase))]
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
  "Parse a hex color string to RGB values.
   Supports formats: #RGB, #RRGGBB, RGB, RRGGBB"
  [hex-str]
  (let [clean-str #?(:clj (clojure.string/replace hex-str #"^#" "")
                     :cljs (-> hex-str (.replace #"^#" "")))
        len (count clean-str)]
    (cond
      (= len 3) ; #RGB format
      (let [r (hex-char-to-int (nth clean-str 0))
            g (hex-char-to-int (nth clean-str 1))
            b (hex-char-to-int (nth clean-str 2))]
        [(* r 17) (* g 17) (* b 17)]) ; Convert to 0-255 range
      
      (= len 6) ; #RRGGBB format
      (let [r (hex-pair-to-int (take 2 clean-str))
            g (hex-pair-to-int (drop 2 (take 4 clean-str)))
            b (hex-pair-to-int (drop 4 clean-str))]
        [r g b])
      
      :else
      (throw (ex-info (str "Invalid hex string length: " len " (expected 3 or 6 characters)") 
                      {:hex-str hex-str :length len})))))

(defn hex-color
  "Convert a hex color string to p-color format.
   Supports formats: #RGB, #RRGGBB, RGB, RRGGBB
   Optional alpha parameter (0-255, defaults to 255)"
  ([hex-str] (hex-color hex-str 255))
  ([hex-str alpha]
   (let [[r g b] (parse-hex-string hex-str)]
     (p-color r g b alpha))))


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
  ([stroke fill thick]
     {:stroke (resolve-color stroke)
      :fill (resolve-color fill)
      :stroke-weight (resolve-thickness thick)
     })
  ([stroke fill]
   (paint stroke fill 1))
  ([stroke]
    (paint stroke :grey 1)))