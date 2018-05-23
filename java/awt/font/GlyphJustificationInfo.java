package java.awt.font;

public final class GlyphJustificationInfo
{
  public static final int PRIORITY_KASHIDA = 0;
  public static final int PRIORITY_WHITESPACE = 1;
  public static final int PRIORITY_INTERCHAR = 2;
  public static final int PRIORITY_NONE = 3;
  public final float weight;
  public final int growPriority;
  public final boolean growAbsorb;
  public final float growLeftLimit;
  public final float growRightLimit;
  public final int shrinkPriority;
  public final boolean shrinkAbsorb;
  public final float shrinkLeftLimit;
  public final float shrinkRightLimit;
  
  public GlyphJustificationInfo(float paramFloat1, boolean paramBoolean1, int paramInt1, float paramFloat2, float paramFloat3, boolean paramBoolean2, int paramInt2, float paramFloat4, float paramFloat5)
  {
    if (paramFloat1 < 0.0F) {
      throw new IllegalArgumentException("weight is negative");
    }
    if (!priorityIsValid(paramInt1)) {
      throw new IllegalArgumentException("Invalid grow priority");
    }
    if (paramFloat2 < 0.0F) {
      throw new IllegalArgumentException("growLeftLimit is negative");
    }
    if (paramFloat3 < 0.0F) {
      throw new IllegalArgumentException("growRightLimit is negative");
    }
    if (!priorityIsValid(paramInt2)) {
      throw new IllegalArgumentException("Invalid shrink priority");
    }
    if (paramFloat4 < 0.0F) {
      throw new IllegalArgumentException("shrinkLeftLimit is negative");
    }
    if (paramFloat5 < 0.0F) {
      throw new IllegalArgumentException("shrinkRightLimit is negative");
    }
    weight = paramFloat1;
    growAbsorb = paramBoolean1;
    growPriority = paramInt1;
    growLeftLimit = paramFloat2;
    growRightLimit = paramFloat3;
    shrinkAbsorb = paramBoolean2;
    shrinkPriority = paramInt2;
    shrinkLeftLimit = paramFloat4;
    shrinkRightLimit = paramFloat5;
  }
  
  private static boolean priorityIsValid(int paramInt)
  {
    return (paramInt >= 0) && (paramInt <= 3);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\font\GlyphJustificationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */