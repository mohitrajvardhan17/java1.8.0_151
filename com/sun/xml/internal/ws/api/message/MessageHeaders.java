package com.sun.xml.internal.ws.api.message;

import com.sun.xml.internal.ws.api.WSBinding;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;

public abstract interface MessageHeaders
{
  public abstract void understood(Header paramHeader);
  
  public abstract void understood(QName paramQName);
  
  public abstract void understood(String paramString1, String paramString2);
  
  public abstract Header get(String paramString1, String paramString2, boolean paramBoolean);
  
  public abstract Header get(QName paramQName, boolean paramBoolean);
  
  public abstract Iterator<Header> getHeaders(String paramString1, String paramString2, boolean paramBoolean);
  
  public abstract Iterator<Header> getHeaders(String paramString, boolean paramBoolean);
  
  public abstract Iterator<Header> getHeaders(QName paramQName, boolean paramBoolean);
  
  public abstract Iterator<Header> getHeaders();
  
  public abstract boolean hasHeaders();
  
  public abstract boolean add(Header paramHeader);
  
  public abstract Header remove(QName paramQName);
  
  public abstract Header remove(String paramString1, String paramString2);
  
  public abstract void replace(Header paramHeader1, Header paramHeader2);
  
  public abstract boolean addOrReplace(Header paramHeader);
  
  public abstract Set<QName> getUnderstoodHeaders();
  
  public abstract Set<QName> getNotUnderstoodHeaders(Set<String> paramSet, Set<QName> paramSet1, WSBinding paramWSBinding);
  
  public abstract boolean isUnderstood(Header paramHeader);
  
  public abstract boolean isUnderstood(QName paramQName);
  
  public abstract boolean isUnderstood(String paramString1, String paramString2);
  
  public abstract List<Header> asList();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\message\MessageHeaders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */