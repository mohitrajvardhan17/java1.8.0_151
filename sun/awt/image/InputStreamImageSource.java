package sun.awt.image;

import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public abstract class InputStreamImageSource
  implements ImageProducer, ImageFetchable
{
  ImageConsumerQueue consumers;
  ImageDecoder decoder;
  ImageDecoder decoders;
  boolean awaitingFetch = false;
  
  public InputStreamImageSource() {}
  
  abstract boolean checkSecurity(Object paramObject, boolean paramBoolean);
  
  int countConsumers(ImageConsumerQueue paramImageConsumerQueue)
  {
    int i = 0;
    while (paramImageConsumerQueue != null)
    {
      i++;
      paramImageConsumerQueue = next;
    }
    return i;
  }
  
  synchronized int countConsumers()
  {
    ImageDecoder localImageDecoder = decoders;
    int i = countConsumers(consumers);
    while (localImageDecoder != null)
    {
      i += countConsumers(queue);
      localImageDecoder = next;
    }
    return i;
  }
  
  public void addConsumer(ImageConsumer paramImageConsumer)
  {
    addConsumer(paramImageConsumer, false);
  }
  
  synchronized void printQueue(ImageConsumerQueue paramImageConsumerQueue, String paramString)
  {
    while (paramImageConsumerQueue != null)
    {
      System.out.println(paramString + paramImageConsumerQueue);
      paramImageConsumerQueue = next;
    }
  }
  
  synchronized void printQueues(String paramString)
  {
    System.out.println(paramString + "[ -----------");
    printQueue(consumers, "  ");
    for (ImageDecoder localImageDecoder = decoders; localImageDecoder != null; localImageDecoder = next)
    {
      System.out.println("    " + localImageDecoder);
      printQueue(queue, "      ");
    }
    System.out.println("----------- ]" + paramString);
  }
  
  synchronized void addConsumer(ImageConsumer paramImageConsumer, boolean paramBoolean)
  {
    checkSecurity(null, false);
    for (Object localObject1 = decoders; localObject1 != null; localObject1 = next) {
      if (((ImageDecoder)localObject1).isConsumer(paramImageConsumer)) {
        return;
      }
    }
    for (localObject1 = consumers; (localObject1 != null) && (consumer != paramImageConsumer); localObject1 = next) {}
    if (localObject1 == null)
    {
      localObject1 = new ImageConsumerQueue(this, paramImageConsumer);
      next = consumers;
      consumers = ((ImageConsumerQueue)localObject1);
    }
    else
    {
      if (!secure)
      {
        Object localObject2 = null;
        SecurityManager localSecurityManager = System.getSecurityManager();
        if (localSecurityManager != null) {
          localObject2 = localSecurityManager.getSecurityContext();
        }
        if (securityContext == null)
        {
          securityContext = localObject2;
        }
        else if (!securityContext.equals(localObject2))
        {
          errorConsumer((ImageConsumerQueue)localObject1, false);
          throw new SecurityException("Applets are trading image data!");
        }
      }
      interested = true;
    }
    if ((paramBoolean) && (decoder == null)) {
      startProduction();
    }
  }
  
  public synchronized boolean isConsumer(ImageConsumer paramImageConsumer)
  {
    for (ImageDecoder localImageDecoder = decoders; localImageDecoder != null; localImageDecoder = next) {
      if (localImageDecoder.isConsumer(paramImageConsumer)) {
        return true;
      }
    }
    return ImageConsumerQueue.isConsumer(consumers, paramImageConsumer);
  }
  
  private void errorAllConsumers(ImageConsumerQueue paramImageConsumerQueue, boolean paramBoolean)
  {
    while (paramImageConsumerQueue != null)
    {
      if (interested) {
        errorConsumer(paramImageConsumerQueue, paramBoolean);
      }
      paramImageConsumerQueue = next;
    }
  }
  
  private void errorConsumer(ImageConsumerQueue paramImageConsumerQueue, boolean paramBoolean)
  {
    consumer.imageComplete(1);
    if ((paramBoolean) && ((consumer instanceof ImageRepresentation))) {
      consumer).image.flush();
    }
    removeConsumer(consumer);
  }
  
  public synchronized void removeConsumer(ImageConsumer paramImageConsumer)
  {
    for (ImageDecoder localImageDecoder = decoders; localImageDecoder != null; localImageDecoder = next) {
      localImageDecoder.removeConsumer(paramImageConsumer);
    }
    consumers = ImageConsumerQueue.removeConsumer(consumers, paramImageConsumer, false);
  }
  
  public void startProduction(ImageConsumer paramImageConsumer)
  {
    addConsumer(paramImageConsumer, true);
  }
  
  private synchronized void startProduction()
  {
    if (!awaitingFetch) {
      if (ImageFetcher.add(this))
      {
        awaitingFetch = true;
      }
      else
      {
        ImageConsumerQueue localImageConsumerQueue = consumers;
        consumers = null;
        errorAllConsumers(localImageConsumerQueue, false);
      }
    }
  }
  
  private synchronized void stopProduction()
  {
    if (awaitingFetch)
    {
      ImageFetcher.remove(this);
      awaitingFetch = false;
    }
  }
  
  public void requestTopDownLeftRightResend(ImageConsumer paramImageConsumer) {}
  
  protected abstract ImageDecoder getDecoder();
  
  protected ImageDecoder decoderForType(InputStream paramInputStream, String paramString)
  {
    return null;
  }
  
  protected ImageDecoder getDecoder(InputStream paramInputStream)
  {
    if (!paramInputStream.markSupported()) {
      paramInputStream = new BufferedInputStream(paramInputStream);
    }
    try
    {
      paramInputStream.mark(8);
      int i = paramInputStream.read();
      int j = paramInputStream.read();
      int k = paramInputStream.read();
      int m = paramInputStream.read();
      int n = paramInputStream.read();
      int i1 = paramInputStream.read();
      int i2 = paramInputStream.read();
      int i3 = paramInputStream.read();
      paramInputStream.reset();
      paramInputStream.mark(-1);
      if ((i == 71) && (j == 73) && (k == 70) && (m == 56)) {
        return new GifImageDecoder(this, paramInputStream);
      }
      if ((i == 255) && (j == 216) && (k == 255)) {
        return new JPEGImageDecoder(this, paramInputStream);
      }
      if ((i == 35) && (j == 100) && (k == 101) && (m == 102)) {
        return new XbmImageDecoder(this, paramInputStream);
      }
      if ((i == 137) && (j == 80) && (k == 78) && (m == 71) && (n == 13) && (i1 == 10) && (i2 == 26) && (i3 == 10)) {
        return new PNGImageDecoder(this, paramInputStream);
      }
    }
    catch (IOException localIOException) {}
    return null;
  }
  
  public void doFetch()
  {
    synchronized (this)
    {
      if (consumers == null)
      {
        awaitingFetch = false;
        return;
      }
    }
    ??? = getDecoder();
    if (??? == null)
    {
      badDecoder();
    }
    else
    {
      setDecoder((ImageDecoder)???);
      try
      {
        ((ImageDecoder)???).produceImage();
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
      }
      catch (ImageFormatException localImageFormatException)
      {
        localImageFormatException.printStackTrace();
      }
      finally
      {
        removeDecoder((ImageDecoder)???);
        if ((Thread.currentThread().isInterrupted()) || (!Thread.currentThread().isAlive())) {
          errorAllConsumers(queue, true);
        } else {
          errorAllConsumers(queue, false);
        }
      }
    }
  }
  
  private void badDecoder()
  {
    ImageConsumerQueue localImageConsumerQueue;
    synchronized (this)
    {
      localImageConsumerQueue = consumers;
      consumers = null;
      awaitingFetch = false;
    }
    errorAllConsumers(localImageConsumerQueue, false);
  }
  
  private void setDecoder(ImageDecoder paramImageDecoder)
  {
    ImageConsumerQueue localImageConsumerQueue;
    synchronized (this)
    {
      next = decoders;
      decoders = paramImageDecoder;
      decoder = paramImageDecoder;
      localImageConsumerQueue = consumers;
      queue = localImageConsumerQueue;
      consumers = null;
      awaitingFetch = false;
    }
    while (localImageConsumerQueue != null)
    {
      if ((interested) && (!checkSecurity(securityContext, true))) {
        errorConsumer(localImageConsumerQueue, false);
      }
      localImageConsumerQueue = next;
    }
  }
  
  private synchronized void removeDecoder(ImageDecoder paramImageDecoder)
  {
    doneDecoding(paramImageDecoder);
    Object localObject = null;
    for (ImageDecoder localImageDecoder = decoders; localImageDecoder != null; localImageDecoder = next)
    {
      if (localImageDecoder == paramImageDecoder)
      {
        if (localObject == null)
        {
          decoders = next;
          break;
        }
        next = next;
        break;
      }
      localObject = localImageDecoder;
    }
  }
  
  synchronized void doneDecoding(ImageDecoder paramImageDecoder)
  {
    if (decoder == paramImageDecoder)
    {
      decoder = null;
      if (consumers != null) {
        startProduction();
      }
    }
  }
  
  void latchConsumers(ImageDecoder paramImageDecoder)
  {
    doneDecoding(paramImageDecoder);
  }
  
  synchronized void flush()
  {
    decoder = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\InputStreamImageSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */