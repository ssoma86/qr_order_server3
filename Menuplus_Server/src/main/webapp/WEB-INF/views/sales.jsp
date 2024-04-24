<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URLDecoder"%>
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
<title>메뉴플러스 주문알림</title>
	<meta http-equiv="Content-Type" content="text/html;" charset="utf-8">
	<meta name="viewport" content="height=device-height,width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no, viewport-fit=cover">
	<meta http-equiv="X-UA-Compatible" content="IE-Edge">
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/app/css/common.css">
	<script src="https://code.jquery.com/jquery-3.7.0.min.js" integrity="sha256-2Pmvv0kuTBOenSvLm6bvfBSSHrUJ+3A7x6P5Ebd07/g=" crossorigin="anonymous"></script>
	<script src="<%= request.getContextPath() %>/app/js/common.js"></script>
	<!--달력 코드 추가-->
	<link rel="stylesheet" type="text/css" href="css/datepicker.css">
	<script src="<%= request.getContextPath() %>/app/js/datepicker.js"></script>
	<script>
		$(document).ready(function(){

	  	});
		
		function searchSales() {
			menuplusApp.loadingShow();
			$("#frm").submit();
			//menuplusApp.loadinghide();
		}
		
		function salesDetail(orderCd) {
			$("input[name=orderCd]").val(orderCd);
			if($('#approveCheck').is(':checked')) {
				$("#salesFrm input[name=approveCheck]").val('on');
			} else {
				$("#salesFrm input[name=approveCheck]").val('');
			}
			if($('#cancelCheck').is(':checked')) {
				$("#salesFrm input[name=cancelCheck]").val('on');
			} else {
				$("#salesFrm input[name=cancelCheck]").val('');
			}
			$("#salesFrm").submit();
		}
		
		//조회일자 변경
		function changeDate(range) {
			var now = new Date(); //현재일자
			var startdate = new Date(now); //시작일자
			
			$('.date-btn-wrap .btn').removeClass('on');
			$('input[name=range]').remove();
			
			if(range == 'today') {
				startdate.setDate(startdate.getDate());
				$('#frm').append("<input type='hidden' name='range' value='today'/>")
				$('.date-btn-wrap .btn:nth-child(1)').addClass('on');
			} else if(range == 'yesterday') {
				startdate.setDate(startdate.getDate() - 1);
				$('#frm').append("<input type='hidden' name='range' value='yesterday'/>")
				$('.date-btn-wrap .btn:nth-child(2)').addClass('on');
			} else if(range == 'week') {
				startdate.setDate(startdate.getDate() - 7);
				$('#frm').append("<input type='hidden' name='range' value='week'/>")
				$('.date-btn-wrap .btn:nth-child(3)').addClass('on');
			} else if(range == 'month') {
				startdate.setMonth(startdate.getMonth() - 1);
				$('#frm').append("<input type='hidden' name='range' value='month'/>")
				$('.date-btn-wrap .btn:nth-child(4)').addClass('on');
			}

			$("#calendarDate").val(dateFormat(startdate) + " ~ " + dateFormat(now));
		}
		
		//일자 포맷 셋팅
		function dateFormat(date) {
			var year = date.getFullYear(); //년
		    var month = ("0" + (1 + date.getMonth())).slice(-2); //월
		    var day = ("0" + date.getDate()).slice(-2); //일
		    
		    return year + ". " + month + ". " + day
		}
	</script>
</head>
<body style="background:#eee;">
	<form action="/app/salesDetail" name="salesFrm" id="salesFrm" method="get">
		<input type="hidden" name="calendarDate" value="${calendarStart} ~ ${calendarEnd}"/>
		<input type="hidden" name="approveCheck" value=""/>
		<input type="hidden" name="cancelCheck" value=""/>
		<input type="hidden" name="range" value="${range}"/>
		<input type="hidden" name="orderCd" value=""/>
	</form>
	<c:forEach var="orderList" items="${orderList}">
		<c:if test="${orderList.cancelYn eq 'Y' }">
			<c:set var="cancelCnt" value="${cancelCnt + 1}"></c:set>
			<c:set var="cancelAmount" value="${cancelAmount + orderList.Amount}"/>
		</c:if>
		<c:if test="${orderList.cancelYn eq 'N' }">
			<c:set var="approveCnt" value="${approveCnt + 1}"></c:set>
			<c:set var="approveAmount" value="${approveAmount + orderList.Amount}"/>
		</c:if>
	</c:forEach>
	<div class="wrap sales">
		<div class="loading-wrap">
			<div>
				<img src="<%= request.getContextPath() %>/app/img/loading.png">
				<p>로딩중</p>
			</div>
		</div>
		<header>
			<div class="gnb">
				<div class="shop-name">${storeInfo.storeNm}</div>
				<div class="gnb-logo"><img src="<%= request.getContextPath() %>/app/img/logo.png"></div>
				<a class="btn-menu" href="#"><img src="<%= request.getContextPath() %>/app/img/i-menu.png"></a>
			</div>
		</header>
		<section class="tab-nav-wrap">
			<a href="<%= request.getContextPath() %>/app/index" class="tab-nav">
				<span class="order-mng">주문관리</span>
			</a>
			<a href="#" class="tab-nav on">
				<span class="sales-check">매출조회</span>
			</a>
		</section>
		<section class="content">
			<form action="/app/salesList" name="frm" id="frm" method="get">
				<div class="search-wrap">
					<div class="date-wrap">
						<label for="date-to">기간</label>
						<input type="text" class="datepick" id="calendarDate" name="calendarDate" readonly="" value="${calendarStart} ~ ${calendarEnd}">
					</div>
					<div class="date-btn-wrap">
						<c:choose>
							<c:when test="${range eq 'today'}">
								<a href="#" class="btn btn-s btn-w on" onclick="changeDate('today');">오늘</a>
								<a href="#" class="btn btn-s btn-w" onclick="changeDate('yesterday');">어제</a>
								<a href="#" class="btn btn-s btn-w" onclick="changeDate('week');">1주</a>
								<a href="#" class="btn btn-s btn-w" onclick="changeDate('month');">1달</a>
								<input type="hidden" name="range" value="today"/>
							</c:when>
							<c:when test="${range eq 'yesterday'}">
								<a href="#" class="btn btn-s btn-w" onclick="changeDate('today');">오늘</a>
								<a href="#" class="btn btn-s btn-w on" onclick="changeDate('yesterday');">어제</a>
								<a href="#" class="btn btn-s btn-w" onclick="changeDate('week');">1주</a>
								<a href="#" class="btn btn-s btn-w" onclick="changeDate('month');">1달</a>
								<input type="hidden" name="range" value="yesterday"/>
							</c:when>
							<c:when test="${range eq 'week'}">
								<a href="#" class="btn btn-s btn-w" onclick="changeDate('today');">오늘</a>
								<a href="#" class="btn btn-s btn-w" onclick="changeDate('yesterday');">어제</a>
								<a href="#" class="btn btn-s btn-w on" onclick="changeDate('week');">1주</a>
								<a href="#" class="btn btn-s btn-w" onclick="changeDate('month');">1달</a>
								<input type="hidden" name="range" value="week"/>
							</c:when>
							<c:when test="${range eq 'month'}">
								<a href="#" class="btn btn-s btn-w" onclick="changeDate('today');">오늘</a>
								<a href="#" class="btn btn-s btn-w" onclick="changeDate('yesterday');">어제</a>
								<a href="#" class="btn btn-s btn-w" onclick="changeDate('week');">1주</a>
								<a href="#" class="btn btn-s btn-w on" onclick="changeDate('month');">1달</a>
								<input type="hidden" name="range" value="month"/>
							</c:when>
							<c:otherwise>
								<a href="#" class="btn btn-s btn-w on" onclick="changeDate('today');">오늘</a>
								<a href="#" class="btn btn-s btn-w" onclick="changeDate('yesterday');">어제</a>
								<a href="#" class="btn btn-s btn-w" onclick="changeDate('week');">1주</a>
								<a href="#" class="btn btn-s btn-w" onclick="changeDate('month');">1달</a>
								<input type="hidden" name="range" value="today"/>
							</c:otherwise>
						</c:choose>
					</div>
					<div class="state-wrap">
						<label>상태</label>
						<div class="w-box">
							<c:choose>
								<c:when test="${payStatus eq 'Y'}">
									<div class="checkbox-wrap">
										<input type="checkbox" name="approveCheck" id="approveCheck"><label for="approveCheck">승인</label>
									</div>
									<div class="checkbox-wrap">
										<input type="checkbox" name="cancelCheck" id="cancelCheck" checked><label for="cancelCheck">취소</label>
									</div>
								</c:when>
								<c:when test="${payStatus eq 'N'}">
									<div class="checkbox-wrap">
										<input type="checkbox" name="approveCheck" id="approveCheck" checked><label for="approveCheck">승인</label>
									</div>
									<div class="checkbox-wrap">
										<input type="checkbox" name="cancelCheck" id="cancelCheck"><label for="cancelCheck">취소</label>
									</div>
								</c:when>
								<c:otherwise>
									<div class="checkbox-wrap">
										<input type="checkbox" name="approveCheck" id="approveCheck" checked><label for="approveCheck">승인</label>
									</div>
									<div class="checkbox-wrap">
										<input type="checkbox" name="cancelCheck" id="cancelCheck" checked><label for="cancelCheck">취소</label>
									</div>
								</c:otherwise>
							</c:choose>
						</div>
						<a href="#" class="btn btn-s btn-d-gray" onclick="searchSales();">조회</a>
					</div>
				</div>
			</form>
			<div class="info-box">
				<div class="t-blue">
					<div class="label">승인</div>
					<div class="data">
						<c:choose>
							<c:when test="${approveCnt eq null}">
								<span class="count">0건</span>
							</c:when>
							<c:otherwise>
								<span class="count"><fmt:formatNumber value="${approveCnt}" type="number"/>건</span>
								<span class="amount"><fmt:formatNumber value="${approveAmount}" type="number"/></span>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
				<div class="t-red">
					<div class="label">취소</div>
					<div class="data">
						<c:choose>
							<c:when test="${cancelCnt eq null}">
								<span class="count">0건</span>
							</c:when>
							<c:otherwise>
								<span class="count"><fmt:formatNumber value="${cancelCnt}" type="number"/>건</span>
								<span class="amount"><fmt:formatNumber value="${cancelAmount}" type="number"/></span>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
				<div>
					<div class="label">합계</div>
					<div class="data">
						<c:choose>
							<c:when test="${approveCnt eq null and cancelCnt eq null}">
								<span class="count">0건</span>
							</c:when>
							<c:otherwise>
								<span class="count"><fmt:formatNumber value="${approveCnt + cancelCnt}" type="number"/>건</span>
								<span class="amount"><fmt:formatNumber value="${approveAmount - cancelAmount}" type="number"/></span>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</div>
			<div class="search-result">
				<c:forEach var="orderList" items="${orderList}">
					<c:set var="menuCnt" value="-1"/>
					<c:if test="${orderList.cancelYn eq 'N'}">
						<div class="order-box s-complete">
							<a href="#" onclick="salesDetail('${orderList.orderCd}');">
								<div class="top-info">
									<div class="room-nm">${orderList.storeRoomNm}</div>
									<div class="time">${orderList.orderDate}</div>
									<div class="state">승인</div>
								</div>
								<ul class="goods-li">
									<li>
										<c:forEach var="smenus" items="${orderList.smenus}" varStatus="status">
											<c:if test="${status.first}">
												<c:set var="menuTitle" value="${smenus.smenuNmLan}"/>												
											</c:if>
											<c:set var="menuCnt" value="${menuCnt + 1}"/>
										</c:forEach>
										<c:choose>
											<c:when test="${menuCnt > 0}">
												<span class="goods-nm">${menuTitle} 외 ${menuCnt}개</span>
											</c:when>
											<c:otherwise>
												<span class="goods-nm">${menuTitle}</span>
											</c:otherwise>
										</c:choose>
										<span class="od-ct"><fmt:formatNumber value="${orderList.Amount}" type="number"/></span>
									</li>
								</ul>
							</a>
						</div>
					</c:if>
					<c:if test="${orderList.cancelYn eq 'Y'}">
						<div class="order-box s-cancel">
							<a href="#" onclick="salesDetail('${orderList.orderCd}');">
								<div class="top-info">
									<div class="room-nm">${orderList.storeRoomNm}</div>
									<div class="time">${orderList.orderDate}</div>
									<div class="state">취소</div>
								</div>
								<ul class="goods-li">
									<li>
										<c:forEach var="smenus" items="${orderList.smenus}" varStatus="status">
											<c:if test="${status.first}">
												<c:set var="menuTitle" value="${smenus.smenuNmLan}"/>												
											</c:if>
											<c:set var="menuCnt" value="${menuCnt + 1}"/>
										</c:forEach>
										<c:choose>
											<c:when test="${menuCnt > 0}">
												<span class="goods-nm">${menuTitle} 외 ${menuCnt}개</span>
											</c:when>
											<c:otherwise>
												<span class="goods-nm">${menuTitle}</span>
											</c:otherwise>
										</c:choose>
										<span class="od-ct"><fmt:formatNumber value="${orderList.Amount}" type="number"/></span>
									</li>
								</ul>
							</a>
						</div>
					</c:if>
				</c:forEach>
				<c:choose>
					<c:when test="${orderList.size() == 0}">
						<p class="info-text">완료된 건이 없습니다.</p>
					</c:when>
					<c:otherwise>
						<p class="info-text">모든 조회내역을 확인 하였습니다.</p>
					</c:otherwise>
				</c:choose>
			</div>
		</section>
		<section class="nav-wrap">
			<div class="nav">
				<div class="top">
					<span>MENU</span>
					<a class="nav-close"><img src="<%= request.getContextPath() %>/app/img/i-x.png"></a>
				</div>
				<ul class="menu">
					<li>
						<a href="<%= request.getContextPath() %>/app/index">
							<img src="<%= request.getContextPath() %>/app/img/i-order-on.png">
							<span>주문관리</span>
						</a>
					</li>
					<li class="current">
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