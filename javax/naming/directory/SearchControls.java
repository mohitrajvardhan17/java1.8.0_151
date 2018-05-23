package javax.naming.directory;

import java.io.Serializable;

public class SearchControls
  implements Serializable
{
  public static final int OBJECT_SCOPE = 0;
  public static final int ONELEVEL_SCOPE = 1;
  public static final int SUBTREE_SCOPE = 2;
  private int searchScope;
  private int timeLimit;
  private boolean derefLink;
  private boolean returnObj;
  private long countLimit;
  private String[] attributesToReturn;
  private static final long serialVersionUID = -2480540967773454797L;
  
  public SearchControls()
  {
    searchScope = 1;
    timeLimit = 0;
    countLimit = 0L;
    derefLink = false;
    returnObj = false;
    attributesToReturn = null;
  }
  
  public SearchControls(int paramInt1, long paramLong, int paramInt2, String[] paramArrayOfString, boolean paramBoolean1, boolean paramBoolean2)
  {
    searchScope = paramInt1;
    timeLimit = paramInt2;
    derefLink = paramBoolean2;
    returnObj = paramBoolean1;
    countLimit = paramLong;
    attributesToReturn = paramArrayOfString;
  }
  
  public int getSearchScope()
  {
    return searchScope;
  }
  
  public int getTimeLimit()
  {
    return timeLimit;
  }
  
  public boolean getDerefLinkFlag()
  {
    return derefLink;
  }
  
  public boolean getReturningObjFlag()
  {
    return returnObj;
  }
  
  public long getCountLimit()
  {
    return countLimit;
  }
  
  public String[] getReturningAttributes()
  {
    return attributesToReturn;
  }
  
  public void setSearchScope(int paramInt)
  {
    searchScope = paramInt;
  }
  
  public void setTimeLimit(int paramInt)
  {
    timeLimit = paramInt;
  }
  
  public void setDerefLinkFlag(boolean paramBoolean)
  {
    derefLink = paramBoolean;
  }
  
  public void setReturningObjFlag(boolean paramBoolean)
  {
    returnObj = paramBoolean;
  }
  
  public void setCountLimit(long paramLong)
  {
    countLimit = paramLong;
  }
  
  public void setReturningAttributes(String[] paramArrayOfString)
  {
    attributesToReturn = paramArrayOfString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\directory\SearchControls.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */