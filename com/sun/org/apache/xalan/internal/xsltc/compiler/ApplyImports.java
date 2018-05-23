package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class ApplyImports
  extends Instruction
{
  private QName _modeName;
  private int _precedence;
  
  ApplyImports() {}
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("ApplyTemplates");
    indent(paramInt + 4);
    if (_modeName != null)
    {
      indent(paramInt + 4);
      Util.println("mode " + _modeName);
    }
  }
  
  public boolean hasWithParams()
  {
    return hasContents();
  }
  
  private int getMinPrecedence(int paramInt)
  {
    for (Stylesheet localStylesheet = getStylesheet(); _includedFrom != null; localStylesheet = _includedFrom) {}
    return localStylesheet.getMinimumDescendantPrecedence();
  }
  
  public void parseContents(Parser paramParser)
  {
    Stylesheet localStylesheet = getStylesheet();
    localStylesheet.setTemplateInlining(false);
    Template localTemplate = getTemplate();
    _modeName = localTemplate.getModeName();
    _precedence = localTemplate.getImportPrecedence();
    localStylesheet = paramParser.getTopLevelStylesheet();
    parseChildren(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    Stylesheet localStylesheet = paramClassGenerator.getStylesheet();
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = paramMethodGenerator.getLocalIndex("current");
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(paramMethodGenerator.loadIterator());
    localInstructionList.append(paramMethodGenerator.loadHandler());
    localInstructionList.append(paramMethodGenerator.loadCurrentNode());
    if (localStylesheet.hasLocalParams())
    {
      localInstructionList.append(paramClassGenerator.loadTranslet());
      j = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "pushParamFrame", "()V");
      localInstructionList.append(new INVOKEVIRTUAL(j));
    }
    int j = _precedence;
    int k = getMinPrecedence(j);
    Mode localMode = localStylesheet.getMode(_modeName);
    String str1 = localMode.functionName(k, j);
    String str2 = paramClassGenerator.getStylesheet().getClassName();
    String str3 = paramClassGenerator.getApplyTemplatesSigForImport();
    int m = localConstantPoolGen.addMethodref(str2, str1, str3);
    localInstructionList.append(new INVOKEVIRTUAL(m));
    if (localStylesheet.hasLocalParams())
    {
      localInstructionList.append(paramClassGenerator.loadTranslet());
      int n = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "popParamFrame", "()V");
      localInstructionList.append(new INVOKEVIRTUAL(n));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ApplyImports.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */