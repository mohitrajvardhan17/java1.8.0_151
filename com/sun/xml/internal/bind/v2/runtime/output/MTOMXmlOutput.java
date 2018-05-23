package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data;
import java.io.IOException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class MTOMXmlOutput
  extends XmlOutputAbstractImpl
{
  private final XmlOutput next;
  private String nsUri;
  private String localName;
  
  public MTOMXmlOutput(XmlOutput paramXmlOutput)
  {
    next = paramXmlOutput;
  }
  
  public void startDocument(XMLSerializer paramXMLSerializer, boolean paramBoolean, int[] paramArrayOfInt, NamespaceContextImpl paramNamespaceContextImpl)
    throws IOException, SAXException, XMLStreamException
  {
    super.startDocument(paramXMLSerializer, paramBoolean, paramArrayOfInt, paramNamespaceContextImpl);
    next.startDocument(paramXMLSerializer, paramBoolean, paramArrayOfInt, paramNamespaceContextImpl);
  }
  
  public void endDocument(boolean paramBoolean)
    throws IOException, SAXException, XMLStreamException
  {
    next.endDocument(paramBoolean);
    super.endDocument(paramBoolean);
  }
  
  public void beginStartTag(Name paramName)
    throws IOException, XMLStreamException
  {
    next.beginStartTag(paramName);
    nsUri = nsUri;
    localName = localName;
  }
  
  public void beginStartTag(int paramInt, String paramString)
    throws IOException, XMLStreamException
  {
    next.beginStartTag(paramInt, paramString);
    nsUri = nsContext.getNamespaceURI(paramInt);
    localName = paramString;
  }
  
  public void attribute(Name paramName, String paramString)
    throws IOException, XMLStreamException
  {
    next.attribute(paramName, paramString);
  }
  
  public void attribute(int paramInt, String paramString1, String paramString2)
    throws IOException, XMLStreamException
  {
    next.attribute(paramInt, paramString1, paramString2);
  }
  
  public void endStartTag()
    throws IOException, SAXException
  {
    next.endStartTag();
  }
  
  public void endTag(Name paramName)
    throws IOException, SAXException, XMLStreamException
  {
    next.endTag(paramName);
  }
  
  public void endTag(int paramInt, String paramString)
    throws IOException, SAXException, XMLStreamException
  {
    next.endTag(paramInt, paramString);
  }
  
  public void text(String paramString, boolean paramBoolean)
    throws IOException, SAXException, XMLStreamException
  {
    next.text(paramString, paramBoolean);
  }
  
  public void text(Pcdata paramPcdata, boolean paramBoolean)
    throws IOException, SAXException, XMLStreamException
  {
    if (((paramPcdata instanceof Base64Data)) && (!serializer.getInlineBinaryFlag()))
    {
      Base64Data localBase64Data = (Base64Data)paramPcdata;
      String str;
      if (localBase64Data.hasData()) {
        str = serializer.attachmentMarshaller.addMtomAttachment(localBase64Data.get(), 0, localBase64Data.getDataLen(), localBase64Data.getMimeType(), nsUri, localName);
      } else {
        str = serializer.attachmentMarshaller.addMtomAttachment(localBase64Data.getDataHandler(), nsUri, localName);
      }
      if (str != null)
      {
        nsContext.getCurrent().push();
        int i = nsContext.declareNsUri("http://www.w3.org/2004/08/xop/include", "xop", false);
        beginStartTag(i, "Include");
        attribute(-1, "href", str);
        endStartTag();
        endTag(i, "Include");
        nsContext.getCurrent().pop();
        return;
      }
    }
    next.text(paramPcdata, paramBoolean);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\MTOMXmlOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */