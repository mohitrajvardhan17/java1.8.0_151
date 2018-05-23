package com.sun.jndi.dns;

import java.util.Hashtable;

class NameNode
{
  private String label;
  private Hashtable<String, NameNode> children = null;
  private boolean isZoneCut = false;
  private int depth = 0;
  
  NameNode(String paramString)
  {
    label = paramString;
  }
  
  protected NameNode newNameNode(String paramString)
  {
    return new NameNode(paramString);
  }
  
  String getLabel()
  {
    return label;
  }
  
  int depth()
  {
    return depth;
  }
  
  boolean isZoneCut()
  {
    return isZoneCut;
  }
  
  void setZoneCut(boolean paramBoolean)
  {
    isZoneCut = paramBoolean;
  }
  
  Hashtable<String, NameNode> getChildren()
  {
    return children;
  }
  
  NameNode get(String paramString)
  {
    return children != null ? (NameNode)children.get(paramString) : null;
  }
  
  NameNode get(DnsName paramDnsName, int paramInt)
  {
    NameNode localNameNode = this;
    for (int i = paramInt; (i < paramDnsName.size()) && (localNameNode != null); i++) {
      localNameNode = localNameNode.get(paramDnsName.getKey(i));
    }
    return localNameNode;
  }
  
  NameNode add(DnsName paramDnsName, int paramInt)
  {
    Object localObject = this;
    for (int i = paramInt; i < paramDnsName.size(); i++)
    {
      String str1 = paramDnsName.get(i);
      String str2 = paramDnsName.getKey(i);
      NameNode localNameNode = null;
      if (children == null) {
        children = new Hashtable();
      } else {
        localNameNode = (NameNode)children.get(str2);
      }
      if (localNameNode == null)
      {
        localNameNode = newNameNode(str1);
        depth = (depth + 1);
        children.put(str2, localNameNode);
      }
      localObject = localNameNode;
    }
    return (NameNode)localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\dns\NameNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */