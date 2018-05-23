package javax.swing;

import java.io.Serializable;

public class SizeRequirements
  implements Serializable
{
  public int minimum;
  public int preferred;
  public int maximum;
  public float alignment;
  
  public SizeRequirements()
  {
    minimum = 0;
    preferred = 0;
    maximum = 0;
    alignment = 0.5F;
  }
  
  public SizeRequirements(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
  {
    minimum = paramInt1;
    preferred = paramInt2;
    maximum = paramInt3;
    alignment = (paramFloat < 0.0F ? 0.0F : paramFloat > 1.0F ? 1.0F : paramFloat);
  }
  
  public String toString()
  {
    return "[" + minimum + "," + preferred + "," + maximum + "]@" + alignment;
  }
  
  public static SizeRequirements getTiledSizeRequirements(SizeRequirements[] paramArrayOfSizeRequirements)
  {
    SizeRequirements localSizeRequirements1 = new SizeRequirements();
    for (int i = 0; i < paramArrayOfSizeRequirements.length; i++)
    {
      SizeRequirements localSizeRequirements2 = paramArrayOfSizeRequirements[i];
      minimum = ((int)Math.min(minimum + minimum, 2147483647L));
      preferred = ((int)Math.min(preferred + preferred, 2147483647L));
      maximum = ((int)Math.min(maximum + maximum, 2147483647L));
    }
    return localSizeRequirements1;
  }
  
  public static SizeRequirements getAlignedSizeRequirements(SizeRequirements[] paramArrayOfSizeRequirements)
  {
    SizeRequirements localSizeRequirements1 = new SizeRequirements();
    SizeRequirements localSizeRequirements2 = new SizeRequirements();
    for (int i = 0; i < paramArrayOfSizeRequirements.length; i++)
    {
      SizeRequirements localSizeRequirements3 = paramArrayOfSizeRequirements[i];
      k = (int)(alignment * minimum);
      int m = minimum - k;
      minimum = Math.max(k, minimum);
      minimum = Math.max(m, minimum);
      k = (int)(alignment * preferred);
      m = preferred - k;
      preferred = Math.max(k, preferred);
      preferred = Math.max(m, preferred);
      k = (int)(alignment * maximum);
      m = maximum - k;
      maximum = Math.max(k, maximum);
      maximum = Math.max(m, maximum);
    }
    i = (int)Math.min(minimum + minimum, 2147483647L);
    int j = (int)Math.min(preferred + preferred, 2147483647L);
    int k = (int)Math.min(maximum + maximum, 2147483647L);
    float f = 0.0F;
    if (i > 0)
    {
      f = minimum / i;
      f = f < 0.0F ? 0.0F : f > 1.0F ? 1.0F : f;
    }
    return new SizeRequirements(i, j, k, f);
  }
  
  public static void calculateTiledPositions(int paramInt, SizeRequirements paramSizeRequirements, SizeRequirements[] paramArrayOfSizeRequirements, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    calculateTiledPositions(paramInt, paramSizeRequirements, paramArrayOfSizeRequirements, paramArrayOfInt1, paramArrayOfInt2, true);
  }
  
  public static void calculateTiledPositions(int paramInt, SizeRequirements paramSizeRequirements, SizeRequirements[] paramArrayOfSizeRequirements, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean)
  {
    long l1 = 0L;
    long l2 = 0L;
    long l3 = 0L;
    for (int i = 0; i < paramArrayOfSizeRequirements.length; i++)
    {
      l1 += minimum;
      l2 += preferred;
      l3 += maximum;
    }
    if (paramInt >= l2) {
      expandedTile(paramInt, l1, l2, l3, paramArrayOfSizeRequirements, paramArrayOfInt1, paramArrayOfInt2, paramBoolean);
    } else {
      compressedTile(paramInt, l1, l2, l3, paramArrayOfSizeRequirements, paramArrayOfInt1, paramArrayOfInt2, paramBoolean);
    }
  }
  
  private static void compressedTile(int paramInt, long paramLong1, long paramLong2, long paramLong3, SizeRequirements[] paramArrayOfSizeRequirements, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean)
  {
    float f1 = (float)Math.min(paramLong2 - paramInt, paramLong2 - paramLong1);
    float f2 = paramLong2 - paramLong1 == 0L ? 0.0F : f1 / (float)(paramLong2 - paramLong1);
    int i;
    int j;
    SizeRequirements localSizeRequirements;
    float f3;
    if (paramBoolean)
    {
      i = 0;
      for (j = 0; j < paramArrayOfInt2.length; j++)
      {
        paramArrayOfInt1[j] = i;
        localSizeRequirements = paramArrayOfSizeRequirements[j];
        f3 = f2 * (preferred - minimum);
        paramArrayOfInt2[j] = ((int)(preferred - f3));
        i = (int)Math.min(i + paramArrayOfInt2[j], 2147483647L);
      }
    }
    else
    {
      i = paramInt;
      for (j = 0; j < paramArrayOfInt2.length; j++)
      {
        localSizeRequirements = paramArrayOfSizeRequirements[j];
        f3 = f2 * (preferred - minimum);
        paramArrayOfInt2[j] = ((int)(preferred - f3));
        paramArrayOfInt1[j] = (i - paramArrayOfInt2[j]);
        i = (int)Math.max(i - paramArrayOfInt2[j], 0L);
      }
    }
  }
  
  private static void expandedTile(int paramInt, long paramLong1, long paramLong2, long paramLong3, SizeRequirements[] paramArrayOfSizeRequirements, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean)
  {
    float f1 = (float)Math.min(paramInt - paramLong2, paramLong3 - paramLong2);
    float f2 = paramLong3 - paramLong2 == 0L ? 0.0F : f1 / (float)(paramLong3 - paramLong2);
    int i;
    int j;
    SizeRequirements localSizeRequirements;
    int k;
    if (paramBoolean)
    {
      i = 0;
      for (j = 0; j < paramArrayOfInt2.length; j++)
      {
        paramArrayOfInt1[j] = i;
        localSizeRequirements = paramArrayOfSizeRequirements[j];
        k = (int)(f2 * (maximum - preferred));
        paramArrayOfInt2[j] = ((int)Math.min(preferred + k, 2147483647L));
        i = (int)Math.min(i + paramArrayOfInt2[j], 2147483647L);
      }
    }
    else
    {
      i = paramInt;
      for (j = 0; j < paramArrayOfInt2.length; j++)
      {
        localSizeRequirements = paramArrayOfSizeRequirements[j];
        k = (int)(f2 * (maximum - preferred));
        paramArrayOfInt2[j] = ((int)Math.min(preferred + k, 2147483647L));
        paramArrayOfInt1[j] = (i - paramArrayOfInt2[j]);
        i = (int)Math.max(i - paramArrayOfInt2[j], 0L);
      }
    }
  }
  
  public static void calculateAlignedPositions(int paramInt, SizeRequirements paramSizeRequirements, SizeRequirements[] paramArrayOfSizeRequirements, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    calculateAlignedPositions(paramInt, paramSizeRequirements, paramArrayOfSizeRequirements, paramArrayOfInt1, paramArrayOfInt2, true);
  }
  
  public static void calculateAlignedPositions(int paramInt, SizeRequirements paramSizeRequirements, SizeRequirements[] paramArrayOfSizeRequirements, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean)
  {
    float f1 = paramBoolean ? alignment : 1.0F - alignment;
    int i = (int)(paramInt * f1);
    int j = paramInt - i;
    for (int k = 0; k < paramArrayOfSizeRequirements.length; k++)
    {
      SizeRequirements localSizeRequirements = paramArrayOfSizeRequirements[k];
      float f2 = paramBoolean ? alignment : 1.0F - alignment;
      int m = (int)(maximum * f2);
      int n = maximum - m;
      int i1 = Math.min(i, m);
      int i2 = Math.min(j, n);
      paramArrayOfInt1[k] = (i - i1);
      paramArrayOfInt2[k] = ((int)Math.min(i1 + i2, 2147483647L));
    }
  }
  
  public static int[] adjustSizes(int paramInt, SizeRequirements[] paramArrayOfSizeRequirements)
  {
    return new int[0];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\SizeRequirements.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */