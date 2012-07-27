package com.ailk.thirdservice.base.goodsrv;

import org.phw.ibatis.engine.PDao;

import com.ailk.mall.base.utils.EopConstantUtils.GoodsTmpl;

/**
 * 生产订单落地处理类的工厂。
 * 
 * @author wanglei 2012-2-15
 */
public class GoodsSrvFactory {

    public static AbstractGoodsSrv createGoodsSrv(String tmplId, PDao dao) {
        AbstractGoodsSrv goodsSrv = null;
        if (GoodsTmpl.GOODSTMPL_POSTPAID_CONTRACT_PACKAGE.equals(tmplId)) {
            goodsSrv = new PostContractSrv(dao);
        } else if (GoodsTmpl.GOODSTMPL_POSTPAID_NUMBER_PACKAGE.equals(tmplId)) {
            goodsSrv = new PostNumSrv(dao);
        } else if (GoodsTmpl.GOODSTMPL_MOBILE.equals(tmplId)) {
            goodsSrv = new NakedPhoneSrv(dao);
        }
        return goodsSrv;

    }
}
