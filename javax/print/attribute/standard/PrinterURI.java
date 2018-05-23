package javax.print.attribute.standard;

import java.net.URI;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.URISyntax;

public final class PrinterURI
  extends URISyntax
  implements PrintServiceAttribute
{
  private static final long serialVersionUID = 7923912792485606497L;
  
  public PrinterURI(URI paramURI)
  {
    super(paramURI);
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof PrinterURI));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return PrinterURI.class;
  }
  
  public final String getName()
  {
    return "printer-uri";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\PrinterURI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */