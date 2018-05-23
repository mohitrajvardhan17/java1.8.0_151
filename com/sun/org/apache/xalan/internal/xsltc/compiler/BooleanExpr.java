package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class BooleanExpr
  extends Expression
{
  private boolean _value;
  
  public BooleanExpr(boolean paramBoolean)
  {
    _value = paramBoolean;
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    _type = Type.Boolean;
    return _type;
  }
  
  public String toString()
  {
    return _value ? "true()" : "false()";
  }
  
  public boolean getValue()
  {
    return _value;
  }
  
  public boolean contextDependent()
  {
    return false;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(new PUSH(localConstantPoolGen, _value));
  }
  
  public void translateDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if (_value) {
      localInstructionList.append(NOP);
    } else {
      _falseList.add(localInstructionList.append(new GOTO(null)));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\BooleanExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */