package javax.naming.ldap;

public class SortKey
{
  private String attrID;
  private boolean reverseOrder = false;
  private String matchingRuleID = null;
  
  public SortKey(String paramString)
  {
    attrID = paramString;
  }
  
  public SortKey(String paramString1, boolean paramBoolean, String paramString2)
  {
    attrID = paramString1;
    reverseOrder = (!paramBoolean);
    matchingRuleID = paramString2;
  }
  
  public String getAttributeID()
  {
    return attrID;
  }
  
  public boolean isAscending()
  {
    return !reverseOrder;
  }
  
  public String getMatchingRuleID()
  {
    return matchingRuleID;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\ldap\SortKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */