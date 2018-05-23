package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class FilteredAbsoluteLocationPath
  extends Expression
{
  private Expression _path;
  
  public FilteredAbsoluteLocationPath()
  {
    _path = null;
  }
  
  public FilteredAbsoluteLocationPath(Expression paramExpression)
  {
    _path = paramExpression;
    if (paramExpression != null) {
      _path.setParent(this);
    }
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    if (_path != null) {
      _path.setParser(paramParser);
    }
  }
  
  public Expression getPath()
  {
    return _path;
  }
  
  public String toString()
  {
    return "FilteredAbsoluteLocationPath(" + (_path != null ? _path.toString() : "null") + ')';
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (_path != null)
    {
      Type localType = _path.typeCheck(paramSymbolTable);
      if ((localType instanceof NodeType)) {
        _path = new CastExpr(_path, Type.NodeSet);
      }
    }
    return _type = Type.NodeSet;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i;
    if (_path != null)
    {
      i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.DupFilterIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)V");
      LocalVariableGen localLocalVariableGen = paramMethodGenerator.addLocalVariable("filtered_absolute_location_path_tmp", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
      _path.translate(paramClassGenerator, paramMethodGenerator);
      localLocalVariableGen.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen.getIndex())));
      localInstructionList.append(new NEW(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.DupFilterIterator")));
      localInstructionList.append(DUP);
      localLocalVariableGen.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen.getIndex())));
      localInstructionList.append(new INVOKESPECIAL(i));
    }
    else
    {
      i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getIterator", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(new INVOKEINTERFACE(i, 1));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\FilteredAbsoluteLocationPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */