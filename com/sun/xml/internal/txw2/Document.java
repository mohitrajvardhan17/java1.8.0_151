package com.sun.xml.internal.txw2;

import com.sun.xml.internal.txw2.output.XmlSerializer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class Document
{
  private final XmlSerializer out;
  private boolean started = false;
  private Content current = null;
  private final Map<Class, DatatypeWriter> datatypeWriters = new HashMap();
  private int iota = 1;
  private final NamespaceSupport inscopeNamespace = new NamespaceSupport();
  private NamespaceDecl activeNamespaces;
  private final ContentVisitor visitor = new ContentVisitor()
  {
    public void onStartDocument()
    {
      throw new IllegalStateException();
    }
    
    public void onEndDocument()
    {
      out.endDocument();
    }
    
    public void onEndTag()
    {
      out.endTag();
      inscopeNamespace.popContext();
      activeNamespaces = null;
    }
    
    public void onPcdata(StringBuilder paramAnonymousStringBuilder)
    {
      if (activeNamespaces != null) {
        paramAnonymousStringBuilder = Document.this.fixPrefix(paramAnonymousStringBuilder);
      }
      out.text(paramAnonymousStringBuilder);
    }
    
    public void onCdata(StringBuilder paramAnonymousStringBuilder)
    {
      if (activeNamespaces != null) {
        paramAnonymousStringBuilder = Document.this.fixPrefix(paramAnonymousStringBuilder);
      }
      out.cdata(paramAnonymousStringBuilder);
    }
    
    public void onComment(StringBuilder paramAnonymousStringBuilder)
    {
      if (activeNamespaces != null) {
        paramAnonymousStringBuilder = Document.this.fixPrefix(paramAnonymousStringBuilder);
      }
      out.comment(paramAnonymousStringBuilder);
    }
    
    public void onStartTag(String paramAnonymousString1, String paramAnonymousString2, Attribute paramAnonymousAttribute, NamespaceDecl paramAnonymousNamespaceDecl)
    {
      assert (paramAnonymousString1 != null);
      assert (paramAnonymousString2 != null);
      activeNamespaces = paramAnonymousNamespaceDecl;
      if (!started)
      {
        started = true;
        out.startDocument();
      }
      inscopeNamespace.pushContext();
      String str;
      for (Object localObject = paramAnonymousNamespaceDecl; localObject != null; localObject = next)
      {
        declared = false;
        if (prefix != null)
        {
          str = inscopeNamespace.getURI(prefix);
          if ((str == null) || (!str.equals(uri)))
          {
            inscopeNamespace.declarePrefix(prefix, uri);
            declared = true;
          }
        }
      }
      for (localObject = paramAnonymousNamespaceDecl; localObject != null; localObject = next) {
        if (prefix == null) {
          if (inscopeNamespace.getURI("").equals(uri))
          {
            prefix = "";
          }
          else
          {
            str = inscopeNamespace.getPrefix(uri);
            if (str == null)
            {
              while (inscopeNamespace.getURI(str = Document.this.newPrefix()) != null) {}
              declared = true;
              inscopeNamespace.declarePrefix(str, uri);
            }
            prefix = str;
          }
        }
      }
      assert (uri.equals(paramAnonymousString1));
      assert (prefix != null) : "a prefix must have been all allocated";
      out.beginStartTag(paramAnonymousString1, paramAnonymousString2, prefix);
      for (localObject = paramAnonymousNamespaceDecl; localObject != null; localObject = next) {
        if (declared) {
          out.writeXmlns(prefix, uri);
        }
      }
      for (localObject = paramAnonymousAttribute; localObject != null; localObject = next)
      {
        if (nsUri.length() == 0) {
          str = "";
        } else {
          str = inscopeNamespace.getPrefix(nsUri);
        }
        out.writeAttribute(nsUri, localName, str, Document.this.fixPrefix(value));
      }
      out.endStartTag(paramAnonymousString1, paramAnonymousString2, prefix);
    }
  };
  private final StringBuilder prefixSeed = new StringBuilder("ns");
  private int prefixIota = 0;
  static final char MAGIC = '\000';
  
  Document(XmlSerializer paramXmlSerializer)
  {
    out = paramXmlSerializer;
    Iterator localIterator = DatatypeWriter.BUILTIN.iterator();
    while (localIterator.hasNext())
    {
      DatatypeWriter localDatatypeWriter = (DatatypeWriter)localIterator.next();
      datatypeWriters.put(localDatatypeWriter.getType(), localDatatypeWriter);
    }
  }
  
  void flush()
  {
    out.flush();
  }
  
  void setFirstContent(Content paramContent)
  {
    assert (current == null);
    current = new StartDocument();
    current.setNext(this, paramContent);
  }
  
  public void addDatatypeWriter(DatatypeWriter<?> paramDatatypeWriter)
  {
    datatypeWriters.put(paramDatatypeWriter.getType(), paramDatatypeWriter);
  }
  
  void run()
  {
    for (;;)
    {
      Content localContent = current.getNext();
      if ((localContent == null) || (!localContent.isReadyToCommit())) {
        return;
      }
      localContent.accept(visitor);
      localContent.written();
      current = localContent;
    }
  }
  
  void writeValue(Object paramObject, NamespaceResolver paramNamespaceResolver, StringBuilder paramStringBuilder)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("argument contains null");
    }
    if ((paramObject instanceof Object[]))
    {
      for (Object localObject3 : (Object[])paramObject) {
        writeValue(localObject3, paramNamespaceResolver, paramStringBuilder);
      }
      return;
    }
    Object localObject2;
    if ((paramObject instanceof Iterable))
    {
      ??? = ((Iterable)paramObject).iterator();
      while (((Iterator)???).hasNext())
      {
        localObject2 = ((Iterator)???).next();
        writeValue(localObject2, paramNamespaceResolver, paramStringBuilder);
      }
      return;
    }
    if (paramStringBuilder.length() > 0) {
      paramStringBuilder.append(' ');
    }
    for (??? = paramObject.getClass(); ??? != null; ??? = ((Class)???).getSuperclass())
    {
      localObject2 = (DatatypeWriter)datatypeWriters.get(???);
      if (localObject2 != null)
      {
        ((DatatypeWriter)localObject2).print(paramObject, paramNamespaceResolver, paramStringBuilder);
        return;
      }
    }
    paramStringBuilder.append(paramObject);
  }
  
  private String newPrefix()
  {
    prefixSeed.setLength(2);
    prefixSeed.append(++prefixIota);
    return prefixSeed.toString();
  }
  
  private StringBuilder fixPrefix(StringBuilder paramStringBuilder)
  {
    assert (activeNamespaces != null);
    int j = paramStringBuilder.length();
    for (int i = 0; (i < j) && (paramStringBuilder.charAt(i) != 0); i++) {}
    if (i == j) {
      return paramStringBuilder;
    }
    while (i < j)
    {
      int k = paramStringBuilder.charAt(i + 1);
      for (NamespaceDecl localNamespaceDecl = activeNamespaces; (localNamespaceDecl != null) && (uniqueId != k); localNamespaceDecl = next) {}
      if (localNamespaceDecl == null) {
        throw new IllegalStateException("Unexpected use of prefixes " + paramStringBuilder);
      }
      int m = 2;
      String str = prefix;
      if (str.length() == 0)
      {
        if ((paramStringBuilder.length() <= i + 2) || (paramStringBuilder.charAt(i + 2) != ':')) {
          throw new IllegalStateException("Unexpected use of prefixes " + paramStringBuilder);
        }
        m = 3;
      }
      paramStringBuilder.replace(i, i + m, str);
      j += str.length() - m;
      while ((i < j) && (paramStringBuilder.charAt(i) != 0)) {
        i++;
      }
    }
    return paramStringBuilder;
  }
  
  char assignNewId()
  {
    return (char)iota++;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\Document.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */