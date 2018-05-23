package javax.naming;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Enumeration;

public class CompositeName
  implements Name
{
  private transient NameImpl impl;
  private static final long serialVersionUID = 1667768148915813118L;
  
  protected CompositeName(Enumeration<String> paramEnumeration)
  {
    impl = new NameImpl(null, paramEnumeration);
  }
  
  public CompositeName(String paramString)
    throws InvalidNameException
  {
    impl = new NameImpl(null, paramString);
  }
  
  public CompositeName()
  {
    impl = new NameImpl(null);
  }
  
  public String toString()
  {
    return impl.toString();
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject != null) && ((paramObject instanceof CompositeName)) && (impl.equals(impl));
  }
  
  public int hashCode()
  {
    return impl.hashCode();
  }
  
  public int compareTo(Object paramObject)
  {
    if (!(paramObject instanceof CompositeName)) {
      throw new ClassCastException("Not a CompositeName");
    }
    return impl.compareTo(impl);
  }
  
  public Object clone()
  {
    return new CompositeName(getAll());
  }
  
  public int size()
  {
    return impl.size();
  }
  
  public boolean isEmpty()
  {
    return impl.isEmpty();
  }
  
  public Enumeration<String> getAll()
  {
    return impl.getAll();
  }
  
  public String get(int paramInt)
  {
    return impl.get(paramInt);
  }
  
  public Name getPrefix(int paramInt)
  {
    Enumeration localEnumeration = impl.getPrefix(paramInt);
    return new CompositeName(localEnumeration);
  }
  
  public Name getSuffix(int paramInt)
  {
    Enumeration localEnumeration = impl.getSuffix(paramInt);
    return new CompositeName(localEnumeration);
  }
  
  public boolean startsWith(Name paramName)
  {
    if ((paramName instanceof CompositeName)) {
      return impl.startsWith(paramName.size(), paramName.getAll());
    }
    return false;
  }
  
  public boolean endsWith(Name paramName)
  {
    if ((paramName instanceof CompositeName)) {
      return impl.endsWith(paramName.size(), paramName.getAll());
    }
    return false;
  }
  
  public Name addAll(Name paramName)
    throws InvalidNameException
  {
    if ((paramName instanceof CompositeName))
    {
      impl.addAll(paramName.getAll());
      return this;
    }
    throw new InvalidNameException("Not a composite name: " + paramName.toString());
  }
  
  public Name addAll(int paramInt, Name paramName)
    throws InvalidNameException
  {
    if ((paramName instanceof CompositeName))
    {
      impl.addAll(paramInt, paramName.getAll());
      return this;
    }
    throw new InvalidNameException("Not a composite name: " + paramName.toString());
  }
  
  public Name add(String paramString)
    throws InvalidNameException
  {
    impl.add(paramString);
    return this;
  }
  
  public Name add(int paramInt, String paramString)
    throws InvalidNameException
  {
    impl.add(paramInt, paramString);
    return this;
  }
  
  public Object remove(int paramInt)
    throws InvalidNameException
  {
    return impl.remove(paramInt);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeInt(size());
    Enumeration localEnumeration = getAll();
    while (localEnumeration.hasMoreElements()) {
      paramObjectOutputStream.writeObject(localEnumeration.nextElement());
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    impl = new NameImpl(null);
    int i = paramObjectInputStream.readInt();
    try
    {
      for (;;)
      {
        i--;
        if (i < 0) {
          break;
        }
        add((String)paramObjectInputStream.readObject());
      }
    }
    catch (InvalidNameException localInvalidNameException)
    {
      throw new StreamCorruptedException("Invalid name");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\CompositeName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */