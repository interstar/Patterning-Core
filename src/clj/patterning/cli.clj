(ns patterning.cli
  (:require [patterning.core :as core]
            [patterning.view :refer [make-svg]]
            [patterning.groups :as groups]
            [patterning.sshapes :as sshapes]
            [patterning.layouts :as layouts]
            [patterning.library.std :as std]
            [patterning.library.douat :as douat]
            [patterning.color :as color]
            [patterning.maths :as maths]
            [patterning.dynamic :as dynamic]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :as pp]))

(defn- handle-unable-to-resolve-symbol [e filepath]
  (let [message (.getMessage e)
        cause (.getCause e)
        cause-message (when cause (.getMessage cause))
        symbol-match (or (re-find #"Unable to resolve symbol: (\\w+)" message)
                         (re-find #"Unable to resolve symbol: (\\w+)" cause-message))
        symbol-name (second symbol-match)]
    (println (str "ERROR: Function \"" symbol-name "\" not recognized in " filepath))
    (println (str "ERROR: Check if \"" symbol-name "\" is spelled correctly and available in the evaluation context"))))

(defn- handle-wrong-number-of-args [e filepath]
  (let [message (.getMessage e)
        cause (.getCause e)
        cause-message (when cause (.getMessage cause))
        args-match (or (re-find #"Wrong number of args.*passed to: ([^\\s]+)" message)
                       (re-find #"Wrong number of args.*passed to: ([^\\s]+)" cause-message))
        function-name (second args-match)]
    (println (str "ERROR: Wrong number of arguments passed to \"" function-name "\" in " filepath))
    (println (str "ERROR: Check the function signature for \"" function-name "\""))))

(defn- handle-no-such-var [e filepath]
  (let [message (.getMessage e)
        var-match (re-find #"No such var: ([^\\s]+)" message)
        var-name (second var-match)]
    (println (str "ERROR: Variable \"" var-name "\" not found in " filepath))
    (println (str "ERROR: Check if \"" var-name "\" is defined and available"))))

(defn- handle-no-implementation-of-method [e filepath]
  (let [message (.getMessage e)
        method-match (re-find #"No implementation of method.*of protocol.*for.*: (\\w+)" message)
        type-name (second method-match)]
    (println (str "ERROR: Type \"" type-name "\" does not implement required protocol in " filepath))))

(defn- handle-class-cast-exception [e filepath]
  (let [message (.getMessage e)
        cast-match (re-find #"java.lang.ClassCastException: ([^\\s]+) cannot be cast to ([^\\s]+)" message)
        from-type (second cast-match)
        to-type (nth cast-match 2)]
    (println (str "ERROR: Type mismatch in " filepath ": " from-type " cannot be cast to " to-type))))

(defn- handle-divide-by-zero [filepath]
  (println (str "ERROR: Divide by zero error in " filepath))
  (println "ERROR: Check for random functions that might return 0 (rand-nth, rand-int, etc.)"))

(defn- handle-no-value-for-key [e filepath]
  (let [message (.getMessage e)
        key-match (re-find #"No value supplied for key: :(\\w+)" message)
        key-name (second key-match)]
    (println (str "ERROR: Missing required key \":" key-name "\" in " filepath))
    (println (str "ERROR: Check that all required keys are provided in map literals"))))

(defn extract-clean-error [exception filepath]
  "Extract clean, actionable error information from exception"
  (let [message (.getMessage exception)
        cause (.getCause exception)
        cause-message (when cause (.getMessage cause))]
    (cond
      (or (re-find #"Unable to resolve symbol: (\\w+)" message)
          (and cause-message (re-find #"Unable to resolve symbol: (\\w+)" cause-message)))
      (handle-unable-to-resolve-symbol exception filepath)

      (or (re-find #"Wrong number of args.*passed to: ([^\\s]+)" message)
          (and cause-message (re-find #"Wrong number of args.*passed to: ([^\\s]+)" cause-message)))
      (handle-wrong-number-of-args exception filepath)

      (re-find #"No such var: ([^\\s]+)" message)
      (handle-no-such-var exception filepath)

      (re-find #"No implementation of method.*of protocol.*for.*: (\\w+)" message)
      (handle-no-implementation-of-method exception filepath)

      (re-find #"java.lang.ClassCastException: ([^\\s]+) cannot be cast to ([^\\s]+)" message)
      (handle-class-cast-exception exception filepath)

      (re-find #"Divide by zero" message)
      (handle-divide-by-zero filepath)

      (re-find #"No value supplied for key: :(\\w+)" message)
      (handle-no-value-for-key exception filepath)

      :else
      (println (str "ERROR: " message " in " filepath)))))

(defn diagnose-common-issues [content exception]
  "Provide helpful diagnostics for common pattern issues"
  (println "DIAGNOSTICS: Analyzing common issues...")
  
  ;; Check for common syntax issues
  (when (re-find #"Unable to resolve var" (.getMessage exception))
    (println "DIAGNOSTICS: 'Unable to resolve var' error detected")
    (println "DIAGNOSTICS: This usually means a function is not available in the evaluation context")
    (println "DIAGNOSTICS: Check if the function name is spelled correctly and is from a supported namespace"))
  
  (when (re-find #"Wrong number of args" (.getMessage exception))
    (println "DIAGNOSTICS: 'Wrong number of args' error detected")
    (println "DIAGNOSTICS: This usually means a function is being called with incorrect arguments")
    (println "DIAGNOSTICS: Check the function signature and argument count"))
  
  (when (re-find #"Divide by zero" (.getMessage exception))
    (println "DIAGNOSTICS: 'Divide by zero' error detected")
    (println "DIAGNOSTICS: This usually means a random function returned 0 when a positive number was expected")
    (println "DIAGNOSTICS: Check for rand-nth, rand-int, or other random functions that might return 0"))
  
  (when (re-find #"No value supplied for key" (.getMessage exception))
    (println "DIAGNOSTICS: 'No value supplied for key' error detected")
    (println "DIAGNOSTICS: This usually means a map is missing a required key")
    (println "DIAGNOSTICS: Check map literals and function calls that expect specific keys"))
  
  ;; Check for threading macro issues
  (when (re-find #"->" content)
    (println "DIAGNOSTICS: Threading macro (->) detected in pattern")
    (println "DIAGNOSTICS: If this fails, it might be a threading macro expansion issue"))
  
  (when (re-find #">>" content)
    (println "DIAGNOSTICS: Threading macro (->>) detected in pattern")
    (println "DIAGNOSTICS: If this fails, it might be a threading macro expansion issue"))
  
  ;; Check for common function patterns
  (when (re-find #"poly\s*\(\s*0\s*\)" content)
    (println "DIAGNOSTICS: poly(0) detected - this will cause divide by zero")
    (println "DIAGNOSTICS: poly needs at least 3 arguments: x, y, radius, and sides"))
  
  (when (re-find #"grid\s*\(\s*\d+\s*\)" content)
    (println "DIAGNOSTICS: grid with only one argument detected")
    (println "DIAGNOSTICS: grid needs two arguments: size and a sequence of groups"))
  
  (println "DIAGNOSTICS: Analysis complete"))

(defn analyze-pattern-result [result filepath]
  "Analyze the result of pattern evaluation and provide focused diagnostics"
  (cond
    (nil? result)
    (println "ERROR: Pattern evaluation returned nil - this usually means a function failed silently or the final expression doesn't return a pattern")
    
    (not (seq? result))
    (println (str "ERROR: Expected a sequence of SShapes, got: " (type result)))
    
    (empty? result)
    (println "ERROR: Pattern is an empty sequence")
    
    :else
    (println (str "DEBUG: Pattern has " (count result) " elements"))))

(defn debug-pattern-evaluation [content filepath]
  "Debug pattern evaluation by trying to evaluate parts of the code"
  (println "DEBUG: Attempting step-by-step evaluation...")
  (let [sci-ctx (dynamic/get-sci-context)
        lines (str/split-lines content)
        ;; Try to find function definitions and test them individually
        def-lines (filter #(re-find #"^\s*\(def" %) lines)]
    
    (println (str "DEBUG: Found " (count def-lines) " definitions"))
    (doseq [line def-lines]
      (println (str "DEBUG: Definition: " line)))
    
    ;; Try to evaluate just the definitions without the final expression
    (try
      (let [defs-only (str/join "\n" def-lines)]
        (println "DEBUG: Evaluating definitions only...")
        (dynamic/evaluate-pattern sci-ctx defs-only)
        (println "DEBUG: Definitions evaluated successfully"))
      (catch Exception e
        (println (str "DEBUG: Error evaluating definitions: " (.getMessage e)))))))

(defn read-pattern-file [filepath]
  "Read and evaluate a pattern from a file using shared SCI context"
  (let [content (slurp filepath)
        sci-ctx (dynamic/get-sci-context)]
    (try
      (let [result (dynamic/evaluate-pattern sci-ctx content)
            pattern (if (map? result) [result] result)] ; Wrap single maps
        (when (nil? result)
          (analyze-pattern-result result filepath)
          ;; Try with error handling to see if there's a hidden exception
          (let [error-result (dynamic/evaluate-pattern-with-error-handling sci-ctx content)]
            (when-not (:success error-result)
              (println "ERROR: Found hidden exception:")
              (println (str "ERROR: " (.getMessage (:error error-result))))
              (println "ERROR: Stack trace:")
              (.printStackTrace (:error error-result)))))
        pattern)
      (catch Exception e
        (println "ERROR: Failed to evaluate pattern")
        (extract-clean-error e filepath)
        (diagnose-common-issues content e)
        (debug-pattern-evaluation content filepath)
        (println "ERROR: Full stack trace:")
        (.printStackTrace e)
        (throw e)))))


(defn validate-pattern [pattern]
  "Validate that the pattern is a valid Group"
  (try
    (if (groups/validate-group pattern)
      true
      (do
        (println "ERROR: Pattern validation failed")
        (println (str "Invalid pattern: " (groups/explain-group pattern)))
        (let [error-file "failed-pattern.edn"]
          (spit error-file (with-out-str (pp/pprint pattern)))
          (println (str "ERROR: The invalid pattern data has been written to " error-file)))
        false))
    (catch Exception e
      (println "ERROR: Exception during pattern validation")
      (extract-clean-error e "pattern validation")
      (let [error-file "failed-pattern.edn"]
        (spit error-file (with-out-str (pp/pprint pattern)))
        (println (str "ERROR: The pattern that failed validation has been written to " error-file)))
      (.printStackTrace e)
      false)))

(defn generate-svg [pattern output-path width height]
  "Generate SVG from pattern and write to file"
  (try
    (let [svg-content (make-svg width height pattern)]
      (spit output-path svg-content)
      (println (str "Generated SVG: " output-path)))
    (catch Exception e
      (println "ERROR: Failed to generate SVG")
      (extract-clean-error e output-path)
      (let [error-file "failed-pattern.edn"]
        (spit error-file (with-out-str (pp/pprint pattern)))
        (println (str "ERROR: The failing pattern data has been written to " error-file)))
      (throw e))))

(defn generate-ps [pattern output-path width height]
  "Generate PostScript from pattern and write to file"
  ;; TODO: Implement PostScript generation
  (println "PostScript generation not yet implemented"))

(defn process-pattern-file [input-path output-path format width height]
  "Main processing function"
  (try
    (println (str "Processing: " input-path))
    (let [pattern (read-pattern-file input-path)]
      (if (validate-pattern pattern)
        (case format
          "svg" (generate-svg pattern output-path width height)
          "ps" (generate-ps pattern output-path width height)
          (do
            (println "ERROR: Unsupported format:" format)
            (println "ERROR: Supported formats are: svg, ps")
            (System/exit 1)))
        (do
          (println "ERROR: Pattern validation failed")
          (System/exit 1))))
    (catch Exception e
      (println "ERROR: Error processing file:" (.getMessage e))
      (System/exit 1))))

(defn -main [& args]
  "Command line interface for Patterning"
  (if (< (count args) 3)
    (do
      (println "Usage: lein run -m patterning.cli <input-file> <output-file> <format> [width] [height]")
      (println "  input-file:  Path to ClojureScript pattern file")
      (println "  output-file: Path for output file")
      (println "  format:      svg or ps")
      (println "  width:       SVG width (default: 800)")
      (println "  height:      SVG height (default: 800)")
      (System/exit 1))
    (let [[input-path output-path format & rest-args] args
          width (if (first rest-args) (Integer/parseInt (first rest-args)) 800)
          height (if (second rest-args) (Integer/parseInt (second rest-args)) 800)]
      (process-pattern-file input-path output-path format width height))))
