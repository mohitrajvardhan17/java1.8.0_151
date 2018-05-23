package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.namespace.QName;

public class HeaderList
  extends ArrayList<Header>
  implements MessageHeaders
{
  private static final long serialVersionUID = -6358045781349627237L;
  private int understoodBits;
  private BitSet moreUnderstoodBits = null;
  private SOAPVersion soapVersion;
  
  @Deprecated
  public HeaderList() {}
  
  public HeaderList(SOAPVersion paramSOAPVersion)
  {
    soapVersion = paramSOAPVersion;
  }
  
  public HeaderList(HeaderList paramHeaderList)
  {
    super(paramHeaderList);
    understoodBits = understoodBits;
    if (moreUnderstoodBits != null) {
      moreUnderstoodBits = ((BitSet)moreUnderstoodBits.clone());
    }
  }
  
  public HeaderList(MessageHeaders paramMessageHeaders)
  {
    super(paramMessageHeaders.asList());
    Object localObject;
    if ((paramMessageHeaders instanceof HeaderList))
    {
      localObject = (HeaderList)paramMessageHeaders;
      understoodBits = understoodBits;
      if (moreUnderstoodBits != null) {
        moreUnderstoodBits = ((BitSet)moreUnderstoodBits.clone());
      }
    }
    else
    {
      localObject = paramMessageHeaders.getUnderstoodHeaders();
      if (localObject != null)
      {
        Iterator localIterator = ((Set)localObject).iterator();
        while (localIterator.hasNext())
        {
          QName localQName = (QName)localIterator.next();
          understood(localQName);
        }
      }
    }
  }
  
  public int size()
  {
    return super.size();
  }
  
  public boolean hasHeaders()
  {
    return !isEmpty();
  }
  
  @Deprecated
  public void addAll(Header... paramVarArgs)
  {
    addAll(Arrays.asList(paramVarArgs));
  }
  
  public Header get(int paramInt)
  {
    return (Header)super.get(paramInt);
  }
  
  public void understood(int paramInt)
  {
    if (paramInt >= size()) {
      throw new ArrayIndexOutOfBoundsException(paramInt);
    }
    if (paramInt < 32)
    {
      understoodBits |= 1 << paramInt;
    }
    else
    {
      if (moreUnderstoodBits == null) {
        moreUnderstoodBits = new BitSet();
      }
      moreUnderstoodBits.set(paramInt - 32);
    }
  }
  
  public boolean isUnderstood(int paramInt)
  {
    if (paramInt >= size()) {
      throw new ArrayIndexOutOfBoundsException(paramInt);
    }
    if (paramInt < 32) {
      return understoodBits == (understoodBits | 1 << paramInt);
    }
    if (moreUnderstoodBits == null) {
      return false;
    }
    return moreUnderstoodBits.get(paramInt - 32);
  }
  
  /**
   * @deprecated
   */
  public void understood(@NotNull Header paramHeader)
  {
    int i = size();
    for (int j = 0; j < i; j++) {
      if (get(j) == paramHeader)
      {
        understood(j);
        return;
      }
    }
    throw new IllegalArgumentException();
  }
  
  @Nullable
  public Header get(@NotNull String paramString1, @NotNull String paramString2, boolean paramBoolean)
  {
    int i = size();
    for (int j = 0; j < i; j++)
    {
      Header localHeader = get(j);
      if ((localHeader.getLocalPart().equals(paramString2)) && (localHeader.getNamespaceURI().equals(paramString1)))
      {
        if (paramBoolean) {
          understood(j);
        }
        return localHeader;
      }
    }
    return null;
  }
  
  /**
   * @deprecated
   */
  public Header get(String paramString1, String paramString2)
  {
    return get(paramString1, paramString2, true);
  }
  
  @Nullable
  public Header get(@NotNull QName paramQName, boolean paramBoolean)
  {
    return get(paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramBoolean);
  }
  
  /**
   * @deprecated
   */
  @Nullable
  public Header get(@NotNull QName paramQName)
  {
    return get(paramQName, true);
  }
  
  /**
   * @deprecated
   */
  public Iterator<Header> getHeaders(String paramString1, String paramString2)
  {
    return getHeaders(paramString1, paramString2, true);
  }
  
  @NotNull
  public Iterator<Header> getHeaders(@NotNull final String paramString1, @NotNull final String paramString2, final boolean paramBoolean)
  {
    new Iterator()
    {
      int idx = 0;
      Header next;
      
      public boolean hasNext()
      {
        if (next == null) {
          fetch();
        }
        return next != null;
      }
      
      public Header next()
      {
        if (next == null)
        {
          fetch();
          if (next == null) {
            throw new NoSuchElementException();
          }
        }
        if (paramBoolean)
        {
          assert (get(idx - 1) == next);
          understood(idx - 1);
        }
        Header localHeader = next;
        next = null;
        return localHeader;
      }
      
      private void fetch()
      {
        while (idx < size())
        {
          Header localHeader = get(idx++);
          if ((localHeader.getLocalPart().equals(paramString2)) && (localHeader.getNamespaceURI().equals(paramString1)))
          {
            next = localHeader;
            break;
          }
        }
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
  
  @NotNull
  public Iterator<Header> getHeaders(@NotNull QName paramQName, boolean paramBoolean)
  {
    return getHeaders(paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramBoolean);
  }
  
  /**
   * @deprecated
   */
  @NotNull
  public Iterator<Header> getHeaders(@NotNull String paramString)
  {
    return getHeaders(paramString, true);
  }
  
  @NotNull
  public Iterator<Header> getHeaders(@NotNull final String paramString, final boolean paramBoolean)
  {
    new Iterator()
    {
      int idx = 0;
      Header next;
      
      public boolean hasNext()
      {
        if (next == null) {
          fetch();
        }
        return next != null;
      }
      
      public Header next()
      {
        if (next == null)
        {
          fetch();
          if (next == null) {
            throw new NoSuchElementException();
          }
        }
        if (paramBoolean)
        {
          assert (get(idx - 1) == next);
          understood(idx - 1);
        }
        Header localHeader = next;
        next = null;
        return localHeader;
      }
      
      private void fetch()
      {
        while (idx < size())
        {
          Header localHeader = get(idx++);
          if (localHeader.getNamespaceURI().equals(paramString))
          {
            next = localHeader;
            break;
          }
        }
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
  
  public String getTo(AddressingVersion paramAddressingVersion, SOAPVersion paramSOAPVersion)
  {
    return AddressingUtils.getTo(this, paramAddressingVersion, paramSOAPVersion);
  }
  
  public String getAction(@NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion)
  {
    return AddressingUtils.getAction(this, paramAddressingVersion, paramSOAPVersion);
  }
  
  public WSEndpointReference getReplyTo(@NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion)
  {
    return AddressingUtils.getReplyTo(this, paramAddressingVersion, paramSOAPVersion);
  }
  
  public WSEndpointReference getFaultTo(@NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion)
  {
    return AddressingUtils.getFaultTo(this, paramAddressingVersion, paramSOAPVersion);
  }
  
  public String getMessageID(@NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion)
  {
    return AddressingUtils.getMessageID(this, paramAddressingVersion, paramSOAPVersion);
  }
  
  public String getRelatesTo(@NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion)
  {
    return AddressingUtils.getRelatesTo(this, paramAddressingVersion, paramSOAPVersion);
  }
  
  public void fillRequestAddressingHeaders(Packet paramPacket, AddressingVersion paramAddressingVersion, SOAPVersion paramSOAPVersion, boolean paramBoolean1, String paramString, boolean paramBoolean2)
  {
    AddressingUtils.fillRequestAddressingHeaders(this, paramPacket, paramAddressingVersion, paramSOAPVersion, paramBoolean1, paramString, paramBoolean2);
  }
  
  public void fillRequestAddressingHeaders(Packet paramPacket, AddressingVersion paramAddressingVersion, SOAPVersion paramSOAPVersion, boolean paramBoolean, String paramString)
  {
    AddressingUtils.fillRequestAddressingHeaders(this, paramPacket, paramAddressingVersion, paramSOAPVersion, paramBoolean, paramString);
  }
  
  public void fillRequestAddressingHeaders(WSDLPort paramWSDLPort, @NotNull WSBinding paramWSBinding, Packet paramPacket)
  {
    AddressingUtils.fillRequestAddressingHeaders(this, paramWSDLPort, paramWSBinding, paramPacket);
  }
  
  public boolean add(Header paramHeader)
  {
    return super.add(paramHeader);
  }
  
  @Nullable
  public Header remove(@NotNull String paramString1, @NotNull String paramString2)
  {
    int i = size();
    for (int j = 0; j < i; j++)
    {
      Header localHeader = get(j);
      if ((localHeader.getLocalPart().equals(paramString2)) && (localHeader.getNamespaceURI().equals(paramString1))) {
        return remove(j);
      }
    }
    return null;
  }
  
  public boolean addOrReplace(Header paramHeader)
  {
    for (int i = 0; i < size(); i++)
    {
      Header localHeader = get(i);
      if ((localHeader.getNamespaceURI().equals(paramHeader.getNamespaceURI())) && (localHeader.getLocalPart().equals(paramHeader.getLocalPart())))
      {
        removeInternal(i);
        addInternal(i, paramHeader);
        return true;
      }
    }
    return add(paramHeader);
  }
  
  public void replace(Header paramHeader1, Header paramHeader2)
  {
    for (int i = 0; i < size(); i++)
    {
      Header localHeader = get(i);
      if ((localHeader.getNamespaceURI().equals(paramHeader2.getNamespaceURI())) && (localHeader.getLocalPart().equals(paramHeader2.getLocalPart())))
      {
        removeInternal(i);
        addInternal(i, paramHeader2);
        return;
      }
    }
    throw new IllegalArgumentException();
  }
  
  protected void addInternal(int paramInt, Header paramHeader)
  {
    super.add(paramInt, paramHeader);
  }
  
  protected Header removeInternal(int paramInt)
  {
    return (Header)super.remove(paramInt);
  }
  
  @Nullable
  public Header remove(@NotNull QName paramQName)
  {
    return remove(paramQName.getNamespaceURI(), paramQName.getLocalPart());
  }
  
  public Header remove(int paramInt)
  {
    removeUnderstoodBit(paramInt);
    return (Header)super.remove(paramInt);
  }
  
  private void removeUnderstoodBit(int paramInt)
  {
    assert (paramInt < size());
    int i;
    if (paramInt < 32)
    {
      i = understoodBits >>> -31 + paramInt << paramInt;
      int j = understoodBits << -paramInt >>> 31 - paramInt >>> 1;
      understoodBits = (i | j);
      if ((moreUnderstoodBits != null) && (moreUnderstoodBits.cardinality() > 0))
      {
        if (moreUnderstoodBits.get(0)) {
          understoodBits |= 0x80000000;
        }
        moreUnderstoodBits.clear(0);
        for (int k = moreUnderstoodBits.nextSetBit(1); k > 0; k = moreUnderstoodBits.nextSetBit(k + 1))
        {
          moreUnderstoodBits.set(k - 1);
          moreUnderstoodBits.clear(k);
        }
      }
    }
    else if ((moreUnderstoodBits != null) && (moreUnderstoodBits.cardinality() > 0))
    {
      paramInt -= 32;
      moreUnderstoodBits.clear(paramInt);
      for (i = moreUnderstoodBits.nextSetBit(paramInt); i >= 1; i = moreUnderstoodBits.nextSetBit(i + 1))
      {
        moreUnderstoodBits.set(i - 1);
        moreUnderstoodBits.clear(i);
      }
    }
    if ((size() - 1 <= 33) && (moreUnderstoodBits != null)) {
      moreUnderstoodBits = null;
    }
  }
  
  public boolean remove(Object paramObject)
  {
    if (paramObject != null) {
      for (int i = 0; i < size(); i++) {
        if (paramObject.equals(get(i)))
        {
          remove(i);
          return true;
        }
      }
    }
    return false;
  }
  
  public Header remove(Header paramHeader)
  {
    if (remove(paramHeader)) {
      return paramHeader;
    }
    return null;
  }
  
  public static HeaderList copy(MessageHeaders paramMessageHeaders)
  {
    if (paramMessageHeaders == null) {
      return null;
    }
    return new HeaderList(paramMessageHeaders);
  }
  
  public static HeaderList copy(HeaderList paramHeaderList)
  {
    return copy(paramHeaderList);
  }
  
  public void readResponseAddressingHeaders(WSDLPort paramWSDLPort, WSBinding paramWSBinding) {}
  
  public void understood(QName paramQName)
  {
    get(paramQName, true);
  }
  
  public void understood(String paramString1, String paramString2)
  {
    get(paramString1, paramString2, true);
  }
  
  public Set<QName> getUnderstoodHeaders()
  {
    HashSet localHashSet = new HashSet();
    for (int i = 0; i < size(); i++) {
      if (isUnderstood(i))
      {
        Header localHeader = get(i);
        localHashSet.add(new QName(localHeader.getNamespaceURI(), localHeader.getLocalPart()));
      }
    }
    return localHashSet;
  }
  
  public boolean isUnderstood(Header paramHeader)
  {
    return isUnderstood(paramHeader.getNamespaceURI(), paramHeader.getLocalPart());
  }
  
  public boolean isUnderstood(String paramString1, String paramString2)
  {
    for (int i = 0; i < size(); i++)
    {
      Header localHeader = get(i);
      if ((localHeader.getLocalPart().equals(paramString2)) && (localHeader.getNamespaceURI().equals(paramString1))) {
        return isUnderstood(i);
      }
    }
    return false;
  }
  
  public boolean isUnderstood(QName paramQName)
  {
    return isUnderstood(paramQName.getNamespaceURI(), paramQName.getLocalPart());
  }
  
  public Set<QName> getNotUnderstoodHeaders(Set<String> paramSet, Set<QName> paramSet1, WSBinding paramWSBinding)
  {
    HashSet localHashSet = null;
    if (paramSet == null) {
      paramSet = new HashSet();
    }
    SOAPVersion localSOAPVersion = getEffectiveSOAPVersion(paramWSBinding);
    paramSet.add(implicitRole);
    for (int i = 0; i < size(); i++) {
      if (!isUnderstood(i))
      {
        Header localHeader = get(i);
        if (!localHeader.isIgnorable(localSOAPVersion, paramSet))
        {
          QName localQName = new QName(localHeader.getNamespaceURI(), localHeader.getLocalPart());
          if (paramWSBinding == null)
          {
            if (localHashSet == null) {
              localHashSet = new HashSet();
            }
            localHashSet.add(localQName);
          }
          else if (((paramWSBinding instanceof SOAPBindingImpl)) && (!((SOAPBindingImpl)paramWSBinding).understandsHeader(localQName)) && (!paramSet1.contains(localQName)))
          {
            if (localHashSet == null) {
              localHashSet = new HashSet();
            }
            localHashSet.add(localQName);
          }
        }
      }
    }
    return localHashSet;
  }
  
  private SOAPVersion getEffectiveSOAPVersion(WSBinding paramWSBinding)
  {
    SOAPVersion localSOAPVersion = soapVersion != null ? soapVersion : paramWSBinding.getSOAPVersion();
    if (localSOAPVersion == null) {
      localSOAPVersion = SOAPVersion.SOAP_11;
    }
    return localSOAPVersion;
  }
  
  public void setSoapVersion(SOAPVersion paramSOAPVersion)
  {
    soapVersion = paramSOAPVersion;
  }
  
  public Iterator<Header> getHeaders()
  {
    return iterator();
  }
  
  public List<Header> asList()
  {
    return this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\message\HeaderList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */