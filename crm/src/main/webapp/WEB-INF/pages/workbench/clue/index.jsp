<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bs_pagination-master/css/jquery.bs_pagination.min.css" type="text/css" rel="stylesheet"/>

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
<script type="text/javascript" src="jquery/bs_pagination-master/js/jquery.bs_pagination.min.js"></script>
<script type="text/javascript" src="jquery/bs_pagination-master/localization/en.js"></script>

<script type="text/javascript">

	$(function(){
		//设置datetimepicker容器
		$("#create-nextContactTime").datetimepicker({
			language:'zh-CN',
			format:'yyyy-mm-dd',
			minView:'month',
			initData:new Date(),
			autoclose:true,
			todayBtn:true,
			clearBtn:true
		});


		queryClue(1,10);

		$("#createClueBtn").click(function () {
			//重置表单
			$("#createClueForm")[0].reset();
			//显示创建线索弹出框
			$("#createClueModal").modal("show");
		});



		$("#saveCreateClueBtn").click(function () {
			//收集参数
			var owner = $("#create-clueOwner").val();
			var company = $("#create-company").val();
			var appellation = $("#create-call").val();
			var fullname = $("#create-surname").val();
			var job = $("#create-job").val();
			var email = $("#create-email").val();
			var phone = $("#create-phone").val();
			var website = $("#create-website").val();
			var mphone = $("#create-mphone").val();
			var state = $("#create-status").val();
			var source = $("#create-source").val();
			var description = $("#create-describe").val();
			var contact_summary = $("#create-contactSummary").val();
			var next_contact_time = $("#create-nextContactTime").val();
			var address = $("#create-address").val();
			var createBy = '${sessionScope.sessionUser.id}';

			//表单验证
			if (company == "") {
				alert("公司不能为空！");
				return;
			}
			if (fullname == "") {
				alert("姓名不能为空！");
				return;
			}
			//邮箱验证
			var regExp = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
			if (!regExp.test(email) && email != "") {
				alert("邮箱格式不正确，请重新输入！");
				return;
			}
			//手机号码验证
			var regExp1 = /^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\d{8}$/;
			if (!regExp1.test(mphone) && mphone != "") {
				alert("手机号码格式不正确，请重新输入！");
				return;
			}

			//发送请求
			$.ajax({
				url:"workbench/clue/createClue.do",
				data:{
					owner:owner,
					company:company,
					appellation:appellation,
					fullname:fullname,
					job:job,
					email:email,
					phone:phone,
					website:website,
					mphone:mphone,
					state:state,
					source:source,
					description:description,
					contactSummary:contact_summary,
					nextContactTime:next_contact_time,
					address:address,
					createBy:createBy
				},
				type:'post',
				dataType: 'json',
				success:function (data) {
					if (data.code == "1") {
						//关闭模态窗口
						$("#createClueModal").modal("hide");
						//刷新线索列表
						queryClue(1,10);
					} else {
						//提示信息
						alert(data.message);
						//模态窗口不关闭
						$("#createClueModal").modal("show");
					}
				}
			});

		});


        //给“修改”按钮添加单击事件
        $("#editClueBtn").click(function () {
			//验证
			var size = $("#tBodyList input[type='checkbox']:checked").size();
			if (size == 0) {
				alert("请选择要修改的线索！");
				return;
			}else if(size > 1) {
				//选择多个项目
				alert("只能选择一个线索，请重新选择！");
				return;
			}else if(size == 1){
				//打开修改模态窗口
				$("#editClueModal").modal("show");
				$("#editClueForm")[0].reset();
				//收集参数
				var id = $("#tBodyList input[type='checkbox']:checked").prop("value");

				//发送ajax请求
				$.ajax({
					url:"workbench/clue/findDetailOfClueByCondition.do",
					data:{
						id:id
					},
					dataType:'json',
					type:'post',
					success:function (data) {
						$("#edit-id").val(id);
						$("#edit-company").val(data.company);
						$("#edit-call").val(data.appellation);
						$("#edit-surname").val(data.fullname);
						$("#edit-job").val(data.job);
						$("#edit-email").val(data.email);
						$("#edit-website").val(data.website);
						$("#edit-phone").val(data.phone);
						$("#edit-mphone").val(data.mphone);
						$("#edit-status").val(data.state);
						$("#edit-source").val(data.source);
						$("#edit-describe").val(data.description);
						$("#edit-contactSummary").val(data.contactSummary);
						$("#edit-nextContactTime").val(data.nextContactTime);
						$("#edit-address").val(data.address);
					}
				});
			}


        });


		//给“保存”按钮添加单击事件
		$("#edit-saveClueBtn").click(function () {
			//收集参数
			var id = $("#edit-id").val();
			var company = $("#edit-company").val();
			var appellation = $("#edit-call").val();
			var fullname = $("#edit-surname").val();
			var job = $("#edit-job").val();
			var email = $("#edit-email").val();
			var website = $("#edit-website").val();
			var phone = $("#edit-phone").val();
			var mphone = $("#edit-mphone").val();
			var state = $("#edit-status").val();
			var source = $("#edit-source").val();
			var description = $("#edit-describe").val();
			var contactSummary = $("#edit-contactSummary").val();
			var nextContactTime = $("#edit-nextContactTime").val();
			var address = $("#edit-address").val();
			var owner = $("#edit-clueOwner").val();

			//发送请求
			$.ajax({
				url:"workbench/clue/saveClueByCondition.do",
				data:{
					id:id,
					company:company,
					appellation:appellation,
					fullname:fullname,
					job:job,
					email:email,
					website:website,
					phone:phone,
					state:state,
					mphone:mphone,
					source:source,
					description:description,
					contactSummary:contactSummary,
					nextContactTime:nextContactTime,
					address:address,
					owner:owner
				},
				dataType:'json',
				type:'post',
				success:function (data) {
					if (data.code == "1") {
						$("#editClueModal").modal("hide");
						queryClue($("#demo_pag1").bs_pagination('getOption','currentPage'),$("#demo_pag1").bs_pagination('getOption','rowsPerPage'));
					} else {
						alert(data.message);
						$("#editClueModal").modal("show");
					}
				}
			});
		});

		//给“删除”按钮添加单击事件
		$("#deleteClueBtn").click(function () {
			//验证
			var size = $("#tBodyList input[type='checkbox']:checked").size();
			if (size == 0) {
				alert("至少选择一个线索");
				return;
			}
			var checkedBtn = $("#tBodyList input[type='checkbox']:checked");
			//收集参数
			var htmlStr = "";
			$.each(checkedBtn,function (index, obj) {
				htmlStr += "id="+obj.value+"&";
			});
			htmlStr = htmlStr.substr(0,htmlStr.length-1);

			if (window.confirm("确定删除吗？")) {
				//发送请求
				$.ajax({
					url:"workbench/clue/removeClueById.do?"+htmlStr,
					type:'post',
					dataType:"json",
					success:function (data) {
						if (data.code == "1") {
							//查询结果
							queryClue($("#demo_pag1").bs_pagination('getOption','currentPage'),$("#demo_pag1").bs_pagination('getOption','rowsPerPage'));
						} else {
							alert(data.message);
						}
					}
				});
			}


		});
		
	});

	function queryClue(pageNo,pageSize) {
		//收集参数
		var appellation = $("#appellation").val();
		var company = $("#company").val();
		var phone = $("#phone").val();
		var source = $("#source").val();
		var owner = $("#owner").val();
		var mphone = $("#mphone").val();
		var state = $("#state").val();

		//发送请求
		$.ajax({
			url:"workbench/clue/queryClueByCondition.do",
			data:{
				appellation:appellation,
				company:company,
				phone:phone,
				source:source,
				owner:owner,
				mphone:mphone,
				state:state,
				pageSize:pageSize,
				pageNo:pageNo
			},
			type:'post',
			dataType:'json',
			success:function (data) {
				if (data.code == "1") {
					var htmlStr ="";
					$.each(data.retData.clueList,function (index, clue) {
						htmlStr += "<tr>";
						htmlStr += "	<td><input type='checkbox' value='"+clue.id+"'/></td>";
						htmlStr += "	<td><a style='text-decoration: none; cursor: pointer;' onclick='window.location.href=\"workbench/clue/toClueDetail.do?id="+clue.id+"\";'>"+clue.fullname+"</a></td>";
						htmlStr += "	<td>"+clue.company+"</td>";
						htmlStr += "	<td>"+clue.phone+"</td>";
						htmlStr += "	<td>"+clue.mphone+"</td>";
						htmlStr += "	<td>"+clue.source+"</td>";
						htmlStr += "	<td>"+clue.owner+"</td>";
						htmlStr += "	<td>"+clue.state+"</td>";
						htmlStr += "</tr>";
					});
					$("#tBodyList").html(htmlStr);

				} else {
					alert(data.message)
				}


				//设置日历插件
				var totalPages = 1;
				totalPages = Math.ceil(data.retData.totalRows/pageSize);
				$("#demo_pag1").bs_pagination({
					totalPages:totalPages,
					rowsPerPage:pageSize,
					totalRows:data.retData.totalRows,

					currentPage:pageNo,
					visiblePageLinks:5,
					showGoToPage:true,
					showRowsPerPage:true,
					showRowsInfo:true,
					onChangePage:function (event,pageobj) {
						queryClue(pageobj.currentPage,pageobj.rowsPerPage);
					}
				});
			}

		});

	}

</script>
</head>
<body>

	<!-- 创建线索的模态窗口 -->
	<div class="modal fade" id="createClueModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 90%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">创建线索</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" role="form" id="createClueForm">

						<input type="hidden" id="edit-id">
						<div class="form-group">
							<label for="create-clueOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-clueOwner">
                                    <option></option>
									<c:forEach items="${userList}" var="u">
										<option value="${u.id}">${u.name}</option>
									</c:forEach>
								</select>
							</div>
							<label for="create-company" class="col-sm-2 control-label">公司<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-company">
							</div>
						</div>
						
						<div class="form-group">
							<label for="create-call" class="col-sm-2 control-label">称呼</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-call">
								  <option></option>
								  <c:forEach items="${appellationList}" var="app">
                                      <option value="${app.id}">${app.value}</option>
                                  </c:forEach>
								</select>
							</div>
							<label for="create-surname" class="col-sm-2 control-label">姓名<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-surname">
							</div>
						</div>
						
						<div class="form-group">
							<label for="create-job" class="col-sm-2 control-label">职位</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-job">
							</div>
							<label for="create-email" class="col-sm-2 control-label">邮箱</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-email">
							</div>
						</div>
						
						<div class="form-group">
							<label for="create-phone" class="col-sm-2 control-label">公司座机</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-phone">
							</div>
							<label for="create-website" class="col-sm-2 control-label">公司网站</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-website">
							</div>
						</div>
						
						<div class="form-group">
							<label for="create-mphone" class="col-sm-2 control-label">手机</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-mphone">
							</div>
							<label for="create-status" class="col-sm-2 control-label">线索状态</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-status">
								  <option></option>
								  <c:forEach items="${clueStateList}" var="cs">
                                      <option value="${cs.id}">${cs.value}</option>
                                  </c:forEach>
								</select>
							</div>
						</div>
						
						<div class="form-group">
							<label for="create-source" class="col-sm-2 control-label">线索来源</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-source">
								  <option></option>
								  <c:forEach items="${sourceList}" var="sr">
                                      <option value="${sr.id}">${sr.value}</option>
                                  </c:forEach>
								</select>
							</div>
						</div>
						

						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">线索描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-describe"></textarea>
							</div>
						</div>
						
						<div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative;"></div>
						
						<div style="position: relative;top: 15px;">
							<div class="form-group">
								<label for="create-contactSummary" class="col-sm-2 control-label">联系纪要</label>
								<div class="col-sm-10" style="width: 81%;">
									<textarea class="form-control" rows="3" id="create-contactSummary"></textarea>
								</div>
							</div>
							<div class="form-group">
								<label for="create-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
								<div class="col-sm-10 mydate" style="width: 300px;">
									<input type="text" class="form-control" id="create-nextContactTime">
								</div>
							</div>
						</div>
						
						<div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative; top : 10px;"></div>
						
						<div style="position: relative;top: 20px;">
							<div class="form-group">
                                <label for="create-address" class="col-sm-2 control-label">详细地址</label>
                                <div class="col-sm-10" style="width: 81%;">
                                    <textarea class="form-control" rows="1" id="create-address"></textarea>
                                </div>
							</div>
						</div>
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveCreateClueBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改线索的模态窗口 -->
	<div class="modal fade" id="editClueModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 90%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">修改线索</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" role="form" id="editClueForm">
					
						<div class="form-group">
							<label for="edit-clueOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-clueOwner">
									<c:forEach items="${userList}" var="u">
										<option value="${u.id}">${u.name}</option>
									</c:forEach>
								  <%--<option>zhangsan</option>
								  <option>lisi</option>
								  <option>wangwu</option>--%>
								</select>
							</div>
							<label for="edit-company" class="col-sm-2 control-label">公司<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-company" value="动力节点">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-call" class="col-sm-2 control-label">称呼</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-call">
								  <option></option>
                                    <c:forEach items="${appellationList}" var="app">
                                        <option value="${app.id}">${app.value}</option>
                                    </c:forEach>
								</select>
							</div>
							<label for="edit-surname" class="col-sm-2 control-label">姓名<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-surname" value="李四">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-job" class="col-sm-2 control-label">职位</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-job" value="CTO">
							</div>
							<label for="edit-email" class="col-sm-2 control-label">邮箱</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-email" value="lisi@bjpowernode.com">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-phone" class="col-sm-2 control-label">公司座机</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-phone" value="010-84846003">
							</div>
							<label for="edit-website" class="col-sm-2 control-label">公司网站</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-website" value="http://www.bjpowernode.com">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-mphone" class="col-sm-2 control-label">手机</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-mphone" value="12345678901">
							</div>
							<label for="edit-status" class="col-sm-2 control-label">线索状态</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-status">
								  <option></option>
                                    <c:forEach items="${clueStateList}" var="cs">
                                        <option value="${cs.id}">${cs.value}</option>
                                    </c:forEach>
								</select>
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-source" class="col-sm-2 control-label">线索来源</label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-source">
								  <option></option>
                                    <c:forEach items="${sourceList}" var="sr">
                                        <option value="${sr.id}">${sr.value}</option>
                                    </c:forEach>
								</select>
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-describe">这是一条线索的描述信息</textarea>
							</div>
						</div>
						
						<div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative;"></div>
						
						<div style="position: relative;top: 15px;">
							<div class="form-group">
								<label for="edit-contactSummary" class="col-sm-2 control-label">联系纪要</label>
								<div class="col-sm-10" style="width: 81%;">
									<textarea class="form-control" rows="3" id="edit-contactSummary">这个线索即将被转换</textarea>
								</div>
							</div>
							<div class="form-group">
								<label for="edit-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
								<div class="col-sm-10" style="width: 300px;">
									<input type="text" class="form-control" id="edit-nextContactTime" value="2017-05-01">
								</div>
							</div>
						</div>
						
						<div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative; top : 10px;"></div>

                        <div style="position: relative;top: 20px;">
                            <div class="form-group">
                                <label for="edit-address" class="col-sm-2 control-label">详细地址</label>
                                <div class="col-sm-10" style="width: 81%;">
                                    <textarea class="form-control" rows="1" id="edit-address">北京大兴区大族企业湾</textarea>
                                </div>
                            </div>
                        </div>
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="edit-saveClueBtn">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>线索列表</h3>
			</div>
		</div>
	</div>
	
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
	
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="appellation">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">公司</div>
				      <input class="form-control" type="text" id="company">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">公司座机</div>
				      <input class="form-control" type="text" id="phone">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">线索来源</div>
					  <select class="form-control" id="source">
					  	  <option></option>
                          <c:forEach items="${sourceList}" var="sr">
                              <option value="${sr.id}">${sr.value}</option>
                          </c:forEach>
					  </select>
				    </div>
				  </div>
				  
				  <br>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="owner">
				    </div>
				  </div>
				  
				  
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">手机</div>
				      <input class="form-control" type="text" id="mphone">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">线索状态</div>
					  <select class="form-control" id="state">
					  	<option></option>
                          <c:forEach items="${clueStateList}" var="cs">
                              <option value="${cs.id}">${cs.value}</option>
                          </c:forEach>
					  </select>
				    </div>
				  </div>

				  <button type="submit" class="btn btn-default">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 40px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="createClueBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editClueBtn"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteClueBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
				
			</div>
			<div style="position: relative;top: 50px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" /></td>
							<td>名称</td>
							<td>公司</td>
							<td>公司座机</td>
							<td>手机</td>
							<td>线索来源</td>
							<td>所有者</td>
							<td>线索状态</td>
						</tr>
					</thead>
					<tbody id="tBodyList">
						<%--<tr>
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.jsp';">李四先生</a></td>
							<td>动力节点</td>
							<td>010-84846003</td>
							<td>12345678901</td>
							<td>广告</td>
							<td>zhangsan</td>
							<td>已联系</td>
						</tr>
                        <tr class="active">
                            <td><input type="checkbox" /></td>
                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.jsp';">李四先生</a></td>
                            <td>动力节点</td>
                            <td>010-84846003</td>
                            <td>12345678901</td>
                            <td>广告</td>
                            <td>zhangsan</td>
                            <td>已联系</td>
                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 60px;">

				<div id="demo_pag1"></div>
				<%--<div>
					<button type="button" class="btn btn-default" style="cursor: default;">共<b>50</b>条记录</button>
				</div>
				<div class="btn-group" style="position: relative;top: -34px; left: 110px;">
					<button type="button" class="btn btn-default" style="cursor: default;">显示</button>
					<div class="btn-group">
						<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
							10
							<span class="caret"></span>
						</button>
						<ul class="dropdown-menu" role="menu">
							<li><a href="#">20</a></li>
							<li><a href="#">30</a></li>
						</ul>
					</div>
					<button type="button" class="btn btn-default" style="cursor: default;">条/页</button>
				</div>
				<div style="position: relative;top: -88px; left: 285px;">
					<nav>
						<ul class="pagination">
							<li class="disabled"><a href="#">首页</a></li>
							<li class="disabled"><a href="#">上一页</a></li>
							<li class="active"><a href="#">1</a></li>
							<li><a href="#">2</a></li>
							<li><a href="#">3</a></li>
							<li><a href="#">4</a></li>
							<li><a href="#">5</a></li>
							<li><a href="#">下一页</a></li>
							<li class="disabled"><a href="#">末页</a></li>
						</ul>
					</nav>
				</div>--%>

			</div>
			
		</div>
		
	</div>
</body>
</html>