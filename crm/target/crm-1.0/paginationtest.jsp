<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<html>
<head>
    <base href="<%=basePath%>">
    <title>演示bs_pagination插件的使用</title>

    <!--  JQUERY -->
    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>

    <!--  BOOTSTRAP -->
    <link rel="stylesheet" type="text/css" href="jquery/bootstrap_3.3.0/css/bootstrap.min.css">
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>

    <!--  PAGINATION plugin -->
    <link rel="stylesheet" type="text/css" href="jquery/bs_pagination-master/css/jquery.bs_pagination.min.css">
    <script type="text/javascript" src="jquery/bs_pagination-master/js/jquery.bs_pagination.min.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination-master/localization/en.js"></script>

    <script type="text/javascript">
        $(function (){
            $("#demo_pag1").bs_pagination({
                totalPages:100,  //总页数,必填参数，且需要根据总条数与每页条数来计算
                rowsPerPage:10, //每页显示的条数，默认10，相当于pageSize
                totalRows:1000, //总条数，上面这三个参数之间有关系，不能乱写，totalRows = totalPages * rowsPerPage

                currentPage:1,  //当前页号，相当于pageNo
                visiblePageLinks: 10,    //最多可以显示的卡片数
                showGoToPage: true,      //控制是否显示“跳转到”部分，默认是显示
                showRowsPerPage: true,  //是否显示“每页显示条数”，默认是TRUE
                showRowsInfo: true,     //是否显示记录的信息，默认是TRUE

                //每次切换页号都会触发此函数
                //每次返回切换页号之后的pageNo和pageSize
                onChangePage:function (event,pageObj){
                    //pageObj携带了上述demo_pag1的所有信息
                    console.log(pageObj.currentPage)

                }
            })
        });
    </script>
</head>
<body>
<div id="demo_pag1"></div>
</body>
</html>
