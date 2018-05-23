package com.sun.org.apache.xpath.internal;

import javax.xml.transform.SourceLocator;

public abstract interface ExpressionNode
  extends SourceLocator
{
  public abstract void exprSetParent(ExpressionNode paramExpressionNode);
  
  public abstract ExpressionNode exprGetParent();
  
  public abstract void exprAddChild(ExpressionNode paramExpressionNode, int paramInt);
  
  public abstract ExpressionNode exprGetChild(int paramInt);
  
  public abstract int exprGetNumChildren();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\ExpressionNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */