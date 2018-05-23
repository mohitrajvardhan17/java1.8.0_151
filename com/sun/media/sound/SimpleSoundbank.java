package com.sun.media.sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;

public class SimpleSoundbank
  implements Soundbank
{
  String name = "";
  String version = "";
  String vendor = "";
  String description = "";
  List<SoundbankResource> resources = new ArrayList();
  List<Instrument> instruments = new ArrayList();
  
  public SimpleSoundbank() {}
  
  public String getName()
  {
    return name;
  }
  
  public String getVersion()
  {
    return version;
  }
  
  public String getVendor()
  {
    return vendor;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public void setDescription(String paramString)
  {
    description = paramString;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
  
  public void setVendor(String paramString)
  {
    vendor = paramString;
  }
  
  public void setVersion(String paramString)
  {
    version = paramString;
  }
  
  public SoundbankResource[] getResources()
  {
    return (SoundbankResource[])resources.toArray(new SoundbankResource[resources.size()]);
  }
  
  public Instrument[] getInstruments()
  {
    Instrument[] arrayOfInstrument = (Instrument[])instruments.toArray(new Instrument[resources.size()]);
    Arrays.sort(arrayOfInstrument, new ModelInstrumentComparator());
    return arrayOfInstrument;
  }
  
  public Instrument getInstrument(Patch paramPatch)
  {
    int i = paramPatch.getProgram();
    int j = paramPatch.getBank();
    boolean bool1 = false;
    if ((paramPatch instanceof ModelPatch)) {
      bool1 = ((ModelPatch)paramPatch).isPercussion();
    }
    Iterator localIterator = instruments.iterator();
    while (localIterator.hasNext())
    {
      Instrument localInstrument = (Instrument)localIterator.next();
      Patch localPatch = localInstrument.getPatch();
      int k = localPatch.getProgram();
      int m = localPatch.getBank();
      if ((i == k) && (j == m))
      {
        boolean bool2 = false;
        if ((localPatch instanceof ModelPatch)) {
          bool2 = ((ModelPatch)localPatch).isPercussion();
        }
        if (bool1 == bool2) {
          return localInstrument;
        }
      }
    }
    return null;
  }
  
  public void addResource(SoundbankResource paramSoundbankResource)
  {
    if ((paramSoundbankResource instanceof Instrument)) {
      instruments.add((Instrument)paramSoundbankResource);
    } else {
      resources.add(paramSoundbankResource);
    }
  }
  
  public void removeResource(SoundbankResource paramSoundbankResource)
  {
    if ((paramSoundbankResource instanceof Instrument)) {
      instruments.remove((Instrument)paramSoundbankResource);
    } else {
      resources.remove(paramSoundbankResource);
    }
  }
  
  public void addInstrument(Instrument paramInstrument)
  {
    instruments.add(paramInstrument);
  }
  
  public void removeInstrument(Instrument paramInstrument)
  {
    instruments.remove(paramInstrument);
  }
  
  public void addAllInstruments(Soundbank paramSoundbank)
  {
    for (Instrument localInstrument : paramSoundbank.getInstruments()) {
      addInstrument(localInstrument);
    }
  }
  
  public void removeAllInstruments(Soundbank paramSoundbank)
  {
    for (Instrument localInstrument : paramSoundbank.getInstruments()) {
      removeInstrument(localInstrument);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SimpleSoundbank.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */