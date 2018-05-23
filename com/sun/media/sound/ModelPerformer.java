package com.sun.media.sound;

import java.util.ArrayList;
import java.util.List;

public final class ModelPerformer
{
  private final List<ModelOscillator> oscillators = new ArrayList();
  private List<ModelConnectionBlock> connectionBlocks = new ArrayList();
  private int keyFrom = 0;
  private int keyTo = 127;
  private int velFrom = 0;
  private int velTo = 127;
  private int exclusiveClass = 0;
  private boolean releaseTrigger = false;
  private boolean selfNonExclusive = false;
  private Object userObject = null;
  private boolean addDefaultConnections = true;
  private String name = null;
  
  public ModelPerformer() {}
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
  
  public List<ModelConnectionBlock> getConnectionBlocks()
  {
    return connectionBlocks;
  }
  
  public void setConnectionBlocks(List<ModelConnectionBlock> paramList)
  {
    connectionBlocks = paramList;
  }
  
  public List<ModelOscillator> getOscillators()
  {
    return oscillators;
  }
  
  public int getExclusiveClass()
  {
    return exclusiveClass;
  }
  
  public void setExclusiveClass(int paramInt)
  {
    exclusiveClass = paramInt;
  }
  
  public boolean isSelfNonExclusive()
  {
    return selfNonExclusive;
  }
  
  public void setSelfNonExclusive(boolean paramBoolean)
  {
    selfNonExclusive = paramBoolean;
  }
  
  public int getKeyFrom()
  {
    return keyFrom;
  }
  
  public void setKeyFrom(int paramInt)
  {
    keyFrom = paramInt;
  }
  
  public int getKeyTo()
  {
    return keyTo;
  }
  
  public void setKeyTo(int paramInt)
  {
    keyTo = paramInt;
  }
  
  public int getVelFrom()
  {
    return velFrom;
  }
  
  public void setVelFrom(int paramInt)
  {
    velFrom = paramInt;
  }
  
  public int getVelTo()
  {
    return velTo;
  }
  
  public void setVelTo(int paramInt)
  {
    velTo = paramInt;
  }
  
  public boolean isReleaseTriggered()
  {
    return releaseTrigger;
  }
  
  public void setReleaseTriggered(boolean paramBoolean)
  {
    releaseTrigger = paramBoolean;
  }
  
  public Object getUserObject()
  {
    return userObject;
  }
  
  public void setUserObject(Object paramObject)
  {
    userObject = paramObject;
  }
  
  public boolean isDefaultConnectionsEnabled()
  {
    return addDefaultConnections;
  }
  
  public void setDefaultConnectionsEnabled(boolean paramBoolean)
  {
    addDefaultConnections = paramBoolean;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\ModelPerformer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */