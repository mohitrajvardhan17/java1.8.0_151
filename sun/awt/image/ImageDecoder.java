package sun.awt.image;

import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;

public abstract class ImageDecoder
{
  InputStreamImageSource source;
  InputStream input;
  Thread feeder;
  protected boolean aborted;
  protected boolean finished;
  ImageConsumerQueue queue;
  ImageDecoder next;
  
  public ImageDecoder(InputStreamImageSource paramInputStreamImageSource, InputStream paramInputStream)
  {
    source = paramInputStreamImageSource;
    input = paramInputStream;
    feeder = Thread.currentThread();
  }
  
  public boolean isConsumer(ImageConsumer paramImageConsumer)
  {
    return ImageConsumerQueue.isConsumer(queue, paramImageConsumer);
  }
  
  public void removeConsumer(ImageConsumer paramImageConsumer)
  {
    queue = ImageConsumerQueue.removeConsumer(queue, paramImageConsumer, false);
    if ((!finished) && (queue == null)) {
      abort();
    }
  }
  
  protected ImageConsumerQueue nextConsumer(ImageConsumerQueue paramImageConsumerQueue)
  {
    synchronized (source)
    {
      if (aborted) {
        return null;
      }
      for (paramImageConsumerQueue = paramImageConsumerQueue == null ? queue : next; paramImageConsumerQueue != null; paramImageConsumerQueue = next) {
        if (interested) {
          return paramImageConsumerQueue;
        }
      }
    }
    return null;
  }
  
  protected int setDimensions(int paramInt1, int paramInt2)
  {
    ImageConsumerQueue localImageConsumerQueue = null;
    for (int i = 0; (localImageConsumerQueue = nextConsumer(localImageConsumerQueue)) != null; i++) {
      consumer.setDimensions(paramInt1, paramInt2);
    }
    return i;
  }
  
  protected int setProperties(Hashtable paramHashtable)
  {
    ImageConsumerQueue localImageConsumerQueue = null;
    for (int i = 0; (localImageConsumerQueue = nextConsumer(localImageConsumerQueue)) != null; i++) {
      consumer.setProperties(paramHashtable);
    }
    return i;
  }
  
  protected int setColorModel(ColorModel paramColorModel)
  {
    ImageConsumerQueue localImageConsumerQueue = null;
    for (int i = 0; (localImageConsumerQueue = nextConsumer(localImageConsumerQueue)) != null; i++) {
      consumer.setColorModel(paramColorModel);
    }
    return i;
  }
  
  protected int setHints(int paramInt)
  {
    ImageConsumerQueue localImageConsumerQueue = null;
    for (int i = 0; (localImageConsumerQueue = nextConsumer(localImageConsumerQueue)) != null; i++) {
      consumer.setHints(paramInt);
    }
    return i;
  }
  
  protected void headerComplete()
  {
    feeder.setPriority(3);
  }
  
  protected int setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6)
  {
    source.latchConsumers(this);
    ImageConsumerQueue localImageConsumerQueue = null;
    for (int i = 0; (localImageConsumerQueue = nextConsumer(localImageConsumerQueue)) != null; i++) {
      consumer.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, paramColorModel, paramArrayOfByte, paramInt5, paramInt6);
    }
    return i;
  }
  
  protected int setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt5, int paramInt6)
  {
    source.latchConsumers(this);
    ImageConsumerQueue localImageConsumerQueue = null;
    for (int i = 0; (localImageConsumerQueue = nextConsumer(localImageConsumerQueue)) != null; i++) {
      consumer.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, paramColorModel, paramArrayOfInt, paramInt5, paramInt6);
    }
    return i;
  }
  
  protected int imageComplete(int paramInt, boolean paramBoolean)
  {
    source.latchConsumers(this);
    if (paramBoolean)
    {
      finished = true;
      source.doneDecoding(this);
    }
    ImageConsumerQueue localImageConsumerQueue = null;
    for (int i = 0; (localImageConsumerQueue = nextConsumer(localImageConsumerQueue)) != null; i++) {
      consumer.imageComplete(paramInt);
    }
    return i;
  }
  
  public abstract void produceImage()
    throws IOException, ImageFormatException;
  
  public void abort()
  {
    aborted = true;
    source.doneDecoding(this);
    close();
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        feeder.interrupt();
        return null;
      }
    });
  }
  
  public synchronized void close()
  {
    if (input != null) {
      try
      {
        input.close();
      }
      catch (IOException localIOException) {}
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\ImageDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */