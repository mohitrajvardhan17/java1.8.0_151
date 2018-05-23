package javax.imageio.spi;

import java.io.IOException;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public abstract class ImageReaderSpi
  extends ImageReaderWriterSpi
{
  @Deprecated
  public static final Class[] STANDARD_INPUT_TYPE = { ImageInputStream.class };
  protected Class[] inputTypes = null;
  protected String[] writerSpiNames = null;
  private Class readerClass = null;
  
  protected ImageReaderSpi() {}
  
  public ImageReaderSpi(String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3, String paramString3, Class[] paramArrayOfClass, String[] paramArrayOfString4, boolean paramBoolean1, String paramString4, String paramString5, String[] paramArrayOfString5, String[] paramArrayOfString6, boolean paramBoolean2, String paramString6, String paramString7, String[] paramArrayOfString7, String[] paramArrayOfString8)
  {
    super(paramString1, paramString2, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, paramString3, paramBoolean1, paramString4, paramString5, paramArrayOfString5, paramArrayOfString6, paramBoolean2, paramString6, paramString7, paramArrayOfString7, paramArrayOfString8);
    if (paramArrayOfClass == null) {
      throw new IllegalArgumentException("inputTypes == null!");
    }
    if (paramArrayOfClass.length == 0) {
      throw new IllegalArgumentException("inputTypes.length == 0!");
    }
    inputTypes = (paramArrayOfClass == STANDARD_INPUT_TYPE ? new Class[] { ImageInputStream.class } : (Class[])paramArrayOfClass.clone());
    if ((paramArrayOfString4 != null) && (paramArrayOfString4.length > 0)) {
      writerSpiNames = ((String[])paramArrayOfString4.clone());
    }
  }
  
  public Class[] getInputTypes()
  {
    return (Class[])inputTypes.clone();
  }
  
  public abstract boolean canDecodeInput(Object paramObject)
    throws IOException;
  
  public ImageReader createReaderInstance()
    throws IOException
  {
    return createReaderInstance(null);
  }
  
  public abstract ImageReader createReaderInstance(Object paramObject)
    throws IOException;
  
  public boolean isOwnReader(ImageReader paramImageReader)
  {
    if (paramImageReader == null) {
      throw new IllegalArgumentException("reader == null!");
    }
    String str = paramImageReader.getClass().getName();
    return str.equals(pluginClassName);
  }
  
  public String[] getImageWriterSpiNames()
  {
    return writerSpiNames == null ? null : (String[])writerSpiNames.clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\spi\ImageReaderSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */