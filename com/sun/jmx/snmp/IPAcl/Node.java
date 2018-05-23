package com.sun.jmx.snmp.IPAcl;

abstract interface Node
{
  public abstract void jjtOpen();
  
  public abstract void jjtClose();
  
  public abstract void jjtSetParent(Node paramNode);
  
  public abstract Node jjtGetParent();
  
  public abstract void jjtAddChild(Node paramNode, int paramInt);
  
  public abstract Node jjtGetChild(int paramInt);
  
  public abstract int jjtGetNumChildren();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\Node.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */