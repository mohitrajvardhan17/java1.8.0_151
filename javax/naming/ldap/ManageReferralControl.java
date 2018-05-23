package javax.naming.ldap;

public final class ManageReferralControl
  extends BasicControl
{
  public static final String OID = "2.16.840.1.113730.3.4.2";
  private static final long serialVersionUID = 3017756160149982566L;
  
  public ManageReferralControl()
  {
    super("2.16.840.1.113730.3.4.2", true, null);
  }
  
  public ManageReferralControl(boolean paramBoolean)
  {
    super("2.16.840.1.113730.3.4.2", paramBoolean, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\ldap\ManageReferralControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */