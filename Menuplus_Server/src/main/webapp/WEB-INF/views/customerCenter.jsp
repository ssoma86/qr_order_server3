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
	<title>메뉴플러스 고객센터</title>
	<meta http-equiv="Content-Type" content="text/html;" charset="utf-8">
	<meta name="viewport" content="height=device-height,width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no, viewport-fit=cover">
	<meta http-equiv="X-UA-Compatible" content="IE-Edge">
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/app/css/common.css">
	<script src="https://code.jquery.com/jquery-3.7.0.min.js" integrity="sha256-2Pmvv0kuTBOenSvLm6bvfBSSHrUJ+3A7x6P5Ebd07/g=" crossorigin="anonymous"></script>
	<script src="<%= request.getContextPath() %>/app/js/common.js"></script>
	<script>
	$(document).ready(function(){
		
	});
	
	function searchSales() {
		$("#frm").submit();
	}
	</script>
</head>
<body>
	<form action="/app/salesList" name="frm" id="frm" method="get"></form>
	<div class="wrap b-info">
		<header>
			<div class="gnb">
				<a class="btn-undo" href="#">
					<img src="<%= request.getContextPath() %>/app/img/i_undo.png">
				</a>
				<h2 class="page-title">고객센터</h2>
				<a class="btn-home" href="<%= request.getContextPath() %>/app/index">
					<img src="<%= request.getContextPath() %>/app/img/i_home.png">
				</a>
				<a class="btn-menu" href="#">
					<img src="<%= request.getContextPath() %>/app/img/i-menu.png">
				</a>
			</div>
		</header>
		<section class="content">
			<div class="info-box">
				<div>
					<div class="label">전화번호</div>
					<div class="data">
						<a href="tel:1688-1250">1688-1250</a>
					</div>
				</div>
				<div>
					<div class="label">운영시간</div>
					<div class="data">
						오전 9시 ~ 오후 6시<br>(토,일,공휴일 휴무)
					</div>
				</div>
				<div>
					<div class="label">이메일</div>
					<div class="data">
						<a href="mailTo:sales@infinisoft.co.kr">sales@infinisoft.co.kr</a>
					</div>
				</div>
				<div>
					<div class="label">팩스</div>
					<div class="data">
						<a href="#" class="no-link">02-6280-4489</a>
					</div>
				</div>
			</div>
		</section>
		<section class="nav-wrap">
			<div class="nav">
				<div class="top">
					<span>MENU</span>
					<a class="nav-close">
						<img src="<%= request.getContextPath() %>/app/img/i-x.png">
					</a>
				</div>
				<ul class="menu">
					<li>
						<a href="<%= request.getContextPath() %>/app/index">
							<img src="<%= request.getContextPath() %>/app/img/i-order-on.png">
							<span>주문관리</span>
						</a>
					</li>
					<li>
						<a href="#" onclick="searchSales();">
							<img src="<%= request.getContextPath() %>/app/img/i-sales-on.png">
							<span>매출조회</span>
						</a>
					</li>
					<li>
						<a href="<%= request.getContextPath() %>/app/businessInfo">
							<img src="<%= request.getContextPath() %>/app/img/i-menu1.png">
							<span>사업자 정보</span>
						</a>
					</li>
<!-- 					<li> -->
<%-- 						<a href="<%= request.getContextPath() %>/app/setting"> --%>
<%-- 							<img src="<%= request.getContextPath() %>/app/img/i-menu2.png"> --%>
<!-- 							<span>주문 알림설정</span> -->
<!-- 						</a> -->
<!-- 					</li> -->
					<li class="current">
						<a href="<%= request.getContextPath() %>/app/coustomerCenter">
							<img src="<%= request.getContextPath() %>/app/img/i-menu3.png">
							<span>고객센터</span>
						</a>
					</li>
					<li>
						<a href="#" class="logout-btn popup-trigger" popup-class=".popup-logout">
							<img src="<%= request.getContextPath() %>/app/img/i-menu4.png">
							<span>로그아웃</span>
						</a>
					</li>
				</ul>
			</div>
			<div class="nav-close"></div>
		</section>
		<div class="popup-wrap popup-logout">
			<div class="popup">
				<div class="popup-con">
					<h3 class="notice-text">로그아웃 하시겠습니까?</h3>
				</div>
				<div class="btn-wrap">
					<a href="#" class="btn btn-l btn-wm btn-gray popup-close">닫기</a>
					<a href="<%= request.getContextPath() %>/app/logout" class="btn btn-l btn-wm btn-d-gray popup-close">로그아웃</a>
				</div>
			</div>
		</div>
	</div>
</body>
</html>