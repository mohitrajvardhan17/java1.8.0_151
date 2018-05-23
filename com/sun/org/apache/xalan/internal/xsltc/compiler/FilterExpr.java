package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.Vector;

class FilterExpr
  extends Expression
{
  private Expression _primary;
  private final Vector _predicates;
  
  public FilterExpr(Expression paramExpression, Vector paramVector)
  {
    _primary = paramExpression;
    _predicates = paramVector;
    paramExpression.setParent(this);
  }
  
  protected Expression getExpr()
  {
    if ((_primary instanceof CastExpr)) {
      return ((CastExpr)_primary).getExpr();
    }
    return _primary;
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    _primary.setParser(paramParser);
    if (_predicates != null)
    {
      int i = _predicates.size();
      for (int j = 0; j < i; j++)
      {
        Expression localExpression = (Expression)_predicates.elementAt(j);
        localExpression.setParser(paramParser);
        localExpression.setParent(this);
      }
    }
  }
  
  public String toString()
  {
    return "filter-expr(" + _primary + ", " + _predicates + ")";
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Type localType = _primary.typeCheck(paramSymbolTable);
    boolean bool = _primary instanceof KeyCall;
    if (!(localType instanceof NodeSetType)) {
      if ((localType instanceof ReferenceType)) {
        _primary = new CastExpr(_primary, Type.NodeSet);
      } else {
        throw new TypeCheckError(this);
      }
    }
    int i = _predicates.size();
    for (int j = 0; j < i; j++)
    {
      Predicate localPredicate = (Predicate)_predicates.elementAt(j);
      if (!bool) {
        localPredicate.dontOptimize();
      }
      localPredicate.typeCheck(paramSymbolTable);
    }
    return _type = Type.NodeSet;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    translateFilterExpr(paramClassGenerator, paramMethodGenerator, _predicates == null ? -1 : _predicates.size() - 1);
  }
  
  private void translateFilterExpr(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, int paramInt)
  {
    if (paramInt >= 0) {
      translatePredicates(paramClassGenerator, paramMethodGenerator, paramInt);
    } else {
      _primary.translate(paramClassGenerator, paramMethodGenerator);
    }
  }
  
  public void translatePredicates(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, int paramInt)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if (paramInt < 0)
    {
      translateFilterExpr(paramClassGenerator, paramMethodGenerator, paramInt);
    }
    else
    {
      Predicate localPredicate = (Predicate)_predicates.get(paramInt--);
      translatePredicates(paramClassGenerator, paramMethodGenerator, paramInt);
      int i;
      LocalVariableGen localLocalVariableGen1;
      LocalVariableGen localLocalVariableGen2;
      if (localPredicate.isNthPositionFilter())
      {
        i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NthIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;I)V");
        localLocalVariableGen1 = paramMethodGenerator.addLocalVariable("filter_expr_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
        localLocalVariableGen1.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen1.getIndex())));
        localPredicate.translate(paramClassGenerator, paramMethodGenerator);
        localLocalVariableGen2 = paramMethodGenerator.addLocalVariable("filter_expr_tmp2", Util.getJCRefType("I"), null, null);
        localLocalVariableGen2.setStart(localInstructionList.append(new ISTORE(localLocalVariableGen2.getIndex())));
        localInstructionList.append(new NEW(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.NthIterator")));
        localInstructionList.append(DUP);
        localLocalVariableGen1.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen1.getIndex())));
        localLocalVariableGen2.setEnd(localInstructionList.append(new ILOAD(localLocalVariableGen2.getIndex())));
        localInstructionList.append(new INVOKESPECIAL(i));
      }
      else
      {
        i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;ZLcom/sun/org/apache/xalan/internal/xsltc/dom/CurrentNodeListFilter;ILcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;)V");
        localLocalVariableGen1 = paramMethodGenerator.addLocalVariable("filter_expr_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
        localLocalVariableGen1.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen1.getIndex())));
        localPredicate.translate(paramClassGenerator, paramMethodGenerator);
        localLocalVariableGen2 = paramMethodGenerator.addLocalVariable("filter_expr_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/dom/CurrentNodeListFilter;"), null, null);
        localLocalVariableGen2.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen2.getIndex())));
        localInstructionList.append(new NEW(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListIterator")));
        localInstructionList.append(DUP);
        localLocalVariableGen1.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen1.getIndex())));
        localInstructionList.append(ICONST_1);
        localLocalVariableGen2.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen2.getIndex())));
        localInstructionList.append(paramMethodGenerator.loadCurrentNode());
        localInstructionList.append(paramClassGenerator.loadTranslet());
        localInstructionList.append(new INVOKESPECIAL(i));
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\FilterExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */