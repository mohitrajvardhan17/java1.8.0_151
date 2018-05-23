package sun.security.jgss.krb5;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.kerberos.KeyTab;

class SubjectComber
{
  private static final boolean DEBUG = Krb5Util.DEBUG;
  
  private SubjectComber() {}
  
  static <T> T find(Subject paramSubject, String paramString1, String paramString2, Class<T> paramClass)
  {
    return (T)paramClass.cast(findAux(paramSubject, paramString1, paramString2, paramClass, true));
  }
  
  static <T> List<T> findMany(Subject paramSubject, String paramString1, String paramString2, Class<T> paramClass)
  {
    return (List)findAux(paramSubject, paramString1, paramString2, paramClass, false);
  }
  
  private static <T> Object findAux(Subject paramSubject, String paramString1, String paramString2, Class<T> paramClass, boolean paramBoolean)
  {
    if (paramSubject == null) {
      return null;
    }
    ArrayList localArrayList = paramBoolean ? null : new ArrayList();
    Object localObject1;
    Object localObject2;
    Object localObject3;
    Object localObject5;
    if (paramClass == KeyTab.class)
    {
      localObject1 = paramSubject.getPrivateCredentials(KeyTab.class).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (KeyTab)((Iterator)localObject1).next();
        if ((paramString1 != null) && (((KeyTab)localObject2).isBound()))
        {
          localObject3 = ((KeyTab)localObject2).getPrincipal();
          if (localObject3 != null)
          {
            if (paramString1.equals(((KerberosPrincipal)localObject3).getName())) {}
          }
          else
          {
            int i = 0;
            localObject5 = paramSubject.getPrincipals(KerberosPrincipal.class).iterator();
            while (((Iterator)localObject5).hasNext())
            {
              KerberosPrincipal localKerberosPrincipal = (KerberosPrincipal)((Iterator)localObject5).next();
              if (localKerberosPrincipal.getName().equals(paramString1))
              {
                i = 1;
                break;
              }
            }
            if (i == 0) {
              continue;
            }
          }
        }
        if (DEBUG) {
          System.out.println("Found " + paramClass.getSimpleName() + " " + localObject2);
        }
        if (paramBoolean) {
          return localObject2;
        }
        localArrayList.add(paramClass.cast(localObject2));
      }
    }
    else if (paramClass == KerberosKey.class)
    {
      localObject1 = paramSubject.getPrivateCredentials(KerberosKey.class).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (KerberosKey)((Iterator)localObject1).next();
        localObject3 = ((KerberosKey)localObject2).getPrincipal().getName();
        if ((paramString1 == null) || (paramString1.equals(localObject3)))
        {
          if (DEBUG) {
            System.out.println("Found " + paramClass.getSimpleName() + " for " + (String)localObject3);
          }
          if (paramBoolean) {
            return localObject2;
          }
          localArrayList.add(paramClass.cast(localObject2));
        }
      }
    }
    else if (paramClass == KerberosTicket.class)
    {
      localObject1 = paramSubject.getPrivateCredentials();
      synchronized (localObject1)
      {
        localObject3 = ((Set)localObject1).iterator();
        while (((Iterator)localObject3).hasNext())
        {
          Object localObject4 = ((Iterator)localObject3).next();
          if ((localObject4 instanceof KerberosTicket))
          {
            localObject5 = (KerberosTicket)localObject4;
            if (DEBUG) {
              System.out.println("Found ticket for " + ((KerberosTicket)localObject5).getClient() + " to go to " + ((KerberosTicket)localObject5).getServer() + " expiring on " + ((KerberosTicket)localObject5).getEndTime());
            }
            if (!((KerberosTicket)localObject5).isCurrent())
            {
              if (!paramSubject.isReadOnly())
              {
                ((Iterator)localObject3).remove();
                try
                {
                  ((KerberosTicket)localObject5).destroy();
                  if (DEBUG) {
                    System.out.println("Removed and destroyed the expired Ticket \n" + localObject5);
                  }
                }
                catch (DestroyFailedException localDestroyFailedException)
                {
                  if (DEBUG) {
                    System.out.println("Expired ticket not detroyed successfully. " + localDestroyFailedException);
                  }
                }
              }
            }
            else if (((paramString1 == null) || (((KerberosTicket)localObject5).getServer().getName().equals(paramString1))) && ((paramString2 == null) || (paramString2.equals(((KerberosTicket)localObject5).getClient().getName()))))
            {
              if (paramBoolean) {
                return localObject5;
              }
              if (paramString2 == null) {
                paramString2 = ((KerberosTicket)localObject5).getClient().getName();
              }
              if (paramString1 == null) {
                paramString1 = ((KerberosTicket)localObject5).getServer().getName();
              }
              localArrayList.add(paramClass.cast(localObject5));
            }
          }
        }
      }
    }
    return localArrayList;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\krb5\SubjectComber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */