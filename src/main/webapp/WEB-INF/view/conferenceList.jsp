<%@ page language="java" contentType="text/html; UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div id='conferenceList' class="col-sm-6">
	<h5 class="color-text lcd">Conferences</h5>
	<hr class="dashed-line">
	<table>
		<thead>
			<tr>
				<th></th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${conferences}" var="conference">
				<tr>
					<td ondblclick='openConversation("${conference.uuid}")' class="noselect pointer color-text conference"><i class="demo-icon icon-anonymouses"></i><c:out
							value="${conference.name}" /><a href="#" style="float: right;" onclick='removeUserFromConversation("${conference.uuid}")'><i class="demo-icon icon-user-delete delete-btn"></i></a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>