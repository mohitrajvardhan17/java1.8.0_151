package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

final class AttributeValueTemplate
  extends AttributeValue
{
  static final int OUT_EXPR = 0;
  static final int IN_EXPR = 1;
  static final int IN_EXPR_SQUOTES = 2;
  static final int IN_EXPR_DQUOTES = 3;
  static final String DELIMITER = "￾";
  
  public AttributeValueTemplate(String paramString, Parser paramParser, SyntaxTreeNode paramSyntaxTreeNode)
  {
    setParent(paramSyntaxTreeNode);
    setParser(paramParser);
    try
    {
      parseAVTemplate(paramString, paramParser);
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      reportError(paramSyntaxTreeNode, paramParser, "ATTR_VAL_TEMPLATE_ERR", paramString);
    }
  }
  
  private void parseAVTemplate(String paramString, Parser paramParser)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "{}\"'", true);
    Object localObject = null;
    String str = null;
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    while (localStringTokenizer.hasMoreTokens())
    {
      if (str != null)
      {
        localObject = str;
        str = null;
      }
      else
      {
        localObject = localStringTokenizer.nextToken();
      }
      if (((String)localObject).length() == 1) {
        switch (((String)localObject).charAt(0))
        {
        case '{': 
          switch (i)
          {
          case 0: 
            str = localStringTokenizer.nextToken();
            if (str.equals("{"))
            {
              localStringBuffer.append(str);
              str = null;
            }
            else
            {
              localStringBuffer.append("￾");
              i = 1;
            }
            break;
          case 1: 
          case 2: 
          case 3: 
            reportError(getParent(), paramParser, "ATTR_VAL_TEMPLATE_ERR", paramString);
          }
          break;
        case '}': 
          switch (i)
          {
          case 0: 
            str = localStringTokenizer.nextToken();
            if (str.equals("}"))
            {
              localStringBuffer.append(str);
              str = null;
            }
            else
            {
              reportError(getParent(), paramParser, "ATTR_VAL_TEMPLATE_ERR", paramString);
            }
            break;
          case 1: 
            localStringBuffer.append("￾");
            i = 0;
            break;
          case 2: 
          case 3: 
            localStringBuffer.append((String)localObject);
          }
          break;
        case '\'': 
          switch (i)
          {
          case 1: 
            i = 2;
            break;
          case 2: 
            i = 1;
            break;
          }
          localStringBuffer.append((String)localObject);
          break;
        case '"': 
          switch (i)
          {
          case 1: 
            i = 3;
            break;
          case 3: 
            i = 1;
            break;
          }
          localStringBuffer.append((String)localObject);
          break;
        default: 
          localStringBuffer.append((String)localObject);
          break;
        }
      } else {
        localStringBuffer.append((String)localObject);
      }
    }
    if (i != 0) {
      reportError(getParent(), paramParser, "ATTR_VAL_TEMPLATE_ERR", paramString);
    }
    localStringTokenizer = new StringTokenizer(localStringBuffer.toString(), "￾", true);
    while (localStringTokenizer.hasMoreTokens())
    {
      localObject = localStringTokenizer.nextToken();
      if (((String)localObject).equals("￾"))
      {
        addElement(paramParser.parseExpression(this, localStringTokenizer.nextToken()));
        localStringTokenizer.nextToken();
      }
      else
      {
        addElement(new LiteralExpr((String)localObject));
      }
    }
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    List localList = getContents();
    int i = localList.size();
    for (int j = 0; j < i; j++)
    {
      Expression localExpression = (Expression)localList.get(j);
      if (!localExpression.typeCheck(paramSymbolTable).identicalTo(Type.String)) {
        localList.set(j, new CastExpr(localExpression, Type.String));
      }
    }
    return _type = Type.String;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("AVT:[");
    int i = elementCount();
    for (int j = 0; j < i; j++)
    {
      localStringBuffer.append(elementAt(j).toString());
      if (j < i - 1) {
        localStringBuffer.append(' ');
      }
    }
    return ']';
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    Object localObject;
    if (elementCount() == 1)
    {
      localObject = (Expression)elementAt(0);
      ((Expression)localObject).translate(paramClassGenerator, paramMethodGenerator);
    }
    else
    {
      localObject = paramClassGenerator.getConstantPool();
      InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
      int i = ((ConstantPoolGen)localObject).addMethodref("java.lang.StringBuffer", "<init>", "()V");
      INVOKEVIRTUAL localINVOKEVIRTUAL = new INVOKEVIRTUAL(((ConstantPoolGen)localObject).addMethodref("java.lang.StringBuffer", "append", "(Ljava/lang/String;)Ljava/lang/StringBuffer;"));
      int j = ((ConstantPoolGen)localObject).addMethodref("java.lang.StringBuffer", "toString", "()Ljava/lang/String;");
      localInstructionList.append(new NEW(((ConstantPoolGen)localObject).addClass("java.lang.StringBuffer")));
      localInstructionList.append(DUP);
      localInstructionList.append(new INVOKESPECIAL(i));
      Iterator localIterator = elements();
      while (localIterator.hasNext())
      {
        Expression localExpression = (Expression)localIterator.next();
        localExpression.translate(paramClassGenerator, paramMethodGenerator);
        localInstructionList.append(localINVOKEVIRTUAL);
      }
      localInstructionList.append(new INVOKEVIRTUAL(j));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\AttributeValueTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */