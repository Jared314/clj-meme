(ns clj-meme.drawing
  (:require [clojure.string :as string])
  (:import [java.io ByteArrayOutputStream]
           [java.awt.image BufferedImage]
           [java.awt Font FontMetrics Color Graphics]
           [java.awt.geom Rectangle2D]))

;; Defaults
(def MAX_FONT_SIZE 48)
(def BOTTOM_MARGIN 10)
(def TOP_MARGIN 5)
(def SIDE_MARGIN 10)

(defn- split-with2 [f coll]
  (loop [a []
         b coll]
    (let [item (first b)]
      (if (and item (f a item))
        (recur (conj a item) (rest b))
        [a b]))))

(defn- width-check [g width a b]
  (<= (.. g
          getFontMetrics
          (getStringBounds (string/join " " (conj a b)) g)
          getWidth)
      width))

(defn- split-string [g text maxLineWidth]
  (loop [lines []
         words (string/split text #"\s")]
    (let [[l w] (split-with2 (partial width-check g maxLineWidth) words)]
      (if (empty? l)
        (if (empty? w)
          lines
          (conj lines (string/join " " w)))
        (recur (conj lines (string/join " " l)) w)))))

(defn- calculateSize [g text]
  (reduce
   (fn [[h maxWidth] line]
     (let [stringBounds (.. g getFontMetrics (getStringBounds line g))]
       [(+ h (.getHeight stringBounds))
        (max maxWidth (Math/ceil (.getWidth stringBounds)))]))
   [0 0]
   (string/split-lines text)))

(defn calculate-text
  ([g text image] (calculate-text g text image SIDE_MARGIN MAX_FONT_SIZE))
  ([g text image side-margin max-font-size]
   (let [maxCaptionHeight (/ (.getHeight image) 5) ;; 1/5th image height
         maxLineWidth (- (.getWidth image) (* SIDE_MARGIN 2))]
     (loop [fontsize max-font-size]
       (.setFont g (Font. "Arial" Font/BOLD fontsize))
       (let [formattedString (string/join "\n" (split-string g text maxLineWidth))
             [h linewidth] (calculateSize g formattedString)]
         (if (and (<= h maxCaptionHeight) (<= linewidth maxLineWidth))
           {:fontsize fontsize
            :height (int h)
            :formattedtext formattedString}
           (recur (dec fontsize))))))))



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
