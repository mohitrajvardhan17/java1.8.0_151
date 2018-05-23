package java.lang;

import java.io.Serializable;

public final class Boolean
  implements Serializable, Comparable<Boolean>
{
  public static final Boolean TRUE = new Boolean(true);
  public static final Boolean FALSE = new Boolean(false);
  public static final Class<Boolean> TYPE = Class.getPrimitiveClass("boolean");
  private final boolean value;
  private static final long serialVersionUID = -3665804199014368530L;
  
  public Boolean(boolean paramBoolean)
  {
    value = paramBoolean;
  }
  
  public Boolean(String paramString)
  {
    this(parseBoolean(paramString));
  }
  
  public static boolean parseBoolean(String paramString)
  {
    return (paramString != null) && (paramString.equalsIgnoreCase("true"));
  }
  
  public boolean booleanValue()
  {
    return value;
  }
  
  public static Boolean valueOf(boolean paramBoolean)
  {
    return paramBoolean ? TRUE : FALSE;
  }
  
  public static Boolean valueOf(String paramString)
  {
    return parseBoolean(paramString) ? TRUE : FALSE;
  }
  
  public static String toString(boolean paramBoolean)
  {
    return paramBoolean ? "true" : "false";
  }
  
  public String toString()
  {
    return value ? "true" : "false";
  }
  
  public int hashCode()
  {
    return hashCode(value);
  }
  
  public static int hashCode(boolean paramBoolean)
  {
    return paramBoolean ? 1231 : 1237;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Boolean)) {
      return value == ((Boolean)paramObject).booleanValue();
    }
    return false;
  }
  
  public static boolean getBoolean(String paramString)
  {
    boolean bool = false;
    try
    {
      bool = parseBoolean(System.getProperty(paramString));
    }
    catch (IllegalArgumentException|NullPointerException localIllegalArgumentException) {}
    return bool;
  }
  
  public int compareTo(Boolean paramBoolean)
  {
    return compare(value, value);
  }
  
  public static int compare(boolean paramBoolean1, boolean paramBoolean2)
  {
    return paramBoolean1 ? 1 : paramBoolean1 == paramBoolean2 ? 0 : -1;
  }
  
  public static boolean logicalAnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    return (paramBoolean1) && (paramBoolean2);
  }
  
  public static boolean logicalOr(boolean paramBoolean1, boolean paramBoolean2)
  {
    return (paramBoolean1) || (paramBoolean2);
  }
  
  public static boolean logicalXor(boolean paramBoolean1, boolean paramBoolean2)
  {
    return paramBoolean1 ^ paramBoolean2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\Boolean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */