package sun.java2d;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class Spans
{
  private static final int kMaxAddsSinceSort = 256;
  private List mSpans = new Vector(256);
  private int mAddsSinceSort = 0;
  
  public Spans() {}
  
  public void add(float paramFloat1, float paramFloat2)
  {
    if (mSpans != null)
    {
      mSpans.add(new Span(paramFloat1, paramFloat2));
      if (++mAddsSinceSort >= 256) {
        sortAndCollapse();
      }
    }
  }
  
  public void addInfinite()
  {
    mSpans = null;
  }
  
  public boolean intersects(float paramFloat1, float paramFloat2)
  {
    boolean bool;
    if (mSpans != null)
    {
      if (mAddsSinceSort > 0) {
        sortAndCollapse();
      }
      int i = Collections.binarySearch(mSpans, new Span(paramFloat1, paramFloat2), SpanIntersection.instance);
      bool = i >= 0;
    }
    else
    {
      bool = true;
    }
    return bool;
  }
  
  private void sortAndCollapse()
  {
    Collections.sort(mSpans);
    mAddsSinceSort = 0;
    Iterator localIterator = mSpans.iterator();
    Object localObject = null;
    if (localIterator.hasNext()) {
      localObject = (Span)localIterator.next();
    }
    while (localIterator.hasNext())
    {
      Span localSpan = (Span)localIterator.next();
      if (((Span)localObject).subsume(localSpan)) {
        localIterator.remove();
      } else {
        localObject = localSpan;
      }
    }
  }
  
  static class Span
    implements Comparable
  {
    private float mStart;
    private float mEnd;
    
    Span(float paramFloat1, float paramFloat2)
    {
      mStart = paramFloat1;
      mEnd = paramFloat2;
    }
    
    final float getStart()
    {
      return mStart;
    }
    
    final float getEnd()
    {
      return mEnd;
    }
    
    final void setStart(float paramFloat)
    {
      mStart = paramFloat;
    }
    
    final void setEnd(float paramFloat)
    {
      mEnd = paramFloat;
    }
    
    boolean subsume(Span paramSpan)
    {
      boolean bool = contains(mStart);
      if ((bool) && (mEnd > mEnd)) {
        mEnd = mEnd;
      }
      return bool;
    }
    
    boolean contains(float paramFloat)
    {
      return (mStart <= paramFloat) && (paramFloat < mEnd);
    }
    
    public int compareTo(Object paramObject)
    {
      Span localSpan = (Span)paramObject;
      float f = localSpan.getStart();
      int i;
      if (mStart < f) {
        i = -1;
      } else if (mStart > f) {
        i = 1;
      } else {
        i = 0;
      }
      return i;
    }
    
    public String toString()
    {
      return "Span: " + mStart + " to " + mEnd;
    }
  }
  
  static class SpanIntersection
    implements Comparator
  {
    static final SpanIntersection instance = new SpanIntersection();
    
    private SpanIntersection() {}
    
    public int compare(Object paramObject1, Object paramObject2)
    {
      Spans.Span localSpan1 = (Spans.Span)paramObject1;
      Spans.Span localSpan2 = (Spans.Span)paramObject2;
      int i;
      if (localSpan1.getEnd() <= localSpan2.getStart()) {
        i = -1;
      } else if (localSpan1.getStart() >= localSpan2.getEnd()) {
        i = 1;
      } else {
        i = 0;
      }
      return i;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\Spans.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */