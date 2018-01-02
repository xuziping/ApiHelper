package com.xuzp.apihelper.template.help;

import com.google.common.collect.Maps;
import com.xuzp.apihelper.utils.Constants;
import com.xuzp.apihelper.template.core.TemplateProvider;

/**
 * @author za-xuzhiping
 * @Date 2017/12/7
 * @Time 17:31
 */

public class Help {

    public static String message() {
        return TemplateProvider.loadTemplate(Constants.HELP_FTL, Maps.newHashMap());
    }
}
