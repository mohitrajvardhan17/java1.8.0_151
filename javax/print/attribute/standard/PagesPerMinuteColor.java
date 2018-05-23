package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintServiceAttribute;

public final class PagesPerMinuteColor
  extends IntegerSyntax
  implements PrintServiceAttribute
{
  static final long serialVersionUID = 1684993151687470944L;
  
  public PagesPerMinuteColor(int paramInt)
  {
    super(paramInt, 0, Integer.MAX_VALUE);
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof PagesPerMinuteColor));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return PagesPerMinuteColor.class;
  }
  
  public final String getName()
  {
    return "pages-per-minute-color";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\PagesPerMinuteColor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */