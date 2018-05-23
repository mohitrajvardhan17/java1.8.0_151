package com.sun.imageio.spi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.stream.FileCacheImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

public class InputStreamImageInputStreamSpi
  extends ImageInputStreamSpi
{
  private static final String vendorName = "Oracle Corporation";
  private static final String version = "1.0";
  private static final Class inputClass = InputStream.class;
  
  public InputStreamImageInputStreamSpi()
  {
    super("Oracle Corporation", "1.0", inputClass);
  }
  
  public String getDescription(Locale paramLocale)
  {
    return "Service provider that instantiates a FileCacheImageInputStream or MemoryCacheImageInputStream from an InputStream";
  }
  
  public boolean canUseCacheFile()
  {
    return true;
  }
  
  public boolean needsCacheFile()
  {
    return false;
  }
  
  public ImageInputStream createInputStreamInstance(Object paramObject, boolean paramBoolean, File paramFile)
    throws IOException
  {
    if ((paramObject instanceof InputStream))
    {
      InputStream localInputStream = (InputStream)paramObject;
      if (paramBoolean) {
        return new FileCacheImageInputStream(localInputStream, paramFile);
      }
      return new MemoryCacheImageInputStream(localInputStream);
    }
    throw new IllegalArgumentException();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\spi\InputStreamImageInputStreamSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */