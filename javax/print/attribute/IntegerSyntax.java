package javax.print.attribute;

import java.io.Serializable;

public abstract class IntegerSyntax
  implements Serializable, Cloneable
{
  private static final long serialVersionUID = 3644574816328081943L;
  private int value;
  
  protected IntegerSyntax(int paramInt)
  {
    value = paramInt;
  }
  
  protected IntegerSyntax(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt2 > paramInt1) || (paramInt1 > paramInt3)) {
      throw new IllegalArgumentException("Value " + paramInt1 + " not in range " + paramInt2 + ".." + paramInt3);
    }
    value = paramInt1;
  }
  
  public int getValue()
  {
    return value;
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject != null) && ((paramObject instanceof IntegerSyntax)) && (value == value);
  }
  
  public int hashCode()
  {
    return value;
  }
  
  public String toString()
  {
    return "" + value;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\IntegerSyntax.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */