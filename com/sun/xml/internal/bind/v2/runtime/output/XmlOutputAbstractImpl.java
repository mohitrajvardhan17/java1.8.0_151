package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public abstract class XmlOutputAbstractImpl
  implements XmlOutput
{
  protected int[] nsUriIndex2prefixIndex;
  protected NamespaceContextImpl nsContext;
  protected XMLSerializer serializer;
  
  public XmlOutputAbstractImpl() {}
  
  public void startDocument(XMLSerializer paramXMLSerializer, boolean paramBoolean, int[] paramArrayOfInt, NamespaceContextImpl paramNamespaceContextImpl)
    throws IOException, SAXException, XMLStreamException
  {
    nsUriIndex2prefixIndex = paramArrayOfInt;
    nsContext = paramNamespaceContextImpl;
    serializer = paramXMLSerializer;
  }
  
  public void endDocument(boolean paramBoolean)
    throws IOException, SAXException, XMLStreamException
  {
    serializer = null;
  }
  
  public void beginStartTag(Name paramName)
    throws IOException, XMLStreamException
  {
    beginStartTag(nsUriIndex2prefixIndex[nsUriIndex], localName);
  }
  
  public abstract void beginStartTag(int paramInt, String paramString)
    throws IOException, XMLStreamException;
  
  public void attribute(Name paramName, String paramString)
    throws IOException, XMLStreamException
  {
    int i = nsUriIndex;
    if (i == -1) {
      attribute(-1, localName, paramString);
    } else {
      attribute(nsUriIndex2prefixIndex[i], localName, paramString);
    }
  }
  
  public abstract void attribute(int paramInt, String paramString1, String paramString2)
    throws IOException, XMLStreamException;
  
  public abstract void endStartTag()
    throws IOException, SAXException;
  
  public void endTag(Name paramName)
    throws IOException, SAXException, XMLStreamException
  {
    endTag(nsUriIndex2prefixIndex[nsUriIndex], localName);
  }
  
  public abstract void endTag(int paramInt, String paramString)
    throws IOException, SAXException, XMLStreamException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\XmlOutputAbstractImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */