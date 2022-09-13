package dk.liga.mobileid.backendapi.hibernate;

import java.security.cert.X509Certificate;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class X509Type extends AbstractSingleColumnStandardBasicType<X509Certificate> {

    public static final X509Type INSTANCE = new X509Type();

    public X509Type() {
        super(VarcharTypeDescriptor.INSTANCE, X509CertificateJavaDescriptor.INSTANCE);
    }


    @Override
    public String getName() {
        return "X509Type";
    }

}	
