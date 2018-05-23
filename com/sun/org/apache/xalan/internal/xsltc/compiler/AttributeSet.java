package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.AttributeSetMethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.util.Iterator;
import java.util.List;

final class AttributeSet
  extends TopLevelElement
{
  private static final String AttributeSetPrefix = "$as$";
  private QName _name;
  private UseAttributeSets _useSets;
  private AttributeSet _mergeSet;
  private String _method;
  private boolean _ignore = false;
  
  AttributeSet() {}
  
  public QName getName()
  {
    return _name;
  }
  
  public String getMethodName()
  {
    return _method;
  }
  
  public void ignore()
  {
    _ignore = true;
  }
  
  public void parseContents(Parser paramParser)
  {
    String str = getAttribute("name");
    if (!XML11Char.isXML11ValidQName(str))
    {
      localObject1 = new ErrorMsg("INVALID_QNAME_ERR", str, this);
      paramParser.reportError(3, (ErrorMsg)localObject1);
    }
    _name = paramParser.getQNameIgnoreDefaultNs(str);
    if ((_name == null) || (_name.equals("")))
    {
      localObject1 = new ErrorMsg("UNNAMED_ATTRIBSET_ERR", this);
      paramParser.reportError(3, (ErrorMsg)localObject1);
    }
    Object localObject1 = getAttribute("use-attribute-sets");
    if (((String)localObject1).length() > 0)
    {
      if (!Util.isValidQNames((String)localObject1))
      {
        localObject2 = new ErrorMsg("INVALID_QNAME_ERR", localObject1, this);
        paramParser.reportError(3, (ErrorMsg)localObject2);
      }
      _useSets = new UseAttributeSets((String)localObject1, paramParser);
    }
    Object localObject2 = getContents();
    int i = ((List)localObject2).size();
    for (int j = 0; j < i; j++)
    {
      SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)((List)localObject2).get(j);
      if ((localSyntaxTreeNode instanceof XslAttribute))
      {
        paramParser.getSymbolTable().setCurrentNode(localSyntaxTreeNode);
        localSyntaxTreeNode.parseContents(paramParser);
      }
      else if (!(localSyntaxTreeNode instanceof Text))
      {
        ErrorMsg localErrorMsg = new ErrorMsg("ILLEGAL_CHILD_ERR", this);
        paramParser.reportError(3, localErrorMsg);
      }
    }
    paramParser.getSymbolTable().setCurrentNode(this);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (_ignore) {
      return Type.Void;
    }
    _mergeSet = paramSymbolTable.addAttributeSet(this);
    _method = ("$as$" + getXSLTC().nextAttributeSetSerial());
    if (_useSets != null) {
      _useSets.typeCheck(paramSymbolTable);
    }
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    if (_ignore) {
      return;
    }
    paramMethodGenerator = new AttributeSetMethodGenerator(_method, paramClassGenerator);
    Object localObject3;
    if (_mergeSet != null)
    {
      localObject1 = paramClassGenerator.getConstantPool();
      localObject2 = paramMethodGenerator.getInstructionList();
      localObject3 = _mergeSet.getMethodName();
      ((InstructionList)localObject2).append(paramClassGenerator.loadTranslet());
      ((InstructionList)localObject2).append(paramMethodGenerator.loadDOM());
      ((InstructionList)localObject2).append(paramMethodGenerator.loadIterator());
      ((InstructionList)localObject2).append(paramMethodGenerator.loadHandler());
      ((InstructionList)localObject2).append(paramMethodGenerator.loadCurrentNode());
      int i = ((ConstantPoolGen)localObject1).addMethodref(paramClassGenerator.getClassName(), (String)localObject3, "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;I)V");
      ((InstructionList)localObject2).append(new INVOKESPECIAL(i));
    }
    if (_useSets != null) {
      _useSets.translate(paramClassGenerator, paramMethodGenerator);
    }
    Object localObject1 = elements();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (SyntaxTreeNode)((Iterator)localObject1).next();
      if ((localObject2 instanceof XslAttribute))
      {
        localObject3 = (XslAttribute)localObject2;
        ((XslAttribute)localObject3).translate(paramClassGenerator, paramMethodGenerator);
      }
    }
    Object localObject2 = paramMethodGenerator.getInstructionList();
    ((InstructionList)localObject2).append(RETURN);
    paramClassGenerator.addMethod(paramMethodGenerator);
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("attribute-set: ");
    Iterator localIterator = elements();
    while (localIterator.hasNext())
    {
      XslAttribute localXslAttribute = (XslAttribute)localIterator.next();
      localStringBuffer.append(localXslAttribute);
    }
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\AttributeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */