package com.sun.jmx.snmp.IPAcl;

import java.util.Stack;

class JJTParserState
{
  private Stack<Node> nodes = new Stack();
  private Stack<Integer> marks = new Stack();
  private int sp = 0;
  private int mk = 0;
  private boolean node_created;
  
  JJTParserState() {}
  
  boolean nodeCreated()
  {
    return node_created;
  }
  
  void reset()
  {
    nodes.removeAllElements();
    marks.removeAllElements();
    sp = 0;
    mk = 0;
  }
  
  Node rootNode()
  {
    return (Node)nodes.elementAt(0);
  }
  
  void pushNode(Node paramNode)
  {
    nodes.push(paramNode);
    sp += 1;
  }
  
  Node popNode()
  {
    if (--sp < mk) {
      mk = ((Integer)marks.pop()).intValue();
    }
    return (Node)nodes.pop();
  }
  
  Node peekNode()
  {
    return (Node)nodes.peek();
  }
  
  int nodeArity()
  {
    return sp - mk;
  }
  
  void clearNodeScope(Node paramNode)
  {
    while (sp > mk) {
      popNode();
    }
    mk = ((Integer)marks.pop()).intValue();
  }
  
  void openNodeScope(Node paramNode)
  {
    marks.push(new Integer(mk));
    mk = sp;
    paramNode.jjtOpen();
  }
  
  void closeNodeScope(Node paramNode, int paramInt)
  {
    mk = ((Integer)marks.pop()).intValue();
    while (paramInt-- > 0)
    {
      Node localNode = popNode();
      localNode.jjtSetParent(paramNode);
      paramNode.jjtAddChild(localNode, paramInt);
    }
    paramNode.jjtClose();
    pushNode(paramNode);
    node_created = true;
  }
  
  void closeNodeScope(Node paramNode, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      int i = nodeArity();
      mk = ((Integer)marks.pop()).intValue();
      while (i-- > 0)
      {
        Node localNode = popNode();
        localNode.jjtSetParent(paramNode);
        paramNode.jjtAddChild(localNode, i);
      }
      paramNode.jjtClose();
      pushNode(paramNode);
      node_created = true;
    }
    else
    {
      mk = ((Integer)marks.pop()).intValue();
      node_created = false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\JJTParserState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */