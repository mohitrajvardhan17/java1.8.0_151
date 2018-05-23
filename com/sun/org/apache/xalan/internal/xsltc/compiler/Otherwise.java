package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class Otherwise
  extends Instruction
{
  Otherwise() {}
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("Otherwise");
    indent(paramInt + 4);
    displayContents(paramInt + 4);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    Parser localParser = getParser();
    ErrorMsg localErrorMsg = new ErrorMsg("STRAY_OTHERWISE_ERR", this);
    localParser.reportError(3, localErrorMsg);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Otherwise.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */