package com.sun.media.sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class SoftPerformer
{
  static ModelConnectionBlock[] defaultconnections = new ModelConnectionBlock[42];
  public int keyFrom = 0;
  public int keyTo = 127;
  public int velFrom = 0;
  public int velTo = 127;
  public int exclusiveClass = 0;
  public boolean selfNonExclusive = false;
  public boolean forcedVelocity = false;
  public boolean forcedKeynumber = false;
  public ModelPerformer performer;
  public ModelConnectionBlock[] connections;
  public ModelOscillator[] oscillators;
  public Map<Integer, int[]> midi_rpn_connections = new HashMap();
  public Map<Integer, int[]> midi_nrpn_connections = new HashMap();
  public int[][] midi_ctrl_connections;
  public int[][] midi_connections;
  public int[] ctrl_connections;
  private List<Integer> ctrl_connections_list = new ArrayList();
  private static KeySortComparator keySortComparator = new KeySortComparator(null);
  
  private String extractKeys(ModelConnectionBlock paramModelConnectionBlock)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (paramModelConnectionBlock.getSources() != null)
    {
      localStringBuffer.append("[");
      ModelSource[] arrayOfModelSource1 = paramModelConnectionBlock.getSources();
      ModelSource[] arrayOfModelSource2 = new ModelSource[arrayOfModelSource1.length];
      for (int i = 0; i < arrayOfModelSource1.length; i++) {
        arrayOfModelSource2[i] = arrayOfModelSource1[i];
      }
      Arrays.sort(arrayOfModelSource2, keySortComparator);
      for (i = 0; i < arrayOfModelSource1.length; i++)
      {
        localStringBuffer.append(arrayOfModelSource1[i].getIdentifier());
        localStringBuffer.append(";");
      }
      localStringBuffer.append("]");
    }
    localStringBuffer.append(";");
    if (paramModelConnectionBlock.getDestination() != null) {
      localStringBuffer.append(paramModelConnectionBlock.getDestination().getIdentifier());
    }
    localStringBuffer.append(";");
    return localStringBuffer.toString();
  }
  
  private void processSource(ModelSource paramModelSource, int paramInt)
  {
    ModelIdentifier localModelIdentifier = paramModelSource.getIdentifier();
    String str = localModelIdentifier.getObject();
    if (str.equals("midi_cc"))
    {
      processMidiControlSource(paramModelSource, paramInt);
    }
    else if (str.equals("midi_rpn"))
    {
      processMidiRpnSource(paramModelSource, paramInt);
    }
    else if (str.equals("midi_nrpn"))
    {
      processMidiNrpnSource(paramModelSource, paramInt);
    }
    else if (str.equals("midi"))
    {
      processMidiSource(paramModelSource, paramInt);
    }
    else if (str.equals("noteon"))
    {
      processNoteOnSource(paramModelSource, paramInt);
    }
    else
    {
      if (str.equals("osc")) {
        return;
      }
      if (str.equals("mixer")) {
        return;
      }
      ctrl_connections_list.add(Integer.valueOf(paramInt));
    }
  }
  
  private void processMidiControlSource(ModelSource paramModelSource, int paramInt)
  {
    String str = paramModelSource.getIdentifier().getVariable();
    if (str == null) {
      return;
    }
    int i = Integer.parseInt(str);
    if (midi_ctrl_connections[i] == null)
    {
      midi_ctrl_connections[i] = { paramInt };
    }
    else
    {
      int[] arrayOfInt1 = midi_ctrl_connections[i];
      int[] arrayOfInt2 = new int[arrayOfInt1.length + 1];
      for (int j = 0; j < arrayOfInt1.length; j++) {
        arrayOfInt2[j] = arrayOfInt1[j];
      }
      arrayOfInt2[(arrayOfInt2.length - 1)] = paramInt;
      midi_ctrl_connections[i] = arrayOfInt2;
    }
  }
  
  private void processNoteOnSource(ModelSource paramModelSource, int paramInt)
  {
    String str = paramModelSource.getIdentifier().getVariable();
    int i = -1;
    if (str.equals("on")) {
      i = 3;
    }
    if (str.equals("keynumber")) {
      i = 4;
    }
    if (i == -1) {
      return;
    }
    if (midi_connections[i] == null)
    {
      midi_connections[i] = { paramInt };
    }
    else
    {
      int[] arrayOfInt1 = midi_connections[i];
      int[] arrayOfInt2 = new int[arrayOfInt1.length + 1];
      for (int j = 0; j < arrayOfInt1.length; j++) {
        arrayOfInt2[j] = arrayOfInt1[j];
      }
      arrayOfInt2[(arrayOfInt2.length - 1)] = paramInt;
      midi_connections[i] = arrayOfInt2;
    }
  }
  
  private void processMidiSource(ModelSource paramModelSource, int paramInt)
  {
    String str = paramModelSource.getIdentifier().getVariable();
    int i = -1;
    if (str.equals("pitch")) {
      i = 0;
    }
    if (str.equals("channel_pressure")) {
      i = 1;
    }
    if (str.equals("poly_pressure")) {
      i = 2;
    }
    if (i == -1) {
      return;
    }
    if (midi_connections[i] == null)
    {
      midi_connections[i] = { paramInt };
    }
    else
    {
      int[] arrayOfInt1 = midi_connections[i];
      int[] arrayOfInt2 = new int[arrayOfInt1.length + 1];
      for (int j = 0; j < arrayOfInt1.length; j++) {
        arrayOfInt2[j] = arrayOfInt1[j];
      }
      arrayOfInt2[(arrayOfInt2.length - 1)] = paramInt;
      midi_connections[i] = arrayOfInt2;
    }
  }
  
  private void processMidiRpnSource(ModelSource paramModelSource, int paramInt)
  {
    String str = paramModelSource.getIdentifier().getVariable();
    if (str == null) {
      return;
    }
    int i = Integer.parseInt(str);
    if (midi_rpn_connections.get(Integer.valueOf(i)) == null)
    {
      midi_rpn_connections.put(Integer.valueOf(i), new int[] { paramInt });
    }
    else
    {
      int[] arrayOfInt1 = (int[])midi_rpn_connections.get(Integer.valueOf(i));
      int[] arrayOfInt2 = new int[arrayOfInt1.length + 1];
      for (int j = 0; j < arrayOfInt1.length; j++) {
        arrayOfInt2[j] = arrayOfInt1[j];
      }
      arrayOfInt2[(arrayOfInt2.length - 1)] = paramInt;
      midi_rpn_connections.put(Integer.valueOf(i), arrayOfInt2);
    }
  }
  
  private void processMidiNrpnSource(ModelSource paramModelSource, int paramInt)
  {
    String str = paramModelSource.getIdentifier().getVariable();
    if (str == null) {
      return;
    }
    int i = Integer.parseInt(str);
    if (midi_nrpn_connections.get(Integer.valueOf(i)) == null)
    {
      midi_nrpn_connections.put(Integer.valueOf(i), new int[] { paramInt });
    }
    else
    {
      int[] arrayOfInt1 = (int[])midi_nrpn_connections.get(Integer.valueOf(i));
      int[] arrayOfInt2 = new int[arrayOfInt1.length + 1];
      for (int j = 0; j < arrayOfInt1.length; j++) {
        arrayOfInt2[j] = arrayOfInt1[j];
      }
      arrayOfInt2[(arrayOfInt2.length - 1)] = paramInt;
      midi_nrpn_connections.put(Integer.valueOf(i), arrayOfInt2);
    }
  }
  
  public SoftPerformer(ModelPerformer paramModelPerformer)
  {
    performer = paramModelPerformer;
    keyFrom = paramModelPerformer.getKeyFrom();
    keyTo = paramModelPerformer.getKeyTo();
    velFrom = paramModelPerformer.getVelFrom();
    velTo = paramModelPerformer.getVelTo();
    exclusiveClass = paramModelPerformer.getExclusiveClass();
    selfNonExclusive = paramModelPerformer.isSelfNonExclusive();
    HashMap localHashMap = new HashMap();
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(paramModelPerformer.getConnectionBlocks());
    int k;
    Object localObject3;
    if (paramModelPerformer.isDefaultConnectionsEnabled())
    {
      int i = 0;
      for (int j = 0; j < localArrayList.size(); j++)
      {
        ModelConnectionBlock localModelConnectionBlock3 = (ModelConnectionBlock)localArrayList.get(j);
        localObject2 = localModelConnectionBlock3.getSources();
        ModelDestination localModelDestination1 = localModelConnectionBlock3.getDestination();
        int i3 = 0;
        if ((localModelDestination1 != null) && (localObject2 != null) && (localObject2.length > 1)) {
          for (int i4 = 0; i4 < localObject2.length; i4++) {
            if ((localObject2[i4].getIdentifier().getObject().equals("midi_cc")) && (localObject2[i4].getIdentifier().getVariable().equals("1")))
            {
              i3 = 1;
              i = 1;
              break;
            }
          }
        }
        if (i3 != 0)
        {
          localObject4 = new ModelConnectionBlock();
          ((ModelConnectionBlock)localObject4).setSources(localModelConnectionBlock3.getSources());
          ((ModelConnectionBlock)localObject4).setDestination(localModelConnectionBlock3.getDestination());
          ((ModelConnectionBlock)localObject4).addSource(new ModelSource(new ModelIdentifier("midi_rpn", "5")));
          ((ModelConnectionBlock)localObject4).setScale(localModelConnectionBlock3.getScale() * 256.0D);
          localArrayList.set(j, localObject4);
        }
      }
      if (i == 0)
      {
        ModelConnectionBlock localModelConnectionBlock1 = new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, true, 0), new ModelSource(new ModelIdentifier("midi_cc", "1", 0), false, false, 0), 50.0D, new ModelDestination(ModelDestination.DESTINATION_PITCH));
        localModelConnectionBlock1.addSource(new ModelSource(new ModelIdentifier("midi_rpn", "5")));
        localModelConnectionBlock1.setScale(localModelConnectionBlock1.getScale() * 256.0D);
        localArrayList.add(localModelConnectionBlock1);
      }
      k = 0;
      n = 0;
      localObject2 = null;
      int i2 = 0;
      localObject3 = localArrayList.iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localObject4 = (ModelConnectionBlock)((Iterator)localObject3).next();
        localObject5 = ((ModelConnectionBlock)localObject4).getSources();
        ModelDestination localModelDestination2 = ((ModelConnectionBlock)localObject4).getDestination();
        if ((localModelDestination2 != null) && (localObject5 != null)) {
          for (int i9 = 0; i9 < localObject5.length; i9++)
          {
            ModelIdentifier localModelIdentifier2 = localObject5[i9].getIdentifier();
            if ((localModelIdentifier2.getObject().equals("midi_cc")) && (localModelIdentifier2.getVariable().equals("1")))
            {
              localObject2 = localObject4;
              i2 = i9;
            }
            if (localModelIdentifier2.getObject().equals("midi"))
            {
              if (localModelIdentifier2.getVariable().equals("channel_pressure")) {
                k = 1;
              }
              if (localModelIdentifier2.getVariable().equals("poly_pressure")) {
                n = 1;
              }
            }
          }
        }
      }
      if (localObject2 != null)
      {
        int i8;
        if (k == 0)
        {
          localObject3 = new ModelConnectionBlock();
          ((ModelConnectionBlock)localObject3).setDestination(((ModelConnectionBlock)localObject2).getDestination());
          ((ModelConnectionBlock)localObject3).setScale(((ModelConnectionBlock)localObject2).getScale());
          localObject4 = ((ModelConnectionBlock)localObject2).getSources();
          localObject5 = new ModelSource[localObject4.length];
          for (i8 = 0; i8 < localObject5.length; i8++) {
            localObject5[i8] = localObject4[i8];
          }
          localObject5[i2] = new ModelSource(new ModelIdentifier("midi", "channel_pressure"));
          ((ModelConnectionBlock)localObject3).setSources((ModelSource[])localObject5);
          localHashMap.put(extractKeys((ModelConnectionBlock)localObject3), localObject3);
        }
        if (n == 0)
        {
          localObject3 = new ModelConnectionBlock();
          ((ModelConnectionBlock)localObject3).setDestination(((ModelConnectionBlock)localObject2).getDestination());
          ((ModelConnectionBlock)localObject3).setScale(((ModelConnectionBlock)localObject2).getScale());
          localObject4 = ((ModelConnectionBlock)localObject2).getSources();
          localObject5 = new ModelSource[localObject4.length];
          for (i8 = 0; i8 < localObject5.length; i8++) {
            localObject5[i8] = localObject4[i8];
          }
          localObject5[i2] = new ModelSource(new ModelIdentifier("midi", "poly_pressure"));
          ((ModelConnectionBlock)localObject3).setSources((ModelSource[])localObject5);
          localHashMap.put(extractKeys((ModelConnectionBlock)localObject3), localObject3);
        }
      }
      localObject3 = null;
      Object localObject4 = localArrayList.iterator();
      while (((Iterator)localObject4).hasNext())
      {
        localObject5 = (ModelConnectionBlock)((Iterator)localObject4).next();
        ModelSource[] arrayOfModelSource = ((ModelConnectionBlock)localObject5).getSources();
        if ((arrayOfModelSource.length != 0) && (arrayOfModelSource[0].getIdentifier().getObject().equals("lfo")) && (((ModelConnectionBlock)localObject5).getDestination().getIdentifier().equals(ModelDestination.DESTINATION_PITCH))) {
          if (localObject3 == null) {
            localObject3 = localObject5;
          } else if (((ModelConnectionBlock)localObject3).getSources().length > arrayOfModelSource.length) {
            localObject3 = localObject5;
          } else if ((localObject3.getSources()[0].getIdentifier().getInstance() < 1) && (localObject3.getSources()[0].getIdentifier().getInstance() > arrayOfModelSource[0].getIdentifier().getInstance())) {
            localObject3 = localObject5;
          }
        }
      }
      int i5 = 1;
      if (localObject3 != null) {
        i5 = localObject3.getSources()[0].getIdentifier().getInstance();
      }
      Object localObject5 = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "78"), false, true, 0), 2000.0D, new ModelDestination(new ModelIdentifier("lfo", "delay2", i5)));
      localHashMap.put(extractKeys((ModelConnectionBlock)localObject5), localObject5);
      final double d = localObject3 == null ? 0.0D : ((ModelConnectionBlock)localObject3).getScale();
      localObject5 = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("lfo", i5)), new ModelSource(new ModelIdentifier("midi_cc", "77"), new ModelTransform()
      {
        double s = d;
        
        public double transform(double paramAnonymousDouble)
        {
          paramAnonymousDouble = paramAnonymousDouble * 2.0D - 1.0D;
          paramAnonymousDouble *= 600.0D;
          if (s == 0.0D) {
            return paramAnonymousDouble;
          }
          if (s > 0.0D)
          {
            if (paramAnonymousDouble < -s) {
              paramAnonymousDouble = -s;
            }
            return paramAnonymousDouble;
          }
          if (paramAnonymousDouble < s) {
            paramAnonymousDouble = -s;
          }
          return -paramAnonymousDouble;
        }
      }), new ModelDestination(ModelDestination.DESTINATION_PITCH));
      localHashMap.put(extractKeys((ModelConnectionBlock)localObject5), localObject5);
      localObject5 = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "76"), false, true, 0), 2400.0D, new ModelDestination(new ModelIdentifier("lfo", "freq", i5)));
      localHashMap.put(extractKeys((ModelConnectionBlock)localObject5), localObject5);
    }
    if (paramModelPerformer.isDefaultConnectionsEnabled()) {
      for (localObject2 : defaultconnections) {
        localHashMap.put(extractKeys((ModelConnectionBlock)localObject2), localObject2);
      }
    }
    ??? = localArrayList.iterator();
    while (((Iterator)???).hasNext())
    {
      ModelConnectionBlock localModelConnectionBlock2 = (ModelConnectionBlock)((Iterator)???).next();
      localHashMap.put(extractKeys(localModelConnectionBlock2), localModelConnectionBlock2);
    }
    ??? = new ArrayList();
    midi_ctrl_connections = new int[''][];
    for (int m = 0; m < midi_ctrl_connections.length; m++) {
      midi_ctrl_connections[m] = null;
    }
    midi_connections = new int[5][];
    for (m = 0; m < midi_connections.length; m++) {
      midi_connections[m] = null;
    }
    m = 0;
    int n = 0;
    Object localObject2 = localHashMap.values().iterator();
    ModelConnectionBlock localModelConnectionBlock4;
    while (((Iterator)localObject2).hasNext())
    {
      localModelConnectionBlock4 = (ModelConnectionBlock)((Iterator)localObject2).next();
      if (localModelConnectionBlock4.getDestination() != null)
      {
        localObject3 = localModelConnectionBlock4.getDestination();
        ModelIdentifier localModelIdentifier1 = ((ModelDestination)localObject3).getIdentifier();
        if (localModelIdentifier1.getObject().equals("noteon"))
        {
          n = 1;
          if (localModelIdentifier1.getVariable().equals("keynumber")) {
            forcedKeynumber = true;
          }
          if (localModelIdentifier1.getVariable().equals("velocity")) {
            forcedVelocity = true;
          }
        }
      }
      if (n != 0)
      {
        ((List)???).add(0, localModelConnectionBlock4);
        n = 0;
      }
      else
      {
        ((List)???).add(localModelConnectionBlock4);
      }
    }
    localObject2 = ((List)???).iterator();
    int i6;
    while (((Iterator)localObject2).hasNext())
    {
      localModelConnectionBlock4 = (ModelConnectionBlock)((Iterator)localObject2).next();
      if (localModelConnectionBlock4.getSources() != null)
      {
        localObject3 = localModelConnectionBlock4.getSources();
        for (i6 = 0; i6 < localObject3.length; i6++) {
          processSource(localObject3[i6], m);
        }
      }
      m++;
    }
    connections = new ModelConnectionBlock[((List)???).size()];
    ((List)???).toArray(connections);
    ctrl_connections = new int[ctrl_connections_list.size()];
    for (int i1 = 0; i1 < ctrl_connections.length; i1++) {
      ctrl_connections[i1] = ((Integer)ctrl_connections_list.get(i1)).intValue();
    }
    oscillators = new ModelOscillator[paramModelPerformer.getOscillators().size()];
    paramModelPerformer.getOscillators().toArray(oscillators);
    Iterator localIterator = ((List)???).iterator();
    while (localIterator.hasNext())
    {
      localModelConnectionBlock4 = (ModelConnectionBlock)localIterator.next();
      if ((localModelConnectionBlock4.getDestination() != null) && (isUnnecessaryTransform(localModelConnectionBlock4.getDestination().getTransform()))) {
        localModelConnectionBlock4.getDestination().setTransform(null);
      }
      if (localModelConnectionBlock4.getSources() != null) {
        for (Object localObject6 : localModelConnectionBlock4.getSources()) {
          if (isUnnecessaryTransform(((ModelSource)localObject6).getTransform())) {
            ((ModelSource)localObject6).setTransform(null);
          }
        }
      }
    }
  }
  
  private static boolean isUnnecessaryTransform(ModelTransform paramModelTransform)
  {
    if (paramModelTransform == null) {
      return false;
    }
    if (!(paramModelTransform instanceof ModelStandardTransform)) {
      return false;
    }
    ModelStandardTransform localModelStandardTransform = (ModelStandardTransform)paramModelTransform;
    if (localModelStandardTransform.getDirection()) {
      return false;
    }
    if (localModelStandardTransform.getPolarity()) {
      return false;
    }
    return localModelStandardTransform.getTransform() == 0;
  }
  
  static
  {
    int i = 0;
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("noteon", "on", 0), false, false, 0), 1.0D, new ModelDestination(new ModelIdentifier("eg", "on", 0)));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("noteon", "on", 0), false, false, 0), 1.0D, new ModelDestination(new ModelIdentifier("eg", "on", 1)));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("eg", "active", 0), false, false, 0), 1.0D, new ModelDestination(new ModelIdentifier("mixer", "active", 0)));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("eg", 0), true, false, 0), -960.0D, new ModelDestination(new ModelIdentifier("mixer", "gain")));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("noteon", "velocity"), true, false, 1), -960.0D, new ModelDestination(new ModelIdentifier("mixer", "gain")));
    defaultconnections[(i++) = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi", "pitch"), false, true, 0), new ModelSource(new ModelIdentifier("midi_rpn", "0"), new ModelTransform()
    {
      public double transform(double paramAnonymousDouble)
      {
        int i = (int)(paramAnonymousDouble * 16384.0D);
        int j = i >> 7;
        int k = i & 0x7F;
        return j * 100 + k;
      }
    }), new ModelDestination(new ModelIdentifier("osc", "pitch")));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("noteon", "keynumber"), false, false, 0), 12800.0D, new ModelDestination(new ModelIdentifier("osc", "pitch")));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "7"), true, false, 1), -960.0D, new ModelDestination(new ModelIdentifier("mixer", "gain")));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "8"), false, false, 0), 1000.0D, new ModelDestination(new ModelIdentifier("mixer", "balance")));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "10"), false, false, 0), 1000.0D, new ModelDestination(new ModelIdentifier("mixer", "pan")));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "11"), true, false, 1), -960.0D, new ModelDestination(new ModelIdentifier("mixer", "gain")));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "91"), false, false, 0), 1000.0D, new ModelDestination(new ModelIdentifier("mixer", "reverb")));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "93"), false, false, 0), 1000.0D, new ModelDestination(new ModelIdentifier("mixer", "chorus")));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "71"), false, true, 0), 200.0D, new ModelDestination(new ModelIdentifier("filter", "q")));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "74"), false, true, 0), 9600.0D, new ModelDestination(new ModelIdentifier("filter", "freq")));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "72"), false, true, 0), 6000.0D, new ModelDestination(new ModelIdentifier("eg", "release2")));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "73"), false, true, 0), 2000.0D, new ModelDestination(new ModelIdentifier("eg", "attack2")));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "75"), false, true, 0), 6000.0D, new ModelDestination(new ModelIdentifier("eg", "decay2")));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "67"), false, false, 3), -50.0D, new ModelDestination(ModelDestination.DESTINATION_GAIN));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "67"), false, false, 3), -2400.0D, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_rpn", "1"), false, true, 0), 100.0D, new ModelDestination(new ModelIdentifier("osc", "pitch")));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_rpn", "2"), false, true, 0), 12800.0D, new ModelDestination(new ModelIdentifier("osc", "pitch")));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("master", "fine_tuning"), false, true, 0), 100.0D, new ModelDestination(new ModelIdentifier("osc", "pitch")));
    defaultconnections[(i++)] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("master", "coarse_tuning"), false, true, 0), 12800.0D, new ModelDestination(new ModelIdentifier("osc", "pitch")));
    defaultconnections[(i++)] = new ModelConnectionBlock(13500.0D, new ModelDestination(new ModelIdentifier("filter", "freq", 0)));
    defaultconnections[(i++)] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "delay", 0)));
    defaultconnections[(i++)] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "attack", 0)));
    defaultconnections[(i++)] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "hold", 0)));
    defaultconnections[(i++)] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "decay", 0)));
    defaultconnections[(i++)] = new ModelConnectionBlock(1000.0D, new ModelDestination(new ModelIdentifier("eg", "sustain", 0)));
    defaultconnections[(i++)] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "release", 0)));
    defaultconnections[(i++)] = new ModelConnectionBlock(1200.0D * Math.log(0.015D) / Math.log(2.0D), new ModelDestination(new ModelIdentifier("eg", "shutdown", 0)));
    defaultconnections[(i++)] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "delay", 1)));
    defaultconnections[(i++)] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "attack", 1)));
    defaultconnections[(i++)] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "hold", 1)));
    defaultconnections[(i++)] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "decay", 1)));
    defaultconnections[(i++)] = new ModelConnectionBlock(1000.0D, new ModelDestination(new ModelIdentifier("eg", "sustain", 1)));
    defaultconnections[(i++)] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "release", 1)));
    defaultconnections[(i++)] = new ModelConnectionBlock(-8.51318D, new ModelDestination(new ModelIdentifier("lfo", "freq", 0)));
    defaultconnections[(i++)] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("lfo", "delay", 0)));
    defaultconnections[(i++)] = new ModelConnectionBlock(-8.51318D, new ModelDestination(new ModelIdentifier("lfo", "freq", 1)));
    defaultconnections[(i++)] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("lfo", "delay", 1)));
  }
  
  private static class KeySortComparator
    implements Comparator<ModelSource>
  {
    private KeySortComparator() {}
    
    public int compare(ModelSource paramModelSource1, ModelSource paramModelSource2)
    {
      return paramModelSource1.getIdentifier().toString().compareTo(paramModelSource2.getIdentifier().toString());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftPerformer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */