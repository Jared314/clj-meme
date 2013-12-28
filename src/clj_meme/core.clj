(ns clj-meme.core
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.pprint :as pp])
  (:import [com.amazonaws.memes ImageOverlay]
           [javax.imageio ImageIO]
           [java.io ByteArrayOutputStream]
           [java.awt.image BufferedImage]
           [java.awt Font FontMetrics Color Graphics]
           [java.awt.geom Rectangle2D]))

(set! *warn-on-reflection* true)
;; Defaults
(def MAX_FONT_SIZE 48)
(def BOTTOM_MARGIN 10)
(def TOP_MARGIN 5)
(def SIDE_MARGIN 10)



(defn- drawStringCentered2 [^Graphics g
                            ^BufferedImage image
                            y
                            line]
  (let [stringBounds (.. g getFontMetrics (getStringBounds line g))
        x (int (/ (- (.getWidth image) (.getWidth stringBounds)) 2))]
    (doto g
      (.setColor Color/BLACK)
      (.drawString line (+ x 2) (+ y 2))
      (.setColor Color/WHITE)
      (.drawString line x y))
    (+ y (.. g getFontMetrics getHeight))))

(defn- drawStringCentered
  ([g text image top]
   (let [{:keys [formattedtext fontsize height]} (ImageOverlay/calculateText g text image SIDE_MARGIN MAX_FONT_SIZE)]
     (drawStringCentered g formattedtext image top height fontsize TOP_MARGIN BOTTOM_MARGIN)))
  ([^Graphics g text image top height fontsize top-margin bottom-margin]
   (let [y (if top
             (+ TOP_MARGIN (-> g (.getFontMetrics) (.getHeight)))
             (+ (-> g (.getFontMetrics) (.getHeight))
                (- (.getHeight image) height BOTTOM_MARGIN)))]
     (.setFont g (Font. "Arial" Font/BOLD fontsize))
     (reduce (partial drawStringCentered2 g image)
             y
             (string/split-lines text)))))

(defn- overlay-text [^BufferedImage image top-caption bottom-caption]
  (let [g (.getGraphics image)]
    (when top-caption (drawStringCentered g top-caption image true))
    (when bottom-caption (drawStringCentered g bottom-caption image false))
    image))


;; Returns java.awt.image.BufferedImage
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
