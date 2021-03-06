package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class TextImpl
  extends DefaultText
{
  String fData = null;
  SchemaDOM fSchemaDOM = null;
  int fRow;
  int fCol;
  
  public TextImpl(StringBuffer paramStringBuffer, SchemaDOM paramSchemaDOM, int paramInt1, int paramInt2)
  {
    fData = paramStringBuffer.toString();
    fSchemaDOM = paramSchemaDOM;
    fRow = paramInt1;
    fCol = paramInt2;
    rawname = (prefix = localpart = uri = null);
    nodeType = 3;
  }
  
  public Node getParentNode()
  {
    return fSchemaDOM.relations[fRow][0];
  }
  
  public Node getPreviousSibling()
  {
    if (fCol == 1) {
      return null;
    }
    return fSchemaDOM.relations[fRow][(fCol - 1)];
  }
  
  public Node getNextSibling()
  {
    if (fCol == fSchemaDOM.relations[fRow].length - 1) {
      return null;
    }
    return fSchemaDOM.relations[fRow][(fCol + 1)];
  }
  
  public String getData()
    throws DOMException
  {
    return fData;
  }
  
  public int getLength()
  {
    if (fData == null) {
      return 0;
    }
    return fData.length();
  }
  
  public String substringData(int paramInt1, int paramInt2)
    throws DOMException
  {
    if (fData == null) {
      return null;
    }
    if ((paramInt2 < 0) || (paramInt1 < 0) || (paramInt1 > fData.length())) {
      throw new DOMException((short)1, "parameter error");
    }
    if (paramInt1 + paramInt2 >= fData.length()) {
      return fData.substring(paramInt1);
    }
    return fData.substring(paramInt1, paramInt1 + paramInt2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\opti\TextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */