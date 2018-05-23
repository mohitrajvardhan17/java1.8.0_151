package com.sun.org.apache.xalan.internal.xsltc.trax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Namespace;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Locator2;

public class SAX2StAXEventWriter
  extends SAX2StAXBaseWriter
{
  private XMLEventWriter writer;
  private XMLEventFactory eventFactory;
  private List namespaceStack = new ArrayList();
  private boolean needToCallStartDocument = false;
  
  public SAX2StAXEventWriter()
  {
    eventFactory = XMLEventFactory.newInstance();
  }
  
  public SAX2StAXEventWriter(XMLEventWriter paramXMLEventWriter)
  {
    writer = paramXMLEventWriter;
    eventFactory = XMLEventFactory.newInstance();
  }
  
  public SAX2StAXEventWriter(XMLEventWriter paramXMLEventWriter, XMLEventFactory paramXMLEventFactory)
  {
    writer = paramXMLEventWriter;
    if (paramXMLEventFactory != null) {
      eventFactory = paramXMLEventFactory;
    } else {
      eventFactory = XMLEventFactory.newInstance();
    }
  }
  
  public XMLEventWriter getEventWriter()
  {
    return writer;
  }
  
  public void setEventWriter(XMLEventWriter paramXMLEventWriter)
  {
    writer = paramXMLEventWriter;
  }
  
  public XMLEventFactory getEventFactory()
  {
    return eventFactory;
  }
  
  public void setEventFactory(XMLEventFactory paramXMLEventFactory)
  {
    eventFactory = paramXMLEventFactory;
  }
  
  public void startDocument()
    throws SAXException
  {
    super.startDocument();
    namespaceStack.clear();
    eventFactory.setLocation(getCurrentLocation());
    needToCallStartDocument = true;
  }
  
  private void writeStartDocument()
    throws SAXException
  {
    try
    {
      if (docLocator == null) {
        writer.add(eventFactory.createStartDocument());
      } else {
        try
        {
          writer.add(eventFactory.createStartDocument(((Locator2)docLocator).getEncoding(), ((Locator2)docLocator).getXMLVersion()));
        }
        catch (ClassCastException localClassCastException)
        {
          writer.add(eventFactory.createStartDocument());
        }
      }
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
    needToCallStartDocument = false;
  }
  
  public void endDocument()
    throws SAXException
  {
    eventFactory.setLocation(getCurrentLocation());
    try
    {
      writer.add(eventFactory.createEndDocument());
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
    super.endDocument();
    namespaceStack.clear();
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    if (needToCallStartDocument) {
      writeStartDocument();
    }
    eventFactory.setLocation(getCurrentLocation());
    Collection[] arrayOfCollection = { null, null };
    createStartEvents(paramAttributes, arrayOfCollection);
    namespaceStack.add(arrayOfCollection[0]);
    try
    {
      String[] arrayOfString = { null, null };
      parseQName(paramString3, arrayOfString);
      writer.add(eventFactory.createStartElement(arrayOfString[0], paramString1, arrayOfString[1], arrayOfCollection[1].iterator(), arrayOfCollection[0].iterator()));
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
    finally
    {
      super.startElement(paramString1, paramString2, paramString3, paramAttributes);
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    super.endElement(paramString1, paramString2, paramString3);
    eventFactory.setLocation(getCurrentLocation());
    String[] arrayOfString = { null, null };
    parseQName(paramString3, arrayOfString);
    Collection localCollection = (Collection)namespaceStack.remove(namespaceStack.size() - 1);
    Iterator localIterator = localCollection.iterator();
    try
    {
      writer.add(eventFactory.createEndElement(arrayOfString[0], paramString1, arrayOfString[1], localIterator));
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (needToCallStartDocument) {
      writeStartDocument();
    }
    super.comment(paramArrayOfChar, paramInt1, paramInt2);
    eventFactory.setLocation(getCurrentLocation());
    try
    {
      writer.add(eventFactory.createComment(new String(paramArrayOfChar, paramInt1, paramInt2)));
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
      if (!isCDATA)
      {
        eventFactory.setLocation(getCurrentLocation());
        writer.add(eventFactory.createCharacters(new String(paramArrayOfChar, paramInt1, paramInt2)));
      }
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    super.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
    characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    if (needToCallStartDocument) {
      writeStartDocument();
    }
    super.processingInstruction(paramString1, paramString2);
    try
    {
      writer.add(eventFactory.createProcessingInstruction(paramString1, paramString2));
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
  }
  
  public void endCDATA()
    throws SAXException
  {
    eventFactory.setLocation(getCurrentLocation());
    try
    {
      writer.add(eventFactory.createCData(CDATABuffer.toString()));
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
    super.endCDATA();
  }
  
  protected void createStartEvents(Attributes paramAttributes, Collection[] paramArrayOfCollection)
  {
    HashMap localHashMap = null;
    ArrayList localArrayList = null;
    String str2;
    Object localObject1;
    if (namespaces != null)
    {
      int i = namespaces.size();
      for (j = 0; j < i; j++)
      {
        String str1 = (String)namespaces.elementAt(j++);
        str2 = (String)namespaces.elementAt(j);
        localObject1 = createNamespace(str1, str2);
        if (localHashMap == null) {
          localHashMap = new HashMap();
        }
        localHashMap.put(str1, localObject1);
      }
    }
    String[] arrayOfString = { null, null };
    int j = 0;
    int k = paramAttributes.getLength();
    while (j < k)
    {
      parseQName(paramAttributes.getQName(j), arrayOfString);
      str2 = arrayOfString[0];
      localObject1 = arrayOfString[1];
      String str3 = paramAttributes.getQName(j);
      String str4 = paramAttributes.getValue(j);
      String str5 = paramAttributes.getURI(j);
      Object localObject2;
      if (("xmlns".equals(str3)) || ("xmlns".equals(str2)))
      {
        if (localHashMap == null) {
          localHashMap = new HashMap();
        }
        if (!localHashMap.containsKey(localObject1))
        {
          localObject2 = createNamespace((String)localObject1, str4);
          localHashMap.put(localObject1, localObject2);
        }
      }
      else
      {
        if (str2.length() > 0) {
          localObject2 = eventFactory.createAttribute(str2, str5, (String)localObject1, str4);
        } else {
          localObject2 = eventFactory.createAttribute((String)localObject1, str4);
        }
        if (localArrayList == null) {
          localArrayList = new ArrayList();
        }
        localArrayList.add(localObject2);
      }
      j++;
    }
    paramArrayOfCollection[0] = (localHashMap == null ? Collections.EMPTY_LIST : localHashMap.values());
    paramArrayOfCollection[1] = (localArrayList == null ? Collections.EMPTY_LIST : localArrayList);
  }
  
  protected Namespace createNamespace(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString1.length() == 0)) {
      return eventFactory.createNamespace(paramString2);
    }
    return eventFactory.createNamespace(paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\SAX2StAXEventWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */