package com.sun.org.apache.xalan.internal.xsltc.compiler;

public abstract interface Closure
{
  public abstract boolean inInnerClass();
  
  public abstract Closure getParentClosure();
  
  public abstract String getInnerClassName();
  
  public abstract void addVariable(VariableRefBase paramVariableRefBase);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Closure.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */