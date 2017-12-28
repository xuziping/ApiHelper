## ${LABELNAME} ##

##### 请求方式: ${APIMETHOD}
##### URL:  http://ip:port/${APIGROUP}/${PATH}

<#if DESC??>
#####  使用说明
> ${DESC}
</#if>

<#if PARAM_LIST??>
#####   请求参数
| 参数名      | 类型 | 说明|
| :-------- | :--------| :--: |
${PARAM_LIST}
</#if>

<#if REQUEST_JSON??>
#####  接口请求入参示例:
${REQUEST_JSON}
</#if>

<#if RESPONSE_JSON??>
##### 接口数据响应示例：
${RESPONSE_JSON}
</#if>

----------