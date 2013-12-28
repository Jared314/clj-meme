(ns clj-meme.core
  (:require [clojure.java.io :as io])
  (:import [com.amazonaws.memes MemeWorker]))

;; Returns PNG compressed byte[] data
(defn render-image [image-path top-caption bottom-caption]
  (MemeWorker/processImage (io/file image-path) top-caption bottom-caption))

(defn generate-image! [image-path top-caption bottom-caption output-path]
  (let [content (render-image image-path top-caption bottom-caption)]
    (with-open [w (io/output-stream output-path)]
      (.write w content))))
