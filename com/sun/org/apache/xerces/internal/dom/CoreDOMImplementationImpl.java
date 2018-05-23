package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.RevalidationHandler;
import com.sun.org.apache.xerces.internal.parsers.DOMParserImpl;
import com.sun.org.apache.xerces.internal.parsers.DTDConfiguration;
import com.sun.org.apache.xerces.internal.parsers.XIncludeAwareParserConfiguration;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xml.internal.serialize.DOMSerializerImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSSerializer;

public class CoreDOMImplementationImpl
  implements DOMImplementation, DOMImplementationLS
{
  private static final int SIZE = 2;
  private RevalidationHandler[] validators = new RevalidationHandler[2];
  private RevalidationHandler[] dtdValidators = new RevalidationHandler[2];
  private int freeValidatorIndex = -1;
  private int freeDTDValidatorIndex = -1;
  private int currentSize = 2;
  private int docAndDoctypeCounter = 0;
  static CoreDOMImplementationImpl singleton = new CoreDOMImplementationImpl();
  
  public CoreDOMImplementationImpl() {}
  
  public static DOMImplementation getDOMImplementation()
  {
    return singleton;
  }
  
  public boolean hasFeature(String paramString1, String paramString2)
  {
    int i = (paramString2 == null) || (paramString2.length() == 0) ? 1 : 0;
    if ((paramString1.equalsIgnoreCase("+XPath")) && ((i != 0) || (paramString2.equals("3.0"))))
    {
      try
      {
        Class localClass = ObjectFactory.findProviderClass("com.sun.org.apache.xpath.internal.domapi.XPathEvaluatorImpl", true);
        Class[] arrayOfClass = localClass.getInterfaces();
        for (int j = 0; j < arrayOfClass.length; j++) {
          if (arrayOfClass[j].getName().equals("org.w3c.dom.xpath.XPathEvaluator")) {
            return true;
          }
        }
      }
      catch (Exception localException)
      {
        return false;
      }
      return true;
    }
    if (paramString1.startsWith("+")) {
      paramString1 = paramString1.substring(1);
    }
    return ((paramString1.equalsIgnoreCase("Core")) && ((i != 0) || (paramString2.equals("1.0")) || (paramString2.equals("2.0")) || (paramString2.equals("3.0")))) || ((paramString1.equalsIgnoreCase("XML")) && ((i != 0) || (paramString2.equals("1.0")) || (paramString2.equals("2.0")) || (paramString2.equals("3.0")))) || ((paramString1.equalsIgnoreCase("LS")) && ((i != 0) || (paramString2.equals("3.0"))));
  }
  
  public DocumentType createDocumentType(String paramString1, String paramString2, String paramString3)
  {
    checkQName(paramString1);
    return new DocumentTypeImpl(null, paramString1, paramString2, paramString3);
  }
  
  final void checkQName(String paramString)
  {
    int i = paramString.indexOf(':');
    int j = paramString.lastIndexOf(':');
    int k = paramString.length();
    if ((i == 0) || (i == k - 1) || (j != i))
    {
      String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
      throw new DOMException((short)14, str1);
    }
    int m = 0;
    String str4;
    if (i > 0)
    {
      if (!XMLChar.isNCNameStart(paramString.charAt(m)))
      {
        String str2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
        throw new DOMException((short)5, str2);
      }
      for (int n = 1; n < i; n++) {
        if (!XMLChar.isNCName(paramString.charAt(n)))
        {
          str4 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
          throw new DOMException((short)5, str4);
        }
      }
      m = i + 1;
    }
    if (!XMLChar.isNCNameStart(paramString.charAt(m)))
    {
      String str3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, str3);
    }
    for (int i1 = m + 1; i1 < k; i1++) {
      if (!XMLChar.isNCName(paramString.charAt(i1)))
      {
        str4 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
        throw new DOMException((short)5, str4);
      }
    }
  }
  
  public Document createDocument(String paramString1, String paramString2, DocumentType paramDocumentType)
    throws DOMException
  {
    if ((paramDocumentType != null) && (paramDocumentType.getOwnerDocument() != null))
    {
      localObject = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
      throw new DOMException((short)4, (String)localObject);
    }
    Object localObject = new CoreDocumentImpl(paramDocumentType);
    Element localElement = ((CoreDocumentImpl)localObject).createElementNS(paramString1, paramString2);
    ((CoreDocumentImpl)localObject).appendChild(localElement);
    return (Document)localObject;
  }
  
  public Object getFeature(String paramString1, String paramString2)
  {
    if (singleton.hasFeature(paramString1, paramString2))
    {
      if (paramString1.equalsIgnoreCase("+XPath")) {
        try
        {
          Class localClass = ObjectFactory.findProviderClass("com.sun.org.apache.xpath.internal.domapi.XPathEvaluatorImpl", true);
          Class[] arrayOfClass = localClass.getInterfaces();
          for (int i = 0; i < arrayOfClass.length; i++) {
            if (arrayOfClass[i].getName().equals("org.w3c.dom.xpath.XPathEvaluator")) {
              return localClass.newInstance();
            }
          }
        }
        catch (Exception localException)
        {
          return null;
        }
      }
      return singleton;
    }
    return null;
  }
  
  public LSParser createLSParser(short paramShort, String paramString)
    throws DOMException
  {
    if ((paramShort != 1) || ((paramString != null) && (!"http://www.w3.org/2001/XMLSchema".equals(paramString)) && (!"http://www.w3.org/TR/REC-xml".equals(paramString))))
    {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
      throw new DOMException((short)9, str);
    }
    if ((paramString != null) && (paramString.equals("http://www.w3.org/TR/REC-xml"))) {
      return new DOMParserImpl(new DTDConfiguration(), paramString);
    }
    return new DOMParserImpl(new XIncludeAwareParserConfiguration(), paramString);
  }
  
  public LSSerializer createLSSerializer()
  {
    return new DOMSerializerImpl();
  }
  
  public LSInput createLSInput()
  {
    return new DOMInputImpl();
  }
  
  synchronized RevalidationHandler getValidator(String paramString)
  {
    RevalidationHandler localRevalidationHandler;
    if (paramString == "http://www.w3.org/2001/XMLSchema")
    {
      if (freeValidatorIndex < 0) {
        return (RevalidationHandler)ObjectFactory.newInstance("com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator", ObjectFactory.findClassLoader(), true);
      }
      localRevalidationHandler = validators[freeValidatorIndex];
      validators[(freeValidatorIndex--)] = null;
      return localRevalidationHandler;
    }
    if (paramString == "http://www.w3.org/TR/REC-xml")
    {
      if (freeDTDValidatorIndex < 0) {
        return (RevalidationHandler)ObjectFactory.newInstance("com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidator", ObjectFactory.findClassLoader(), true);
      }
      localRevalidationHandler = dtdValidators[freeDTDValidatorIndex];
      dtdValidators[(freeDTDValidatorIndex--)] = null;
      return localRevalidationHandler;
    }
    return null;
  }
  
  synchronized void releaseValidator(String paramString, RevalidationHandler paramRevalidationHandler)
  {
    RevalidationHandler[] arrayOfRevalidationHandler;
    if (paramString == "http://www.w3.org/2001/XMLSchema")
    {
      freeValidatorIndex += 1;
      if (validators.length == freeValidatorIndex)
      {
        currentSize += 2;
        arrayOfRevalidationHandler = new RevalidationHandler[currentSize];
        System.arraycopy(validators, 0, arrayOfRevalidationHandler, 0, validators.length);
        validators = arrayOfRevalidationHandler;
      }
      validators[freeValidatorIndex] = paramRevalidationHandler;
    }
    else if (paramString == "http://www.w3.org/TR/REC-xml")
    {
      freeDTDValidatorIndex += 1;
      if (dtdValidators.length == freeDTDValidatorIndex)
      {
        currentSize += 2;
        arrayOfRevalidationHandler = new RevalidationHandler[currentSize];
        System.arraycopy(dtdValidators, 0, arrayOfRevalidationHandler, 0, dtdValidators.length);
        dtdValidators = arrayOfRevalidationHandler;
      }
      dtdValidators[freeDTDValidatorIndex] = paramRevalidationHandler;
    }
  }
  
  protected synchronized int assignDocumentNumber()
  {
    return ++docAndDoctypeCounter;
  }
  
  protected synchronized int assignDocTypeNumber()
  {
    return ++docAndDoctypeCounter;
  }
  
  public LSOutput createLSOutput()
  {
    return new DOMOutputImpl();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\CoreDOMImplementationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */