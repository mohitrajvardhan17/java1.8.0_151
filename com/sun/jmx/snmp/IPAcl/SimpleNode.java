package com.sun.jmx.snmp.IPAcl;

import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Vector;

class SimpleNode
  implements Node
{
  protected Node parent;
  protected Node[] children;
  protected int id;
  protected Parser parser;
  
  public SimpleNode(int paramInt)
  {
    id = paramInt;
  }
  
  public SimpleNode(Parser paramParser, int paramInt)
  {
    this(paramInt);
    parser = paramParser;
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new SimpleNode(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new SimpleNode(paramParser, paramInt);
  }
  
  public void jjtOpen() {}
  
  public void jjtClose() {}
  
  public void jjtSetParent(Node paramNode)
  {
    parent = paramNode;
  }
  
  public Node jjtGetParent()
  {
    return parent;
  }
  
  public void jjtAddChild(Node paramNode, int paramInt)
  {
    if (children == null)
    {
      children = new Node[paramInt + 1];
    }
    else if (paramInt >= children.length)
    {
      Node[] arrayOfNode = new Node[paramInt + 1];
      System.arraycopy(children, 0, arrayOfNode, 0, children.length);
      children = arrayOfNode;
    }
    children[paramInt] = paramNode;
  }
  
  public Node jjtGetChild(int paramInt)
  {
    return children[paramInt];
  }
  
  public int jjtGetNumChildren()
  {
    return children == null ? 0 : children.length;
  }
  
  public void buildTrapEntries(Hashtable<InetAddress, Vector<String>> paramHashtable)
  {
    if (children != null) {
      for (int i = 0; i < children.length; i++)
      {
        SimpleNode localSimpleNode = (SimpleNode)children[i];
        if (localSimpleNode != null) {
          localSimpleNode.buildTrapEntries(paramHashtable);
        }
      }
    }
  }
  
  public void buildInformEntries(Hashtable<InetAddress, Vector<String>> paramHashtable)
  {
    if (children != null) {
      for (int i = 0; i < children.length; i++)
      {
        SimpleNode localSimpleNode = (SimpleNode)children[i];
        if (localSimpleNode != null) {
          localSimpleNode.buildInformEntries(paramHashtable);
        }
      }
    }
  }
  
  public void buildAclEntries(PrincipalImpl paramPrincipalImpl, AclImpl paramAclImpl)
  {
    if (children != null) {
      for (int i = 0; i < children.length; i++)
      {
        SimpleNode localSimpleNode = (SimpleNode)children[i];
        if (localSimpleNode != null) {
          localSimpleNode.buildAclEntries(paramPrincipalImpl, paramAclImpl);
        }
      }
    }
  }
  
  public String toString()
  {
    return ParserTreeConstants.jjtNodeName[id];
  }
  
  public String toString(String paramString)
  {
    return paramString + toString();
  }
  
  public void dump(String paramString)
  {
    if (children != null) {
      for (int i = 0; i < children.length; i++)
      {
        SimpleNode localSimpleNode = (SimpleNode)children[i];
        if (localSimpleNode != null) {
          localSimpleNode.dump(paramString + " ");
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\SimpleNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */