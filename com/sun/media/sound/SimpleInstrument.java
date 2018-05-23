package com.sun.media.sound;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.sound.midi.Patch;

public class SimpleInstrument
  extends ModelInstrument
{
  protected int preset = 0;
  protected int bank = 0;
  protected boolean percussion = false;
  protected String name = "";
  protected List<SimpleInstrumentPart> parts = new ArrayList();
  
  public SimpleInstrument()
  {
    super(null, null, null, null);
  }
  
  public void clear()
  {
    parts.clear();
  }
  
  public void add(ModelPerformer[] paramArrayOfModelPerformer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    SimpleInstrumentPart localSimpleInstrumentPart = new SimpleInstrumentPart(null);
    performers = paramArrayOfModelPerformer;
    keyFrom = paramInt1;
    keyTo = paramInt2;
    velFrom = paramInt3;
    velTo = paramInt4;
    exclusiveClass = paramInt5;
    parts.add(localSimpleInstrumentPart);
  }
  
  public void add(ModelPerformer[] paramArrayOfModelPerformer, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    add(paramArrayOfModelPerformer, paramInt1, paramInt2, paramInt3, paramInt4, -1);
  }
  
  public void add(ModelPerformer[] paramArrayOfModelPerformer, int paramInt1, int paramInt2)
  {
    add(paramArrayOfModelPerformer, paramInt1, paramInt2, 0, 127, -1);
  }
  
  public void add(ModelPerformer[] paramArrayOfModelPerformer)
  {
    add(paramArrayOfModelPerformer, 0, 127, 0, 127, -1);
  }
  
  public void add(ModelPerformer paramModelPerformer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    add(new ModelPerformer[] { paramModelPerformer }, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void add(ModelPerformer paramModelPerformer, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    add(new ModelPerformer[] { paramModelPerformer }, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void add(ModelPerformer paramModelPerformer, int paramInt1, int paramInt2)
  {
    add(new ModelPerformer[] { paramModelPerformer }, paramInt1, paramInt2);
  }
  
  public void add(ModelPerformer paramModelPerformer)
  {
    add(new ModelPerformer[] { paramModelPerformer });
  }
  
  public void add(ModelInstrument paramModelInstrument, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    add(paramModelInstrument.getPerformers(), paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void add(ModelInstrument paramModelInstrument, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    add(paramModelInstrument.getPerformers(), paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void add(ModelInstrument paramModelInstrument, int paramInt1, int paramInt2)
  {
    add(paramModelInstrument.getPerformers(), paramInt1, paramInt2);
  }
  
  public void add(ModelInstrument paramModelInstrument)
  {
    add(paramModelInstrument.getPerformers());
  }
  
  public ModelPerformer[] getPerformers()
  {
    int i = 0;
    Object localObject = parts.iterator();
    while (((Iterator)localObject).hasNext())
    {
      SimpleInstrumentPart localSimpleInstrumentPart1 = (SimpleInstrumentPart)((Iterator)localObject).next();
      if (performers != null) {
        i += performers.length;
      }
    }
    localObject = new ModelPerformer[i];
    int j = 0;
    Iterator localIterator = parts.iterator();
    while (localIterator.hasNext())
    {
      SimpleInstrumentPart localSimpleInstrumentPart2 = (SimpleInstrumentPart)localIterator.next();
      if (performers != null) {
        for (ModelPerformer localModelPerformer1 : performers)
        {
          ModelPerformer localModelPerformer2 = new ModelPerformer();
          localModelPerformer2.setName(getName());
          localObject[(j++)] = localModelPerformer2;
          localModelPerformer2.setDefaultConnectionsEnabled(localModelPerformer1.isDefaultConnectionsEnabled());
          localModelPerformer2.setKeyFrom(localModelPerformer1.getKeyFrom());
          localModelPerformer2.setKeyTo(localModelPerformer1.getKeyTo());
          localModelPerformer2.setVelFrom(localModelPerformer1.getVelFrom());
          localModelPerformer2.setVelTo(localModelPerformer1.getVelTo());
          localModelPerformer2.setExclusiveClass(localModelPerformer1.getExclusiveClass());
          localModelPerformer2.setSelfNonExclusive(localModelPerformer1.isSelfNonExclusive());
          localModelPerformer2.setReleaseTriggered(localModelPerformer1.isReleaseTriggered());
          if (exclusiveClass != -1) {
            localModelPerformer2.setExclusiveClass(exclusiveClass);
          }
          if (keyFrom > localModelPerformer2.getKeyFrom()) {
            localModelPerformer2.setKeyFrom(keyFrom);
          }
          if (keyTo < localModelPerformer2.getKeyTo()) {
            localModelPerformer2.setKeyTo(keyTo);
          }
          if (velFrom > localModelPerformer2.getVelFrom()) {
            localModelPerformer2.setVelFrom(velFrom);
          }
          if (velTo < localModelPerformer2.getVelTo()) {
            localModelPerformer2.setVelTo(velTo);
          }
          localModelPerformer2.getOscillators().addAll(localModelPerformer1.getOscillators());
          localModelPerformer2.getConnectionBlocks().addAll(localModelPerformer1.getConnectionBlocks());
        }
      }
    }
    return (ModelPerformer[])localObject;
  }
  
  public Object getData()
  {
    return null;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
  
  public ModelPatch getPatch()
  {
    return new ModelPatch(bank, preset, percussion);
  }
  
  public void setPatch(Patch paramPatch)
  {
    if (((paramPatch instanceof ModelPatch)) && (((ModelPatch)paramPatch).isPercussion()))
    {
      percussion = true;
      bank = paramPatch.getBank();
      preset = paramPatch.getProgram();
    }
    else
    {
      percussion = false;
      bank = paramPatch.getBank();
      preset = paramPatch.getProgram();
    }
  }
  
  private static class SimpleInstrumentPart
  {
    ModelPerformer[] performers;
    int keyFrom;
    int keyTo;
    int velFrom;
    int velTo;
    int exclusiveClass;
    
    private SimpleInstrumentPart() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SimpleInstrument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */