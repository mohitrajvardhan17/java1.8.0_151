package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XmlDataContentHandler
  implements DataContentHandler
{
  private final DataFlavor[] flavors = new DataFlavor[3];
  
  public XmlDataContentHandler()
    throws ClassNotFoundException
  {
    flavors[0] = new ActivationDataFlavor(StreamSource.class, "text/xml", "XML");
    flavors[1] = new ActivationDataFlavor(StreamSource.class, "application/xml", "XML");
    flavors[2] = new ActivationDataFlavor(String.class, "text/xml", "XML String");
  }
  
  public DataFlavor[] getTransferDataFlavors()
  {
    return (DataFlavor[])Arrays.copyOf(flavors, flavors.length);
  }
  
  public Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource)
    throws IOException
  {
    for (DataFlavor localDataFlavor : flavors) {
      if (localDataFlavor.equals(paramDataFlavor)) {
        return getContent(paramDataSource);
      }
    }
    return null;
  }
  
  public Object getContent(DataSource paramDataSource)
    throws IOException
  {
    String str1 = paramDataSource.getContentType();
    String str2 = null;
    if (str1 != null)
    {
      ContentType localContentType = new ContentType(str1);
      if (!isXml(localContentType)) {
        throw new IOException("Cannot convert DataSource with content type \"" + str1 + "\" to object in XmlDataContentHandler");
      }
      str2 = localContentType.getParameter("charset");
    }
    return str2 != null ? new StreamSource(new InputStreamReader(paramDataSource.getInputStream()), str2) : new StreamSource(paramDataSource.getInputStream());
  }
  
  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    if ((!(paramObject instanceof DataSource)) && (!(paramObject instanceof Source)) && (!(paramObject instanceof String))) {
      throw new IOException("Invalid Object type = " + paramObject.getClass() + ". XmlDataContentHandler can only convert DataSource|Source|String to XML.");
    }
    ContentType localContentType = new ContentType(paramString);
    if (!isXml(localContentType)) {
      throw new IOException("Invalid content type \"" + paramString + "\" for XmlDataContentHandler");
    }
    String str = localContentType.getParameter("charset");
    Object localObject2;
    if ((paramObject instanceof String))
    {
      localObject1 = (String)paramObject;
      if (str == null) {
        str = "utf-8";
      }
      localObject2 = new OutputStreamWriter(paramOutputStream, str);
      ((OutputStreamWriter)localObject2).write((String)localObject1, 0, ((String)localObject1).length());
      ((OutputStreamWriter)localObject2).flush();
      return;
    }
    Object localObject1 = (paramObject instanceof DataSource) ? (Source)getContent((DataSource)paramObject) : (Source)paramObject;
    try
    {
      localObject2 = XmlUtil.newTransformer();
      if (str != null) {
        ((Transformer)localObject2).setOutputProperty("encoding", str);
      }
      StreamResult localStreamResult = new StreamResult(paramOutputStream);
      ((Transformer)localObject2).transform((Source)localObject1, localStreamResult);
    }
    catch (Exception localException)
    {
      throw new IOException("Unable to run the JAXP transformer in XmlDataContentHandler " + localException.getMessage());
    }
  }
  
  private boolean isXml(ContentType paramContentType)
  {
    return (paramContentType.getSubType().equals("xml")) && ((paramContentType.getPrimaryType().equals("text")) || (paramContentType.getPrimaryType().equals("application")));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\XmlDataContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */