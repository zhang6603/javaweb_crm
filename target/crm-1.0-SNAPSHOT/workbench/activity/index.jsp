<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

	<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
	<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
	<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>

<script type="text/javascript">

	$(function(){

		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});
		//为创建按钮绑定单击事件,打开添加操作的模态窗口
		$("#addBtn").click(function () {

			/*

			操作模态窗口的方式
				需要操作的模态窗口的jquery对象,调用modal方法,为该方法传递参数 show:打开模态窗口  hide:关闭模态窗口

		 */


			//$("#createActivityModal").modal("show");

			//走后台,目的是为了取得用户信息列表,为所有者下拉框铺值
			$.ajax({
				url:"workbench/activity/getUserList.do",
				type:"get",
				dataType:"json",
				success:function (data) {
					/*

						data:
							[{user1},{user2},...]

					 */
					var html = "<option></option>";

					$.each(data,function (i,n) {
						html += "<option value='"+n.id+"'>"+n.name+"</option>"
					})

					$("#create-Owner").html(html);

					//将当前登录的用户,设置为下拉默认的选项
					//在JS中使用EL表达式,EL表达式一定要套用在字符串中

					var id = "${user.id}"
					$("#create-Owner").val(id)

					$("#createActivityModal").modal("show");
				}
			})




		})

		//为保存按钮绑定事件,执行添加操作
		$("#saveBtn").click(function () {

			$.ajax({
				url:"workbench/activity/save.do",
				data:{
					"owner":$.trim($("#create-Owner").val()),
					"name":$.trim($("#create-Name").val()),
					"startDate":$.trim($("#create-startDate").val()),
					"endDate":$.trim($("#create-endDate").val()),
					"cost":$.trim($("#create-cost").val()),
					"description":$.trim($("#create-description").val())
				},
				type:"post",
				dataType:"json",
				success:function (data) {
					/*
                        date:
                            {"success":true/false}
                     */
					if (data.success){
						//添加成功后
						//刷新市场活动信息表
						//清空添加操作模态窗口中的数据残留
						//$("#activityAddForm").reset();
						// 以上方法不存在,但idea给我们提示了,这是个坑,所以要用原生的js对象来处理
						//
						// jquery对象转换成为dom对象
						// 	jquery对象[下标]
						//
						// dom对象转换成为jquery对象
						// 	$(dom对象)
						$("#activityAddForm")[0].reset();
						//关闭添加操作的模态窗口
						$("#createActivityModal").modal("hide");
						pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
					} else {
						alert("添加市场活动失败")
					}
				}
			})
		})

		/*
			对于所有的关系型数据库,做前端的分页相关操作的基础组件
			就是pageNo和pageSize
			pageNo:页码
			pageSize:每页可展现的记录数

			pageList方法:就是发出ajax请求到后台,从后台取得最新的市场活动信息列表数据
						通过响应回来的数据,局部刷新市场活动信息列表

			我们都在哪些情况下,需要调用pageList方法(什么情况下需要刷新一下市场活动列表)
			(1)点击左侧菜单中的"市场活动"超链接,需要刷新市场活动列表,调用pageList方法
			(2)添加,修改,删除后,需要刷新市场活动列表,调用pageList方法
			(3)点击查询按钮的时候,需要刷新市场活动列表,调用pageList方法
			(4)点击分页组件的时候,调用pageList方法

			以上为pageList方法制定了六个入口,也就是说,在以上6个操作执行完毕之后,我们必须调用pageList方法,刷新市场活动信息列表

		 */

		//页面加载完毕之后执行pageList方法展现市场活动信息列表
		pageList(1,2);

		//为查询按钮绑定事件,触发pageList方法
		$("#searchBtn").click(function () {
			/*

				点击查询按钮的时候,我们应该将搜索框中的信息保存起来,保存到隐藏域中

			*/

			$("#hidden-name").val($.trim($("#search-name").val()));
			$("#hidden-owner").val($.trim($("#search-owner").val()));
			$("#hidden-startDate").val($.trim($("#search-startDate").val()));
			$("#hidden-endDate").val($.trim($("#search-endDate").val()));

			pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
		})

		//全选框绑定点击事件
		$("#qx").click(function () {
			$("input[name=xz]").prop("checked",this.checked);
		})


		//动态生成的元素,是不能够以普通绑定事件的形式来进行操作的
		/*

			动态生成的元素,我们要以on方法的形式来触发事件

			语法:
			$(需要绑定元素的有效外层元素).on(绑定的事件名称,需要绑定元素的jquery对象,回调函数)

		*/

		//全选框的反向绑定
		$("#activityBody").on("click",$("input[name=xz]"),function () {
			$("#qx").prop("checked",$("input[name=xz]").length==$("input[name=xz]:checked").length);
		})


		//给删除按钮绑定点击事件
		$("#deleteBtn").click(function () {
			//取出所有打勾的选项,并取出每一个活动市场的id值
			var $ids = $("input[name=xz]:checked");
			if($ids.length == 0){
				alert("请选择需要删除的项!")
			}else {
				if(confirm("亲,确定要将所选项删除吗?")){
					var param = "";
					for (var i = 0 ; i < $ids.length ; i++){
						param += "id="+$($ids[i]).val();

						if (i < $ids.length-1) {
							param += "&";
						}
					}
					//拼好参数之后开始发送ajax请求
					$.ajax({
						url:"workbench/activity/delete.do",
						data:param,
						type:"post",
						dataType:"json",
						success:function (data) {
							/*
                                data:
                                    {"success":true/false}

                            */

							if (data.success){
								pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
							} else{
								alert("删除失败");
							}
						}
					})
				}

			}



		})

		//给修改按钮绑定点击事件
		$("#editBtn").click(function () {
			//取得所选择的项
			var $xz = $("input[name=xz]:checked");
			//判断该jquery对象的长度,修改按钮只支持单项修改
			if ($xz.length == 0){
				alert("请选择需要修改的项!");
			} else if($xz.length > 1){
				alert("只能选择一项作为修改对象!")
			} else{
				//程序能到这里说明有且仅有一项被选择,即$xz数组中只有一个dom对象
				//获取所需要修改项的id,并发送ajax请求
				var id = $xz.val();

				$.ajax({
					url:"workbench/activity/edit.do",
					data:{
						"id":id
					},
					type:"get",
					dataType:"json",
					success:function (data) {
						/*
							data:
								{"uList":[{用户1},{用户2},...],"a":Activity}

						*/
						html = "<option></option>";
						$.each(data.uList,function (i,n) {
							html += "<option value='"+n.id+"'>"+n.name+"</option>"
						})
						$("#edit-owner").html(html);
						$("#edit-id").val(data.a.id);
						$("#edit-name").val(data.a.name);
						$("#edit-owner").val(data.a.owner);
						$("#edit-startDate").val(data.a.startDate);
						$("#edit-endDate").val(data.a.endDate);
						$("#edit-cost").val(data.a.cost);
						$("#edit-describe").val(data.a.description);

						$("#editActivityModal").modal("show")
					}
				})

			}
		})

		//给更新按钮绑定点击事件
		$("#updateBtn").click(function () {
			$.ajax({
				url:"workbench/activity/update.do",
				data:{
					"id":$.trim($("#edit-id").val()),
					"owner":$.trim($("#edit-owner").val()),
					"name":$.trim($("#edit-name").val()),
					"startDate":$.trim($("#edit-startDate").val()),
					"endDate":$.trim($("#edit-endDate").val()),
					"cost":$.trim($("#edit-cost").val()),
					"description":$.trim($("#edit-describe").val())
				},
				type:"post",
				dataType:"json",
				success:function (data) {
					/*
                        date:
                            {"success":true/false}
                     */
					if (data.success){
						//添加成功后
						//刷新市场活动信息表
						//清空添加操作模态窗口中的数据残留
						//$("#activityAddForm").reset();
						// 以上方法不存在,但idea给我们提示了,这是个坑,所以要用原生的js对象来处理
						//
						// jquery对象转换成为dom对象
						// 	jquery对象[下标]
						//
						// dom对象转换成为jquery对象
						// 	$(dom对象)
						//关闭添加操作的模态窗口
						$("#editActivityModal").modal("hide");
						pageList($("#activityPage").bs_pagination('getOption', 'currentPage'),
								$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
					} else {
						alert("修改市场活动失败")
					}
				}
			})
		})

	});


	//定义pageList方法,该方法一共有六个入口分别是(点击市场活动页面/点击查询/点击保存/点击更新/点击分页组件/点击删除)
	function pageList(pageNo, pageSize) {
		$("#qx").prop("checked",false);

		//查询前,将隐藏域中保存的信息取出来,重新赋予到搜索框中
		$("#search-name").val($.trim($("#hidden-name").val()));
		$("#search-owner").val($.trim($("#hidden-owner").val()));
		$("#search-startDate").val($.trim($("#hidden-startDate").val()));
		$("#search-endDate").val($.trim($("#hidden-endDate").val()));

		$.ajax({
			url:"workbench/activity/pageList.do",
			data:{
				"pageNo":pageNo,
				"pageSize":pageSize,
				"name":$.trim($("#search-name").val()),
				"owner":$.trim($("#search-owner").val()),
				"startDate":$.trim($("#search-startDate").val()),
				"endDate":$.trim($("#search-endDate").val())
			},
			type:"get",
			dataType:"json",
			success:function (data) {
				/*
					data
						我们需要的:市场活动信息列表
						[{市场活动1},{市场活动2},....] List<Activity> aList
						一会儿分页插件需要的:查询出来的总记录数
						[{"total":100}] int total

						整合一下之后得出data的格式:
						[{"total":100,"dataList":[{市场活动1},{市场活动2},...]}]
				 */

				var html = "";

				//每一个n就是每一个市场活动对象
				$.each(data.dataList,function (i,n) {

					html += '<tr class="active">';
					html += '<td><input type="checkbox" name="xz" value="'+n.id+'"/></td>';
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id='+n.id+'\';">'+n.name+'</a></td>';
					html += '<td>'+n.owner+'</td>';
					html += '<td>'+n.startDate+'</td>';
					html += '<td>'+n.endDate+'</td>';
					html += '</tr>';
				})
				$("#activityBody").html(html);

				//计算总页数
				var totalPages = data.total%pageSize==0?data.total/pageSize:parseInt(data.total/pageSize)+1;

				//数据处理完毕之后，结合分页插件，对前端展现分页信息
				$("#activityPage").bs_pagination({
					currentPage: pageNo, // 页码
					rowsPerPage: pageSize, // 每页显示的记录条数
					maxRowsPerPage: 20, // 每页最多显示的记录条数
					totalPages: totalPages, // 总页数
					totalRows: data.total, // 总记录条数

					visiblePageLinks: 3, // 显示几个卡片

					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,

					onChangePage : function(event, data){
						pageList(data.currentPage , data.rowsPerPage);
					}
				});
			}
		})


	}


	
</script>
</head>
<body>
<input type="hidden" id="hidden-name">
<input type="hidden" id="hidden-owner">
<input type="hidden" id="hidden-startDate">
<input type="hidden" id="hidden-endDate">

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form id="activityAddForm" class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-Owner">

								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-Name">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startDate" >
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endDate" >
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">
						<input type="hidden" id="edit-id">
					
						<div class="form-group">
							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-owner">
<%--								  <option>zhangsan</option>--%>
<%--								  <option>lisi</option>--%>
<%--								  <option>wangwu</option>--%>
								</select>
							</div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-name" >
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-startDate" >
							</div>
							<label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-endDate" >
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost" >
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-describe"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updateBtn">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
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
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="search-startDate" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="search-endDate">
				    </div>
				  </div>
				  
				  <button type="button" class="btn btn-default" id="searchBtn">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx"/></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityBody">
<%--						<tr class="active">--%>
<%--							<td><input type="checkbox" /></td>--%>
<%--							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.jsp';">发传单</a></td>--%>
<%--                            <td>zhangsan</td>--%>
<%--							<td>2020-10-10</td>--%>
<%--							<td>2020-10-20</td>--%>
<%--						</tr>--%>
<%--                        <tr class="active">--%>
<%--                            <td><input type="checkbox" /></td>--%>
<%--                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.jsp';">发传单</a></td>--%>
<%--                            <td>zhangsan</td>--%>
<%--                            <td>2020-10-10</td>--%>
<%--                            <td>2020-10-20</td>--%>
<%--                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">
				<div id="activityPage"></div>
			</div>
			
		</div>
		
	</div>
</body>
</html>