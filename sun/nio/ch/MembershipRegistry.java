package sun.nio.ch;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.channels.MembershipKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

class MembershipRegistry
{
  private Map<InetAddress, List<MembershipKeyImpl>> groups = null;
  
  MembershipRegistry() {}
  
  MembershipKey checkMembership(InetAddress paramInetAddress1, NetworkInterface paramNetworkInterface, InetAddress paramInetAddress2)
  {
    if (groups != null)
    {
      List localList = (List)groups.get(paramInetAddress1);
      if (localList != null)
      {
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          MembershipKeyImpl localMembershipKeyImpl = (MembershipKeyImpl)localIterator.next();
          if (localMembershipKeyImpl.networkInterface().equals(paramNetworkInterface))
          {
            if (paramInetAddress2 == null)
            {
              if (localMembershipKeyImpl.sourceAddress() == null) {
                return localMembershipKeyImpl;
              }
              throw new IllegalStateException("Already a member to receive all packets");
            }
            if (localMembershipKeyImpl.sourceAddress() == null) {
              throw new IllegalStateException("Already have source-specific membership");
            }
            if (paramInetAddress2.equals(localMembershipKeyImpl.sourceAddress())) {
              return localMembershipKeyImpl;
            }
          }
        }
      }
    }
    return null;
  }
  
  void add(MembershipKeyImpl paramMembershipKeyImpl)
  {
    InetAddress localInetAddress = paramMembershipKeyImpl.group();
    Object localObject;
    if (groups == null)
    {
      groups = new HashMap();
      localObject = null;
    }
    else
    {
      localObject = (List)groups.get(localInetAddress);
    }
    if (localObject == null)
    {
      localObject = new LinkedList();
      groups.put(localInetAddress, localObject);
    }
    ((List)localObject).add(paramMembershipKeyImpl);
  }
  
  void remove(MembershipKeyImpl paramMembershipKeyImpl)
  {
    InetAddress localInetAddress = paramMembershipKeyImpl.group();
    List localList = (List)groups.get(localInetAddress);
    if (localList != null)
    {
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext()) {
        if (localIterator.next() == paramMembershipKeyImpl) {
          localIterator.remove();
        }
      }
      if (localList.isEmpty()) {
        groups.remove(localInetAddress);
      }
    }
  }
  
  void invalidateAll()
  {
    if (groups != null)
    {
      Iterator localIterator1 = groups.keySet().iterator();
      while (localIterator1.hasNext())
      {
        InetAddress localInetAddress = (InetAddress)localIterator1.next();
        Iterator localIterator2 = ((List)groups.get(localInetAddress)).iterator();
        while (localIterator2.hasNext())
        {
          MembershipKeyImpl localMembershipKeyImpl = (MembershipKeyImpl)localIterator2.next();
          localMembershipKeyImpl.invalidate();
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\MembershipRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */