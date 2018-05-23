package java.util.stream;

import java.util.EnumMap;
import java.util.Map;
import java.util.Spliterator;

 enum StreamOpFlag
{
  DISTINCT(0, set(Type.SPLITERATOR).set(Type.STREAM).setAndClear(Type.OP)),  SORTED(1, set(Type.SPLITERATOR).set(Type.STREAM).setAndClear(Type.OP)),  ORDERED(2, set(Type.SPLITERATOR).set(Type.STREAM).setAndClear(Type.OP).clear(Type.TERMINAL_OP).clear(Type.UPSTREAM_TERMINAL_OP)),  SIZED(3, set(Type.SPLITERATOR).set(Type.STREAM).clear(Type.OP)),  SHORT_CIRCUIT(12, set(Type.OP).set(Type.TERMINAL_OP));
  
  private static final int SET_BITS = 1;
  private static final int CLEAR_BITS = 2;
  private static final int PRESERVE_BITS = 3;
  private final Map<Type, Integer> maskTable;
  private final int bitPosition;
  private final int set;
  private final int clear;
  private final int preserve;
  static final int SPLITERATOR_CHARACTERISTICS_MASK = createMask(Type.SPLITERATOR);
  static final int STREAM_MASK = createMask(Type.STREAM);
  static final int OP_MASK = createMask(Type.OP);
  static final int TERMINAL_OP_MASK = createMask(Type.TERMINAL_OP);
  static final int UPSTREAM_TERMINAL_OP_MASK = createMask(Type.UPSTREAM_TERMINAL_OP);
  private static final int FLAG_MASK = createFlagMask();
  private static final int FLAG_MASK_IS = STREAM_MASK;
  private static final int FLAG_MASK_NOT = STREAM_MASK << 1;
  static final int INITIAL_OPS_VALUE = FLAG_MASK_IS | FLAG_MASK_NOT;
  static final int IS_DISTINCT = DISTINCTset;
  static final int NOT_DISTINCT = DISTINCTclear;
  static final int IS_SORTED = SORTEDset;
  static final int NOT_SORTED = SORTEDclear;
  static final int IS_ORDERED = ORDEREDset;
  static final int NOT_ORDERED = ORDEREDclear;
  static final int IS_SIZED = SIZEDset;
  static final int NOT_SIZED = SIZEDclear;
  static final int IS_SHORT_CIRCUIT = SHORT_CIRCUITset;
  
  private static MaskBuilder set(Type paramType)
  {
    return new MaskBuilder(new EnumMap(Type.class)).set(paramType);
  }
  
  private StreamOpFlag(int paramInt, MaskBuilder paramMaskBuilder)
  {
    maskTable = paramMaskBuilder.build();
    paramInt *= 2;
    bitPosition = paramInt;
    set = (1 << paramInt);
    clear = (2 << paramInt);
    preserve = (3 << paramInt);
  }
  
  int set()
  {
    return set;
  }
  
  int clear()
  {
    return clear;
  }
  
  boolean isStreamFlag()
  {
    return ((Integer)maskTable.get(Type.STREAM)).intValue() > 0;
  }
  
  boolean isKnown(int paramInt)
  {
    return (paramInt & preserve) == set;
  }
  
  boolean isCleared(int paramInt)
  {
    return (paramInt & preserve) == clear;
  }
  
  boolean isPreserved(int paramInt)
  {
    return (paramInt & preserve) == preserve;
  }
  
  boolean canSet(Type paramType)
  {
    return (((Integer)maskTable.get(paramType)).intValue() & 0x1) > 0;
  }
  
  private static int createMask(Type paramType)
  {
    int i = 0;
    for (StreamOpFlag localStreamOpFlag : values()) {
      i |= ((Integer)maskTable.get(paramType)).intValue() << bitPosition;
    }
    return i;
  }
  
  private static int createFlagMask()
  {
    int i = 0;
    for (StreamOpFlag localStreamOpFlag : values()) {
      i |= preserve;
    }
    return i;
  }
  
  private static int getMask(int paramInt)
  {
    return paramInt == 0 ? FLAG_MASK : (paramInt | (FLAG_MASK_IS & paramInt) << 1 | (FLAG_MASK_NOT & paramInt) >> 1) ^ 0xFFFFFFFF;
  }
  
  static int combineOpFlags(int paramInt1, int paramInt2)
  {
    return paramInt2 & getMask(paramInt1) | paramInt1;
  }
  
  static int toStreamFlags(int paramInt)
  {
    return (paramInt ^ 0xFFFFFFFF) >> 1 & FLAG_MASK_IS & paramInt;
  }
  
  static int toCharacteristics(int paramInt)
  {
    return paramInt & SPLITERATOR_CHARACTERISTICS_MASK;
  }
  
  static int fromCharacteristics(Spliterator<?> paramSpliterator)
  {
    int i = paramSpliterator.characteristics();
    if (((i & 0x4) != 0) && (paramSpliterator.getComparator() != null)) {
      return i & SPLITERATOR_CHARACTERISTICS_MASK & 0xFFFFFFFB;
    }
    return i & SPLITERATOR_CHARACTERISTICS_MASK;
  }
  
  static int fromCharacteristics(int paramInt)
  {
    return paramInt & SPLITERATOR_CHARACTERISTICS_MASK;
  }
  
  private static class MaskBuilder
  {
    final Map<StreamOpFlag.Type, Integer> map;
    
    MaskBuilder(Map<StreamOpFlag.Type, Integer> paramMap)
    {
      map = paramMap;
    }
    
    MaskBuilder mask(StreamOpFlag.Type paramType, Integer paramInteger)
    {
      map.put(paramType, paramInteger);
      return this;
    }
    
    MaskBuilder set(StreamOpFlag.Type paramType)
    {
      return mask(paramType, Integer.valueOf(1));
    }
    
    MaskBuilder clear(StreamOpFlag.Type paramType)
    {
      return mask(paramType, Integer.valueOf(2));
    }
    
    MaskBuilder setAndClear(StreamOpFlag.Type paramType)
    {
      return mask(paramType, Integer.valueOf(3));
    }
    
    Map<StreamOpFlag.Type, Integer> build()
    {
      for (StreamOpFlag.Type localType : ) {
        map.putIfAbsent(localType, Integer.valueOf(0));
      }
      return map;
    }
  }
  
  static enum Type
  {
    SPLITERATOR,  STREAM,  OP,  TERMINAL_OP,  UPSTREAM_TERMINAL_OP;
    
    private Type() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\stream\StreamOpFlag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */