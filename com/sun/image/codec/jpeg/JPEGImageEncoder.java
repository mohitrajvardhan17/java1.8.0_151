package com.sun.image.codec.jpeg;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.OutputStream;

public abstract interface JPEGImageEncoder
{
  public abstract OutputStream getOutputStream();
  
  public abstract void setJPEGEncodeParam(JPEGEncodeParam paramJPEGEncodeParam);
  
  public abstract JPEGEncodeParam getJPEGEncodeParam();
  
  public abstract JPEGEncodeParam getDefaultJPEGEncodeParam(BufferedImage paramBufferedImage)
    throws ImageFormatException;
  
  public abstract void encode(BufferedImage paramBufferedImage)
    throws IOException, ImageFormatException;
  
  public abstract void encode(BufferedImage paramBufferedImage, JPEGEncodeParam paramJPEGEncodeParam)
    throws IOException, ImageFormatException;
  
  public abstract int getDefaultColorId(ColorModel paramColorModel);
  
  public abstract JPEGEncodeParam getDefaultJPEGEncodeParam(Raster paramRaster, int paramInt)
    throws ImageFormatException;
  
  public abstract JPEGEncodeParam getDefaultJPEGEncodeParam(int paramInt1, int paramInt2)
    throws ImageFormatException;
  
  public abstract JPEGEncodeParam getDefaultJPEGEncodeParam(JPEGDecodeParam paramJPEGDecodeParam)
    throws ImageFormatException;
  
  public abstract void encode(Raster paramRaster)
    throws IOException, ImageFormatException;
  
  public abstract void encode(Raster paramRaster, JPEGEncodeParam paramJPEGEncodeParam)
    throws IOException, ImageFormatException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\image\codec\jpeg\JPEGImageEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */