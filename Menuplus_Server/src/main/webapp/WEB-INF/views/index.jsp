<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URLDecoder"%>
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

Date date = new Date();
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

Calendar cal = Calendar.getInstance();
cal.setTime(date);
String indexDate = sdf.format(cal.getTime());

System.out.println("***** start app index.jsp [" + indexDate + "] ***** ");
Enumeration eNames = session.getAttributeNames();
if(eNames.hasMoreElements()) {
	Map entries = new TreeMap();
	while(eNames.hasMoreElements()) {
		String name = (String) eNames.nextElement();
		List values = new ArrayList();
		values.add(session.getAttribute(name));
		if(values.size() > 0) {
			String value = values.get(0).toString();
			for(int i = 1; i < values.size(); i++) {
				value += "," + values.get(i);
			}
			System.out.println(name + "[" + value + "]");
		}
	}
}
System.out.println("***** end app index.jsp [" + indexDate + "] ***** ");
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
   	<script src="<%= request.getContextPath() %>/app/js/pulltorefresh.js"></script>
   	
   	<script src="<%= request.getContextPath() %>/app/js/swfobject.js"></script>
   	<script src="<%= request.getContextPath() %>/app/js/web_socket.js"></script>
   	
   	<script>
   		$(document).ready(function(){
	   	  	/*새로고침*/
	   	  	var ptr = PullToRefresh.init({
	   	  		mainElement:'.main',
	   	  		triggerElement:'.main',
	   	  		instructionsPullToRefresh:'더 내리면 새로고침',
	   	  		instructionsReleaseToRefresh:'놓으면 새로고침 됩니다.',
	   	  		instructionsRefreshing:'새로고침 중',
	   	  		distThreshold:'80',
	   	  		distMax:'100',
	   	  		onRefresh() {
	   	  			window.location.reload();
	   	  		}
	   	  	});
	   	  	//newOrderList();
	   	 	//setInterval(newOrderList, 1000 * 60);
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
   	  	
   	  	//리스트 최신화
   	  	function newOrderList() {
	   	  	var date = new Date();
	   	 	var year = date.getFullYear(); //년
		    var month = ("0" + (1 + date.getMonth())).slice(-2); //월
		    var day = ("0" + date.getDate()).slice(-2); //일
			var hour = date.getHours(); //시간
			var minute = date.getMinutes(); //분
			var second = date.getSeconds(); //초
			
   	  		console.log("newOrderList call[" + year + "." + month + "." + day + " " + hour + ":" + minute + ":" + second + "]");
   	  		var oldOrderCd = new Array();
   	  		var newOrderCd = new Array();
   	  		var diffOrderCd = new Array();
   	  		var newOrderList = new Array();
   	  		
   	  		$('.s-new .orderCd').each(function(index) {
   	  			oldOrderCd.push($(this).val());
   	  		});
	   	  	var now = new Date(); //시작일자
	   	 	var end = new Date(now); //종료일자
	   	 	end.setDate(end.getDate() + 1); //종료일자
   	  		
   	  		var startDate = dateFormat(now);
	   	 	var endDate = dateFormat(end);
	   	 	
   	  		$.ajax({
   	  			type : "POST",
   	  			url : "https://admin.menuplus.kr/api/app/orderlist/<%=session.getAttribute("accountId") %>/" + startDate + "/" + endDate + "/" + "28/A",
   	  			async : false,
   	  			cache : false,
	   	  		success : function(data) {
	   	  			for(var i = 0; i < data.length; i++) {
	   	  				newOrderList.push(data[i]);
	   	  				newOrderCd.push(JSON.stringify(data[i].orderCd));
	   	  			}
				},
				error : function(data) {
					alert("시스템오류 재시도 필요");
				}
   	  		});
   	  		
   	  		console.log("old order : " + oldOrderCd);
   	  		console.log("new order : " + newOrderCd);

	   	  	newOrderCd.reverse().filter(function(number) {
	   	  		if(!oldOrderCd.includes(number)) {
	   	  			console.log("diff orderCd : " + number);
	   	  			newOrderList.reverse().filter(function(order) {
	   	  				if(number == order.orderCd) {
	   	  					console.log(order);
	   	  					var displayTm = displayTime(order.orderDate);
	   	  					$(".new-order").text(Number($(".new-order").text()) + 1);
	   	  					var html = "";
	   	  					html += "<div class='order-box s-new'>";
	   	  					html += "<input type='hidden' class='orderCd' value='" + order.orderCd + "'/>"; 
	   	  					html += "<a href='<%= request.getContextPath() %>/app/orderDetail/" + order.orderCd + "'>";
	   	  					html += "<div class='top-info'>";
	   	  					html += "<div class='room-nm'>" + order.storeRoomNm + "</div>";
	   	  					html += "<div class='time'>" + displayTm + "</div>";
	   	  					html += "<div class='state'>신규</div>";
	   	  					html += "</div>";
	   	  					html += "<ul class='goods-li'>";
	   	  					for(var menus of order.smenus) {
	   	  						html += "<li>";
	   	  						html += "<div class='top'>";
	   	  						html += "<span class='goods-nm'>" + menus.smenuNmLan + "</span>";
	   	  						html += "<span class='od-ct'>" + menus.cnt + "</span>";
	   	  						html += "</div>";
	   	  						for(var menuOpts of menus.smenuOpts) {
	   	  							for(var menuOptInfo of menuOpts.smenuOptInfos) {
	   	  								if(menuOptInfo.lanTp.id == 'ko') {
	   	  									html += "<div class='option'>" + menuOptInfo.smenuOptInfoNm + "</div>";
	   	  								}
	   	  							}
	   	  						}
	   	  						html += "</li>";
	   	  					}
	   	  					html += "</ul>";
	   	  					html += "</a>";
	   	  					html += "<div class='btns'>";
	   	  					html += "<a class='btn-gray' href='<%= request.getContextPath() %>/app/orderDetail/" + order.orderCd + "'>상세보기</a>";
	   	  					html += "<a class='btn-w' href='#' onclick='changeOrder(" + order.orderCd + ", 30);'>주문접수</a>";
	   	  					html += "</div>";
	   	  					html += "</div>";
	   	  					$('.main').prepend(html);
	   	  				}
	   	  			});
	   	  			
	   	  			console.log("toastOn() 호출");
	   	  			menuplusApp.toastOn();
	   	  		}
		  	});
   	  	}
   	  	
   	  	//일자 포맷 셋팅
		function dateFormat(date) {
			var year = date.getFullYear(); //년
		    var month = ("0" + (1 + date.getMonth())).slice(-2); //월
		    var day = ("0" + date.getDate()).slice(-2); //일
		    
		    return year + month + day;
		}
   	  	
   	  	//주문시간 포맷
		function displayTime(orderDate) {
			var milliSeconds = new Date() - new Date(orderDate);
			
			var seconds = milliSeconds / 1000;
			if (seconds < 60) return '방금 전';
			
			var minutes = seconds / 60;
			if (minutes < 60) return Math.round(minutes) + '분 전';
			
			var hours = minutes / 60;
			if (hours < 24) return Math.round(hours) + '시간 전';
			
			var days = hours / 24;
			if (days < 7) return Math.round(days) + '일 전';
			
			var weeks = days / 7;
			if (weeks < 5) return Math.round(weeks) + '주 전';
			
			var months = days / 30;
			if (months < 12) return Math.round(months) + '개월 전';
			
			var years = days / 365;
			return Math.round(years) + '년 전';
		}
   	  	
   	  	
   	  	var webSocket;
   	  	
   	  	
   	  	
   	  	
   	  	
   </script>
</head>
<body style="background:#eee;">
	<form action="/app/salesList" name="frm" id="frm" method="get"></form>
	<div class="wrap">
		<header>
			<div class="gnb">
				<div class="shop-name">${storeInfo.storeNm}</div>
				<div class="gnb-logo"><img src="<%= request.getContextPath() %>/app/img/logo.png"></div> 
				<a class="btn-menu" href="#"><img src="<%= request.getContextPath() %>/app/img/i-menu.png"></a>
         	</div>
         	<ul class="tab-menu-wrap">
            	<li id="menu1" class="tab-menu new-od active">
            		<div class="state">
            			<c:forEach var="orderList" items="${orderList}">
                  			<jsp:useBean id="now" class="java.util.Date"/>
			   	    		<fmt:parseNumber value="${now.time / (1000*60)}" var="nowTime"/>
			   	    		<fmt:parseDate var="orderDate" value="${orderList.orderDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
			        		<fmt:parseNumber value="${orderDate.time / (1000*60)}" var="orderTime"/>
			        		<fmt:parseNumber value="${nowTime - orderTime}" var="timeDefference"/>
							<c:if test="${orderList.orderStatusCd eq '28'}">
	                  			<c:set var="newOrder" value="${newOrder + 1}"></c:set>
                  			</c:if>
                  			<c:if test="${orderList.orderStatusCd eq '30' and (timeDefference > 0 and timeDefference <= 60*24)}">
	                  			<c:set var="workOrder" value="${workOrder + 1}"></c:set>
                  			</c:if>
                  			<c:if test="${orderList.orderStatusCd eq '32' and (timeDefference > 0 and timeDefference <= 60*24)}">
	                  			<c:set var="completeOrder" value="${completeOrder + 1}"></c:set>
                  			</c:if>
                  		</c:forEach>
                  		<div class="title t-orange">신규</div>
                  		<div class="data new-order">${newOrder}</div>
                  	</div>
                  	<div class="state">
	                  	<div class="title t-green">처리중</div>
	                  	<div class="data">${workOrder}</div>
                  	</div>
                </li>
                <li id="menu2" class="tab-menu">
                	<div class="state">
                		<div class="title t-blue">완료</div>
                  		<div class="data">${completeOrder}</div>
               		</div>
            	</li>
         	</ul>
      	</header>
      	<section class="tab-nav-wrap">
      		<a href="#" class="tab-nav on">
      			<span class="order-mng">주문관리</span>
         	</a>
         	<a href="#" onclick="searchSales();" class="tab-nav">
         		<span class="sales-check">매출조회</span>
         	</a>
         </section>
         <section class="content">
         	<div class="tab-con-wrap">
         		<div class="tab-con active main">
         			<c:if test="${newOrder >= 1}">
         				<c:forEach var="orderList" items="${orderList}">
            				<!-- 24시간 이내 신규주문 -->
            				<fmt:parseDate var="orderDate" value="${orderList.orderDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
			        		<fmt:parseNumber value="${orderDate.time / (1000*60)}" var="orderTime"/>
			        		<fmt:parseNumber value="${nowTime - orderTime}" var="timeDefference"/>
							<c:if test="${orderList.orderStatusCd eq '28'}">
				           		<div class="order-box s-new">
				           			<input type="hidden" class="orderCd" value="${orderList.orderCd}"/>
									<a href="<%= request.getContextPath() %>/app/orderDetail/${orderList.orderCd}">
				           			<div class="top-info">
				           				<div class="room-nm">${orderList.storeRoomNm}</div>
				                    		<c:choose>
				                    			<c:when test="${timeDefference > 0 && timeDefference <= 1}"><!-- 1분 이하 -->
				                    				<fmt:parseNumber value="${timeDefference}" integerOnly="true" var="timeDefference"/>
				                    				<div class="time">방금 전</div>
												</c:when>
				                    			<c:when test="${timeDefference > 1 && timeDefference <= 60}"><!-- 1시간 이하 -->
				                    				<fmt:parseNumber value="${timeDefference}" integerOnly="true" var="timeDefference"/>
				                    				<div class="time">${timeDefference}분 전</div>
												</c:when>
												<c:when test="${timeDefference > 60 && timeDefference <= 60*24}"><!-- 24시간 이하 -->
													<fmt:parseNumber value="${timeDefference / 60}" integerOnly="true" var="timeDefference"/>
													<div class="time">${timeDefference}시간 전</div>
												</c:when>
												<c:when test="${timeDefference > 60*24 && timeDefference <= 60*24*30}"><!-- 30일 이하 -->
												    <fmt:parseNumber value="${timeDefference / (60*24)}" integerOnly="true" var="timeDefference"/>
												    <div class="time">${timeDefference}일 전</div>
												</c:when>
												<c:when test="${timeDefference > 60*24*30 && timeDefference <= 60*24*365}"><!-- 1년 이하 -->
												  	<fmt:parseNumber value="${timeDefference / (60*24*30)}" integerOnly="true" var="timeDefference"/>
												  	<div class="time">${timeDefference}월 전</div>
												</c:when>
												<c:when test="${timeDefference > 60*24*365}">
												  	<fmt:parseNumber value="${timeDefference / (60*24*365)}" integerOnly="true" var="timeDefference"/>
												  	<div class="time">${timeDefference}년 전</div>
												</c:when>
				                    		</c:choose>
				                    	<div class="state">신규</div>
				                 	</div>
				                 	<ul class="goods-li">
				                 		<c:forEach var="smenus" items="${orderList.smenus}">
				                 			<li>
				                 				<div class="top">
					                          		<span class="goods-nm">${smenus.smenuNmLan}</span>
					                          		<span class="od-ct">${smenus.cnt}</span>
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
			                		</a>
			                		<div class="btns">
			                			<a class="btn-gray" href="<%= request.getContextPath() %>/app/orderDetail/${orderList.orderCd}">상세보기</a>
			                			<a class="btn-w" href="#" onclick="changeOrder(${orderList.orderCd}, 30);">주문접수</a>
			                		</div>
			                	</div>
	            			</c:if>
            			</c:forEach>
               		</c:if>
	               	<c:if test="${workOrder >= 1}">
	               		<c:forEach var="orderList" items="${orderList}">
	               			<!-- 24시간 이내 작업중 주문 -->
	            			<fmt:parseDate var="orderDate" value="${orderList.orderDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				       		<fmt:parseNumber value="${orderDate.time / (1000*60)}" var="orderTime"/>
				       		<fmt:parseNumber value="${nowTime - orderTime}" var="timeDefference"/>
				    		<c:if test="${orderList.orderStatusCd eq '30' and (timeDefference > 0 and timeDefference <= 60*24)}">
					        	<div class="order-box s-work">
			            		<a href="<%= request.getContextPath() %>/app/orderDetail/${orderList.orderCd}">
				           			<div class="top-info">
				           				<div class="room-nm">${orderList.storeRoomNm}</div>
					                    <c:choose>
					                    	<c:when test="${timeDefference > 0 && timeDefference <= 1}"><!-- 1분 이하 -->
				                    			<fmt:parseNumber value="${timeDefference}" integerOnly="true" var="timeDefference"/>
				                    			<div class="time">방금 전</div>
											</c:when>
					                    	<c:when test="${timeDefference > 0 && timeDefference <= 60}"><!-- 1시간 이하 -->
					                    		<fmt:parseNumber value="${timeDefference}" integerOnly="true" var="timeDefference"/>
					                    		<div class="time">${timeDefference}분 전</div>
											</c:when>
											<c:when test="${timeDefference > 60 && timeDefference <= 60*24}"><!-- 24시간 이하 -->
												<fmt:parseNumber value="${timeDefference / 60}" integerOnly="true" var="timeDefference"/>
												<div class="time">${timeDefference}시간 전</div>
											</c:when>
<%-- 											<c:when test="${timeDefference > 60*24 && timeDefference <= 60*24*30}"><!-- 30일 이하 --> --%>
<%-- 												<fmt:parseNumber value="${timeDefference / (60*24)}" integerOnly="true" var="timeDefference"/> --%>
<%-- 												<div class="time">${timeDefference}일 전</div> --%>
<%-- 											</c:when> --%>
<%-- 											<c:when test="${timeDefference > 60*24*30 && timeDefference <= 60*24*365}"><!-- 1년 이하 --> --%>
<%-- 											  	<fmt:parseNumber value="${timeDefference / (60*24*30)}" integerOnly="true" var="timeDefference"/> --%>
<%-- 											  	<div class="time">${timeDefference}월 전</div> --%>
<%-- 											</c:when> --%>
<%-- 											<c:when test="${timeDefference > 60*24*365}"> --%>
<%-- 											  	<fmt:parseNumber value="${timeDefference / (60*24*365)}" integerOnly="true" var="timeDefference"/> --%>
<%-- 											  	<div class="time">${timeDefference}년 전</div> --%>
<%-- 											</c:when> --%>
					                    </c:choose>
					                   <div class="state">처리중</div>
					                </div>
					                <ul class="goods-li">
					                	<c:forEach var="smenus" items="${orderList.smenus}">
					                		<li>
					                			<div class="top">
					                				<span class="goods-nm">${smenus.smenuNmLan}</span>
						                         	<span class="od-ct">${smenus.cnt}</span>
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
						       		</a>
						       		<div class="btns">
			                			<a class="btn-gray" href="<%= request.getContextPath() %>/app/orderDetail/${orderList.orderCd}">상세보기</a>
			                			<a class="btn-w" href="#" onclick="changeOrder(${orderList.orderCd}, 32);">완료처리</a>
			                		</div>
					        	</div>
					        </c:if>
	               		</c:forEach>
	               	</c:if>
               		<p class="info-text">최근 24시간 주문만 표시 됩니다.</p>
           		</div>
	            <div class="tab-con">
	            	<c:if test="${completeOrder >= 1}">
	            		<c:forEach var="orderList" items="${orderList}">
	            			<!-- 24시간 이내 완료 주문 -->
	            			<fmt:parseDate var="orderDate" value="${orderList.orderDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				        	<fmt:parseNumber value="${orderDate.time / (1000*60)}" var="orderTime"/>
				        	<fmt:parseNumber value="${nowTime - orderTime}" var="timeDefference"/>
				        	<c:if test="${orderList.orderStatusCd eq '32' and (timeDefference > 0 and timeDefference <= 60*24)}">
								<c:if test="${orderList.cancelYn eq 'N'}">
					        		<div class="order-box s-complete">
					        			<a href="<%= request.getContextPath() %>/app/orderDetail/${orderList.orderCd}">
					        				<div class="top-info">
					        					<div class="room-nm">${orderList.storeRoomNm}</div>
					                        	<div class="time">${orderList.orderDate}</div>
					                        	<div class="state">완료</div>
					                     	</div>
					                     	<ul class="goods-li">
							                	<c:forEach var="smenus" items="${orderList.smenus}">
							                		<li>
							                			<div class="top">
							                				<span class="goods-nm">${smenus.smenuNmLan}</span>
								                         	<span class="od-ct">${smenus.cnt}</span>
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
					                    </a>
					                </div>
				                </c:if>
				                <c:if test="${orderList.cancelYn eq 'Y'}">
				                	<div class="order-box s-cancel">
					                	<a href="<%= request.getContextPath() %>/app/orderDetail/${orderList.orderCd}">
					                		<div class="top-info">
					                        	<div class="room-nm">${orderList.storeRoomNm}</div>
					                        	<div class="time">${orderList.orderDate}</div>
					                        	<div class="state">취소</div>
					                     	</div>
					                     	<ul class="goods-li">
					                     		<c:forEach var="smenus" items="${orderList.smenus}">
							                		<li>
							                			<div class="top">
							                				<span class="goods-nm">${smenus.smenuNmLan}</span>
								                         	<span class="od-ct">${smenus.cnt}</span>
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
					                    </a>
					               	</div>
				                </c:if>
				        	</c:if>
	            		</c:forEach>
	            	</c:if>
	            	<p class="info-text">최근 24시간 주문만 표시 됩니다.</p>
	            </div>
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
<!-- 	               	<li> -->
<%-- 	               		<a href="<%= request.getContextPath() %>/app/setting"> --%>
<%-- 	                     	<img src="<%= request.getContextPath() %>/app/img/i-menu2.png"> --%>
<!-- 	                     	<span>주문 알림설정</span> -->
<!-- 	                  	</a> -->
<!-- 	               	</li> -->
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
      	<div class="toast-popup">
      		새 주문이 들어 왔습니다.
      	</div>
   	</div>
</body>
</html>