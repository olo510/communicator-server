<%@ page language="java" contentType="text/html; UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${conversation.name}</title>
<link href="<c:url value="/resources/bootstrap.css" />" rel="stylesheet">
<link href="<c:url value="/resources/style.css" />" rel="stylesheet">

</head>
<body style="background-image: url('<c:url value="/resources/static/img/background.png"/>');">
	<div>
		<input type"text" id="searchText" class="login input-sm"></input>
		<a href="#" onclick="searchMessage()" class="btn btn-main lcd fs-7">Szukaj</a>
	</div>
	<div id="message-container">
		<table>
			<tbody id=messages>
			</tbody>
		</table>
	</div>
	<form id="fileForm" method="POST" enctype="multipart/form-data" action="<c:url value="/files/upload/" />" class="mb-2">
		<input type="hidden" name="uuid" value="${conversation.uuid}">
		<input type="file" name="file" class="foo mr-1 ml-4 fs-7" style="float:left; display:inline;">
		<span id="fileName" class="bold"></span>
		<input value="SEND FILE" type="button" class="btn btn-main mb-1 lcd fs-7" style="height: 34px;" onclick="sendFile()"></input>
	</form>
	<div class="clear: both;"></div>
	<textarea id="message"
		onkeydown="if (event.keyCode == 13) { sendMessage(); return false;}"></textarea>
	<button class="btn btn-main mt--4 lcd fs-7" onclick="sendMessage()">SEND</button>
</body>
<script src="<c:url value="/resources/webjars/jquery/jquery.min.js" />"></script>
<script src="<c:url value="/resources/webjars/jquery/jquery.form.js" />"></script>
<script
	src="<c:url value="/resources/webjars/sockjs-client/sockjs.min.js" />"></script>
<script
	src="<c:url value="/resources/webjars/stomp-websocket/stomp.min.js" />"></script>
<script>
	let uuidConversation = "${conversation.uuid}"
	let nameConversation = "${conversation.name}"
	let idUser = "${user.id}"
	let messages = ${messages}
	let stompClient = null;
	let offset=10;
	let loaded=false;
	var lastScrollTop = 0
	
	function connect() {
	    var socket = new SockJS("<c:url value="/websocket" />");
	    stompClient = Stomp.over(socket);
	    stompClient.connect({}, function (frame) {
	        console.log('Connected: ' + frame);
	        stompClient.subscribe('/message/receiv/' +uuidConversation, function (message) {
	            showMessage(JSON.parse(message.body));
	        });
	    });
	}

	function disconnect() {
	    if (stompClient !== null) {
	        stompClient.disconnect();
	    }
	}

	function sendMessage() {
		let message = $("#message").val();
		$("#message").val('');
		if(message==null || message=="") return
	    stompClient.send("/app/message/send", {}, JSON.stringify({'content': message, 'writeTime': new Date().getTime(), 'idUser': idUser, 'uuidConversation': uuidConversation}));
	}
	
	function showMessage(message) {
		let tr = createTdFromMessage(message)
	    $("#messages").append(tr);
		$("#message-container").scrollTop($('#message-container').prop("scrollHeight"));
	}
	
    
	window.addEventListener('beforeunload', function (e) { 
		disconnect()
	});
	
	$( document ).ready(function() {
    	connect();
		loadPrevMessages(messages)
		$("#message-container").scrollTop($('#message-container').prop("scrollHeight"));
        loaded = true;
	});
	
	$("#message-container").scroll(function() {
		   var st = $(this).scrollTop();
		   if (st < lastScrollTop){
			   if(200>$(this).scrollTop() && loaded){
		    		loaded=false;
		    		getPreviousMessages()
		    	}
		   }
		   lastScrollTop = st;
    	
	});
	
	function getPreviousMessages(){
		offset+=10
		$.ajax({
			  url: "<c:url value="/messages/get/" />"+uuidConversation+"/"+offset, 
			  success : function(response) {
				let mess = JSON.parse(response)
				if(mess.length!=0){
					loadPrevMessages(mess)					
					loaded = true;
				} else{
					loaded = false;
				}
			  }
			});
	}

	function loadPrevMessages(mess){
		mess.forEach(m=> {
			let tr = createTdFromMessage(m)
			$("#messages").prepend(tr);
		})
		$("#message-container").scrollTop(700);
		loaded= true;
	}
	
	function addFoundedMessages(mess){
		$("#messages").empty()
		mess.forEach(m=> {
			let tr = createTdFromMessage(m)
			$("#messages").prepend(tr);
		})
		loaded= true;
	}
	
	function createTdFromMessage(message){
		let tr = document.createElement('tr');
		let td = document.createElement('td');
		let div = document.createElement('div');
		let time = document.createElement('span');
		let user = document.createElement('span');
		let content = document.createElement('div');
		let data = new Date(message.writeTime);
		time.textContent = data.toLocaleDateString() +" "+ data.toLocaleTimeString() + " "
		user.textContent = message.nick
		user.className+="bold"
		$(content).append($.parseHTML(message.content))
		div.className += "message "
		content.className += "message-content "
		if(message.idUser==idUser){
			content.className += "gray"
			div.className += "message-left";
			td.align = "left"
		}else{
			td.align = "right"
			content.className += "green"
			div.className += "message-right"			
		}
		div.appendChild(time)
		div.appendChild(user)
		div.appendChild(content)
		td.appendChild(div)
		tr.appendChild(td)
		return tr;
	}

	$( '.foo[type=file]' ).on( 'change', function updateFileName( event ){
	    $("#fileName").text( $(this).val())
	} ); 
	
	function sendFile(){
		$("#fileForm").ajaxSubmit({success: showResponse, error: errorResponse, headers:{"${_csrf.headerName}": "${_csrf.token}", "Accept": "*/*"}})
	}
	
	function showResponse(responseText, statusText, xhr, $form)  { 
		let a = document.createElement("a")
		a.textContent=responseText
    	a.download = responseText;
    	a.href = responseText;
		stompClient.send("/app/message/send", {}, JSON.stringify({'content': "<a href='#' onclick='downloadFile(\""+responseText+"\")'>"+responseText.replace(/^.*[\\\/]/, '')+"</a>", 'writeTime': new Date().getTime(), 'idUser': idUser, 'uuidConversation': uuidConversation}));
	}
	
	function errorResponse(){
		connector.showError("Nie można wysłać pliku")
	}
	
	function downloadFile(file){
		try{
			connector.downloadFile(file)		
		}catch(e){
			window.open(file)
		}
	}
	
	function searchMessage(){
		let content = $("#searchText").val();
		if(content == null || content == "") content = "i";
		loaded = false;
		$.ajax({
			  url: "<c:url value="/messages/search/" />"+uuidConversation+"/"+content, 
			  success : function(response) {
				let mess = JSON.parse(response)
				if(mess.length!=0){
					addFoundedMessages(mess)					
					loaded = true;
				} else{
					$("#messages").empty()
					offset=-10
					getPreviousMessages()
					loaded = false;
				}
			  }
			});
	}

	
</script>
</html>