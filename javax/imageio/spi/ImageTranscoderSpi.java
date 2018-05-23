package javax.imageio.spi;

import javax.imageio.ImageTranscoder;

public abstract class ImageTranscoderSpi
  extends IIOServiceProvider
{
  protected ImageTranscoderSpi() {}
  
  public ImageTranscoderSpi(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
  
  public abstract String getReaderServiceProviderName();
  
  public abstract String getWriterServiceProviderName();
  
  public abstract ImageTranscoder createTranscoderInstance();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\spi\ImageTranscoderSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */