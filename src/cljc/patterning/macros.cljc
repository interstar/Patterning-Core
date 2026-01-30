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
   - Existing color/expression: any form that already returns a color
   
   Examples:
   (defcolor my-red \"#ff0000\")
   (defcolor my-blue \"ff0000ff\")
   (defcolor my-green 0 255 0)
   (defcolor my-transparent 255 0 0 128)
   (defcolor faint-red (faint red 100))"
  [name color-def & args]
  (cond
    (string? color-def)
    ;; String format - hex color (use unqualified symbol that will resolve in user namespace)
    `(def ~name (~'hex-color ~color-def))

    (seq args)
    ;; Number format - p-color with RGB or RGBA
    ;; All remaining args are numbers: (defcolor name r g b) or (defcolor name r g b a)
    `(def ~name (~'p-color ~color-def ~@args))

    (number? color-def)
    `(def ~name (~'p-color ~color-def))

    (and (list? color-def) (= 'quote (first color-def)))
    `(def ~name (~'apply ~'p-color ~(second color-def)))

    (vector? color-def)
    `(def ~name (~'apply ~'p-color ~color-def))

    :else
    ;; Existing color/expression
    `(def ~name ~color-def)))

(defmacro defpalette
  "Define a palette of colors. Creates individual color variables and a palette map.
   Color definitions can be:
   - String: hex color (e.g., \"#ff4488\" or \"0000ff\")
   - List: RGB/RGBA values for p-color (e.g., '(255 0 0) or '(255 0 255 150))
   - Vector: RGB/RGBA values for p-color (e.g., [255 0 0] or [255 0 255 150])
   - Existing color/expression: any form that already returns a color
   
   Examples:
   (defpalette palette1
     red '(255 0 0)
     blue \"0000ff\"
     transparent-pink [255 0 255 150]
     faint-red (faint red 100))
   
   This creates:
   - Individual variables: red, blue, transparent-pink, faint-red
   - A map: palette1 with {:red ... :blue ... :transparent-pink ... :faint-red ...}"
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

                                               (and (list? color-spec) (= 'quote (first color-spec)))
                                               `(~'apply ~'p-color ~(second color-spec))
                                               
                                               (vector? color-spec)
                                               `(~'apply ~'p-color ~color-spec)

                                               (list? color-spec)
                                               color-spec

                                               (symbol? color-spec)
                                               color-spec
                                               
                                               :else
                                               (throw (ex-info "Color spec must be string, list, vector, or expression"
                                                               {:color-name color-name :color-spec color-spec})))]
                            [`(def ~color-name ~color-value)
                             `[~(keyword color-name) ~color-name]]))
                        pairs)
        def-statements (take-nth 2 color-bindings)
        map-entries (take-nth 2 (rest color-bindings))]
    `(do
       ~@def-statements
       (def ~palette-name (hash-map ~@(mapcat identity map-entries))))))

(defmacro set-standard-colors
  "Define a default palette of 32 standard colors with English names.
   Creates individual color vars plus a map named standard-colors."
  []
  (let [colors [["black" "#000000"]
                ["white" "#ffffff"]
                ["grey" "#d3d3d3"]
                ["red" "#ff0000"]
                ["green" "#00ff00"]
                ["dark-green" "#008000"]
                ["blue" "#0000ff"]
                ["cyan" "#00ffff"]
                ["magenta" "#ff00ff"]
                ["yellow" "#ffff00"]
                ["orange" "#ffa500"]
                ["purple" "#800080"]
                ["brown" "#a52a2a"]
                ["pink" "#ffc0cb"]
                ["navy" "#000080"]
                ["teal" "#008080"]
                ["olive" "#808000"]
                ["maroon" "#800000"]
                ["silver" "#c0c0c0"]
                ["gold" "#ffd700"]
                ["violet" "#ee82ee"]
                ["indigo" "#4b0082"]
                ["coral" "#ff7f50"]
                ["salmon" "#fa8072"]
                ["khaki" "#f0e68c"]
                ["beige" "#f5f5dc"]
                ["chocolate" "#d2691e"]
                ["crimson" "#dc143c"]
                ["skyblue" "#87ceeb"]
                ["cream" "#fff7d7"]]]
    `(~'defpalette ~(symbol "standard-colors")
       ~@(mapcat (fn [[name hex]] [(symbol name) hex]) colors))))
