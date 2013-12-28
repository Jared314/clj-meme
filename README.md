# clj-meme

A Clojure library designed to generate meme images.

## Usage

    (ns namespace1
      (:require [clj-meme.core :as meme]))

    (meme/generate-image! "./sadkitten.png"
                          "Top Text"
                          "Bottom Text"
                          "./output1.png")

## License

Copyright © 2013 Jared Lobberecht

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
