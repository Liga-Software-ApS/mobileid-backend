package dk.liga.mobileid.backendapi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.x509.GeneralName;

public class X509Utils {

    public static String getUPN(X509Certificate cert) {

        var upn = resolvePrincipalInternal(cert);

        return upn;
    //     try {
    //         var sans = cert.getSubjectAlternativeNames();

    //         for (List<?> generalName : sans) {
    //             final Object key = generalName.get(0);
    //             System.out.println(key);

    //             final Object value = generalName.get(1);
    //             System.out.println(value.getClass().getCanonicalName());

    //             if (value instanceof byte[]) {
    //                 var all = Arrays.toString((byte[]) value);
    //                 System.out.println(all);


    //                 ASN1InputStream input = new ASN1InputStream((byte[]) value);

    //                 var seqObj = input.readObject();

    //                 var asn1 = ASN1Sequence.getInstance(seqObj);
    //                 System.out.println(asn1.size());

    //                 ASN1ObjectIdentifier oid = ASN1ObjectIdentifier.getInstance(asn1.getObjectAt(0));

    //                 System.out.println(oid);

    //                 var sanValue = asn1.getObjectAt(1);
    //                 var to = (org.bouncycastle.asn1.DLTaggedObject)sanValue;
    //                 System.out.println(to.getObject());
    //                 System.out.println(sanValue.getClass().getName());


    //                 ASN1InputStream input2 = new ASN1InputStream(sanValue.toASN1Primitive().getEncoded());
    //                 System.out.println("a");

    //                 var seqObj2 = input2.readObject();
    //                 System.out.println("b");

    //                 var asdf = asn1.getObjectAt(1);
                    
    //                 var utf8str = ((DLTaggedObject)asdf).getLoadedObject();
    //                 System.out.println("dd");
    //                 System.out.println(utf8str);
    //                 System.out.println(utf8str.getClass().getName());

    //                 var printable = Arrays.toString(sanValue.toASN1Primitive().getEncoded());
    //                 // var p2 = new String(sanValue.getEncoded(), "UTF-8");
    //                 System.out.println(printable);

    //                 var aaa = ASN1UTF8String.getInstance(seqObj2);


    //                 System.out.println(aaa.getString());
    //                 System.out.println("c");

    //                 // var seqObj2 = input.readObject()

    //                 // var asn2 = ASN1Sequence.getInstance(sanValue);
    //                 // System.out.println(asn2.size());

    //                 // System.out.println(sanValue.getEncoded("UTF-8"));



    //                 // ASN1UTF8String nameData = ASN1UTF8String.getInstance(sanValue);

    //                 // System.out.println(nameData.toString());

    //                 // result.add(((String) value).toLowerCase());
    //             }
    //         }

    //         var upns = sans.stream().filter(p -> p.size() == 2).filter(p -> (int) p.get(0) == 0)
    //                 .map(p -> p.get(1)).collect(Collectors.toList());

    //         for (var san : upns) {
    //             System.out.println("=====");
    //             var name = (GeneralName) san;
    //             System.out.println(name);
    //         }
    //         System.out.println(upns.getClass());

    //         // return ""; //upns.collect(Collectors.toList());
    //     } catch (Exception e) {
    //         // ignore
    //     }

    //     return new ArrayList<String>(0);

    }

    public static String getSAN(X509Certificate cert) {
        try {
            var sans = cert.getSubjectAlternativeNames();

            var emails = sans.stream().filter(p -> p.size() == 2).filter(p -> (int) p.get(0) == GeneralName.rfc822Name)
                    .map(p -> (String) p.get(1));

            return emails.findFirst().get();
        } catch (Exception e) {
            return null;
        }


    }

    public static Set<TrustAnchor> keyStoreToTrustAnchors(KeyStore keystore) throws KeyStoreException {
        Set<TrustAnchor> ret = new HashSet<>();
        for (String alias : Collections.list(keystore.aliases())) {
            try {
                KeyStore.Entry entry = keystore.getEntry(alias, null);
                if (entry instanceof KeyStore.TrustedCertificateEntry) {
                    Certificate c = ((KeyStore.TrustedCertificateEntry) entry).getTrustedCertificate();
                    if (c instanceof X509Certificate) {
                        c.verify(c.getPublicKey());
                        ret.add(new TrustAnchor((X509Certificate) c, null));
                    }
                }
            } catch (Exception e) {
                // ignore
            }
        }
        return ret;
    }


    // from https://github.com/graciantrivino/cas/blob/b90003f43a8ac0382d5416ceb01074feb936bf24/cas-server-support-x509/src/main/java/org/jasig/cas/adaptors/x509/authentication/principal/X509SubjectAlternativeNameUPNPrincipalResolver.java#L75
    /**
     * ObjectID for upn altName for windows smart card logon.
     */
    public static final String UPN_OBJECTID = "1.3.6.1.4.1.311.20.2.3";

    /**
     * Retrieves Subject Alternative Name UPN extension as a principal id String.
     *
     * @param certificate X.509 certificate credential.
     *
     * @return Resolved principal ID or null if no SAN UPN extension is available in provided certificate.
     *
     * @see AbstractX509PrincipalResolver#resolvePrincipalInternal(java.security.cert.X509Certificate)
     * @see java.security.cert.X509Certificate#getSubjectAlternativeNames()
     */
    protected static String resolvePrincipalInternal(final X509Certificate certificate) {
        // logger.debug("Resolving principal from Subject Alternative Name UPN for {}", certificate);
        try {
            final Collection<List<?>> subjectAltNames = certificate.getSubjectAlternativeNames();
            if (subjectAltNames != null) {
                for (final List<?> sanItem : subjectAltNames) {
                    final ASN1Sequence seq = getAltnameSequence(sanItem);
                    final String upnString = getUPNStringFromSequence(seq);
                    if (upnString != null) {
                        return upnString;
                    }
                }
            }
        } catch (final CertificateParsingException e) {
            // logger.error("Error is encountered while trying to retrieve subject alternative names collection from certificate", e);
            // logger.debug("Returning null principal id...");
            return null;
        }
        // logger.debug("Returning null principal id...");
        return null;
    }

    /**
     * Get UPN String.
     *
     * @param seq ASN1Sequence abstraction representing subject alternative name.
     * First element is the object identifier, second is the object itself.
     *
     * @return UPN string or null
     */
    private static String getUPNStringFromSequence(final ASN1Sequence seq) {
        if (seq != null) {
            // First in sequence is the object identifier, that we must check
            final ASN1ObjectIdentifier id = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(0));
            if (id != null && UPN_OBJECTID.equals(id.getId())) {
                final ASN1TaggedObject obj = (ASN1TaggedObject) seq.getObjectAt(1);
                ASN1Primitive prim = obj.getObject();
                
                // Due to bug in java cert.getSubjectAltName, it can be tagged an extra time
                if (prim instanceof ASN1TaggedObject) {
                    prim = ASN1TaggedObject.getInstance(((ASN1TaggedObject) prim)).getObject();
                }

                if (prim instanceof ASN1OctetString) {
                    return new String(((ASN1OctetString) prim).getOctets());
                } else if (prim instanceof ASN1String) {
                    return ((ASN1String) prim).getString();
                } else{
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Get alt name seq.
     *
     * @param sanItem subject alternative name value encoded as a two elements List with elem(0) representing object id and elem(1)
     * representing object (subject alternative name) itself.
     *
     * @return ASN1Sequence abstraction representing subject alternative name or null if the passed in
     * List doesn't contain at least to elements
     * as expected to be returned by implementation of {@code X509Certificate.html#getSubjectAlternativeNames}
     *
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/security/cert/X509Certificate.html#getSubjectAlternativeNames()">
     *     X509Certificate#getSubjectAlternativeNames</a>
     */
    private static ASN1Sequence getAltnameSequence(final List sanItem) {
        //Should not be the case, but still, a extra "safety" check
        if (sanItem.size() < 2) {
            // logger.error("Subject Alternative Name List does not contain at least two required elements. Returning null principal id...");
        }
        final Integer itemType = (Integer) sanItem.get(0);
        if (itemType == 0) {
            final byte[] altName = (byte[]) sanItem.get(1);
            return getAltnameSequence(altName);
        }
        return null;
    }

    /**
     * Get alt name seq.
     *
     * @param sanValue subject alternative name value encoded as byte[]
     *
     * @return ASN1Sequence abstraction representing subject alternative name
     *
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/security/cert/X509Certificate.html#getSubjectAlternativeNames()">
     *     X509Certificate#getSubjectAlternativeNames</a>
     */
    private static ASN1Sequence getAltnameSequence(final byte[] sanValue) {
        ASN1Primitive oct = null;
        try (final ByteArrayInputStream bInput = new ByteArrayInputStream(sanValue)) {
            try (final ASN1InputStream input = new ASN1InputStream(bInput)) {
                oct = input.readObject();
            } catch (final IOException e) {
                // logger.error("Error on getting Alt Name as a DERSEquence: {}", e.getMessage(), e);
            }
            return ASN1Sequence.getInstance(oct);
        } catch (final IOException e) {
            // logger.error("An error has occurred while reading the subject alternative name value", e);
        }
        return null;
    }

}
