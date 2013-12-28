(ns clj-meme.core
  (:require [clojure.java.io :as io]
            [clojure.string :as string])
  (:import [com.amazonaws.memes ImageOverlay]
           [javax.imageio ImageIO]
           [java.io ByteArrayOutputStream]
           [java.awt.image BufferedImage]))

;; Defaults
(def MAX_FONT_SIZE 48)
(def BOTTOM_MARGIN 10)
(def TOP_MARGIN 5)
(def SIDE_MARGIN 10)


(defn- drawStringCentered [g text image top]
  (ImageOverlay/drawStringCentered g
                                   text
                                   image
                                   top
                                   TOP_MARGIN
                                   BOTTOM_MARGIN
                                   SIDE_MARGIN
                                   MAX_FONT_SIZE))

(defn- overlay-text [^BufferedImage image top-caption bottom-caption]
  (let [g (.getGraphics image)]
    (drawStringCentered g top-caption image true)
    (drawStringCentered g bottom-caption image false)
    image))



;; Returns PNG compressed byte[] data
(defn render-image [image-path top-caption bottom-caption]
  (-> (io/file image-path)
      ImageIO/read
      (overlay-text top-caption bottom-caption)))

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
