package com.sun.jndi.cosnaming;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import javax.naming.CompositeName;
import javax.naming.CompoundName;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import org.omg.CosNaming.NameComponent;

public final class CNNameParser
  implements NameParser
{
  private static final Properties mySyntax = new Properties();
  private static final char kindSeparator = '.';
  private static final char compSeparator = '/';
  private static final char escapeChar = '\\';
  
  public CNNameParser() {}
  
  public Name parse(String paramString)
    throws NamingException
  {
    Vector localVector = insStringToStringifiedComps(paramString);
    return new CNCompoundName(localVector.elements());
  }
  
  static NameComponent[] nameToCosName(Name paramName)
    throws InvalidNameException
  {
    int i = paramName.size();
    if (i == 0) {
      return new NameComponent[0];
    }
    NameComponent[] arrayOfNameComponent = new NameComponent[i];
    for (int j = 0; j < i; j++) {
      arrayOfNameComponent[j] = parseComponent(paramName.get(j));
    }
    return arrayOfNameComponent;
  }
  
  static String cosNameToInsString(NameComponent[] paramArrayOfNameComponent)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramArrayOfNameComponent.length; i++)
    {
      if (i > 0) {
        localStringBuffer.append('/');
      }
      localStringBuffer.append(stringifyComponent(paramArrayOfNameComponent[i]));
    }
    return localStringBuffer.toString();
  }
  
  static Name cosNameToName(NameComponent[] paramArrayOfNameComponent)
  {
    CompositeName localCompositeName = new CompositeName();
    for (int i = 0; (paramArrayOfNameComponent != null) && (i < paramArrayOfNameComponent.length); i++) {
      try
      {
        localCompositeName.add(stringifyComponent(paramArrayOfNameComponent[i]));
      }
      catch (InvalidNameException localInvalidNameException) {}
    }
    return localCompositeName;
  }
  
  private static Vector<String> insStringToStringifiedComps(String paramString)
    throws InvalidNameException
  {
    int i = paramString.length();
    Vector localVector = new Vector(10);
    char[] arrayOfChar1 = new char[i];
    char[] arrayOfChar2 = new char[i];
    int n = 0;
    while (n < i)
    {
      int k;
      int j = k = 0;
      int m = 1;
      while ((n < i) && (paramString.charAt(n) != '/')) {
        if (paramString.charAt(n) == '\\')
        {
          if (n + 1 >= i) {
            throw new InvalidNameException(paramString + ": unescaped \\ at end of component");
          }
          if (isMeta(paramString.charAt(n + 1)))
          {
            n++;
            if (m != 0) {
              arrayOfChar1[(j++)] = paramString.charAt(n++);
            } else {
              arrayOfChar2[(k++)] = paramString.charAt(n++);
            }
          }
          else
          {
            throw new InvalidNameException(paramString + ": invalid character being escaped");
          }
        }
        else if ((m != 0) && (paramString.charAt(n) == '.'))
        {
          n++;
          m = 0;
        }
        else if (m != 0)
        {
          arrayOfChar1[(j++)] = paramString.charAt(n++);
        }
        else
        {
          arrayOfChar2[(k++)] = paramString.charAt(n++);
        }
      }
      localVector.addElement(stringifyComponent(new NameComponent(new String(arrayOfChar1, 0, j), new String(arrayOfChar2, 0, k))));
      if (n < i) {
        n++;
      }
    }
    return localVector;
  }
  
  private static NameComponent parseComponent(String paramString)
    throws InvalidNameException
  {
    NameComponent localNameComponent = new NameComponent();
    int i = -1;
    int j = paramString.length();
    int k = 0;
    char[] arrayOfChar = new char[j];
    int m = 0;
    for (int n = 0; (n < j) && (i < 0); n++) {
      if (m != 0)
      {
        arrayOfChar[(k++)] = paramString.charAt(n);
        m = 0;
      }
      else if (paramString.charAt(n) == '\\')
      {
        if (n + 1 >= j) {
          throw new InvalidNameException(paramString + ": unescaped \\ at end of component");
        }
        if (isMeta(paramString.charAt(n + 1))) {
          m = 1;
        } else {
          throw new InvalidNameException(paramString + ": invalid character being escaped");
        }
      }
      else if (paramString.charAt(n) == '.')
      {
        i = n;
      }
      else
      {
        arrayOfChar[(k++)] = paramString.charAt(n);
      }
    }
    id = new String(arrayOfChar, 0, k);
    if (i < 0)
    {
      kind = "";
    }
    else
    {
      k = 0;
      m = 0;
      for (n = i + 1; n < j; n++) {
        if (m != 0)
        {
          arrayOfChar[(k++)] = paramString.charAt(n);
          m = 0;
        }
        else if (paramString.charAt(n) == '\\')
        {
          if (n + 1 >= j) {
            throw new InvalidNameException(paramString + ": unescaped \\ at end of component");
          }
          if (isMeta(paramString.charAt(n + 1))) {
            m = 1;
          } else {
            throw new InvalidNameException(paramString + ": invalid character being escaped");
          }
        }
        else
        {
          arrayOfChar[(k++)] = paramString.charAt(n);
        }
      }
      kind = new String(arrayOfChar, 0, k);
    }
    return localNameComponent;
  }
  
  private static String stringifyComponent(NameComponent paramNameComponent)
  {
    StringBuffer localStringBuffer = new StringBuffer(escape(id));
    if ((kind != null) && (!kind.equals(""))) {
      localStringBuffer.append('.' + escape(kind));
    }
    if (localStringBuffer.length() == 0) {
      return ".";
    }
    return localStringBuffer.toString();
  }
  
  private static String escape(String paramString)
  {
    if ((paramString.indexOf('.') < 0) && (paramString.indexOf('/') < 0) && (paramString.indexOf('\\') < 0)) {
      return paramString;
    }
    int i = paramString.length();
    int j = 0;
    char[] arrayOfChar = new char[i + i];
    for (int k = 0; k < i; k++)
    {
      if (isMeta(paramString.charAt(k))) {
        arrayOfChar[(j++)] = '\\';
      }
      arrayOfChar[(j++)] = paramString.charAt(k);
    }
    return new String(arrayOfChar, 0, j);
  }
  
  private static boolean isMeta(char paramChar)
  {
    switch (paramChar)
    {
    case '.': 
    case '/': 
    case '\\': 
      return true;
    }
    return false;
  }
  
  static
  {
    mySyntax.put("jndi.syntax.direction", "left_to_right");
    mySyntax.put("jndi.syntax.separator", "/");
    mySyntax.put("jndi.syntax.escape", "\\");
  }
  
  static final class CNCompoundName
    extends CompoundName
  {
    private static final long serialVersionUID = -6599252802678482317L;
    
    CNCompoundName(Enumeration<String> paramEnumeration)
    {
      super(CNNameParser.mySyntax);
    }
    
    public Object clone()
    {
      return new CNCompoundName(getAll());
    }
    
    public Name getPrefix(int paramInt)
    {
      Enumeration localEnumeration = super.getPrefix(paramInt).getAll();
      return new CNCompoundName(localEnumeration);
    }
    
    public Name getSuffix(int paramInt)
    {
      Enumeration localEnumeration = super.getSuffix(paramInt).getAll();
      return new CNCompoundName(localEnumeration);
    }
    
    public String toString()
    {
      try
      {
        return CNNameParser.cosNameToInsString(CNNameParser.nameToCosName(this));
      }
      catch (InvalidNameException localInvalidNameException) {}
      return super.toString();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\cosnaming\CNNameParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */