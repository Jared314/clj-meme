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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.imageio.ImageIO;


public class MemeWorker
{
  public static byte[] processImage(File basefile, String topCaption, String bottomCaption)
    throws IOException
  {
    BufferedImage sourceImage = ImageIO.read(basefile);
    BufferedImage finishedImage = ImageOverlay.overlay(sourceImage, topCaption, bottomCaption);
    return readImageIntoBuffer(finishedImage);
  }

  private static byte[] readImageIntoBuffer(BufferedImage image) throws IOException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      ImageIO.write(image, "png", baos);
      return baos.toByteArray();
    } finally {
      baos.close();
    }
  }
}
