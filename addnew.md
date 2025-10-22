# Adding a New Function to Patterning

This guide explains how to add a new function to the Patterning library and ensure it's available everywhere: workbench, CLI, tutorials, and other places.

## Overview

When you add a new function to Patterning, you need to update several places to ensure it's available in all environments:

1. **Define the function** in the appropriate namespace
2. **Add to key-bindings** in the dynamic evaluation system
3. **Update the `:allow` list** in SCI contexts
4. **Test in all environments**

## Step-by-Step Process

### 1. Define Your Function

Add your function to the appropriate namespace in `src/cljc/patterning/`. For example, if adding a color function:

**File: `src/cljc/patterning/color.cljc`**
```clojure
(defn my-new-function
  "Description of what this function does"
  [param1 param2]
  ;; Your function implementation
  )
```

### 2. Add to Key-Bindings

**File: `src/cljc/patterning/dynamic.cljc`**

Add your function to the key-bindings in the `get-sci-context-config` function:

```clojure
:key-bindings {'basic-turtle (get p-lib-turtle-sci 'basic-turtle)
               'l-system (get p-lib-lsystems-sci 'l-system)
               'PI (get p-maths-sci 'PI)
               'p-color (get p-color-sci 'p-color)
               'hex-color (get p-color-sci 'hex-color)
               'my-new-function (get p-color-sci 'my-new-function)  ; ← Add this line
               'poly (get p-lib-std-sci 'poly)
               ;; ... rest of key-bindings
               }
```

**Important**: Use the correct namespace reference:
- For `patterning.color` functions: `(get p-color-sci 'function-name)`
- For `patterning.layouts` functions: `(get p-layouts-sci 'function-name)`
- For `patterning.library.std` functions: `(get p-lib-std-sci 'function-name)`
- etc.

### 3. No Need to Update `:allow` List

**Good news!** The `:allow` list is now centralized and automatically includes all Patterning functions. Once you add your function to the key-bindings (step 2), it will be automatically available in both the workbench and CLI.

The `:allow` list is managed by `get-core-allow-list` in `dynamic.cljc` and includes all functions from `(keys all-bindings)`, which covers all your Patterning functions.

### 4. That's It for SCI Configuration!

**No additional changes needed!** The SCI context automatically picks up your function through the key-bindings you added in step 2. Both the workbench and CLI will have access to your function.

### 5. Test Your Function

#### A. Test in Workbench
1. Rebuild: `lein build-workbench`
2. Open `http://localhost:8002/`
3. Try your function: `(my-new-function param1 param2)`

#### B. Test in CLI
1. Create a test file with your function
2. Run: `lein run -m patterning.cli test-file.clj output.svg svg 400 400`

#### C. Test in Tutorial Generation
1. Create a markdown file with a pattern using your function
2. Run: `python tutorial/generate_pattern_page.py input.md output.html tutorial_root`

### 6. Update Documentation

Add your function to any relevant documentation:

- **API documentation** (if you have it)
- **Function reference** (if you maintain one)
- **Example patterns** using your new function

## Common Issues and Solutions

### Issue: "Could not resolve symbol"
**Cause**: Function not in key-bindings or `:allow` list
**Solution**: Check steps 2 and 3 above

### Issue: Function works in CLI but not workbench
**Cause**: Missing from worker's `:allow` list
**Solution**: Add to `workbench/worker.cljs` `:allow` list

### Issue: Function works in workbench but not CLI
**Cause**: Missing from main key-bindings
**Solution**: Add to first key-bindings section in `dynamic.cljc`

### Issue: Function not accessible via namespace
**Cause**: Function not properly exported from namespace
**Solution**: Ensure function is public (not private with `defn-`)

## File Checklist

When adding a new function, update these files:

- [ ] **Function definition**: `src/cljc/patterning/[namespace].cljc`
- [ ] **Key-bindings**: `src/cljc/patterning/dynamic.cljc` (both sections)
- [ ] **Worker allow list**: `workbench/worker.cljs`
- [ ] **Test in workbench**: Rebuild and test
- [ ] **Test in CLI**: Create test pattern
- [ ] **Test in tutorial**: Generate tutorial page
- [ ] **Update documentation**: Add to relevant docs

## Example: Adding a `hsl-color` Function

Here's a complete example of adding an HSL color function:

### 1. Add to `src/cljc/patterning/color.cljc`:
```clojure
(defn hsl-color
  "Convert HSL values to p-color format"
  ([h s l] (hsl-color h s l 255))
  ([h s l a]
   ;; HSL to RGB conversion implementation
   (let [r (hsl-to-rgb h s l)
         g (hsl-to-rgb h s l) 
         b (hsl-to-rgb h s l)]
     (p-color r g b a))))
```

### 2. Add to `src/cljc/patterning/dynamic.cljc`:
```clojure
:key-bindings {'basic-turtle (get p-lib-turtle-sci 'basic-turtle)
               ;; ... existing bindings
               'hsl-color (get p-color-sci 'hsl-color)
               ;; ... rest of bindings
               }
```

### 3. Add to `workbench/worker.cljs`:
```clojure
:allow (set (concat (keys all-bindings)
                   ['let 'let* 'def 'defn 'fn 'fn* 'if 'when 'cond 'case 'do '-> '->>
                    ;; ... existing allows
                    'hsl-color
                    ]))
```

### 4. Test:
```clojure
;; In workbench or CLI:
(hsl-color 120 100 50)        ; Green
(p-color/hsl-color 0 100 50)  ; Red via namespace
```

## Summary

**Simplified Process:** To add a new function to Patterning:

1. **Add the function** to the appropriate namespace
2. **Add to key-bindings** in `dynamic.cljc` 
3. **Rebuild** the workbench: `lein build-workbench`
4. **Test** in workbench, CLI, and tutorial generation

**That's it!** The `:allow` list is now centralized and automatically includes all Patterning functions, so you don't need to manually update it anymore.

The key insight is that Patterning uses **two different SCI contexts**:
1. **Main context** (for CLI) - uses `get-sci-context`
2. **Worker context** (for workbench) - uses `get-sci-context-config`

Both automatically pick up your function through the centralized key-bindings configuration, eliminating the need for manual `:allow` list updates.
