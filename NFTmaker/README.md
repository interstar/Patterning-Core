# NFTmaker

A tool for creating generative NFT patterns using Patterning Core. This tool compiles ClojureScript patterns into standalone JavaScript bundles that can be used with [FX(hash)](https://www.fxhash.xyz/) or other NFT platforms.

## Overview

NFTmaker takes Patterning patterns written in ClojureScript and compiles them into self-contained JavaScript bundles. Each bundle includes:
- The compiled pattern code
- FX(hash) integration for deterministic randomness
- A preview HTML page
- All necessary dependencies

## Directory Structure

```
NFTmaker/
├── patterns/          # Source ClojureScript pattern files
├── scripts/          # Build and utility scripts
│   ├── compile_pattern.py
│   ├── pattern_template.html
│   ├── fxhash.min.js
│   └── fxhash_random_generator.js
└── dist/             # Compiled output
    └── patterns/     # Individual pattern bundles
```

## Requirements

- Python 3.x
- Leiningen (for ClojureScript compilation)


## Usage

### Creating a New Pattern

1. Create a new ClojureScript file in the `patterns/` directory
2. Use the template structure from `scripts/pattern_template.cljs`
3. Implement your pattern using Patterning Core functions

Example pattern structure:
```clojure
(ns your-pattern-name
  (:require [patterning.color :refer [p-color]]
            [patterning.groups :refer [rect]]
            [patterning.library.std :refer [square]]
            [patterning.canvasview :as canvasview]))

(def default-params
  {:width 800
   :height 800})

(defn ^:export main [params]
  (let [merged-params (merge default-params (js->clj params :keywordize-keys true))
        canvas (:canvas merged-params)
        random (:random merged-params)
        pattern (square {:fill (p-color 255 0 0)})]
    
    (when canvas
      (canvasview/setupResponsiveCanvas canvas pattern))
    
    pattern))
```

### Compiling a Pattern

Use the `compile_pattern.py` script to compile your pattern:

```bash
python scripts/compile_pattern.py patterns/your-pattern.cljs
```

This will:
1. Compile the ClojureScript to JavaScript
2. Create a bundle in `dist/patterns/your-pattern/`
3. Generate a preview HTML file
4. Include FX(hash) integration

### Previewing Your Pattern

After compilation, you can preview your pattern:

```bash
cd dist/patterns/your-pattern
python3 -m http.server
```

Then open `http://localhost:8000` in your browser.

## Pattern Development

### Key Components

1. **Pattern Definition**: Your ClojureScript code that generates the pattern
2. **Parameters**: Configurable options for your pattern
3. **Random Generation**: Using FX(hash) for deterministic randomness
4. **Canvas Rendering**: Using Patterning's canvas view for display

### Best Practices

1. Always include `canvasview` in your requires
2. Use the `setupResponsiveCanvas` function for rendering
3. Handle both canvas and non-canvas cases (for programmatic use)
4. Use FX(hash) random generator for NFT compatibility

## Examples

See the included example patterns:
- `chita.cljs`: A flower-based pattern
- `city.cljs`: A cityscape generator
- `triangles.cljs`: A geometric pattern

## License

Same as Patterning Core - LGPL 3.0 or later 