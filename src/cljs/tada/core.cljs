(ns tada.core
(:require
[goog.dom :as dom]
[tiltontec.model.core :refer [<mget] :as md]
[mxweb.html :refer [tag-dom-create]]
[tada.matrix :refer [matrix-build!]]
))
(defn mount-root []
(let [root (dom/getElement "app")
app-matrix (matrix-build!)]
(set! (.-innerHTML root) nil)
(dom/appendChild root
(tag-dom-create
(<mget app-matrix :mx-dom)))))
(defn ^:after-load re-render []
(mount-root))
(defn ^:export init []
(mount-root))
(defonce init-block (init))
