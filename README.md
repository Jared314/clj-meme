# clj-meme

A Clojure library designed to locally generate meme images.

## Usage

Generate a `java.awt.image.BufferedImage` with the specified text:

    (ns myns
      (:require [clj-meme.core :as meme]))

    (meme/render-image "./sadkitten.png"
                       "Top Text"
                       "Bottom Text")

Generate a PNG encoded image and return the data as a byte array:

    (ns myns
      (:require [clj-meme.core :as meme]))

    (meme/generate-image "./sadkitten.png"
                         "Top Text"
                         "Bottom Text") ;; #<byte[] [B@27f8d56c>

Generate a PNG encoded image, write the image to the specified file, and return the success of the file operation:

    (ns myns
      (:require [clj-meme.core :as meme]))

    (meme/generate-image! "./sadkitten.png"
                          "Top Text"
                          "Bottom Text"
                          "./output1.png") ;; true


Generate a PNG encoded image from an exception:

    (ns myns
      (:require [clj-meme.core :as meme]))

    (defn handle-exception [output-path ^Exception ex]
      (meme/generate-image! "./sadkitten.png"
                   (.getName (type ex))
                   (.getMessage ex)
                   output-path))

    (def handle (partial handle-exception (str "./exception-"
                                               (System/currentTimeMillis)
                                               ".png")))

    (handle (ArithmeticException. "Divide by zero")) ;; true


## License

Copyright © 2013 Jared Lobberecht

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
