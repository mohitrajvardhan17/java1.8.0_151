package com.sun.xml.internal.txw2;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;

public abstract interface DatatypeWriter<DT>
{
  public static final List<DatatypeWriter<?>> BUILTIN = Collections.unmodifiableList(new AbstractList()
  {
    private DatatypeWriter<?>[] BUILTIN_ARRAY = { new DatatypeWriter()new DatatypeWriter
    {
      public Class<String> getType()
      {
        return String.class;
      }
      
      public void print(String paramAnonymous2String, NamespaceResolver paramAnonymous2NamespaceResolver, StringBuilder paramAnonymous2StringBuilder)
      {
        paramAnonymous2StringBuilder.append(paramAnonymous2String);
      }
    }, new DatatypeWriter()new DatatypeWriter
    {
      public Class<Integer> getType()
      {
        return Integer.class;
      }
      
      public void print(Integer paramAnonymous2Integer, NamespaceResolver paramAnonymous2NamespaceResolver, StringBuilder paramAnonymous2StringBuilder)
      {
        paramAnonymous2StringBuilder.append(paramAnonymous2Integer);
      }
    }, new DatatypeWriter()new DatatypeWriter
    {
      public Class<Float> getType()
      {
        return Float.class;
      }
      
      public void print(Float paramAnonymous2Float, NamespaceResolver paramAnonymous2NamespaceResolver, StringBuilder paramAnonymous2StringBuilder)
      {
        paramAnonymous2StringBuilder.append(paramAnonymous2Float);
      }
    }, new DatatypeWriter()new DatatypeWriter
    {
      public Class<Double> getType()
      {
        return Double.class;
      }
      
      public void print(Double paramAnonymous2Double, NamespaceResolver paramAnonymous2NamespaceResolver, StringBuilder paramAnonymous2StringBuilder)
      {
        paramAnonymous2StringBuilder.append(paramAnonymous2Double);
      }
    }, new DatatypeWriter()
    {
      public Class<QName> getType()
      {
        return QName.class;
      }
      
      public void print(QName paramAnonymous2QName, NamespaceResolver paramAnonymous2NamespaceResolver, StringBuilder paramAnonymous2StringBuilder)
      {
        String str = paramAnonymous2NamespaceResolver.getPrefix(paramAnonymous2QName.getNamespaceURI());
        if (str.length() != 0) {
          paramAnonymous2StringBuilder.append(str).append(':');
        }
        paramAnonymous2StringBuilder.append(paramAnonymous2QName.getLocalPart());
      }
    } };
    
    public DatatypeWriter<?> get(int paramAnonymousInt)
    {
      return BUILTIN_ARRAY[paramAnonymousInt];
    }
    
    public int size()
    {
      return BUILTIN_ARRAY.length;
    }
  });
  
  public abstract Class<DT> getType();
  
  public abstract void print(DT paramDT, NamespaceResolver paramNamespaceResolver, StringBuilder paramStringBuilder);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\DatatypeWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */