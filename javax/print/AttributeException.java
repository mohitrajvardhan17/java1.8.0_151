package javax.print;

import javax.print.attribute.Attribute;

public abstract interface AttributeException
{
  public abstract Class[] getUnsupportedAttributes();
  
  public abstract Attribute[] getUnsupportedValues();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\AttributeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */