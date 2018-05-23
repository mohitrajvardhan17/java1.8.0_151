package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;

public final class Severity
  extends EnumSyntax
  implements Attribute
{
  private static final long serialVersionUID = 8781881462717925380L;
  public static final Severity REPORT = new Severity(0);
  public static final Severity WARNING = new Severity(1);
  public static final Severity ERROR = new Severity(2);
  private static final String[] myStringTable = { "report", "warning", "error" };
  private static final Severity[] myEnumValueTable = { REPORT, WARNING, ERROR };
  
  protected Severity(int paramInt)
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
    return Severity.class;
  }
  
  public final String getName()
  {
    return "severity";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\Severity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */