package com.sun.media.sound;

import java.util.Comparator;
import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;

public final class ModelInstrumentComparator
  implements Comparator<Instrument>
{
  public ModelInstrumentComparator() {}
  
  public int compare(Instrument paramInstrument1, Instrument paramInstrument2)
  {
    Patch localPatch1 = paramInstrument1.getPatch();
    Patch localPatch2 = paramInstrument2.getPatch();
    int i = localPatch1.getBank() * 128 + localPatch1.getProgram();
    int j = localPatch2.getBank() * 128 + localPatch2.getProgram();
    if ((localPatch1 instanceof ModelPatch)) {
      i += (((ModelPatch)localPatch1).isPercussion() ? 2097152 : 0);
    }
    if ((localPatch2 instanceof ModelPatch)) {
      j += (((ModelPatch)localPatch2).isPercussion() ? 2097152 : 0);
    }
    return i - j;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\ModelInstrumentComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */