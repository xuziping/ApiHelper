package com.xuzp.apidoc.template.help;

import com.xuzp.apidoc.utils.Constants;

/**
 * @author za-xuzhiping
 * @Date 2017/12/7
 * @Time 17:31
 */

public class Help {

    public static String message() {
        StringBuffer sb = new StringBuffer(Constants.LF);
        sb.append("##############   使用帮助：  ################").append(Constants.LF);
        sb.append("1 在运行本程序前，建议先给方法和字段名添加必要的注释，当然如果没有注释，并不会影响本程序的运行").append(Constants.LF);
        sb.append("2 然后请在程序同级目录下的autoApiDoc.properties中设置相应值，具体请参见配置文件的注释").append(Constants.LF);
        sb.append("3 请运行程序 java -jar ApiDocGenerator.jar").append(Constants.LF);
        sb.append("#############################################").append(Constants.LF);
        return sb.toString();
    }

}
