package org.ietf.jgss;

public class MessageProp
{
  private boolean privacyState;
  private int qop;
  private boolean dupToken;
  private boolean oldToken;
  private boolean unseqToken;
  private boolean gapToken;
  private int minorStatus;
  private String minorString;
  
  public MessageProp(boolean paramBoolean)
  {
    this(0, paramBoolean);
  }
  
  public MessageProp(int paramInt, boolean paramBoolean)
  {
    qop = paramInt;
    privacyState = paramBoolean;
    resetStatusValues();
  }
  
  public int getQOP()
  {
    return qop;
  }
  
  public boolean getPrivacy()
  {
    return privacyState;
  }
  
  public void setQOP(int paramInt)
  {
    qop = paramInt;
  }
  
  public void setPrivacy(boolean paramBoolean)
  {
    privacyState = paramBoolean;
  }
  
  public boolean isDuplicateToken()
  {
    return dupToken;
  }
  
  public boolean isOldToken()
  {
    return oldToken;
  }
  
  public boolean isUnseqToken()
  {
    return unseqToken;
  }
  
  public boolean isGapToken()
  {
    return gapToken;
  }
  
  public int getMinorStatus()
  {
    return minorStatus;
  }
  
  public String getMinorString()
  {
    return minorString;
  }
  
  public void setSupplementaryStates(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, int paramInt, String paramString)
  {
    dupToken = paramBoolean1;
    oldToken = paramBoolean2;
    unseqToken = paramBoolean3;
    gapToken = paramBoolean4;
    minorStatus = paramInt;
    minorString = paramString;
  }
  
  private void resetStatusValues()
  {
    dupToken = false;
    oldToken = false;
    unseqToken = false;
    gapToken = false;
    minorStatus = 0;
    minorString = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\ietf\jgss\MessageProp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */