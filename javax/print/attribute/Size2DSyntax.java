package javax.print.attribute;

import java.io.Serializable;

public abstract class Size2DSyntax
  implements Serializable, Cloneable
{
  private static final long serialVersionUID = 5584439964938660530L;
  private int x;
  private int y;
  public static final int INCH = 25400;
  public static final int MM = 1000;
  
  protected Size2DSyntax(float paramFloat1, float paramFloat2, int paramInt)
  {
    if (paramFloat1 < 0.0F) {
      throw new IllegalArgumentException("x < 0");
    }
    if (paramFloat2 < 0.0F) {
      throw new IllegalArgumentException("y < 0");
    }
    if (paramInt < 1) {
      throw new IllegalArgumentException("units < 1");
    }
    x = ((int)(paramFloat1 * paramInt + 0.5F));
    y = ((int)(paramFloat2 * paramInt + 0.5F));
  }
  
  protected Size2DSyntax(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("x < 0");
    }
    if (paramInt2 < 0) {
      throw new IllegalArgumentException("y < 0");
    }
    if (paramInt3 < 1) {
      throw new IllegalArgumentException("units < 1");
    }
    x = (paramInt1 * paramInt3);
    y = (paramInt2 * paramInt3);
  }
  
  private static float convertFromMicrometers(int paramInt1, int paramInt2)
  {
    if (paramInt2 < 1) {
      throw new IllegalArgumentException("units is < 1");
    }
    return paramInt1 / paramInt2;
  }
  
  public float[] getSize(int paramInt)
  {
    return new float[] { getX(paramInt), getY(paramInt) };
  }
  
  public float getX(int paramInt)
  {
    return convertFromMicrometers(x, paramInt);
  }
  
  public float getY(int paramInt)
  {
    return convertFromMicrometers(y, paramInt);
  }
  
  public String toString(int paramInt, String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(getX(paramInt));
    localStringBuffer.append('x');
    localStringBuffer.append(getY(paramInt));
    if (paramString != null)
    {
      localStringBuffer.append(' ');
      localStringBuffer.append(paramString);
    }
    return localStringBuffer.toString();
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject != null) && ((paramObject instanceof Size2DSyntax)) && (x == x) && (y == y);
  }
  
  public int hashCode()
  {
    return x & 0xFFFF | (y & 0xFFFF) << 16;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(x);
    localStringBuffer.append('x');
    localStringBuffer.append(y);
    localStringBuffer.append(" um");
    return localStringBuffer.toString();
  }
  
  protected int getXMicrometers()
  {
    return x;
  }
  
  protected int getYMicrometers()
  {
    return y;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\Size2DSyntax.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */