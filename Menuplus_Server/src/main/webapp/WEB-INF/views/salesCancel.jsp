<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

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
	<title>메뉴플러스 주문상세</title>
	<meta http-equiv="Content-Type" content="text/html;" charset="utf-8">
	<meta name="viewport" content="height=device-height,width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no, viewport-fit=cover">
	<meta http-equiv="X-UA-Compatible" content="IE-Edge">
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/app/css/common.css">
	<script src="https://code.jquery.com/jquery-3.7.0.min.js" integrity="sha256-2Pmvv0kuTBOenSvLm6bvfBSSHrUJ+3A7x6P5Ebd07/g=" crossorigin="anonymous"></script>
	<script src="<%=request.getContextPath()%>/app/js/common.js"></script>
	<script>
		$(document).ready(function() {
			
		});
		
		function searchSales() {
   	  		$("#frm").submit();
   	  	}
	</script>
</head>
<body>
	<form action="/app/salesList" name="frm" id="frm" method="get"></form>
	<div class="wrap detail s-cancel">
		<header>
			<div class="gnb">
				<a class="btn-undo" href="#"><img src="<%= request.getContextPath() %>/app/img/i_undo.png"></a>
				<h2 class="page-title">주문상세</h2>
				<a class="btn-home" href="<%= request.getContextPath() %>/app/index"><img src="<%= request.getContextPath() %>/app/img/i_home.png"></a>
				<a class="btn-menu" href="#"><img src="<%= request.getContextPath() %>/app/img/i-menu.png"></a>
			</div>
			<div class="detail-header">
				<div class="room-nm">${orderData.storeRoomNm}</div>
				<div class="time">${orderData.orderDate}</div>
				<div class="state">취소</div>
			</div>
			<div class="detail-ladel">
				<span>상품</span>
				<span>수량</span>
				<span>금액</span>
			</div>
		</header>
		<section class="content">
			<ul class="goods-li detail-li">
				<c:forEach var="smenus" items="${orderData.smenus}">
      				<c:set var="orderCnt" value="${orderCnt + smenus.cnt}"></c:set>
      				<c:set var="orderAmt" value="${orderAmt + smenus.price}"></c:set>
	            	<li>
	               		<div class="top">
	                  		<div class="goods-nm">
	                  			<c:forEach var="smenuInfos" items="${smenus.smenuInfos}">
	                  				<c:if test="${smenuInfos.lanTp.id eq 'ko'}">
			                     		<div>${smenuInfos.smenuInfoNm}</div>
	                  				</c:if>
	                  			</c:forEach>
	                  		</div>
	                  		<div class="od-ct">${smenus.cnt}</div>
	                  		<div class="od-price"><fmt:formatNumber value="${smenus.price}" type="number"/></div>
	               		</div>
	               		<c:forEach var="smenuOpts" items="${smenus.smenuOpts}">
	               			<c:forEach var="smenuOptInfos" items="${smenuOpts.smenuOptInfos}">
	               				<c:if test="${smenuOptInfos.lanTp.id eq 'ko'}">
	               					<div class="option">${smenuOptInfos.smenuOptInfoNm}</div>
	               				</c:if>
	               			</c:forEach>
	               		</c:forEach>
	               	</li>
      			</c:forEach>
			</ul>
			<div class="info-box">
				<div>
					<div class="label">주문자</div>
					<div class="data">${orderData.guestName}</div>
				</div>
				<div>
					<div class="label">휴대폰</div>
					<div class="data no-link">${orderData.orderTel}</div>
				</div>
				<div>
					<div class="label">취소일시</div>
					<div class="data">${orderData.cancelDate}</div>
				</div>
				<div>
					<div class="label">취소사유</div>
					<c:choose>
						<c:when test="${orderData.cancelReason eq '90'}">
							<div class="data">품절 또는 재료소진</div>
						</c:when>
						<c:when test="${orderData.cancelReason eq '91'}">
							<div class="data">고객의 주문 실수</div>
						</c:when>
						<c:when test="${orderData.cancelReason eq '92'}">
							<div class="data">고객의 환불 요청</div>
						</c:when>
					</c:choose>
				</div>
			</div>
			<div class="detail-sum">
				<div class="sum-ct-wrap">
					<span>합계</span>
					<span class="sum-ct">${orderCnt}</span>
					<span>개</span>
				</div>
				<div class="sum-pr-wrap">
					<fmt:formatNumber value="${orderAmt}" type="number"/>
				</div>
			</div>
			<div class="btn-wrap">
				<a href="#" class="btn btn-gray btn-l btn-undo">뒤로가기</a>
			</div>
		</section>
		<section class="nav-wrap">
			<div class="nav">
				<div class="top">
					<span>MENU</span>
					<a class="nav-close"><img src="<%= request.getContextPath() %>/app/img/i-x.png"></a>
				</div>
				<ul class="menu">
					<li class="current">
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
					<li>
						<a href="<%= request.getContextPath() %>/app/customerCenter">
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