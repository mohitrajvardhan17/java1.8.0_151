package sun.java2d.pipe;

public class RegionClipSpanIterator
  implements SpanIterator
{
  Region rgn;
  SpanIterator spanIter;
  RegionIterator resetState;
  RegionIterator lwm;
  RegionIterator row;
  RegionIterator box;
  int spanlox;
  int spanhix;
  int spanloy;
  int spanhiy;
  int lwmloy;
  int lwmhiy;
  int rgnlox;
  int rgnloy;
  int rgnhix;
  int rgnhiy;
  int rgnbndslox;
  int rgnbndsloy;
  int rgnbndshix;
  int rgnbndshiy;
  int[] rgnbox = new int[4];
  int[] spanbox = new int[4];
  boolean doNextSpan;
  boolean doNextBox;
  boolean done = false;
  
  public RegionClipSpanIterator(Region paramRegion, SpanIterator paramSpanIterator)
  {
    spanIter = paramSpanIterator;
    resetState = paramRegion.getIterator();
    lwm = resetState.createCopy();
    if (!lwm.nextYRange(rgnbox))
    {
      done = true;
      return;
    }
    rgnloy = (lwmloy = rgnbox[1]);
    rgnhiy = (lwmhiy = rgnbox[3]);
    paramRegion.getBounds(rgnbox);
    rgnbndslox = rgnbox[0];
    rgnbndsloy = rgnbox[1];
    rgnbndshix = rgnbox[2];
    rgnbndshiy = rgnbox[3];
    if ((rgnbndslox >= rgnbndshix) || (rgnbndsloy >= rgnbndshiy))
    {
      done = true;
      return;
    }
    rgn = paramRegion;
    row = lwm.createCopy();
    box = row.createCopy();
    doNextSpan = true;
    doNextBox = false;
  }
  
  public void getPathBox(int[] paramArrayOfInt)
  {
    int[] arrayOfInt = new int[4];
    rgn.getBounds(arrayOfInt);
    spanIter.getPathBox(paramArrayOfInt);
    if (paramArrayOfInt[0] < arrayOfInt[0]) {
      paramArrayOfInt[0] = arrayOfInt[0];
    }
    if (paramArrayOfInt[1] < arrayOfInt[1]) {
      paramArrayOfInt[1] = arrayOfInt[1];
    }
    if (paramArrayOfInt[2] > arrayOfInt[2]) {
      paramArrayOfInt[2] = arrayOfInt[2];
    }
    if (paramArrayOfInt[3] > arrayOfInt[3]) {
      paramArrayOfInt[3] = arrayOfInt[3];
    }
  }
  
  public void intersectClipBox(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    spanIter.intersectClipBox(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public boolean nextSpan(int[] paramArrayOfInt)
  {
    if (done) {
      return false;
    }
    int n = 0;
    int i;
    int j;
    int k;
    int m;
    do
    {
      for (;;)
      {
        if (doNextSpan)
        {
          if (!spanIter.nextSpan(spanbox))
          {
            done = true;
            return false;
          }
          spanlox = spanbox[0];
          if (spanlox < rgnbndshix)
          {
            spanloy = spanbox[1];
            if (spanloy < rgnbndshiy)
            {
              spanhix = spanbox[2];
              if (spanhix > rgnbndslox)
              {
                spanhiy = spanbox[3];
                if (spanhiy > rgnbndsloy)
                {
                  if (lwmloy > spanloy)
                  {
                    lwm.copyStateFrom(resetState);
                    lwm.nextYRange(rgnbox);
                    lwmloy = rgnbox[1];
                  }
                  for (lwmhiy = rgnbox[3]; (lwmhiy <= spanloy) && (lwm.nextYRange(rgnbox)); lwmhiy = rgnbox[3]) {
                    lwmloy = rgnbox[1];
                  }
                  if ((lwmhiy > spanloy) && (lwmloy < spanhiy))
                  {
                    if (rgnloy != lwmloy)
                    {
                      row.copyStateFrom(lwm);
                      rgnloy = lwmloy;
                      rgnhiy = lwmhiy;
                    }
                    box.copyStateFrom(row);
                    doNextBox = true;
                    doNextSpan = false;
                  }
                }
              }
            }
          }
        }
        else
        {
          boolean bool;
          if (n != 0)
          {
            n = 0;
            bool = row.nextYRange(rgnbox);
            if (bool)
            {
              rgnloy = rgnbox[1];
              rgnhiy = rgnbox[3];
            }
            if ((!bool) || (rgnloy >= spanhiy))
            {
              doNextSpan = true;
            }
            else
            {
              box.copyStateFrom(row);
              doNextBox = true;
            }
          }
          else
          {
            if (!doNextBox) {
              break;
            }
            bool = box.nextXBand(rgnbox);
            if (bool)
            {
              rgnlox = rgnbox[0];
              rgnhix = rgnbox[2];
            }
            if ((!bool) || (rgnlox >= spanhix))
            {
              doNextBox = false;
              if (rgnhiy >= spanhiy) {
                doNextSpan = true;
              } else {
                n = 1;
              }
            }
            else
            {
              doNextBox = (rgnhix <= spanlox);
            }
          }
        }
      }
      doNextBox = true;
      if (spanlox > rgnlox) {
        i = spanlox;
      } else {
        i = rgnlox;
      }
      if (spanloy > rgnloy) {
        j = spanloy;
      } else {
        j = rgnloy;
      }
      if (spanhix < rgnhix) {
        k = spanhix;
      } else {
        k = rgnhix;
      }
      if (spanhiy < rgnhiy) {
        m = spanhiy;
      } else {
        m = rgnhiy;
      }
    } while ((i >= k) || (j >= m));
    paramArrayOfInt[0] = i;
    paramArrayOfInt[1] = j;
    paramArrayOfInt[2] = k;
    paramArrayOfInt[3] = m;
    return true;
  }
  
  public void skipDownTo(int paramInt)
  {
    spanIter.skipDownTo(paramInt);
  }
  
  public long getNativeIterator()
  {
    return 0L;
  }
  
  protected void finalize() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\RegionClipSpanIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */