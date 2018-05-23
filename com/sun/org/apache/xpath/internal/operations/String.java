package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class String
  extends UnaryOperation
{
  static final long serialVersionUID = 2973374377453022888L;
  
  public String() {}
  
  public XObject operate(XObject paramXObject)
    throws TransformerException
  {
    return (XString)paramXObject.xstr();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\operations\String.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */