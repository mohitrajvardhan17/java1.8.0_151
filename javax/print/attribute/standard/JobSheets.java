package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public class JobSheets
  extends EnumSyntax
  implements PrintRequestAttribute, PrintJobAttribute
{
  private static final long serialVersionUID = -4735258056132519759L;
  public static final JobSheets NONE = new JobSheets(0);
  public static final JobSheets STANDARD = new JobSheets(1);
  private static final String[] myStringTable = { "none", "standard" };
  private static final JobSheets[] myEnumValueTable = { NONE, STANDARD };
  
  protected JobSheets(int paramInt)
  {
    super(paramInt);
  }
  
  protected String[] getStringTable()
  {
    return (String[])myStringTable.clone();
  }
  
  protected EnumSyntax[] getEnumValueTable()
  {
    return (EnumSyntax[])myEnumValueTable.clone();
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return JobSheets.class;
  }
  
  public final String getName()
  {
    return "job-sheets";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\JobSheets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */