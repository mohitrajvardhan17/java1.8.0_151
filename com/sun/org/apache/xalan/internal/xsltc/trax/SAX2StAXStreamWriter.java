package com.sun.org.apache.xalan.internal.xsltc.trax;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Locator2;

public class SAX2StAXStreamWriter
  extends SAX2StAXBaseWriter
{
  private XMLStreamWriter writer;
  private boolean needToCallStartDocument = false;
  
  public SAX2StAXStreamWriter() {}
  
  public SAX2StAXStreamWriter(XMLStreamWriter paramXMLStreamWriter)
  {
    writer = paramXMLStreamWriter;
  }
  
  public XMLStreamWriter getStreamWriter()
  {
    return writer;
  }
  
  public void setStreamWriter(XMLStreamWriter paramXMLStreamWriter)
  {
    writer = paramXMLStreamWriter;
  }
  
  public void startDocument()
    throws SAXException
  {
    super.startDocument();
    needToCallStartDocument = true;
  }
  
  public void endDocument()
    throws SAXException
  {
    try
    {
      writer.writeEndDocument();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
    super.endDocument();
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    if (needToCallStartDocument)
    {
      try
      {
        if (docLocator == null) {
          writer.writeStartDocument();
        } else {
          try
          {
            writer.writeStartDocument(((Locator2)docLocator).getXMLVersion());
          }
          catch (ClassCastException localClassCastException)
          {
            writer.writeStartDocument();
          }
        }
      }
      catch (XMLStreamException localXMLStreamException1)
      {
        throw new SAXException(localXMLStreamException1);
      }
      needToCallStartDocument = false;
    }
    try
    {
      String[] arrayOfString = { null, null };
      parseQName(paramString3, arrayOfString);
      writer.writeStartElement(paramString3);
      int i = 0;
      int j = paramAttributes.getLength();
      while (i < j)
      {
        parseQName(paramAttributes.getQName(i), arrayOfString);
        String str1 = arrayOfString[0];
        String str2 = arrayOfString[1];
        String str3 = paramAttributes.getQName(i);
        String str4 = paramAttributes.getValue(i);
        String str5 = paramAttributes.getURI(i);
        if (("xmlns".equals(str1)) || ("xmlns".equals(str3)))
        {
          if (str2.length() == 0) {
            writer.setDefaultNamespace(str4);
          } else {
            writer.setPrefix(str2, str4);
          }
          writer.writeNamespace(str2, str4);
        }
        else if (str1.length() > 0)
        {
          writer.writeAttribute(str1, str5, str2, str4);
        }
        else
        {
          writer.writeAttribute(str3, str4);
        }
        i++;
      }
    }
    catch (XMLStreamException localXMLStreamException2)
    {
      throw new SAXException(localXMLStreamException2);
    }
    finally
    {
      super.startElement(paramString1, paramString2, paramString3, paramAttributes);
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    try
    {
      writer.writeEndElement();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
    finally
    {
      super.endElement(paramString1, paramString2, paramString3);
    }
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    super.comment(paramArrayOfChar, paramInt1, paramInt2);
    try
    {
      writer.writeComment(new String(paramArrayOfChar, paramInt1, paramInt2));
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    super.characters(paramArrayOfChar, paramInt1, paramInt2);
    try
    {
      if (!isCDATA) {
        writer.writeCharacters(paramArrayOfChar, paramInt1, paramInt2);
      }
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
  }
  
  public void endCDATA()
    throws SAXException
  {
    try
    {
      writer.writeCData(CDATABuffer.toString());
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
    super.endCDATA();
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    super.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
    try
    {
      writer.writeCharacters(paramArrayOfChar, paramInt1, paramInt2);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    super.processingInstruction(paramString1, paramString2);
    try
    {
      writer.writeProcessingInstruction(paramString1, paramString2);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\SAX2StAXStreamWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */