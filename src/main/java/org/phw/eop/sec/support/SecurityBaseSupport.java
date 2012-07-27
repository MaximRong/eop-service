package org.phw.eop.sec.support;

import java.io.Serializable;

import org.phw.eop.domain.EopAppSecurityBean;

public interface SecurityBaseSupport extends Serializable {
    void setEopAppSecurityBean(EopAppSecurityBean bean);
}
