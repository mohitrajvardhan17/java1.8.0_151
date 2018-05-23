package java.awt;

import java.io.Serializable;

public class GridBagLayoutInfo
  implements Serializable
{
  private static final long serialVersionUID = -4899416460737170217L;
  int width;
  int height;
  int startx;
  int starty;
  int[] minWidth;
  int[] minHeight;
  double[] weightX;
  double[] weightY;
  boolean hasBaseline;
  short[] baselineType;
  int[] maxAscent;
  int[] maxDescent;
  
  GridBagLayoutInfo(int paramInt1, int paramInt2)
  {
    width = paramInt1;
    height = paramInt2;
  }
  
  boolean hasConstantDescent(int paramInt)
  {
    return (baselineType[paramInt] & 1 << Component.BaselineResizeBehavior.CONSTANT_DESCENT.ordinal()) != 0;
  }
  
  boolean hasBaseline(int paramInt)
  {
    return (hasBaseline) && (baselineType[paramInt] != 0);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\GridBagLayoutInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */