package com.sun.jmx.snmp.IPAcl;

import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Vector;

class JDMInformBlock
  extends SimpleNode
{
  JDMInformBlock(int paramInt)
  {
    super(paramInt);
  }
  
  JDMInformBlock(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMInformBlock(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMInformBlock(paramParser, paramInt);
  }
  
  public void buildAclEntries(PrincipalImpl paramPrincipalImpl, AclImpl paramAclImpl) {}
  
  public void buildTrapEntries(Hashtable<InetAddress, Vector<String>> paramHashtable) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMInformBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */