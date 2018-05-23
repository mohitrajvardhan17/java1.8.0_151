package sun.print;

import javax.print.AttributeException;
import javax.print.PrintException;
import javax.print.attribute.Attribute;

class PrintJobAttributeException
  extends PrintException
  implements AttributeException
{
  private Attribute attr;
  private Class category;
  
  PrintJobAttributeException(String paramString, Class paramClass, Attribute paramAttribute)
  {
    super(paramString);
    attr = paramAttribute;
    category = paramClass;
  }
  
  public Class[] getUnsupportedAttributes()
  {
    if (category == null) {
      return null;
    }
    Class[] arrayOfClass = { category };
    return arrayOfClass;
  }
  
  public Attribute[] getUnsupportedValues()
  {
    if (attr == null) {
      return null;
    }
    Attribute[] arrayOfAttribute = { attr };
    return arrayOfAttribute;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\PrintJobAttributeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */