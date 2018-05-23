package com.sun.corba.se.impl.orbutil.fsm;

import com.sun.corba.se.spi.orbutil.fsm.Action;
import com.sun.corba.se.spi.orbutil.fsm.Guard;
import com.sun.corba.se.spi.orbutil.fsm.Input;
import com.sun.corba.se.spi.orbutil.fsm.State;
import java.util.StringTokenizer;

public class NameBase
{
  private String name;
  private String toStringName;
  
  private String getClassName()
  {
    String str1 = getClass().getName();
    StringTokenizer localStringTokenizer = new StringTokenizer(str1, ".");
    for (String str2 = localStringTokenizer.nextToken(); localStringTokenizer.hasMoreTokens(); str2 = localStringTokenizer.nextToken()) {}
    return str2;
  }
  
  private String getPreferredClassName()
  {
    if ((this instanceof Action)) {
      return "Action";
    }
    if ((this instanceof State)) {
      return "State";
    }
    if ((this instanceof Guard)) {
      return "Guard";
    }
    if ((this instanceof Input)) {
      return "Input";
    }
    return getClassName();
  }
  
  public NameBase(String paramString)
  {
    name = paramString;
    toStringName = (getPreferredClassName() + "[" + paramString + "]");
  }
  
  public String getName()
  {
    return name;
  }
  
  public String toString()
  {
    return toStringName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\fsm\NameBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */