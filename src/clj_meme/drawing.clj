(ns clj-meme.drawing
  (:require [clojure.string :as string])
  (:import [com.amazonaws.memes ImageOverlay]
           [java.io ByteArrayOutputStream]
           [java.awt.image BufferedImage]
           [java.awt Font FontMetrics Color Graphics]
           [java.awt.geom Rectangle2D]))

;; Defaults
(def MAX_FONT_SIZE 48)
(def BOTTOM_MARGIN 10)
(def TOP_MARGIN 5)
(def SIDE_MARGIN 10)

(defn calculate-text
  ([g text image]
   (calculate-text g text image SIDE_MARGIN MAX_FONT_SIZE))
  ([g text image side-margin max-font-size]
   (ImageOverlay/calculateText g text image side-margin max-font-size)))

(defn- drawStringCentered2 [^Graphics g image y line]
  (let [stringBounds (.. g getFontMetrics (getStringBounds line g))
        x (int (/ (- (.getWidth image) (.getWidth stringBounds)) 2))]
    (doto g
      (.setColor Color/BLACK)
      (.drawString line (+ x 2) (+ y 2))
      (.setColor Color/WHITE)
      (.drawString line x y))
    (+ y (.. g getFontMetrics getHeight))))

(defn drawStringCentered
  ([g text image top]
   (let [{:keys [formattedtext fontsize height]} (calculate-text g text image)]
     (drawStringCentered g formattedtext image top height fontsize TOP_MARGIN BOTTOM_MARGIN)))
  ([^Graphics g text image top height fontsize top-margin bottom-margin]
   (let [y (if top
             (+ top-margin (-> g (.getFontMetrics) (.getHeight)))
             (+ (-> g (.getFontMetrics) (.getHeight))
                (- (.getHeight image) height bottom-margin)))]
     (.setFont g (Font. "Arial" Font/BOLD fontsize))
     (reduce (partial drawStringCentered2 g image)
             y
             (string/split-lines text)))))

(defn overlay-text [^BufferedImage image top-caption bottom-caption]
  (let [g (.getGraphics image)]
    (when top-caption (drawStringCentered g top-caption image true))
    (when bottom-caption (drawStringCentered g bottom-caption image false))
    image))
