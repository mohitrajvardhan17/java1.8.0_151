package com.sun.xml.internal.ws.util.xml;

import com.sun.xml.internal.org.jvnet.staxex.Base64Data;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx;
import com.sun.xml.internal.ws.streaming.MtomStreamWriter;
import java.io.IOException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class XMLStreamReaderToXMLStreamWriter
{
  private static final int BUF_SIZE = 4096;
  protected XMLStreamReader in;
  protected XMLStreamWriter out;
  private char[] buf;
  boolean optimizeBase64Data = false;
  AttachmentMarshaller mtomAttachmentMarshaller;
  
  public XMLStreamReaderToXMLStreamWriter() {}
  
  public void bridge(XMLStreamReader paramXMLStreamReader, XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    assert ((paramXMLStreamReader != null) && (paramXMLStreamWriter != null));
    in = paramXMLStreamReader;
    out = paramXMLStreamWriter;
    optimizeBase64Data = (paramXMLStreamReader instanceof XMLStreamReaderEx);
    if (((paramXMLStreamWriter instanceof XMLStreamWriterEx)) && ((paramXMLStreamWriter instanceof MtomStreamWriter))) {
      mtomAttachmentMarshaller = ((MtomStreamWriter)paramXMLStreamWriter).getAttachmentMarshaller();
    }
    int i = 0;
    buf = new char['က'];
    int j = paramXMLStreamReader.getEventType();
    if (j == 7) {
      while (!paramXMLStreamReader.isStartElement())
      {
        j = paramXMLStreamReader.next();
        if (j == 5) {
          handleComment();
        }
      }
    }
    if (j != 1) {
      throw new IllegalStateException("The current event is not START_ELEMENT\n but " + j);
    }
    do
    {
      switch (j)
      {
      case 1: 
        i++;
        handleStartElement();
        break;
      case 2: 
        handleEndElement();
        i--;
        if (i == 0) {
          return;
        }
        break;
      case 4: 
        handleCharacters();
        break;
      case 9: 
        handleEntityReference();
        break;
      case 3: 
        handlePI();
        break;
      case 5: 
        handleComment();
        break;
      case 11: 
        handleDTD();
        break;
      case 12: 
        handleCDATA();
        break;
      case 6: 
        handleSpace();
        break;
      case 8: 
        throw new XMLStreamException("Malformed XML at depth=" + i + ", Reached EOF. Event=" + j);
      case 7: 
      case 10: 
      default: 
        throw new XMLStreamException("Cannot process event: " + j);
      }
      j = paramXMLStreamReader.next();
    } while (i != 0);
  }
  
  protected void handlePI()
    throws XMLStreamException
  {
    out.writeProcessingInstruction(in.getPITarget(), in.getPIData());
  }
  
  protected void handleCharacters()
    throws XMLStreamException
  {
    CharSequence localCharSequence = null;
    if (optimizeBase64Data) {
      localCharSequence = ((XMLStreamReaderEx)in).getPCDATA();
    }
    if ((localCharSequence != null) && ((localCharSequence instanceof Base64Data)))
    {
      if (mtomAttachmentMarshaller != null)
      {
        Base64Data localBase64Data = (Base64Data)localCharSequence;
        ((XMLStreamWriterEx)out).writeBinary(localBase64Data.getDataHandler());
      }
      else
      {
        try
        {
          ((Base64Data)localCharSequence).writeTo(out);
        }
        catch (IOException localIOException)
        {
          throw new XMLStreamException(localIOException);
        }
      }
    }
    else
    {
      int i = 0;
      int j = buf.length;
      while (j == buf.length)
      {
        j = in.getTextCharacters(i, buf, 0, buf.length);
        out.writeCharacters(buf, 0, j);
        i += buf.length;
      }
    }
  }
  
  protected void handleEndElement()
    throws XMLStreamException
  {
    out.writeEndElement();
  }
  
  protected void handleStartElement()
    throws XMLStreamException
  {
    String str = in.getNamespaceURI();
    if (str == null) {
      out.writeStartElement(in.getLocalName());
    } else {
      out.writeStartElement(fixNull(in.getPrefix()), in.getLocalName(), str);
    }
    int i = in.getNamespaceCount();
    for (int j = 0; j < i; j++) {
      out.writeNamespace(in.getNamespacePrefix(j), fixNull(in.getNamespaceURI(j)));
    }
    j = in.getAttributeCount();
    for (int k = 0; k < j; k++) {
      handleAttribute(k);
    }
  }
  
  protected void handleAttribute(int paramInt)
    throws XMLStreamException
  {
    String str1 = in.getAttributeNamespace(paramInt);
    String str2 = in.getAttributePrefix(paramInt);
    if (fixNull(str1).equals("http://www.w3.org/2000/xmlns/")) {
      return;
    }
    if ((str1 == null) || (str2 == null) || (str2.equals(""))) {
      out.writeAttribute(in.getAttributeLocalName(paramInt), in.getAttributeValue(paramInt));
    } else {
      out.writeAttribute(str2, str1, in.getAttributeLocalName(paramInt), in.getAttributeValue(paramInt));
    }
  }
  
  protected void handleDTD()
    throws XMLStreamException
  {
    out.writeDTD(in.getText());
  }
  
  protected void handleComment()
    throws XMLStreamException
  {
    out.writeComment(in.getText());
  }
  
  protected void handleEntityReference()
    throws XMLStreamException
  {
    out.writeEntityRef(in.getText());
  }
  
  protected void handleSpace()
    throws XMLStreamException
  {
    handleCharacters();
  }
  
  protected void handleCDATA()
    throws XMLStreamException
  {
    out.writeCData(in.getText());
  }
  
  private static String fixNull(String paramString)
  {
    if (paramString == null) {
      return "";
    }
    return paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\xml\XMLStreamReaderToXMLStreamWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */