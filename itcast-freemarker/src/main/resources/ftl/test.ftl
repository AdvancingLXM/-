<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Freemarker测试</title>
</head>
<body>
${name} --- ${msg}
<br>
<hr>
<br>
<#-- assign 定义变量-->
assign 定义变量<br>
<#assign str="传智播客">
${str}

<br>
<#assign info={"mobile":"13400000000", "address":"吉山村"} >
${info.mobile} ---- ${info.address}

<br>
<hr>
<br>
include包含其他模版文件<br>
<#include "header.ftl">

<br>
<hr>
<br>
条件控制语句if：<br>
<#assign bool=true>
<#if bool>
    bool的值为true
<#else>
    bool的值为false
</#if>

<br>
<hr>
<br>
循环控制语句list：<br>
<#list goodsList as goods>
    ${goods_index}---${goods.name}---${goods.price}<br>
</#list>
<br>
共有${goodsList?size}条记录。
<br>
<hr>
<br>
将json格式字符串转化为json对象<br>
<#assign jsonStr="{'id':123,'name':'itcast'}">
<#assign jsonObj=jsonStr?eval>
${jsonObj.id} --- ${jsonObj.name}
<br>
<hr>
<br>
格式化输出日期：<br>
输出日期：${today?date}<br>
输出时间：${today?time}<br>
输出日期时间：${today?datetime}<br>
格式化输出特定格式日期字符串：${today?string("yyyy年MM月dd日 HH:mm:ss")}<br>
<br>
<hr>
<br>
数值输出：${number} --- 数值转换为字符串输出:${number?c}
<br>
<hr>
<br>
空值处理：<br>
如果值为空则可以在变量之后使用!；表示如果值为空则什么都不显示：${emp!}<br>
如果值为空则可以在变量之后使用!""；表示如果值为空则显示双引号的内容：${emp!"emp的值为空。。。"}

<br>
??? 前面两个??判断变量是否存在；如果存在则返回true，否则返回false；最后一个?表示函数的调用<br>

<#assign bool2=false>
${bool2???string}

<br>

<#if aaa??>
    aaa存在
<#else>
    aaa不存在
</#if>


<br>
<br>
<br>
<br>
<br>
</body>
</html>