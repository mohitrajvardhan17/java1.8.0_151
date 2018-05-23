package java.awt.font;

public final class TextHitInfo
{
  private int charIndex;
  private boolean isLeadingEdge;
  
  private TextHitInfo(int paramInt, boolean paramBoolean)
  {
    charIndex = paramInt;
    isLeadingEdge = paramBoolean;
  }
  
  public int getCharIndex()
  {
    return charIndex;
  }
  
  public boolean isLeadingEdge()
  {
    return isLeadingEdge;
  }
  
  public int getInsertionIndex()
  {
    return isLeadingEdge ? charIndex : charIndex + 1;
  }
  
  public int hashCode()
  {
    return charIndex;
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof TextHitInfo)) && (equals((TextHitInfo)paramObject));
  }
  
  public boolean equals(TextHitInfo paramTextHitInfo)
  {
    return (paramTextHitInfo != null) && (charIndex == charIndex) && (isLeadingEdge == isLeadingEdge);
  }
  
  public String toString()
  {
    return "TextHitInfo[" + charIndex + (isLeadingEdge ? "L" : "T") + "]";
  }
  
  public static TextHitInfo leading(int paramInt)
  {
    return new TextHitInfo(paramInt, true);
  }
  
  public static TextHitInfo trailing(int paramInt)
  {
    return new TextHitInfo(paramInt, false);
  }
  
  public static TextHitInfo beforeOffset(int paramInt)
  {
    return new TextHitInfo(paramInt - 1, false);
  }
  
  public static TextHitInfo afterOffset(int paramInt)
  {
    return new TextHitInfo(paramInt, true);
  }
  
  public TextHitInfo getOtherHit()
  {
    if (isLeadingEdge) {
      return trailing(charIndex - 1);
    }
    return leading(charIndex + 1);
  }
  
  public TextHitInfo getOffsetHit(int paramInt)
  {
    return new TextHitInfo(charIndex + paramInt, isLeadingEdge);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\font\TextHitInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */