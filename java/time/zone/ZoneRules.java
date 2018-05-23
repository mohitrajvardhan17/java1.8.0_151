package java.time.zone;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ZoneRules
  implements Serializable
{
  private static final long serialVersionUID = 3044319355680032515L;
  private static final int LAST_CACHED_YEAR = 2100;
  private final long[] standardTransitions;
  private final ZoneOffset[] standardOffsets;
  private final long[] savingsInstantTransitions;
  private final LocalDateTime[] savingsLocalTransitions;
  private final ZoneOffset[] wallOffsets;
  private final ZoneOffsetTransitionRule[] lastRules;
  private final transient ConcurrentMap<Integer, ZoneOffsetTransition[]> lastRulesCache = new ConcurrentHashMap();
  private static final long[] EMPTY_LONG_ARRAY = new long[0];
  private static final ZoneOffsetTransitionRule[] EMPTY_LASTRULES = new ZoneOffsetTransitionRule[0];
  private static final LocalDateTime[] EMPTY_LDT_ARRAY = new LocalDateTime[0];
  
  public static ZoneRules of(ZoneOffset paramZoneOffset1, ZoneOffset paramZoneOffset2, List<ZoneOffsetTransition> paramList1, List<ZoneOffsetTransition> paramList2, List<ZoneOffsetTransitionRule> paramList)
  {
    Objects.requireNonNull(paramZoneOffset1, "baseStandardOffset");
    Objects.requireNonNull(paramZoneOffset2, "baseWallOffset");
    Objects.requireNonNull(paramList1, "standardOffsetTransitionList");
    Objects.requireNonNull(paramList2, "transitionList");
    Objects.requireNonNull(paramList, "lastRules");
    return new ZoneRules(paramZoneOffset1, paramZoneOffset2, paramList1, paramList2, paramList);
  }
  
  public static ZoneRules of(ZoneOffset paramZoneOffset)
  {
    Objects.requireNonNull(paramZoneOffset, "offset");
    return new ZoneRules(paramZoneOffset);
  }
  
  ZoneRules(ZoneOffset paramZoneOffset1, ZoneOffset paramZoneOffset2, List<ZoneOffsetTransition> paramList1, List<ZoneOffsetTransition> paramList2, List<ZoneOffsetTransitionRule> paramList)
  {
    standardTransitions = new long[paramList1.size()];
    standardOffsets = new ZoneOffset[paramList1.size() + 1];
    standardOffsets[0] = paramZoneOffset1;
    for (int i = 0; i < paramList1.size(); i++)
    {
      standardTransitions[i] = ((ZoneOffsetTransition)paramList1.get(i)).toEpochSecond();
      standardOffsets[(i + 1)] = ((ZoneOffsetTransition)paramList1.get(i)).getOffsetAfter();
    }
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    localArrayList2.add(paramZoneOffset2);
    Iterator localIterator = paramList2.iterator();
    while (localIterator.hasNext())
    {
      ZoneOffsetTransition localZoneOffsetTransition = (ZoneOffsetTransition)localIterator.next();
      if (localZoneOffsetTransition.isGap())
      {
        localArrayList1.add(localZoneOffsetTransition.getDateTimeBefore());
        localArrayList1.add(localZoneOffsetTransition.getDateTimeAfter());
      }
      else
      {
        localArrayList1.add(localZoneOffsetTransition.getDateTimeAfter());
        localArrayList1.add(localZoneOffsetTransition.getDateTimeBefore());
      }
      localArrayList2.add(localZoneOffsetTransition.getOffsetAfter());
    }
    savingsLocalTransitions = ((LocalDateTime[])localArrayList1.toArray(new LocalDateTime[localArrayList1.size()]));
    wallOffsets = ((ZoneOffset[])localArrayList2.toArray(new ZoneOffset[localArrayList2.size()]));
    savingsInstantTransitions = new long[paramList2.size()];
    for (int j = 0; j < paramList2.size(); j++) {
      savingsInstantTransitions[j] = ((ZoneOffsetTransition)paramList2.get(j)).toEpochSecond();
    }
    if (paramList.size() > 16) {
      throw new IllegalArgumentException("Too many transition rules");
    }
    lastRules = ((ZoneOffsetTransitionRule[])paramList.toArray(new ZoneOffsetTransitionRule[paramList.size()]));
  }
  
  private ZoneRules(long[] paramArrayOfLong1, ZoneOffset[] paramArrayOfZoneOffset1, long[] paramArrayOfLong2, ZoneOffset[] paramArrayOfZoneOffset2, ZoneOffsetTransitionRule[] paramArrayOfZoneOffsetTransitionRule)
  {
    standardTransitions = paramArrayOfLong1;
    standardOffsets = paramArrayOfZoneOffset1;
    savingsInstantTransitions = paramArrayOfLong2;
    wallOffsets = paramArrayOfZoneOffset2;
    lastRules = paramArrayOfZoneOffsetTransitionRule;
    if (paramArrayOfLong2.length == 0)
    {
      savingsLocalTransitions = EMPTY_LDT_ARRAY;
    }
    else
    {
      ArrayList localArrayList = new ArrayList();
      for (int i = 0; i < paramArrayOfLong2.length; i++)
      {
        ZoneOffset localZoneOffset1 = paramArrayOfZoneOffset2[i];
        ZoneOffset localZoneOffset2 = paramArrayOfZoneOffset2[(i + 1)];
        ZoneOffsetTransition localZoneOffsetTransition = new ZoneOffsetTransition(paramArrayOfLong2[i], localZoneOffset1, localZoneOffset2);
        if (localZoneOffsetTransition.isGap())
        {
          localArrayList.add(localZoneOffsetTransition.getDateTimeBefore());
          localArrayList.add(localZoneOffsetTransition.getDateTimeAfter());
        }
        else
        {
          localArrayList.add(localZoneOffsetTransition.getDateTimeAfter());
          localArrayList.add(localZoneOffsetTransition.getDateTimeBefore());
        }
      }
      savingsLocalTransitions = ((LocalDateTime[])localArrayList.toArray(new LocalDateTime[localArrayList.size()]));
    }
  }
  
  private ZoneRules(ZoneOffset paramZoneOffset)
  {
    standardOffsets = new ZoneOffset[1];
    standardOffsets[0] = paramZoneOffset;
    standardTransitions = EMPTY_LONG_ARRAY;
    savingsInstantTransitions = EMPTY_LONG_ARRAY;
    savingsLocalTransitions = EMPTY_LDT_ARRAY;
    wallOffsets = standardOffsets;
    lastRules = EMPTY_LASTRULES;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws InvalidObjectException
  {
    throw new InvalidObjectException("Deserialization via serialization delegate");
  }
  
  private Object writeReplace()
  {
    return new Ser((byte)1, this);
  }
  
  void writeExternal(DataOutput paramDataOutput)
    throws IOException
  {
    paramDataOutput.writeInt(standardTransitions.length);
    for (long l1 : standardTransitions) {
      Ser.writeEpochSec(l1, paramDataOutput);
    }
    for (ZoneOffset localZoneOffset1 : standardOffsets) {
      Ser.writeOffset(localZoneOffset1, paramDataOutput);
    }
    paramDataOutput.writeInt(savingsInstantTransitions.length);
    for (long l2 : savingsInstantTransitions) {
      Ser.writeEpochSec(l2, paramDataOutput);
    }
    ZoneOffset localZoneOffset2;
    for (localZoneOffset2 : wallOffsets) {
      Ser.writeOffset(localZoneOffset2, paramDataOutput);
    }
    paramDataOutput.writeByte(lastRules.length);
    for (localZoneOffset2 : lastRules) {
      localZoneOffset2.writeExternal(paramDataOutput);
    }
  }
  
  static ZoneRules readExternal(DataInput paramDataInput)
    throws IOException, ClassNotFoundException
  {
    int i = paramDataInput.readInt();
    long[] arrayOfLong1 = i == 0 ? EMPTY_LONG_ARRAY : new long[i];
    for (int j = 0; j < i; j++) {
      arrayOfLong1[j] = Ser.readEpochSec(paramDataInput);
    }
    ZoneOffset[] arrayOfZoneOffset1 = new ZoneOffset[i + 1];
    for (int k = 0; k < arrayOfZoneOffset1.length; k++) {
      arrayOfZoneOffset1[k] = Ser.readOffset(paramDataInput);
    }
    k = paramDataInput.readInt();
    long[] arrayOfLong2 = k == 0 ? EMPTY_LONG_ARRAY : new long[k];
    for (int m = 0; m < k; m++) {
      arrayOfLong2[m] = Ser.readEpochSec(paramDataInput);
    }
    ZoneOffset[] arrayOfZoneOffset2 = new ZoneOffset[k + 1];
    for (int n = 0; n < arrayOfZoneOffset2.length; n++) {
      arrayOfZoneOffset2[n] = Ser.readOffset(paramDataInput);
    }
    n = paramDataInput.readByte();
    ZoneOffsetTransitionRule[] arrayOfZoneOffsetTransitionRule = n == 0 ? EMPTY_LASTRULES : new ZoneOffsetTransitionRule[n];
    for (int i1 = 0; i1 < n; i1++) {
      arrayOfZoneOffsetTransitionRule[i1] = ZoneOffsetTransitionRule.readExternal(paramDataInput);
    }
    return new ZoneRules(arrayOfLong1, arrayOfZoneOffset1, arrayOfLong2, arrayOfZoneOffset2, arrayOfZoneOffsetTransitionRule);
  }
  
  public boolean isFixedOffset()
  {
    return savingsInstantTransitions.length == 0;
  }
  
  public ZoneOffset getOffset(Instant paramInstant)
  {
    if (savingsInstantTransitions.length == 0) {
      return standardOffsets[0];
    }
    long l = paramInstant.getEpochSecond();
    if ((lastRules.length > 0) && (l > savingsInstantTransitions[(savingsInstantTransitions.length - 1)]))
    {
      i = findYear(l, wallOffsets[(wallOffsets.length - 1)]);
      ZoneOffsetTransition[] arrayOfZoneOffsetTransition = findTransitionArray(i);
      ZoneOffsetTransition localZoneOffsetTransition = null;
      for (int j = 0; j < arrayOfZoneOffsetTransition.length; j++)
      {
        localZoneOffsetTransition = arrayOfZoneOffsetTransition[j];
        if (l < localZoneOffsetTransition.toEpochSecond()) {
          return localZoneOffsetTransition.getOffsetBefore();
        }
      }
      return localZoneOffsetTransition.getOffsetAfter();
    }
    int i = Arrays.binarySearch(savingsInstantTransitions, l);
    if (i < 0) {
      i = -i - 2;
    }
    return wallOffsets[(i + 1)];
  }
  
  public ZoneOffset getOffset(LocalDateTime paramLocalDateTime)
  {
    Object localObject = getOffsetInfo(paramLocalDateTime);
    if ((localObject instanceof ZoneOffsetTransition)) {
      return ((ZoneOffsetTransition)localObject).getOffsetBefore();
    }
    return (ZoneOffset)localObject;
  }
  
  public List<ZoneOffset> getValidOffsets(LocalDateTime paramLocalDateTime)
  {
    Object localObject = getOffsetInfo(paramLocalDateTime);
    if ((localObject instanceof ZoneOffsetTransition)) {
      return ((ZoneOffsetTransition)localObject).getValidOffsets();
    }
    return Collections.singletonList((ZoneOffset)localObject);
  }
  
  public ZoneOffsetTransition getTransition(LocalDateTime paramLocalDateTime)
  {
    Object localObject = getOffsetInfo(paramLocalDateTime);
    return (localObject instanceof ZoneOffsetTransition) ? (ZoneOffsetTransition)localObject : null;
  }
  
  private Object getOffsetInfo(LocalDateTime paramLocalDateTime)
  {
    if (savingsInstantTransitions.length == 0) {
      return standardOffsets[0];
    }
    Object localObject1;
    if ((lastRules.length > 0) && (paramLocalDateTime.isAfter(savingsLocalTransitions[(savingsLocalTransitions.length - 1)])))
    {
      ZoneOffsetTransition[] arrayOfZoneOffsetTransition = findTransitionArray(paramLocalDateTime.getYear());
      localObject1 = null;
      for (ZoneOffsetTransition localZoneOffsetTransition : arrayOfZoneOffsetTransition)
      {
        localObject1 = findOffsetInfo(paramLocalDateTime, localZoneOffsetTransition);
        if (((localObject1 instanceof ZoneOffsetTransition)) || (localObject1.equals(localZoneOffsetTransition.getOffsetBefore()))) {
          return localObject1;
        }
      }
      return localObject1;
    }
    int i = Arrays.binarySearch(savingsLocalTransitions, paramLocalDateTime);
    if (i == -1) {
      return wallOffsets[0];
    }
    if (i < 0) {
      i = -i - 2;
    } else if ((i < savingsLocalTransitions.length - 1) && (savingsLocalTransitions[i].equals(savingsLocalTransitions[(i + 1)]))) {
      i++;
    }
    if ((i & 0x1) == 0)
    {
      localObject1 = savingsLocalTransitions[i];
      ??? = savingsLocalTransitions[(i + 1)];
      ZoneOffset localZoneOffset1 = wallOffsets[(i / 2)];
      ZoneOffset localZoneOffset2 = wallOffsets[(i / 2 + 1)];
      if (localZoneOffset2.getTotalSeconds() > localZoneOffset1.getTotalSeconds()) {
        return new ZoneOffsetTransition((LocalDateTime)localObject1, localZoneOffset1, localZoneOffset2);
      }
      return new ZoneOffsetTransition((LocalDateTime)???, localZoneOffset1, localZoneOffset2);
    }
    return wallOffsets[(i / 2 + 1)];
  }
  
  private Object findOffsetInfo(LocalDateTime paramLocalDateTime, ZoneOffsetTransition paramZoneOffsetTransition)
  {
    LocalDateTime localLocalDateTime = paramZoneOffsetTransition.getDateTimeBefore();
    if (paramZoneOffsetTransition.isGap())
    {
      if (paramLocalDateTime.isBefore(localLocalDateTime)) {
        return paramZoneOffsetTransition.getOffsetBefore();
      }
      if (paramLocalDateTime.isBefore(paramZoneOffsetTransition.getDateTimeAfter())) {
        return paramZoneOffsetTransition;
      }
      return paramZoneOffsetTransition.getOffsetAfter();
    }
    if (!paramLocalDateTime.isBefore(localLocalDateTime)) {
      return paramZoneOffsetTransition.getOffsetAfter();
    }
    if (paramLocalDateTime.isBefore(paramZoneOffsetTransition.getDateTimeAfter())) {
      return paramZoneOffsetTransition.getOffsetBefore();
    }
    return paramZoneOffsetTransition;
  }
  
  private ZoneOffsetTransition[] findTransitionArray(int paramInt)
  {
    Integer localInteger = Integer.valueOf(paramInt);
    ZoneOffsetTransition[] arrayOfZoneOffsetTransition = (ZoneOffsetTransition[])lastRulesCache.get(localInteger);
    if (arrayOfZoneOffsetTransition != null) {
      return arrayOfZoneOffsetTransition;
    }
    ZoneOffsetTransitionRule[] arrayOfZoneOffsetTransitionRule = lastRules;
    arrayOfZoneOffsetTransition = new ZoneOffsetTransition[arrayOfZoneOffsetTransitionRule.length];
    for (int i = 0; i < arrayOfZoneOffsetTransitionRule.length; i++) {
      arrayOfZoneOffsetTransition[i] = arrayOfZoneOffsetTransitionRule[i].createTransition(paramInt);
    }
    if (paramInt < 2100) {
      lastRulesCache.putIfAbsent(localInteger, arrayOfZoneOffsetTransition);
    }
    return arrayOfZoneOffsetTransition;
  }
  
  public ZoneOffset getStandardOffset(Instant paramInstant)
  {
    if (savingsInstantTransitions.length == 0) {
      return standardOffsets[0];
    }
    long l = paramInstant.getEpochSecond();
    int i = Arrays.binarySearch(standardTransitions, l);
    if (i < 0) {
      i = -i - 2;
    }
    return standardOffsets[(i + 1)];
  }
  
  public Duration getDaylightSavings(Instant paramInstant)
  {
    if (savingsInstantTransitions.length == 0) {
      return Duration.ZERO;
    }
    ZoneOffset localZoneOffset1 = getStandardOffset(paramInstant);
    ZoneOffset localZoneOffset2 = getOffset(paramInstant);
    return Duration.ofSeconds(localZoneOffset2.getTotalSeconds() - localZoneOffset1.getTotalSeconds());
  }
  
  public boolean isDaylightSavings(Instant paramInstant)
  {
    return !getStandardOffset(paramInstant).equals(getOffset(paramInstant));
  }
  
  public boolean isValidOffset(LocalDateTime paramLocalDateTime, ZoneOffset paramZoneOffset)
  {
    return getValidOffsets(paramLocalDateTime).contains(paramZoneOffset);
  }
  
  public ZoneOffsetTransition nextTransition(Instant paramInstant)
  {
    if (savingsInstantTransitions.length == 0) {
      return null;
    }
    long l = paramInstant.getEpochSecond();
    if (l >= savingsInstantTransitions[(savingsInstantTransitions.length - 1)])
    {
      if (lastRules.length == 0) {
        return null;
      }
      i = findYear(l, wallOffsets[(wallOffsets.length - 1)]);
      ZoneOffsetTransition[] arrayOfZoneOffsetTransition1 = findTransitionArray(i);
      for (ZoneOffsetTransition localZoneOffsetTransition : arrayOfZoneOffsetTransition1) {
        if (l < localZoneOffsetTransition.toEpochSecond()) {
          return localZoneOffsetTransition;
        }
      }
      if (i < 999999999)
      {
        arrayOfZoneOffsetTransition1 = findTransitionArray(i + 1);
        return arrayOfZoneOffsetTransition1[0];
      }
      return null;
    }
    int i = Arrays.binarySearch(savingsInstantTransitions, l);
    if (i < 0) {
      i = -i - 1;
    } else {
      i++;
    }
    return new ZoneOffsetTransition(savingsInstantTransitions[i], wallOffsets[i], wallOffsets[(i + 1)]);
  }
  
  public ZoneOffsetTransition previousTransition(Instant paramInstant)
  {
    if (savingsInstantTransitions.length == 0) {
      return null;
    }
    long l1 = paramInstant.getEpochSecond();
    if ((paramInstant.getNano() > 0) && (l1 < Long.MAX_VALUE)) {
      l1 += 1L;
    }
    long l2 = savingsInstantTransitions[(savingsInstantTransitions.length - 1)];
    if ((lastRules.length > 0) && (l1 > l2))
    {
      ZoneOffset localZoneOffset = wallOffsets[(wallOffsets.length - 1)];
      int j = findYear(l1, localZoneOffset);
      ZoneOffsetTransition[] arrayOfZoneOffsetTransition = findTransitionArray(j);
      for (int k = arrayOfZoneOffsetTransition.length - 1; k >= 0; k--) {
        if (l1 > arrayOfZoneOffsetTransition[k].toEpochSecond()) {
          return arrayOfZoneOffsetTransition[k];
        }
      }
      k = findYear(l2, localZoneOffset);
      j--;
      if (j > k)
      {
        arrayOfZoneOffsetTransition = findTransitionArray(j);
        return arrayOfZoneOffsetTransition[(arrayOfZoneOffsetTransition.length - 1)];
      }
    }
    int i = Arrays.binarySearch(savingsInstantTransitions, l1);
    if (i < 0) {
      i = -i - 1;
    }
    if (i <= 0) {
      return null;
    }
    return new ZoneOffsetTransition(savingsInstantTransitions[(i - 1)], wallOffsets[(i - 1)], wallOffsets[i]);
  }
  
  private int findYear(long paramLong, ZoneOffset paramZoneOffset)
  {
    long l1 = paramLong + paramZoneOffset.getTotalSeconds();
    long l2 = Math.floorDiv(l1, 86400L);
    return LocalDate.ofEpochDay(l2).getYear();
  }
  
  public List<ZoneOffsetTransition> getTransitions()
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < savingsInstantTransitions.length; i++) {
      localArrayList.add(new ZoneOffsetTransition(savingsInstantTransitions[i], wallOffsets[i], wallOffsets[(i + 1)]));
    }
    return Collections.unmodifiableList(localArrayList);
  }
  
  public List<ZoneOffsetTransitionRule> getTransitionRules()
  {
    return Collections.unmodifiableList(Arrays.asList(lastRules));
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof ZoneRules))
    {
      ZoneRules localZoneRules = (ZoneRules)paramObject;
      return (Arrays.equals(standardTransitions, standardTransitions)) && (Arrays.equals(standardOffsets, standardOffsets)) && (Arrays.equals(savingsInstantTransitions, savingsInstantTransitions)) && (Arrays.equals(wallOffsets, wallOffsets)) && (Arrays.equals(lastRules, lastRules));
    }
    return false;
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(standardTransitions) ^ Arrays.hashCode(standardOffsets) ^ Arrays.hashCode(savingsInstantTransitions) ^ Arrays.hashCode(wallOffsets) ^ Arrays.hashCode(lastRules);
  }
  
  public String toString()
  {
    return "ZoneRules[currentStandardOffset=" + standardOffsets[(standardOffsets.length - 1)] + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\zone\ZoneRules.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */