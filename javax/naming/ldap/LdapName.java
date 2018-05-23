package javax.naming.ldap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.naming.InvalidNameException;
import javax.naming.Name;

public class LdapName
  implements Name
{
  private transient List<Rdn> rdns;
  private transient String unparsed;
  private static final long serialVersionUID = -1595520034788997356L;
  
  public LdapName(String paramString)
    throws InvalidNameException
  {
    unparsed = paramString;
    parse();
  }
  
  public LdapName(List<Rdn> paramList)
  {
    rdns = new ArrayList(paramList.size());
    for (int i = 0; i < paramList.size(); i++)
    {
      Object localObject = paramList.get(i);
      if (!(localObject instanceof Rdn)) {
        throw new IllegalArgumentException("Entry:" + localObject + "  not a valid type;list entries must be of type Rdn");
      }
      rdns.add((Rdn)localObject);
    }
  }
  
  private LdapName(String paramString, List<Rdn> paramList, int paramInt1, int paramInt2)
  {
    unparsed = paramString;
    List localList = paramList.subList(paramInt1, paramInt2);
    rdns = new ArrayList(localList);
  }
  
  public int size()
  {
    return rdns.size();
  }
  
  public boolean isEmpty()
  {
    return rdns.isEmpty();
  }
  
  public Enumeration<String> getAll()
  {
    final Iterator localIterator = rdns.iterator();
    new Enumeration()
    {
      public boolean hasMoreElements()
      {
        return localIterator.hasNext();
      }
      
      public String nextElement()
      {
        return ((Rdn)localIterator.next()).toString();
      }
    };
  }
  
  public String get(int paramInt)
  {
    return ((Rdn)rdns.get(paramInt)).toString();
  }
  
  public Rdn getRdn(int paramInt)
  {
    return (Rdn)rdns.get(paramInt);
  }
  
  public Name getPrefix(int paramInt)
  {
    try
    {
      return new LdapName(null, rdns, 0, paramInt);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new IndexOutOfBoundsException("Posn: " + paramInt + ", Size: " + rdns.size());
    }
  }
  
  public Name getSuffix(int paramInt)
  {
    try
    {
      return new LdapName(null, rdns, paramInt, rdns.size());
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new IndexOutOfBoundsException("Posn: " + paramInt + ", Size: " + rdns.size());
    }
  }
  
  public boolean startsWith(Name paramName)
  {
    if (paramName == null) {
      return false;
    }
    int i = rdns.size();
    int j = paramName.size();
    return (i >= j) && (matches(0, j, paramName));
  }
  
  public boolean startsWith(List<Rdn> paramList)
  {
    if (paramList == null) {
      return false;
    }
    int i = rdns.size();
    int j = paramList.size();
    return (i >= j) && (doesListMatch(0, j, paramList));
  }
  
  public boolean endsWith(Name paramName)
  {
    if (paramName == null) {
      return false;
    }
    int i = rdns.size();
    int j = paramName.size();
    return (i >= j) && (matches(i - j, i, paramName));
  }
  
  public boolean endsWith(List<Rdn> paramList)
  {
    if (paramList == null) {
      return false;
    }
    int i = rdns.size();
    int j = paramList.size();
    return (i >= j) && (doesListMatch(i - j, i, paramList));
  }
  
  private boolean doesListMatch(int paramInt1, int paramInt2, List<Rdn> paramList)
  {
    for (int i = paramInt1; i < paramInt2; i++) {
      if (!((Rdn)rdns.get(i)).equals(paramList.get(i - paramInt1))) {
        return false;
      }
    }
    return true;
  }
  
  private boolean matches(int paramInt1, int paramInt2, Name paramName)
  {
    if ((paramName instanceof LdapName))
    {
      LdapName localLdapName = (LdapName)paramName;
      return doesListMatch(paramInt1, paramInt2, rdns);
    }
    for (int i = paramInt1; i < paramInt2; i++)
    {
      String str = paramName.get(i - paramInt1);
      Rdn localRdn;
      try
      {
        localRdn = new Rfc2253Parser(str).parseRdn();
      }
      catch (InvalidNameException localInvalidNameException)
      {
        return false;
      }
      if (!localRdn.equals(rdns.get(i))) {
        return false;
      }
    }
    return true;
  }
  
  public Name addAll(Name paramName)
    throws InvalidNameException
  {
    return addAll(size(), paramName);
  }
  
  public Name addAll(List<Rdn> paramList)
  {
    return addAll(size(), paramList);
  }
  
  public Name addAll(int paramInt, Name paramName)
    throws InvalidNameException
  {
    unparsed = null;
    Object localObject;
    if ((paramName instanceof LdapName))
    {
      localObject = (LdapName)paramName;
      rdns.addAll(paramInt, rdns);
    }
    else
    {
      localObject = paramName.getAll();
      while (((Enumeration)localObject).hasMoreElements()) {
        rdns.add(paramInt++, new Rfc2253Parser((String)((Enumeration)localObject).nextElement()).parseRdn());
      }
    }
    return this;
  }
  
  public Name addAll(int paramInt, List<Rdn> paramList)
  {
    unparsed = null;
    for (int i = 0; i < paramList.size(); i++)
    {
      Object localObject = paramList.get(i);
      if (!(localObject instanceof Rdn)) {
        throw new IllegalArgumentException("Entry:" + localObject + "  not a valid type;suffix list entries must be of type Rdn");
      }
      rdns.add(i + paramInt, (Rdn)localObject);
    }
    return this;
  }
  
  public Name add(String paramString)
    throws InvalidNameException
  {
    return add(size(), paramString);
  }
  
  public Name add(Rdn paramRdn)
  {
    return add(size(), paramRdn);
  }
  
  public Name add(int paramInt, String paramString)
    throws InvalidNameException
  {
    Rdn localRdn = new Rfc2253Parser(paramString).parseRdn();
    rdns.add(paramInt, localRdn);
    unparsed = null;
    return this;
  }
  
  public Name add(int paramInt, Rdn paramRdn)
  {
    if (paramRdn == null) {
      throw new NullPointerException("Cannot set comp to null");
    }
    rdns.add(paramInt, paramRdn);
    unparsed = null;
    return this;
  }
  
  public Object remove(int paramInt)
    throws InvalidNameException
  {
    unparsed = null;
    return ((Rdn)rdns.remove(paramInt)).toString();
  }
  
  public List<Rdn> getRdns()
  {
    return Collections.unmodifiableList(rdns);
  }
  
  public Object clone()
  {
    return new LdapName(unparsed, rdns, 0, rdns.size());
  }
  
  public String toString()
  {
    if (unparsed != null) {
      return unparsed;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    int i = rdns.size();
    if (i - 1 >= 0) {
      localStringBuilder.append(rdns.get(i - 1));
    }
    for (int j = i - 2; j >= 0; j--)
    {
      localStringBuilder.append(',');
      localStringBuilder.append(rdns.get(j));
    }
    unparsed = localStringBuilder.toString();
    return unparsed;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof LdapName)) {
      return false;
    }
    LdapName localLdapName = (LdapName)paramObject;
    if (rdns.size() != rdns.size()) {
      return false;
    }
    if ((unparsed != null) && (unparsed.equalsIgnoreCase(unparsed))) {
      return true;
    }
    for (int i = 0; i < rdns.size(); i++)
    {
      Rdn localRdn1 = (Rdn)rdns.get(i);
      Rdn localRdn2 = (Rdn)rdns.get(i);
      if (!localRdn1.equals(localRdn2)) {
        return false;
      }
    }
    return true;
  }
  
  public int compareTo(Object paramObject)
  {
    if (!(paramObject instanceof LdapName)) {
      throw new ClassCastException("The obj is not a LdapName");
    }
    if (paramObject == this) {
      return 0;
    }
    LdapName localLdapName = (LdapName)paramObject;
    if ((unparsed != null) && (unparsed.equalsIgnoreCase(unparsed))) {
      return 0;
    }
    int i = Math.min(rdns.size(), rdns.size());
    for (int j = 0; j < i; j++)
    {
      Rdn localRdn1 = (Rdn)rdns.get(j);
      Rdn localRdn2 = (Rdn)rdns.get(j);
      int k = localRdn1.compareTo(localRdn2);
      if (k != 0) {
        return k;
      }
    }
    return rdns.size() - rdns.size();
  }
  
  public int hashCode()
  {
    int i = 0;
    for (int j = 0; j < rdns.size(); j++)
    {
      Rdn localRdn = (Rdn)rdns.get(j);
      i += localRdn.hashCode();
    }
    return i;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(toString());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    unparsed = ((String)paramObjectInputStream.readObject());
    try
    {
      parse();
    }
    catch (InvalidNameException localInvalidNameException)
    {
      throw new StreamCorruptedException("Invalid name: " + unparsed);
    }
  }
  
  private void parse()
    throws InvalidNameException
  {
    rdns = new Rfc2253Parser(unparsed).parseDn();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\ldap\LdapName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */