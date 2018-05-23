package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class Lt
  extends Operation
{
  static final long serialVersionUID = 3388420509289359422L;
  
  public Lt() {}
  
  public XObject operate(XObject paramXObject1, XObject paramXObject2)
    throws TransformerException
  {
    return paramXObject1.lessThan(paramXObject2) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\operations\Lt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */