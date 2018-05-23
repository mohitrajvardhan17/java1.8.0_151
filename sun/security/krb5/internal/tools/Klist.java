package sun.security.krb5.internal.tools;

import java.io.PrintStream;
import java.net.InetAddress;
import java.util.Date;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.TicketFlags;
import sun.security.krb5.internal.ccache.CredentialsCache;
import sun.security.krb5.internal.crypto.EType;
import sun.security.krb5.internal.ktab.KeyTab;
import sun.security.krb5.internal.ktab.KeyTabEntry;

public class Klist
{
  Object target;
  char[] options = new char[4];
  String name;
  char action;
  private static boolean DEBUG = Krb5.DEBUG;
  
  public Klist() {}
  
  public static void main(String[] paramArrayOfString)
  {
    Klist localKlist = new Klist();
    if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {
      action = 'c';
    } else {
      localKlist.processArgs(paramArrayOfString);
    }
    switch (action)
    {
    case 'c': 
      if (name == null)
      {
        target = CredentialsCache.getInstance();
        name = CredentialsCache.cacheName();
      }
      else
      {
        target = CredentialsCache.getInstance(name);
      }
      if (target != null)
      {
        localKlist.displayCache();
      }
      else
      {
        localKlist.displayMessage("Credentials cache");
        System.exit(-1);
      }
      break;
    case 'k': 
      KeyTab localKeyTab = KeyTab.getInstance(name);
      if (localKeyTab.isMissing())
      {
        System.out.println("KeyTab " + name + " not found.");
        System.exit(-1);
      }
      else if (!localKeyTab.isValid())
      {
        System.out.println("KeyTab " + name + " format not supported.");
        System.exit(-1);
      }
      target = localKeyTab;
      name = localKeyTab.tabName();
      localKlist.displayTab();
      break;
    default: 
      if (name != null)
      {
        localKlist.printHelp();
        System.exit(-1);
      }
      else
      {
        target = CredentialsCache.getInstance();
        name = CredentialsCache.cacheName();
        if (target != null)
        {
          localKlist.displayCache();
        }
        else
        {
          localKlist.displayMessage("Credentials cache");
          System.exit(-1);
        }
      }
      break;
    }
  }
  
  void processArgs(String[] paramArrayOfString)
  {
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      Character localCharacter;
      if ((paramArrayOfString[i].length() >= 2) && (paramArrayOfString[i].startsWith("-"))) {
        localCharacter = new Character(paramArrayOfString[i].charAt(1));
      }
      switch (localCharacter.charValue())
      {
      case 'c': 
        action = 'c';
        break;
      case 'k': 
        action = 'k';
        break;
      case 'a': 
        options[2] = 'a';
        break;
      case 'n': 
        options[3] = 'n';
        break;
      case 'f': 
        options[1] = 'f';
        break;
      case 'e': 
        options[0] = 'e';
        break;
      case 'K': 
        options[1] = 'K';
        break;
      case 't': 
        options[2] = 't';
        break;
      default: 
        printHelp();
        System.exit(-1);
        continue;
        if ((!paramArrayOfString[i].startsWith("-")) && (i == paramArrayOfString.length - 1))
        {
          name = paramArrayOfString[i];
          localCharacter = null;
        }
        else
        {
          printHelp();
          System.exit(-1);
        }
        break;
      }
    }
  }
  
  void displayTab()
  {
    KeyTab localKeyTab = (KeyTab)target;
    KeyTabEntry[] arrayOfKeyTabEntry = localKeyTab.getEntries();
    if (arrayOfKeyTabEntry.length == 0)
    {
      System.out.println("\nKey tab: " + name + ",  0 entries found.\n");
    }
    else
    {
      if (arrayOfKeyTabEntry.length == 1) {
        System.out.println("\nKey tab: " + name + ", " + arrayOfKeyTabEntry.length + " entry found.\n");
      } else {
        System.out.println("\nKey tab: " + name + ", " + arrayOfKeyTabEntry.length + " entries found.\n");
      }
      for (int i = 0; i < arrayOfKeyTabEntry.length; i++)
      {
        System.out.println("[" + (i + 1) + "] Service principal: " + arrayOfKeyTabEntry[i].getService().toString());
        System.out.println("\t KVNO: " + arrayOfKeyTabEntry[i].getKey().getKeyVersionNumber());
        EncryptionKey localEncryptionKey;
        if (options[0] == 'e')
        {
          localEncryptionKey = arrayOfKeyTabEntry[i].getKey();
          System.out.println("\t Key type: " + localEncryptionKey.getEType());
        }
        if (options[1] == 'K')
        {
          localEncryptionKey = arrayOfKeyTabEntry[i].getKey();
          System.out.println("\t Key: " + arrayOfKeyTabEntry[i].getKeyString());
        }
        if (options[2] == 't') {
          System.out.println("\t Time stamp: " + format(arrayOfKeyTabEntry[i].getTimeStamp()));
        }
      }
    }
  }
  
  void displayCache()
  {
    CredentialsCache localCredentialsCache = (CredentialsCache)target;
    sun.security.krb5.internal.ccache.Credentials[] arrayOfCredentials = localCredentialsCache.getCredsList();
    if (arrayOfCredentials == null)
    {
      System.out.println("No credentials available in the cache " + name);
      System.exit(-1);
    }
    System.out.println("\nCredentials cache: " + name);
    String str1 = localCredentialsCache.getPrimaryPrincipal().toString();
    int i = arrayOfCredentials.length;
    if (i == 1) {
      System.out.println("\nDefault principal: " + str1 + ", " + arrayOfCredentials.length + " entry found.\n");
    } else {
      System.out.println("\nDefault principal: " + str1 + ", " + arrayOfCredentials.length + " entries found.\n");
    }
    if (arrayOfCredentials != null) {
      for (int j = 0; j < arrayOfCredentials.length; j++) {
        try
        {
          String str2;
          if (arrayOfCredentials[j].getStartTime() != null) {
            str2 = format(arrayOfCredentials[j].getStartTime());
          } else {
            str2 = format(arrayOfCredentials[j].getAuthTime());
          }
          String str3 = format(arrayOfCredentials[j].getEndTime());
          String str5 = arrayOfCredentials[j].getServicePrincipal().toString();
          System.out.println("[" + (j + 1) + "]  Service Principal:  " + str5);
          System.out.println("     Valid starting:     " + str2);
          System.out.println("     Expires:            " + str3);
          if (arrayOfCredentials[j].getRenewTill() != null)
          {
            String str4 = format(arrayOfCredentials[j].getRenewTill());
            System.out.println("     Renew until:        " + str4);
          }
          Object localObject1;
          if (options[0] == 'e')
          {
            String str6 = EType.toString(arrayOfCredentials[j].getEType());
            localObject1 = EType.toString(arrayOfCredentials[j].getTktEType());
            System.out.println("     EType (skey, tkt):  " + str6 + ", " + (String)localObject1);
          }
          if (options[1] == 'f') {
            System.out.println("     Flags:              " + arrayOfCredentials[j].getTicketFlags().toString());
          }
          if (options[2] == 'a')
          {
            int k = 1;
            localObject1 = arrayOfCredentials[j].setKrbCreds().getClientAddresses();
            if (localObject1 != null) {
              for (Object localObject3 : localObject1)
              {
                String str7;
                if (options[3] == 'n') {
                  str7 = ((InetAddress)localObject3).getHostAddress();
                } else {
                  str7 = ((InetAddress)localObject3).getCanonicalHostName();
                }
                System.out.println("     " + (k != 0 ? "Addresses:" : "          ") + "       " + str7);
                k = 0;
              }
            } else {
              System.out.println("     [No host addresses info]");
            }
          }
        }
        catch (RealmException localRealmException)
        {
          System.out.println("Error reading principal from the entry.");
          if (DEBUG) {
            localRealmException.printStackTrace();
          }
          System.exit(-1);
        }
      }
    } else {
      System.out.println("\nNo entries found.");
    }
  }
  
  void displayMessage(String paramString)
  {
    if (name == null) {
      System.out.println("Default " + paramString + " not found.");
    } else {
      System.out.println(paramString + " " + name + " not found.");
    }
  }
  
  private String format(KerberosTime paramKerberosTime)
  {
    String str = paramKerberosTime.toDate().toString();
    return str.substring(4, 7) + " " + str.substring(8, 10) + ", " + str.substring(24) + " " + str.substring(11, 19);
  }
  
  void printHelp()
  {
    System.out.println("\nUsage: klist [[-c] [-f] [-e] [-a [-n]]] [-k [-t] [-K]] [name]");
    System.out.println("   name\t name of credentials cache or  keytab with the prefix. File-based cache or keytab's prefix is FILE:.");
    System.out.println("   -c specifies that credential cache is to be listed");
    System.out.println("   -k specifies that key tab is to be listed");
    System.out.println("   options for credentials caches:");
    System.out.println("\t-f \t shows credentials flags");
    System.out.println("\t-e \t shows the encryption type");
    System.out.println("\t-a \t shows addresses");
    System.out.println("\t  -n \t   do not reverse-resolve addresses");
    System.out.println("   options for keytabs:");
    System.out.println("\t-t \t shows keytab entry timestamps");
    System.out.println("\t-K \t shows keytab entry key value");
    System.out.println("\t-e \t shows keytab entry key type");
    System.out.println("\nUsage: java sun.security.krb5.tools.Klist -help for help.");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\tools\Klist.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */