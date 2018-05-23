package javax.swing.text.html;

import java.awt.Polygon;
import java.io.Serializable;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.text.AttributeSet;

class Map
  implements Serializable
{
  private String name;
  private Vector<AttributeSet> areaAttributes;
  private Vector<RegionContainment> areas;
  
  public Map() {}
  
  public Map(String paramString)
  {
    name = paramString;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void addArea(AttributeSet paramAttributeSet)
  {
    if (paramAttributeSet == null) {
      return;
    }
    if (areaAttributes == null) {
      areaAttributes = new Vector(2);
    }
    areaAttributes.addElement(paramAttributeSet.copyAttributes());
  }
  
  public void removeArea(AttributeSet paramAttributeSet)
  {
    if ((paramAttributeSet != null) && (areaAttributes != null))
    {
      int i = areas != null ? areas.size() : 0;
      for (int j = areaAttributes.size() - 1; j >= 0; j--) {
        if (((AttributeSet)areaAttributes.elementAt(j)).isEqual(paramAttributeSet))
        {
          areaAttributes.removeElementAt(j);
          if (j < i) {
            areas.removeElementAt(j);
          }
        }
      }
    }
  }
  
  public AttributeSet[] getAreas()
  {
    int i = areaAttributes != null ? areaAttributes.size() : 0;
    if (i != 0)
    {
      AttributeSet[] arrayOfAttributeSet = new AttributeSet[i];
      areaAttributes.copyInto(arrayOfAttributeSet);
      return arrayOfAttributeSet;
    }
    return null;
  }
  
  public AttributeSet getArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = areaAttributes != null ? areaAttributes.size() : 0;
    if (i > 0)
    {
      int j = areas != null ? areas.size() : 0;
      if (areas == null) {
        areas = new Vector(i);
      }
      for (int k = 0; k < i; k++)
      {
        if (k >= j) {
          areas.addElement(createRegionContainment((AttributeSet)areaAttributes.elementAt(k)));
        }
        RegionContainment localRegionContainment = (RegionContainment)areas.elementAt(k);
        if ((localRegionContainment != null) && (localRegionContainment.contains(paramInt1, paramInt2, paramInt3, paramInt4))) {
          return (AttributeSet)areaAttributes.elementAt(k);
        }
      }
    }
    return null;
  }
  
  protected RegionContainment createRegionContainment(AttributeSet paramAttributeSet)
  {
    Object localObject1 = paramAttributeSet.getAttribute(HTML.Attribute.SHAPE);
    if (localObject1 == null) {
      localObject1 = "rect";
    }
    if ((localObject1 instanceof String))
    {
      String str = ((String)localObject1).toLowerCase();
      Object localObject2 = null;
      try
      {
        if (str.equals("rect")) {
          localObject2 = new RectangleRegionContainment(paramAttributeSet);
        } else if (str.equals("circle")) {
          localObject2 = new CircleRegionContainment(paramAttributeSet);
        } else if (str.equals("poly")) {
          localObject2 = new PolygonRegionContainment(paramAttributeSet);
        } else if (str.equals("default")) {
          localObject2 = DefaultRegionContainment.sharedInstance();
        }
      }
      catch (RuntimeException localRuntimeException)
      {
        localObject2 = null;
      }
      return (RegionContainment)localObject2;
    }
    return null;
  }
  
  protected static int[] extractCoords(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof String))) {
      return null;
    }
    StringTokenizer localStringTokenizer = new StringTokenizer((String)paramObject, ", \t\n\r");
    Object localObject1 = null;
    int i = 0;
    Object localObject2;
    while (localStringTokenizer.hasMoreElements())
    {
      localObject2 = localStringTokenizer.nextToken();
      int j;
      if (((String)localObject2).endsWith("%"))
      {
        j = -1;
        localObject2 = ((String)localObject2).substring(0, ((String)localObject2).length() - 1);
      }
      else
      {
        j = 1;
      }
      try
      {
        int k = Integer.parseInt((String)localObject2);
        if (localObject1 == null)
        {
          localObject1 = new int[4];
        }
        else if (i == localObject1.length)
        {
          int[] arrayOfInt = new int[localObject1.length * 2];
          System.arraycopy(localObject1, 0, arrayOfInt, 0, localObject1.length);
          localObject1 = arrayOfInt;
        }
        localObject1[(i++)] = (k * j);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        return null;
      }
    }
    if ((i > 0) && (i != localObject1.length))
    {
      localObject2 = new int[i];
      System.arraycopy(localObject1, 0, localObject2, 0, i);
      localObject1 = localObject2;
    }
    return (int[])localObject1;
  }
  
  static class CircleRegionContainment
    implements Map.RegionContainment
  {
    int x;
    int y;
    int radiusSquared;
    float[] percentValues;
    int lastWidth;
    int lastHeight;
    
    public CircleRegionContainment(AttributeSet paramAttributeSet)
    {
      int[] arrayOfInt = Map.extractCoords(paramAttributeSet.getAttribute(HTML.Attribute.COORDS));
      if ((arrayOfInt == null) || (arrayOfInt.length != 3)) {
        throw new RuntimeException("Unable to parse circular area");
      }
      x = arrayOfInt[0];
      y = arrayOfInt[1];
      radiusSquared = (arrayOfInt[2] * arrayOfInt[2]);
      if ((arrayOfInt[0] < 0) || (arrayOfInt[1] < 0) || (arrayOfInt[2] < 0))
      {
        lastWidth = (lastHeight = -1);
        percentValues = new float[3];
        for (int i = 0; i < 3; i++) {
          if (arrayOfInt[i] < 0) {
            percentValues[i] = (arrayOfInt[i] / -100.0F);
          } else {
            percentValues[i] = -1.0F;
          }
        }
      }
      else
      {
        percentValues = null;
      }
    }
    
    public boolean contains(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if ((percentValues != null) && ((lastWidth != paramInt3) || (lastHeight != paramInt4)))
      {
        int i = Math.min(paramInt3, paramInt4) / 2;
        lastWidth = paramInt3;
        lastHeight = paramInt4;
        if (percentValues[0] != -1.0F) {
          x = ((int)(percentValues[0] * paramInt3));
        }
        if (percentValues[1] != -1.0F) {
          y = ((int)(percentValues[1] * paramInt4));
        }
        if (percentValues[2] != -1.0F)
        {
          radiusSquared = ((int)(percentValues[2] * Math.min(paramInt3, paramInt4)));
          radiusSquared *= radiusSquared;
        }
      }
      return (paramInt1 - x) * (paramInt1 - x) + (paramInt2 - y) * (paramInt2 - y) <= radiusSquared;
    }
  }
  
  static class DefaultRegionContainment
    implements Map.RegionContainment
  {
    static DefaultRegionContainment si = null;
    
    DefaultRegionContainment() {}
    
    public static DefaultRegionContainment sharedInstance()
    {
      if (si == null) {
        si = new DefaultRegionContainment();
      }
      return si;
    }
    
    public boolean contains(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return (paramInt1 <= paramInt3) && (paramInt1 >= 0) && (paramInt2 >= 0) && (paramInt2 <= paramInt3);
    }
  }
  
  static class PolygonRegionContainment
    extends Polygon
    implements Map.RegionContainment
  {
    float[] percentValues;
    int[] percentIndexs;
    int lastWidth;
    int lastHeight;
    
    public PolygonRegionContainment(AttributeSet paramAttributeSet)
    {
      int[] arrayOfInt = Map.extractCoords(paramAttributeSet.getAttribute(HTML.Attribute.COORDS));
      if ((arrayOfInt == null) || (arrayOfInt.length == 0) || (arrayOfInt.length % 2 != 0)) {
        throw new RuntimeException("Unable to parse polygon area");
      }
      int i = 0;
      lastWidth = (lastHeight = -1);
      for (int j = arrayOfInt.length - 1; j >= 0; j--) {
        if (arrayOfInt[j] < 0) {
          i++;
        }
      }
      if (i > 0)
      {
        percentIndexs = new int[i];
        percentValues = new float[i];
        j = arrayOfInt.length - 1;
        int k = 0;
        while (j >= 0)
        {
          if (arrayOfInt[j] < 0)
          {
            percentValues[k] = (arrayOfInt[j] / -100.0F);
            percentIndexs[k] = j;
            k++;
          }
          j--;
        }
      }
      else
      {
        percentIndexs = null;
        percentValues = null;
      }
      npoints = (arrayOfInt.length / 2);
      xpoints = new int[npoints];
      ypoints = new int[npoints];
      for (j = 0; j < npoints; j++)
      {
        xpoints[j] = arrayOfInt[(j + j)];
        ypoints[j] = arrayOfInt[(j + j + 1)];
      }
    }
    
    public boolean contains(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if ((percentValues == null) || ((lastWidth == paramInt3) && (lastHeight == paramInt4))) {
        return contains(paramInt1, paramInt2);
      }
      bounds = null;
      lastWidth = paramInt3;
      lastHeight = paramInt4;
      float f1 = paramInt3;
      float f2 = paramInt4;
      for (int i = percentValues.length - 1; i >= 0; i--) {
        if (percentIndexs[i] % 2 == 0) {
          xpoints[(percentIndexs[i] / 2)] = ((int)(percentValues[i] * f1));
        } else {
          ypoints[(percentIndexs[i] / 2)] = ((int)(percentValues[i] * f2));
        }
      }
      return contains(paramInt1, paramInt2);
    }
  }
  
  static class RectangleRegionContainment
    implements Map.RegionContainment
  {
    float[] percents;
    int lastWidth;
    int lastHeight;
    int x0;
    int y0;
    int x1;
    int y1;
    
    public RectangleRegionContainment(AttributeSet paramAttributeSet)
    {
      int[] arrayOfInt = Map.extractCoords(paramAttributeSet.getAttribute(HTML.Attribute.COORDS));
      percents = null;
      if ((arrayOfInt == null) || (arrayOfInt.length != 4)) {
        throw new RuntimeException("Unable to parse rectangular area");
      }
      x0 = arrayOfInt[0];
      y0 = arrayOfInt[1];
      x1 = arrayOfInt[2];
      y1 = arrayOfInt[3];
      if ((x0 < 0) || (y0 < 0) || (x1 < 0) || (y1 < 0))
      {
        percents = new float[4];
        lastWidth = (lastHeight = -1);
        for (int i = 0; i < 4; i++) {
          if (arrayOfInt[i] < 0) {
            percents[i] = (Math.abs(arrayOfInt[i]) / 100.0F);
          } else {
            percents[i] = -1.0F;
          }
        }
      }
    }
    
    public boolean contains(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (percents == null) {
        return contains(paramInt1, paramInt2);
      }
      if ((lastWidth != paramInt3) || (lastHeight != paramInt4))
      {
        lastWidth = paramInt3;
        lastHeight = paramInt4;
        if (percents[0] != -1.0F) {
          x0 = ((int)(percents[0] * paramInt3));
        }
        if (percents[1] != -1.0F) {
          y0 = ((int)(percents[1] * paramInt4));
        }
        if (percents[2] != -1.0F) {
          x1 = ((int)(percents[2] * paramInt3));
        }
        if (percents[3] != -1.0F) {
          y1 = ((int)(percents[3] * paramInt4));
        }
      }
      return contains(paramInt1, paramInt2);
    }
    
    public boolean contains(int paramInt1, int paramInt2)
    {
      return (paramInt1 >= x0) && (paramInt1 <= x1) && (paramInt2 >= y0) && (paramInt2 <= y1);
    }
  }
  
  static abstract interface RegionContainment
  {
    public abstract boolean contains(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\Map.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */