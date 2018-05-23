package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

public abstract class Pattern
  extends Expression
{
  public Pattern() {}
  
  public abstract Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError;
  
  public abstract void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator);
  
  public abstract double getPriority();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Pattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */