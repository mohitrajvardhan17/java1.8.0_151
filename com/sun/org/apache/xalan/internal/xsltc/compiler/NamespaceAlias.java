package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class NamespaceAlias
  extends TopLevelElement
{
  private String sPrefix;
  private String rPrefix;
  
  NamespaceAlias() {}
  
  public void parseContents(Parser paramParser)
  {
    sPrefix = getAttribute("stylesheet-prefix");
    rPrefix = getAttribute("result-prefix");
    paramParser.getSymbolTable().addPrefixAlias(sPrefix, rPrefix);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\NamespaceAlias.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */