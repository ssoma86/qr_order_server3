<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
	request.setCharacterEncoding("UTF-8");
	response.setHeader("Cache-Control","no-store");
	response.setHeader("Pragma","no-cache");
	response.setDateHeader("Expires",0);
	response.setHeader("Cache-Control", "no-cache");
%>
<!DOCTYPE html>
<html lang="ko">
<head>
	<title>메뉴플러스 주문알림</title>
	<meta http-equiv="Content-Type" content="text/html;" charset="utf-8">
	<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no, viewport-fit=cover">
	<meta http-equiv="X-UA-Compatible" content="IE-Edge">
	<link rel="stylesheet" type="text/css" href="css/common.css">
	<script src="https://code.jquery.com/jquery-3.7.0.min.js" integrity="sha256-2Pmvv0kuTBOenSvLm6bvfBSSHrUJ+3A7x6P5Ebd07/g=" crossorigin="anonymous"></script>
	<script src="<%= request.getContextPath() %>/app/js/common.js"></script>
	<script>
		function btnClose() {
			Javascript:window.AndroidBridge.terminate();
		}
		
		function btnRetry() {
			Javascript:window.AndroidBridge.retry();
		}
	</script>
</head>
<body style="background:#eee;">
	<div class="wrap">
		<header>
			<div class="gnb">
				<div class="shop-name"></div>
				<div class="gnb-logo">
					<img src="<%= request.getContextPath() %>/app/img/logo.png">
				</div>
			</div>
		</header>
		<section class="content">
			<div class="error">
				<img src="<%= request.getContextPath() %>/app/img/i_error.png">
				<p>오류가 발생했습니다.</p>
				<div class="btn-wrap">
					<a href="#" class="btn btn-m btn-wm btn-gray" onclick="btnClose();">종료</a>
					<a href="#" class="btn btn-m btn-wm btn-d-gray" onclick="btnRetry();">재시작</a>
				</div>
			</div>
		</section>
		<section class="nav-wrap">
			<div class="nav">
				<div class="top">
					<span>MENU</span>
					<a class="nav-close"><img src="<%= request.getContextPath() %>/app/img/i-x.png"></a>
				</div>
			</div>
			<div class="nav-close"></div>
		</section>
	</div>
</body>
</html>