<%@ page session="false" %><%@ 
page import="ordappengine.*,java.util.ArrayList" %><%

// Anti-cache headers. See:
// http://www.xyzws.com/JSPfaq/how-to-disable-browser-caching-for-a-specific-jsp/11
// http://stackoverflow.com/questions/49547/making-sure-a-web-page-is-not-cached-across-all-browsers
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
response.setHeader("Expires", "Sat, 26 Jul 1997 05:00:00 GMT");

// Check if session exists and is valid
HttpSession session = request.getSession(false);
NetworkEndpoint endpoint = new NetworkEndpoint();
BackendSession backendSession;

if (session != null) {
	if (session.getAttribute("token") != null) {
		endpoint.setBackendSessionToken((String)session.getAttribute("token"));
		backendSession = endpoint.authenticateSession();
		
		if (backendSession != null) {
			if (backendSession.isAdmin) {
				ArrayList<Submission> submissions = endpoint.getAllSubmissions();
				User editUser=null;
		 		String user = request.getParameter("q");
						 		
		 		if(user != null) {
		 			submissions=endpoint.getSubmissions(user);
					if(submissions.isEmpty())
					editUser = endpoint.getUser(user);
		 		}		 		
		 		String filter = request.getParameter("filter");		 		
		 		if(filter != null) {
		 			submissions=endpoint.filterSubmissions(filter);
		 		}		 		
		 		int sidePaneSize = 428;
		 		if(submissions.size() > 4){
		 			sidePaneSize += 76 * (submissions.size() - 4);
		 		}

%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta content="text/html; charset=utf-8" http-equiv="Content-Type" />
<link href="css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="css/ord.css" media="screen" rel="stylesheet" type="text/css" />
<link href='http://fonts.googleapis.com/css?family=Roboto' rel='stylesheet' type='text/css'>
<link href='http://fonts.googleapis.com/css?family=Roboto:300' rel='stylesheet' type='text/css'>
<title>Computer Science</title>
<script type="text/javascript">
	function showEdit(var1){
		document.getElementById("overlay").style.display = "block";
		document.getElementById("register-dialog").style.display = "block";
		document.getElementById("username").innerHTML=var1;
		document.getElementById("editUsername").value=var1;
	}
	function closeEdit(){
		document.getElementById("overlay").style.display = "none";
		document.getElementById("register-dialog").style.display = "none";
	}
	function showNotification(){
		document.getElementById("overlay").style.display = "block";
		document.getElementById("notification-dialog").style.display = "block";
	}
	function closeNotification(){
		document.getElementById("overlay").style.display = "none";
		document.getElementById("notification-dialog").style.display = "none";
	}
</script>
</head>

<body<% if (request.getParameter("msg") != null) { out.print(" onload=\"showNotification()\""); } %>>

<div id="menu-wrapper">
	<div id="menu-center">
		<img alt="" src="images/whitelogo.png" />
	</div>
</div>
<div id="content-wrapper">
	<div id="content-window">
		<div id="side-pane" style="height: <%=sidePaneSize%>px">
			<form method="post" action="NetworkServlet">		
				<input type="search" placeholder="Search by user" results="5" name="q" autosave="ord">
				<input type="hidden" name="actionIndex" value="<%=NetworkServlet.SEARCH%>"/>
			</form>
			<div class="welcome-message">Sort by date</div>
				<ul>
					<li onclick="newestForm.submit()">
						<form name="newestForm" method = "post" action ="NetworkServlet">
							<input type="hidden" name="actionIndex" value="<%=NetworkServlet.FILTER%>"/>
							<input type="hidden" name="filter" value ="newest"/>		
				 		</form>
						newest
					</li>
					<li onclick="oldestForm.submit()">
						<form name="oldestForm" method = "post" action ="NetworkServlet">
							<input type="hidden" name="actionIndex" value="<%=NetworkServlet.FILTER%>"/>
							<input type="hidden" name="filter" value ="oldest"/>		
				 		</form>
						oldest
					</li>
				</ul>
			<!--Sort by status list-->
			<div class="welcome-message">Sort by status</div>
				<ul>
					<li onclick="submittedForm.submit()">
						<form name="submittedForm" method = "post" action ="NetworkServlet">
							<input type="hidden" name="actionIndex" value="<%=NetworkServlet.FILTER%>"/>
							<input type="hidden" name="filter" value ="<%=Submission.SUBMITTED%>"/>		
				 		</form>
				 		<%=Submission.SUBMITTED%>
					</li>
					<li onclick="processingForm.submit()">
						<form name="processingForm" method = "post" action ="NetworkServlet">
							<input type="hidden" name="actionIndex" value="<%=NetworkServlet.FILTER%>"/>
							<input type="hidden" name="filter" value ="<%=Submission.PROCESSING%>"/>		
				 		</form>
				 		<%=Submission.PROCESSING%>
					</li>
					<li onclick="printedForm.submit()">
						<form name="printedForm" method = "post" action ="NetworkServlet">
							<input type="hidden" name="actionIndex" value="<%=NetworkServlet.FILTER%>"/>
							<input type="hidden" name="filter" value ="<%=Submission.PRINTED%>"/>		
				 		</form>
				 		<%=Submission.PRINTED%>
					</li>
					<li onclick="finishedForm.submit()">
						<form name="finishedForm" method = "post" action ="NetworkServlet">
							<input type="hidden" name="actionIndex" value="<%=NetworkServlet.FILTER%>"/>
							<input type="hidden" name="filter" value ="<%=Submission.FINISHED%>"/>		
				 		</form>
				 		<%=Submission.FINISHED%>
					</li>
					<li onclick="wrongFormatForm.submit()">
						<form name="wrongFormatForm" method = "post" action ="NetworkServlet">
							<input type="hidden" name="actionIndex" value="<%=NetworkServlet.FILTER%>"/>
							<input type="hidden" name="filter" value ="<%=Submission.WRONG_FORMAT_SIZE%>"/>		
				 		</form>
				 		<%=Submission.WRONG_FORMAT_SIZE%>
					</li>
					<li onclick="otherErrorsForm.submit()">
						<form name="otherErrorsForm" method = "post" action ="NetworkServlet">
							<input type="hidden" name="actionIndex" value="<%=NetworkServlet.FILTER%>"/>
							<input type="hidden" name="filter" value ="<%=Submission.OTHER_ERRORS%>"/>		
				 		</form>
				 		<%=Submission.OTHER_ERRORS%>
					</li>
					<li onclick="noneForm.submit()">	
						<form name="noneForm" method = "post" action ="/admin.jsp">			
				 		</form>	
				 		none
					</li>
				</ul>
		</div>
		<!--Header on admin page-->
		<div id="content-pane">
			<p align="right"><b>Welcome <%= backendSession.emailAddress %> (<a href="NetworkServlet?actionIndex=<%= NetworkServlet.LOGOUT %>">Logout</a>)</b></p>
			<p align="right">You have <%= submissions.size()%> poster(s) (<b><a href="admin.jsp">Refresh</a></b>)</p>

<ul>
<%if(submissions.isEmpty()){
	if(editUser!=null){
	%>
	<div class="list-username" id="myusername" onclick="showEdit('<%=editUser.emailAddress%>')"><% out.print(editUser.emailAddress);%></div>
	<div class="list-filename"><%out.print("This user has no submissions but, you can change their password by clicking on their email address");%></div>
	<%
	}else{
    out.print("Either you have no submissions, or your filter turned up zero results.");
	}
    }%>
	<%for(int i =0; i< submissions.size(); i++){%>
	<!--Submissions list-->
		<li>
		<!--Username and Edit-->
			<div class="list-username" id="myusername" onclick="showEdit('<%=submissions.get(i).username%>')"><% out.print(submissions.get(i).username);%></div>
			<!--File Name-->
			<div class="list-filename"><%=submissions.get(i).posterName%></div>
			<!--Delete button-->
					<form action="/NetworkServlet" class="formright">
						<input type="hidden" name="actionIndex" value="<%= NetworkServlet.DELETESUBMISSION %>"/>
						<input type="hidden" name="blobKey" value="<%= submissions.get(i).getBlobKey() %>"/>
						<input class="button" type="submit" value="Delete"/>
					</form>	
					<!--Download button-->
					<form class="formright" action="/ServeBlobServlet">
						<input class="button" type="submit" value="Download"/>
						<input type ="hidden" name ="blob-key" value = "<%= submissions.get(i).getBlobKey() %>"/>
					</form>
					<!--Update Status-->
					<form method="post" action="NetworkServlet">							
						<input type="hidden" name="blobKey" value="<%=submissions.get(i).getBlobKey()%>"/>
						<input type="hidden" name="actionIndex" value="<%=NetworkServlet.UPDATE%>"/>
						<select onchange="this.form.submit()" name = "status">
							<option value="<%=Submission.SUBMITTED%>" <% if(submissions.get(i).posterStatus.equals(Submission.SUBMITTED)){ out.print("SELECTED");}%>><%=Submission.SUBMITTED%> </option>
							<option value="<%=Submission.PROCESSING%>"<% if(submissions.get(i).posterStatus.equals(Submission.PROCESSING)){ out.print("SELECTED");}%>><%=Submission.PROCESSING%></option>
							<option value="<%=Submission.PRINTED%>" <% if(submissions.get(i).posterStatus.equals(Submission.PRINTED)){ out.print("SELECTED");}%>><%=Submission.PRINTED%></option>
							<option value="<%=Submission.FINISHED%>" <% if(submissions.get(i).posterStatus.equals(Submission.FINISHED)){ out.print("SELECTED");}%>><%=Submission.FINISHED%></option>
							<option value="<%=Submission.WRONG_FORMAT_SIZE%>" <% if(submissions.get(i).posterStatus.equals(Submission.WRONG_FORMAT_SIZE)){ out.print("SELECTED");}%>><%=Submission.WRONG_FORMAT_SIZE%></option>
							<option value="<%=Submission.OTHER_ERRORS%>" <% if(submissions.get(i).posterStatus.equals(Submission.OTHER_ERRORS)){ out.print("SELECTED");}%>><%=Submission.OTHER_ERRORS%></option>
						</select>
					</form>
					</li>
			 <%}%>			
			</ul>
		</div>
	</div>
</div>
<div id="footer-wrapper">
	<div id="menu-center">
		<img alt="" src="images/bwbroncho.png" /> 
		<div class="footer-content underlined">
			<a class="footer-link" href="http://uconnect.uco.edu">Uconnect</a>
			<a class="footer-link" href="https://www.dreamspark.com/">MSDN-AA [DreamSpark]</a>
			<a class="footer-link" href="http://learn.uco.edu">D2L</a>
			<a class="footer-link" href="http://cs2.uco.edu/~acm/">ACM Club</a>
		</div>
		<div class="footer-content">
			&copy; 2013 Department of Computer Science<br>
			100 North University Drive, Edmond, OK 73034 &middot; (405) 974-5717 <br>
			Designed by Stephen Staker
		</div>
	</div>
</div>
<div id="overlay"></div>
<div id="notification-dialog">
	<div id="notification-area"><p><%
	
if (request.getParameter("msg") != null) {
	if (request.getParameter("msg").equals("delete_success")) {
		out.print("The poster was deleted successfully!");
	} else if (request.getParameter("msg").equals("delete_unprivileged")) {
		out.print("You do not have enough privileges to delete this poster.");
	} else if (request.getParameter("msg").equals("delete_unspecified")) {
		out.print("The poster could not be deleted. Please try again. An unspecified error has occurred.");
	} else if (request.getParameter("msg").equals("delete_not_exists")) {
		out.print("The poster could not be deleted because it no longer exists.");
	}else if (request.getParameter("msg").equals("edit_success")) {
		out.print("Password has been successfully updated");
	}else if (request.getParameter("msg").equals("edit_failed")) {
		out.print("Warning: Password update was unsuccessful.");
	}
}
			
%></p><p><input class="button" type="button" value="Ok" onclick="closeNotification()" /></p></div>
</div>
<!--Edit user pop up form-->
<div class="dialog" id="register-dialog">
	<form id="register-form" method="post" action="NetworkServlet?actionIndex=<% out.print(NetworkServlet.EDIT); %>">
			<img src="images/exit.png" onClick="closeEdit()"/>
			<p>Editing User:</p>
			<p id="username"></p>		
			<p>New Password</p>
			<p><input placeholder="New Password" name="password"/></p>			
			<p><input class="button" type="submit" value="Submit Edit"/></p>
			<input type = "hidden" id="editUsername" name="username"/>
	</form>
</div>
</body>

</html><%
			} else {
				// User is not an administrator
				response.sendRedirect("/user.jsp");
			}
		} else {
			// Session does not exist
			response.sendRedirect("/index.jsp?msg=no_session");
		}
	} else {
		// Token is not stored in HTTP session. Logout
		response.sendRedirect("/NetworkServlet?actionIndex=" + NetworkServlet.LOGOUT);
	}
} else {
	// Session does not exist
	response.sendRedirect("/index.jsp?msg=no_session");
}

%>
