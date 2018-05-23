package javax.imageio.spi;

import java.awt.image.RenderedImage;
import java.io.IOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public abstract class ImageWriterSpi
  extends ImageReaderWriterSpi
{
  @Deprecated
  public static final Class[] STANDARD_OUTPUT_TYPE = { ImageOutputStream.class };
  protected Class[] outputTypes = null;
  protected String[] readerSpiNames = null;
  private Class writerClass = null;
  
  protected ImageWriterSpi() {}
  
  public ImageWriterSpi(String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3, String paramString3, Class[] paramArrayOfClass, String[] paramArrayOfString4, boolean paramBoolean1, String paramString4, String paramString5, String[] paramArrayOfString5, String[] paramArrayOfString6, boolean paramBoolean2, String paramString6, String paramString7, String[] paramArrayOfString7, String[] paramArrayOfString8)
  {
    super(paramString1, paramString2, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, paramString3, paramBoolean1, paramString4, paramString5, paramArrayOfString5, paramArrayOfString6, paramBoolean2, paramString6, paramString7, paramArrayOfString7, paramArrayOfString8);
    if (paramArrayOfClass == null) {
      throw new IllegalArgumentException("outputTypes == null!");
    }
    if (paramArrayOfClass.length == 0) {
      throw new IllegalArgumentException("outputTypes.length == 0!");
    }
    outputTypes = (paramArrayOfClass == STANDARD_OUTPUT_TYPE ? new Class[] { ImageOutputStream.class } : (Class[])paramArrayOfClass.clone());
    if ((paramArrayOfString4 != null) && (paramArrayOfString4.length > 0)) {
      readerSpiNames = ((String[])paramArrayOfString4.clone());
    }
  }
  
  public boolean isFormatLossless()
  {
    return true;
  }
  
  public Class[] getOutputTypes()
  {
    return (Class[])outputTypes.clone();
  }
  
  public abstract boolean canEncodeImage(ImageTypeSpecifier paramImageTypeSpecifier);
  
  public boolean canEncodeImage(RenderedImage paramRenderedImage)
  {
    return canEncodeImage(ImageTypeSpecifier.createFromRenderedImage(paramRenderedImage));
  }
  
  public ImageWriter createWriterInstance()
    throws IOException
  {
    return createWriterInstance(null);
  }
  
  public abstract ImageWriter createWriterInstance(Object paramObject)
    throws IOException;
  
  public boolean isOwnWriter(ImageWriter paramImageWriter)
  {
    if (paramImageWriter == null) {
      throw new IllegalArgumentException("writer == null!");
    }
    String str = paramImageWriter.getClass().getName();
    return str.equals(pluginClassName);
  }
  
  public String[] getImageReaderSpiNames()
  {
    return readerSpiNames == null ? null : (String[])readerSpiNames.clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\spi\ImageWriterSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */