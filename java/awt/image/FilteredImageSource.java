package java.awt.image;

import java.util.Hashtable;

public class FilteredImageSource
  implements ImageProducer
{
  ImageProducer src;
  ImageFilter filter;
  private Hashtable proxies;
  
  public FilteredImageSource(ImageProducer paramImageProducer, ImageFilter paramImageFilter)
  {
    src = paramImageProducer;
    filter = paramImageFilter;
  }
  
  public synchronized void addConsumer(ImageConsumer paramImageConsumer)
  {
    if (proxies == null) {
      proxies = new Hashtable();
    }
    if (!proxies.containsKey(paramImageConsumer))
    {
      ImageFilter localImageFilter = filter.getFilterInstance(paramImageConsumer);
      proxies.put(paramImageConsumer, localImageFilter);
      src.addConsumer(localImageFilter);
    }
  }
  
  public synchronized boolean isConsumer(ImageConsumer paramImageConsumer)
  {
    return (proxies != null) && (proxies.containsKey(paramImageConsumer));
  }
  
  public synchronized void removeConsumer(ImageConsumer paramImageConsumer)
  {
    if (proxies != null)
    {
      ImageFilter localImageFilter = (ImageFilter)proxies.get(paramImageConsumer);
      if (localImageFilter != null)
      {
        src.removeConsumer(localImageFilter);
        proxies.remove(paramImageConsumer);
        if (proxies.isEmpty()) {
          proxies = null;
        }
      }
    }
  }
  
  public void startProduction(ImageConsumer paramImageConsumer)
  {
    if (proxies == null) {
      proxies = new Hashtable();
    }
    ImageFilter localImageFilter = (ImageFilter)proxies.get(paramImageConsumer);
    if (localImageFilter == null)
    {
      localImageFilter = filter.getFilterInstance(paramImageConsumer);
      proxies.put(paramImageConsumer, localImageFilter);
    }
    src.startProduction(localImageFilter);
  }
  
  public void requestTopDownLeftRightResend(ImageConsumer paramImageConsumer)
  {
    if (proxies != null)
    {
      ImageFilter localImageFilter = (ImageFilter)proxies.get(paramImageConsumer);
      if (localImageFilter != null) {
        localImageFilter.resendTopDownLeftRight(src);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\FilteredImageSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */