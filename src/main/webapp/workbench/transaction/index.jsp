<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

		pageList(1,2);
		
		$("#searchBtn").click(function () {
			$("#hidden-owner").val($("#search-owner").val());
			$("#hidden-name").val($("#search-name").val());
			$("#hidden-customerName").val($("#search-customerName").val());
			$("#hidden-stage").val($("#search-stage").val());
			$("#hidden-type").val($("#search-type").val());
			$("#hidden-source").val($("#search-source").val());
			$("#hidden-contactsName").val($("#search-contactsName").val());
			pageList(1,
					$("#transactionPage").bs_pagination('getOption', 'rowsPerPage'));
		})

		$("#qx").click(function () {
			$("input[name=xz]").prop("checked",this.checked);
		})

		$("#transactionBody").on("click",$("input[name=xz]"),function () {
			$("#qx").prop("checked",$("input[name=xz]").length==$("input[name=xz]:checked").length);
		})
	});

	function pageList(pageNo,pageSize){
		$("#search-owner").val($("#hidden-owner").val());
		$("#search-name").val($("#hidden-name").val());
		$("#search-customerName").val($("#hidden-customerName").val());
		$("#search-stage").val($("#hidden-stage").val());
		$("#search-type").val($("#hidden-type").val());
		$("#search-source").val($("#hidden-source").val());
		$("#search-contactsName").val($("#hidden-contactsName").val());

		$.ajax({
			url: "workbench/transaction/pageList.do",
			data: {
				"owner": $.trim($("#search-owner").val()),
				"name": $.trim($("#search-name").val()),
				"customerName": $.trim($("#search-customerName").val()),
				"stage": $.trim($("#search-stage").val()),
				"transactionType": $.trim($("#search-transactionType").val()),
				"source": $.trim($("#search-source").val()),
				"contactsName": $.trim($("#search-contactsName").val()),
				"pageNo": pageNo,
				"pageSize": pageSize
			},
			type: "get",
			dataType: "json",
			success: function (data) {
				/*
					data:
						{"total":total,"tList":[{????????????1},{????????????2},....]}
				 */
				var html = "";
				$.each(data.dataList, function (i, n) {
					html += '<tr class="active">';
					html += '<td><input type="checkbox" name=xz value="'+n.id+'"/></td>';
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/transaction/detail.do?id='+n.id+'\';">' + n.name + '</a></td>';
					html += '<td>' + n.customerId + '</td>';
					html += '<td>' + n.stage + '</td>';
					html += '<td>' + n.type + '</td>';
					html += '<td>' + n.owner + '</td>';
					html += '<td>' + n.source + '</td>';
					html += '<td>' + n.contactsId + '</td>';
					html += '</tr>';
				})
				$("#transactionBody").html(html);
				//???????????????
				var totalPages = data.total % pageSize == 0 ? data.total / pageSize : parseInt(data.total / pageSize) + 1;

				//???????????????????????????????????????????????????????????????????????????
				$("#transactionPage").bs_pagination({
					currentPage: pageNo, // ??????
					rowsPerPage: pageSize, // ???????????????????????????
					maxRowsPerPage: 20, // ?????????????????????????????????
					totalPages: totalPages, // ?????????
					totalRows: data.total, // ???????????????

					visiblePageLinks: 3, // ??????????????????

					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,

					onChangePage: function (event, data) {
						pageList(data.currentPage, data.rowsPerPage);
					}
				})
			}

		})
	}
	
</script>
</head>
<body>
<input type="hidden" id="hidden-owner">
<input type="hidden" id="hidden-name">
<input type="hidden" id="hidden-customerName">
<input type="hidden" id="hidden-stage">
<input type="hidden" id="hidden-type">
<input type="hidden" id="hidden-source">
<input type="hidden" id="hidden-contactsName">

	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>????????????</h3>
			</div>
		</div>
	</div>
	
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
	
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">?????????</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">??????</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">????????????</div>
				      <input class="form-control" type="text" id="search-customerName">
				    </div>
				  </div>
				  
				  <br>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">??????</div>
					  <select class="form-control" id="search-stage">
					  	<option></option>
					  	<c:forEach items="${stage}" var="s">
							<option value="${s.value}">${s.text}</option>
						</c:forEach>
					  </select>
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">??????</div>
					  <select class="form-control" id="search-transactionType">
					  	<option></option>
						  <c:forEach items="${transactionType}" var="t">
							  <option value="${t.value}">${t.text}</option>
						  </c:forEach>
					  </select>
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">??????</div>
				      <select class="form-control" id="search-source">
						  <option></option>
						  <c:forEach items="${source}" var="s">
							  <option value="${s.value}">${s.text}</option>
						  </c:forEach>
						</select>
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">???????????????</div>
				      <input class="form-control" type="text" id="search-contactsName">
				    </div>
				  </div>
				  
				  <button type="button" class="btn btn-default" id="searchBtn">??????</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 10px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" onclick="window.location.href='workbench/transaction/add.do';"><span class="glyphicon glyphicon-plus"></span> ??????</button>
				  <button type="button" class="btn btn-default" onclick="window.location.href='edit.html';"><span class="glyphicon glyphicon-pencil"></span> ??????</button>
				  <button type="button" class="btn btn-danger"><span class="glyphicon glyphicon-minus"></span> ??????</button>
				</div>
				
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx" /></td>
							<td>??????</td>
							<td>????????????</td>
							<td>??????</td>
							<td>??????</td>
							<td>?????????</td>
							<td>??????</td>
							<td>???????????????</td>
						</tr>
					</thead>
					<tbody id="transactionBody">
<%--						<tr>--%>
<%--							<td><input type="checkbox" /></td>--%>
<%--							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/transaction/detail.jsp';">????????????-??????01</a></td>--%>
<%--							<td>????????????</td>--%>
<%--							<td>??????/??????</td>--%>
<%--							<td>?????????</td>--%>
<%--							<td>zhangsan</td>--%>
<%--							<td>??????</td>--%>
<%--							<td>??????</td>--%>
<%--						</tr>--%>
<%--                        <tr class="active">--%>
<%--                            <td><input type="checkbox" /></td>--%>
<%--                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.html';">????????????-??????01</a></td>--%>
<%--                            <td>????????????</td>--%>
<%--                            <td>??????/??????</td>--%>
<%--                            <td>?????????</td>--%>
<%--                            <td>zhangsan</td>--%>
<%--                            <td>??????</td>--%>
<%--                            <td>??????</td>--%>
<%--                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 20px;">


					<div id="transactionPage"></div>

			</div>
			
		</div>
		
	</div>
</body>
</html>