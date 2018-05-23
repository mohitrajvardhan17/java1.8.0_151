package com.sun.xml.internal.txw2;

import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.annotation.XmlNamespace;
import com.sun.xml.internal.txw2.output.TXWSerializer;
import com.sun.xml.internal.txw2.output.XmlSerializer;
import javax.xml.namespace.QName;

public abstract class TXW
{
  private TXW() {}
  
  static QName getTagName(Class<?> paramClass)
  {
    String str1 = "";
    String str2 = "##default";
    XmlElement localXmlElement = (XmlElement)paramClass.getAnnotation(XmlElement.class);
    if (localXmlElement != null)
    {
      str1 = localXmlElement.value();
      str2 = localXmlElement.ns();
    }
    if (str1.length() == 0)
    {
      str1 = paramClass.getName();
      int i = str1.lastIndexOf('.');
      if (i >= 0) {
        str1 = str1.substring(i + 1);
      }
      str1 = Character.toLowerCase(str1.charAt(0)) + str1.substring(1);
    }
    if (str2.equals("##default"))
    {
      Package localPackage = paramClass.getPackage();
      if (localPackage != null)
      {
        XmlNamespace localXmlNamespace = (XmlNamespace)localPackage.getAnnotation(XmlNamespace.class);
        if (localXmlNamespace != null) {
          str2 = localXmlNamespace.value();
        }
      }
    }
    if (str2.equals("##default")) {
      str2 = "";
    }
    return new QName(str2, str1);
  }
  
  public static <T extends TypedXmlWriter> T create(Class<T> paramClass, XmlSerializer paramXmlSerializer)
  {
    if ((paramXmlSerializer instanceof TXWSerializer))
    {
      localObject = (TXWSerializer)paramXmlSerializer;
      return txw._element(paramClass);
    }
    Object localObject = new Document(paramXmlSerializer);
    QName localQName = getTagName(paramClass);
    return new ContainerElement((Document)localObject, null, localQName.getNamespaceURI(), localQName.getLocalPart())._cast(paramClass);
  }
  
  public static <T extends TypedXmlWriter> T create(QName paramQName, Class<T> paramClass, XmlSerializer paramXmlSerializer)
  {
    if ((paramXmlSerializer instanceof TXWSerializer))
    {
      TXWSerializer localTXWSerializer = (TXWSerializer)paramXmlSerializer;
      return txw._element(paramQName, paramClass);
    }
    return new ContainerElement(new Document(paramXmlSerializer), null, paramQName.getNamespaceURI(), paramQName.getLocalPart())._cast(paramClass);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\TXW.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */