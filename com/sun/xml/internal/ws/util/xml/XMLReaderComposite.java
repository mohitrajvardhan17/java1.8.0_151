package com.sun.xml.internal.ws.util.xml;

import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx;
import com.sun.xml.internal.ws.encoding.TagInfoset;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.helpers.AttributesImpl;

public class XMLReaderComposite
  implements XMLStreamReaderEx
{
  protected State state = State.StartTag;
  protected ElemInfo elemInfo;
  protected TagInfoset tagInfo;
  protected XMLStreamReader[] children;
  protected int payloadIndex = -1;
  protected XMLStreamReader payloadReader;
  
  public XMLReaderComposite(ElemInfo paramElemInfo, XMLStreamReader[] paramArrayOfXMLStreamReader)
  {
    elemInfo = paramElemInfo;
    tagInfo = tagInfo;
    children = paramArrayOfXMLStreamReader;
    if ((children != null) && (children.length > 0))
    {
      payloadIndex = 0;
      payloadReader = children[payloadIndex];
    }
  }
  
  public int next()
    throws XMLStreamException
  {
    switch (state)
    {
    case StartTag: 
      if (payloadReader != null)
      {
        state = State.Payload;
        return payloadReader.getEventType();
      }
      state = State.EndTag;
      return 2;
    case EndTag: 
      return 8;
    }
    int i = 8;
    if ((payloadReader != null) && (payloadReader.hasNext())) {
      i = payloadReader.next();
    }
    if (i != 8) {
      return i;
    }
    if (payloadIndex + 1 < children.length)
    {
      payloadIndex += 1;
      payloadReader = children[payloadIndex];
      return payloadReader.getEventType();
    }
    state = State.EndTag;
    return 2;
  }
  
  public boolean hasNext()
    throws XMLStreamException
  {
    switch (state)
    {
    case EndTag: 
      return false;
    }
    return true;
  }
  
  public String getElementText()
    throws XMLStreamException
  {
    switch (state)
    {
    case StartTag: 
      if (payloadReader.isCharacters()) {
        return payloadReader.getText();
      }
      return "";
    }
    return payloadReader.getElementText();
  }
  
  public int nextTag()
    throws XMLStreamException
  {
    int i = next();
    if (i == 8) {
      return i;
    }
    while (i != 8)
    {
      if (i == 1) {
        return i;
      }
      if (i == 2) {
        return i;
      }
      i = next();
    }
    return i;
  }
  
  public Object getProperty(String paramString)
    throws IllegalArgumentException
  {
    return payloadReader != null ? payloadReader.getProperty(paramString) : null;
  }
  
  public void require(int paramInt, String paramString1, String paramString2)
    throws XMLStreamException
  {
    if (payloadReader != null) {
      payloadReader.require(paramInt, paramString1, paramString2);
    }
  }
  
  public void close()
    throws XMLStreamException
  {
    if (payloadReader != null) {
      payloadReader.close();
    }
  }
  
  public String getNamespaceURI(String paramString)
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return elemInfo.getNamespaceURI(paramString);
    }
    return payloadReader.getNamespaceURI(paramString);
  }
  
  public boolean isStartElement()
  {
    switch (state)
    {
    case StartTag: 
      return true;
    case EndTag: 
      return false;
    }
    return payloadReader.isStartElement();
  }
  
  public boolean isEndElement()
  {
    switch (state)
    {
    case StartTag: 
      return false;
    case EndTag: 
      return true;
    }
    return payloadReader.isEndElement();
  }
  
  public boolean isCharacters()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return false;
    }
    return payloadReader.isCharacters();
  }
  
  public boolean isWhiteSpace()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return false;
    }
    return payloadReader.isWhiteSpace();
  }
  
  public String getAttributeValue(String paramString1, String paramString2)
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return tagInfo.atts.getValue(paramString1, paramString2);
    }
    return payloadReader.getAttributeValue(paramString1, paramString2);
  }
  
  public int getAttributeCount()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return tagInfo.atts.getLength();
    }
    return payloadReader.getAttributeCount();
  }
  
  public QName getAttributeName(int paramInt)
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return new QName(tagInfo.atts.getURI(paramInt), tagInfo.atts.getLocalName(paramInt), getPrfix(tagInfo.atts.getQName(paramInt)));
    }
    return payloadReader.getAttributeName(paramInt);
  }
  
  public String getAttributeNamespace(int paramInt)
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return tagInfo.atts.getURI(paramInt);
    }
    return payloadReader.getAttributeNamespace(paramInt);
  }
  
  public String getAttributeLocalName(int paramInt)
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return tagInfo.atts.getLocalName(paramInt);
    }
    return payloadReader.getAttributeLocalName(paramInt);
  }
  
  public String getAttributePrefix(int paramInt)
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return getPrfix(tagInfo.atts.getQName(paramInt));
    }
    return payloadReader.getAttributePrefix(paramInt);
  }
  
  private static String getPrfix(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int i = paramString.indexOf(":");
    return i > 0 ? paramString.substring(0, i) : "";
  }
  
  public String getAttributeType(int paramInt)
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return tagInfo.atts.getType(paramInt);
    }
    return payloadReader.getAttributeType(paramInt);
  }
  
  public String getAttributeValue(int paramInt)
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return tagInfo.atts.getValue(paramInt);
    }
    return payloadReader.getAttributeValue(paramInt);
  }
  
  public boolean isAttributeSpecified(int paramInt)
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return tagInfo.atts.getLocalName(paramInt) != null;
    }
    return payloadReader.isAttributeSpecified(paramInt);
  }
  
  public int getNamespaceCount()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return tagInfo.ns.length / 2;
    }
    return payloadReader.getNamespaceCount();
  }
  
  public String getNamespacePrefix(int paramInt)
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return tagInfo.ns[(2 * paramInt)];
    }
    return payloadReader.getNamespacePrefix(paramInt);
  }
  
  public String getNamespaceURI(int paramInt)
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return tagInfo.ns[(2 * paramInt + 1)];
    }
    return payloadReader.getNamespaceURI(paramInt);
  }
  
  public NamespaceContextEx getNamespaceContext()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return new NamespaceContextExAdaper(elemInfo);
    }
    return isPayloadReaderEx() ? payloadReaderEx().getNamespaceContext() : new NamespaceContextExAdaper(payloadReader.getNamespaceContext());
  }
  
  private boolean isPayloadReaderEx()
  {
    return payloadReader instanceof XMLStreamReaderEx;
  }
  
  private XMLStreamReaderEx payloadReaderEx()
  {
    return (XMLStreamReaderEx)payloadReader;
  }
  
  public int getEventType()
  {
    switch (state)
    {
    case StartTag: 
      return 1;
    case EndTag: 
      return 2;
    }
    return payloadReader.getEventType();
  }
  
  public String getText()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return null;
    }
    return payloadReader.getText();
  }
  
  public char[] getTextCharacters()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return null;
    }
    return payloadReader.getTextCharacters();
  }
  
  public int getTextCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3)
    throws XMLStreamException
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return -1;
    }
    return payloadReader.getTextCharacters(paramInt1, paramArrayOfChar, paramInt2, paramInt3);
  }
  
  public int getTextStart()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return 0;
    }
    return payloadReader.getTextStart();
  }
  
  public int getTextLength()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return 0;
    }
    return payloadReader.getTextLength();
  }
  
  public String getEncoding()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return null;
    }
    return payloadReader.getEncoding();
  }
  
  public boolean hasText()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return false;
    }
    return payloadReader.hasText();
  }
  
  public Location getLocation()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      new Location()
      {
        public int getLineNumber()
        {
          return 0;
        }
        
        public int getColumnNumber()
        {
          return 0;
        }
        
        public int getCharacterOffset()
        {
          return 0;
        }
        
        public String getPublicId()
        {
          return null;
        }
        
        public String getSystemId()
        {
          return null;
        }
      };
    }
    return payloadReader.getLocation();
  }
  
  public QName getName()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return new QName(tagInfo.nsUri, tagInfo.localName, tagInfo.prefix);
    }
    return payloadReader.getName();
  }
  
  public String getLocalName()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return tagInfo.localName;
    }
    return payloadReader.getLocalName();
  }
  
  public boolean hasName()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return true;
    }
    return payloadReader.hasName();
  }
  
  public String getNamespaceURI()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return tagInfo.nsUri;
    }
    return payloadReader.getNamespaceURI();
  }
  
  public String getPrefix()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return tagInfo.prefix;
    }
    return payloadReader.getPrefix();
  }
  
  public String getVersion()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return null;
    }
    return payloadReader.getVersion();
  }
  
  public boolean isStandalone()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return true;
    }
    return payloadReader.isStandalone();
  }
  
  public boolean standaloneSet()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return true;
    }
    return payloadReader.standaloneSet();
  }
  
  public String getCharacterEncodingScheme()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return null;
    }
    return payloadReader.getCharacterEncodingScheme();
  }
  
  public String getPITarget()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return null;
    }
    return payloadReader.getPITarget();
  }
  
  public String getPIData()
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return null;
    }
    return payloadReader.getPIData();
  }
  
  public String getElementTextTrim()
    throws XMLStreamException
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return null;
    }
    return isPayloadReaderEx() ? payloadReaderEx().getElementTextTrim() : payloadReader.getElementText().trim();
  }
  
  public CharSequence getPCDATA()
    throws XMLStreamException
  {
    switch (state)
    {
    case StartTag: 
    case EndTag: 
      return null;
    }
    return isPayloadReaderEx() ? payloadReaderEx().getPCDATA() : payloadReader.getElementText();
  }
  
  public static class ElemInfo
    implements NamespaceContext
  {
    ElemInfo ancestor;
    TagInfoset tagInfo;
    
    public ElemInfo(TagInfoset paramTagInfoset, ElemInfo paramElemInfo)
    {
      tagInfo = paramTagInfoset;
      ancestor = paramElemInfo;
    }
    
    public String getNamespaceURI(String paramString)
    {
      String str = tagInfo.getNamespaceURI(paramString);
      return ancestor != null ? ancestor.getNamespaceURI(paramString) : str != null ? str : null;
    }
    
    public String getPrefix(String paramString)
    {
      String str = tagInfo.getPrefix(paramString);
      return ancestor != null ? ancestor.getPrefix(paramString) : str != null ? str : null;
    }
    
    public List<String> allPrefixes(String paramString)
    {
      List localList1 = tagInfo.allPrefixes(paramString);
      if (ancestor != null)
      {
        List localList2 = ancestor.allPrefixes(paramString);
        localList2.addAll(localList1);
        return localList2;
      }
      return localList1;
    }
    
    public Iterator<String> getPrefixes(String paramString)
    {
      return allPrefixes(paramString).iterator();
    }
  }
  
  public static enum State
  {
    StartTag,  Payload,  EndTag;
    
    private State() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\xml\XMLReaderComposite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */