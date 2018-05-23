package javax.print.attribute;

import java.io.Serializable;

public class HashPrintRequestAttributeSet
  extends HashAttributeSet
  implements PrintRequestAttributeSet, Serializable
{
  private static final long serialVersionUID = 2364756266107751933L;
  
  public HashPrintRequestAttributeSet()
  {
    super(PrintRequestAttribute.class);
  }
  
  public HashPrintRequestAttributeSet(PrintRequestAttribute paramPrintRequestAttribute)
  {
    super(paramPrintRequestAttribute, PrintRequestAttribute.class);
  }
  
  public HashPrintRequestAttributeSet(PrintRequestAttribute[] paramArrayOfPrintRequestAttribute)
  {
    super(paramArrayOfPrintRequestAttribute, PrintRequestAttribute.class);
  }
  
  public HashPrintRequestAttributeSet(PrintRequestAttributeSet paramPrintRequestAttributeSet)
  {
    super(paramPrintRequestAttributeSet, PrintRequestAttribute.class);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\HashPrintRequestAttributeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */