package com.ailk.base.utils.msg;

import java.io.IOException;

import com.sinovatech.ntf.client.CallBackImpl;
import com.sinovatech.ntf.model.dto.SendGroupDTO;
import com.sinovatech.ntf.model.dto.SendSingleDTO;
import com.sinovatech.ntf.util.Encryption;
import com.sinovatech.ntf.util.ValidateException;

public class MallSendByHttp {

    public static String sendNtf(String url, String xml)
            throws IOException {
        MallHttpCommandProvider httpProvider = new MallHttpCommandProvider();
        String end = httpProvider.setUrl(url).setEncoding("GBK").setPostMethod().setRequestBody(xml).send();
        return end == null ? null : end.trim();
    }

    public void sendNtf(String url, Object obj) throws ValidateException, IOException {
        MallHttpCommandProvider httpProvider = new MallHttpCommandProvider();
        String xml = null;
        if (obj instanceof SendSingleDTO) {
            SendSingleDTO singleDto = (SendSingleDTO) obj;
            xml = Encryption.EncryptionXml(singleDto.toDOM());
        } else if (obj instanceof SendGroupDTO) {
            SendGroupDTO groupDto = (SendGroupDTO) obj;
            xml = Encryption.EncryptionXml(groupDto.toDOM());
        } else {
            return;
        }
        httpProvider.setUrl(url).setEncoding("GBK").setPostMethod().setRequestBody(xml).send(new CallBackImpl());
    }
}
