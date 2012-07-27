package com.ailk.ess.g3ess.ejb;

import com.ailk.ess.MallEJBRemote;

public class MallCall extends EjbCallAgent<MallEJBRemote> {

    public MallCall(String provinceCode) {
        super(provinceCode);
    }

    @Override
    protected Class<MallEJBRemote> getClazz() {
        return MallEJBRemote.class;
    }

}
