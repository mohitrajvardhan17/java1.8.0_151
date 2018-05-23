package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class LogicalExpr
  extends Expression
{
  public static final int OR = 0;
  public static final int AND = 1;
  private final int _op;
  private Expression _left;
  private Expression _right;
  private static final String[] Ops = { "or", "and" };
  
  public LogicalExpr(int paramInt, Expression paramExpression1, Expression paramExpression2)
  {
    _op = paramInt;
    (_left = paramExpression1).setParent(this);
    (_right = paramExpression2).setParent(this);
  }
  
  public boolean hasPositionCall()
  {
    return (_left.hasPositionCall()) || (_right.hasPositionCall());
  }
  
  public boolean hasLastCall()
  {
    return (_left.hasLastCall()) || (_right.hasLastCall());
  }
  
  public Object evaluateAtCompileTime()
  {
    Object localObject1 = _left.evaluateAtCompileTime();
    Object localObject2 = _right.evaluateAtCompileTime();
    if ((localObject1 == null) || (localObject2 == null)) {
      return null;
    }
    if (_op == 1) {
      return (localObject1 == Boolean.TRUE) && (localObject2 == Boolean.TRUE) ? Boolean.TRUE : Boolean.FALSE;
    }
    return (localObject1 == Boolean.TRUE) || (localObject2 == Boolean.TRUE) ? Boolean.TRUE : Boolean.FALSE;
  }
  
  public int getOp()
  {
    return _op;
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    _left.setParser(paramParser);
    _right.setParser(paramParser);
  }
  
  public String toString()
  {
    return Ops[_op] + '(' + _left + ", " + _right + ')';
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Type localType1 = _left.typeCheck(paramSymbolTable);
    Type localType2 = _right.typeCheck(paramSymbolTable);
    MethodType localMethodType1 = new MethodType(Type.Void, localType1, localType2);
    MethodType localMethodType2 = lookupPrimop(paramSymbolTable, Ops[_op], localMethodType1);
    if (localMethodType2 != null)
    {
      Type localType3 = (Type)localMethodType2.argsType().elementAt(0);
      if (!localType3.identicalTo(localType1)) {
        _left = new CastExpr(_left, localType3);
      }
      Type localType4 = (Type)localMethodType2.argsType().elementAt(1);
      if (!localType4.identicalTo(localType2)) {
        _right = new CastExpr(_right, localType3);
      }
      return _type = localMethodType2.resultType();
    }
    throw new TypeCheckError(this);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    translateDesynthesized(paramClassGenerator, paramMethodGenerator);
    synthesize(paramClassGenerator, paramMethodGenerator);
  }
  
  public void translateDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    SyntaxTreeNode localSyntaxTreeNode = getParent();
    Object localObject;
    if (_op == 1)
    {
      _left.translateDesynthesized(paramClassGenerator, paramMethodGenerator);
      localObject = localInstructionList.append(NOP);
      _right.translateDesynthesized(paramClassGenerator, paramMethodGenerator);
      InstructionHandle localInstructionHandle = localInstructionList.append(NOP);
      _falseList.append(_right._falseList.append(_left._falseList));
      if (((_left instanceof LogicalExpr)) && (((LogicalExpr)_left).getOp() == 0)) {
        _left.backPatchTrueList((InstructionHandle)localObject);
      } else if ((_left instanceof NotCall)) {
        _left.backPatchTrueList((InstructionHandle)localObject);
      } else {
        _trueList.append(_left._trueList);
      }
      if (((_right instanceof LogicalExpr)) && (((LogicalExpr)_right).getOp() == 0)) {
        _right.backPatchTrueList(localInstructionHandle);
      } else if ((_right instanceof NotCall)) {
        _right.backPatchTrueList(localInstructionHandle);
      } else {
        _trueList.append(_right._trueList);
      }
    }
    else
    {
      _left.translateDesynthesized(paramClassGenerator, paramMethodGenerator);
      localObject = localInstructionList.append(new GOTO(null));
      _right.translateDesynthesized(paramClassGenerator, paramMethodGenerator);
      _left._trueList.backPatch((InstructionHandle)localObject);
      _left._falseList.backPatch(((InstructionHandle)localObject).getNext());
      _falseList.append(_right._falseList);
      _trueList.add((InstructionHandle)localObject).append(_right._trueList);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\LogicalExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */