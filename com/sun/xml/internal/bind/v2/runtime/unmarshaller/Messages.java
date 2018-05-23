package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import java.text.MessageFormat;
import java.util.ResourceBundle;

 enum Messages
{
  UNRESOLVED_IDREF,  UNEXPECTED_ELEMENT,  UNEXPECTED_TEXT,  NOT_A_QNAME,  UNRECOGNIZED_TYPE_NAME,  UNRECOGNIZED_TYPE_NAME_MAYBE,  UNABLE_TO_CREATE_MAP,  UNINTERNED_STRINGS,  ERRORS_LIMIT_EXCEEDED;
  
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\Messages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */