package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.io.PrintStream;
import java.util.Vector;

final class CallTemplate
  extends Instruction
{
  private QName _name;
  private SyntaxTreeNode[] _parameters = null;
  private Template _calleeTemplate = null;
  
  CallTemplate() {}
  
  public void display(int paramInt)
  {
    indent(paramInt);
    System.out.print("CallTemplate");
    Util.println(" name " + _name);
    displayContents(paramInt + 4);
  }
  
  public boolean hasWithParams()
  {
    return elementCount() > 0;
  }
  
  public void parseContents(Parser paramParser)
  {
    String str = getAttribute("name");
    if (str.length() > 0)
    {
      if (!XML11Char.isXML11ValidQName(str))
      {
        ErrorMsg localErrorMsg = new ErrorMsg("INVALID_QNAME_ERR", str, this);
        paramParser.reportError(3, localErrorMsg);
      }
      _name = paramParser.getQNameIgnoreDefaultNs(str);
    }
    else
    {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "name");
    }
    parseChildren(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Template localTemplate = paramSymbolTable.lookupTemplate(_name);
    if (localTemplate != null)
    {
      typeCheckContents(paramSymbolTable);
    }
    else
    {
      ErrorMsg localErrorMsg = new ErrorMsg("TEMPLATE_UNDEF_ERR", _name, this);
      throw new TypeCheckError(localErrorMsg);
    }
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    Stylesheet localStylesheet = paramClassGenerator.getStylesheet();
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if ((localStylesheet.hasLocalParams()) || (hasContents()))
    {
      _calleeTemplate = getCalleeTemplate();
      if (_calleeTemplate != null)
      {
        buildParameterList();
      }
      else
      {
        int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "pushParamFrame", "()V");
        localInstructionList.append(paramClassGenerator.loadTranslet());
        localInstructionList.append(new INVOKEVIRTUAL(i));
        translateContents(paramClassGenerator, paramMethodGenerator);
      }
    }
    String str1 = localStylesheet.getClassName();
    String str2 = Util.escape(_name.toString());
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(paramMethodGenerator.loadIterator());
    localInstructionList.append(paramMethodGenerator.loadHandler());
    localInstructionList.append(paramMethodGenerator.loadCurrentNode());
    StringBuffer localStringBuffer = new StringBuffer("(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;I");
    int j;
    if (_calleeTemplate != null)
    {
      j = _parameters.length;
      for (int k = 0; k < j; k++)
      {
        SyntaxTreeNode localSyntaxTreeNode = _parameters[k];
        localStringBuffer.append("Ljava/lang/Object;");
        if ((localSyntaxTreeNode instanceof Param)) {
          localInstructionList.append(ACONST_NULL);
        } else {
          localSyntaxTreeNode.translate(paramClassGenerator, paramMethodGenerator);
        }
      }
    }
    localStringBuffer.append(")V");
    localInstructionList.append(new INVOKEVIRTUAL(localConstantPoolGen.addMethodref(str1, str2, localStringBuffer.toString())));
    if (_parameters != null) {
      for (j = 0; j < _parameters.length; j++) {
        if ((_parameters[j] instanceof WithParam)) {
          ((WithParam)_parameters[j]).releaseResultTree(paramClassGenerator, paramMethodGenerator);
        }
      }
    }
    if ((_calleeTemplate == null) && ((localStylesheet.hasLocalParams()) || (hasContents())))
    {
      j = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "popParamFrame", "()V");
      localInstructionList.append(paramClassGenerator.loadTranslet());
      localInstructionList.append(new INVOKEVIRTUAL(j));
    }
  }
  
  public Template getCalleeTemplate()
  {
    Template localTemplate = getXSLTC().getParser().getSymbolTable().lookupTemplate(_name);
    return localTemplate.isSimpleNamedTemplate() ? localTemplate : null;
  }
  
  private void buildParameterList()
  {
    Vector localVector = _calleeTemplate.getParameters();
    int i = localVector.size();
    _parameters = new SyntaxTreeNode[i];
    for (int j = 0; j < i; j++) {
      _parameters[j] = ((SyntaxTreeNode)localVector.elementAt(j));
    }
    j = elementCount();
    for (int k = 0; k < j; k++)
    {
      Object localObject = elementAt(k);
      if ((localObject instanceof WithParam))
      {
        WithParam localWithParam = (WithParam)localObject;
        QName localQName = localWithParam.getName();
        for (int m = 0; m < i; m++)
        {
          SyntaxTreeNode localSyntaxTreeNode = _parameters[m];
          if (((localSyntaxTreeNode instanceof Param)) && (((Param)localSyntaxTreeNode).getName().equals(localQName)))
          {
            localWithParam.setDoParameterOptimization(true);
            _parameters[m] = localWithParam;
            break;
          }
          if (((localSyntaxTreeNode instanceof WithParam)) && (((WithParam)localSyntaxTreeNode).getName().equals(localQName)))
          {
            localWithParam.setDoParameterOptimization(true);
            _parameters[m] = localWithParam;
            break;
          }
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\CallTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */