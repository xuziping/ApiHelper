package com.xuzp.apihelper.template.help;

import com.xuzp.apihelper.utils.Constants;

/**
 * @author za-xuzhiping
 * @Date 2017/12/7
 * @Time 17:31
 */

public class Help {

    public static String message() {
        StringBuffer sb = new StringBuffer(Constants.LF);
        sb.append("##############   使用帮助：  ################").append(Constants.LF);
        sb.append("1 在运行本程序前，建议先给方法和字段名添加必要的注释").append(Constants.LF);
        sb.append("2 在运行本程序前，请先在程序同级目录下的apiHelper.properties中设置相应值，具体设置请参见README.md").append(Constants.LF);
        sb.append("3 请运行程序 java -jar MainGenerator.jar，目前支持生成ApiDoc, Postman, Markdown格式文件").append(Constants.LF);
        sb.append("#############################################").append(Constants.LF);
        return sb.toString();
    }

}
