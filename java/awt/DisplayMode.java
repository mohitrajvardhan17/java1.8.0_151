package java.awt;

public final class DisplayMode
{
  private Dimension size;
  private int bitDepth;
  private int refreshRate;
  public static final int BIT_DEPTH_MULTI = -1;
  public static final int REFRESH_RATE_UNKNOWN = 0;
  
  public DisplayMode(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    size = new Dimension(paramInt1, paramInt2);
    bitDepth = paramInt3;
    refreshRate = paramInt4;
  }
  
  public int getHeight()
  {
    return size.height;
  }
  
  public int getWidth()
  {
    return size.width;
  }
  
  public int getBitDepth()
  {
    return bitDepth;
  }
  
  public int getRefreshRate()
  {
    return refreshRate;
  }
  
  public boolean equals(DisplayMode paramDisplayMode)
  {
    if (paramDisplayMode == null) {
      return false;
    }
    return (getHeight() == paramDisplayMode.getHeight()) && (getWidth() == paramDisplayMode.getWidth()) && (getBitDepth() == paramDisplayMode.getBitDepth()) && (getRefreshRate() == paramDisplayMode.getRefreshRate());
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof DisplayMode)) {
      return equals((DisplayMode)paramObject);
    }
    return false;
  }
  
  public int hashCode()
  {
    return getWidth() + getHeight() + getBitDepth() * 7 + getRefreshRate() * 13;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\DisplayMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */