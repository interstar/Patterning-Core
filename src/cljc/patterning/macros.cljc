(ns patterning.macros)

(defmacro optional-styled-primitive [args body]
   (let [extra (conj args 'style)]
     `(fn (~extra (~'APattern (~'->SShape ~'style ~body)))
         (~args  (~'APattern (~'->SShape {} ~body)))
      )
   )
)

(defmacro defcolor
  "Define a color by name. Automatically detects the format:
   - String: hex color (e.g., \"#ff4488\" or \"ff4488dd\" with alpha)
   - Numbers: RGB or RGBA values for p-color (e.g., 255 0 0 or 255 0 0 128)
   
   Examples:
   (defcolor my-red \"#ff0000\")
   (defcolor my-blue \"ff0000ff\")
   (defcolor my-green 0 255 0)
   (defcolor my-transparent 255 0 0 128)"
  [name color-def & args]
  (if (string? color-def)
    ;; String format - hex color (use unqualified symbol that will resolve in user namespace)
    `(def ~name (~'hex-color ~color-def))
    ;; Number format - p-color with RGB or RGBA
    ;; All remaining args are numbers: (defcolor name r g b) or (defcolor name r g b a)
    `(def ~name (~'p-color ~color-def ~@args))))
