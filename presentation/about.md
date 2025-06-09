# Patterning Presentation System

This system creates a Reveal.js presentation from a collection of Patterning pattern files. Each pattern file becomes a slide in the presentation, showing both the pattern code and its rendered output.

## Current State (as of 2024-06)

- The Python script (`compile_slide.py`) builds HTML slides from ClojureScript pattern files in the `patterns/` directory.
- For each pattern, only the code between `;; PATTERN START` and `;; PATTERN END` markers is extracted and shown on the slide. Boilerplate code is omitted for clarity.
- Metadata (title, description, tags) is extracted from comments at the top of each pattern file.
- Each slide displays:
  - The pattern's title and description
  - The relevant pattern code (with syntax highlighting)
  - The rendered pattern output (canvas)
- Syntax highlighting for Clojure code is enabled using Highlight.js via CDN.
- The generated slides are placed in the `slides/` directory, one HTML file per pattern.
- The layout is responsive and optimized for presentations, with a wide code area and a square pattern output area.
- You can skip recompiling the ClojureScript when tweaking the slide layout or CSS by using the `--no-compile` flag with the Python script.

## Project Structure

```
presentation/
├── patterns/           # Directory containing pattern files
│   ├── pattern1.cljs   # Individual pattern files
│   ├── pattern2.cljs
│   └── ...
├── slides/            # Generated slides
│   ├── chita.html     # Example generated slide
│   └── ...
├── compile_slide.py   # Script to generate slides
├── slide_template.html# HTML template for slides
└── ...
```

## Pattern File Format

Each pattern file should be a ClojureScript file with the following structure:

```clojure
;; Title: Pattern Title
;; Description: A brief description of what this pattern demonstrates
;; Tags: tag1, tag2, tag3

(ns patterning.patterns.pattern1
  (:require ...))

;; ...

;; PATTERN START
(defn the-pattern [params]
  ;; Pattern code here
)
;; PATTERN END
```

Only the code between `;; PATTERN START` and `;; PATTERN END` will be shown on the slide.

## Generation Process

The `compile_slide.py` script will:

1. Read a pattern file from the `patterns/` directory
2. Extract metadata (title, description, tags) from comments
3. Extract only the code between `;; PATTERN START` and `;; PATTERN END` markers
4. Generate an individual slide HTML file with code and rendered output
5. Optionally skip ClojureScript compilation with the `--no-compile` flag for faster iteration on layout/CSS

## Slide Template

Each slide follows this structure:

```html
<section>
    <h1>Pattern Title</h1>
    <p class="description">Pattern description</p>
    <div class="slide-main-content">
        <div class="slide-code-col">
            <div class="slide-code-box">
                <pre><code class="language-clojure">
                    ;; Pattern code here
                </code></pre>
            </div>
        </div>
        <div class="slide-pattern-col">
            <div class="pattern-container">
                <canvas id="pattern-canvas"></canvas>
            </div>
        </div>
    </div>
</section>
```

## Usage

1. Place pattern files in the `patterns/` directory, using the `;; PATTERN START` and `;; PATTERN END` markers.
2. Run the generation script:
   ```bash
   python compile_slide.py patterns/chita.cljs
   # or, to skip ClojureScript compilation:
   python compile_slide.py patterns/chita.cljs --no-compile
   ```
3. Open the generated HTML slide in a browser to view the code and pattern output.

## Next Steps

- **Interactive/Quil Patterns:**
  - We will extend the system to support interactive and animated patterns using Quil and ClojureScript.
  - These will be precompiled with a Leiningen project and adapted to the same slide format.
- **Slide Organization:**
  - We will add a system to organize all generated slides into a single presentation, allowing navigation forward and backward through the slides (e.g., with a main `index.html` or Reveal.js deck).

Stay tuned for further enhancements as we integrate interactive patterns and presentation navigation!