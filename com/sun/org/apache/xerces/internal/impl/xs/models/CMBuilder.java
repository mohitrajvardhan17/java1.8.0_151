package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSDeclarationPool;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSModelGroupImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;

public class CMBuilder
{
  private XSDeclarationPool fDeclPool = null;
  private static XSEmptyCM fEmptyCM = new XSEmptyCM();
  private int fLeafCount;
  private int fParticleCount;
  private CMNodeFactory fNodeFactory;
  
  public CMBuilder(CMNodeFactory paramCMNodeFactory)
  {
    fNodeFactory = paramCMNodeFactory;
  }
  
  public void setDeclPool(XSDeclarationPool paramXSDeclarationPool)
  {
    fDeclPool = paramXSDeclarationPool;
  }
  
  public XSCMValidator getContentModel(XSComplexTypeDecl paramXSComplexTypeDecl)
  {
    int i = paramXSComplexTypeDecl.getContentType();
    if ((i == 1) || (i == 0)) {
      return null;
    }
    XSParticleDecl localXSParticleDecl = (XSParticleDecl)paramXSComplexTypeDecl.getParticle();
    if (localXSParticleDecl == null) {
      return fEmptyCM;
    }
    Object localObject = null;
    if ((fType == 3) && (fValue).fCompositor == 103)) {
      localObject = createAllCM(localXSParticleDecl);
    } else {
      localObject = createDFACM(localXSParticleDecl);
    }
    fNodeFactory.resetNodeCount();
    if (localObject == null) {
      localObject = fEmptyCM;
    }
    return (XSCMValidator)localObject;
  }
  
  XSCMValidator createAllCM(XSParticleDecl paramXSParticleDecl)
  {
    if (fMaxOccurs == 0) {
      return null;
    }
    XSModelGroupImpl localXSModelGroupImpl = (XSModelGroupImpl)fValue;
    XSAllCM localXSAllCM = new XSAllCM(fMinOccurs == 0, fParticleCount);
    for (int i = 0; i < fParticleCount; i++) {
      localXSAllCM.addElement((XSElementDecl)fParticles[i].fValue, fParticles[i].fMinOccurs == 0);
    }
    return localXSAllCM;
  }
  
  XSCMValidator createDFACM(XSParticleDecl paramXSParticleDecl)
  {
    fLeafCount = 0;
    fParticleCount = 0;
    CMNode localCMNode = useRepeatingLeafNodes(paramXSParticleDecl) ? buildCompactSyntaxTree(paramXSParticleDecl) : buildSyntaxTree(paramXSParticleDecl, true);
    if (localCMNode == null) {
      return null;
    }
    return new XSDFACM(localCMNode, fLeafCount);
  }
  
  private CMNode buildSyntaxTree(XSParticleDecl paramXSParticleDecl, boolean paramBoolean)
  {
    int i = fMaxOccurs;
    int j = fMinOccurs;
    int k = fType;
    Object localObject = null;
    if ((k == 2) || (k == 1))
    {
      localObject = fNodeFactory.getCMLeafNode(fType, fValue, fParticleCount++, fLeafCount++);
      localObject = expandContentModel((CMNode)localObject, j, i, paramBoolean);
    }
    else if (k == 3)
    {
      XSModelGroupImpl localXSModelGroupImpl = (XSModelGroupImpl)fValue;
      CMNode localCMNode = null;
      int m = 0;
      for (int n = 0; n < fParticleCount; n++)
      {
        localCMNode = buildSyntaxTree(fParticles[n], (paramBoolean) && (j == 1) && (i == 1) && ((fCompositor == 102) || (fParticleCount == 1)));
        if (localCMNode != null) {
          if (localObject == null)
          {
            localObject = localCMNode;
          }
          else
          {
            localObject = fNodeFactory.getCMBinOpNode(fCompositor, (CMNode)localObject, localCMNode);
            m = 1;
          }
        }
      }
      if (localObject != null)
      {
        if ((fCompositor == 101) && (m == 0) && (fParticleCount > 1)) {
          localObject = fNodeFactory.getCMUniOpNode(5, (CMNode)localObject);
        }
        localObject = expandContentModel((CMNode)localObject, j, i, false);
      }
    }
    return (CMNode)localObject;
  }
  
  private CMNode expandContentModel(CMNode paramCMNode, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    CMNode localCMNode = null;
    if ((paramInt1 == 1) && (paramInt2 == 1))
    {
      localCMNode = paramCMNode;
    }
    else if ((paramInt1 == 0) && (paramInt2 == 1))
    {
      localCMNode = fNodeFactory.getCMUniOpNode(5, paramCMNode);
    }
    else if ((paramInt1 == 0) && (paramInt2 == -1))
    {
      localCMNode = fNodeFactory.getCMUniOpNode(4, paramCMNode);
    }
    else if ((paramInt1 == 1) && (paramInt2 == -1))
    {
      localCMNode = fNodeFactory.getCMUniOpNode(6, paramCMNode);
    }
    else if (((paramBoolean) && (paramCMNode.type() == 1)) || (paramCMNode.type() == 2))
    {
      localCMNode = fNodeFactory.getCMUniOpNode(paramInt1 == 0 ? 4 : 6, paramCMNode);
      localCMNode.setUserData(new int[] { paramInt1, paramInt2 });
    }
    else if (paramInt2 == -1)
    {
      localCMNode = fNodeFactory.getCMUniOpNode(6, paramCMNode);
      localCMNode = fNodeFactory.getCMBinOpNode(102, multiNodes(paramCMNode, paramInt1 - 1, true), localCMNode);
    }
    else
    {
      if (paramInt1 > 0) {
        localCMNode = multiNodes(paramCMNode, paramInt1, false);
      }
      if (paramInt2 > paramInt1)
      {
        paramCMNode = fNodeFactory.getCMUniOpNode(5, paramCMNode);
        if (localCMNode == null) {
          localCMNode = multiNodes(paramCMNode, paramInt2 - paramInt1, false);
        } else {
          localCMNode = fNodeFactory.getCMBinOpNode(102, localCMNode, multiNodes(paramCMNode, paramInt2 - paramInt1, true));
        }
      }
    }
    return localCMNode;
  }
  
  private CMNode multiNodes(CMNode paramCMNode, int paramInt, boolean paramBoolean)
  {
    if (paramInt == 0) {
      return null;
    }
    if (paramInt == 1) {
      return paramBoolean ? copyNode(paramCMNode) : paramCMNode;
    }
    int i = paramInt / 2;
    return fNodeFactory.getCMBinOpNode(102, multiNodes(paramCMNode, i, paramBoolean), multiNodes(paramCMNode, paramInt - i, true));
  }
  
  private CMNode copyNode(CMNode paramCMNode)
  {
    int i = paramCMNode.type();
    Object localObject;
    if ((i == 101) || (i == 102))
    {
      localObject = (XSCMBinOp)paramCMNode;
      paramCMNode = fNodeFactory.getCMBinOpNode(i, copyNode(((XSCMBinOp)localObject).getLeft()), copyNode(((XSCMBinOp)localObject).getRight()));
    }
    else if ((i == 4) || (i == 6) || (i == 5))
    {
      localObject = (XSCMUniOp)paramCMNode;
      paramCMNode = fNodeFactory.getCMUniOpNode(i, copyNode(((XSCMUniOp)localObject).getChild()));
    }
    else if ((i == 1) || (i == 2))
    {
      localObject = (XSCMLeaf)paramCMNode;
      paramCMNode = fNodeFactory.getCMLeafNode(((XSCMLeaf)localObject).type(), ((XSCMLeaf)localObject).getLeaf(), ((XSCMLeaf)localObject).getParticleId(), fLeafCount++);
    }
    return paramCMNode;
  }
  
  private CMNode buildCompactSyntaxTree(XSParticleDecl paramXSParticleDecl)
  {
    int i = fMaxOccurs;
    int j = fMinOccurs;
    int k = fType;
    Object localObject = null;
    if ((k == 2) || (k == 1)) {
      return buildCompactSyntaxTree2(paramXSParticleDecl, j, i);
    }
    if (k == 3)
    {
      XSModelGroupImpl localXSModelGroupImpl = (XSModelGroupImpl)fValue;
      if ((fParticleCount == 1) && ((j != 1) || (i != 1))) {
        return buildCompactSyntaxTree2(fParticles[0], j, i);
      }
      CMNode localCMNode = null;
      int m = 0;
      for (int n = 0; n < fParticleCount; n++)
      {
        localCMNode = buildCompactSyntaxTree(fParticles[n]);
        if (localCMNode != null)
        {
          m++;
          if (localObject == null) {
            localObject = localCMNode;
          } else {
            localObject = fNodeFactory.getCMBinOpNode(fCompositor, (CMNode)localObject, localCMNode);
          }
        }
      }
      if ((localObject != null) && (fCompositor == 101) && (m < fParticleCount)) {
        localObject = fNodeFactory.getCMUniOpNode(5, (CMNode)localObject);
      }
    }
    return (CMNode)localObject;
  }
  
  private CMNode buildCompactSyntaxTree2(XSParticleDecl paramXSParticleDecl, int paramInt1, int paramInt2)
  {
    CMNode localCMNode = null;
    if ((paramInt1 == 1) && (paramInt2 == 1))
    {
      localCMNode = fNodeFactory.getCMLeafNode(fType, fValue, fParticleCount++, fLeafCount++);
    }
    else if ((paramInt1 == 0) && (paramInt2 == 1))
    {
      localCMNode = fNodeFactory.getCMLeafNode(fType, fValue, fParticleCount++, fLeafCount++);
      localCMNode = fNodeFactory.getCMUniOpNode(5, localCMNode);
    }
    else if ((paramInt1 == 0) && (paramInt2 == -1))
    {
      localCMNode = fNodeFactory.getCMLeafNode(fType, fValue, fParticleCount++, fLeafCount++);
      localCMNode = fNodeFactory.getCMUniOpNode(4, localCMNode);
    }
    else if ((paramInt1 == 1) && (paramInt2 == -1))
    {
      localCMNode = fNodeFactory.getCMLeafNode(fType, fValue, fParticleCount++, fLeafCount++);
      localCMNode = fNodeFactory.getCMUniOpNode(6, localCMNode);
    }
    else
    {
      localCMNode = fNodeFactory.getCMRepeatingLeafNode(fType, fValue, paramInt1, paramInt2, fParticleCount++, fLeafCount++);
      if (paramInt1 == 0) {
        localCMNode = fNodeFactory.getCMUniOpNode(4, localCMNode);
      } else {
        localCMNode = fNodeFactory.getCMUniOpNode(6, localCMNode);
      }
    }
    return localCMNode;
  }
  
  private boolean useRepeatingLeafNodes(XSParticleDecl paramXSParticleDecl)
  {
    int i = fMaxOccurs;
    int j = fMinOccurs;
    int k = fType;
    if (k == 3)
    {
      XSModelGroupImpl localXSModelGroupImpl = (XSModelGroupImpl)fValue;
      if ((j != 1) || (i != 1))
      {
        if (fParticleCount == 1)
        {
          XSParticleDecl localXSParticleDecl = fParticles[0];
          int n = fType;
          return ((n == 1) || (n == 2)) && (fMinOccurs == 1) && (fMaxOccurs == 1);
        }
        return fParticleCount == 0;
      }
      for (int m = 0; m < fParticleCount; m++) {
        if (!useRepeatingLeafNodes(fParticles[m])) {
          return false;
        }
      }
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\models\CMBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */