package com.sun.xml.internal.bind;

import java.text.MessageFormat;
import java.util.ResourceBundle;

 enum Messages
{
  FAILED_TO_INITIALE_DATATYPE_FACTORY;
  
  private static final ResourceBundle rb = ResourceBundle.getBundle(Messages.class.getName());
  
  private Messages() {}
  
  public String toString()
  {
    return format(new Object[0]);
  }
  
  public String format(Object... paramVarArgs)
  {
    return MessageFormat.format(rb.getString(name()), paramVarArgs);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\Messages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */