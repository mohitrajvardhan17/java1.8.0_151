package com.sun.image.codec.jpeg;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.InputStream;
import java.io.OutputStream;
import sun.awt.image.codec.JPEGImageDecoderImpl;
import sun.awt.image.codec.JPEGImageEncoderImpl;
import sun.awt.image.codec.JPEGParam;

public class JPEGCodec
{
  private JPEGCodec() {}
  
  public static JPEGImageDecoder createJPEGDecoder(InputStream paramInputStream)
  {
    return new JPEGImageDecoderImpl(paramInputStream);
  }
  
  public static JPEGImageDecoder createJPEGDecoder(InputStream paramInputStream, JPEGDecodeParam paramJPEGDecodeParam)
  {
    return new JPEGImageDecoderImpl(paramInputStream, paramJPEGDecodeParam);
  }
  
  public static JPEGImageEncoder createJPEGEncoder(OutputStream paramOutputStream)
  {
    return new JPEGImageEncoderImpl(paramOutputStream);
  }
  
  public static JPEGImageEncoder createJPEGEncoder(OutputStream paramOutputStream, JPEGEncodeParam paramJPEGEncodeParam)
  {
    return new JPEGImageEncoderImpl(paramOutputStream, paramJPEGEncodeParam);
  }
  
  public static JPEGEncodeParam getDefaultJPEGEncodeParam(BufferedImage paramBufferedImage)
  {
    int i = JPEGParam.getDefaultColorId(paramBufferedImage.getColorModel());
    return getDefaultJPEGEncodeParam(paramBufferedImage.getRaster(), i);
  }
  
  public static JPEGEncodeParam getDefaultJPEGEncodeParam(Raster paramRaster, int paramInt)
  {
    JPEGParam localJPEGParam = new JPEGParam(paramInt, paramRaster.getNumBands());
    localJPEGParam.setWidth(paramRaster.getWidth());
    localJPEGParam.setHeight(paramRaster.getHeight());
    return localJPEGParam;
  }
  
  public static JPEGEncodeParam getDefaultJPEGEncodeParam(int paramInt1, int paramInt2)
    throws ImageFormatException
  {
    return new JPEGParam(paramInt2, paramInt1);
  }
  
  public static JPEGEncodeParam getDefaultJPEGEncodeParam(JPEGDecodeParam paramJPEGDecodeParam)
    throws ImageFormatException
  {
    return new JPEGParam(paramJPEGDecodeParam);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\image\codec\jpeg\JPEGCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */