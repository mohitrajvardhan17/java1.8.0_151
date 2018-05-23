package com.sun.image.codec.jpeg;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

public class TruncatedFileException
  extends RuntimeException
{
  private Raster ras = null;
  private BufferedImage bi = null;
  
  public TruncatedFileException(BufferedImage paramBufferedImage)
  {
    super("Premature end of input file");
    bi = paramBufferedImage;
    ras = paramBufferedImage.getData();
  }
  
  public TruncatedFileException(Raster paramRaster)
  {
    super("Premature end of input file");
    ras = paramRaster;
  }
  
  public Raster getRaster()
  {
    return ras;
  }
  
  public BufferedImage getBufferedImage()
  {
    return bi;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\image\codec\jpeg\TruncatedFileException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */