package sun.net;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class ProgressMonitor
{
  private static ProgressMeteringPolicy meteringPolicy = new DefaultProgressMeteringPolicy();
  private static ProgressMonitor pm = new ProgressMonitor();
  private ArrayList<ProgressSource> progressSourceList = new ArrayList();
  private ArrayList<ProgressListener> progressListenerList = new ArrayList();
  
  public ProgressMonitor() {}
  
  public static synchronized ProgressMonitor getDefault()
  {
    return pm;
  }
  
  public static synchronized void setDefault(ProgressMonitor paramProgressMonitor)
  {
    if (paramProgressMonitor != null) {
      pm = paramProgressMonitor;
    }
  }
  
  public static synchronized void setMeteringPolicy(ProgressMeteringPolicy paramProgressMeteringPolicy)
  {
    if (paramProgressMeteringPolicy != null) {
      meteringPolicy = paramProgressMeteringPolicy;
    }
  }
  
  public ArrayList<ProgressSource> getProgressSources()
  {
    ArrayList localArrayList = new ArrayList();
    try
    {
      synchronized (progressSourceList)
      {
        Iterator localIterator = progressSourceList.iterator();
        while (localIterator.hasNext())
        {
          ProgressSource localProgressSource = (ProgressSource)localIterator.next();
          localArrayList.add((ProgressSource)localProgressSource.clone());
        }
      }
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      localCloneNotSupportedException.printStackTrace();
    }
    return localArrayList;
  }
  
  public synchronized int getProgressUpdateThreshold()
  {
    return meteringPolicy.getProgressUpdateThreshold();
  }
  
  public boolean shouldMeterInput(URL paramURL, String paramString)
  {
    return meteringPolicy.shouldMeterInput(paramURL, paramString);
  }
  
  public void registerSource(ProgressSource paramProgressSource)
  {
    synchronized (progressSourceList)
    {
      if (progressSourceList.contains(paramProgressSource)) {
        return;
      }
      progressSourceList.add(paramProgressSource);
    }
    if (progressListenerList.size() > 0)
    {
      ??? = new ArrayList();
      Object localObject2;
      synchronized (progressListenerList)
      {
        localObject2 = progressListenerList.iterator();
        while (((Iterator)localObject2).hasNext()) {
          ((ArrayList)???).add(((Iterator)localObject2).next());
        }
      }
      ??? = ((ArrayList)???).iterator();
      while (((Iterator)???).hasNext())
      {
        localObject2 = (ProgressListener)((Iterator)???).next();
        ProgressEvent localProgressEvent = new ProgressEvent(paramProgressSource, paramProgressSource.getURL(), paramProgressSource.getMethod(), paramProgressSource.getContentType(), paramProgressSource.getState(), paramProgressSource.getProgress(), paramProgressSource.getExpected());
        ((ProgressListener)localObject2).progressStart(localProgressEvent);
      }
    }
  }
  
  public void unregisterSource(ProgressSource paramProgressSource)
  {
    synchronized (progressSourceList)
    {
      if (!progressSourceList.contains(paramProgressSource)) {
        return;
      }
      paramProgressSource.close();
      progressSourceList.remove(paramProgressSource);
    }
    if (progressListenerList.size() > 0)
    {
      ??? = new ArrayList();
      Object localObject2;
      synchronized (progressListenerList)
      {
        localObject2 = progressListenerList.iterator();
        while (((Iterator)localObject2).hasNext()) {
          ((ArrayList)???).add(((Iterator)localObject2).next());
        }
      }
      ??? = ((ArrayList)???).iterator();
      while (((Iterator)???).hasNext())
      {
        localObject2 = (ProgressListener)((Iterator)???).next();
        ProgressEvent localProgressEvent = new ProgressEvent(paramProgressSource, paramProgressSource.getURL(), paramProgressSource.getMethod(), paramProgressSource.getContentType(), paramProgressSource.getState(), paramProgressSource.getProgress(), paramProgressSource.getExpected());
        ((ProgressListener)localObject2).progressFinish(localProgressEvent);
      }
    }
  }
  
  public void updateProgress(ProgressSource paramProgressSource)
  {
    synchronized (progressSourceList)
    {
      if (!progressSourceList.contains(paramProgressSource)) {
        return;
      }
    }
    if (progressListenerList.size() > 0)
    {
      ??? = new ArrayList();
      Object localObject2;
      synchronized (progressListenerList)
      {
        localObject2 = progressListenerList.iterator();
        while (((Iterator)localObject2).hasNext()) {
          ((ArrayList)???).add(((Iterator)localObject2).next());
        }
      }
      ??? = ((ArrayList)???).iterator();
      while (((Iterator)???).hasNext())
      {
        localObject2 = (ProgressListener)((Iterator)???).next();
        ProgressEvent localProgressEvent = new ProgressEvent(paramProgressSource, paramProgressSource.getURL(), paramProgressSource.getMethod(), paramProgressSource.getContentType(), paramProgressSource.getState(), paramProgressSource.getProgress(), paramProgressSource.getExpected());
        ((ProgressListener)localObject2).progressUpdate(localProgressEvent);
      }
    }
  }
  
  public void addProgressListener(ProgressListener paramProgressListener)
  {
    synchronized (progressListenerList)
    {
      progressListenerList.add(paramProgressListener);
    }
  }
  
  public void removeProgressListener(ProgressListener paramProgressListener)
  {
    synchronized (progressListenerList)
    {
      progressListenerList.remove(paramProgressListener);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\ProgressMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */