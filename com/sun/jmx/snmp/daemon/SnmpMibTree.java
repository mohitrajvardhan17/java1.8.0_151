package com.sun.jmx.snmp.daemon;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import java.util.Enumeration;
import java.util.Vector;

final class SnmpMibTree
{
  private SnmpMibAgent defaultAgent = null;
  private TreeNode root = new TreeNode(-1L, null, null, null);
  
  public SnmpMibTree() {}
  
  public void setDefaultAgent(SnmpMibAgent paramSnmpMibAgent)
  {
    defaultAgent = paramSnmpMibAgent;
    root.agent = paramSnmpMibAgent;
  }
  
  public SnmpMibAgent getDefaultAgent()
  {
    return defaultAgent;
  }
  
  public void register(SnmpMibAgent paramSnmpMibAgent)
  {
    root.registerNode(paramSnmpMibAgent);
  }
  
  public void register(SnmpMibAgent paramSnmpMibAgent, long[] paramArrayOfLong)
  {
    root.registerNode(paramArrayOfLong, 0, paramSnmpMibAgent);
  }
  
  public SnmpMibAgent getAgentMib(SnmpOid paramSnmpOid)
  {
    TreeNode localTreeNode = root.retrieveMatchingBranch(paramSnmpOid.longValue(), 0);
    if (localTreeNode == null) {
      return defaultAgent;
    }
    if (localTreeNode.getAgentMib() == null) {
      return defaultAgent;
    }
    return localTreeNode.getAgentMib();
  }
  
  public void unregister(SnmpMibAgent paramSnmpMibAgent, SnmpOid[] paramArrayOfSnmpOid)
  {
    for (int i = 0; i < paramArrayOfSnmpOid.length; i++)
    {
      long[] arrayOfLong = paramArrayOfSnmpOid[i].longValue();
      TreeNode localTreeNode = root.retrieveMatchingBranch(arrayOfLong, 0);
      if (localTreeNode != null) {
        localTreeNode.removeAgent(paramSnmpMibAgent);
      }
    }
  }
  
  public void unregister(SnmpMibAgent paramSnmpMibAgent)
  {
    root.removeAgentFully(paramSnmpMibAgent);
  }
  
  public void printTree()
  {
    root.printTree(">");
  }
  
  final class TreeNode
  {
    private Vector<TreeNode> children = new Vector();
    private Vector<SnmpMibAgent> agents = new Vector();
    private long nodeValue;
    private SnmpMibAgent agent;
    private TreeNode parent;
    
    void registerNode(SnmpMibAgent paramSnmpMibAgent)
    {
      long[] arrayOfLong = paramSnmpMibAgent.getRootOid();
      registerNode(arrayOfLong, 0, paramSnmpMibAgent);
    }
    
    TreeNode retrieveMatchingBranch(long[] paramArrayOfLong, int paramInt)
    {
      TreeNode localTreeNode1 = retrieveChild(paramArrayOfLong, paramInt);
      if (localTreeNode1 == null) {
        return this;
      }
      if (children.isEmpty()) {
        return localTreeNode1;
      }
      if (paramInt + 1 == paramArrayOfLong.length) {
        return localTreeNode1;
      }
      TreeNode localTreeNode2 = localTreeNode1.retrieveMatchingBranch(paramArrayOfLong, paramInt + 1);
      return agent == null ? this : localTreeNode2;
    }
    
    SnmpMibAgent getAgentMib()
    {
      return agent;
    }
    
    public void printTree(String paramString)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      if (agents == null) {
        return;
      }
      Enumeration localEnumeration = agents.elements();
      Object localObject;
      while (localEnumeration.hasMoreElements())
      {
        localObject = (SnmpMibAgent)localEnumeration.nextElement();
        if (localObject == null) {
          localStringBuilder.append("empty ");
        } else {
          localStringBuilder.append(((SnmpMibAgent)localObject).getMibName()).append(" ");
        }
      }
      paramString = paramString + " ";
      if (children == null) {
        return;
      }
      localEnumeration = children.elements();
      while (localEnumeration.hasMoreElements())
      {
        localObject = (TreeNode)localEnumeration.nextElement();
        ((TreeNode)localObject).printTree(paramString);
      }
    }
    
    private TreeNode(long paramLong, SnmpMibAgent paramSnmpMibAgent, TreeNode paramTreeNode)
    {
      nodeValue = paramLong;
      parent = paramTreeNode;
      agents.addElement(paramSnmpMibAgent);
    }
    
    private void removeAgentFully(SnmpMibAgent paramSnmpMibAgent)
    {
      Vector localVector = new Vector();
      Enumeration localEnumeration = children.elements();
      while (localEnumeration.hasMoreElements())
      {
        TreeNode localTreeNode = (TreeNode)localEnumeration.nextElement();
        localTreeNode.removeAgentFully(paramSnmpMibAgent);
        if (agents.isEmpty()) {
          localVector.add(localTreeNode);
        }
      }
      localEnumeration = localVector.elements();
      while (localEnumeration.hasMoreElements()) {
        children.removeElement(localEnumeration.nextElement());
      }
      removeAgent(paramSnmpMibAgent);
    }
    
    private void removeAgent(SnmpMibAgent paramSnmpMibAgent)
    {
      if (!agents.contains(paramSnmpMibAgent)) {
        return;
      }
      agents.removeElement(paramSnmpMibAgent);
      if (!agents.isEmpty()) {
        agent = ((SnmpMibAgent)agents.firstElement());
      }
    }
    
    private void setAgent(SnmpMibAgent paramSnmpMibAgent)
    {
      agent = paramSnmpMibAgent;
    }
    
    private void registerNode(long[] paramArrayOfLong, int paramInt, SnmpMibAgent paramSnmpMibAgent)
    {
      if (paramInt >= paramArrayOfLong.length) {
        return;
      }
      TreeNode localTreeNode = retrieveChild(paramArrayOfLong, paramInt);
      if (localTreeNode == null)
      {
        long l = paramArrayOfLong[paramInt];
        localTreeNode = new TreeNode(SnmpMibTree.this, l, paramSnmpMibAgent, this);
        children.addElement(localTreeNode);
      }
      else if (!agents.contains(paramSnmpMibAgent))
      {
        agents.addElement(paramSnmpMibAgent);
      }
      if (paramInt == paramArrayOfLong.length - 1) {
        localTreeNode.setAgent(paramSnmpMibAgent);
      } else {
        localTreeNode.registerNode(paramArrayOfLong, paramInt + 1, paramSnmpMibAgent);
      }
    }
    
    private TreeNode retrieveChild(long[] paramArrayOfLong, int paramInt)
    {
      long l = paramArrayOfLong[paramInt];
      Enumeration localEnumeration = children.elements();
      while (localEnumeration.hasMoreElements())
      {
        TreeNode localTreeNode = (TreeNode)localEnumeration.nextElement();
        if (localTreeNode.match(l)) {
          return localTreeNode;
        }
      }
      return null;
    }
    
    private boolean match(long paramLong)
    {
      return nodeValue == paramLong;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\daemon\SnmpMibTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */