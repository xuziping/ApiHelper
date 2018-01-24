/**
 * @api {${APIMETHOD}} /${APIGROUP}/${PATH} ${LABELNAME}
 * @apiVersion 1.0.0
 * @apiName ${APINAME}
 * @apiGroup ${APIGROUP}
 * @apiExample Example
 *   http://ip:port/${APIGROUP}/${PATH}
<#if DESC!=''>
 * @apiDescription <b>使用说明：</b>${DESC}
</#if>
<#if PARAM_LIST??>
 <#list PARAM_LIST as param>
 * @apiParam ${param.type} ${param.name} ${param.desc}
 </#list>
</#if>
<#if REQUEST_JSON??>
 * @apiParamExample {json} 接口请求入参示例
${REQUEST_JSON}
</#if>
 * @apiSuccess {String} success true:成功, false:失败
 * @apiSuccess {String} msg 返回信息
 * @apiSuccessExample {json} 接口数据响应示例
${RESPONSE_JSON}
 */