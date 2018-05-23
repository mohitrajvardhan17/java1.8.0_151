package java.awt;

import java.io.Serializable;

public class Insets
  implements Cloneable, Serializable
{
  public int top;
  public int left;
  public int bottom;
  public int right;
  private static final long serialVersionUID = -2272572637695466749L;
  
  public Insets(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    top = paramInt1;
    left = paramInt2;
    bottom = paramInt3;
    right = paramInt4;
  }
  
  public void set(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    top = paramInt1;
    left = paramInt2;
    bottom = paramInt3;
    right = paramInt4;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Insets))
    {
      Insets localInsets = (Insets)paramObject;
      return (top == top) && (left == left) && (bottom == bottom) && (right == right);
    }
    return false;
  }
  
  public int hashCode()
  {
    int i = left + bottom;
    int j = right + top;
    int k = i * (i + 1) / 2 + left;
    int m = j * (j + 1) / 2 + top;
    int n = k + m;
    return n * (n + 1) / 2 + m;
  }
  
  public String toString()
  {
    return getClass().getName() + "[top=" + top + ",left=" + left + ",bottom=" + bottom + ",right=" + right + "]";
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  private static native void initIDs();
  
  static
  {
    
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Insets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */