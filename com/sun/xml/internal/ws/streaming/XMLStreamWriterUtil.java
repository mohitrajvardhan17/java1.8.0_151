package com.sun.xml.internal.ws.streaming;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.encoding.HasEncoding;
import java.io.OutputStream;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XMLStreamWriterUtil
{
  private XMLStreamWriterUtil() {}
  
  @Nullable
  public static OutputStream getOutputStream(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    Object localObject = null;
    if ((paramXMLStreamWriter instanceof Map)) {
      localObject = ((Map)paramXMLStreamWriter).get("sjsxp-outputstream");
    }
    if (localObject == null) {
      try
      {
        localObject = paramXMLStreamWriter.getProperty("com.ctc.wstx.outputUnderlyingStream");
      }
      catch (Exception localException1) {}
    }
    if (localObject == null) {
      try
      {
        localObject = paramXMLStreamWriter.getProperty("http://java.sun.com/xml/stream/properties/outputstream");
      }
      catch (Exception localException2) {}
    }
    if (localObject != null)
    {
      paramXMLStreamWriter.writeCharacters("");
      paramXMLStreamWriter.flush();
      return (OutputStream)localObject;
    }
    return null;
  }
  
  @Nullable
  public static String getEncoding(XMLStreamWriter paramXMLStreamWriter)
  {
    return (paramXMLStreamWriter instanceof HasEncoding) ? ((HasEncoding)paramXMLStreamWriter).getEncoding() : null;
  }
  
  public static String encodeQName(XMLStreamWriter paramXMLStreamWriter, QName paramQName, PrefixFactory paramPrefixFactory)
  {
    try
    {
      String str1 = paramQName.getNamespaceURI();
      String str2 = paramQName.getLocalPart();
      if ((str1 == null) || (str1.equals(""))) {
        return str2;
      }
      String str3 = paramXMLStreamWriter.getPrefix(str1);
      if (str3 == null)
      {
        str3 = paramPrefixFactory.getPrefix(str1);
        paramXMLStreamWriter.writeNamespace(str3, str1);
      }
      return str3 + ":" + str2;
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new RuntimeException(localXMLStreamException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\streaming\XMLStreamWriterUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */