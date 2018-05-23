package javax.naming;

import java.io.Serializable;
import java.util.Enumeration;

public abstract interface Name
  extends Cloneable, Serializable, Comparable<Object>
{
  public static final long serialVersionUID = -3617482732056931635L;
  
  public abstract Object clone();
  
  public abstract int compareTo(Object paramObject);
  
  public abstract int size();
  
  public abstract boolean isEmpty();
  
  public abstract Enumeration<String> getAll();
  
  public abstract String get(int paramInt);
  
  public abstract Name getPrefix(int paramInt);
  
  public abstract Name getSuffix(int paramInt);
  
  public abstract boolean startsWith(Name paramName);
  
  public abstract boolean endsWith(Name paramName);
  
  public abstract Name addAll(Name paramName)
    throws InvalidNameException;
  
  public abstract Name addAll(int paramInt, Name paramName)
    throws InvalidNameException;
  
  public abstract Name add(String paramString)
    throws InvalidNameException;
  
  public abstract Name add(int paramInt, String paramString)
    throws InvalidNameException;
  
  public abstract Object remove(int paramInt)
    throws InvalidNameException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\Name.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */