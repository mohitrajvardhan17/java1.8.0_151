package javax.imageio.spi;

import java.lang.reflect.Method;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public abstract class ImageReaderWriterSpi
  extends IIOServiceProvider
{
  protected String[] names = null;
  protected String[] suffixes = null;
  protected String[] MIMETypes = null;
  protected String pluginClassName = null;
  protected boolean supportsStandardStreamMetadataFormat = false;
  protected String nativeStreamMetadataFormatName = null;
  protected String nativeStreamMetadataFormatClassName = null;
  protected String[] extraStreamMetadataFormatNames = null;
  protected String[] extraStreamMetadataFormatClassNames = null;
  protected boolean supportsStandardImageMetadataFormat = false;
  protected String nativeImageMetadataFormatName = null;
  protected String nativeImageMetadataFormatClassName = null;
  protected String[] extraImageMetadataFormatNames = null;
  protected String[] extraImageMetadataFormatClassNames = null;
  
  public ImageReaderWriterSpi(String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3, String paramString3, boolean paramBoolean1, String paramString4, String paramString5, String[] paramArrayOfString4, String[] paramArrayOfString5, boolean paramBoolean2, String paramString6, String paramString7, String[] paramArrayOfString6, String[] paramArrayOfString7)
  {
    super(paramString1, paramString2);
    if (paramArrayOfString1 == null) {
      throw new IllegalArgumentException("names == null!");
    }
    if (paramArrayOfString1.length == 0) {
      throw new IllegalArgumentException("names.length == 0!");
    }
    if (paramString3 == null) {
      throw new IllegalArgumentException("pluginClassName == null!");
    }
    names = ((String[])paramArrayOfString1.clone());
    if ((paramArrayOfString2 != null) && (paramArrayOfString2.length > 0)) {
      suffixes = ((String[])paramArrayOfString2.clone());
    }
    if ((paramArrayOfString3 != null) && (paramArrayOfString3.length > 0)) {
      MIMETypes = ((String[])paramArrayOfString3.clone());
    }
    pluginClassName = paramString3;
    supportsStandardStreamMetadataFormat = paramBoolean1;
    nativeStreamMetadataFormatName = paramString4;
    nativeStreamMetadataFormatClassName = paramString5;
    if ((paramArrayOfString4 != null) && (paramArrayOfString4.length > 0)) {
      extraStreamMetadataFormatNames = ((String[])paramArrayOfString4.clone());
    }
    if ((paramArrayOfString5 != null) && (paramArrayOfString5.length > 0)) {
      extraStreamMetadataFormatClassNames = ((String[])paramArrayOfString5.clone());
    }
    supportsStandardImageMetadataFormat = paramBoolean2;
    nativeImageMetadataFormatName = paramString6;
    nativeImageMetadataFormatClassName = paramString7;
    if ((paramArrayOfString6 != null) && (paramArrayOfString6.length > 0)) {
      extraImageMetadataFormatNames = ((String[])paramArrayOfString6.clone());
    }
    if ((paramArrayOfString7 != null) && (paramArrayOfString7.length > 0)) {
      extraImageMetadataFormatClassNames = ((String[])paramArrayOfString7.clone());
    }
  }
  
  public ImageReaderWriterSpi() {}
  
  public String[] getFormatNames()
  {
    return (String[])names.clone();
  }
  
  public String[] getFileSuffixes()
  {
    return suffixes == null ? null : (String[])suffixes.clone();
  }
  
  public String[] getMIMETypes()
  {
    return MIMETypes == null ? null : (String[])MIMETypes.clone();
  }
  
  public String getPluginClassName()
  {
    return pluginClassName;
  }
  
  public boolean isStandardStreamMetadataFormatSupported()
  {
    return supportsStandardStreamMetadataFormat;
  }
  
  public String getNativeStreamMetadataFormatName()
  {
    return nativeStreamMetadataFormatName;
  }
  
  public String[] getExtraStreamMetadataFormatNames()
  {
    return extraStreamMetadataFormatNames == null ? null : (String[])extraStreamMetadataFormatNames.clone();
  }
  
  public boolean isStandardImageMetadataFormatSupported()
  {
    return supportsStandardImageMetadataFormat;
  }
  
  public String getNativeImageMetadataFormatName()
  {
    return nativeImageMetadataFormatName;
  }
  
  public String[] getExtraImageMetadataFormatNames()
  {
    return extraImageMetadataFormatNames == null ? null : (String[])extraImageMetadataFormatNames.clone();
  }
  
  public IIOMetadataFormat getStreamMetadataFormat(String paramString)
  {
    return getMetadataFormat(paramString, supportsStandardStreamMetadataFormat, nativeStreamMetadataFormatName, nativeStreamMetadataFormatClassName, extraStreamMetadataFormatNames, extraStreamMetadataFormatClassNames);
  }
  
  public IIOMetadataFormat getImageMetadataFormat(String paramString)
  {
    return getMetadataFormat(paramString, supportsStandardImageMetadataFormat, nativeImageMetadataFormatName, nativeImageMetadataFormatClassName, extraImageMetadataFormatNames, extraImageMetadataFormatClassNames);
  }
  
  private IIOMetadataFormat getMetadataFormat(String paramString1, boolean paramBoolean, String paramString2, String paramString3, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    if (paramString1 == null) {
      throw new IllegalArgumentException("formatName == null!");
    }
    if ((paramBoolean) && (paramString1.equals("javax_imageio_1.0"))) {
      return IIOMetadataFormatImpl.getStandardFormatInstance();
    }
    String str = null;
    if (paramString1.equals(paramString2)) {
      str = paramString3;
    } else if (paramArrayOfString1 != null) {
      for (int i = 0; i < paramArrayOfString1.length; i++) {
        if (paramString1.equals(paramArrayOfString1[i]))
        {
          str = paramArrayOfString2[i];
          break;
        }
      }
    }
    if (str == null) {
      throw new IllegalArgumentException("Unsupported format name");
    }
    try
    {
      Class localClass = Class.forName(str, true, ClassLoader.getSystemClassLoader());
      localObject = localClass.getMethod("getInstance", new Class[0]);
      return (IIOMetadataFormat)((Method)localObject).invoke(null, new Object[0]);
    }
    catch (Exception localException)
    {
      Object localObject = new IllegalStateException("Can't obtain format");
      ((RuntimeException)localObject).initCause(localException);
      throw ((Throwable)localObject);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\spi\ImageReaderWriterSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */