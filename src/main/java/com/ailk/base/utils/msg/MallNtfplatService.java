package com.ailk.base.utils.msg;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.parsers.DOMParser;
import org.phw.eop.mgr.ConnManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sinovatech.ntf.client.SendNtf;
import com.sinovatech.ntf.model.dto.SendSingleDTO;
import com.sinovatech.ntf.send.NtfplatService;
import com.sinovatech.ntf.util.Encryption;
import com.sinovatech.ntf.util.Gb2Byte;
import com.sinovatech.ntf.util.ValidateException;

public class MallNtfplatService {
    private static Log log = LogFactory.getLog(NtfplatService.class);
    private static String sysAccount;
    private static String sysId;
    private static String pwd;
    private static String url;
    private static String sp;
    private static String rmsg = "000000";
    private static String singleinterfaces;
    private static String sendtype;
    private static String sendingtime;

    static {
        String envspacedir = "mallappconfig";
        Properties p = new Properties();
        final InputStream envspaceIn = ConnManager.class.getResourceAsStream("/envspace.props");
        try {
            p.load(envspaceIn);
            envspacedir = p.getProperty("envspacedir");
        }
        catch (IOException e) {
            log.error("MallNtfplatService加载envspace.props异常", e);
        }
        ResourceBundle rb = ResourceBundle.getBundle(envspacedir + "/ntfplatservice");
        sysAccount = rb.getString("sysAccount");
        pwd = rb.getString("pwd");
        sysId = rb.getString("sysId");
        sp = rb.getString("sp");
        url = rb.getString("url");
        singleinterfaces = rb.getString("singleinterfaces");
        sendtype = rb.getString("sendtype");
    }

    public static int sendSingle(String bizCode, String bizName, String taskName, String content, String target)
            throws IOException, SAXException {
        String result = Gb2Byte.Gb2Bytes(content);
        String result1 = Gb2Byte.Gb2Bytes(taskName);
        try {
            SendSingleDTO SendSingleDTO = new SendSingleDTO();
            sendingtime = new SimpleDateFormat("yyyyMMddHHmmss").format(
                    Long.valueOf(System.currentTimeMillis()));

            Document doc = ((SendSingleDTO) SendSingleDTO.setBizCode(bizCode)
                    .setSysId(sysId).setBizName(bizName).setInterfaces(
                            singleinterfaces).setSysAccount(sysAccount).setPwd(
                            pwd).setSendingtime(sendingtime).setTaskName(
                            result1).setSp(sp)).setContent(result).setTarget(
                    target).toDOM();

            String str = Encryption.EncryptionXml(doc);
            log.info(str);

            String xmlinfo = "";

            if (sendtype.equalsIgnoreCase("0")) {
                xmlinfo = SendNtf.sendNtf(null, str);
            }
            else if (sendtype.equalsIgnoreCase("1")) {
                xmlinfo = MallSendByHttp.sendNtf(url, str);
            }

            log.info(xmlinfo);
            String[] codes = code(xmlinfo);
            if (sysAccount.equalsIgnoreCase(codes[0]) &&
                    (codes[1].equalsIgnoreCase(rmsg) ||
                    codes[1]
                            .equalsIgnoreCase("000008")) &&
                    codes[2].equalsIgnoreCase(rmsg) &&
                    codes[3].equalsIgnoreCase(rmsg) &&
                    codes[4].equalsIgnoreCase(singleinterfaces) &&
                    codes[5].equalsIgnoreCase(rmsg)) {
                log.info("return 0:发送成功，状态正常");

                return 0;
            }

            log.info("return 1 :发送失败");

            return 1;
        }
        catch (ValidateException e) {
            e.printStackTrace();
        }
        catch (SAXException e2) {
            throw e2;
        }
        catch (IOException e1) {
            throw e1;
        }
        return 1;
    }

    private static String[] code(String xmlinfo)
            throws SAXException, IOException {
        String head = "<?xml version=\"1.0\" encoding=\"GBK\"?>";
        String xml = head + xmlinfo;
        DOMParser parser = new DOMParser();
        parser.parse(new InputSource(new ByteArrayInputStream(xml.getBytes())));
        Document docx = parser.getDocument();
        NodeList list = docx.getElementsByTagName("stateEncode");
        if (list.getLength() == 1) {
            Node node = list.item(0);
            String code = node.getTextContent();
            String[] codes = code.split(",");
            return codes;
        }
        return null;
    }

}
