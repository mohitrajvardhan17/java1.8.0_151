package sun.security.provider.certpath;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.PublicKey;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathBuilderSpi;
import java.security.cert.CertPathChecker;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorException.BasicReason;
import java.security.cert.CertSelector;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.PolicyNode;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.PKIXExtensions;

public final class SunCertPathBuilder
  extends CertPathBuilderSpi
{
  private static final Debug debug = Debug.getInstance("certpath");
  private PKIX.BuilderParams buildParams;
  private CertificateFactory cf;
  private boolean pathCompleted = false;
  private PolicyNode policyTreeResult;
  private TrustAnchor trustAnchor;
  private PublicKey finalPublicKey;
  
  public SunCertPathBuilder()
    throws CertPathBuilderException
  {
    try
    {
      cf = CertificateFactory.getInstance("X.509");
    }
    catch (CertificateException localCertificateException)
    {
      throw new CertPathBuilderException(localCertificateException);
    }
  }
  
  public CertPathChecker engineGetRevocationChecker()
  {
    return new RevocationChecker();
  }
  
  public CertPathBuilderResult engineBuild(CertPathParameters paramCertPathParameters)
    throws CertPathBuilderException, InvalidAlgorithmParameterException
  {
    if (debug != null) {
      debug.println("SunCertPathBuilder.engineBuild(" + paramCertPathParameters + ")");
    }
    buildParams = PKIX.checkBuilderParams(paramCertPathParameters);
    return build();
  }
  
  private PKIXCertPathBuilderResult build()
    throws CertPathBuilderException
  {
    ArrayList localArrayList = new ArrayList();
    PKIXCertPathBuilderResult localPKIXCertPathBuilderResult = buildCertPath(false, localArrayList);
    if (localPKIXCertPathBuilderResult == null)
    {
      if (debug != null) {
        debug.println("SunCertPathBuilder.engineBuild: 2nd pass; try building again searching all certstores");
      }
      localArrayList.clear();
      localPKIXCertPathBuilderResult = buildCertPath(true, localArrayList);
      if (localPKIXCertPathBuilderResult == null) {
        throw new SunCertPathBuilderException("unable to find valid certification path to requested target", new AdjacencyList(localArrayList));
      }
    }
    return localPKIXCertPathBuilderResult;
  }
  
  private PKIXCertPathBuilderResult buildCertPath(boolean paramBoolean, List<List<Vertex>> paramList)
    throws CertPathBuilderException
  {
    pathCompleted = false;
    trustAnchor = null;
    finalPublicKey = null;
    policyTreeResult = null;
    LinkedList localLinkedList = new LinkedList();
    try
    {
      buildForward(paramList, localLinkedList, paramBoolean);
    }
    catch (GeneralSecurityException|IOException localGeneralSecurityException)
    {
      if (debug != null)
      {
        debug.println("SunCertPathBuilder.engineBuild() exception in build");
        localGeneralSecurityException.printStackTrace();
      }
      throw new SunCertPathBuilderException("unable to find valid certification path to requested target", localGeneralSecurityException, new AdjacencyList(paramList));
    }
    try
    {
      if (pathCompleted)
      {
        if (debug != null) {
          debug.println("SunCertPathBuilder.engineBuild() pathCompleted");
        }
        Collections.reverse(localLinkedList);
        return new SunCertPathBuilderResult(cf.generateCertPath(localLinkedList), trustAnchor, policyTreeResult, finalPublicKey, new AdjacencyList(paramList));
      }
    }
    catch (CertificateException localCertificateException)
    {
      if (debug != null)
      {
        debug.println("SunCertPathBuilder.engineBuild() exception in wrap-up");
        localCertificateException.printStackTrace();
      }
      throw new SunCertPathBuilderException("unable to find valid certification path to requested target", localCertificateException, new AdjacencyList(paramList));
    }
    return null;
  }
  
  private void buildForward(List<List<Vertex>> paramList, LinkedList<X509Certificate> paramLinkedList, boolean paramBoolean)
    throws GeneralSecurityException, IOException
  {
    if (debug != null) {
      debug.println("SunCertPathBuilder.buildForward()...");
    }
    ForwardState localForwardState = new ForwardState();
    localForwardState.initState(buildParams.certPathCheckers());
    paramList.clear();
    paramList.add(new LinkedList());
    untrustedChecker = new UntrustedChecker();
    depthFirstSearchForward(buildParams.targetSubject(), localForwardState, new ForwardBuilder(buildParams, paramBoolean), paramList, paramLinkedList);
  }
  
  private void depthFirstSearchForward(X500Principal paramX500Principal, ForwardState paramForwardState, ForwardBuilder paramForwardBuilder, List<List<Vertex>> paramList, LinkedList<X509Certificate> paramLinkedList)
    throws GeneralSecurityException, IOException
  {
    if (debug != null) {
      debug.println("SunCertPathBuilder.depthFirstSearchForward(" + paramX500Principal + ", " + paramForwardState.toString() + ")");
    }
    Collection localCollection = paramForwardBuilder.getMatchingCerts(paramForwardState, buildParams.certStores());
    List localList = addVertices(localCollection, paramList);
    if (debug != null) {
      debug.println("SunCertPathBuilder.depthFirstSearchForward(): certs.size=" + localList.size());
    }
    Iterator localIterator1 = localList.iterator();
    while (localIterator1.hasNext())
    {
      Vertex localVertex = (Vertex)localIterator1.next();
      ForwardState localForwardState = (ForwardState)paramForwardState.clone();
      X509Certificate localX509Certificate = localVertex.getCertificate();
      try
      {
        paramForwardBuilder.verifyCert(localX509Certificate, localForwardState, paramLinkedList);
      }
      catch (GeneralSecurityException localGeneralSecurityException)
      {
        if (debug != null)
        {
          debug.println("SunCertPathBuilder.depthFirstSearchForward(): validation failed: " + localGeneralSecurityException);
          localGeneralSecurityException.printStackTrace();
        }
        localVertex.setThrowable(localGeneralSecurityException);
      }
      continue;
      if (paramForwardBuilder.isPathCompleted(localX509Certificate))
      {
        if (debug != null) {
          debug.println("SunCertPathBuilder.depthFirstSearchForward(): commencing final verification");
        }
        ArrayList localArrayList1 = new ArrayList(paramLinkedList);
        if (trustAnchor.getTrustedCert() == null) {
          localArrayList1.add(0, localX509Certificate);
        }
        Set localSet1 = Collections.singleton("2.5.29.32.0");
        PolicyNodeImpl localPolicyNodeImpl = new PolicyNodeImpl(null, "2.5.29.32.0", null, false, localSet1, false);
        ArrayList localArrayList2 = new ArrayList();
        PolicyChecker localPolicyChecker = new PolicyChecker(buildParams.initialPolicies(), localArrayList1.size(), buildParams.explicitPolicyRequired(), buildParams.policyMappingInhibited(), buildParams.anyPolicyInhibited(), buildParams.policyQualifiersRejected(), localPolicyNodeImpl);
        localArrayList2.add(localPolicyChecker);
        localArrayList2.add(new AlgorithmChecker(trustAnchor, buildParams.date(), buildParams.variant()));
        BasicChecker localBasicChecker = null;
        if (localForwardState.keyParamsNeeded())
        {
          PublicKey localPublicKey = localX509Certificate.getPublicKey();
          if (trustAnchor.getTrustedCert() == null)
          {
            localPublicKey = trustAnchor.getCAPublicKey();
            if (debug != null) {
              debug.println("SunCertPathBuilder.depthFirstSearchForward using buildParams public key: " + localPublicKey.toString());
            }
          }
          localObject1 = new TrustAnchor(localX509Certificate.getSubjectX500Principal(), localPublicKey, null);
          localBasicChecker = new BasicChecker((TrustAnchor)localObject1, buildParams.date(), buildParams.sigProvider(), true);
          localArrayList2.add(localBasicChecker);
        }
        buildParams.setCertPath(cf.generateCertPath(localArrayList1));
        int i = 0;
        Object localObject1 = buildParams.certPathCheckers();
        Iterator localIterator2 = ((List)localObject1).iterator();
        Object localObject3;
        while (localIterator2.hasNext())
        {
          localObject3 = (PKIXCertPathChecker)localIterator2.next();
          if ((localObject3 instanceof PKIXRevocationChecker))
          {
            if (i != 0) {
              throw new CertPathValidatorException("Only one PKIXRevocationChecker can be specified");
            }
            i = 1;
            if ((localObject3 instanceof RevocationChecker)) {
              ((RevocationChecker)localObject3).init(trustAnchor, buildParams);
            }
          }
        }
        if ((buildParams.revocationEnabled()) && (i == 0)) {
          localArrayList2.add(new RevocationChecker(trustAnchor, buildParams));
        }
        localArrayList2.addAll((Collection)localObject1);
        for (int j = 0;; j++)
        {
          if (j >= localArrayList1.size()) {
            break label1163;
          }
          localObject3 = (X509Certificate)localArrayList1.get(j);
          if (debug != null) {
            debug.println("current subject = " + ((X509Certificate)localObject3).getSubjectX500Principal());
          }
          Set localSet2 = ((X509Certificate)localObject3).getCriticalExtensionOIDs();
          if (localSet2 == null) {
            localSet2 = Collections.emptySet();
          }
          Iterator localIterator3 = localArrayList2.iterator();
          PKIXCertPathChecker localPKIXCertPathChecker;
          for (;;)
          {
            if (!localIterator3.hasNext()) {
              break label926;
            }
            localPKIXCertPathChecker = (PKIXCertPathChecker)localIterator3.next();
            if (!localPKIXCertPathChecker.isForwardCheckingSupported())
            {
              if (j == 0)
              {
                localPKIXCertPathChecker.init(false);
                if ((localPKIXCertPathChecker instanceof AlgorithmChecker)) {
                  ((AlgorithmChecker)localPKIXCertPathChecker).trySetTrustAnchor(trustAnchor);
                }
              }
              try
              {
                localPKIXCertPathChecker.check((Certificate)localObject3, localSet2);
              }
              catch (CertPathValidatorException localCertPathValidatorException)
              {
                if (debug != null) {
                  debug.println("SunCertPathBuilder.depthFirstSearchForward(): final verification failed: " + localCertPathValidatorException);
                }
                if ((buildParams.targetCertConstraints().match((Certificate)localObject3)) && (localCertPathValidatorException.getReason() == CertPathValidatorException.BasicReason.REVOKED)) {
                  throw localCertPathValidatorException;
                }
                localVertex.setThrowable(localCertPathValidatorException);
              }
              break;
            }
          }
          label926:
          localIterator3 = buildParams.certPathCheckers().iterator();
          while (localIterator3.hasNext())
          {
            localPKIXCertPathChecker = (PKIXCertPathChecker)localIterator3.next();
            if (localPKIXCertPathChecker.isForwardCheckingSupported())
            {
              Set localSet3 = localPKIXCertPathChecker.getSupportedExtensions();
              if (localSet3 != null) {
                localSet2.removeAll(localSet3);
              }
            }
          }
          if (!localSet2.isEmpty())
          {
            localSet2.remove(PKIXExtensions.BasicConstraints_Id.toString());
            localSet2.remove(PKIXExtensions.NameConstraints_Id.toString());
            localSet2.remove(PKIXExtensions.CertificatePolicies_Id.toString());
            localSet2.remove(PKIXExtensions.PolicyMappings_Id.toString());
            localSet2.remove(PKIXExtensions.PolicyConstraints_Id.toString());
            localSet2.remove(PKIXExtensions.InhibitAnyPolicy_Id.toString());
            localSet2.remove(PKIXExtensions.SubjectAlternativeName_Id.toString());
            localSet2.remove(PKIXExtensions.KeyUsage_Id.toString());
            localSet2.remove(PKIXExtensions.ExtendedKeyUsage_Id.toString());
            if (!localSet2.isEmpty()) {
              throw new CertPathValidatorException("unrecognized critical extension(s)", null, null, -1, PKIXReason.UNRECOGNIZED_CRIT_EXT);
            }
          }
        }
        label1163:
        if (debug != null) {
          debug.println("SunCertPathBuilder.depthFirstSearchForward(): final verification succeeded - path completed!");
        }
        pathCompleted = true;
        if (trustAnchor.getTrustedCert() == null) {
          paramForwardBuilder.addCertToPath(localX509Certificate, paramLinkedList);
        }
        trustAnchor = trustAnchor;
        if (localBasicChecker != null)
        {
          finalPublicKey = localBasicChecker.getPublicKey();
        }
        else
        {
          Object localObject2;
          if (paramLinkedList.isEmpty()) {
            localObject2 = trustAnchor.getTrustedCert();
          } else {
            localObject2 = (Certificate)paramLinkedList.getLast();
          }
          finalPublicKey = ((Certificate)localObject2).getPublicKey();
        }
        policyTreeResult = localPolicyChecker.getPolicyTree();
        return;
      }
      paramForwardBuilder.addCertToPath(localX509Certificate, paramLinkedList);
      localForwardState.updateState(localX509Certificate);
      paramList.add(new LinkedList());
      localVertex.setIndex(paramList.size() - 1);
      depthFirstSearchForward(localX509Certificate.getIssuerX500Principal(), localForwardState, paramForwardBuilder, paramList, paramLinkedList);
      if (pathCompleted) {
        return;
      }
      if (debug != null) {
        debug.println("SunCertPathBuilder.depthFirstSearchForward(): backtracking");
      }
      paramForwardBuilder.removeFinalCertFromPath(paramLinkedList);
    }
  }
  
  private static List<Vertex> addVertices(Collection<X509Certificate> paramCollection, List<List<Vertex>> paramList)
  {
    List localList = (List)paramList.get(paramList.size() - 1);
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      X509Certificate localX509Certificate = (X509Certificate)localIterator.next();
      Vertex localVertex = new Vertex(localX509Certificate);
      localList.add(localVertex);
    }
    return localList;
  }
  
  private static boolean anchorIsTarget(TrustAnchor paramTrustAnchor, CertSelector paramCertSelector)
  {
    X509Certificate localX509Certificate = paramTrustAnchor.getTrustedCert();
    if (localX509Certificate != null) {
      return paramCertSelector.match(localX509Certificate);
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\SunCertPathBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */