package com.sun.xml.internal.bind.v2.runtime.reflect;

import java.text.MessageFormat;
import java.util.ResourceBundle;

 enum Messages
{
  UNABLE_TO_ACCESS_NON_PUBLIC_FIELD,  UNASSIGNABLE_TYPE,  NO_SETTER,  NO_GETTER;
  
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\Messages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */