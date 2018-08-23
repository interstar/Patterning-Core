(ns patterning.macros
 )

(defmacro optional-styled-primitive [args body]
   (let [extra (conj args 'style)]
     `(fn (~extra (~'APattern (~'->SShape ~'style ~body)))
         (~args  (~'APattern (~'->SShape {} ~body)))
      )
   )
)
