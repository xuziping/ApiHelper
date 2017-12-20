package com.xuzp.apihelper.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * @author XuZiPing
 * @Date 2017/12/19
 * @Time 23:18
 */
@Slf4j
public class UrlHelper {

    private URL url;

    public UrlHelper(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            log.error("解析URL出错，url={}", url);
            try {
                this.url = new URL(Constants.DEFAULT_REQUEST_URL);
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        }
    }

    public String getPort() {
        if (80 == url.getPort() || -1 == url.getPort()) {
            return null;
        }
        return "" + url.getPort();
    }

    public String getProtocol() {
        return url.getProtocol();
    }

    public String getHost() {
        String host = url.getHost();
        if (host.indexOf(Constants.DOT) == -1) {
            return "127.0.0.1";
        }
        return url.getHost();
    }

    public List<String> getHostSplit() {
        String[] str = getHost().split("\\.");
        return Arrays.asList(str);
    }
}
