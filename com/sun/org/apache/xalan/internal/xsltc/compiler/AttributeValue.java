package com.sun.org.apache.xalan.internal.xsltc.compiler;

abstract class AttributeValue
  extends Expression
{
  AttributeValue() {}
  
  public static final AttributeValue create(SyntaxTreeNode paramSyntaxTreeNode, String paramString, Parser paramParser)
  {
    Object localObject;
    if (paramString.indexOf('{') != -1)
    {
      localObject = new AttributeValueTemplate(paramString, paramParser, paramSyntaxTreeNode);
    }
    else if (paramString.indexOf('}') != -1)
    {
      localObject = new AttributeValueTemplate(paramString, paramParser, paramSyntaxTreeNode);
    }
    else
    {
      localObject = new SimpleAttributeValue(paramString);
      ((AttributeValue)localObject).setParser(paramParser);
      ((AttributeValue)localObject).setParent(paramSyntaxTreeNode);
    }
    return (AttributeValue)localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\AttributeValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */