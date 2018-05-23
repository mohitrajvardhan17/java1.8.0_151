package sun.awt.image;

import java.awt.image.ImageConsumer;

class ImageConsumerQueue
{
  ImageConsumerQueue next;
  ImageConsumer consumer;
  boolean interested;
  Object securityContext;
  boolean secure;
  
  static ImageConsumerQueue removeConsumer(ImageConsumerQueue paramImageConsumerQueue, ImageConsumer paramImageConsumer, boolean paramBoolean)
  {
    Object localObject = null;
    for (ImageConsumerQueue localImageConsumerQueue = paramImageConsumerQueue; localImageConsumerQueue != null; localImageConsumerQueue = next)
    {
      if (consumer == paramImageConsumer)
      {
        if (localObject == null) {
          paramImageConsumerQueue = next;
        } else {
          next = next;
        }
        interested = paramBoolean;
        break;
      }
      localObject = localImageConsumerQueue;
    }
    return paramImageConsumerQueue;
  }
  
  static boolean isConsumer(ImageConsumerQueue paramImageConsumerQueue, ImageConsumer paramImageConsumer)
  {
    for (ImageConsumerQueue localImageConsumerQueue = paramImageConsumerQueue; localImageConsumerQueue != null; localImageConsumerQueue = next) {
      if (consumer == paramImageConsumer) {
        return true;
      }
    }
    return false;
  }
  
  ImageConsumerQueue(InputStreamImageSource paramInputStreamImageSource, ImageConsumer paramImageConsumer)
  {
    consumer = paramImageConsumer;
    interested = true;
    Object localObject;
    if ((paramImageConsumer instanceof ImageRepresentation))
    {
      localObject = (ImageRepresentation)paramImageConsumer;
      if (image.source != paramInputStreamImageSource) {
        throw new SecurityException("ImageRep added to wrong image source");
      }
      secure = true;
    }
    else
    {
      localObject = System.getSecurityManager();
      if (localObject != null) {
        securityContext = ((SecurityManager)localObject).getSecurityContext();
      } else {
        securityContext = null;
      }
    }
  }
  
  public String toString()
  {
    return "[" + consumer + ", " + (interested ? "" : "not ") + "interested" + (securityContext != null ? ", " + securityContext : "") + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\ImageConsumerQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */