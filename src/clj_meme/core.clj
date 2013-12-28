(ns clj-meme.core
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clj-meme.drawing :as drawing])
  (:import [javax.imageio ImageIO]
           [java.io ByteArrayOutputStream]
           [java.awt.image BufferedImage]))

;; Returns java.awt.image.BufferedImage
(defn render-image [image-path top-caption bottom-caption]
  (-> (io/file image-path)
      ImageIO/read
      (drawing/overlay-text top-caption bottom-caption)))

;; Returns Byte[] image
(defn generate-image [image-path top-caption bottom-caption]
  (let [output-format "png"
        output-format (case output-format
                        ;"jpg" output-format
                        "png" output-format
                        ;"gif" output-format
                        :default "png")
        content (render-image image-path top-caption bottom-caption)]
    (with-open [baos (ByteArrayOutputStream.)]
      (ImageIO/write content output-format baos)
      (.toByteArray baos))))

;; Returns Boolean success
(defn generate-image! [image-path top-caption bottom-caption output-path]
  (let [outfile (io/file output-path)
        outname (.getName outfile)
        output-format (string/lower-case (subs outname (inc (.lastIndexOf outname "."))))
        output-format (case output-format
                        ;"jpg" output-format
                        "png" output-format
                        ;"gif" output-format
                        :default "png")
        content (render-image image-path top-caption bottom-caption)]
    (with-open [w (io/output-stream output-path)]
      (ImageIO/write content output-format w))))
