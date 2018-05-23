package javax.print.attribute.standard;

import java.net.URI;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.URISyntax;

public final class PrinterMoreInfoManufacturer
  extends URISyntax
  implements PrintServiceAttribute
{
  private static final long serialVersionUID = 3323271346485076608L;
  
  public PrinterMoreInfoManufacturer(URI paramURI)
  {
    super(paramURI);
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof PrinterMoreInfoManufacturer));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return PrinterMoreInfoManufacturer.class;
  }
  
  public final String getName()
  {
    return "printer-more-info-manufacturer";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\PrinterMoreInfoManufacturer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */