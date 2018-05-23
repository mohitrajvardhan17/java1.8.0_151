package com.sun.org.apache.xalan.internal.xsltc.compiler;

abstract class RelativeLocationPath
  extends Expression
{
  RelativeLocationPath() {}
  
  public abstract int getAxis();
  
  public abstract void setAxis(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\RelativeLocationPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */