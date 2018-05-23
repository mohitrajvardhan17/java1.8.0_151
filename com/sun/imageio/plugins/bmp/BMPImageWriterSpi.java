package com.sun.imageio.plugins.bmp;

import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageOutputStream;

public class BMPImageWriterSpi
  extends ImageWriterSpi
{
  private static String[] readerSpiNames = { "com.sun.imageio.plugins.bmp.BMPImageReaderSpi" };
  private static String[] formatNames = { "bmp", "BMP" };
  private static String[] entensions = { "bmp" };
  private static String[] mimeType = { "image/bmp" };
  private boolean registered = false;
  
  public BMPImageWriterSpi()
  {
    super("Oracle Corporation", "1.0", formatNames, entensions, mimeType, "com.sun.imageio.plugins.bmp.BMPImageWriter", new Class[] { ImageOutputStream.class }, readerSpiNames, false, null, null, null, null, true, "javax_imageio_bmp_1.0", "com.sun.imageio.plugins.bmp.BMPMetadataFormat", null, null);
  }
  
  public String getDescription(Locale paramLocale)
  {
    return "Standard BMP Image Writer";
  }
  
  public void onRegistration(ServiceRegistry paramServiceRegistry, Class<?> paramClass)
  {
    if (registered) {
      return;
    }
    registered = true;
  }
  
  public boolean canEncodeImage(ImageTypeSpecifier paramImageTypeSpecifier)
  {
    int i = paramImageTypeSpecifier.getSampleModel().getDataType();
    if ((i < 0) || (i > 3)) {
      return false;
    }
    SampleModel localSampleModel = paramImageTypeSpecifier.getSampleModel();
    int j = localSampleModel.getNumBands();
    if ((j != 1) && (j != 3)) {
      return false;
    }
    if ((j == 1) && (i != 0)) {
      return false;
    }
    return (i <= 0) || ((localSampleModel instanceof SinglePixelPackedSampleModel));
  }
  
  public ImageWriter createWriterInstance(Object paramObject)
    throws IIOException
  {
    return new BMPImageWriter(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\bmp\BMPImageWriterSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */