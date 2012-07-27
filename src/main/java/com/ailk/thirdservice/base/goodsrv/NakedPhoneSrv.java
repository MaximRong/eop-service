package com.ailk.thirdservice.base.goodsrv;

import java.util.Map;

import org.phw.ibatis.engine.PDao;

/**
 * 裸终端订单落地处理类。
 * 
 * @author wanglei 2012-2-15
 */
public class NakedPhoneSrv extends AbstractGoodsSrv {

    public NakedPhoneSrv(PDao dao) {
        super(dao);
    }

    @Override
    public Map checkGoodsDtlInfo(Map inMap) {
        // TODO：校验价格等
        return null;
    }

}
