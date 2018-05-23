package javax.naming.directory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public class BasicAttributes
  implements Attributes
{
  private boolean ignoreCase = false;
  transient Hashtable<String, Attribute> attrs = new Hashtable(11);
  private static final long serialVersionUID = 4980164073184639448L;
  
  public BasicAttributes() {}
  
  public BasicAttributes(boolean paramBoolean)
  {
    ignoreCase = paramBoolean;
  }
  
  public BasicAttributes(String paramString, Object paramObject)
  {
    this();
    put(new BasicAttribute(paramString, paramObject));
  }
  
  public BasicAttributes(String paramString, Object paramObject, boolean paramBoolean)
  {
    this(paramBoolean);
    put(new BasicAttribute(paramString, paramObject));
  }
  
  public Object clone()
  {
    BasicAttributes localBasicAttributes;
    try
    {
      localBasicAttributes = (BasicAttributes)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      localBasicAttributes = new BasicAttributes(ignoreCase);
    }
    attrs = ((Hashtable)attrs.clone());
    return localBasicAttributes;
  }
  
  public boolean isCaseIgnored()
  {
    return ignoreCase;
  }
  
  public int size()
  {
    return attrs.size();
  }
  
  public Attribute get(String paramString)
  {
    Attribute localAttribute = (Attribute)attrs.get(ignoreCase ? paramString.toLowerCase(Locale.ENGLISH) : paramString);
    return localAttribute;
  }
  
  public NamingEnumeration<Attribute> getAll()
  {
    return new AttrEnumImpl();
  }
  
  public NamingEnumeration<String> getIDs()
  {
    return new IDEnumImpl();
  }
  
  public Attribute put(String paramString, Object paramObject)
  {
    return put(new BasicAttribute(paramString, paramObject));
  }
  
  public Attribute put(Attribute paramAttribute)
  {
    String str = paramAttribute.getID();
    if (ignoreCase) {
      str = str.toLowerCase(Locale.ENGLISH);
    }
    return (Attribute)attrs.put(str, paramAttribute);
  }
  
  public Attribute remove(String paramString)
  {
    String str = ignoreCase ? paramString.toLowerCase(Locale.ENGLISH) : paramString;
    return (Attribute)attrs.remove(str);
  }
  
  public String toString()
  {
    if (attrs.size() == 0) {
      return "No attributes";
    }
    return attrs.toString();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof Attributes)))
    {
      Attributes localAttributes = (Attributes)paramObject;
      if (ignoreCase != localAttributes.isCaseIgnored()) {
        return false;
      }
      if (size() == localAttributes.size())
      {
        try
        {
          NamingEnumeration localNamingEnumeration = localAttributes.getAll();
          while (localNamingEnumeration.hasMore())
          {
            Attribute localAttribute1 = (Attribute)localNamingEnumeration.next();
            Attribute localAttribute2 = get(localAttribute1.getID());
            if (!localAttribute1.equals(localAttribute2)) {
              return false;
            }
          }
        }
        catch (NamingException localNamingException)
        {
          return false;
        }
        return true;
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    int i = ignoreCase ? 1 : 0;
    try
    {
      NamingEnumeration localNamingEnumeration = getAll();
      while (localNamingEnumeration.hasMore()) {
        i += localNamingEnumeration.next().hashCode();
      }
    }
    catch (NamingException localNamingException) {}
    return i;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(attrs.size());
    Enumeration localEnumeration = attrs.elements();
    while (localEnumeration.hasMoreElements()) {
      paramObjectOutputStream.writeObject(localEnumeration.nextElement());
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    attrs = (i >= 1 ? new Hashtable(i * 2) : new Hashtable(2));
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      put((Attribute)paramObjectInputStream.readObject());
    }
  }
  
  class AttrEnumImpl
    implements NamingEnumeration<Attribute>
  {
    Enumeration<Attribute> elements = attrs.elements();
    
    public AttrEnumImpl() {}
    
    public boolean hasMoreElements()
    {
      return elements.hasMoreElements();
    }
    
    public Attribute nextElement()
    {
      return (Attribute)elements.nextElement();
    }
    
    public boolean hasMore()
      throws NamingException
    {
      return hasMoreElements();
    }
    
    public Attribute next()
      throws NamingException
    {
      return nextElement();
    }
    
    public void close()
      throws NamingException
    {
      elements = null;
    }
  }
  
  class IDEnumImpl
    implements NamingEnumeration<String>
  {
    Enumeration<Attribute> elements = attrs.elements();
    
    public IDEnumImpl() {}
    
    public boolean hasMoreElements()
    {
      return elements.hasMoreElements();
    }
    
    public String nextElement()
    {
      Attribute localAttribute = (Attribute)elements.nextElement();
      return localAttribute.getID();
    }
    
    public boolean hasMore()
      throws NamingException
    {
      return hasMoreElements();
    }
    
    public String next()
      throws NamingException
    {
      return nextElement();
    }
    
    public void close()
      throws NamingException
    {
      elements = null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\directory\BasicAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */