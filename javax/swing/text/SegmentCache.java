package javax.swing.text;

import java.util.ArrayList;
import java.util.List;

class SegmentCache
{
  private static SegmentCache sharedCache = new SegmentCache();
  private List<Segment> segments = new ArrayList(11);
  
  public static SegmentCache getSharedInstance()
  {
    return sharedCache;
  }
  
  public static Segment getSharedSegment()
  {
    return getSharedInstance().getSegment();
  }
  
  public static void releaseSharedSegment(Segment paramSegment)
  {
    getSharedInstance().releaseSegment(paramSegment);
  }
  
  public SegmentCache() {}
  
  public Segment getSegment()
  {
    synchronized (this)
    {
      int i = segments.size();
      if (i > 0) {
        return (Segment)segments.remove(i - 1);
      }
    }
    return new CachedSegment(null);
  }
  
  public void releaseSegment(Segment paramSegment)
  {
    if ((paramSegment instanceof CachedSegment)) {
      synchronized (this)
      {
        array = null;
        count = 0;
        segments.add(paramSegment);
      }
    }
  }
  
  private static class CachedSegment
    extends Segment
  {
    private CachedSegment() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\SegmentCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */