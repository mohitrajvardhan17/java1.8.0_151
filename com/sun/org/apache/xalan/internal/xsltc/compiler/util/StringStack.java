package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.Stack;

public final class StringStack
  extends Stack
{
  static final long serialVersionUID = -1506910875640317898L;
  
  public StringStack() {}
  
  public String peekString()
  {
    return (String)super.peek();
  }
  
  public String popString()
  {
    return (String)super.pop();
  }
  
  public String pushString(String paramString)
  {
    return (String)super.push(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\StringStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */