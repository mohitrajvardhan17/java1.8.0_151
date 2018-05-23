package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IFNE;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPEQ;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPNE;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NumberType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.RealType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Operators;

final class EqualityExpr
  extends Expression
{
  private final int _op;
  private Expression _left;
  private Expression _right;
  
  public EqualityExpr(int paramInt, Expression paramExpression1, Expression paramExpression2)
  {
    _op = paramInt;
    (_left = paramExpression1).setParent(this);
    (_right = paramExpression2).setParent(this);
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    _left.setParser(paramParser);
    _right.setParser(paramParser);
  }
  
  public String toString()
  {
    return Operators.getOpNames(_op) + '(' + _left + ", " + _right + ')';
  }
  
  public Expression getLeft()
  {
    return _left;
  }
  
  public Expression getRight()
  {
    return _right;
  }
  
  public boolean getOp()
  {
    return _op != 1;
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
    if (_left.hasLastCall()) {
      return true;
    }
    return _right.hasLastCall();
  }
  
  private void swapArguments()
  {
    Expression localExpression = _left;
    _left = _right;
    _right = localExpression;
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Type localType1 = _left.typeCheck(paramSymbolTable);
    Type localType2 = _right.typeCheck(paramSymbolTable);
    if ((localType1.isSimple()) && (localType2.isSimple()))
    {
      if (localType1 != localType2) {
        if ((localType1 instanceof BooleanType))
        {
          _right = new CastExpr(_right, Type.Boolean);
        }
        else if ((localType2 instanceof BooleanType))
        {
          _left = new CastExpr(_left, Type.Boolean);
        }
        else if (((localType1 instanceof NumberType)) || ((localType2 instanceof NumberType)))
        {
          _left = new CastExpr(_left, Type.Real);
          _right = new CastExpr(_right, Type.Real);
        }
        else
        {
          _left = new CastExpr(_left, Type.String);
          _right = new CastExpr(_right, Type.String);
        }
      }
    }
    else if ((localType1 instanceof ReferenceType))
    {
      _right = new CastExpr(_right, Type.Reference);
    }
    else if ((localType2 instanceof ReferenceType))
    {
      _left = new CastExpr(_left, Type.Reference);
    }
    else if (((localType1 instanceof NodeType)) && (localType2 == Type.String))
    {
      _left = new CastExpr(_left, Type.String);
    }
    else if ((localType1 == Type.String) && ((localType2 instanceof NodeType)))
    {
      _right = new CastExpr(_right, Type.String);
    }
    else if (((localType1 instanceof NodeType)) && ((localType2 instanceof NodeType)))
    {
      _left = new CastExpr(_left, Type.String);
      _right = new CastExpr(_right, Type.String);
    }
    else if ((!(localType1 instanceof NodeType)) || (!(localType2 instanceof NodeSetType)))
    {
      if (((localType1 instanceof NodeSetType)) && ((localType2 instanceof NodeType)))
      {
        swapArguments();
      }
      else
      {
        if ((localType1 instanceof NodeType)) {
          _left = new CastExpr(_left, Type.NodeSet);
        }
        if ((localType2 instanceof NodeType)) {
          _right = new CastExpr(_right, Type.NodeSet);
        }
        if ((localType1.isSimple()) || (((localType1 instanceof ResultTreeType)) && ((localType2 instanceof NodeSetType)))) {
          swapArguments();
        }
        if ((_right.getType() instanceof IntType)) {
          _right = new CastExpr(_right, Type.Real);
        }
      }
    }
    return _type = Type.Boolean;
  }
  
  public void translateDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    Type localType = _left.getType();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if ((localType instanceof BooleanType))
    {
      _left.translate(paramClassGenerator, paramMethodGenerator);
      _right.translate(paramClassGenerator, paramMethodGenerator);
      _falseList.add(localInstructionList.append(_op == 0 ? new IF_ICMPNE(null) : new IF_ICMPEQ(null)));
    }
    else if ((localType instanceof NumberType))
    {
      _left.translate(paramClassGenerator, paramMethodGenerator);
      _right.translate(paramClassGenerator, paramMethodGenerator);
      if ((localType instanceof RealType))
      {
        localInstructionList.append(DCMPG);
        _falseList.add(localInstructionList.append(_op == 0 ? new IFNE(null) : new IFEQ(null)));
      }
      else
      {
        _falseList.add(localInstructionList.append(_op == 0 ? new IF_ICMPNE(null) : new IF_ICMPEQ(null)));
      }
    }
    else
    {
      translate(paramClassGenerator, paramMethodGenerator);
      desynthesize(paramClassGenerator, paramMethodGenerator);
    }
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    Type localType1 = _left.getType();
    Type localType2 = _right.getType();
    if (((localType1 instanceof BooleanType)) || ((localType1 instanceof NumberType)))
    {
      translateDesynthesized(paramClassGenerator, paramMethodGenerator);
      synthesize(paramClassGenerator, paramMethodGenerator);
      return;
    }
    if ((localType1 instanceof StringType))
    {
      int i = localConstantPoolGen.addMethodref("java.lang.String", "equals", "(Ljava/lang/Object;)Z");
      _left.translate(paramClassGenerator, paramMethodGenerator);
      _right.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(new INVOKEVIRTUAL(i));
      if (_op == 1)
      {
        localInstructionList.append(ICONST_1);
        localInstructionList.append(IXOR);
      }
      return;
    }
    if ((localType1 instanceof ResultTreeType))
    {
      if ((localType2 instanceof BooleanType))
      {
        _right.translate(paramClassGenerator, paramMethodGenerator);
        if (_op == 1)
        {
          localInstructionList.append(ICONST_1);
          localInstructionList.append(IXOR);
        }
        return;
      }
      if ((localType2 instanceof RealType))
      {
        _left.translate(paramClassGenerator, paramMethodGenerator);
        localType1.translateTo(paramClassGenerator, paramMethodGenerator, Type.Real);
        _right.translate(paramClassGenerator, paramMethodGenerator);
        localInstructionList.append(DCMPG);
        BranchHandle localBranchHandle2 = localInstructionList.append(_op == 0 ? new IFNE(null) : new IFEQ(null));
        localInstructionList.append(ICONST_1);
        BranchHandle localBranchHandle1 = localInstructionList.append(new GOTO(null));
        localBranchHandle2.setTarget(localInstructionList.append(ICONST_0));
        localBranchHandle1.setTarget(localInstructionList.append(NOP));
        return;
      }
      _left.translate(paramClassGenerator, paramMethodGenerator);
      localType1.translateTo(paramClassGenerator, paramMethodGenerator, Type.String);
      _right.translate(paramClassGenerator, paramMethodGenerator);
      if ((localType2 instanceof ResultTreeType)) {
        localType2.translateTo(paramClassGenerator, paramMethodGenerator, Type.String);
      }
      j = localConstantPoolGen.addMethodref("java.lang.String", "equals", "(Ljava/lang/Object;)Z");
      localInstructionList.append(new INVOKEVIRTUAL(j));
      if (_op == 1)
      {
        localInstructionList.append(ICONST_1);
        localInstructionList.append(IXOR);
      }
      return;
    }
    if (((localType1 instanceof NodeSetType)) && ((localType2 instanceof BooleanType)))
    {
      _left.translate(paramClassGenerator, paramMethodGenerator);
      _left.startIterator(paramClassGenerator, paramMethodGenerator);
      Type.NodeSet.translateTo(paramClassGenerator, paramMethodGenerator, Type.Boolean);
      _right.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(IXOR);
      if (_op == 0)
      {
        localInstructionList.append(ICONST_1);
        localInstructionList.append(IXOR);
      }
      return;
    }
    if (((localType1 instanceof NodeSetType)) && ((localType2 instanceof StringType)))
    {
      _left.translate(paramClassGenerator, paramMethodGenerator);
      _left.startIterator(paramClassGenerator, paramMethodGenerator);
      _right.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(new PUSH(localConstantPoolGen, _op));
      localInstructionList.append(paramMethodGenerator.loadDOM());
      j = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "compare", "(" + localType1.toSignature() + localType2.toSignature() + "I" + "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;" + ")Z");
      localInstructionList.append(new INVOKESTATIC(j));
      return;
    }
    _left.translate(paramClassGenerator, paramMethodGenerator);
    _left.startIterator(paramClassGenerator, paramMethodGenerator);
    _right.translate(paramClassGenerator, paramMethodGenerator);
    _right.startIterator(paramClassGenerator, paramMethodGenerator);
    if ((localType2 instanceof ResultTreeType))
    {
      localType2.translateTo(paramClassGenerator, paramMethodGenerator, Type.String);
      localType2 = Type.String;
    }
    localInstructionList.append(new PUSH(localConstantPoolGen, _op));
    localInstructionList.append(paramMethodGenerator.loadDOM());
    int j = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "compare", "(" + localType1.toSignature() + localType2.toSignature() + "I" + "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;" + ")Z");
    localInstructionList.append(new INVOKESTATIC(j));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\EqualityExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */