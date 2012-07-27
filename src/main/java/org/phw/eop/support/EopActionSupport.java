package org.phw.eop.support;

import java.util.Map;

public interface EopActionSupport {
    Object doAction(Map<String, Object> params) throws EopActionException;
}
