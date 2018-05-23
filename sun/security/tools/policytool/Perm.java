package sun.security.tools.policytool;

class Perm
{
  public final String CLASS;
  public final String FULL_CLASS;
  public final String[] TARGETS;
  public final String[] ACTIONS;
  
  public Perm(String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    CLASS = paramString1;
    FULL_CLASS = paramString2;
    TARGETS = paramArrayOfString1;
    ACTIONS = paramArrayOfString2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\Perm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */