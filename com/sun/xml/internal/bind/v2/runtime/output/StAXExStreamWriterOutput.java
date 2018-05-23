package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx;
import javax.xml.stream.XMLStreamException;

public final class StAXExStreamWriterOutput
  extends XMLStreamWriterOutput
{
  private final XMLStreamWriterEx out;
  
  public StAXExStreamWriterOutput(XMLStreamWriterEx paramXMLStreamWriterEx)
  {
    super(paramXMLStreamWriterEx);
    out = paramXMLStreamWriterEx;
  }
  
  public void text(Pcdata paramPcdata, boolean paramBoolean)
    throws XMLStreamException
  {
    if (paramBoolean) {
      out.writeCharacters(" ");
    }
    if (!(paramPcdata instanceof Base64Data))
    {
      out.writeCharacters(paramPcdata.toString());
    }
    else
    {
      Base64Data localBase64Data = (Base64Data)paramPcdata;
      out.writeBinary(localBase64Data.getDataHandler());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\StAXExStreamWriterOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */