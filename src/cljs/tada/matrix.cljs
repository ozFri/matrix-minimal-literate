(ns tada.matrix
(:require
[tiltontec.util.core :as util]
[tiltontec.cell.core :refer-macros [cF cFn cFonce ] :refer [cI]]
[tiltontec.model.core
; todo trim
:refer-macros [with-par]
:refer [kid-values-kids matrix <mget mswap!> mxu-find-type] :as md]
[mxweb.gen
:refer-macros [section header h1 ul
li input]]
[clojure.string :as str]
[goog.dom.forms :as form]))
(def statements
["Roses are red"
"Violets are blue"
"Socrates is mortal"
"And so are you"])
(declare td-deleted td-completed make-todo)
(defn make-statement
"Make a matrix incarnation of a statement item"
[title]
;; So we key off a UUID for when we get to persistence, record a
;; fixed creation time, use a timestamp to denote "completed", and
;; use another timestamp for logical deletion.
(md/make
:id (util/uuidv4)
:created (util/now)
;; we wrap mutable slots as Cells...
:title (cI title)))
(defn mx-find-matrix [mx]
(assert mx)
(mxu-find-type mx ::md/tadaApp))
(defn mx-statements
"Given a node in the matrix, navigate to the root and read the todos. After
the matrix is initially loaded (say in an event handler), one can pass nil
and find the matrix in @matrix. Put another way, a starting node is required
during the matrix's initial build."
([]
(<mget @matrix :statements))
([mx]
(if (nil? mx)
(mx-statements)
(let [mtrx (mx-find-matrix mx)]
(assert mtrx)
(<mget mtrx :statements)))))
(defn statement-list [seed-statements]
(md/make ::statements
:statements-raw (cFn (for [s seed-statements]
(make-statement s)))
))
(defn input-bar []
(input {:class "input-bar"
;; :autofocus true
:placeholder "State your mind"
:onkeypress #(when (= (.-key %) "Enter")
(let [raw (form/getValue (.-target %))
title (str/trim raw)]
(when-not (str/blank? title)
(mswap!> (<mget @matrix :statements)
:statements-raw conj (make-statement title)))
;; (make-todo title)))
(form/setValue (.-target %) "")))}))
(defn statement-items-list []
(section {:class "main"}
(ul {:class "statement-list"}
{:kid-values (cF (<mget (mx-statements me) :statements-raw))
:kid-key #(<mget % :statement)
:kid-factory (fn [me statement]
(li (<mget statement :title)))}
;; cache is prior value for this implicit 'kids' slot; k-v-k uses it for diffing
(kid-values-kids me cache))))
(defn matrix-build! []
(reset! md/matrix
;; now we provide an optional "type" to support Matrix node space search
(md/make ::md/tadaApp
;;
;; HTML tag syntax is (<tag> [dom-attribute-map [custom-property map] children*]
;;
:statements (statement-list statements)
:input (cI "")
:mx-dom (cFonce
(with-par me
(section {:class "todoapp" :style "padding:24px"}
(header {:class "header"}
(h1 "Tada")
(input-bar)
(statement-items-list)
))
)))))
