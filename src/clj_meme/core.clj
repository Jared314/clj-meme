(ns clj-meme.core
  (:require [clojure.java.io :as io])
  (:import [com.amazonaws.memes ImageOverlay]
           [javax.imageio ImageIO]
           [java.io ByteArrayOutputStream]
           [java.awt.image BufferedImage]))


(defn- overlay-text [^BufferedImage image top-caption bottom-caption]
  (let [g (.getGraphics image)]
    (ImageOverlay/drawStringCentered g top-caption image true)
    (ImageOverlay/drawStringCentered g bottom-caption image false)
    image))

(defn- readImageIntoBuffer [image]
  (let [baos (ByteArrayOutputStream.)]
    (try
      (ImageIO/write image "png" baos)
      (.toByteArray baos)
      (finally (.close baos)))))



;; Returns PNG compressed byte[] data
(defn render-image [image-path top-caption bottom-caption]
  (-> (io/file image-path)
      ImageIO/read
      (overlay-text top-caption bottom-caption)
      readImageIntoBuffer))

(defn generate-image! [image-path top-caption bottom-caption output-path]
  (let [content (render-image image-path top-caption bottom-caption)]
    (with-open [w (io/output-stream output-path)]
      (.write w content))))
