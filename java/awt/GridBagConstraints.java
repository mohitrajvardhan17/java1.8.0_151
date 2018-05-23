package java.awt;

import java.io.Serializable;

public class GridBagConstraints
  implements Cloneable, Serializable
{
  public static final int RELATIVE = -1;
  public static final int REMAINDER = 0;
  public static final int NONE = 0;
  public static final int BOTH = 1;
  public static final int HORIZONTAL = 2;
  public static final int VERTICAL = 3;
  public static final int CENTER = 10;
  public static final int NORTH = 11;
  public static final int NORTHEAST = 12;
  public static final int EAST = 13;
  public static final int SOUTHEAST = 14;
  public static final int SOUTH = 15;
  public static final int SOUTHWEST = 16;
  public static final int WEST = 17;
  public static final int NORTHWEST = 18;
  public static final int PAGE_START = 19;
  public static final int PAGE_END = 20;
  public static final int LINE_START = 21;
  public static final int LINE_END = 22;
  public static final int FIRST_LINE_START = 23;
  public static final int FIRST_LINE_END = 24;
  public static final int LAST_LINE_START = 25;
  public static final int LAST_LINE_END = 26;
  public static final int BASELINE = 256;
  public static final int BASELINE_LEADING = 512;
  public static final int BASELINE_TRAILING = 768;
  public static final int ABOVE_BASELINE = 1024;
  public static final int ABOVE_BASELINE_LEADING = 1280;
  public static final int ABOVE_BASELINE_TRAILING = 1536;
  public static final int BELOW_BASELINE = 1792;
  public static final int BELOW_BASELINE_LEADING = 2048;
  public static final int BELOW_BASELINE_TRAILING = 2304;
  public int gridx;
  public int gridy;
  public int gridwidth;
  public int gridheight;
  public double weightx;
  public double weighty;
  public int anchor;
  public int fill;
  public Insets insets;
  public int ipadx;
  public int ipady;
  int tempX;
  int tempY;
  int tempWidth;
  int tempHeight;
  int minWidth;
  int minHeight;
  transient int ascent;
  transient int descent;
  transient Component.BaselineResizeBehavior baselineResizeBehavior;
  transient int centerPadding;
  transient int centerOffset;
  private static final long serialVersionUID = -1000070633030801713L;
  
  public GridBagConstraints()
  {
    gridx = -1;
    gridy = -1;
    gridwidth = 1;
    gridheight = 1;
    weightx = 0.0D;
    weighty = 0.0D;
    anchor = 10;
    fill = 0;
    insets = new Insets(0, 0, 0, 0);
    ipadx = 0;
    ipady = 0;
  }
  
  public GridBagConstraints(int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble1, double paramDouble2, int paramInt5, int paramInt6, Insets paramInsets, int paramInt7, int paramInt8)
  {
    gridx = paramInt1;
    gridy = paramInt2;
    gridwidth = paramInt3;
    gridheight = paramInt4;
    fill = paramInt6;
    ipadx = paramInt7;
    ipady = paramInt8;
    insets = paramInsets;
    anchor = paramInt5;
    weightx = paramDouble1;
    weighty = paramDouble2;
  }
  
  public Object clone()
  {
    try
    {
      GridBagConstraints localGridBagConstraints = (GridBagConstraints)super.clone();
      insets = ((Insets)insets.clone());
      return localGridBagConstraints;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  boolean isVerticallyResizable()
  {
    return (fill == 1) || (fill == 3);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\GridBagConstraints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */