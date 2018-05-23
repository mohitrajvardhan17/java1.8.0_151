package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import javax.xml.namespace.NamespaceContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public final class InterningXmlVisitor
  implements XmlVisitor
{
  private final XmlVisitor next;
  private final AttributesImpl attributes = new AttributesImpl(null);
  
  public InterningXmlVisitor(XmlVisitor paramXmlVisitor)
  {
    next = paramXmlVisitor;
  }
  
  public void startDocument(LocatorEx paramLocatorEx, NamespaceContext paramNamespaceContext)
    throws SAXException
  {
    next.startDocument(paramLocatorEx, paramNamespaceContext);
  }
  
  public void endDocument()
    throws SAXException
  {
    next.endDocument();
  }
  
  public void startElement(TagName paramTagName)
    throws SAXException
  {
    attributes.setAttributes(atts);
    atts = attributes;
    uri = intern(uri);
    local = intern(local);
    next.startElement(paramTagName);
  }
  
  public void endElement(TagName paramTagName)
    throws SAXException
  {
    uri = intern(uri);
    local = intern(local);
    next.endElement(paramTagName);
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    next.startPrefixMapping(intern(paramString1), intern(paramString2));
  }
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {
    next.endPrefixMapping(intern(paramString));
  }
  
  public void text(CharSequence paramCharSequence)
    throws SAXException
  {
    next.text(paramCharSequence);
  }
  
  public UnmarshallingContext getContext()
  {
    return next.getContext();
  }
  
  public XmlVisitor.TextPredictor getPredictor()
  {
    return next.getPredictor();
  }
  
  private static String intern(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return paramString.intern();
  }
  
  private static class AttributesImpl
    implements Attributes
  {
    private Attributes core;
    
    private AttributesImpl() {}
    
    void setAttributes(Attributes paramAttributes)
    {
      core = paramAttributes;
    }
    
    public int getIndex(String paramString)
    {
      return core.getIndex(paramString);
    }
    
    public int getIndex(String paramString1, String paramString2)
    {
      return core.getIndex(paramString1, paramString2);
    }
    
    public int getLength()
    {
      return core.getLength();
    }
    
    public String getLocalName(int paramInt)
    {
      return InterningXmlVisitor.intern(core.getLocalName(paramInt));
    }
    
    public String getQName(int paramInt)
    {
      return InterningXmlVisitor.intern(core.getQName(paramInt));
    }
    
    public String getType(int paramInt)
    {
      return InterningXmlVisitor.intern(core.getType(paramInt));
    }
    
    public String getType(String paramString)
    {
      return InterningXmlVisitor.intern(core.getType(paramString));
    }
    
    public String getType(String paramString1, String paramString2)
    {
      return InterningXmlVisitor.intern(core.getType(paramString1, paramString2));
    }
    
    public String getURI(int paramInt)
    {
      return InterningXmlVisitor.intern(core.getURI(paramInt));
    }
    
    public String getValue(int paramInt)
    {
      return core.getValue(paramInt);
    }
    
    public String getValue(String paramString)
    {
      return core.getValue(paramString);
    }
    
    public String getValue(String paramString1, String paramString2)
    {
      return core.getValue(paramString1, paramString2);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\InterningXmlVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */