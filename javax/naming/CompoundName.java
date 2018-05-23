package javax.naming;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Enumeration;
import java.util.Properties;

public class CompoundName
  implements Name
{
  protected transient NameImpl impl;
  protected transient Properties mySyntax;
  private static final long serialVersionUID = 3513100557083972036L;
  
  protected CompoundName(Enumeration<String> paramEnumeration, Properties paramProperties)
  {
    if (paramProperties == null) {
      throw new NullPointerException();
    }
    mySyntax = paramProperties;
    impl = new NameImpl(paramProperties, paramEnumeration);
  }
  
  public CompoundName(String paramString, Properties paramProperties)
    throws InvalidNameException
  {
    if (paramProperties == null) {
      throw new NullPointerException();
    }
    mySyntax = paramProperties;
    impl = new NameImpl(paramProperties, paramString);
  }
  
  public String toString()
  {
    return impl.toString();
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject != null) && ((paramObject instanceof CompoundName)) && (impl.equals(impl));
  }
  
  public int hashCode()
  {
    return impl.hashCode();
  }
  
  public Object clone()
  {
    return new CompoundName(getAll(), mySyntax);
  }
  
  public int compareTo(Object paramObject)
  {
    if (!(paramObject instanceof CompoundName)) {
      throw new ClassCastException("Not a CompoundName");
    }
    return impl.compareTo(impl);
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
    return new CompoundName(localEnumeration, mySyntax);
  }
  
  public Name getSuffix(int paramInt)
  {
    Enumeration localEnumeration = impl.getSuffix(paramInt);
    return new CompoundName(localEnumeration, mySyntax);
  }
  
  public boolean startsWith(Name paramName)
  {
    if ((paramName instanceof CompoundName)) {
      return impl.startsWith(paramName.size(), paramName.getAll());
    }
    return false;
  }
  
  public boolean endsWith(Name paramName)
  {
    if ((paramName instanceof CompoundName)) {
      return impl.endsWith(paramName.size(), paramName.getAll());
    }
    return false;
  }
  
  public Name addAll(Name paramName)
    throws InvalidNameException
  {
    if ((paramName instanceof CompoundName))
    {
      impl.addAll(paramName.getAll());
      return this;
    }
    throw new InvalidNameException("Not a compound name: " + paramName.toString());
  }
  
  public Name addAll(int paramInt, Name paramName)
    throws InvalidNameException
  {
    if ((paramName instanceof CompoundName))
    {
      impl.addAll(paramInt, paramName.getAll());
      return this;
    }
    throw new InvalidNameException("Not a compound name: " + paramName.toString());
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
    paramObjectOutputStream.writeObject(mySyntax);
    paramObjectOutputStream.writeInt(size());
    Enumeration localEnumeration = getAll();
    while (localEnumeration.hasMoreElements()) {
      paramObjectOutputStream.writeObject(localEnumeration.nextElement());
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    mySyntax = ((Properties)paramObjectInputStream.readObject());
    impl = new NameImpl(mySyntax);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\CompoundName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */