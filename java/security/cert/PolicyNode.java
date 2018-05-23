package java.security.cert;

import java.util.Iterator;
import java.util.Set;

public abstract interface PolicyNode
{
  public abstract PolicyNode getParent();
  
  public abstract Iterator<? extends PolicyNode> getChildren();
  
  public abstract int getDepth();
  
  public abstract String getValidPolicy();
  
  public abstract Set<? extends PolicyQualifierInfo> getPolicyQualifiers();
  
  public abstract Set<String> getExpectedPolicies();
  
  public abstract boolean isCritical();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\PolicyNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */