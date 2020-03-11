<%@ page language="java" contentType="text/html; UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />

<title>Komunikator</title>
<link href="<c:url value="/resources/bootstrap.css" />" rel="stylesheet">
<link href="<c:url value="/resources/style.css" />" rel="stylesheet">
<link href="<c:url value="/resources/css/fontello-codes.css" />" rel="stylesheet">

</head>
<body
	style="background-image: url('<c:url value="/resources/static/img/background.png"/>');">
	<img src="<c:url value="/resources/static/img/napis.png"/>"
		width="300px" class="mx-auto d-block">
	<div class="row mt-3">
		<div id='userList' class='col-sm-6'>
		<h5 class="color-text lcd">Comrades</h5>
		<hr class="dashed-line">
			<table>
				<thead>
					<tr>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${users}" var="user">
						<tr id="tr-<c:out value="${user.nick}"/>"
							onclick="doubleclick(this,'<c:out value="${user.nick}"/>')">
							<td class="noselect pointer color-text"><i class="demo-icon icon-anonymous"></i><c:out value="${user.nick}" /></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
		<jsp:include page="./conferenceList.jsp"></jsp:include>
	</div>
	<hr class="dashed-line">
	<div id="conversationControl" style="display: none;">
		<div id="conversationList"></div>
		<input name="conversationName" class="login"
			placeholder="Conference title" onkeydown="if (event.keyCode == 13) { startConversationBtnClick(); return false;}"/>
		<button onclick="startConversationBtnClick()" class="btn btn-lg btn-block btn-main lcd">Call</button>
	</div>
</body>
<script src="<c:url value="/resources/webjars/jquery/jquery.min.js" />"></script>
<script>

	let idUser = ${idUser}
	let uName = "<c:out value="${uName}"/>"
	let stompClient = null;
	

	function connect() {
	    var socket = new SockJS("<c:url value="/websocket" />");
	    stompClient = Stomp.over(socket);
	    stompClient.connect({}, function (frame) {
	        stompClient.subscribe('/message/receiv/' +idUser, function (message) {
	        	let body = JSON.parse(message.body);
	        	if(body.idUser!=idUser)
	        		processMessage(body)	
	        });
	    });
	}

	function disconnect() {
	    if (stompClient !== null) {
	        stompClient.disconnect();
	    }
	}
	
	function processMessage(message){
		openConversation(message.uuidConversation)
		showNotify(message);
	}

	function showNotify(message) {
		let data = new Date(message.writeTime)
		try{
			connector.notify(data.toLocaleTimeString()+" " + message.nick, message.content)		
		}catch(e){
			console.log(e)
		}
	}
	
	function openConversation(uuidConversation) {
		try{
		  	window.connector.openConversation(uuidConversation)					  
		  }catch(e){
			  openWindow(uuidConversation)
		  }
	}
	
	function openWindow(uuidConversation){
		window.open("<c:url value="/panel/openConversation/" />"+uuidConversation);
	}
	
	window.addEventListener('beforeunload', function (e) { 
		disconnect()
	});
	
	function doubleclick(el, nick) {
		if (el.getAttribute("data-dblclick") == null) {
			el.setAttribute("data-dblclick", 1);
			setTimeout(function() {
				if (el.getAttribute("data-dblclick") == 1) {
					toggleUser(el, nick);
					if( $('#conversationList').children().length > 1) {
						$("#conversationControl").show()						
					}else{
						$("#conversationControl").hide()
					}
				}
				el.removeAttribute("data-dblclick");
			}, 300);
		} else {
			el.removeAttribute("data-dblclick");
			let nicks = [];
			nicks.push(nick)
			clearUserList();
			startConversation(nicks, nick);
		}
	}
	
	function startConversationBtnClick(){
		let childs = $("#conversationList").children()
		let users = []
		for (var i = 0; i < childs.length; i++){
		    users.push(childs[i].id.replace('nick-',''));
			
		}
		startConversation(users, $("input[name='conversationName']").val(), true)
	}
	
	function toggleUser(element, nick) {
		if ($('#conversationList').find('#nick-' + nick).length == 1) {
			removeUser(nick)
			$(element).removeClass("selected")
		} else {
			addUser(nick)
			$(element).addClass("selected")
		}
	}

	function startConversation(nicks, name, refreshConferences) {
		$.ajax({
			  url: "<c:url value="/panel/createConversation/" />",
			  headers: {          
			    'Accept': "*/*", 
			    "Content-Type": "application/json",
			    "${_csrf.headerName}": "${_csrf.token}"
			  }, 
			  type: 'POST',
			  data: JSON.stringify({"name": name,"nicks":nicks}),    
			  success : function(uuid) { 
				  openConversation(uuid);
				  clearUserList()
				  if(refreshConferences){
					  refreshConferenceList()
				  }
			  }
			});
	}
	
	function removeUserFromConversation(uuid){
		var token = $("meta[name='_csrf']").attr("content");
		var header = $("meta[name='_csrf_header']").attr("content");
		$.ajax({
			  url: "<c:url value="/panel/removeUser/" />",
			  headers: {          
			    'Accept': "*/*", 
			    "Content-Type": "application/json",
			    "${_csrf.headerName}": "${_csrf.token}"
			  }, 
			  type: 'POST',
			  data: uuid,    
			  success : function(html) { 
				  $("#conferenceList").replaceWith(html)
				  try{
					  connector.closeConversation(uuid);				  
				  }catch(e){console.log(e)}
			  }
			});
	}
	

	function addUser(nick) {
		let a = document.createElement('a');
		a.textContent = nick
		a.href="#"
		a.id="nick-"+nick
		a.classList+="badge badge-dark"
		a.onclick=function(event){
			$(event.target).remove();
			$("#tr-"+nick).removeClass("selected")
			if( $('#conversationList').children().length > 1) {
				$("#conversationControl").show()						
			}else{
				$("#conversationControl").hide();
			}
		}
		$('#conversationList').append(a);
	}
	
	function removeUser(nick) {
		$("#nick-"+nick).remove();
	}
	
	function clearUserList() {
		$(".selected").removeClass("selected")
		$("#conversationControl").hide()
		$('#conversationList').empty();
		$("input[name='conversationName']").val("")
		
	}
	
	function changeWindowName(){
		try{
			connector.setName(uName)			
		}catch(e){console.log(e)}
	}
	
	$( document ).ready(function() {
		connect();
	});
	
	function refreshConferenceList(){
		$.get("<c:url value="/panel/getConferences/" />", function(data, status){
			$("#conferenceList").replaceWith(data)
		});
	}

	
</script>
<script
	src="<c:url value="/resources/webjars/sockjs-client/sockjs.min.js" />"></script>
<script
	src="<c:url value="/resources/webjars/stomp-websocket/stomp.min.js" />"></script>
</html>