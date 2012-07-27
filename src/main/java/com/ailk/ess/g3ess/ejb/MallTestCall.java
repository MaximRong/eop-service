package com.ailk.ess.g3ess.ejb;

import com.ailk.ess.MallEJBTestRemote;

public class MallTestCall extends EjbCallAgent<MallEJBTestRemote> {

    public MallTestCall(String provinceCode) {
        super(provinceCode);
    }

    @Override
    protected Class<MallEJBTestRemote> getClazz() {
        return MallEJBTestRemote.class;
    }

}
