package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class Copies
  extends IntegerSyntax
  implements PrintRequestAttribute, PrintJobAttribute
{
  private static final long serialVersionUID = -6426631521680023833L;
  
  public Copies(int paramInt)
  {
    super(paramInt, 1, Integer.MAX_VALUE);
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof Copies));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return Copies.class;
  }
  
  public final String getName()
  {
    return "copies";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\Copies.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */