package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class Sides
  extends EnumSyntax
  implements DocAttribute, PrintRequestAttribute, PrintJobAttribute
{
  private static final long serialVersionUID = -6890309414893262822L;
  public static final Sides ONE_SIDED = new Sides(0);
  public static final Sides TWO_SIDED_LONG_EDGE = new Sides(1);
  public static final Sides TWO_SIDED_SHORT_EDGE = new Sides(2);
  public static final Sides DUPLEX = TWO_SIDED_LONG_EDGE;
  public static final Sides TUMBLE = TWO_SIDED_SHORT_EDGE;
  private static final String[] myStringTable = { "one-sided", "two-sided-long-edge", "two-sided-short-edge" };
  private static final Sides[] myEnumValueTable = { ONE_SIDED, TWO_SIDED_LONG_EDGE, TWO_SIDED_SHORT_EDGE };
  
  protected Sides(int paramInt)
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
    return Sides.class;
  }
  
  public final String getName()
  {
    return "sides";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\Sides.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */