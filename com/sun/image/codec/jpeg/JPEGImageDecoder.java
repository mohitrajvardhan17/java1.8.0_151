package com.sun.image.codec.jpeg;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;

public abstract interface JPEGImageDecoder
{
  public abstract JPEGDecodeParam getJPEGDecodeParam();
  
  public abstract void setJPEGDecodeParam(JPEGDecodeParam paramJPEGDecodeParam);
  
  public abstract InputStream getInputStream();
  
  public abstract Raster decodeAsRaster()
    throws IOException, ImageFormatException;
  
  public abstract BufferedImage decodeAsBufferedImage()
    throws IOException, ImageFormatException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\image\codec\jpeg\JPEGImageDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */