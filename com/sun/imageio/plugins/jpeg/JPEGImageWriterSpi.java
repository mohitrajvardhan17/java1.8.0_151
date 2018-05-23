package com.sun.imageio.plugins.jpeg;

import java.awt.image.SampleModel;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

public class JPEGImageWriterSpi
  extends ImageWriterSpi
{
  private static String[] readerSpiNames = { "com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi" };
  
  public JPEGImageWriterSpi()
  {
    super("Oracle Corporation", "0.5", JPEG.names, JPEG.suffixes, JPEG.MIMETypes, "com.sun.imageio.plugins.jpeg.JPEGImageWriter", new Class[] { ImageOutputStream.class }, readerSpiNames, true, "javax_imageio_jpeg_stream_1.0", "com.sun.imageio.plugins.jpeg.JPEGStreamMetadataFormat", null, null, true, "javax_imageio_jpeg_image_1.0", "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat", null, null);
  }
  
  public String getDescription(Locale paramLocale)
  {
    return "Standard JPEG Image Writer";
  }
  
  public boolean isFormatLossless()
  {
    return false;
  }
  
  public boolean canEncodeImage(ImageTypeSpecifier paramImageTypeSpecifier)
  {
    SampleModel localSampleModel = paramImageTypeSpecifier.getSampleModel();
    int[] arrayOfInt = localSampleModel.getSampleSize();
    int i = arrayOfInt[0];
    for (int j = 1; j < arrayOfInt.length; j++) {
      if (arrayOfInt[j] > i) {
        i = arrayOfInt[j];
      }
    }
    return (i >= 1) && (i <= 8);
  }
  
  public ImageWriter createWriterInstance(Object paramObject)
    throws IIOException
  {
    return new JPEGImageWriter(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\jpeg\JPEGImageWriterSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */