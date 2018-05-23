package com.sun.java.util.jar.pack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

class CodingChooser
{
  int verbose;
  int effort;
  boolean optUseHistogram = true;
  boolean optUsePopulationCoding = true;
  boolean optUseAdaptiveCoding = true;
  boolean disablePopCoding;
  boolean disableRunCoding;
  boolean topLevel = true;
  double fuzz;
  Coding[] allCodingChoices;
  Choice[] choices;
  ByteArrayOutputStream context;
  CodingChooser popHelper;
  CodingChooser runHelper;
  Random stress;
  private int[] values;
  private int start;
  private int end;
  private int[] deltas;
  private int min;
  private int max;
  private Histogram vHist;
  private Histogram dHist;
  private int searchOrder;
  private Choice regularChoice;
  private Choice bestChoice;
  private CodingMethod bestMethod;
  private int bestByteSize;
  private int bestZipSize;
  private int targetSize;
  public static final int MIN_EFFORT = 1;
  public static final int MID_EFFORT = 5;
  public static final int MAX_EFFORT = 9;
  public static final int POP_EFFORT = 4;
  public static final int RUN_EFFORT = 3;
  public static final int BYTE_SIZE = 0;
  public static final int ZIP_SIZE = 1;
  private Sizer zipSizer = new Sizer();
  private Deflater zipDef = new Deflater();
  private DeflaterOutputStream zipOut = new DeflaterOutputStream(zipSizer, zipDef);
  private Sizer byteSizer = new Sizer(zipOut);
  private Sizer byteOnlySizer = new Sizer();
  
  CodingChooser(int paramInt, Coding[] paramArrayOfCoding)
  {
    PropMap localPropMap = Utils.currentPropMap();
    if (localPropMap != null)
    {
      verbose = Math.max(localPropMap.getInteger("com.sun.java.util.jar.pack.verbose"), localPropMap.getInteger("com.sun.java.util.jar.pack.verbose.coding"));
      optUseHistogram = (!localPropMap.getBoolean("com.sun.java.util.jar.pack.no.histogram"));
      optUsePopulationCoding = (!localPropMap.getBoolean("com.sun.java.util.jar.pack.no.population.coding"));
      optUseAdaptiveCoding = (!localPropMap.getBoolean("com.sun.java.util.jar.pack.no.adaptive.coding"));
      i = localPropMap.getInteger("com.sun.java.util.jar.pack.stress.coding");
      if (i != 0) {
        stress = new Random(i);
      }
    }
    effort = paramInt;
    allCodingChoices = paramArrayOfCoding;
    fuzz = (1.0D + 0.0025D * (paramInt - 5));
    int i = 0;
    for (int j = 0; j < paramArrayOfCoding.length; j++) {
      if (paramArrayOfCoding[j] != null) {
        i++;
      }
    }
    choices = new Choice[i];
    i = 0;
    Object localObject;
    for (j = 0; j < paramArrayOfCoding.length; j++) {
      if (paramArrayOfCoding[j] != null)
      {
        localObject = new int[choices.length];
        choices[(i++)] = new Choice(paramArrayOfCoding[j], j, (int[])localObject);
      }
    }
    for (j = 0; j < choices.length; j++)
    {
      localObject = choices[j].coding;
      assert (((Coding)localObject).distanceFrom((Coding)localObject) == 0);
      for (int k = 0; k < j; k++)
      {
        Coding localCoding = choices[k].coding;
        int m = ((Coding)localObject).distanceFrom(localCoding);
        assert (m > 0);
        assert (m == localCoding.distanceFrom((Coding)localObject));
        choices[j].distance[k] = m;
        choices[k].distance[j] = m;
      }
    }
  }
  
  Choice makeExtraChoice(Coding paramCoding)
  {
    int[] arrayOfInt = new int[choices.length];
    for (int i = 0; i < arrayOfInt.length; i++)
    {
      Coding localCoding = choices[i].coding;
      int j = paramCoding.distanceFrom(localCoding);
      assert (j > 0);
      assert (j == localCoding.distanceFrom(paramCoding));
      arrayOfInt[i] = j;
    }
    Choice localChoice = new Choice(paramCoding, -1, arrayOfInt);
    localChoice.reset();
    return localChoice;
  }
  
  ByteArrayOutputStream getContext()
  {
    if (context == null) {
      context = new ByteArrayOutputStream(65536);
    }
    return context;
  }
  
  private void reset(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    values = paramArrayOfInt;
    start = paramInt1;
    end = paramInt2;
    deltas = null;
    min = Integer.MAX_VALUE;
    max = Integer.MIN_VALUE;
    vHist = null;
    dHist = null;
    searchOrder = 0;
    regularChoice = null;
    bestChoice = null;
    bestMethod = null;
    bestZipSize = Integer.MAX_VALUE;
    bestByteSize = Integer.MAX_VALUE;
    targetSize = Integer.MAX_VALUE;
  }
  
  CodingMethod choose(int[] paramArrayOfInt1, int paramInt1, int paramInt2, Coding paramCoding, int[] paramArrayOfInt2)
  {
    reset(paramArrayOfInt1, paramInt1, paramInt2);
    if ((effort <= 1) || (paramInt1 >= paramInt2))
    {
      if (paramArrayOfInt2 != null)
      {
        int[] arrayOfInt = computeSizePrivate(paramCoding);
        paramArrayOfInt2[0] = arrayOfInt[0];
        paramArrayOfInt2[1] = arrayOfInt[1];
      }
      return paramCoding;
    }
    if (optUseHistogram)
    {
      getValueHistogram();
      getDeltaHistogram();
    }
    int j;
    for (int i = paramInt1; i < paramInt2; i++)
    {
      j = paramArrayOfInt1[i];
      if (min > j) {
        min = j;
      }
      if (max < j) {
        max = j;
      }
    }
    i = markUsableChoices(paramCoding);
    if (stress != null)
    {
      j = stress.nextInt(i * 2 + 4);
      Object localObject1 = null;
      for (k = 0; k < choices.length; k++)
      {
        Choice localChoice = choices[k];
        if ((searchOrder >= 0) && (j-- == 0))
        {
          localObject1 = coding;
          break;
        }
      }
      if (localObject1 == null) {
        if ((j & 0x7) != 0) {
          localObject1 = paramCoding;
        } else {
          localObject1 = stressCoding(min, max);
        }
      }
      if ((!disablePopCoding) && (optUsePopulationCoding) && (effort >= 4)) {
        localObject1 = stressPopCoding((CodingMethod)localObject1);
      }
      if ((!disableRunCoding) && (optUseAdaptiveCoding) && (effort >= 3)) {
        localObject1 = stressAdaptiveCoding((CodingMethod)localObject1);
      }
      return (CodingMethod)localObject1;
    }
    double d = 1.0D;
    for (int k = effort; k < 9; k++) {
      d /= 1.414D;
    }
    k = (int)Math.ceil(i * d);
    bestChoice = regularChoice;
    evaluate(regularChoice);
    int m = updateDistances(regularChoice);
    int n = bestZipSize;
    int i1 = bestByteSize;
    if ((regularChoice.coding == paramCoding) && (topLevel))
    {
      i2 = BandStructure.encodeEscapeValue(115, paramCoding);
      if (paramCoding.canRepresentSigned(i2))
      {
        int i3 = paramCoding.getLength(i2);
        regularChoice.zipSize -= i3;
        bestByteSize = regularChoice.byteSize;
        bestZipSize = regularChoice.zipSize;
      }
    }
    int i2 = 1;
    while (searchOrder < k)
    {
      if (i2 > m) {
        i2 = 1;
      }
      int i4 = m / i2;
      int i5 = m / (i2 *= 2) + 1;
      localObject2 = findChoiceNear(bestChoice, i4, i5);
      if (localObject2 != null)
      {
        assert (coding.canRepresent(min, max));
        evaluate((Choice)localObject2);
        int i6 = updateDistances((Choice)localObject2);
        if (localObject2 == bestChoice)
        {
          m = i6;
          if (verbose > 5) {
            Utils.log.info("maxd = " + m);
          }
        }
      }
    }
    Object localObject2 = bestChoice.coding;
    assert (localObject2 == bestMethod);
    if (verbose > 2) {
      Utils.log.info("chooser: plain result=" + bestChoice + " after " + bestChoice.searchOrder + " rounds, " + (regularChoice.zipSize - bestZipSize) + " fewer bytes than regular " + paramCoding);
    }
    bestChoice = null;
    if ((!disablePopCoding) && (optUsePopulationCoding) && (effort >= 4) && ((bestMethod instanceof Coding))) {
      tryPopulationCoding((Coding)localObject2);
    }
    if ((!disableRunCoding) && (optUseAdaptiveCoding) && (effort >= 3) && ((bestMethod instanceof Coding))) {
      tryAdaptiveCoding((Coding)localObject2);
    }
    if (paramArrayOfInt2 != null)
    {
      paramArrayOfInt2[0] = bestByteSize;
      paramArrayOfInt2[1] = bestZipSize;
    }
    if (verbose > 1) {
      Utils.log.info("chooser: result=" + bestMethod + " " + (n - bestZipSize) + " fewer bytes than regular " + paramCoding + "; win=" + pct(n - bestZipSize, n));
    }
    CodingMethod localCodingMethod = bestMethod;
    reset(null, 0, 0);
    return localCodingMethod;
  }
  
  CodingMethod choose(int[] paramArrayOfInt, int paramInt1, int paramInt2, Coding paramCoding)
  {
    return choose(paramArrayOfInt, paramInt1, paramInt2, paramCoding, null);
  }
  
  CodingMethod choose(int[] paramArrayOfInt1, Coding paramCoding, int[] paramArrayOfInt2)
  {
    return choose(paramArrayOfInt1, 0, paramArrayOfInt1.length, paramCoding, paramArrayOfInt2);
  }
  
  CodingMethod choose(int[] paramArrayOfInt, Coding paramCoding)
  {
    return choose(paramArrayOfInt, 0, paramArrayOfInt.length, paramCoding, null);
  }
  
  private int markUsableChoices(Coding paramCoding)
  {
    int i = 0;
    Choice localChoice;
    for (int j = 0; j < choices.length; j++)
    {
      localChoice = choices[j];
      localChoice.reset();
      if (!coding.canRepresent(min, max))
      {
        searchOrder = -1;
        if ((verbose > 1) && (coding == paramCoding)) {
          Utils.log.info("regular coding cannot represent [" + min + ".." + max + "]: " + paramCoding);
        }
      }
      else
      {
        if (coding == paramCoding) {
          regularChoice = localChoice;
        }
        i++;
      }
    }
    if ((regularChoice == null) && (paramCoding.canRepresent(min, max)))
    {
      regularChoice = makeExtraChoice(paramCoding);
      if (verbose > 1) {
        Utils.log.info("*** regular choice is extra: " + regularChoice.coding);
      }
    }
    if (regularChoice == null)
    {
      for (j = 0; j < choices.length; j++)
      {
        localChoice = choices[j];
        if (searchOrder != -1)
        {
          regularChoice = localChoice;
          break;
        }
      }
      if (verbose > 1)
      {
        Utils.log.info("*** regular choice does not apply " + paramCoding);
        Utils.log.info("    using instead " + regularChoice.coding);
      }
    }
    if (verbose > 2)
    {
      Utils.log.info("chooser: #choices=" + i + " [" + min + ".." + max + "]");
      if (verbose > 4) {
        for (j = 0; j < choices.length; j++)
        {
          localChoice = choices[j];
          if (searchOrder >= 0) {
            Utils.log.info("  " + localChoice);
          }
        }
      }
    }
    return i;
  }
  
  private Choice findChoiceNear(Choice paramChoice, int paramInt1, int paramInt2)
  {
    if (verbose > 5) {
      Utils.log.info("findChoice " + paramInt1 + ".." + paramInt2 + " near: " + paramChoice);
    }
    int[] arrayOfInt = distance;
    Object localObject = null;
    for (int i = 0; i < choices.length; i++)
    {
      Choice localChoice = choices[i];
      if ((searchOrder >= searchOrder) && (arrayOfInt[i] >= paramInt2) && (arrayOfInt[i] <= paramInt1))
      {
        if ((minDistance >= paramInt2) && (minDistance <= paramInt1))
        {
          if (verbose > 5) {
            Utils.log.info("findChoice => good " + localChoice);
          }
          return localChoice;
        }
        localObject = localChoice;
      }
    }
    if (verbose > 5) {
      Utils.log.info("findChoice => found " + localObject);
    }
    return (Choice)localObject;
  }
  
  private void evaluate(Choice paramChoice)
  {
    assert (searchOrder == Integer.MAX_VALUE);
    searchOrder = (searchOrder++);
    int i;
    Object localObject;
    if ((paramChoice == bestChoice) || (paramChoice.isExtra()))
    {
      i = 1;
    }
    else if (optUseHistogram)
    {
      localObject = getHistogram(coding.isDelta());
      histSize = ((int)Math.ceil(((Histogram)localObject).getBitLength(coding) / 8.0D));
      byteSize = histSize;
      i = byteSize <= targetSize ? 1 : 0;
    }
    else
    {
      i = 1;
    }
    if (i != 0)
    {
      localObject = computeSizePrivate(coding);
      byteSize = localObject[0];
      zipSize = localObject[1];
      if (noteSizes(coding, byteSize, zipSize)) {
        bestChoice = paramChoice;
      }
    }
    if ((histSize >= 0) && (!$assertionsDisabled) && (byteSize != histSize)) {
      throw new AssertionError();
    }
    if (verbose > 4) {
      Utils.log.info("evaluated " + paramChoice);
    }
  }
  
  private boolean noteSizes(CodingMethod paramCodingMethod, int paramInt1, int paramInt2)
  {
    assert ((paramInt2 > 0) && (paramInt1 > 0));
    int i = paramInt2 < bestZipSize ? 1 : 0;
    if (verbose > 3) {
      Utils.log.info("computed size " + paramCodingMethod + " " + paramInt1 + "/zs=" + paramInt2 + ((i != 0) && (bestMethod != null) ? " better by " + pct(bestZipSize - paramInt2, paramInt2) : ""));
    }
    if (i != 0)
    {
      bestMethod = paramCodingMethod;
      bestZipSize = paramInt2;
      bestByteSize = paramInt1;
      targetSize = ((int)(paramInt1 * fuzz));
      return true;
    }
    return false;
  }
  
  private int updateDistances(Choice paramChoice)
  {
    int[] arrayOfInt = distance;
    int i = 0;
    for (int j = 0; j < choices.length; j++)
    {
      Choice localChoice = choices[j];
      if (searchOrder >= searchOrder)
      {
        int k = arrayOfInt[j];
        if (verbose > 5) {
          Utils.log.info("evaluate dist " + k + " to " + localChoice);
        }
        int m = minDistance;
        if (m > k) {
          minDistance = (m = k);
        }
        if (i < k) {
          i = k;
        }
      }
    }
    if (verbose > 5) {
      Utils.log.info("evaluate maxd => " + i);
    }
    return i;
  }
  
  public void computeSize(CodingMethod paramCodingMethod, int[] paramArrayOfInt1, int paramInt1, int paramInt2, int[] paramArrayOfInt2)
  {
    if (paramInt2 <= paramInt1)
    {
      paramArrayOfInt2[0] = (paramArrayOfInt2[1] = 0);
      return;
    }
    try
    {
      resetData();
      paramCodingMethod.writeArrayTo(byteSizer, paramArrayOfInt1, paramInt1, paramInt2);
      paramArrayOfInt2[0] = getByteSize();
      paramArrayOfInt2[1] = getZipSize();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
  
  public void computeSize(CodingMethod paramCodingMethod, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    computeSize(paramCodingMethod, paramArrayOfInt1, 0, paramArrayOfInt1.length, paramArrayOfInt2);
  }
  
  public int[] computeSize(CodingMethod paramCodingMethod, int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = { 0, 0 };
    computeSize(paramCodingMethod, paramArrayOfInt, paramInt1, paramInt2, arrayOfInt);
    return arrayOfInt;
  }
  
  public int[] computeSize(CodingMethod paramCodingMethod, int[] paramArrayOfInt)
  {
    return computeSize(paramCodingMethod, paramArrayOfInt, 0, paramArrayOfInt.length);
  }
  
  private int[] computeSizePrivate(CodingMethod paramCodingMethod)
  {
    int[] arrayOfInt = { 0, 0 };
    computeSize(paramCodingMethod, values, start, end, arrayOfInt);
    return arrayOfInt;
  }
  
  public int computeByteSize(CodingMethod paramCodingMethod, int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int i = paramInt2 - paramInt1;
    if (i < 0) {
      return 0;
    }
    if ((paramCodingMethod instanceof Coding))
    {
      Coding localCoding = (Coding)paramCodingMethod;
      int j = localCoding.getLength(paramArrayOfInt, paramInt1, paramInt2);
      int k;
      assert (j == (k = countBytesToSizer(paramCodingMethod, paramArrayOfInt, paramInt1, paramInt2))) : (paramCodingMethod + " : " + j + " != " + k);
      return j;
    }
    return countBytesToSizer(paramCodingMethod, paramArrayOfInt, paramInt1, paramInt2);
  }
  
  private int countBytesToSizer(CodingMethod paramCodingMethod, int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    try
    {
      byteOnlySizer.reset();
      paramCodingMethod.writeArrayTo(byteOnlySizer, paramArrayOfInt, paramInt1, paramInt2);
      return byteOnlySizer.getSize();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
  
  int[] getDeltas(int paramInt1, int paramInt2)
  {
    if ((paramInt1 | paramInt2) != 0) {
      return Coding.makeDeltas(values, start, end, paramInt1, paramInt2);
    }
    if (deltas == null) {
      deltas = Coding.makeDeltas(values, start, end, 0, 0);
    }
    return deltas;
  }
  
  Histogram getValueHistogram()
  {
    if (vHist == null)
    {
      vHist = new Histogram(values, start, end);
      if (verbose > 3) {
        vHist.print("vHist", System.out);
      } else if (verbose > 1) {
        vHist.print("vHist", null, System.out);
      }
    }
    return vHist;
  }
  
  Histogram getDeltaHistogram()
  {
    if (dHist == null)
    {
      dHist = new Histogram(getDeltas(0, 0));
      if (verbose > 3) {
        dHist.print("dHist", System.out);
      } else if (verbose > 1) {
        dHist.print("dHist", null, System.out);
      }
    }
    return dHist;
  }
  
  Histogram getHistogram(boolean paramBoolean)
  {
    return paramBoolean ? getDeltaHistogram() : getValueHistogram();
  }
  
  private void tryPopulationCoding(Coding paramCoding)
  {
    Histogram localHistogram = getValueHistogram();
    Coding localCoding1 = paramCoding.getValueCoding();
    Coding localCoding2 = BandStructure.UNSIGNED5.setL(64);
    Coding localCoding3 = paramCoding.getValueCoding();
    int i = 4 + Math.max(localCoding1.getLength(min), localCoding1.getLength(max));
    int m = localCoding2.getLength(0);
    int j = m * (end - start);
    int k = (int)Math.ceil(localHistogram.getBitLength(localCoding3) / 8.0D);
    int n = i + j + k;
    Coding localCoding4 = 0;
    int[] arrayOfInt1 = new int[1 + localHistogram.getTotalLength()];
    int i1 = -1;
    int i2 = -1;
    int[][] arrayOfInt = localHistogram.getMatrix();
    int i3 = -1;
    int i4 = 1;
    int i5 = 0;
    for (Coding localCoding5 = 1; localCoding5 <= localHistogram.getTotalLength(); localCoding5++)
    {
      if (i4 == 1)
      {
        i3++;
        i5 = arrayOfInt[i3][0];
        i4 = arrayOfInt[i3].length;
      }
      int i6 = arrayOfInt[i3][(--i4)];
      arrayOfInt1[localCoding5] = i6;
      int i7 = localCoding1.getLength(i6);
      i += i7;
      int i8 = i5;
      int i9 = localCoding5;
      j += (localCoding2.getLength(i9) - m) * i8;
      k -= i7 * i8;
      int i10 = i + j + k;
      if (n > i10)
      {
        if (i10 <= targetSize)
        {
          i2 = localCoding5;
          if (i1 < 0) {
            i1 = localCoding5;
          }
          if (verbose > 4) {
            Utils.log.info("better pop-size at fvc=" + localCoding5 + " by " + pct(n - i10, n));
          }
        }
        n = i10;
        localCoding4 = localCoding5;
      }
    }
    if (i1 < 0)
    {
      if ((verbose > 1) && (verbose > 1)) {
        Utils.log.info("no good pop-size; best was " + n + " at " + localCoding4 + " worse by " + pct(n - bestByteSize, bestByteSize));
      }
      return;
    }
    if (verbose > 1) {
      Utils.log.info("initial best pop-size at fvc=" + localCoding4 + " in [" + i1 + ".." + i2 + "] by " + pct(bestByteSize - n, bestByteSize));
    }
    localCoding5 = bestZipSize;
    int[] arrayOfInt2 = PopulationCoding.LValuesCoded;
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    Coding localCoding7;
    Object localObject2;
    if (localCoding4 <= 255)
    {
      localArrayList1.add(BandStructure.BYTE1);
    }
    else
    {
      int i11 = 5;
      int i12 = effort > 4 ? 1 : 0;
      if (i12 != 0) {
        localArrayList2.add(BandStructure.BYTE1.setS(1));
      }
      for (int i14 = arrayOfInt2.length - 1; i14 >= 1; i14--)
      {
        int i15 = arrayOfInt2[i14];
        Coding localCoding6 = PopulationCoding.fitTokenCoding(i1, i15);
        localCoding7 = PopulationCoding.fitTokenCoding(localCoding4, i15);
        localObject2 = PopulationCoding.fitTokenCoding(i2, i15);
        if (localCoding7 != null)
        {
          if (!localArrayList1.contains(localCoding7)) {
            localArrayList1.add(localCoding7);
          }
          if (i11 > localCoding7.B()) {
            i11 = localCoding7.B();
          }
        }
        if (i12 != 0)
        {
          if (localObject2 == null) {
            localObject2 = localCoding7;
          }
          for (int i18 = localCoding6.B(); i18 <= ((Coding)localObject2).B(); i18++) {
            if ((i18 != localCoding7.B()) && (i18 != 1))
            {
              Coding localCoding8 = ((Coding)localObject2).setB(i18).setS(1);
              if (!localArrayList2.contains(localCoding8)) {
                localArrayList2.add(localCoding8);
              }
            }
          }
        }
      }
      localIterator2 = localArrayList1.iterator();
      while (localIterator2.hasNext())
      {
        localObject1 = (Coding)localIterator2.next();
        if (((Coding)localObject1).B() > i11)
        {
          localIterator2.remove();
          localArrayList3.add(0, localObject1);
        }
      }
    }
    ArrayList localArrayList4 = new ArrayList();
    Iterator localIterator1 = localArrayList1.iterator();
    Iterator localIterator2 = localArrayList2.iterator();
    Object localObject1 = localArrayList3.iterator();
    while ((localIterator1.hasNext()) || (localIterator2.hasNext()) || (((Iterator)localObject1).hasNext()))
    {
      if (localIterator1.hasNext()) {
        localArrayList4.add(localIterator1.next());
      }
      if (localIterator2.hasNext()) {
        localArrayList4.add(localIterator2.next());
      }
      if (((Iterator)localObject1).hasNext()) {
        localArrayList4.add(((Iterator)localObject1).next());
      }
    }
    localArrayList1.clear();
    localArrayList2.clear();
    localArrayList3.clear();
    int i13 = localArrayList4.size();
    if (effort == 4)
    {
      i13 = 2;
    }
    else if (i13 > 4)
    {
      i13 -= 4;
      i13 = i13 * (effort - 4) / 5;
      i13 += 4;
    }
    if (localArrayList4.size() > i13)
    {
      if (verbose > 4) {
        Utils.log.info("allFits before clip: " + localArrayList4);
      }
      localArrayList4.subList(i13, localArrayList4.size()).clear();
    }
    if (verbose > 3) {
      Utils.log.info("allFits: " + localArrayList4);
    }
    localIterator2 = localArrayList4.iterator();
    while (localIterator2.hasNext())
    {
      localObject1 = (Coding)localIterator2.next();
      int i16 = 0;
      if (((Coding)localObject1).S() == 1)
      {
        i16 = 1;
        localObject1 = ((Coding)localObject1).setS(0);
      }
      int i17;
      if (i16 == 0)
      {
        localCoding7 = localCoding4;
        assert (((Coding)localObject1).umax() >= localCoding7);
        if ((!$assertionsDisabled) && (((Coding)localObject1).B() != 1) && (((Coding)localObject1).setB(((Coding)localObject1).B() - 1).umax() >= localCoding7)) {
          throw new AssertionError();
        }
      }
      else
      {
        i17 = Math.min(((Coding)localObject1).umax(), i2);
        if ((i17 < i1) || (i17 == localCoding4)) {
          continue;
        }
      }
      localObject2 = new PopulationCoding();
      ((PopulationCoding)localObject2).setHistogram(localHistogram);
      ((PopulationCoding)localObject2).setL(((Coding)localObject1).L());
      ((PopulationCoding)localObject2).setFavoredValues(arrayOfInt1, i17);
      assert (tokenCoding == localObject1);
      ((PopulationCoding)localObject2).resortFavoredValues();
      int[] arrayOfInt3 = computePopSizePrivate((PopulationCoding)localObject2, localCoding1, localCoding3);
      noteSizes((CodingMethod)localObject2, arrayOfInt3[0], 4 + arrayOfInt3[1]);
    }
    if (verbose > 3)
    {
      Utils.log.info("measured best pop, size=" + bestByteSize + "/zs=" + bestZipSize + " better by " + pct(localCoding5 - bestZipSize, localCoding5));
      if (bestZipSize < localCoding5) {
        Utils.log.info(">>> POP WINS BY " + (localCoding5 - bestZipSize));
      }
    }
  }
  
  private int[] computePopSizePrivate(PopulationCoding paramPopulationCoding, Coding paramCoding1, Coding paramCoding2)
  {
    if (popHelper == null)
    {
      popHelper = new CodingChooser(effort, allCodingChoices);
      if (stress != null) {
        popHelper.addStressSeed(stress.nextInt());
      }
      popHelper.topLevel = false;
      popHelper.verbose -= 1;
      popHelper.disablePopCoding = true;
      popHelper.disableRunCoding = disableRunCoding;
      if (effort < 5) {
        popHelper.disableRunCoding = true;
      }
    }
    int i = fVlen;
    if (verbose > 2)
    {
      Utils.log.info("computePopSizePrivate fvlen=" + i + " tc=" + tokenCoding);
      Utils.log.info("{ //BEGIN");
    }
    int[] arrayOfInt1 = fValues;
    int[][] arrayOfInt = paramPopulationCoding.encodeValues(values, start, end);
    int[] arrayOfInt2 = arrayOfInt[0];
    int[] arrayOfInt3 = arrayOfInt[1];
    if (verbose > 2) {
      Utils.log.info("-- refine on fv[" + i + "] fc=" + paramCoding1);
    }
    paramPopulationCoding.setFavoredCoding(popHelper.choose(arrayOfInt1, 1, 1 + i, paramCoding1));
    Object localObject;
    if (((tokenCoding instanceof Coding)) && ((stress == null) || (stress.nextBoolean())))
    {
      if (verbose > 2) {
        Utils.log.info("-- refine on tv[" + arrayOfInt2.length + "] tc=" + tokenCoding);
      }
      localObject = popHelper.choose(arrayOfInt2, (Coding)tokenCoding);
      if (localObject != tokenCoding)
      {
        if (verbose > 2) {
          Utils.log.info(">>> refined tc=" + localObject);
        }
        paramPopulationCoding.setTokenCoding((CodingMethod)localObject);
      }
    }
    if (arrayOfInt3.length == 0)
    {
      paramPopulationCoding.setUnfavoredCoding(null);
    }
    else
    {
      if (verbose > 2) {
        Utils.log.info("-- refine on uv[" + arrayOfInt3.length + "] uc=" + unfavoredCoding);
      }
      paramPopulationCoding.setUnfavoredCoding(popHelper.choose(arrayOfInt3, paramCoding2));
    }
    if (verbose > 3)
    {
      Utils.log.info("finish computePopSizePrivate fvlen=" + i + " fc=" + favoredCoding + " tc=" + tokenCoding + " uc=" + unfavoredCoding);
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("fv = {");
      for (int j = 1; j <= i; j++)
      {
        if (j % 10 == 0) {
          ((StringBuilder)localObject).append('\n');
        }
        ((StringBuilder)localObject).append(" ").append(arrayOfInt1[j]);
      }
      ((StringBuilder)localObject).append('\n');
      ((StringBuilder)localObject).append("}");
      Utils.log.info(((StringBuilder)localObject).toString());
    }
    if (verbose > 2) {
      Utils.log.info("} //END");
    }
    if (stress != null) {
      return null;
    }
    try
    {
      resetData();
      paramPopulationCoding.writeSequencesTo(byteSizer, arrayOfInt2, arrayOfInt3);
      localObject = new int[] { getByteSize(), getZipSize() };
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
    int[] arrayOfInt4 = null;
    assert ((arrayOfInt4 = computeSizePrivate(paramPopulationCoding)) != null);
    assert (arrayOfInt4[0] == localObject[0]) : (arrayOfInt4[0] + " != " + localObject[0]);
    return (int[])localObject;
  }
  
  private void tryAdaptiveCoding(Coding paramCoding)
  {
    int i = bestZipSize;
    int j = start;
    int k = end;
    int[] arrayOfInt1 = values;
    int m = k - j;
    if (paramCoding.isDelta())
    {
      arrayOfInt1 = getDeltas(0, 0);
      j = 0;
      k = arrayOfInt1.length;
    }
    int[] arrayOfInt2 = new int[m + 1];
    int n = 0;
    int i1 = 0;
    for (int i2 = j; i2 < k; i2++)
    {
      int i3 = arrayOfInt1[i2];
      arrayOfInt2[(n++)] = i1;
      int i4 = paramCoding.getLength(i3);
      assert (i4 < Integer.MAX_VALUE);
      i1 += i4;
    }
    arrayOfInt2[(n++)] = i1;
    assert (n == arrayOfInt2.length);
    double d1 = i1 / m;
    double d2;
    if (effort >= 5)
    {
      if (effort > 6) {
        d2 = 1.001D;
      } else {
        d2 = 1.003D;
      }
    }
    else if (effort > 3) {
      d2 = 1.01D;
    } else {
      d2 = 1.03D;
    }
    d2 *= d2;
    double d3 = d2 * d2;
    double d4 = d2 * d2 * d2;
    double[] arrayOfDouble1 = new double[1 + (effort - 3)];
    double d5 = Math.log(m);
    for (int i5 = 0; i5 < arrayOfDouble1.length; i5++) {
      arrayOfDouble1[i5] = Math.exp(d5 * (i5 + 1) / (arrayOfDouble1.length + 1));
    }
    int[] arrayOfInt3 = new int[arrayOfDouble1.length];
    int i6 = 0;
    for (int i7 = 0; i7 < arrayOfDouble1.length; i7++)
    {
      int i8 = (int)Math.round(arrayOfDouble1[i7]);
      i8 = AdaptiveCoding.getNextK(i8 - 1);
      if ((i8 > 0) && (i8 < m) && ((i6 <= 0) || (i8 != arrayOfInt3[(i6 - 1)]))) {
        arrayOfInt3[(i6++)] = i8;
      }
    }
    arrayOfInt3 = BandStructure.realloc(arrayOfInt3, i6);
    int[] arrayOfInt4 = new int[arrayOfInt3.length];
    double[] arrayOfDouble2 = new double[arrayOfInt3.length];
    int i10;
    for (int i9 = 0; i9 < arrayOfInt3.length; i9++)
    {
      i10 = arrayOfInt3[i9];
      double d6;
      if (i10 < 10) {
        d6 = d4;
      } else if (i10 < 100) {
        d6 = d3;
      } else {
        d6 = d2;
      }
      arrayOfDouble2[i9] = d6;
      arrayOfInt4[i9] = (4 + (int)Math.ceil(i10 * d1 * d6));
    }
    if (verbose > 1)
    {
      System.out.print("tryAdaptiveCoding [" + m + "] avgS=" + d1 + " fuzz=" + d2 + " meshes: {");
      for (i9 = 0; i9 < arrayOfInt3.length; i9++) {
        System.out.print(" " + arrayOfInt3[i9] + "(" + arrayOfInt4[i9] + ")");
      }
      Utils.log.info(" }");
    }
    if (runHelper == null)
    {
      runHelper = new CodingChooser(effort, allCodingChoices);
      if (stress != null) {
        runHelper.addStressSeed(stress.nextInt());
      }
      runHelper.topLevel = false;
      runHelper.verbose -= 1;
      runHelper.disableRunCoding = true;
      runHelper.disablePopCoding = disablePopCoding;
      if (effort < 5) {
        runHelper.disablePopCoding = true;
      }
    }
    for (i9 = 0; i9 < m; i9++)
    {
      i9 = AdaptiveCoding.getNextK(i9 - 1);
      if (i9 > m) {
        i9 = m;
      }
      for (i10 = arrayOfInt3.length - 1; i10 >= 0; i10--)
      {
        int i11 = arrayOfInt3[i10];
        int i12 = arrayOfInt4[i10];
        if (i9 + i11 <= m)
        {
          int i13 = arrayOfInt2[(i9 + i11)] - arrayOfInt2[i9];
          if (i13 >= i12)
          {
            int i14 = i9 + i11;
            int i15 = i13;
            double d7 = d1 * arrayOfDouble2[i10];
            while ((i14 < m) && (i14 - i9 <= m / 2))
            {
              i16 = i14;
              int i17 = i15;
              i14 += i11;
              i14 = i9 + AdaptiveCoding.getNextK(i14 - i9 - 1);
              if ((i14 < 0) || (i14 > m)) {
                i14 = m;
              }
              i15 = arrayOfInt2[i14] - arrayOfInt2[i9];
              if (i15 < 4.0D + (i14 - i9) * d7)
              {
                i15 = i17;
                i14 = i16;
                break;
              }
            }
            int i16 = i14;
            if (verbose > 2)
            {
              Utils.log.info("bulge at " + i9 + "[" + (i14 - i9) + "] of " + pct(i15 - d1 * (i14 - i9), d1 * (i14 - i9)));
              Utils.log.info("{ //BEGIN");
            }
            CodingMethod localCodingMethod = runHelper.choose(values, start + i9, start + i14, paramCoding);
            Object localObject1;
            Object localObject2;
            if (localCodingMethod == paramCoding)
            {
              localObject1 = paramCoding;
              localObject2 = paramCoding;
            }
            else
            {
              localObject1 = runHelper.choose(values, start, start + i9, paramCoding);
              localObject2 = runHelper.choose(values, start + i14, start + m, paramCoding);
            }
            if (verbose > 2) {
              Utils.log.info("} //END");
            }
            if ((localObject1 == localCodingMethod) && (i9 > 0) && (AdaptiveCoding.isCodableLength(i14))) {
              i9 = 0;
            }
            if ((localCodingMethod == localObject2) && (i14 < m)) {
              i14 = m;
            }
            if ((localObject1 != paramCoding) || (localCodingMethod != paramCoding) || (localObject2 != paramCoding))
            {
              int i18 = 0;
              Object localObject3;
              if (i14 == m)
              {
                localObject3 = localCodingMethod;
              }
              else
              {
                localObject3 = new AdaptiveCoding(i14 - i9, localCodingMethod, (CodingMethod)localObject2);
                i18 += 4;
              }
              if (i9 > 0)
              {
                localObject3 = new AdaptiveCoding(i9, (CodingMethod)localObject1, (CodingMethod)localObject3);
                i18 += 4;
              }
              int[] arrayOfInt5 = computeSizePrivate((CodingMethod)localObject3);
              noteSizes((CodingMethod)localObject3, arrayOfInt5[0], arrayOfInt5[1] + i18);
            }
            i9 = i16;
            break;
          }
        }
      }
    }
    if ((verbose > 3) && (bestZipSize < i)) {
      Utils.log.info(">>> RUN WINS BY " + (i - bestZipSize));
    }
  }
  
  private static String pct(double paramDouble1, double paramDouble2)
  {
    return Math.round(paramDouble1 / paramDouble2 * 10000.0D) / 100.0D + "%";
  }
  
  private void resetData()
  {
    flushData();
    zipDef.reset();
    if (context != null) {
      try
      {
        context.writeTo(byteSizer);
      }
      catch (IOException localIOException)
      {
        throw new RuntimeException(localIOException);
      }
    }
    zipSizer.reset();
    byteSizer.reset();
  }
  
  private void flushData()
  {
    try
    {
      zipOut.finish();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
  
  private int getByteSize()
  {
    return byteSizer.getSize();
  }
  
  private int getZipSize()
  {
    flushData();
    return zipSizer.getSize();
  }
  
  void addStressSeed(int paramInt)
  {
    if (stress == null) {
      return;
    }
    stress.setSeed(paramInt + (stress.nextInt() << 32));
  }
  
  private CodingMethod stressPopCoding(CodingMethod paramCodingMethod)
  {
    assert (stress != null);
    if (!(paramCodingMethod instanceof Coding)) {
      return paramCodingMethod;
    }
    Coding localCoding = ((Coding)paramCodingMethod).getValueCoding();
    Histogram localHistogram = getValueHistogram();
    int i = stressLen(localHistogram.getTotalLength());
    if (i == 0) {
      return paramCodingMethod;
    }
    ArrayList localArrayList = new ArrayList();
    Object localObject1;
    int k;
    if (stress.nextBoolean())
    {
      localObject1 = new HashSet();
      for (k = start; k < end; k++) {
        if (((Set)localObject1).add(Integer.valueOf(values[k]))) {
          localArrayList.add(Integer.valueOf(values[k]));
        }
      }
    }
    else
    {
      localObject1 = localHistogram.getMatrix();
      for (k = 0; k < localObject1.length; k++)
      {
        Object localObject2 = localObject1[k];
        for (int n = 1; n < localObject2.length; n++) {
          localArrayList.add(Integer.valueOf(localObject2[n]));
        }
      }
    }
    int j = stress.nextInt();
    if ((j & 0x7) <= 2)
    {
      Collections.shuffle(localArrayList, stress);
    }
    else
    {
      if ((j >>>= 3 & 0x7) <= 2) {
        Collections.sort(localArrayList);
      }
      if ((j >>>= 3 & 0x7) <= 2) {
        Collections.reverse(localArrayList);
      }
      if ((j >>>= 3 & 0x7) <= 2) {
        Collections.rotate(localArrayList, stressLen(localArrayList.size()));
      }
    }
    if (localArrayList.size() > i) {
      if ((j >>>= 3 & 0x7) <= 2) {
        localArrayList.subList(i, localArrayList.size()).clear();
      } else {
        localArrayList.subList(0, localArrayList.size() - i).clear();
      }
    }
    i = localArrayList.size();
    int[] arrayOfInt1 = new int[1 + i];
    for (int m = 0; m < i; m++) {
      arrayOfInt1[(1 + m)] = ((Integer)localArrayList.get(m)).intValue();
    }
    PopulationCoding localPopulationCoding = new PopulationCoding();
    localPopulationCoding.setFavoredValues(arrayOfInt1, i);
    int[] arrayOfInt2 = PopulationCoding.LValuesCoded;
    int i2;
    for (int i1 = 0; i1 < arrayOfInt2.length / 2; i1++)
    {
      i2 = arrayOfInt2[stress.nextInt(arrayOfInt2.length)];
      if ((i2 >= 0) && (PopulationCoding.fitTokenCoding(i, i2) != null))
      {
        localPopulationCoding.setL(i2);
        break;
      }
    }
    if (tokenCoding == null)
    {
      i1 = arrayOfInt1[1];
      i2 = i1;
      for (int i3 = 2; i3 <= i; i3++)
      {
        int i4 = arrayOfInt1[i3];
        if (i1 > i4) {
          i1 = i4;
        }
        if (i2 < i4) {
          i2 = i4;
        }
      }
      tokenCoding = stressCoding(i1, i2);
    }
    computePopSizePrivate(localPopulationCoding, localCoding, localCoding);
    return localPopulationCoding;
  }
  
  private CodingMethod stressAdaptiveCoding(CodingMethod paramCodingMethod)
  {
    assert (stress != null);
    if (!(paramCodingMethod instanceof Coding)) {
      return paramCodingMethod;
    }
    Coding localCoding = (Coding)paramCodingMethod;
    int i = end - start;
    if (i < 2) {
      return paramCodingMethod;
    }
    int j = stressLen(i - 1) + 1;
    if (j == i) {
      return paramCodingMethod;
    }
    try
    {
      assert (!disableRunCoding);
      disableRunCoding = true;
      int[] arrayOfInt = (int[])values.clone();
      Object localObject1 = null;
      int k = end;
      int m = start;
      while (k > m)
      {
        int i2 = k - m < 100 ? -1 : stress.nextInt();
        if ((i2 & 0x7) != 0)
        {
          i1 = j == 1 ? j : stressLen(j - 1) + 1;
        }
        else
        {
          int i3 = i2 >>>= 3 & 0x3;
          int i4 = i2 >>>= 3 & 0xFF;
          for (;;)
          {
            i1 = AdaptiveCoding.decodeK(i3, i4);
            if (i1 <= k - m) {
              break;
            }
            if (i4 != 3) {
              i4 = 3;
            } else {
              i3--;
            }
          }
          assert (AdaptiveCoding.isCodableLength(i1));
        }
        if (i1 > k - m) {}
        for (int i1 = k - m; !AdaptiveCoding.isCodableLength(i1); i1--) {}
        int n = k - i1;
        assert (n < k);
        assert (n >= m);
        CodingMethod localCodingMethod = choose(arrayOfInt, n, k, localCoding);
        if (localObject1 == null) {
          localObject1 = localCodingMethod;
        } else {
          localObject1 = new AdaptiveCoding(k - n, localCodingMethod, (CodingMethod)localObject1);
        }
        k = n;
      }
      Object localObject2 = localObject1;
      return (CodingMethod)localObject2;
    }
    finally
    {
      disableRunCoding = false;
    }
  }
  
  private Coding stressCoding(int paramInt1, int paramInt2)
  {
    assert (stress != null);
    for (int i = 0; i < 100; i++)
    {
      Coding localCoding1 = Coding.of(stress.nextInt(5) + 1, stress.nextInt(256) + 1, stress.nextInt(3));
      if (localCoding1.B() == 1) {
        localCoding1 = localCoding1.setH(256);
      }
      if ((localCoding1.H() == 256) && (localCoding1.B() >= 5)) {
        localCoding1 = localCoding1.setB(4);
      }
      if (stress.nextBoolean())
      {
        Coding localCoding2 = localCoding1.setD(1);
        if (localCoding2.canRepresent(paramInt1, paramInt2)) {
          return localCoding2;
        }
      }
      if (localCoding1.canRepresent(paramInt1, paramInt2)) {
        return localCoding1;
      }
    }
    return BandStructure.UNSIGNED5;
  }
  
  private int stressLen(int paramInt)
  {
    assert (stress != null);
    assert (paramInt >= 0);
    int i = stress.nextInt(100);
    if (i < 20) {
      return Math.min(paramInt / 5, i);
    }
    if (i < 40) {
      return paramInt;
    }
    return stress.nextInt(paramInt);
  }
  
  static class Choice
  {
    final Coding coding;
    final int index;
    final int[] distance;
    int searchOrder;
    int minDistance;
    int zipSize;
    int byteSize;
    int histSize;
    
    Choice(Coding paramCoding, int paramInt, int[] paramArrayOfInt)
    {
      coding = paramCoding;
      index = paramInt;
      distance = paramArrayOfInt;
    }
    
    void reset()
    {
      searchOrder = Integer.MAX_VALUE;
      minDistance = Integer.MAX_VALUE;
      zipSize = (byteSize = histSize = -1);
    }
    
    boolean isExtra()
    {
      return index < 0;
    }
    
    public String toString()
    {
      return stringForDebug();
    }
    
    private String stringForDebug()
    {
      String str = "";
      if (searchOrder < Integer.MAX_VALUE) {
        str = str + " so: " + searchOrder;
      }
      if (minDistance < Integer.MAX_VALUE) {
        str = str + " md: " + minDistance;
      }
      if (zipSize > 0) {
        str = str + " zs: " + zipSize;
      }
      if (byteSize > 0) {
        str = str + " bs: " + byteSize;
      }
      if (histSize > 0) {
        str = str + " hs: " + histSize;
      }
      return "Choice[" + index + "] " + str + " " + coding;
    }
  }
  
  static class Sizer
    extends OutputStream
  {
    final OutputStream out;
    private int count;
    
    Sizer(OutputStream paramOutputStream)
    {
      out = paramOutputStream;
    }
    
    Sizer()
    {
      this(null);
    }
    
    public void write(int paramInt)
      throws IOException
    {
      count += 1;
      if (out != null) {
        out.write(paramInt);
      }
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      count += paramInt2;
      if (out != null) {
        out.write(paramArrayOfByte, paramInt1, paramInt2);
      }
    }
    
    public void reset()
    {
      count = 0;
    }
    
    public int getSize()
    {
      return count;
    }
    
    public String toString()
    {
      String str = super.toString();
      assert ((str = stringForDebug()) != null);
      return str;
    }
    
    String stringForDebug()
    {
      return "<Sizer " + getSize() + ">";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\util\jar\pack\CodingChooser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */