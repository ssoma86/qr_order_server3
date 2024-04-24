<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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

	System.out.println("***** start app login.jsp [" + indexDate + "] ***** ");
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
	System.out.println("***** end app login.jsp [" + indexDate + "] ***** ");
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
			//로그인 오류처리
			if("${resultCode}" == "0001" || "${resultCode}" == "0002") {
   				alert("아이디 또는 비밀번호 불일치");
   				window.location.replace("<%= request.getContextPath() %>/app/login");
   			} else if ("${resultCode}" == "9999") {
   				alert("시스템오류 재시도필요");
   				window.location.replace("<%= request.getContextPath() %>/app/login");
   			}
   			
   			//로그인처리
   			$('.goLogin').click(function() {
   				if(validateData('login')) {
	   				$("#loginFrm").submit();   					
   				}
   			});
   			
   			//비밀번호찾기
   			$('.find-user').click(function() {
   				if(validateData('find')) {
	   				var findData =  new Object();
	   				findData.accountId = $('#findId').val();
	   				findData.email = $('#findEmail').val();
	   				
	   				$.ajax({
						type : "POST",
						contentType : "application/json; charset=utf-8;",
						dataType : "json",
						url : "<%=request.getContextPath()%>/app/findUser",
						async : false,
						data : JSON.stringify(findData),
						cache : false,
						success : function(data) {
							alert(data.resultMsg);
						},
						error : function(data) {
							alert(data.resultMsg);
						}
					});
   				}
   			});
   			
   			//아이디, 비밀번호 입력체크
   			function validateData(type) {
   				if(type == 'login') {
	   				var userId = $("#userId").val().trim();
	   				var userPw = $("#userPw").val();
	   				
	   				if(userId == '' || userId == undefined || userId == null) {
	   					alert("아이디를 입력하세요.");
	   					$("#userId").focus();
	   					
	   					return false;
	   				}
	   				
	   				if(userPw == '' || userPw == undefined || userPw == null) {
	   					alert("비밀번호를 입력하세요.");
	   					$("#userPw").focus();
	   					
	   					return false;
	   				}
	   				
	   				return true;
   				} else if(type = 'find') {
   					var findId = $('#findId').val().trim();
   	   				var findEmail = $('#findEmail').val().trim();
   	   				
   	   				if(findId == '' || findId == undefined || findId == null) {
   	   					alert("아이디를 입력하세요.");
   	   					$("#findId").focus();
   	   					
   	   					return false;
   	   				}
   	   				
   	   				if(findEmail == '' || findEmail == undefined || findEmail == null) {
   	   					alert("이메일을 입력하세요.");
   	   					$("#findEmail").focus();
   	   					
   	   					return false;
   	   				}
   	   				
   	   				if(findEmail.indexOf('@') == -1) {
   	   					alert("이메일형식에 맞지 않습니다.");
	   					$("#findEmail").focus();
	   					
	   					return false;
   	   				}
   	   				
   	   				var check = true;
   	   				for(var i = 0; i < findEmail.length; i++) {
   	   					if(!alphabetCheck(findEmail.charAt(i)) && !numberCheck(findEmail.charAt(i)) && !specialCheck(findEmail.charAt(i))) {
   	   						check = false;
   	   						break;
   	   					}
   	   				}
   	   				
   	   				if(!check) {
	   	   				alert("이메일형식에 맞지 않습니다.");
	   					$("#findEmail").focus();
	   					
	   					return false;
   	   				}
   	   				
   	   				$('.popup-find-pw').removeClass('on');
   	   				return true;
   				}
   			}
   			
   			function alphabetCheck(str) {
   				var alphaStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
   				if(alphaStr.indexOf(str) == -1) return false;
   				else return true;
   			}
   			
   			function numberCheck(str) {
   				var numberStr = "0123456789";
   				if(numberStr.indexOf(str) == -1) return false;
   				else return true;
   			}
   			
   			function specialCheck(str) {
   				var specialStr = "_-@.";
   				if(specialStr.indexOf(str) == -1) return false;
   				else return true;
   			}
   		});
	</script>
</head>
<body style="background:#eee;">
	<div class="wrap login-wrap">
		<form action="/app/loginProc" name="loginFrm" id="loginFrm" method="POST">
			<section class="content">
				<div class="login-title">
					<img src="<%= request.getContextPath() %>/app/img/logo.png" class="login-logo">
					<h1>메뉴플러스 주문관리</h1>
				</div>
				<div class="login-box">
					<div class="input-wrap">
						<label for="userId">아이디</label>
						<input type="text" id="userId" name="userId">
					</div>
					<div class="input-wrap">
						<label for="userPw">비밀번호</label>
						<input type="password" id="userPw" value="" name="userPw">
					</div>
					<div class="dp-flex">
						<div class="checkbox-wrap">
							<input type="checkbox" name="autoLogin" id="autoLogin"><label for="autoLogin">자동 로그인</label>
						</div>
						<a href="#" class="find-pw-btn popup-trigger find-pw" popup-class=".popup-find-pw">비밀번호 찾기</a>
					</div>
				</div>
				<div class="btn-wrap">
					<a href="#" class="btn btn-l btn-d-gray goLogin">로그인</a>
				</div>
			</section>
		</form>
		<div class="popup-wrap popup-find-pw">
			<div class="popup">
				<div class="popup-con">
					<h3 class="notice-text">
						아이디와 이메일 확인 후<br>이메일로 임시 비밀번호를<br>발송해 드립니다.
					</h3>
					<div class="input-wrap">
						<label for="findId">아이디</label>
						<input type="text" id="findId" name="findId">
					</div>
					<div class="input-wrap">
						<label for="findEmail">이메일</label>
						<input type="text" id="findEmail" name="findEmail">
					</div>
				</div>
				<div class="btn-wrap">
					<a href="#" class="btn btn-l btn-wm btn-gray popup-close">닫기</a>
					<ahref="#" class="btn btn-l btn-wm btn-d-gray find-user">비밀번호 찾기</a>
				</div>
			</div>
		</div>
	</div>
</body>
</html>