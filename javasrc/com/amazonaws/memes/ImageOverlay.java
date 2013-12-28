/*
 * Copyright 2012-2013 Amazon Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package com.amazonaws.memes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import clojure.lang.Keyword;
import clojure.lang.PersistentHashMap;

/**
 * Worker that knows how to overlay text onto an image.
 */
public final class ImageOverlay {

  public static PersistentHashMap calculateText(Graphics g,
                                  String text,
                                  BufferedImage image,
                                  int SIDE_MARGIN,
                                  int MAX_FONT_SIZE)
  {
    int height = 0;
    int fontSize = MAX_FONT_SIZE;
    int maxCaptionHeight = image.getHeight() / 5;
    int maxLineWidth = image.getWidth() - SIDE_MARGIN * 2;
    String formattedString = "";

    do {
      g.setFont(new Font("Arial", Font.BOLD, fontSize));

      // first inject newlines into the text to wrap properly
      StringBuilder sb = new StringBuilder();
      int left = 0;
      int right = text.length() - 1;
      while ( left < right ) {

        String substring = text.substring(left, right + 1);
        Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(substring, g);
        while ( stringBounds.getWidth() > maxLineWidth ) {

          // look for a space to break the line
          boolean spaceFound = false;
          for ( int i = right; i > left; i-- ) {
            if ( text.charAt(i) == ' ' ) {
              right = i - 1;
              spaceFound = true;
              break;
            }
          }
          substring = text.substring(left, right + 1);
          stringBounds = g.getFontMetrics().getStringBounds(substring, g);

          // If we're down to a single word and we are still too wide,
          // the font is just too big.
          if ( !spaceFound && stringBounds.getWidth() > maxLineWidth ) {
            break;
          }
        }
        sb.append(substring).append("\n");
        left = right + 2;
        right = text.length() - 1;
      }

      formattedString = sb.toString();

      // now determine if this font size is too big for the allowed height
      height = 0;
      for ( String line : formattedString.split("\n") ) {
        Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(line, g);
        height += stringBounds.getHeight();
      }
      fontSize--;
    } while ( height > maxCaptionHeight );

    return PersistentHashMap.create(Keyword.intern("formattedtext"), formattedString,
                                    Keyword.intern("fontsize"), fontSize,
                                    Keyword.intern("height"), height);
  }
}
