package com.sun.xml.internal.txw2;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlCDATA;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.annotation.XmlNamespace;
import com.sun.xml.internal.txw2.annotation.XmlValue;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.xml.namespace.QName;

final class ContainerElement
  implements InvocationHandler, TypedXmlWriter
{
  final Document document;
  StartTag startTag;
  final EndTag endTag = new EndTag();
  private final String nsUri;
  private Content tail;
  private ContainerElement prevOpen;
  private ContainerElement nextOpen;
  private final ContainerElement parent;
  private ContainerElement lastOpenChild;
  private boolean blocked;
  
  public ContainerElement(Document paramDocument, ContainerElement paramContainerElement, String paramString1, String paramString2)
  {
    parent = paramContainerElement;
    document = paramDocument;
    nsUri = paramString1;
    startTag = new StartTag(this, paramString1, paramString2);
    tail = startTag;
    if (isRoot()) {
      paramDocument.setFirstContent(startTag);
    }
  }
  
  private boolean isRoot()
  {
    return parent == null;
  }
  
  private boolean isCommitted()
  {
    return tail == null;
  }
  
  public Document getDocument()
  {
    return document;
  }
  
  boolean isBlocked()
  {
    return (blocked) && (!isCommitted());
  }
  
  public void block()
  {
    blocked = true;
  }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
    throws Throwable
  {
    if ((paramMethod.getDeclaringClass() == TypedXmlWriter.class) || (paramMethod.getDeclaringClass() == Object.class)) {
      try
      {
        return paramMethod.invoke(this, paramArrayOfObject);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throw localInvocationTargetException.getTargetException();
      }
    }
    XmlAttribute localXmlAttribute = (XmlAttribute)paramMethod.getAnnotation(XmlAttribute.class);
    XmlValue localXmlValue = (XmlValue)paramMethod.getAnnotation(XmlValue.class);
    XmlElement localXmlElement = (XmlElement)paramMethod.getAnnotation(XmlElement.class);
    if (localXmlAttribute != null)
    {
      if ((localXmlValue != null) || (localXmlElement != null)) {
        throw new IllegalAnnotationException(paramMethod.toString());
      }
      addAttribute(localXmlAttribute, paramMethod, paramArrayOfObject);
      return paramObject;
    }
    if (localXmlValue != null)
    {
      if (localXmlElement != null) {
        throw new IllegalAnnotationException(paramMethod.toString());
      }
      _pcdata(paramArrayOfObject);
      return paramObject;
    }
    return addElement(localXmlElement, paramMethod, paramArrayOfObject);
  }
  
  private void addAttribute(XmlAttribute paramXmlAttribute, Method paramMethod, Object[] paramArrayOfObject)
  {
    assert (paramXmlAttribute != null);
    checkStartTag();
    String str = paramXmlAttribute.value();
    if (paramXmlAttribute.value().length() == 0) {
      str = paramMethod.getName();
    }
    _attribute(paramXmlAttribute.ns(), str, paramArrayOfObject);
  }
  
  private void checkStartTag()
  {
    if (startTag == null) {
      throw new IllegalStateException("start tag has already been written");
    }
  }
  
  private Object addElement(XmlElement paramXmlElement, Method paramMethod, Object[] paramArrayOfObject)
  {
    Class localClass1 = paramMethod.getReturnType();
    String str1 = "##default";
    String str2 = paramMethod.getName();
    if (paramXmlElement != null)
    {
      if (paramXmlElement.value().length() != 0) {
        str2 = paramXmlElement.value();
      }
      str1 = paramXmlElement.ns();
    }
    Object localObject1;
    if (str1.equals("##default"))
    {
      Class localClass2 = paramMethod.getDeclaringClass();
      localObject1 = (XmlElement)localClass2.getAnnotation(XmlElement.class);
      if (localObject1 != null) {
        str1 = ((XmlElement)localObject1).ns();
      }
      if (str1.equals("##default")) {
        str1 = getNamespace(localClass2.getPackage());
      }
    }
    if (localClass1 == Void.TYPE)
    {
      int i = paramMethod.getAnnotation(XmlCDATA.class) != null ? 1 : 0;
      localObject1 = new StartTag(document, str1, str2);
      addChild((Content)localObject1);
      for (Object localObject2 : paramArrayOfObject)
      {
        Object localObject3;
        if (i != 0) {
          localObject3 = new Cdata(document, (NamespaceResolver)localObject1, localObject2);
        } else {
          localObject3 = new Pcdata(document, (NamespaceResolver)localObject1, localObject2);
        }
        addChild((Content)localObject3);
      }
      addChild(new EndTag());
      return null;
    }
    if (TypedXmlWriter.class.isAssignableFrom(localClass1)) {
      return _element(str1, str2, localClass1);
    }
    throw new IllegalSignatureException("Illegal return type: " + localClass1);
  }
  
  private String getNamespace(Package paramPackage)
  {
    if (paramPackage == null) {
      return "";
    }
    XmlNamespace localXmlNamespace = (XmlNamespace)paramPackage.getAnnotation(XmlNamespace.class);
    String str;
    if (localXmlNamespace != null) {
      str = localXmlNamespace.value();
    } else {
      str = "";
    }
    return str;
  }
  
  private void addChild(Content paramContent)
  {
    tail.setNext(document, paramContent);
    tail = paramContent;
  }
  
  public void commit()
  {
    commit(true);
  }
  
  public void commit(boolean paramBoolean)
  {
    _commit(paramBoolean);
    document.flush();
  }
  
  private void _commit(boolean paramBoolean)
  {
    if (isCommitted()) {
      return;
    }
    addChild(endTag);
    if (isRoot()) {
      addChild(new EndDocument());
    }
    tail = null;
    if (paramBoolean) {
      for (ContainerElement localContainerElement = this; localContainerElement != null; localContainerElement = parent) {
        while (prevOpen != null) {
          prevOpen._commit(false);
        }
      }
    }
    while (lastOpenChild != null) {
      lastOpenChild._commit(false);
    }
    if (parent != null)
    {
      if (parent.lastOpenChild == this)
      {
        assert (nextOpen == null) : "this must be the last one";
        parent.lastOpenChild = prevOpen;
      }
      else
      {
        assert (nextOpen.prevOpen == this);
        nextOpen.prevOpen = prevOpen;
      }
      if (prevOpen != null)
      {
        assert (prevOpen.nextOpen == this);
        prevOpen.nextOpen = nextOpen;
      }
    }
    nextOpen = null;
    prevOpen = null;
  }
  
  public void _attribute(String paramString, Object paramObject)
  {
    _attribute("", paramString, paramObject);
  }
  
  public void _attribute(String paramString1, String paramString2, Object paramObject)
  {
    checkStartTag();
    startTag.addAttribute(paramString1, paramString2, paramObject);
  }
  
  public void _attribute(QName paramQName, Object paramObject)
  {
    _attribute(paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramObject);
  }
  
  public void _namespace(String paramString)
  {
    _namespace(paramString, false);
  }
  
  public void _namespace(String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      throw new IllegalArgumentException();
    }
    checkStartTag();
    startTag.addNamespaceDecl(paramString1, paramString2, false);
  }
  
  public void _namespace(String paramString, boolean paramBoolean)
  {
    checkStartTag();
    startTag.addNamespaceDecl(paramString, null, paramBoolean);
  }
  
  public void _pcdata(Object paramObject)
  {
    addChild(new Pcdata(document, startTag, paramObject));
  }
  
  public void _cdata(Object paramObject)
  {
    addChild(new Cdata(document, startTag, paramObject));
  }
  
  public void _comment(Object paramObject)
    throws UnsupportedOperationException
  {
    addChild(new Comment(document, startTag, paramObject));
  }
  
  public <T extends TypedXmlWriter> T _element(String paramString, Class<T> paramClass)
  {
    return _element(nsUri, paramString, paramClass);
  }
  
  public <T extends TypedXmlWriter> T _element(QName paramQName, Class<T> paramClass)
  {
    return _element(paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramClass);
  }
  
  public <T extends TypedXmlWriter> T _element(Class<T> paramClass)
  {
    return _element(TXW.getTagName(paramClass), paramClass);
  }
  
  public <T extends TypedXmlWriter> T _cast(Class<T> paramClass)
  {
    return (TypedXmlWriter)paramClass.cast(Proxy.newProxyInstance(paramClass.getClassLoader(), new Class[] { paramClass }, this));
  }
  
  public <T extends TypedXmlWriter> T _element(String paramString1, String paramString2, Class<T> paramClass)
  {
    ContainerElement localContainerElement = new ContainerElement(document, this, paramString1, paramString2);
    addChild(startTag);
    tail = endTag;
    if (lastOpenChild != null)
    {
      assert (lastOpenChild.parent == this);
      assert (prevOpen == null);
      assert (nextOpen == null);
      prevOpen = lastOpenChild;
      assert (lastOpenChild.nextOpen == null);
      lastOpenChild.nextOpen = localContainerElement;
    }
    lastOpenChild = localContainerElement;
    return localContainerElement._cast(paramClass);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\ContainerElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */