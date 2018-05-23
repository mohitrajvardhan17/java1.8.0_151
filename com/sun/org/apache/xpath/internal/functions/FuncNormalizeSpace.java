package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class FuncNormalizeSpace
  extends FunctionDef1Arg
{
  static final long serialVersionUID = -3377956872032190880L;
  
  public FuncNormalizeSpace() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    XMLString localXMLString = getArg0AsString(paramXPathContext);
    return (XString)localXMLString.fixWhiteSpace(true, true, false);
  }
  
  public void executeCharsToContentHandler(XPathContext paramXPathContext, ContentHandler paramContentHandler)
    throws TransformerException, SAXException
  {
    if (Arg0IsNodesetExpr())
    {
      int i = getArg0AsNode(paramXPathContext);
      if (-1 != i)
      {
        DTM localDTM = paramXPathContext.getDTM(i);
        localDTM.dispatchCharactersEvents(i, paramContentHandler, true);
      }
    }
    else
    {
      XObject localXObject = execute(paramXPathContext);
      localXObject.dispatchCharactersEvents(paramContentHandler);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncNormalizeSpace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */