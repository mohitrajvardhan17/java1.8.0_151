package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.SetOfIntegerSyntax;
import javax.print.attribute.SupportedValuesAttribute;

public final class NumberUpSupported
  extends SetOfIntegerSyntax
  implements SupportedValuesAttribute
{
  private static final long serialVersionUID = -1041573395759141805L;
  
  public NumberUpSupported(int[][] paramArrayOfInt)
  {
    super(paramArrayOfInt);
    if (paramArrayOfInt == null) {
      throw new NullPointerException("members is null");
    }
    int[][] arrayOfInt = getMembers();
    int i = arrayOfInt.length;
    if (i == 0) {
      throw new IllegalArgumentException("members is zero-length");
    }
    for (int j = 0; j < i; j++) {
      if (arrayOfInt[j][0] < 1) {
        throw new IllegalArgumentException("Number up value must be > 0");
      }
    }
  }
  
  public NumberUpSupported(int paramInt)
  {
    super(paramInt);
    if (paramInt < 1) {
      throw new IllegalArgumentException("Number up value must be > 0");
    }
  }
  
  public NumberUpSupported(int paramInt1, int paramInt2)
  {
    super(paramInt1, paramInt2);
    if (paramInt1 > paramInt2) {
      throw new IllegalArgumentException("Null range specified");
    }
    if (paramInt1 < 1) {
      throw new IllegalArgumentException("Number up value must be > 0");
    }
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof NumberUpSupported));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return NumberUpSupported.class;
  }
  
  public final String getName()
  {
    return "number-up-supported";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\NumberUpSupported.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */