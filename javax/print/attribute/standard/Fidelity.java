package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class Fidelity
  extends EnumSyntax
  implements PrintJobAttribute, PrintRequestAttribute
{
  private static final long serialVersionUID = 6320827847329172308L;
  public static final Fidelity FIDELITY_TRUE = new Fidelity(0);
  public static final Fidelity FIDELITY_FALSE = new Fidelity(1);
  private static final String[] myStringTable = { "true", "false" };
  private static final Fidelity[] myEnumValueTable = { FIDELITY_TRUE, FIDELITY_FALSE };
  
  protected Fidelity(int paramInt)
  {
    super(paramInt);
  }
  
  protected String[] getStringTable()
  {
    return myStringTable;
  }
  
  protected EnumSyntax[] getEnumValueTable()
  {
    return myEnumValueTable;
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return Fidelity.class;
  }
  
  public final String getName()
  {
    return "ipp-attribute-fidelity";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\Fidelity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */