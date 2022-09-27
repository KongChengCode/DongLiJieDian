<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<html>
<head>
    <base href="<%=basePath%>">
    <title>文件下载示范</title>
    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript">
        $(function () {
            $("#fileDownloadBtn").click(function () {
                window.location.href = "test/fileDownload.do";
            });
        });

    </script>
</head>
<body>
<input type="button" id="fileDownloadBtn" value=" 下载 ">
</body>
</html>
