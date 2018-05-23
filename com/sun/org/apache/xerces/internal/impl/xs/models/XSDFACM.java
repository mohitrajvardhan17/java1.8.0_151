package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMStateSet;
import com.sun.org.apache.xerces.internal.impl.xs.SubstitutionGroupHandler;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
import com.sun.org.apache.xerces.internal.impl.xs.XSConstraints;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSWildcardDecl;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class XSDFACM
  implements XSCMValidator
{
  private static final boolean DEBUG = false;
  private static final boolean DEBUG_VALIDATE_CONTENT = false;
  private Object[] fElemMap = null;
  private int[] fElemMapType = null;
  private int[] fElemMapId = null;
  private int fElemMapSize = 0;
  private boolean[] fFinalStateFlags = null;
  private CMStateSet[] fFollowList = null;
  private CMNode fHeadNode = null;
  private int fLeafCount = 0;
  private XSCMLeaf[] fLeafList = null;
  private int[] fLeafListType = null;
  private int[][] fTransTable = (int[][])null;
  private Occurence[] fCountingStates = null;
  private int fTransTableSize = 0;
  private int[] fElemMapCounter;
  private int[] fElemMapCounterLowerBound;
  private int[] fElemMapCounterUpperBound;
  private static long time = 0L;
  
  public XSDFACM(CMNode paramCMNode, int paramInt)
  {
    fLeafCount = paramInt;
    buildDFA(paramCMNode);
  }
  
  public boolean isFinalState(int paramInt)
  {
    return paramInt < 0 ? 0 : fFinalStateFlags[paramInt];
  }
  
  public Object oneTransition(QName paramQName, int[] paramArrayOfInt, SubstitutionGroupHandler paramSubstitutionGroupHandler)
  {
    int i = paramArrayOfInt[0];
    if ((i == -1) || (i == -2))
    {
      if (i == -1) {
        paramArrayOfInt[0] = -2;
      }
      return findMatchingDecl(paramQName, paramSubstitutionGroupHandler);
    }
    int j = 0;
    int k = 0;
    Object localObject = null;
    while (k < fElemMapSize)
    {
      j = fTransTable[i][k];
      if (j != -1)
      {
        int m = fElemMapType[k];
        if (m == 1)
        {
          localObject = paramSubstitutionGroupHandler.getMatchingElemDecl(paramQName, (XSElementDecl)fElemMap[k]);
          if (localObject != null)
          {
            if (fElemMapCounter[k] < 0) {
              break;
            }
            fElemMapCounter[k] += 1;
            break;
          }
        }
        else if ((m == 2) && (((XSWildcardDecl)fElemMap[k]).allowNamespace(uri)))
        {
          localObject = fElemMap[k];
          if (fElemMapCounter[k] < 0) {
            break;
          }
          fElemMapCounter[k] += 1;
          break;
        }
      }
      k++;
    }
    if (k == fElemMapSize)
    {
      paramArrayOfInt[1] = paramArrayOfInt[0];
      paramArrayOfInt[0] = -1;
      return findMatchingDecl(paramQName, paramSubstitutionGroupHandler);
    }
    if (fCountingStates != null)
    {
      Occurence localOccurence = fCountingStates[i];
      if (localOccurence != null)
      {
        if (i == j)
        {
          if ((paramArrayOfInt[2] += 1 > maxOccurs) && (maxOccurs != -1)) {
            return findMatchingDecl(paramQName, paramArrayOfInt, paramSubstitutionGroupHandler, k);
          }
        }
        else
        {
          if (paramArrayOfInt[2] < minOccurs)
          {
            paramArrayOfInt[1] = paramArrayOfInt[0];
            paramArrayOfInt[0] = -1;
            return findMatchingDecl(paramQName, paramSubstitutionGroupHandler);
          }
          localOccurence = fCountingStates[j];
          if (localOccurence != null) {
            paramArrayOfInt[2] = (k == elemIndex ? 1 : 0);
          }
        }
      }
      else
      {
        localOccurence = fCountingStates[j];
        if (localOccurence != null) {
          paramArrayOfInt[2] = (k == elemIndex ? 1 : 0);
        }
      }
    }
    paramArrayOfInt[0] = j;
    return localObject;
  }
  
  Object findMatchingDecl(QName paramQName, SubstitutionGroupHandler paramSubstitutionGroupHandler)
  {
    XSElementDecl localXSElementDecl = null;
    for (int i = 0; i < fElemMapSize; i++)
    {
      int j = fElemMapType[i];
      if (j == 1)
      {
        localXSElementDecl = paramSubstitutionGroupHandler.getMatchingElemDecl(paramQName, (XSElementDecl)fElemMap[i]);
        if (localXSElementDecl != null) {
          return localXSElementDecl;
        }
      }
      else if ((j == 2) && (((XSWildcardDecl)fElemMap[i]).allowNamespace(uri)))
      {
        return fElemMap[i];
      }
    }
    return null;
  }
  
  Object findMatchingDecl(QName paramQName, int[] paramArrayOfInt, SubstitutionGroupHandler paramSubstitutionGroupHandler, int paramInt)
  {
    int i = paramArrayOfInt[0];
    int j = 0;
    Object localObject = null;
    for (;;)
    {
      paramInt++;
      if (paramInt >= fElemMapSize) {
        break;
      }
      j = fTransTable[i][paramInt];
      if (j != -1)
      {
        int k = fElemMapType[paramInt];
        if (k == 1)
        {
          localObject = paramSubstitutionGroupHandler.getMatchingElemDecl(paramQName, (XSElementDecl)fElemMap[paramInt]);
          if (localObject != null) {
            break;
          }
        }
        else if ((k == 2) && (((XSWildcardDecl)fElemMap[paramInt]).allowNamespace(uri)))
        {
          localObject = fElemMap[paramInt];
          break;
        }
      }
    }
    if (paramInt == fElemMapSize)
    {
      paramArrayOfInt[1] = paramArrayOfInt[0];
      paramArrayOfInt[0] = -1;
      return findMatchingDecl(paramQName, paramSubstitutionGroupHandler);
    }
    paramArrayOfInt[0] = j;
    Occurence localOccurence = fCountingStates[j];
    if (localOccurence != null) {
      paramArrayOfInt[2] = (paramInt == elemIndex ? 1 : 0);
    }
    return localObject;
  }
  
  public int[] startContentModel()
  {
    for (int i = 0; i < fElemMapSize; i++) {
      if (fElemMapCounter[i] != -1) {
        fElemMapCounter[i] = 0;
      }
    }
    return new int[3];
  }
  
  public boolean endContentModel(int[] paramArrayOfInt)
  {
    int i = paramArrayOfInt[0];
    if (fFinalStateFlags[i] != 0)
    {
      if (fCountingStates != null)
      {
        Occurence localOccurence = fCountingStates[i];
        if ((localOccurence != null) && (paramArrayOfInt[2] < minOccurs)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  private void buildDFA(CMNode paramCMNode)
  {
    int i = fLeafCount;
    XSCMLeaf localXSCMLeaf1 = new XSCMLeaf(1, null, -1, fLeafCount++);
    fHeadNode = new XSCMBinOp(102, paramCMNode, localXSCMLeaf1);
    fLeafList = new XSCMLeaf[fLeafCount];
    fLeafListType = new int[fLeafCount];
    postTreeBuildInit(fHeadNode);
    fFollowList = new CMStateSet[fLeafCount];
    for (int j = 0; j < fLeafCount; j++) {
      fFollowList[j] = new CMStateSet(fLeafCount);
    }
    calcFollowList(fHeadNode);
    fElemMap = new Object[fLeafCount];
    fElemMapType = new int[fLeafCount];
    fElemMapId = new int[fLeafCount];
    fElemMapCounter = new int[fLeafCount];
    fElemMapCounterLowerBound = new int[fLeafCount];
    fElemMapCounterUpperBound = new int[fLeafCount];
    fElemMapSize = 0;
    Occurence[] arrayOfOccurence = null;
    for (int k = 0; k < fLeafCount; k++)
    {
      fElemMap[k] = null;
      m = 0;
      n = fLeafList[k].getParticleId();
      while ((m < fElemMapSize) && (n != fElemMapId[m])) {
        m++;
      }
      if (m == fElemMapSize)
      {
        XSCMLeaf localXSCMLeaf2 = fLeafList[k];
        fElemMap[fElemMapSize] = localXSCMLeaf2.getLeaf();
        if ((localXSCMLeaf2 instanceof XSCMRepeatingLeaf))
        {
          if (arrayOfOccurence == null) {
            arrayOfOccurence = new Occurence[fLeafCount];
          }
          arrayOfOccurence[fElemMapSize] = new Occurence((XSCMRepeatingLeaf)localXSCMLeaf2, fElemMapSize);
        }
        fElemMapType[fElemMapSize] = fLeafListType[k];
        fElemMapId[fElemMapSize] = n;
        int[] arrayOfInt2 = (int[])localXSCMLeaf2.getUserData();
        if (arrayOfInt2 != null)
        {
          fElemMapCounter[fElemMapSize] = 0;
          fElemMapCounterLowerBound[fElemMapSize] = arrayOfInt2[0];
          fElemMapCounterUpperBound[fElemMapSize] = arrayOfInt2[1];
        }
        else
        {
          fElemMapCounter[fElemMapSize] = -1;
          fElemMapCounterLowerBound[fElemMapSize] = -1;
          fElemMapCounterUpperBound[fElemMapSize] = -1;
        }
        fElemMapSize += 1;
      }
    }
    fElemMapSize -= 1;
    int[] arrayOfInt1 = new int[fLeafCount + fElemMapSize];
    int m = 0;
    for (int n = 0; n < fElemMapSize; n++)
    {
      int i1 = fElemMapId[n];
      for (int i2 = 0; i2 < fLeafCount; i2++) {
        if (i1 == fLeafList[i2].getParticleId()) {
          arrayOfInt1[(m++)] = i2;
        }
      }
      arrayOfInt1[(m++)] = -1;
    }
    n = fLeafCount * 4;
    Object localObject1 = new CMStateSet[n];
    fFinalStateFlags = new boolean[n];
    fTransTable = new int[n][];
    CMStateSet localCMStateSet = fHeadNode.firstPos();
    int i3 = 0;
    int i4 = 0;
    fTransTable[i4] = makeDefStateList();
    localObject1[i4] = localCMStateSet;
    i4++;
    HashMap localHashMap = new HashMap();
    Object localObject2;
    int i6;
    while (i3 < i4)
    {
      localCMStateSet = localObject1[i3];
      int[] arrayOfInt3 = fTransTable[i3];
      fFinalStateFlags[i3] = localCMStateSet.getBit(i);
      i3++;
      localObject2 = null;
      i6 = 0;
      for (int i7 = 0; i7 < fElemMapSize; i7++)
      {
        if (localObject2 == null) {
          localObject2 = new CMStateSet(fLeafCount);
        } else {
          ((CMStateSet)localObject2).zeroBits();
        }
        for (int i8 = arrayOfInt1[(i6++)]; i8 != -1; i8 = arrayOfInt1[(i6++)]) {
          if (localCMStateSet.getBit(i8)) {
            ((CMStateSet)localObject2).union(fFollowList[i8]);
          }
        }
        if (!((CMStateSet)localObject2).isEmpty())
        {
          Integer localInteger = (Integer)localHashMap.get(localObject2);
          int i9 = localInteger == null ? i4 : localInteger.intValue();
          if (i9 == i4)
          {
            localObject1[i4] = localObject2;
            fTransTable[i4] = makeDefStateList();
            localHashMap.put(localObject2, new Integer(i4));
            i4++;
            localObject2 = null;
          }
          arrayOfInt3[i7] = i9;
          if (i4 == n)
          {
            int i10 = (int)(n * 1.5D);
            CMStateSet[] arrayOfCMStateSet = new CMStateSet[i10];
            boolean[] arrayOfBoolean = new boolean[i10];
            int[][] arrayOfInt = new int[i10][];
            System.arraycopy(localObject1, 0, arrayOfCMStateSet, 0, n);
            System.arraycopy(fFinalStateFlags, 0, arrayOfBoolean, 0, n);
            System.arraycopy(fTransTable, 0, arrayOfInt, 0, n);
            n = i10;
            localObject1 = arrayOfCMStateSet;
            fFinalStateFlags = arrayOfBoolean;
            fTransTable = arrayOfInt;
          }
        }
      }
    }
    if (arrayOfOccurence != null)
    {
      fCountingStates = new Occurence[i4];
      for (int i5 = 0; i5 < i4; i5++)
      {
        localObject2 = fTransTable[i5];
        for (i6 = 0; i6 < localObject2.length; i6++) {
          if (i5 == localObject2[i6])
          {
            fCountingStates[i5] = arrayOfOccurence[i6];
            break;
          }
        }
      }
    }
    fHeadNode = null;
    fLeafList = null;
    fFollowList = null;
    fLeafListType = null;
    fElemMapId = null;
  }
  
  private void calcFollowList(CMNode paramCMNode)
  {
    if (paramCMNode.type() == 101)
    {
      calcFollowList(((XSCMBinOp)paramCMNode).getLeft());
      calcFollowList(((XSCMBinOp)paramCMNode).getRight());
    }
    else
    {
      CMStateSet localCMStateSet1;
      CMStateSet localCMStateSet2;
      int i;
      if (paramCMNode.type() == 102)
      {
        calcFollowList(((XSCMBinOp)paramCMNode).getLeft());
        calcFollowList(((XSCMBinOp)paramCMNode).getRight());
        localCMStateSet1 = ((XSCMBinOp)paramCMNode).getLeft().lastPos();
        localCMStateSet2 = ((XSCMBinOp)paramCMNode).getRight().firstPos();
        for (i = 0; i < fLeafCount; i++) {
          if (localCMStateSet1.getBit(i)) {
            fFollowList[i].union(localCMStateSet2);
          }
        }
      }
      else if ((paramCMNode.type() == 4) || (paramCMNode.type() == 6))
      {
        calcFollowList(((XSCMUniOp)paramCMNode).getChild());
        localCMStateSet1 = paramCMNode.firstPos();
        localCMStateSet2 = paramCMNode.lastPos();
        for (i = 0; i < fLeafCount; i++) {
          if (localCMStateSet2.getBit(i)) {
            fFollowList[i].union(localCMStateSet1);
          }
        }
      }
      else if (paramCMNode.type() == 5)
      {
        calcFollowList(((XSCMUniOp)paramCMNode).getChild());
      }
    }
  }
  
  private void dumpTree(CMNode paramCMNode, int paramInt)
  {
    for (int i = 0; i < paramInt; i++) {
      System.out.print("   ");
    }
    i = paramCMNode.type();
    switch (i)
    {
    case 101: 
    case 102: 
      if (i == 101) {
        System.out.print("Choice Node ");
      } else {
        System.out.print("Seq Node ");
      }
      if (paramCMNode.isNullable()) {
        System.out.print("Nullable ");
      }
      System.out.print("firstPos=");
      System.out.print(paramCMNode.firstPos().toString());
      System.out.print(" lastPos=");
      System.out.println(paramCMNode.lastPos().toString());
      dumpTree(((XSCMBinOp)paramCMNode).getLeft(), paramInt + 1);
      dumpTree(((XSCMBinOp)paramCMNode).getRight(), paramInt + 1);
      break;
    case 4: 
    case 5: 
    case 6: 
      System.out.print("Rep Node ");
      if (paramCMNode.isNullable()) {
        System.out.print("Nullable ");
      }
      System.out.print("firstPos=");
      System.out.print(paramCMNode.firstPos().toString());
      System.out.print(" lastPos=");
      System.out.println(paramCMNode.lastPos().toString());
      dumpTree(((XSCMUniOp)paramCMNode).getChild(), paramInt + 1);
      break;
    case 1: 
      System.out.print("Leaf: (pos=" + ((XSCMLeaf)paramCMNode).getPosition() + "), (elemIndex=" + ((XSCMLeaf)paramCMNode).getLeaf() + ") ");
      if (paramCMNode.isNullable()) {
        System.out.print(" Nullable ");
      }
      System.out.print("firstPos=");
      System.out.print(paramCMNode.firstPos().toString());
      System.out.print(" lastPos=");
      System.out.println(paramCMNode.lastPos().toString());
      break;
    case 2: 
      System.out.print("Any Node: ");
      System.out.print("firstPos=");
      System.out.print(paramCMNode.firstPos().toString());
      System.out.print(" lastPos=");
      System.out.println(paramCMNode.lastPos().toString());
      break;
    default: 
      throw new RuntimeException("ImplementationMessages.VAL_NIICM");
    }
  }
  
  private int[] makeDefStateList()
  {
    int[] arrayOfInt = new int[fElemMapSize];
    for (int i = 0; i < fElemMapSize; i++) {
      arrayOfInt[i] = -1;
    }
    return arrayOfInt;
  }
  
  private void postTreeBuildInit(CMNode paramCMNode)
    throws RuntimeException
  {
    paramCMNode.setMaxStates(fLeafCount);
    XSCMLeaf localXSCMLeaf = null;
    int i = 0;
    if (paramCMNode.type() == 2)
    {
      localXSCMLeaf = (XSCMLeaf)paramCMNode;
      i = localXSCMLeaf.getPosition();
      fLeafList[i] = localXSCMLeaf;
      fLeafListType[i] = 2;
    }
    else if ((paramCMNode.type() == 101) || (paramCMNode.type() == 102))
    {
      postTreeBuildInit(((XSCMBinOp)paramCMNode).getLeft());
      postTreeBuildInit(((XSCMBinOp)paramCMNode).getRight());
    }
    else if ((paramCMNode.type() == 4) || (paramCMNode.type() == 6) || (paramCMNode.type() == 5))
    {
      postTreeBuildInit(((XSCMUniOp)paramCMNode).getChild());
    }
    else if (paramCMNode.type() == 1)
    {
      localXSCMLeaf = (XSCMLeaf)paramCMNode;
      i = localXSCMLeaf.getPosition();
      fLeafList[i] = localXSCMLeaf;
      fLeafListType[i] = 1;
    }
    else
    {
      throw new RuntimeException("ImplementationMessages.VAL_NIICM");
    }
  }
  
  public boolean checkUniqueParticleAttribution(SubstitutionGroupHandler paramSubstitutionGroupHandler)
    throws XMLSchemaException
  {
    byte[][] arrayOfByte = new byte[fElemMapSize][fElemMapSize];
    int j;
    for (int i = 0; (i < fTransTable.length) && (fTransTable[i] != null); i++) {
      for (j = 0; j < fElemMapSize; j++) {
        for (int k = j + 1; k < fElemMapSize; k++) {
          if ((fTransTable[i][j] != -1) && (fTransTable[i][k] != -1) && (arrayOfByte[j][k] == 0)) {
            if (XSConstraints.overlapUPA(fElemMap[j], fElemMap[k], paramSubstitutionGroupHandler))
            {
              if (fCountingStates != null)
              {
                Occurence localOccurence = fCountingStates[i];
                if (localOccurence != null) {
                  if ((((fTransTable[i][j] == i ? 1 : 0) ^ (fTransTable[i][k] == i ? 1 : 0)) != 0) && (minOccurs == maxOccurs))
                  {
                    arrayOfByte[j][k] = -1;
                    continue;
                  }
                }
              }
              arrayOfByte[j][k] = 1;
            }
            else
            {
              arrayOfByte[j][k] = -1;
            }
          }
        }
      }
    }
    for (i = 0; i < fElemMapSize; i++) {
      for (j = 0; j < fElemMapSize; j++) {
        if (arrayOfByte[i][j] == 1) {
          throw new XMLSchemaException("cos-nonambig", new Object[] { fElemMap[i].toString(), fElemMap[j].toString() });
        }
      }
    }
    for (i = 0; i < fElemMapSize; i++) {
      if (fElemMapType[i] == 2)
      {
        XSWildcardDecl localXSWildcardDecl = (XSWildcardDecl)fElemMap[i];
        if ((fType == 3) || (fType == 2)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public Vector whatCanGoHere(int[] paramArrayOfInt)
  {
    int i = paramArrayOfInt[0];
    if (i < 0) {
      i = paramArrayOfInt[1];
    }
    Object localObject = fCountingStates != null ? fCountingStates[i] : null;
    int j = paramArrayOfInt[2];
    Vector localVector = new Vector();
    for (int k = 0; k < fElemMapSize; k++)
    {
      int m = fTransTable[i][k];
      if ((m != -1) && ((localObject == null) || (i == m ? (j < maxOccurs) && (maxOccurs == -1) : j >= minOccurs))) {
        localVector.addElement(fElemMap[k]);
      }
    }
    return localVector;
  }
  
  public ArrayList checkMinMaxBounds()
  {
    ArrayList localArrayList = null;
    for (int i = 0; i < fElemMapSize; i++)
    {
      int j = fElemMapCounter[i];
      if (j != -1)
      {
        int k = fElemMapCounterLowerBound[i];
        int m = fElemMapCounterUpperBound[i];
        if (j < k)
        {
          if (localArrayList == null) {
            localArrayList = new ArrayList();
          }
          localArrayList.add("cvc-complex-type.2.4.b");
          localArrayList.add("{" + fElemMap[i] + "}");
        }
        if ((m != -1) && (j > m))
        {
          if (localArrayList == null) {
            localArrayList = new ArrayList();
          }
          localArrayList.add("cvc-complex-type.2.4.e");
          localArrayList.add("{" + fElemMap[i] + "}");
        }
      }
    }
    return localArrayList;
  }
  
  static final class Occurence
  {
    final int minOccurs;
    final int maxOccurs;
    final int elemIndex;
    
    public Occurence(XSCMRepeatingLeaf paramXSCMRepeatingLeaf, int paramInt)
    {
      minOccurs = paramXSCMRepeatingLeaf.getMinOccurs();
      maxOccurs = paramXSCMRepeatingLeaf.getMaxOccurs();
      elemIndex = paramInt;
    }
    
    public String toString()
    {
      return "minOccurs=" + minOccurs + ";maxOccurs=" + (maxOccurs != -1 ? Integer.toString(maxOccurs) : "unbounded");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\models\XSDFACM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */