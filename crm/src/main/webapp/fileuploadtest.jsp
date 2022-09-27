<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<html>
<head>
    <base href="<%=basePath%>">
    <title>演示文件上传</title>
</head>
<body>
<%--表单--%>
<%--
    文件上传的表单必须满足三个条件
1）表单组件标签只能用:<input type="file">
                  <input type="text|password|radio|checkbox|hidden|button|submit|reset|file">
                  <select>,<textarea>
2)请求方式只能用: post
    get：参数通过请求头提交到后台，只能向后台提交文本数据，对参数长度有限制，数据不安全，效率高（能够使用浏览器缓存）
    post：参数通过请求体提交到后台，技能提交文件数据，又能够提交二进制数据，理论上对参数长度没有限制，相对安全，效率相对较低
3）表单的编码格式只能用:multipart/form-data
    根据http协议的规定，浏览器每次向后台提交参数，都会对参数进行统一编码；默认采用的编码格式是:urlencoded，这种编码格式只能对文本数据（字符串，如果是int等数据都会统一转化为字符串）进行编码，
    浏览器每次向后台提交参数，，都会首先把所有的参数转化为字符串，然后对这些数据统一进行urlcoded编码（所以我们在前台的数据，无论是什么格式，发往后台都是字符串）
--%>
<form action="workbench/activity/fileUpload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="myFile"><br>
    <input type="text" name="userName"><br>
    <input type="submit" value="提交">
</form>
</body>
</html>
