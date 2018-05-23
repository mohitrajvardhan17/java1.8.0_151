package javax.print.attribute;

import java.io.Serializable;

public abstract interface Attribute
  extends Serializable
{
  public abstract Class<? extends Attribute> getCategory();
  
  public abstract String getName();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */