package javax.print.attribute.standard;

import java.net.URI;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.URISyntax;

public final class Destination
  extends URISyntax
  implements PrintJobAttribute, PrintRequestAttribute
{
  private static final long serialVersionUID = 6776739171700415321L;
  
  public Destination(URI paramURI)
  {
    super(paramURI);
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof Destination));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return Destination.class;
  }
  
  public final String getName()
  {
    return "spool-data-destination";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\Destination.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */