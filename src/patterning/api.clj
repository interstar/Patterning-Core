(ns patterning.api
 (:require [patterning.maths :as maths])
  (:require [patterning.sshapes :refer [->SShape ]])

  (:require [patterning.groups :as groups])
  (:require [patterning.layouts :refer [framed clock-rotate stack grid-layout diamond-layout
                                        four-mirror four-round nested-stack checked-layout
                                        half-drop-grid-layout random-turn-groups h-mirror]])

  (:require [patterning.library.std :refer [poly spiral horizontal-line]])
  (:require [patterning.library.turtle :refer [basic-turtle]])
  (:require [patterning.library.complex_elements :refer [vase zig-zag]])
  (:require [patterning.view :refer [make-txpt make-svg transformed-sshape ]])
  (:require [patterning.color :refer [p-color]])

  (:require [patterning.examples.tutorial :as tutorial])
  (:require [patterning.examples.framedplant :as framedplant])
  (:require [patterning.examples.design_language1 :as design-language])
  (:require [patterning.library.symbols :as symbols])

  (:require [patterning.examples.interactive :as interactive])
  (:require [patterning.examples.testing :as testing])


  (:gen-class
   :name com.alchemyislands.patterning.api
   :methods [#^{:static true} [makeTransformPointFunction [double double double double double double double double] clojure.lang.IFn ]
             #^{:static true} [transformSShape [clojure.lang.IFn clojure.lang.IPersistentMap] clojure.lang.IPersistentMap ]
             #^{:static true} [test1 [int] clojure.lang.IPersistentVector]
             #^{:static true} [spit [String clojure.lang.IPersistentVector] void]
             #^{:static true} [basicTurtle [float float float float float String clojure.lang.IPersistentMap
                                            clojure.lang.IPersistentMap] clojure.lang.IPersistentVector ]
             #^{:static true} [emptyMap [] clojure.lang.IPersistentMap]
             #^{:static true} [pColor ["[Ljava.lang.Integer;"] java.lang.Object ]
             ] )
  )

(defn -test1 [n] (testing/nested n))

(defn -makeTransformPointFunction [vx1 vy1 vx2 vy2 wx1 wy1 wx2 wy2] (make-txpt [vx1 vy1 vx2 vy2] [wx1 wy1 wx2 wy2]))

(defn -transformSShape [txpt sshape] (transformed-sshape txpt sshape))

(defn -emptyMap [] {})

(defn -pColor [args] (apply p-color args))

(defn -basicTurtle [startX startY d a da script leafMap style] (basic-turtle [startX startY] d a da script leafMap style))

(defn -spit [file-name data] (spit file-name data))
