package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

public abstract class NumberType
  extends Type
{
  public NumberType() {}
  
  public boolean isNumber()
  {
    return true;
  }
  
  public boolean isSimple()
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\NumberType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */