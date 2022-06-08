package com.project;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author quangvn
 */
public class AppMain {
    public static void main(String[] args) {
        try {
            Gson gson = new Gson();
            Type typeMap = new TypeToken<Map<String, String>>() {
            }.getType();

            String req = "{\"access_code\":\"WUHCQYUCBA\",\"address1\":\"Khuong Trung, Thanh Xuan\",\"amount\":\"100000\",\"cancel_url\":\"http://sb.ite.com.vn/sand-box/create-order\",\"city\":\"Ha Noi\",\"country\":\"Việt Nam\",\"currency\":\"vnd\",\"device\":\"os={name=Windows, version=windows-10}, browser={name=Chrome, version=102.0.5005.63}, location={long=0, mailto:lat=0}\",\"email\":\"trangggtranggg@gmail.com\",\"first_name\":\"Trang\",\"ip_address\":\"192.168.1.1\",\"issuer_code\":\"\",\"last_name\":\"Nguyen\",\"mac\":\"D85705CD2406AB3D03FFF8294CB19CF317DCA4EE50A7D7471ED81ABDD94C7987\",\"mac_type\":\"MD5\",\"merchant_id\":\"102141\",\"order_info\":\"thanh toán thử\",\"order_reference\":\"2022060820164617157\",\"pay_type\":\"pay\",\"phone\":\"0363057929\",\"return_url\":\"http://sb.ite.com.vn/sand-box/create-order\",\"token\":\"\",\"user_id\":\"\"}";
            String secretKey = "94d2a4b59deb34cd0fd7b84f0e90f313";
            Map mapData = gson.fromJson(req, typeMap);
            String hashData = createHashData(mapData, secretKey);
            String mac = hmacSha256(hashData, secretKey).toUpperCase();

            System.out.println("mac: " + mac);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String createHashData(Map fields, String secret) throws UnsupportedEncodingException {
        fields.remove("mac_type");
        fields.remove("mac");
        List fieldNames = new ArrayList(fields.keySet());
        Iterator iterator = fieldNames.iterator();
        while (iterator.hasNext()) {
            String fieldName = String.valueOf(iterator.next());
            String fieldValue = String.valueOf(fields.get(fieldName));
            if ((isNullOrEmpty(fieldValue) || fieldValue.equals("null"))) {
                iterator.remove();
            }
        }
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        sb.append(secret);
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = String.valueOf(itr.next());
            String fieldValue = String.valueOf(fields.get(fieldName));
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(fieldName);
                sb.append("=");
                sb.append(URLDecoder.decode(fieldValue.replaceAll("\\+", "%2b"), "UTF-8"));
                if (itr.hasNext()) {
                    sb.append("&");
                }
            }
        }
        System.out.println("dataVerify=" + sb);
        return sb.toString();
    }

    public static String hmacSha256(String data, String key) throws Exception {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        Mac mac = Mac.getInstance("HmacSHA256");

        mac.init(signingKey);

        byte[] digest = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        return bytesToHex(digest);
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }
}
