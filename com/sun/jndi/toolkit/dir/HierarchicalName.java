package com.sun.jndi.toolkit.dir;

import java.util.Enumeration;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Properties;
import javax.naming.CompoundName;
import javax.naming.InvalidNameException;
import javax.naming.Name;

final class HierarchicalName
  extends CompoundName
{
  private int hashValue = -1;
  private static final long serialVersionUID = -6717336834584573168L;
  
  HierarchicalName()
  {
    super(new Enumeration()
    {
      public boolean hasMoreElements()
      {
        return false;
      }
      
      public String nextElement()
      {
        throw new NoSuchElementException();
      }
    }, HierarchicalNameParser.mySyntax);
  }
  
  HierarchicalName(Enumeration<String> paramEnumeration, Properties paramProperties)
  {
    super(paramEnumeration, paramProperties);
  }
  
  HierarchicalName(String paramString, Properties paramProperties)
    throws InvalidNameException
  {
    super(paramString, paramProperties);
  }
  
  public int hashCode()
  {
    if (hashValue == -1)
    {
      String str = toString().toUpperCase(Locale.ENGLISH);
      int i = str.length();
      int j = 0;
      char[] arrayOfChar = new char[i];
      str.getChars(0, i, arrayOfChar, 0);
      for (int k = i; k > 0; k--) {
        hashValue = (hashValue * 37 + arrayOfChar[(j++)]);
      }
    }
    return hashValue;
  }
  
  public Name getPrefix(int paramInt)
  {
    Enumeration localEnumeration = super.getPrefix(paramInt).getAll();
    return new HierarchicalName(localEnumeration, mySyntax);
  }
  
  public Name getSuffix(int paramInt)
  {
    Enumeration localEnumeration = super.getSuffix(paramInt).getAll();
    return new HierarchicalName(localEnumeration, mySyntax);
  }
  
  public Object clone()
  {
    return new HierarchicalName(getAll(), mySyntax);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\toolkit\dir\HierarchicalName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */