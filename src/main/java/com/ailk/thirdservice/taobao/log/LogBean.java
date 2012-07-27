package com.ailk.thirdservice.taobao.log;

import java.io.Serializable;

import com.ailk.thirdservice.taobao.utils.TaobaoConst;

public class LogBean implements Serializable {

    private static final long serialVersionUID = 1136477211473194630L;
    public static String[] ORDERFAIL = { "00", "订单-订单入库失败，存储过程异常" };
    public static String[] ORDERSUCC = { "01", "订单-成功" };
    public static String[] ORDERINVALIDTID = { "02", "订单-无效，已获取到该订单" };
    public static String[] ORDERINVALIDSTATUE = { "02", "订单-无效，订单状态不是退款或是已支付" };
    public static String[] ORDERRETFAIL = { "03", "订单-特殊，获取到退款订单，入库失败" };
    public static String[] ORDERRETSUCC = { "04", "订单-特殊，获取到退款订单，入库正常" };
    public static String[] ORDERCHGFAIL = { "05", "订单-特殊，不能转换省份或是地市编码" };
    public static String[] ORDERSQLFAIL = { "07", "订单-java程序异常，可能数据库访问失败" };
    public static String[] ORDERNOTWT = { "08", "订单-非网厅订单" };

    public static String[] RETFAIL = { "00", "退款-订单入库失败，存储过程异常" };
    public static String[] RETFSUCC = { "01", "退款-成功" };
    public static String[] RETINVALID = { "02", "退款-无效，已获取到该订单" };
    public static String[] RETORDERFAIL = { "03", "退款-特殊，没有订单信息，订单信息入库失败" };
    public static String[] RETORDERSUCC = { "04", "退款-特殊，没有订单信息，订单信息入库正常" };
    public static String[] RETCHGFAIL = { "05", "退款-特殊，没有订单信息，不能转换省份或是地市编码" };
    public static String[] RETSQLFAIL = { "07", "退款-java程序异常，可能数据库访问失败" };

    public static String[] SENDFAIL = { "00", "物流-发送物流信息失败" };
    public static String[] SENDSUCC = { "01", "物流-发送物流信息成功，更新成功" };
    public static String[] SENDPROCFAIL = { "06", "物流-发送物流信息成功，更新失败" };
    public static String[] SENDRETFAIL = { "07", "物流-发送物流信息成功，淘宝更新物流信息失败" };

    public static String[] MODSENDFAIL = { "00", "更新物流地址-更新物流地址失败" };
    public static String[] MODSENDSUCC = { "01", "更新物流地址-更新物流地址成功，更新成功" };
    public static String[] MODSENDSQLFAIL = { "07", "更新物流地址-java程序异常，可能数据库访问失败" };

    public static String[] SPEC = { "99", "特殊-请查询TF_B_TAOBAO_SRC_SUBORDER获取详细信息" };
    public static String[] SPECFAIL = { "98", "特殊-订单明确失败" };

    private String tid = ""; // 淘宝订单号
    private String order_status = ""; // 订单获取时候的状态
    private String status = ""; // 状态：00：失败    01：成功     02：失效
    private String desc = ""; // 描述

    public void setParam(String type, String res_status) {
        res_status = res_status == null ? "" : res_status;
        if (TaobaoConst.GETORDER.equals(type)) {
            if (res_status.startsWith(TaobaoConst.PROCFAIL)) {
                status = ORDERFAIL[0];
                desc = ORDERFAIL[1] + res_status.substring(res_status.indexOf(":"));
            }
            else if (TaobaoConst.PROCSUCC.equals(res_status)) {
                status = ORDERSUCC[0];
                desc = ORDERSUCC[1];
            }
            else if (res_status.startsWith(TaobaoConst.PROCSPECFAIL)) {
                status = ORDERRETFAIL[0];
                desc = ORDERRETFAIL[1] + res_status.substring(res_status.indexOf(":"));
            }
            else if (TaobaoConst.PROCSPECSUCC.equals(res_status)) {
                status = ORDERRETSUCC[0];
                desc = ORDERRETSUCC[1];
            }
            else if (TaobaoConst.ORDERINVALID.equals(res_status)) {
                status = ORDERINVALIDTID[0];
                desc = ORDERINVALIDTID[1];
            }
            else if (TaobaoConst.ORDERINVALIDSTATUE.equals(res_status)) {
                status = ORDERINVALIDSTATUE[0];
                desc = ORDERINVALIDSTATUE[1];
            }
            else if (TaobaoConst.ORDERCHGFAIL.equals(res_status)) {
                status = ORDERCHGFAIL[0];
                desc = ORDERCHGFAIL[1];
            }
            /*2012年04月19日10：42：22 添加特殊情况，非网厅订单 begin*/
            if (TaobaoConst.SPEC.equals(res_status)) {
                status = SPEC[0];
                desc = SPEC[1];
            }
            if (res_status.startsWith(TaobaoConst.SPECFAIL)) {
                status = SPECFAIL[0];
                desc = SPECFAIL[1] + res_status.substring(res_status.indexOf(":"));
            }
            if (TaobaoConst.SQLFAIL.equals(res_status)) {
                status = ORDERSQLFAIL[0];
                desc = ORDERSQLFAIL[1];
            }
            if (TaobaoConst.ORDERNOTWT.equals(res_status)) {
                status = ORDERNOTWT[0];
                desc = ORDERNOTWT[1];
            }

            /*2012年04月19日10：42：22 添加特殊情况，非网厅订单 end*/
        }
        else if (TaobaoConst.RETORDER.equals(type)) {
            if (res_status.startsWith(TaobaoConst.PROCFAIL)) {
                status = RETFAIL[0];
                desc = RETFAIL[1] + res_status.substring(res_status.indexOf(":"));
            }
            else if (TaobaoConst.PROCSUCC.equals(res_status)) {
                status = RETFSUCC[0];
                desc = RETFSUCC[1];
            }
            else if (res_status.startsWith(TaobaoConst.PROCSPECFAIL)) {
                status = RETORDERFAIL[0];
                desc = RETORDERFAIL[1] + res_status.substring(res_status.indexOf(":"));
            }
            else if (TaobaoConst.PROCSPECSUCC.equals(res_status)) {
                status = RETORDERSUCC[0];
                desc = RETORDERSUCC[1];
            }
            else if (TaobaoConst.ORDERINVALID.equals(res_status)) {
                status = RETINVALID[0];
                desc = RETINVALID[1];
            }
            else if (TaobaoConst.ORDERCHGFAIL.equals(res_status)) {
                status = RETCHGFAIL[0];
                desc = RETCHGFAIL[1];
            }
            /*2012年04月23日15：42：57 添加特殊情况，非网厅订单 begin*/
            if (TaobaoConst.SPEC.equals(res_status)) {
                status = SPEC[0];
                desc = SPEC[1];
            }
            if (res_status.startsWith(TaobaoConst.SPECFAIL)) {
                status = SPECFAIL[0];
                desc = SPECFAIL[1] + res_status.substring(res_status.indexOf(":"));
            }
            if (TaobaoConst.SQLFAIL.equals(res_status)) {
                status = RETSQLFAIL[0];
                desc = RETSQLFAIL[1];
            }
            /*2012年04月23日15：42：57 添加特殊情况，非网厅订单 end*/
        }
        else if (TaobaoConst.SEND.equals(type)) {
            if (TaobaoConst.SENDSUCC.equals(res_status)) {
                status = SENDSUCC[0];
                desc = SENDSUCC[1];
            }
            else if (TaobaoConst.SENDPROCFAIL.equals(res_status)) {
                status = SENDPROCFAIL[0];
                desc = SENDPROCFAIL[1];
            }
            else if (TaobaoConst.SENDFAIL.equals(res_status)) {
                status = SENDFAIL[0];
                desc = SENDFAIL[1];
            }
            else if (TaobaoConst.SENDRETFAIL.equals(res_status)) {
                status = SENDRETFAIL[0];
                desc = SENDRETFAIL[1];
            }
        }
        else if (TaobaoConst.MODSEND.equals(type)) {
            if (TaobaoConst.PROCSUCC.equals(res_status)) {
                status = MODSENDSUCC[0];
                desc = MODSENDSUCC[1];
            }
            else if (res_status.startsWith(TaobaoConst.PROCFAIL)) {
                status = MODSENDFAIL[0];
                desc = MODSENDFAIL[1] + res_status.substring(res_status.indexOf(":"));
            }
            else if (TaobaoConst.SQLFAIL.equals(type)) {
                status = MODSENDSQLFAIL[0];
                desc = MODSENDSQLFAIL[1];
            }
        }
        try {
            res_status = res_status == null ? "" : res_status.substring(0, 100);
        }
        catch (Exception e) {
            // System.out.println("111");
        }
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String orderStatus) {
        order_status = orderStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
