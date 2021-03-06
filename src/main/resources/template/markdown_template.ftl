## ${LABELNAME} ##

##### 请求方式: ${APIMETHOD}
##### URL:  http://ip:port/${APIGROUP}/${PATH}

<#if DESC??>
#####  使用说明
> ${DESC}
</#if>

<#if PARAM_LIST??>
#####   请求参数
| 参数名      | 类型 | 说明| 是否可选 |
| :-------- | :--------| :-- | :--: |
    <#list PARAM_LIST as param>
     ${param.name} | ${param.type} | ${param.desc} | ${param.isOptional?string("可选","**必选**")}
    </#list>
</#if>

<#if REQUEST_JSON??>
#####  接口请求入参示例：
${REQUEST_JSON}
</#if>

<#if RESPONSE_JSON??>
##### 接口数据响应示例：
${RESPONSE_JSON}
</#if>

----------