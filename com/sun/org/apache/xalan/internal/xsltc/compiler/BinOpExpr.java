package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class BinOpExpr
  extends Expression
{
  public static final int PLUS = 0;
  public static final int MINUS = 1;
  public static final int TIMES = 2;
  public static final int DIV = 3;
  public static final int MOD = 4;
  private static final String[] Ops = { "+", "-", "*", "/", "%" };
  private int _op;
  private Expression _left;
  private Expression _right;
  
  public BinOpExpr(int paramInt, Expression paramExpression1, Expression paramExpression2)
  {
    _op = paramInt;
    (_left = paramExpression1).setParent(this);
    (_right = paramExpression2).setParent(this);
  }
  
  public boolean hasPositionCall()
  {
    if (_left.hasPositionCall()) {
      return true;
    }
    return _right.hasPositionCall();
  }
  
  public boolean hasLastCall()
  {
    return (_left.hasLastCall()) || (_right.hasLastCall());
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    _left.setParser(paramParser);
    _right.setParser(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Type localType1 = _left.typeCheck(paramSymbolTable);
    Type localType2 = _right.typeCheck(paramSymbolTable);
    MethodType localMethodType = lookupPrimop(paramSymbolTable, Ops[_op], new MethodType(Type.Void, localType1, localType2));
    if (localMethodType != null)
    {
      Type localType3 = (Type)localMethodType.argsType().elementAt(0);
      if (!localType3.identicalTo(localType1)) {
        _left = new CastExpr(_left, localType3);
      }
      Type localType4 = (Type)localMethodType.argsType().elementAt(1);
      if (!localType4.identicalTo(localType2)) {
        _right = new CastExpr(_right, localType3);
      }
      return _type = localMethodType.resultType();
    }
    throw new TypeCheckError(this);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    _left.translate(paramClassGenerator, paramMethodGenerator);
    _right.translate(paramClassGenerator, paramMethodGenerator);
    switch (_op)
    {
    case 0: 
      localInstructionList.append(_type.ADD());
      break;
    case 1: 
      localInstructionList.append(_type.SUB());
      break;
    case 2: 
      localInstructionList.append(_type.MUL());
      break;
    case 3: 
      localInstructionList.append(_type.DIV());
      break;
    case 4: 
      localInstructionList.append(_type.REM());
      break;
    default: 
      ErrorMsg localErrorMsg = new ErrorMsg("ILLEGAL_BINARY_OP_ERR", this);
      getParser().reportError(3, localErrorMsg);
    }
  }
  
  public String toString()
  {
    return Ops[_op] + '(' + _left + ", " + _right + ')';
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\BinOpExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */