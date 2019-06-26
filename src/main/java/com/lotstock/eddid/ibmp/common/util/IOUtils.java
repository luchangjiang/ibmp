package com.lotstock.eddid.ibmp.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {

    private static final Logger log = LoggerFactory.getLogger(IOUtils.class);

    /**
     * 关闭流
     *
     * @param c 流对象
     */
    public static void close(Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (IOException e) {
            log.error("", e);
            c = null;
        }
    }

    /**
     * 批量关闭流
     *
     * @param cs 流对象
     */
    public static void closeBatch(Closeable... cs) {
        if (cs != null && cs.length > 0) {
            for (Closeable c : cs) {
                close(c);
            }
        }
    }

    /**
     * 读取输入流到字符串
     *
     * @param is 输入流
     * @return 字符串
     */
    public static String readToString(InputStream is) throws IOException {
        try {
            StringBuffer sb = new StringBuffer();
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = is.read(b)) != -1) {
                sb.append(new String(b, 0, len));
            }
            return sb.toString();
        } finally {
            close(is);
        }
    }


}
