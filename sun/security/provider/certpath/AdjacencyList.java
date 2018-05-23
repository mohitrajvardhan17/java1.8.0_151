package sun.security.provider.certpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AdjacencyList
{
  private ArrayList<BuildStep> mStepList = new ArrayList();
  private List<List<Vertex>> mOrigList;
  
  public AdjacencyList(List<List<Vertex>> paramList)
  {
    mOrigList = paramList;
    buildList(paramList, 0, null);
  }
  
  public Iterator<BuildStep> iterator()
  {
    return Collections.unmodifiableList(mStepList).iterator();
  }
  
  private boolean buildList(List<List<Vertex>> paramList, int paramInt, BuildStep paramBuildStep)
  {
    List localList = (List)paramList.get(paramInt);
    int i = 1;
    int j = 1;
    Object localObject1 = localList.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Vertex)((Iterator)localObject1).next();
      if (((Vertex)localObject2).getIndex() != -1)
      {
        if (((List)paramList.get(((Vertex)localObject2).getIndex())).size() != 0) {
          i = 0;
        }
      }
      else if (((Vertex)localObject2).getThrowable() == null) {
        j = 0;
      }
      mStepList.add(new BuildStep((Vertex)localObject2, 1));
    }
    Vertex localVertex;
    if (i != 0)
    {
      if (j != 0)
      {
        if (paramBuildStep == null) {
          mStepList.add(new BuildStep(null, 4));
        } else {
          mStepList.add(new BuildStep(paramBuildStep.getVertex(), 2));
        }
        return false;
      }
      localObject1 = new ArrayList();
      localObject2 = localList.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localVertex = (Vertex)((Iterator)localObject2).next();
        if (localVertex.getThrowable() == null) {
          ((List)localObject1).add(localVertex);
        }
      }
      if (((List)localObject1).size() == 1) {
        mStepList.add(new BuildStep((Vertex)((List)localObject1).get(0), 5));
      } else {
        mStepList.add(new BuildStep((Vertex)((List)localObject1).get(0), 5));
      }
      return true;
    }
    boolean bool = false;
    Object localObject2 = localList.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localVertex = (Vertex)((Iterator)localObject2).next();
      if ((localVertex.getIndex() != -1) && (((List)paramList.get(localVertex.getIndex())).size() != 0))
      {
        BuildStep localBuildStep = new BuildStep(localVertex, 3);
        mStepList.add(localBuildStep);
        bool = buildList(paramList, localVertex.getIndex(), localBuildStep);
      }
    }
    if (bool) {
      return true;
    }
    if (paramBuildStep == null) {
      mStepList.add(new BuildStep(null, 4));
    } else {
      mStepList.add(new BuildStep(paramBuildStep.getVertex(), 2));
    }
    return false;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("[\n");
    int i = 0;
    Iterator localIterator1 = mOrigList.iterator();
    while (localIterator1.hasNext())
    {
      List localList = (List)localIterator1.next();
      localStringBuilder.append("LinkedList[").append(i++).append("]:\n");
      Iterator localIterator2 = localList.iterator();
      while (localIterator2.hasNext())
      {
        Vertex localVertex = (Vertex)localIterator2.next();
        localStringBuilder.append(localVertex.toString()).append("\n");
      }
    }
    localStringBuilder.append("]\n");
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\AdjacencyList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */