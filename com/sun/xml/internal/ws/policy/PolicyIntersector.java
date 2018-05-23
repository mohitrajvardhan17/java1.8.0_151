package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public final class PolicyIntersector
{
  private static final PolicyIntersector STRICT_INTERSECTOR = new PolicyIntersector(CompatibilityMode.STRICT);
  private static final PolicyIntersector LAX_INTERSECTOR = new PolicyIntersector(CompatibilityMode.LAX);
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyIntersector.class);
  private CompatibilityMode mode;
  
  private PolicyIntersector(CompatibilityMode paramCompatibilityMode)
  {
    mode = paramCompatibilityMode;
  }
  
  public static PolicyIntersector createStrictPolicyIntersector()
  {
    return STRICT_INTERSECTOR;
  }
  
  public static PolicyIntersector createLaxPolicyIntersector()
  {
    return LAX_INTERSECTOR;
  }
  
  public Policy intersect(Policy... paramVarArgs)
  {
    if ((paramVarArgs == null) || (paramVarArgs.length == 0)) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0056_NEITHER_NULL_NOR_EMPTY_POLICY_COLLECTION_EXPECTED())));
    }
    if (paramVarArgs.length == 1) {
      return paramVarArgs[0];
    }
    int i = 0;
    int j = 1;
    NamespaceVersion localNamespaceVersion = null;
    for (Object localObject2 : paramVarArgs)
    {
      if (((Policy)localObject2).isEmpty())
      {
        i = 1;
      }
      else
      {
        if (((Policy)localObject2).isNull()) {
          i = 1;
        }
        j = 0;
      }
      if (localNamespaceVersion == null) {
        localNamespaceVersion = ((Policy)localObject2).getNamespaceVersion();
      } else if (localNamespaceVersion.compareTo(((Policy)localObject2).getNamespaceVersion()) < 0) {
        localNamespaceVersion = ((Policy)localObject2).getNamespaceVersion();
      }
      if ((i != 0) && (j == 0)) {
        return Policy.createNullPolicy(localNamespaceVersion, null, null);
      }
    }
    localNamespaceVersion = localNamespaceVersion != null ? localNamespaceVersion : NamespaceVersion.getLatestVersion();
    if (j != 0) {
      return Policy.createEmptyPolicy(localNamespaceVersion, null, null);
    }
    ??? = new LinkedList(paramVarArgs[0].getContent());
    LinkedList localLinkedList = new LinkedList();
    ArrayList localArrayList = new ArrayList(2);
    for (int n = 1; n < paramVarArgs.length; n++)
    {
      Collection localCollection = paramVarArgs[n].getContent();
      localLinkedList.clear();
      localLinkedList.addAll((Collection)???);
      ((List)???).clear();
      AssertionSet localAssertionSet1;
      while ((localAssertionSet1 = (AssertionSet)localLinkedList.poll()) != null)
      {
        Iterator localIterator = localCollection.iterator();
        while (localIterator.hasNext())
        {
          AssertionSet localAssertionSet2 = (AssertionSet)localIterator.next();
          if (localAssertionSet1.isCompatibleWith(localAssertionSet2, mode))
          {
            localArrayList.add(localAssertionSet1);
            localArrayList.add(localAssertionSet2);
            ((List)???).add(AssertionSet.createMergedAssertionSet(localArrayList));
            localArrayList.clear();
          }
        }
      }
    }
    return Policy.createPolicy(localNamespaceVersion, null, null, (Collection)???);
  }
  
  static enum CompatibilityMode
  {
    STRICT,  LAX;
    
    private CompatibilityMode() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\PolicyIntersector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */