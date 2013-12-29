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

import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Worker that knows how to overlay text onto an image.
 */
public final class ImageOverlay {

  public static String wrapString(Graphics g, String text, int maxLineWidth)
  {
    StringBuilder sb = new StringBuilder();
    int left = 0;
    int right = text.length() - 1;
    while ( left < right )
    {

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

    return sb.toString();
  }
}
