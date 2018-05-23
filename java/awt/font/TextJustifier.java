package java.awt.font;

import java.io.PrintStream;

class TextJustifier
{
  private GlyphJustificationInfo[] info;
  private int start;
  private int limit;
  static boolean DEBUG = false;
  public static final int MAX_PRIORITY = 3;
  
  TextJustifier(GlyphJustificationInfo[] paramArrayOfGlyphJustificationInfo, int paramInt1, int paramInt2)
  {
    info = paramArrayOfGlyphJustificationInfo;
    start = paramInt1;
    limit = paramInt2;
    if (DEBUG)
    {
      System.out.println("start: " + paramInt1 + ", limit: " + paramInt2);
      for (int i = paramInt1; i < paramInt2; i++)
      {
        GlyphJustificationInfo localGlyphJustificationInfo = paramArrayOfGlyphJustificationInfo[i];
        System.out.println("w: " + weight + ", gp: " + growPriority + ", gll: " + growLeftLimit + ", grl: " + growRightLimit);
      }
    }
  }
  
  public float[] justify(float paramFloat)
  {
    float[] arrayOfFloat = new float[info.length * 2];
    int i = paramFloat > 0.0F ? 1 : 0;
    if (DEBUG) {
      System.out.println("delta: " + paramFloat);
    }
    int j = -1;
    int m;
    for (int k = 0; paramFloat != 0.0F; k++)
    {
      m = k > 3 ? 1 : 0;
      if (m != 0) {
        k = j;
      }
      float f2 = 0.0F;
      float f3 = 0.0F;
      float f4 = 0.0F;
      for (int n = start; n < limit; n++)
      {
        GlyphJustificationInfo localGlyphJustificationInfo1 = info[n];
        if ((i != 0 ? growPriority : shrinkPriority) == k)
        {
          if (j == -1) {
            j = k;
          }
          if (n != start)
          {
            f2 += weight;
            if (i != 0)
            {
              f3 += growLeftLimit;
              if (growAbsorb) {
                f4 += weight;
              }
            }
            else
            {
              f3 += shrinkLeftLimit;
              if (shrinkAbsorb) {
                f4 += weight;
              }
            }
          }
          if (n + 1 != limit)
          {
            f2 += weight;
            if (i != 0)
            {
              f3 += growRightLimit;
              if (growAbsorb) {
                f4 += weight;
              }
            }
            else
            {
              f3 += shrinkRightLimit;
              if (shrinkAbsorb) {
                f4 += weight;
              }
            }
          }
        }
      }
      if (i == 0) {
        f3 = -f3;
      }
      if (f2 != 0.0F) {
        if (m != 0) {
          break label375;
        }
      }
      label375:
      n = (paramFloat < 0.0F ? 1 : 0) == (paramFloat < f3 ? 1 : 0) ? 1 : 0;
      int i1 = (n != 0) && (f4 > 0.0F) ? 1 : 0;
      float f5 = paramFloat / f2;
      float f6 = 0.0F;
      if ((n != 0) && (f4 > 0.0F)) {
        f6 = (paramFloat - f3) / f4;
      }
      if (DEBUG) {
        System.out.println("pass: " + k + ", d: " + paramFloat + ", l: " + f3 + ", w: " + f2 + ", aw: " + f4 + ", wd: " + f5 + ", wa: " + f6 + ", hit: " + (n != 0 ? "y" : "n"));
      }
      int i2 = start * 2;
      for (int i3 = start; i3 < limit; i3++)
      {
        GlyphJustificationInfo localGlyphJustificationInfo2 = info[i3];
        if ((i != 0 ? growPriority : shrinkPriority) == k)
        {
          float f7;
          if (i3 != start)
          {
            if (n != 0)
            {
              f7 = i != 0 ? growLeftLimit : -shrinkLeftLimit;
              if (i1 != 0) {
                f7 += weight * f6;
              }
            }
            else
            {
              f7 = weight * f5;
            }
            arrayOfFloat[i2] += f7;
          }
          i2++;
          if (i3 + 1 != limit)
          {
            if (n != 0)
            {
              f7 = i != 0 ? growRightLimit : -shrinkRightLimit;
              if (i1 != 0) {
                f7 += weight * f6;
              }
            }
            else
            {
              f7 = weight * f5;
            }
            arrayOfFloat[i2] += f7;
          }
          i2++;
        }
        else
        {
          i2 += 2;
        }
      }
      if ((m == 0) && (n != 0) && (i1 == 0)) {
        paramFloat -= f3;
      } else {
        paramFloat = 0.0F;
      }
    }
    if (DEBUG)
    {
      float f1 = 0.0F;
      for (m = 0; m < arrayOfFloat.length; m++)
      {
        f1 += arrayOfFloat[m];
        System.out.print(arrayOfFloat[m] + ", ");
        if (m % 20 == 9) {
          System.out.println();
        }
      }
      System.out.println("\ntotal: " + f1);
      System.out.println();
    }
    return arrayOfFloat;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\font\TextJustifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */