package dk.liga.mobileid.backendapi.hibernate;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

public class X509CertificateJavaDescriptor extends AbstractTypeDescriptor<X509Certificate> {

    public static final X509CertificateJavaDescriptor INSTANCE = new X509CertificateJavaDescriptor();

    public X509CertificateJavaDescriptor() {
        super(X509Certificate.class, ImmutableMutabilityPlan.INSTANCE);
    }

    @Override
    public X509Certificate fromString(String string) {
        // TODO Auto-generated method stub
        return null;

    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(X509Certificate value, Class<X> type, WrapperOptions options) {
        if (value == null)
            return null;

        if (String.class.isAssignableFrom(type)) {
            try {
                var encodedString = Base64.getEncoder().encodeToString(value.getEncoded());
                return (X) encodedString;
            } catch (Exception e) {

            }

        }

        throw unknownUnwrap(type);
    }

    @Override
    public <X> X509Certificate wrap(X value, WrapperOptions options) {
        if (value == null)
            return null;

        if (String.class.isInstance(value)) {
            try {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                var bytes = Base64.getDecoder().decode((String) value);
                var is = new ByteArrayInputStream(bytes);
                var cert = (X509Certificate) cf.generateCertificate(is);
                return cert;
            } catch (CertificateException e) {

            }
        }

        throw unknownWrap(value.getClass());
    }
}
