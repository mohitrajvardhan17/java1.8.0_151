package javax.print.attribute;

import java.io.Serializable;

public class HashPrintServiceAttributeSet
  extends HashAttributeSet
  implements PrintServiceAttributeSet, Serializable
{
  private static final long serialVersionUID = 6642904616179203070L;
  
  public HashPrintServiceAttributeSet()
  {
    super(PrintServiceAttribute.class);
  }
  
  public HashPrintServiceAttributeSet(PrintServiceAttribute paramPrintServiceAttribute)
  {
    super(paramPrintServiceAttribute, PrintServiceAttribute.class);
  }
  
  public HashPrintServiceAttributeSet(PrintServiceAttribute[] paramArrayOfPrintServiceAttribute)
  {
    super(paramArrayOfPrintServiceAttribute, PrintServiceAttribute.class);
  }
  
  public HashPrintServiceAttributeSet(PrintServiceAttributeSet paramPrintServiceAttributeSet)
  {
    super(paramPrintServiceAttributeSet, PrintServiceAttribute.class);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\HashPrintServiceAttributeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */