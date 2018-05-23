package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFNE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

abstract class IdKeyPattern
  extends LocationPathPattern
{
  protected RelativePathPattern _left = null;
  private String _index = null;
  private String _value = null;
  
  public IdKeyPattern(String paramString1, String paramString2)
  {
    _index = paramString1;
    _value = paramString2;
  }
  
  public String getIndexName()
  {
    return _index;
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    return Type.NodeSet;
  }
  
  public boolean isWildcard()
  {
    return false;
  }
  
  public void setLeft(RelativePathPattern paramRelativePathPattern)
  {
    _left = paramRelativePathPattern;
  }
  
  public StepPattern getKernelPattern()
  {
    return null;
  }
  
  public void reduceKernelPattern() {}
  
  public String toString()
  {
    return "id/keyPattern(" + _index + ", " + _value + ')';
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "getKeyIndex", "(Ljava/lang/String;)Lcom/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex;");
    int j = localConstantPoolGen.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex", "containsID", "(ILjava/lang/Object;)I");
    int k = localConstantPoolGen.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex", "containsKey", "(ILjava/lang/Object;)I");
    int m = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeIdent", "(I)I");
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(new PUSH(localConstantPoolGen, _index));
    localInstructionList.append(new INVOKEVIRTUAL(i));
    localInstructionList.append(SWAP);
    localInstructionList.append(new PUSH(localConstantPoolGen, _value));
    if ((this instanceof IdPattern)) {
      localInstructionList.append(new INVOKEVIRTUAL(j));
    } else {
      localInstructionList.append(new INVOKEVIRTUAL(k));
    }
    _trueList.add(localInstructionList.append(new IFNE(null)));
    _falseList.add(localInstructionList.append(new GOTO(null)));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\IdKeyPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */