package java.awt;

abstract class MediaEntry
{
  MediaTracker tracker;
  int ID;
  MediaEntry next;
  int status;
  boolean cancelled;
  static final int LOADING = 1;
  static final int ABORTED = 2;
  static final int ERRORED = 4;
  static final int COMPLETE = 8;
  static final int LOADSTARTED = 13;
  static final int DONE = 14;
  
  MediaEntry(MediaTracker paramMediaTracker, int paramInt)
  {
    tracker = paramMediaTracker;
    ID = paramInt;
  }
  
  abstract Object getMedia();
  
  static MediaEntry insert(MediaEntry paramMediaEntry1, MediaEntry paramMediaEntry2)
  {
    MediaEntry localMediaEntry1 = paramMediaEntry1;
    MediaEntry localMediaEntry2 = null;
    while ((localMediaEntry1 != null) && (ID <= ID))
    {
      localMediaEntry2 = localMediaEntry1;
      localMediaEntry1 = next;
    }
    next = localMediaEntry1;
    if (localMediaEntry2 == null) {
      paramMediaEntry1 = paramMediaEntry2;
    } else {
      next = paramMediaEntry2;
    }
    return paramMediaEntry1;
  }
  
  int getID()
  {
    return ID;
  }
  
  abstract void startLoad();
  
  void cancel()
  {
    cancelled = true;
  }
  
  synchronized int getStatus(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && ((status & 0xD) == 0))
    {
      status = (status & 0xFFFFFFFD | 0x1);
      startLoad();
    }
    return status;
  }
  
  void setStatus(int paramInt)
  {
    synchronized (this)
    {
      status = paramInt;
    }
    tracker.setDone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\MediaEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */