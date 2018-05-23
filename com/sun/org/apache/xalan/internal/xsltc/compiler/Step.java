package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ICONST;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.dtm.Axis;
import java.util.Vector;

final class Step
  extends RelativeLocationPath
{
  private int _axis;
  private Vector _predicates;
  private boolean _hadPredicates = false;
  private int _nodeType;
  
  public Step(int paramInt1, int paramInt2, Vector paramVector)
  {
    _axis = paramInt1;
    _nodeType = paramInt2;
    _predicates = paramVector;
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    if (_predicates != null)
    {
      int i = _predicates.size();
      for (int j = 0; j < i; j++)
      {
        Predicate localPredicate = (Predicate)_predicates.elementAt(j);
        localPredicate.setParser(paramParser);
        localPredicate.setParent(this);
      }
    }
  }
  
  public int getAxis()
  {
    return _axis;
  }
  
  public void setAxis(int paramInt)
  {
    _axis = paramInt;
  }
  
  public int getNodeType()
  {
    return _nodeType;
  }
  
  public Vector getPredicates()
  {
    return _predicates;
  }
  
  public void addPredicates(Vector paramVector)
  {
    if (_predicates == null) {
      _predicates = paramVector;
    } else {
      _predicates.addAll(paramVector);
    }
  }
  
  private boolean hasParentPattern()
  {
    SyntaxTreeNode localSyntaxTreeNode = getParent();
    return ((localSyntaxTreeNode instanceof ParentPattern)) || ((localSyntaxTreeNode instanceof ParentLocationPath)) || ((localSyntaxTreeNode instanceof UnionPathExpr)) || ((localSyntaxTreeNode instanceof FilterParentPath));
  }
  
  private boolean hasParentLocationPath()
  {
    return getParent() instanceof ParentLocationPath;
  }
  
  private boolean hasPredicates()
  {
    return (_predicates != null) && (_predicates.size() > 0);
  }
  
  private boolean isPredicate()
  {
    Object localObject = this;
    while (localObject != null)
    {
      localObject = ((SyntaxTreeNode)localObject).getParent();
      if ((localObject instanceof Predicate)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isAbbreviatedDot()
  {
    return (_nodeType == -1) && (_axis == 13);
  }
  
  public boolean isAbbreviatedDDot()
  {
    return (_nodeType == -1) && (_axis == 10);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    _hadPredicates = hasPredicates();
    if (isAbbreviatedDot()) {
      _type = ((hasParentPattern()) || (hasPredicates()) || (hasParentLocationPath()) ? Type.NodeSet : Type.Node);
    } else {
      _type = Type.NodeSet;
    }
    if (_predicates != null)
    {
      int i = _predicates.size();
      for (int j = 0; j < i; j++)
      {
        Expression localExpression = (Expression)_predicates.elementAt(j);
        localExpression.typeCheck(paramSymbolTable);
      }
    }
    return _type;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    translateStep(paramClassGenerator, paramMethodGenerator, hasPredicates() ? _predicates.size() - 1 : -1);
  }
  
  private void translateStep(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, int paramInt)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if (paramInt >= 0)
    {
      translatePredicates(paramClassGenerator, paramMethodGenerator, paramInt);
    }
    else
    {
      int i = 0;
      String str1 = null;
      XSLTC localXSLTC = getParser().getXSLTC();
      if (_nodeType >= 14)
      {
        Vector localVector = localXSLTC.getNamesIndex();
        str1 = (String)localVector.elementAt(_nodeType - 14);
        i = str1.lastIndexOf('*');
      }
      if ((_axis == 2) && (_nodeType != 2) && (_nodeType != -1) && (!hasParentPattern()) && (i == 0))
      {
        int j = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getTypedAxisIterator", "(II)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
        localInstructionList.append(paramMethodGenerator.loadDOM());
        localInstructionList.append(new PUSH(localConstantPoolGen, 2));
        localInstructionList.append(new PUSH(localConstantPoolGen, _nodeType));
        localInstructionList.append(new INVOKEINTERFACE(j, 3));
        return;
      }
      SyntaxTreeNode localSyntaxTreeNode = getParent();
      int k;
      if (isAbbreviatedDot())
      {
        if (_type == Type.Node)
        {
          localInstructionList.append(paramMethodGenerator.loadContextNode());
        }
        else if ((localSyntaxTreeNode instanceof ParentLocationPath))
        {
          k = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.SingletonIterator", "<init>", "(I)V");
          localInstructionList.append(new NEW(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.SingletonIterator")));
          localInstructionList.append(DUP);
          localInstructionList.append(paramMethodGenerator.loadContextNode());
          localInstructionList.append(new INVOKESPECIAL(k));
        }
        else
        {
          k = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getAxisIterator", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
          localInstructionList.append(paramMethodGenerator.loadDOM());
          localInstructionList.append(new PUSH(localConstantPoolGen, _axis));
          localInstructionList.append(new INVOKEINTERFACE(k, 2));
        }
        return;
      }
      if (((localSyntaxTreeNode instanceof ParentLocationPath)) && ((localSyntaxTreeNode.getParent() instanceof ParentLocationPath)) && (_nodeType == 1) && (!_hadPredicates)) {
        _nodeType = -1;
      }
      switch (_nodeType)
      {
      case 2: 
        _axis = 2;
      case -1: 
        k = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getAxisIterator", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
        localInstructionList.append(paramMethodGenerator.loadDOM());
        localInstructionList.append(new PUSH(localConstantPoolGen, _axis));
        localInstructionList.append(new INVOKEINTERFACE(k, 2));
        break;
      case 0: 
      default: 
        if (i > 1)
        {
          String str2;
          if (_axis == 2) {
            str2 = str1.substring(0, i - 2);
          } else {
            str2 = str1.substring(0, i - 1);
          }
          int n = localXSLTC.registerNamespace(str2);
          int i1 = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNamespaceAxisIterator", "(II)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
          localInstructionList.append(paramMethodGenerator.loadDOM());
          localInstructionList.append(new PUSH(localConstantPoolGen, _axis));
          localInstructionList.append(new PUSH(localConstantPoolGen, n));
          localInstructionList.append(new INVOKEINTERFACE(i1, 3));
        }
        break;
      }
      int m = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getTypedAxisIterator", "(II)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(new PUSH(localConstantPoolGen, _axis));
      localInstructionList.append(new PUSH(localConstantPoolGen, _nodeType));
      localInstructionList.append(new INVOKEINTERFACE(m, 3));
    }
  }
  
  public void translatePredicates(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, int paramInt)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = 0;
    if (paramInt < 0)
    {
      translateStep(paramClassGenerator, paramMethodGenerator, paramInt);
    }
    else
    {
      Predicate localPredicate = (Predicate)_predicates.get(paramInt--);
      Object localObject1;
      Object localObject2;
      if (localPredicate.isNodeValueTest())
      {
        localObject1 = localPredicate.getStep();
        localInstructionList.append(paramMethodGenerator.loadDOM());
        if (((Step)localObject1).isAbbreviatedDot())
        {
          translateStep(paramClassGenerator, paramMethodGenerator, paramInt);
          localInstructionList.append(new ICONST(0));
        }
        else
        {
          localObject2 = new ParentLocationPath(this, (Expression)localObject1);
          _parent = (_parent = localObject2);
          try
          {
            ((ParentLocationPath)localObject2).typeCheck(getParser().getSymbolTable());
          }
          catch (TypeCheckError localTypeCheckError) {}
          translateStep(paramClassGenerator, paramMethodGenerator, paramInt);
          ((ParentLocationPath)localObject2).translateStep(paramClassGenerator, paramMethodGenerator);
          localInstructionList.append(new ICONST(1));
        }
        localPredicate.translate(paramClassGenerator, paramMethodGenerator);
        i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeValueIterator", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;ILjava/lang/String;Z)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
        localInstructionList.append(new INVOKEINTERFACE(i, 5));
      }
      else if (localPredicate.isNthDescendant())
      {
        localInstructionList.append(paramMethodGenerator.loadDOM());
        localInstructionList.append(new PUSH(localConstantPoolGen, localPredicate.getPosType()));
        localPredicate.translate(paramClassGenerator, paramMethodGenerator);
        localInstructionList.append(new ICONST(0));
        i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNthDescendant", "(IIZ)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
        localInstructionList.append(new INVOKEINTERFACE(i, 4));
      }
      else if (localPredicate.isNthPositionFilter())
      {
        i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NthIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;I)V");
        translatePredicates(paramClassGenerator, paramMethodGenerator, paramInt);
        localObject1 = paramMethodGenerator.addLocalVariable("step_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
        ((LocalVariableGen)localObject1).setStart(localInstructionList.append(new ASTORE(((LocalVariableGen)localObject1).getIndex())));
        localPredicate.translate(paramClassGenerator, paramMethodGenerator);
        localObject2 = paramMethodGenerator.addLocalVariable("step_tmp2", Util.getJCRefType("I"), null, null);
        ((LocalVariableGen)localObject2).setStart(localInstructionList.append(new ISTORE(((LocalVariableGen)localObject2).getIndex())));
        localInstructionList.append(new NEW(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.NthIterator")));
        localInstructionList.append(DUP);
        ((LocalVariableGen)localObject1).setEnd(localInstructionList.append(new ALOAD(((LocalVariableGen)localObject1).getIndex())));
        ((LocalVariableGen)localObject2).setEnd(localInstructionList.append(new ILOAD(((LocalVariableGen)localObject2).getIndex())));
        localInstructionList.append(new INVOKESPECIAL(i));
      }
      else
      {
        i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xalan/internal/xsltc/dom/CurrentNodeListFilter;ILcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;)V");
        translatePredicates(paramClassGenerator, paramMethodGenerator, paramInt);
        localObject1 = paramMethodGenerator.addLocalVariable("step_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
        ((LocalVariableGen)localObject1).setStart(localInstructionList.append(new ASTORE(((LocalVariableGen)localObject1).getIndex())));
        localPredicate.translateFilter(paramClassGenerator, paramMethodGenerator);
        localObject2 = paramMethodGenerator.addLocalVariable("step_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/dom/CurrentNodeListFilter;"), null, null);
        ((LocalVariableGen)localObject2).setStart(localInstructionList.append(new ASTORE(((LocalVariableGen)localObject2).getIndex())));
        localInstructionList.append(new NEW(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListIterator")));
        localInstructionList.append(DUP);
        ((LocalVariableGen)localObject1).setEnd(localInstructionList.append(new ALOAD(((LocalVariableGen)localObject1).getIndex())));
        ((LocalVariableGen)localObject2).setEnd(localInstructionList.append(new ALOAD(((LocalVariableGen)localObject2).getIndex())));
        localInstructionList.append(paramMethodGenerator.loadCurrentNode());
        localInstructionList.append(paramClassGenerator.loadTranslet());
        if (paramClassGenerator.isExternal())
        {
          String str = paramClassGenerator.getClassName();
          localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass(str)));
        }
        localInstructionList.append(new INVOKESPECIAL(i));
      }
    }
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("step(\"");
    localStringBuffer.append(Axis.getNames(_axis)).append("\", ").append(_nodeType);
    if (_predicates != null)
    {
      int i = _predicates.size();
      for (int j = 0; j < i; j++)
      {
        Predicate localPredicate = (Predicate)_predicates.elementAt(j);
        localStringBuffer.append(", ").append(localPredicate.toString());
      }
    }
    return ')';
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Step.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */