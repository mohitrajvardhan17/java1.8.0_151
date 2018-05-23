package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Element;

public abstract class DetailImpl
  extends FaultElementImpl
  implements Detail
{
  public DetailImpl(SOAPDocumentImpl paramSOAPDocumentImpl, NameImpl paramNameImpl)
  {
    super(paramSOAPDocumentImpl, paramNameImpl);
  }
  
  protected abstract DetailEntry createDetailEntry(Name paramName);
  
  protected abstract DetailEntry createDetailEntry(QName paramQName);
  
  public DetailEntry addDetailEntry(Name paramName)
    throws SOAPException
  {
    DetailEntry localDetailEntry = createDetailEntry(paramName);
    addNode(localDetailEntry);
    return localDetailEntry;
  }
  
  public DetailEntry addDetailEntry(QName paramQName)
    throws SOAPException
  {
    DetailEntry localDetailEntry = createDetailEntry(paramQName);
    addNode(localDetailEntry);
    return localDetailEntry;
  }
  
  protected SOAPElement addElement(Name paramName)
    throws SOAPException
  {
    return addDetailEntry(paramName);
  }
  
  protected SOAPElement addElement(QName paramQName)
    throws SOAPException
  {
    return addDetailEntry(paramQName);
  }
  
  protected SOAPElement convertToSoapElement(Element paramElement)
  {
    if ((paramElement instanceof DetailEntry)) {
      return (SOAPElement)paramElement;
    }
    DetailEntry localDetailEntry = createDetailEntry(NameImpl.copyElementName(paramElement));
    return replaceElementWithSOAPElement(paramElement, (ElementImpl)localDetailEntry);
  }
  
  public Iterator getDetailEntries()
  {
    new Iterator()
    {
      Iterator eachNode = getChildElementNodes();
      SOAPElement next = null;
      SOAPElement last = null;
      
      public boolean hasNext()
      {
        if (next == null) {
          while (eachNode.hasNext())
          {
            next = ((SOAPElement)eachNode.next());
            if ((next instanceof DetailEntry)) {
              break;
            }
            next = null;
          }
        }
        return next != null;
      }
      
      public Object next()
      {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        last = next;
        next = null;
        return last;
      }
      
      public void remove()
      {
        if (last == null) {
          throw new IllegalStateException();
        }
        SOAPElement localSOAPElement = last;
        removeChild(localSOAPElement);
        last = null;
      }
    };
  }
  
  protected boolean isStandardFaultElement()
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\impl\DetailImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */