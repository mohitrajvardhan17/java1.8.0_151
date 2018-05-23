package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.io.PrintStream;

final class If
  extends Instruction
{
  private Expression _test;
  private boolean _ignore = false;
  
  If() {}
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("If");
    indent(paramInt + 4);
    System.out.print("test ");
    Util.println(_test.toString());
    displayContents(paramInt + 4);
  }
  
  public void parseContents(Parser paramParser)
  {
    _test = paramParser.parseExpression(this, "test", null);
    if (_test.isDummy())
    {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "test");
      return;
    }
    Object localObject = _test.evaluateAtCompileTime();
    if ((localObject != null) && ((localObject instanceof Boolean))) {
      _ignore = (!((Boolean)localObject).booleanValue());
    }
    parseChildren(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (!(_test.typeCheck(paramSymbolTable) instanceof BooleanType)) {
      _test = new CastExpr(_test, Type.Boolean);
    }
    if (!_ignore) {
      typeCheckContents(paramSymbolTable);
    }
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    _test.translateDesynthesized(paramClassGenerator, paramMethodGenerator);
    InstructionHandle localInstructionHandle = localInstructionList.getEnd();
    if (!_ignore) {
      translateContents(paramClassGenerator, paramMethodGenerator);
    }
    _test.backPatchFalseList(localInstructionList.append(NOP));
    _test.backPatchTrueList(localInstructionHandle.getNext());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\If.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */