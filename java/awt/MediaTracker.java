package java.awt;

import java.io.Serializable;
import sun.awt.image.MultiResolutionToolkitImage;

public class MediaTracker
  implements Serializable
{
  Component target;
  MediaEntry head;
  private static final long serialVersionUID = -483174189758638095L;
  public static final int LOADING = 1;
  public static final int ABORTED = 2;
  public static final int ERRORED = 4;
  public static final int COMPLETE = 8;
  static final int DONE = 14;
  
  public MediaTracker(Component paramComponent)
  {
    target = paramComponent;
  }
  
  public void addImage(Image paramImage, int paramInt)
  {
    addImage(paramImage, paramInt, -1, -1);
  }
  
  public synchronized void addImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3)
  {
    addImageImpl(paramImage, paramInt1, paramInt2, paramInt3);
    Image localImage = getResolutionVariant(paramImage);
    if (localImage != null) {
      addImageImpl(localImage, paramInt1, paramInt2 == -1 ? -1 : 2 * paramInt2, paramInt3 == -1 ? -1 : 2 * paramInt3);
    }
  }
  
  private void addImageImpl(Image paramImage, int paramInt1, int paramInt2, int paramInt3)
  {
    head = MediaEntry.insert(head, new ImageMediaEntry(this, paramImage, paramInt1, paramInt2, paramInt3));
  }
  
  public boolean checkAll()
  {
    return checkAll(false, true);
  }
  
  public boolean checkAll(boolean paramBoolean)
  {
    return checkAll(paramBoolean, true);
  }
  
  private synchronized boolean checkAll(boolean paramBoolean1, boolean paramBoolean2)
  {
    MediaEntry localMediaEntry = head;
    boolean bool = true;
    while (localMediaEntry != null)
    {
      if ((localMediaEntry.getStatus(paramBoolean1, paramBoolean2) & 0xE) == 0) {
        bool = false;
      }
      localMediaEntry = next;
    }
    return bool;
  }
  
  public synchronized boolean isErrorAny()
  {
    for (MediaEntry localMediaEntry = head; localMediaEntry != null; localMediaEntry = next) {
      if ((localMediaEntry.getStatus(false, true) & 0x4) != 0) {
        return true;
      }
    }
    return false;
  }
  
  public synchronized Object[] getErrorsAny()
  {
    MediaEntry localMediaEntry = head;
    int i = 0;
    while (localMediaEntry != null)
    {
      if ((localMediaEntry.getStatus(false, true) & 0x4) != 0) {
        i++;
      }
      localMediaEntry = next;
    }
    if (i == 0) {
      return null;
    }
    Object[] arrayOfObject = new Object[i];
    localMediaEntry = head;
    i = 0;
    while (localMediaEntry != null)
    {
      if ((localMediaEntry.getStatus(false, false) & 0x4) != 0) {
        arrayOfObject[(i++)] = localMediaEntry.getMedia();
      }
      localMediaEntry = next;
    }
    return arrayOfObject;
  }
  
  public void waitForAll()
    throws InterruptedException
  {
    waitForAll(0L);
  }
  
  public synchronized boolean waitForAll(long paramLong)
    throws InterruptedException
  {
    long l1 = System.currentTimeMillis() + paramLong;
    boolean bool = true;
    for (;;)
    {
      int i = statusAll(bool, bool);
      if ((i & 0x1) == 0) {
        return i == 8;
      }
      bool = false;
      long l2;
      if (paramLong == 0L)
      {
        l2 = 0L;
      }
      else
      {
        l2 = l1 - System.currentTimeMillis();
        if (l2 <= 0L) {
          return false;
        }
      }
      wait(l2);
    }
  }
  
  public int statusAll(boolean paramBoolean)
  {
    return statusAll(paramBoolean, true);
  }
  
  private synchronized int statusAll(boolean paramBoolean1, boolean paramBoolean2)
  {
    MediaEntry localMediaEntry = head;
    int i = 0;
    while (localMediaEntry != null)
    {
      i |= localMediaEntry.getStatus(paramBoolean1, paramBoolean2);
      localMediaEntry = next;
    }
    return i;
  }
  
  public boolean checkID(int paramInt)
  {
    return checkID(paramInt, false, true);
  }
  
  public boolean checkID(int paramInt, boolean paramBoolean)
  {
    return checkID(paramInt, paramBoolean, true);
  }
  
  private synchronized boolean checkID(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    MediaEntry localMediaEntry = head;
    boolean bool = true;
    while (localMediaEntry != null)
    {
      if ((localMediaEntry.getID() == paramInt) && ((localMediaEntry.getStatus(paramBoolean1, paramBoolean2) & 0xE) == 0)) {
        bool = false;
      }
      localMediaEntry = next;
    }
    return bool;
  }
  
  public synchronized boolean isErrorID(int paramInt)
  {
    for (MediaEntry localMediaEntry = head; localMediaEntry != null; localMediaEntry = next) {
      if ((localMediaEntry.getID() == paramInt) && ((localMediaEntry.getStatus(false, true) & 0x4) != 0)) {
        return true;
      }
    }
    return false;
  }
  
  public synchronized Object[] getErrorsID(int paramInt)
  {
    MediaEntry localMediaEntry = head;
    int i = 0;
    while (localMediaEntry != null)
    {
      if ((localMediaEntry.getID() == paramInt) && ((localMediaEntry.getStatus(false, true) & 0x4) != 0)) {
        i++;
      }
      localMediaEntry = next;
    }
    if (i == 0) {
      return null;
    }
    Object[] arrayOfObject = new Object[i];
    localMediaEntry = head;
    i = 0;
    while (localMediaEntry != null)
    {
      if ((localMediaEntry.getID() == paramInt) && ((localMediaEntry.getStatus(false, false) & 0x4) != 0)) {
        arrayOfObject[(i++)] = localMediaEntry.getMedia();
      }
      localMediaEntry = next;
    }
    return arrayOfObject;
  }
  
  public void waitForID(int paramInt)
    throws InterruptedException
  {
    waitForID(paramInt, 0L);
  }
  
  public synchronized boolean waitForID(int paramInt, long paramLong)
    throws InterruptedException
  {
    long l1 = System.currentTimeMillis() + paramLong;
    boolean bool = true;
    for (;;)
    {
      int i = statusID(paramInt, bool, bool);
      if ((i & 0x1) == 0) {
        return i == 8;
      }
      bool = false;
      long l2;
      if (paramLong == 0L)
      {
        l2 = 0L;
      }
      else
      {
        l2 = l1 - System.currentTimeMillis();
        if (l2 <= 0L) {
          return false;
        }
      }
      wait(l2);
    }
  }
  
  public int statusID(int paramInt, boolean paramBoolean)
  {
    return statusID(paramInt, paramBoolean, true);
  }
  
  private synchronized int statusID(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    MediaEntry localMediaEntry = head;
    int i = 0;
    while (localMediaEntry != null)
    {
      if (localMediaEntry.getID() == paramInt) {
        i |= localMediaEntry.getStatus(paramBoolean1, paramBoolean2);
      }
      localMediaEntry = next;
    }
    return i;
  }
  
  public synchronized void removeImage(Image paramImage)
  {
    removeImageImpl(paramImage);
    Image localImage = getResolutionVariant(paramImage);
    if (localImage != null) {
      removeImageImpl(localImage);
    }
    notifyAll();
  }
  
  private void removeImageImpl(Image paramImage)
  {
    Object localObject1 = head;
    Object localObject2 = null;
    while (localObject1 != null)
    {
      MediaEntry localMediaEntry = next;
      if (((MediaEntry)localObject1).getMedia() == paramImage)
      {
        if (localObject2 == null) {
          head = localMediaEntry;
        } else {
          next = localMediaEntry;
        }
        ((MediaEntry)localObject1).cancel();
      }
      else
      {
        localObject2 = localObject1;
      }
      localObject1 = localMediaEntry;
    }
  }
  
  public synchronized void removeImage(Image paramImage, int paramInt)
  {
    removeImageImpl(paramImage, paramInt);
    Image localImage = getResolutionVariant(paramImage);
    if (localImage != null) {
      removeImageImpl(localImage, paramInt);
    }
    notifyAll();
  }
  
  private void removeImageImpl(Image paramImage, int paramInt)
  {
    Object localObject1 = head;
    Object localObject2 = null;
    while (localObject1 != null)
    {
      MediaEntry localMediaEntry = next;
      if ((((MediaEntry)localObject1).getID() == paramInt) && (((MediaEntry)localObject1).getMedia() == paramImage))
      {
        if (localObject2 == null) {
          head = localMediaEntry;
        } else {
          next = localMediaEntry;
        }
        ((MediaEntry)localObject1).cancel();
      }
      else
      {
        localObject2 = localObject1;
      }
      localObject1 = localMediaEntry;
    }
  }
  
  public synchronized void removeImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3)
  {
    removeImageImpl(paramImage, paramInt1, paramInt2, paramInt3);
    Image localImage = getResolutionVariant(paramImage);
    if (localImage != null) {
      removeImageImpl(localImage, paramInt1, paramInt2 == -1 ? -1 : 2 * paramInt2, paramInt3 == -1 ? -1 : 2 * paramInt3);
    }
    notifyAll();
  }
  
  private void removeImageImpl(Image paramImage, int paramInt1, int paramInt2, int paramInt3)
  {
    Object localObject1 = head;
    Object localObject2 = null;
    while (localObject1 != null)
    {
      MediaEntry localMediaEntry = next;
      if ((((MediaEntry)localObject1).getID() == paramInt1) && ((localObject1 instanceof ImageMediaEntry)) && (((ImageMediaEntry)localObject1).matches(paramImage, paramInt2, paramInt3)))
      {
        if (localObject2 == null) {
          head = localMediaEntry;
        } else {
          next = localMediaEntry;
        }
        ((MediaEntry)localObject1).cancel();
      }
      else
      {
        localObject2 = localObject1;
      }
      localObject1 = localMediaEntry;
    }
  }
  
  synchronized void setDone()
  {
    notifyAll();
  }
  
  private static Image getResolutionVariant(Image paramImage)
  {
    if ((paramImage instanceof MultiResolutionToolkitImage)) {
      return ((MultiResolutionToolkitImage)paramImage).getResolutionVariant();
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\MediaTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */