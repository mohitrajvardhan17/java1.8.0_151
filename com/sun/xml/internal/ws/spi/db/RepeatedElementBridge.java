package com.sun.xml.internal.ws.spi.db;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public class RepeatedElementBridge<T>
  implements XMLBridge<T>
{
  XMLBridge<T> delegate;
  CollectionHandler collectionHandler;
  static final CollectionHandler ListHandler = new BaseCollectionHandler(List.class)
  {
    public Object convert(List paramAnonymousList)
    {
      return paramAnonymousList;
    }
  };
  static final CollectionHandler HashSetHandler = new BaseCollectionHandler(HashSet.class)
  {
    public Object convert(List paramAnonymousList)
    {
      return new HashSet(paramAnonymousList);
    }
  };
  
  public RepeatedElementBridge(TypeInfo paramTypeInfo, XMLBridge paramXMLBridge)
  {
    delegate = paramXMLBridge;
    collectionHandler = create(paramTypeInfo);
  }
  
  public CollectionHandler collectionHandler()
  {
    return collectionHandler;
  }
  
  public BindingContext context()
  {
    return delegate.context();
  }
  
  public void marshal(T paramT, XMLStreamWriter paramXMLStreamWriter, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException
  {
    delegate.marshal(paramT, paramXMLStreamWriter, paramAttachmentMarshaller);
  }
  
  public void marshal(T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException
  {
    delegate.marshal(paramT, paramOutputStream, paramNamespaceContext, paramAttachmentMarshaller);
  }
  
  public void marshal(T paramT, Node paramNode)
    throws JAXBException
  {
    delegate.marshal(paramT, paramNode);
  }
  
  public void marshal(T paramT, ContentHandler paramContentHandler, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException
  {
    delegate.marshal(paramT, paramContentHandler, paramAttachmentMarshaller);
  }
  
  public void marshal(T paramT, Result paramResult)
    throws JAXBException
  {
    delegate.marshal(paramT, paramResult);
  }
  
  public T unmarshal(XMLStreamReader paramXMLStreamReader, AttachmentUnmarshaller paramAttachmentUnmarshaller)
    throws JAXBException
  {
    return (T)delegate.unmarshal(paramXMLStreamReader, paramAttachmentUnmarshaller);
  }
  
  public T unmarshal(Source paramSource, AttachmentUnmarshaller paramAttachmentUnmarshaller)
    throws JAXBException
  {
    return (T)delegate.unmarshal(paramSource, paramAttachmentUnmarshaller);
  }
  
  public T unmarshal(InputStream paramInputStream)
    throws JAXBException
  {
    return (T)delegate.unmarshal(paramInputStream);
  }
  
  public T unmarshal(Node paramNode, AttachmentUnmarshaller paramAttachmentUnmarshaller)
    throws JAXBException
  {
    return (T)delegate.unmarshal(paramNode, paramAttachmentUnmarshaller);
  }
  
  public TypeInfo getTypeInfo()
  {
    return delegate.getTypeInfo();
  }
  
  public boolean supportOutputStream()
  {
    return delegate.supportOutputStream();
  }
  
  public static CollectionHandler create(TypeInfo paramTypeInfo)
  {
    Class localClass = (Class)type;
    if (localClass.isArray()) {
      return new ArrayHandler((Class)getItemTypetype);
    }
    if ((List.class.equals(localClass)) || (Collection.class.equals(localClass))) {
      return ListHandler;
    }
    if ((Set.class.equals(localClass)) || (HashSet.class.equals(localClass))) {
      return HashSetHandler;
    }
    return new BaseCollectionHandler(localClass);
  }
  
  static class ArrayHandler
    implements RepeatedElementBridge.CollectionHandler
  {
    Class componentClass;
    
    public ArrayHandler(Class paramClass)
    {
      componentClass = paramClass;
    }
    
    public int getSize(Object paramObject)
    {
      return Array.getLength(paramObject);
    }
    
    public Object convert(List paramList)
    {
      Object localObject = Array.newInstance(componentClass, paramList.size());
      for (int i = 0; i < paramList.size(); i++) {
        Array.set(localObject, i, paramList.get(i));
      }
      return localObject;
    }
    
    public Iterator iterator(final Object paramObject)
    {
      new Iterator()
      {
        int index = 0;
        
        public boolean hasNext()
        {
          if ((paramObject == null) || (Array.getLength(paramObject) == 0)) {
            return false;
          }
          return index != Array.getLength(paramObject);
        }
        
        public Object next()
          throws NoSuchElementException
        {
          Object localObject = null;
          try
          {
            localObject = Array.get(paramObject, index++);
          }
          catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
          {
            throw new NoSuchElementException();
          }
          return localObject;
        }
        
        public void remove() {}
      };
    }
  }
  
  static class BaseCollectionHandler
    implements RepeatedElementBridge.CollectionHandler
  {
    Class type;
    
    BaseCollectionHandler(Class paramClass)
    {
      type = paramClass;
    }
    
    public int getSize(Object paramObject)
    {
      return ((Collection)paramObject).size();
    }
    
    public Object convert(List paramList)
    {
      try
      {
        Object localObject = type.newInstance();
        ((Collection)localObject).addAll(paramList);
        return localObject;
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
      return paramList;
    }
    
    public Iterator iterator(Object paramObject)
    {
      return ((Collection)paramObject).iterator();
    }
  }
  
  public static abstract interface CollectionHandler
  {
    public abstract int getSize(Object paramObject);
    
    public abstract Iterator iterator(Object paramObject);
    
    public abstract Object convert(List paramList);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\spi\db\RepeatedElementBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */