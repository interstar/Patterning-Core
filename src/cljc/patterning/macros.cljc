(ns patterning.macros)

(defmacro optional-styled-primitive [args body]
   (let [extra (conj args 'style)]
     `(fn (~extra (~'APattern (~'->SShape ~'style ~body)))
         (~args  (~'APattern (~'->SShape patterning.color/default-style ~body)))
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

(defmacro defpalette
  "Define a palette of colors. Creates individual color variables and a palette map.
   Color definitions can be:
   - String: hex color (e.g., \"#ff4488\" or \"0000ff\")
   - List: RGB/RGBA values for p-color (e.g., '(255 0 0) or '(255 0 255 150))
   - Vector: RGB/RGBA values for p-color (e.g., [255 0 0] or [255 0 255 150])
   
   Examples:
   (defpalette palette1
     red '(255 0 0)
     blue \"0000ff\"
     transparent-pink [255 0 255 150])
   
   This creates:
   - Individual variables: red, blue, transparent-pink
   - A map: palette1 with {:red ... :blue ... :transparent-pink ...}"
  [palette-name & color-defs]
  (when (odd? (count color-defs))
    (throw (ex-info "defpalette requires an even number of arguments (color-name color-spec pairs)"
                    {:palette-name palette-name :args color-defs})))
  
  (let [pairs (partition 2 color-defs)
        color-bindings (mapcat
                        (fn [[color-name color-spec]]
                          (let [color-value (cond
                                               (string? color-spec)
                                               `(~'hex-color ~color-spec)
                                               
                                               (list? color-spec)
                                               `(~'apply ~'p-color ~color-spec)
                                               
                                               (vector? color-spec)
                                               `(~'apply ~'p-color ~color-spec)
                                               
                                               :else
                                               (throw (ex-info "Color spec must be string, list, or vector"
                                                               {:color-name color-name :color-spec color-spec})))]
                            [`(def ~color-name ~color-value)
                             `[~(keyword color-name) ~color-name]]))
                        pairs)
        def-statements (take-nth 2 color-bindings)
        map-entries (take-nth 2 (rest color-bindings))]
    `(do
       ~@def-statements
       (def ~palette-name (hash-map ~@(mapcat identity map-entries))))))
