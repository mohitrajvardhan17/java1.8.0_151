package sun.security.tools.policytool;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.NoSuchAlgorithmException;
import java.security.Permission;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.LinkedList;
import java.util.ListIterator;
import sun.security.provider.PolicyParser.GrantEntry;
import sun.security.provider.PolicyParser.PermissionEntry;
import sun.security.provider.PolicyParser.PrincipalEntry;

class PolicyEntry
{
  private CodeSource codesource;
  private PolicyTool tool;
  private PolicyParser.GrantEntry grantEntry;
  private boolean testing = false;
  
  PolicyEntry(PolicyTool paramPolicyTool, PolicyParser.GrantEntry paramGrantEntry)
    throws MalformedURLException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException
  {
    tool = paramPolicyTool;
    URL localURL = null;
    if (codeBase != null) {
      localURL = new URL(codeBase);
    }
    codesource = new CodeSource(localURL, (Certificate[])null);
    if (testing)
    {
      System.out.println("Adding Policy Entry:");
      System.out.println("    CodeBase = " + localURL);
      System.out.println("    Signers = " + signedBy);
      System.out.println("    with " + principals.size() + " Principals");
    }
    grantEntry = paramGrantEntry;
  }
  
  CodeSource getCodeSource()
  {
    return codesource;
  }
  
  PolicyParser.GrantEntry getGrantEntry()
  {
    return grantEntry;
  }
  
  String headerToString()
  {
    String str = principalsToString();
    if (str.length() == 0) {
      return codebaseToString();
    }
    return codebaseToString() + ", " + str;
  }
  
  String codebaseToString()
  {
    String str = new String();
    if ((grantEntry.codeBase != null) && (!grantEntry.codeBase.equals(""))) {
      str = str.concat("CodeBase \"" + grantEntry.codeBase + "\"");
    }
    if ((grantEntry.signedBy != null) && (!grantEntry.signedBy.equals(""))) {
      str = str.length() > 0 ? str.concat(", SignedBy \"" + grantEntry.signedBy + "\"") : str.concat("SignedBy \"" + grantEntry.signedBy + "\"");
    }
    if (str.length() == 0) {
      return new String("CodeBase <ALL>");
    }
    return str;
  }
  
  String principalsToString()
  {
    String str = "";
    if ((grantEntry.principals != null) && (!grantEntry.principals.isEmpty()))
    {
      StringBuffer localStringBuffer = new StringBuffer(200);
      ListIterator localListIterator = grantEntry.principals.listIterator();
      while (localListIterator.hasNext())
      {
        PolicyParser.PrincipalEntry localPrincipalEntry = (PolicyParser.PrincipalEntry)localListIterator.next();
        localStringBuffer.append(" Principal " + localPrincipalEntry.getDisplayClass() + " " + localPrincipalEntry.getDisplayName(true));
        if (localListIterator.hasNext()) {
          localStringBuffer.append(", ");
        }
      }
      str = localStringBuffer.toString();
    }
    return str;
  }
  
  PolicyParser.PermissionEntry toPermissionEntry(Permission paramPermission)
  {
    String str = null;
    if ((paramPermission.getActions() != null) && (paramPermission.getActions().trim() != "")) {
      str = paramPermission.getActions();
    }
    PolicyParser.PermissionEntry localPermissionEntry = new PolicyParser.PermissionEntry(paramPermission.getClass().getName(), paramPermission.getName(), str);
    return localPermissionEntry;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\PolicyEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */