package javax.print.attribute;

import java.io.Serializable;
import java.util.Date;

public abstract class DateTimeSyntax
  implements Serializable, Cloneable
{
  private static final long serialVersionUID = -1400819079791208582L;
  private Date value;
  
  protected DateTimeSyntax(Date paramDate)
  {
    if (paramDate == null) {
      throw new NullPointerException("value is null");
    }
    value = paramDate;
  }
  
  public Date getValue()
  {
    return new Date(value.getTime());
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject != null) && ((paramObject instanceof DateTimeSyntax)) && (value.equals(value));
  }
  
  public int hashCode()
  {
    return value.hashCode();
  }
  
  public String toString()
  {
    return "" + value;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\DateTimeSyntax.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */