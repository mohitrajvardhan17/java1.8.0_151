package javax.print.attribute.standard;

import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.TextSyntax;

public final class PrinterName
  extends TextSyntax
  implements PrintServiceAttribute
{
  private static final long serialVersionUID = 299740639137803127L;
  
  public PrinterName(String paramString, Locale paramLocale)
  {
    super(paramString, paramLocale);
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof PrinterName));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return PrinterName.class;
  }
  
  public final String getName()
  {
    return "printer-name";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\PrinterName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */