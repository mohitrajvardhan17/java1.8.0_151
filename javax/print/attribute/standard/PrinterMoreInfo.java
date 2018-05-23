package javax.print.attribute.standard;

import java.net.URI;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.URISyntax;

public final class PrinterMoreInfo
  extends URISyntax
  implements PrintServiceAttribute
{
  private static final long serialVersionUID = 4555850007675338574L;
  
  public PrinterMoreInfo(URI paramURI)
  {
    super(paramURI);
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof PrinterMoreInfo));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return PrinterMoreInfo.class;
  }
  
  public final String getName()
  {
    return "printer-more-info";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\PrinterMoreInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */