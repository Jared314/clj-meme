(ns clj-meme.core
  (:require [clojure.java.io :as io])
  (:import [com.amazonaws.memes ImageOverlay]
           [javax.imageio ImageIO]
           [java.io ByteArrayOutputStream]))

(defn readImageIntoBuffer [image]
  (let [baos (ByteArrayOutputStream.)]
    (try
      (ImageIO/write image "png" baos)
      (.toByteArray baos)
      (finally (.close baos)))))

;; Returns PNG compressed byte[] data
(defn render-image [image-path top-caption bottom-caption]
  (let [base (ImageIO/read (io/file image-path))
        rendered (ImageOverlay/overlay base top-caption bottom-caption)]
    (readImageIntoBuffer rendered)))

(defn generate-image! [image-path top-caption bottom-caption output-path]
  (let [content (render-image image-path top-caption bottom-caption)]
    (with-open [w (io/output-stream output-path)]
      (.write w content))))
