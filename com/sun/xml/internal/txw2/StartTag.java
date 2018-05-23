package com.sun.xml.internal.txw2;

class StartTag
  extends Content
  implements NamespaceResolver
{
  private String uri;
  private final String localName;
  private Attribute firstAtt;
  private Attribute lastAtt;
  private ContainerElement owner;
  private NamespaceDecl firstNs;
  private NamespaceDecl lastNs;
  final Document document;
  
  public StartTag(ContainerElement paramContainerElement, String paramString1, String paramString2)
  {
    this(document, paramString1, paramString2);
    owner = paramContainerElement;
  }
  
  public StartTag(Document paramDocument, String paramString1, String paramString2)
  {
    assert (paramString1 != null);
    assert (paramString2 != null);
    uri = paramString1;
    localName = paramString2;
    document = paramDocument;
    addNamespaceDecl(paramString1, null, false);
  }
  
  public void addAttribute(String paramString1, String paramString2, Object paramObject)
  {
    checkWritable();
    for (Attribute localAttribute = firstAtt; (localAttribute != null) && (!localAttribute.hasName(paramString1, paramString2)); localAttribute = next) {}
    if (localAttribute == null)
    {
      localAttribute = new Attribute(paramString1, paramString2);
      if (lastAtt == null)
      {
        assert (firstAtt == null);
        firstAtt = (lastAtt = localAttribute);
      }
      else
      {
        assert (firstAtt != null);
        lastAtt.next = localAttribute;
        lastAtt = localAttribute;
      }
      if (paramString1.length() > 0) {
        addNamespaceDecl(paramString1, null, true);
      }
    }
    document.writeValue(paramObject, this, value);
  }
  
  public NamespaceDecl addNamespaceDecl(String paramString1, String paramString2, boolean paramBoolean)
  {
    checkWritable();
    if (paramString1 == null) {
      throw new IllegalArgumentException();
    }
    if (paramString1.length() == 0)
    {
      if (paramBoolean) {
        throw new IllegalArgumentException("The empty namespace cannot have a non-empty prefix");
      }
      if ((paramString2 != null) && (paramString2.length() > 0)) {
        throw new IllegalArgumentException("The empty namespace can be only bound to the empty prefix");
      }
      paramString2 = "";
    }
    for (NamespaceDecl localNamespaceDecl = firstNs; localNamespaceDecl != null; localNamespaceDecl = next)
    {
      if (paramString1.equals(uri))
      {
        if (paramString2 == null)
        {
          requirePrefix |= paramBoolean;
          return localNamespaceDecl;
        }
        if (prefix == null)
        {
          prefix = paramString2;
          requirePrefix |= paramBoolean;
          return localNamespaceDecl;
        }
        if (paramString2.equals(prefix))
        {
          requirePrefix |= paramBoolean;
          return localNamespaceDecl;
        }
      }
      if ((paramString2 != null) && (prefix != null) && (prefix.equals(paramString2))) {
        throw new IllegalArgumentException("Prefix '" + paramString2 + "' is already bound to '" + uri + '\'');
      }
    }
    localNamespaceDecl = new NamespaceDecl(document.assignNewId(), paramString1, paramString2, paramBoolean);
    if (lastNs == null)
    {
      assert (firstNs == null);
      firstNs = (lastNs = localNamespaceDecl);
    }
    else
    {
      assert (firstNs != null);
      lastNs.next = localNamespaceDecl;
      lastNs = localNamespaceDecl;
    }
    return localNamespaceDecl;
  }
  
  private void checkWritable()
  {
    if (isWritten()) {
      throw new IllegalStateException("The start tag of " + localName + " has already been written. If you need out of order writing, see the TypedXmlWriter.block method");
    }
  }
  
  boolean isWritten()
  {
    return uri == null;
  }
  
  boolean isReadyToCommit()
  {
    if ((owner != null) && (owner.isBlocked())) {
      return false;
    }
    for (Content localContent = getNext(); localContent != null; localContent = localContent.getNext()) {
      if (localContent.concludesPendingStartTag()) {
        return true;
      }
    }
    return false;
  }
  
  public void written()
  {
    firstAtt = (lastAtt = null);
    uri = null;
    if (owner != null)
    {
      assert (owner.startTag == this);
      owner.startTag = null;
    }
  }
  
  boolean concludesPendingStartTag()
  {
    return true;
  }
  
  void accept(ContentVisitor paramContentVisitor)
  {
    paramContentVisitor.onStartTag(uri, localName, firstAtt, firstNs);
  }
  
  public String getPrefix(String paramString)
  {
    NamespaceDecl localNamespaceDecl = addNamespaceDecl(paramString, null, false);
    if (prefix != null) {
      return prefix;
    }
    return dummyPrefix;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\StartTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */