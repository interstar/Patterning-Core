(ns patterning.api
 (:require [patterning.maths :as maths])
  (:require [patterning.sshapes :refer [->SShape add-property]])

  (:require [patterning.groups :as groups])
  (:require [patterning.layouts :refer [framed clock-rotate stack grid-layout diamond-layout
                                        four-mirror four-round nested-stack checked-layout
                                        half-drop-grid-layout random-turn-groups h-mirror
                                        superimpose-layout]])

  (:require [patterning.library.std :refer [poly spiral horizontal-line]])
  (:require [patterning.library.turtle :refer [basic-turtle]])
  (:require [patterning.library.complex_elements :refer [vase zig-zag]])
  (:require [patterning.library.l_systems :refer [l-system]])

  (:require [patterning.view :refer [make-txpt make-svg transformed-sshape ]])
  (:require [patterning.color :refer [p-color]])

  (:require [patterning.examples.framedplant :as framedplant])
  (:require [patterning.examples.design_language1 :as design-language])
  (:require [patterning.library.symbols :as symbols])


  (:gen-class
   :name com.alchemyislands.patterning.api
   :methods [#^{:static true} [makeTransformPointFunction [double double double double double double double double] clojure.lang.IFn ]
             #^{:static true} [transformSShape [clojure.lang.IFn clojure.lang.IPersistentMap] clojure.lang.IPersistentMap ]


             #^{:static true} [emptyPattern [] clojure.lang.IPersistentVector]
             #^{:static true} [poly [float float float float clojure.lang.IPersistentMap] clojure.lang.IPersistentVector]
             #^{:static true} [basicTurtle [float float float float float String clojure.lang.IPersistentMap
                                            clojure.lang.IPersistentMap] clojure.lang.IPersistentVector ]
             #^{:static true} [emptyMap [] clojure.lang.IPersistentMap]

             #^{:static true} [setStroke [clojure.lang.IPersistentMap int int int int] clojure.lang.IPersistentMap]
             #^{:static true} [setFill   [clojure.lang.IPersistentMap int int int int] clojure.lang.IPersistentMap]
             #^{:static true} [setStrokeWeight [clojure.lang.IPersistentMap int] clojure.lang.IPersistentMap]
             #^{:static true} [removeProperty [clojure.lang.IPersistentMap clojure.lang.Keyword ] clojure.lang.IPersistentMap]

             #^{:static true} [superimpose [clojure.lang.IPersistentVector clojure.lang.IPersistentVector]
                               clojure.lang.IPersistentVector ]
             #^{:static true} [grid [int clojure.lang.IPersistentVector] clojure.lang.IPersistentVector]
             #^{:static true} [gridList [int "[Lclojure.lang.IPersistentVector;"] clojure.lang.IPersistentVector]

             #^{:static true} [fourRound [clojure.lang.IPersistentVector] clojure.lang.IPersistentVector]
             #^{:static true} [fourMirror [clojure.lang.IPersistentVector] clojure.lang.IPersistentVector]
             #^{:static true} [clock [int clojure.lang.IPersistentVector] clojure.lang.IPersistentVector]

             #^{:static true} [makeLSystem [ "[[Ljava.lang.String;" ] clojure.lang.IFn]
             #^{:static true} [runLSystem [clojure.lang.IFn int String] String]

             #^{:static true} [makeSVG ["[Ljava.lang.Float;" float float clojure.lang.IPersistentVector] String]
             #^{:static true} [spit [String String] Object ]
             #^{:static true} [makeShapePattern [clojure.lang.IPersistentMap "[[Ljava.lang.Float;" ] clojure.lang.IPersistentVector]
             ] )
  )



; Rendering
(defn -makeTransformPointFunction [vx1 vy1 vx2 vy2 wx1 wy1 wx2 wy2] (make-txpt [vx1 vy1 vx2 vy2] [wx1 wy1 wx2 wy2]))
(defn -transformSShape [txpt sshape] (transformed-sshape txpt sshape))
(defn -makeSVG [viewport width height group] (make-svg viewport [0 0 width height] width height group))


(defn -emptyMap [] {})

; Styles
(defn -setStroke [style r g b a] (add-property style :stroke (p-color r g b a) ))
(defn -setFill [style r g b a] (add-property style :fill (p-color r g b a)))
(defn -setStrokeWeight [style w] (add-property style :stroke-weight w))
(defn -removeProperty [style prop] (dissoc style prop))

(defn -emptyPattern [] (groups/empty-pattern))

; Shape making
(defn -makeShapePattern [style points] [(->SShape style points)])
(defn -poly [cx cy radius no-sides style] (poly cx cy radius no-sides style))
(defn -basicTurtle [startX startY d a da script leafMap style] (basic-turtle [startX startY] d a da script leafMap style))


; Layouts (simplified)
(defn -superimpose [p1 p2] (superimpose-layout p1 p2))

(defn -grid [size p] (into [] (grid-layout size (repeat p))))
(defn -gridList [size p] (into [] (grid-layout size (cycle p)) ))

(defn -fourRound [p] (into [] (four-round p)))
(defn -fourMirror [p] (into [] (four-mirror p)))
(defn -clock [n p] (into [] (clock-rotate n p)))


; L-Systems
(defn -makeLSystem [rules] (l-system rules))
(defn -runLSystem [l-sys steps seed] (l-sys steps seed ))

(defn -spit [fName f] (spit fName f))
