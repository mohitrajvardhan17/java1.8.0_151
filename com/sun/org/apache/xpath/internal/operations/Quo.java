package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

/**
 * @deprecated
 */
public class Quo
  extends Operation
{
  static final long serialVersionUID = 693765299196169905L;
  
  public Quo() {}
  
  public XObject operate(XObject paramXObject1, XObject paramXObject2)
    throws TransformerException
  {
    return new XNumber((int)(paramXObject1.num() / paramXObject2.num()));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\operations\Quo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */