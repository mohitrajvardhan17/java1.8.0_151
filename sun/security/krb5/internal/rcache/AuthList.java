package sun.security.krb5.internal.rcache;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.KrbApErrException;

public class AuthList
{
  private final LinkedList<AuthTimeWithHash> entries;
  private final int lifespan;
  
  public AuthList(int paramInt)
  {
    lifespan = paramInt;
    entries = new LinkedList();
  }
  
  public void put(AuthTimeWithHash paramAuthTimeWithHash, KerberosTime paramKerberosTime)
    throws KrbApErrException
  {
    if (entries.isEmpty())
    {
      entries.addFirst(paramAuthTimeWithHash);
    }
    else
    {
      AuthTimeWithHash localAuthTimeWithHash1 = (AuthTimeWithHash)entries.getFirst();
      int i = localAuthTimeWithHash1.compareTo(paramAuthTimeWithHash);
      if (i < 0)
      {
        entries.addFirst(paramAuthTimeWithHash);
      }
      else
      {
        if (i == 0) {
          throw new KrbApErrException(34);
        }
        localListIterator = entries.listIterator(1);
        int j = 0;
        while (localListIterator.hasNext())
        {
          localAuthTimeWithHash1 = (AuthTimeWithHash)localListIterator.next();
          i = localAuthTimeWithHash1.compareTo(paramAuthTimeWithHash);
          if (i < 0)
          {
            entries.add(entries.indexOf(localAuthTimeWithHash1), paramAuthTimeWithHash);
            j = 1;
          }
          else if (i == 0)
          {
            throw new KrbApErrException(34);
          }
        }
        if (j == 0) {
          entries.addLast(paramAuthTimeWithHash);
        }
      }
    }
    long l = paramKerberosTime.getSeconds() - lifespan;
    ListIterator localListIterator = entries.listIterator(0);
    AuthTimeWithHash localAuthTimeWithHash2 = null;
    int k = -1;
    while (localListIterator.hasNext())
    {
      localAuthTimeWithHash2 = (AuthTimeWithHash)localListIterator.next();
      if (ctime < l) {
        k = entries.indexOf(localAuthTimeWithHash2);
      }
    }
    if (k > -1) {
      do
      {
        entries.removeLast();
      } while (entries.size() > k);
    }
  }
  
  public boolean isEmpty()
  {
    return entries.isEmpty();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = entries.descendingIterator();
    int i = entries.size();
    while (localIterator.hasNext())
    {
      AuthTimeWithHash localAuthTimeWithHash = (AuthTimeWithHash)localIterator.next();
      localStringBuilder.append('#').append(i--).append(": ").append(localAuthTimeWithHash.toString()).append('\n');
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\rcache\AuthList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */