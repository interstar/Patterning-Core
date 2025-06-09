Patterning-Quil
===============

A Quil / Processing wrapper for Patterning

Live Coding Setup
----------------

1. Start the REPL with dev profile:
   ```bash
   lein with-profile dev repl
   ```

2. In Emacs:
   - Open your project files
   - Connect to the REPL using CIDER:
     - Use `M-x cider-connect` (not jack-in)
     - When prompted for host, press return (uses localhost)
     - When prompted for port, enter: `46657`
   - Open `src/patterning_quil/core.clj`
   - Open `src/patterning_quil/examples.clj`
   - Open `dev/patterning_quil/dev.clj`

3. Start the live-coding environment:
   - In the REPL, load the dev namespace:
     ```clojure
     (require 'patterning_quil.dev :reload)
     ```
   - This will start the Quil window using the dev sketch (customizable in `dev/patterning_quil/dev.clj`)

4. Live coding workflow:
   - Edit pattern definitions in `examples.clj` or customize `dev-setup` and `dev-draw` in `dev.clj`
   - Use `(reset! patterning_quil.core/current-pattern (your-new-pattern))` to see changes
   - Or use `(patterning_quil.dev/refresh-all)` to reload everything
   - Use `C-c C-k` to reload the current buffer

5. Example pattern modification:
   ```clojure
   ;; In the REPL
   (reset! patterning_quil.core/current-pattern 
           (patterning_quil.examples/four
            (patterning_quil.examples/city-pattern)
            (patterning_quil.examples/round-pattern)
            (patterning_quil.examples/spray-pattern)
            (patterning_quil.examples/block-pattern)))
   ```

6. To stop:
   - Close the Quil window
   - Or use `(patterning_quil.dev/stop-sketch)` in the REPL

---

**Note:** The dev sketch is defined in `dev/patterning_quil/dev.clj` and can be customized for live coding. The main sketch in `core.clj` is only run with `lein run`.

---

## Quick Reference: Reloading Code in Emacs/CIDER

| What you changed         | What to do in Emacs/REPL                |
|-------------------------|------------------------------------------|
| A single function       | Place cursor in function, press `C-c C-c` (or `C-M-x`) |
| The whole buffer/file   | Press `C-c C-k`                          |
| Need to restart sketch  | `(patterning_quil.dev/stop-sketch)` then `(patterning_quil.dev/start-sketch)` |
| Pattern only            | `(reset! patterning_quil.core/current-pattern ...)` |
| Reload all namespaces   | `(patterning_quil.dev/refresh-all)`      |
| Reload dev namespace    | `(require 'patterning_quil.dev :reload)` |

- `C-c C-c` (or `C-M-x`): Evaluate the function at point.
- `C-c C-k`: Reload the entire buffer.
- Use the REPL commands above for sketch and pattern management.

