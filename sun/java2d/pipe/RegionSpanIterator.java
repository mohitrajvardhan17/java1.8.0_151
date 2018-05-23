package sun.java2d.pipe;

public class RegionSpanIterator
  implements SpanIterator
{
  RegionIterator ri;
  int lox;
  int loy;
  int hix;
  int hiy;
  int curloy;
  int curhiy;
  boolean done = false;
  boolean isrect;
  
  public RegionSpanIterator(Region paramRegion)
  {
    int[] arrayOfInt = new int[4];
    paramRegion.getBounds(arrayOfInt);
    lox = arrayOfInt[0];
    loy = arrayOfInt[1];
    hix = arrayOfInt[2];
    hiy = arrayOfInt[3];
    isrect = paramRegion.isRectangular();
    ri = paramRegion.getIterator();
  }
  
  public void getPathBox(int[] paramArrayOfInt)
  {
    paramArrayOfInt[0] = lox;
    paramArrayOfInt[1] = loy;
    paramArrayOfInt[2] = hix;
    paramArrayOfInt[3] = hiy;
  }
  
  public void intersectClipBox(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramInt1 > lox) {
      lox = paramInt1;
    }
    if (paramInt2 > loy) {
      loy = paramInt2;
    }
    if (paramInt3 < hix) {
      hix = paramInt3;
    }
    if (paramInt4 < hiy) {
      hiy = paramInt4;
    }
    done = ((lox >= hix) || (loy >= hiy));
  }
  
  public boolean nextSpan(int[] paramArrayOfInt)
  {
    if (done) {
      return false;
    }
    if (isrect)
    {
      getPathBox(paramArrayOfInt);
      done = true;
      return true;
    }
    int k = curloy;
    int m = curhiy;
    int i;
    int j;
    for (;;)
    {
      if (!ri.nextXBand(paramArrayOfInt))
      {
        if (!ri.nextYRange(paramArrayOfInt))
        {
          done = true;
          return false;
        }
        k = paramArrayOfInt[1];
        m = paramArrayOfInt[3];
        if (k < loy) {
          k = loy;
        }
        if (m > hiy) {
          m = hiy;
        }
        if (k >= hiy)
        {
          done = true;
          return false;
        }
      }
      else
      {
        i = paramArrayOfInt[0];
        j = paramArrayOfInt[2];
        if (i < lox) {
          i = lox;
        }
        if (j > hix) {
          j = hix;
        }
        if ((i < j) && (k < m)) {
          break;
        }
      }
    }
    paramArrayOfInt[0] = i;
    paramArrayOfInt[1] = (curloy = k);
    paramArrayOfInt[2] = j;
    paramArrayOfInt[3] = (curhiy = m);
    return true;
  }
  
  public void skipDownTo(int paramInt)
  {
    loy = paramInt;
  }
  
  public long getNativeIterator()
  {
    return 0L;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\RegionSpanIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */