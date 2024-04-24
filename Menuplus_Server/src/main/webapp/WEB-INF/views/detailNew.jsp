<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

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
   	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/app/css/common.css">
   	<script src="https://code.jquery.com/jquery-3.7.0.min.js" integrity="sha256-2Pmvv0kuTBOenSvLm6bvfBSSHrUJ+3A7x6P5Ebd07/g=" crossorigin="anonymous"></script>
   	<script src="<%= request.getContextPath() %>/app/js/common.js"></script>
	<script>
		$(document).ready(function(){
			$('.btn-cancel').click(function() {
				if(validateData()) {
					var cancelData = new Object();
					${orderData.orderCd}
					cancelData.orderCd = '${orderData.orderCd}';
					cancelData.orderId = '${orderData.orderId}';
					cancelData.cancelReason = $('input[name=cancelRadio]:checked').val();
					cancelData.cancelPw = $('input[name=cancelPw]').val();
					
					$.ajax({
						type : "POST",
						contentType : "application/json; charset=utf-8;",
						dataType : "json",
						url : "<%=request.getContextPath()%>/app/cancelOrder",
						async : false,
						data : JSON.stringify(cancelData),
						cache : false,
						success : function(data) {
							alert(data.resultMsg);
							if(data.resultCode == "2001") {
								window.location.replace("<%= request.getContextPath() %>/app/index");							
							}
						},
						error : function(data) {
							alert(data.resultMsg);
						}
					});
				}
			});
		
			function validateData() {
				var cancelValue = $('input[name=cancelRadio]:checked').val();
				var cancelPw = $('input[name=cancelPw]').val();
				
				if(cancelValue == '' || cancelValue == undefined || cancelValue == null) {
					alert("취소사유를 선택해주세요.");
					return false;
				}
				
				if(cancelPw == '' || cancelPw == undefined || cancelPw == null) {
					alert("취소비밀번호를 입력해주세요.");
					$('input[name=cancelPw]').focus();
					return false;
				}
				
				$('.oreder-cancel').removeClass('on');
				return true;
			}
		});
		
		function searchSales() {
   	  		$("#frm").submit();
   	  	}
		
		function changeOrder(orderCd, orderStatusCd) {
   	  		var changeData = new Object();
   	  		changeData.orderCd = orderCd;
   	  		changeData.orderStatusCd = orderStatusCd;
   	  		
   	  		$.ajax({
	   	  		type : "POST",
				contentType : "application/json; charset=utf-8;",
				dataType : "json",
				url : "<%=request.getContextPath()%>/app/changeStatus",
				async : false,
				data : JSON.stringify(changeData),
				cache : false,
				success : function(data) {
					if(data.resultCode == "0000") {
						window.location.replace("<%= request.getContextPath() %>/app/index");							
					} else {
						alert(data.resultMsg);
					}
				},
				error : function(data) {
					alert(data.resultMsg);
				}
   	  		});
   	  	}
</script>
</head>
<body>
	<form action="/app/salesList" name="frm" id="frm" method="get"></form>
	<div class="wrap detail s-new">
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
            	<div class="state">신규</div>
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
         	</div>
         	<div class="cancel-btn-wrap">
            	<a href="#" class="btn btn-w btn-m popup-trigger" popup-class=".oreder-cancel">주문취소</a>
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
            	<a href="#" class="btn btn-gray btn-l btn-ws btn-undo">뒤로가기</a>
            	<a href="#" class="btn btn-green btn-l btn-wl" onclick="changeOrder('${orderData.orderCd}', 30);">주문접수</a>
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
<!--                <li> -->
<%--                   <a href="<%= request.getContextPath() %>/app/setting"> --%>
<%--                      <img src="<%= request.getContextPath() %>/app/img/i-menu2.png"> --%>
<!--                      <span>주문 알림설정</span> -->
<!--                   </a> -->
<!--                </li> -->
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
      <div class="popup-wrap oreder-cancel">
         <div class="popup">
            <div class="popup-con">
               <div class="input-wrap">
                  <label>취소 사유</label>
                  <div class="radio-wrap">
                     <input type="radio" name="cancelRadio" id="cancelRadio1" value="90"><label for="cancelRadio1">품절 또는 재료 소진</label>
                  </div>
                  <div class="radio-wrap">
                     <input type="radio" name="cancelRadio" id="cancelRadio2" value="91"><label for="cancelRadio2">고객의 주문 실수</label>
                  </div>
                  <div class="radio-wrap">
                     <input type="radio" name="cancelRadio" id="cancelRadio3" value="92"><label for="cancelRadio3">고객의 환불 요청</label>
                  </div>
               </div>
               <div class="input-wrap">
                  <label for="cancelPw">취소 비밀번호</label>
                  <input type="password" id="cancelPw" name="cancelPw">
               </div>
            </div>
            <div class="btn-wrap">
               <a href="#" class="btn btn-l btn-wm btn-gray popup-close">닫기</a>
               <a href="#" class="btn btn-l btn-wm btn-d-gray btn-cancel">결제취소</a>
            </div>
         </div>
      </div>
   </div>
</body>
</html>