package org.phw.eop.sec.support;

public interface SecurityCipherSupport extends SecurityBaseSupport {
    String encrypt(String value);

    String decrypt(String value);
}
