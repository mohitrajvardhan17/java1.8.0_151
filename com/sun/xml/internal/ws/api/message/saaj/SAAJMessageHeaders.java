package com.sun.xml.internal.ws.api.message.saaj;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.message.saaj.SAAJHeader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

public class SAAJMessageHeaders
  implements MessageHeaders
{
  SOAPMessage sm;
  Map<SOAPHeaderElement, Header> nonSAAJHeaders;
  Map<QName, Integer> notUnderstoodCount;
  SOAPVersion soapVersion;
  private Set<QName> understoodHeaders;
  
  public SAAJMessageHeaders(SOAPMessage paramSOAPMessage, SOAPVersion paramSOAPVersion)
  {
    sm = paramSOAPMessage;
    soapVersion = paramSOAPVersion;
    initHeaderUnderstanding();
  }
  
  private void initHeaderUnderstanding()
  {
    SOAPHeader localSOAPHeader = ensureSOAPHeader();
    if (localSOAPHeader == null) {
      return;
    }
    Iterator localIterator = localSOAPHeader.examineAllHeaderElements();
    while (localIterator.hasNext())
    {
      SOAPHeaderElement localSOAPHeaderElement = (SOAPHeaderElement)localIterator.next();
      if (localSOAPHeaderElement != null) {
        if (localSOAPHeaderElement.getMustUnderstand()) {
          notUnderstood(localSOAPHeaderElement.getElementQName());
        }
      }
    }
  }
  
  public void understood(Header paramHeader)
  {
    understood(paramHeader.getNamespaceURI(), paramHeader.getLocalPart());
  }
  
  public void understood(String paramString1, String paramString2)
  {
    understood(new QName(paramString1, paramString2));
  }
  
  public void understood(QName paramQName)
  {
    if (notUnderstoodCount == null) {
      notUnderstoodCount = new HashMap();
    }
    Integer localInteger = (Integer)notUnderstoodCount.get(paramQName);
    if ((localInteger != null) && (localInteger.intValue() > 0))
    {
      localInteger = Integer.valueOf(localInteger.intValue() - 1);
      if (localInteger.intValue() <= 0) {
        notUnderstoodCount.remove(paramQName);
      } else {
        notUnderstoodCount.put(paramQName, localInteger);
      }
    }
    if (understoodHeaders == null) {
      understoodHeaders = new HashSet();
    }
    understoodHeaders.add(paramQName);
  }
  
  public boolean isUnderstood(Header paramHeader)
  {
    return isUnderstood(paramHeader.getNamespaceURI(), paramHeader.getLocalPart());
  }
  
  public boolean isUnderstood(String paramString1, String paramString2)
  {
    return isUnderstood(new QName(paramString1, paramString2));
  }
  
  public boolean isUnderstood(QName paramQName)
  {
    if (understoodHeaders == null) {
      return false;
    }
    return understoodHeaders.contains(paramQName);
  }
  
  public boolean isUnderstood(int paramInt)
  {
    return false;
  }
  
  public Header get(String paramString1, String paramString2, boolean paramBoolean)
  {
    SOAPHeaderElement localSOAPHeaderElement = find(paramString1, paramString2);
    if (localSOAPHeaderElement != null)
    {
      if (paramBoolean) {
        understood(paramString1, paramString2);
      }
      return new SAAJHeader(localSOAPHeaderElement);
    }
    return null;
  }
  
  public Header get(QName paramQName, boolean paramBoolean)
  {
    return get(paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramBoolean);
  }
  
  public Iterator<Header> getHeaders(QName paramQName, boolean paramBoolean)
  {
    return getHeaders(paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramBoolean);
  }
  
  public Iterator<Header> getHeaders(String paramString1, String paramString2, boolean paramBoolean)
  {
    SOAPHeader localSOAPHeader = ensureSOAPHeader();
    if (localSOAPHeader == null) {
      return null;
    }
    Iterator localIterator = localSOAPHeader.examineAllHeaderElements();
    if (paramBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      while (localIterator.hasNext())
      {
        SOAPHeaderElement localSOAPHeaderElement = (SOAPHeaderElement)localIterator.next();
        if ((localSOAPHeaderElement != null) && (localSOAPHeaderElement.getNamespaceURI().equals(paramString1)) && ((paramString2 == null) || (localSOAPHeaderElement.getLocalName().equals(paramString2))))
        {
          understood(localSOAPHeaderElement.getNamespaceURI(), localSOAPHeaderElement.getLocalName());
          localArrayList.add(new SAAJHeader(localSOAPHeaderElement));
        }
      }
      return localArrayList.iterator();
    }
    return new HeaderReadIterator(localIterator, paramString1, paramString2);
  }
  
  public Iterator<Header> getHeaders(String paramString, boolean paramBoolean)
  {
    return getHeaders(paramString, null, paramBoolean);
  }
  
  public boolean add(Header paramHeader)
  {
    try
    {
      paramHeader.writeTo(sm);
    }
    catch (SOAPException localSOAPException)
    {
      return false;
    }
    notUnderstood(new QName(paramHeader.getNamespaceURI(), paramHeader.getLocalPart()));
    if (isNonSAAJHeader(paramHeader)) {
      addNonSAAJHeader(find(paramHeader.getNamespaceURI(), paramHeader.getLocalPart()), paramHeader);
    }
    return true;
  }
  
  public Header remove(QName paramQName)
  {
    return remove(paramQName.getNamespaceURI(), paramQName.getLocalPart());
  }
  
  public Header remove(String paramString1, String paramString2)
  {
    SOAPHeader localSOAPHeader = ensureSOAPHeader();
    if (localSOAPHeader == null) {
      return null;
    }
    SOAPHeaderElement localSOAPHeaderElement = find(paramString1, paramString2);
    if (localSOAPHeaderElement == null) {
      return null;
    }
    localSOAPHeaderElement = (SOAPHeaderElement)localSOAPHeader.removeChild(localSOAPHeaderElement);
    removeNonSAAJHeader(localSOAPHeaderElement);
    QName localQName = paramString1 == null ? new QName(paramString2) : new QName(paramString1, paramString2);
    if (understoodHeaders != null) {
      understoodHeaders.remove(localQName);
    }
    removeNotUnderstood(localQName);
    return new SAAJHeader(localSOAPHeaderElement);
  }
  
  private void removeNotUnderstood(QName paramQName)
  {
    if (notUnderstoodCount == null) {
      return;
    }
    Integer localInteger = (Integer)notUnderstoodCount.get(paramQName);
    if (localInteger != null)
    {
      int i = localInteger.intValue();
      i--;
      if (i <= 0) {
        notUnderstoodCount.remove(paramQName);
      }
    }
  }
  
  private SOAPHeaderElement find(QName paramQName)
  {
    return find(paramQName.getNamespaceURI(), paramQName.getLocalPart());
  }
  
  private SOAPHeaderElement find(String paramString1, String paramString2)
  {
    SOAPHeader localSOAPHeader = ensureSOAPHeader();
    if (localSOAPHeader == null) {
      return null;
    }
    Iterator localIterator = localSOAPHeader.examineAllHeaderElements();
    while (localIterator.hasNext())
    {
      SOAPHeaderElement localSOAPHeaderElement = (SOAPHeaderElement)localIterator.next();
      if ((localSOAPHeaderElement.getNamespaceURI().equals(paramString1)) && (localSOAPHeaderElement.getLocalName().equals(paramString2))) {
        return localSOAPHeaderElement;
      }
    }
    return null;
  }
  
  private void notUnderstood(QName paramQName)
  {
    if (notUnderstoodCount == null) {
      notUnderstoodCount = new HashMap();
    }
    Integer localInteger = (Integer)notUnderstoodCount.get(paramQName);
    if (localInteger == null) {
      notUnderstoodCount.put(paramQName, Integer.valueOf(1));
    } else {
      notUnderstoodCount.put(paramQName, Integer.valueOf(localInteger.intValue() + 1));
    }
    if (understoodHeaders != null) {
      understoodHeaders.remove(paramQName);
    }
  }
  
  private SOAPHeader ensureSOAPHeader()
  {
    try
    {
      SOAPHeader localSOAPHeader = sm.getSOAPPart().getEnvelope().getHeader();
      if (localSOAPHeader != null) {
        return localSOAPHeader;
      }
      return sm.getSOAPPart().getEnvelope().addHeader();
    }
    catch (Exception localException) {}
    return null;
  }
  
  private boolean isNonSAAJHeader(Header paramHeader)
  {
    return !(paramHeader instanceof SAAJHeader);
  }
  
  private void addNonSAAJHeader(SOAPHeaderElement paramSOAPHeaderElement, Header paramHeader)
  {
    if (nonSAAJHeaders == null) {
      nonSAAJHeaders = new HashMap();
    }
    nonSAAJHeaders.put(paramSOAPHeaderElement, paramHeader);
  }
  
  private void removeNonSAAJHeader(SOAPHeaderElement paramSOAPHeaderElement)
  {
    if (nonSAAJHeaders != null) {
      nonSAAJHeaders.remove(paramSOAPHeaderElement);
    }
  }
  
  public boolean addOrReplace(Header paramHeader)
  {
    remove(paramHeader.getNamespaceURI(), paramHeader.getLocalPart());
    return add(paramHeader);
  }
  
  public void replace(Header paramHeader1, Header paramHeader2)
  {
    if (remove(paramHeader1.getNamespaceURI(), paramHeader1.getLocalPart()) == null) {
      throw new IllegalArgumentException();
    }
    add(paramHeader2);
  }
  
  public Set<QName> getUnderstoodHeaders()
  {
    return understoodHeaders;
  }
  
  public Set<QName> getNotUnderstoodHeaders(Set<String> paramSet, Set<QName> paramSet1, WSBinding paramWSBinding)
  {
    HashSet localHashSet = new HashSet();
    if (notUnderstoodCount == null) {
      return localHashSet;
    }
    Iterator localIterator = notUnderstoodCount.keySet().iterator();
    while (localIterator.hasNext())
    {
      QName localQName = (QName)localIterator.next();
      int i = ((Integer)notUnderstoodCount.get(localQName)).intValue();
      if (i > 0)
      {
        SOAPHeaderElement localSOAPHeaderElement = find(localQName);
        if (localSOAPHeaderElement.getMustUnderstand())
        {
          SAAJHeader localSAAJHeader = new SAAJHeader(localSOAPHeaderElement);
          boolean bool = false;
          if (paramSet != null) {
            bool = !paramSet.contains(localSAAJHeader.getRole(soapVersion));
          }
          if (!bool)
          {
            if ((paramWSBinding != null) && ((paramWSBinding instanceof SOAPBindingImpl)))
            {
              bool = ((SOAPBindingImpl)paramWSBinding).understandsHeader(localQName);
              if ((!bool) && (paramSet1 != null) && (paramSet1.contains(localQName))) {
                bool = true;
              }
            }
            if (!bool) {
              localHashSet.add(localQName);
            }
          }
        }
      }
    }
    return localHashSet;
  }
  
  public Iterator<Header> getHeaders()
  {
    SOAPHeader localSOAPHeader = ensureSOAPHeader();
    if (localSOAPHeader == null) {
      return null;
    }
    Iterator localIterator = localSOAPHeader.examineAllHeaderElements();
    return new HeaderReadIterator(localIterator, null, null);
  }
  
  public boolean hasHeaders()
  {
    SOAPHeader localSOAPHeader = ensureSOAPHeader();
    if (localSOAPHeader == null) {
      return false;
    }
    Iterator localIterator = localSOAPHeader.examineAllHeaderElements();
    return localIterator.hasNext();
  }
  
  public List<Header> asList()
  {
    SOAPHeader localSOAPHeader = ensureSOAPHeader();
    if (localSOAPHeader == null) {
      return Collections.emptyList();
    }
    Iterator localIterator = localSOAPHeader.examineAllHeaderElements();
    ArrayList localArrayList = new ArrayList();
    while (localIterator.hasNext())
    {
      SOAPHeaderElement localSOAPHeaderElement = (SOAPHeaderElement)localIterator.next();
      localArrayList.add(new SAAJHeader(localSOAPHeaderElement));
    }
    return localArrayList;
  }
  
  private static class HeaderReadIterator
    implements Iterator<Header>
  {
    SOAPHeaderElement current;
    Iterator soapHeaders;
    String myNsUri;
    String myLocalName;
    
    public HeaderReadIterator(Iterator paramIterator, String paramString1, String paramString2)
    {
      soapHeaders = paramIterator;
      myNsUri = paramString1;
      myLocalName = paramString2;
    }
    
    public boolean hasNext()
    {
      if (current == null) {
        advance();
      }
      return current != null;
    }
    
    public Header next()
    {
      if (!hasNext()) {
        return null;
      }
      if (current == null) {
        return null;
      }
      SAAJHeader localSAAJHeader = new SAAJHeader(current);
      current = null;
      return localSAAJHeader;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
    
    private void advance()
    {
      while (soapHeaders.hasNext())
      {
        SOAPHeaderElement localSOAPHeaderElement = (SOAPHeaderElement)soapHeaders.next();
        if ((localSOAPHeaderElement != null) && ((myNsUri == null) || (localSOAPHeaderElement.getNamespaceURI().equals(myNsUri))) && ((myLocalName == null) || (localSOAPHeaderElement.getLocalName().equals(myLocalName))))
        {
          current = localSOAPHeaderElement;
          return;
        }
      }
      current = null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\message\saaj\SAAJMessageHeaders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */