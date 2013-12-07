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

if (session != null) {
	if (session.getAttribute("token") != null) {
		endpoint.setBackendSessionToken((String)session.getAttribute("token"));
		BackendSession backendSession = endpoint.authenticateSession();
		
		if (backendSession != null) {
			if (!backendSession.isAdmin) {
		
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
		document.getElementById("register-edit").style.display = "block";
		document.getElementById("editUsername").value=var1;
	}
	function closeEdit(){
		document.getElementById("overlay").style.display = "none";
		document.getElementById("register-edit").style.display = "none";
	}
	function showRegistration(){
		document.getElementById("overlay").style.display = "block";
		document.getElementById("register-dialog").style.display = "block";
	}
	function closeRegistration(){
		document.getElementById("overlay").style.display = "none";
		document.getElementById("register-dialog").style.display = "none";
	}
</script>
</head>

<body<% if (request.getParameter("msg") != null) { out.print(" onload=\"showNotification()\""); } %>>

<div id="menu-wrapper">
	<div id="menu-center">
		<a href="index.html"><img alt="" src="images/whitelogo.png" /></a>
	</div>
</div>
<div id="content-wrapper">
	<div id="content-window">
		<div id="side-pane">
			<div class="welcome-message">
				These are instructions to use our system. You do not know how to use the system so these are instructions to use the system.
			</div>		
		</div>
		<%
// Here is where the list of submissions should be output
  ArrayList<Submission> submissions = endpoint.getSubmissions(backendSession.emailAddress);
%>
		<div id="content-pane">
		<div align="right"
			<p><b>Welcome </b><a href="javascript:void(0)" onclick="showEdit('<%=backendSession.emailAddress%>')"><% out.print(backendSession.emailAddress);%></a><b> (<a href="NetworkServlet?actionIndex=<%= NetworkServlet.LOGOUT %>">Logout</a>)</b></p>
			</div>
			<div id="upload-area">
				<p>Click on the button below to upload a poster. You may upload .pdf, .ppt, .pptx, .jpg, or .png files that are 7 MB or smaller.</p>
				<p><input type="button" class="button" value="Upload Poster" onclick="showRegistration()"></p>
			</div>
			<p align="right">You have <%= submissions.size()%> poster(s) (<b><a href="user.jsp">Refresh</a></b>)</p>
			<ul>
			
	<%if(submissions.size()>0){
	 for(int i =0; i< submissions.size(); i++){%>
		<li>
			<div class="list-username"><%=submissions.get(i).posterName%></div>
			<div class="list-filename"><%=submissions.get(i).posterStatus%></div>
				<form action="/NetworkServlet" class="formright">
					<input type="hidden" name="actionIndex" value="<%= NetworkServlet.DELETESUBMISSION %>"/>
					<input type="hidden" name="blobKey" value="<%= submissions.get(i).getBlobKey() %>"/>
					<input class="button" type="submit" value="Delete"/>
				</form>	
				<form action="/ServeBlobServlet" class="formright">
					<input type = "hidden" name="blob-key" value ="<%=submissions.get(i).getBlobKey()%>"/>
					<input class="button" type="submit" value="Download"/>
				</form>	
		</li>
	<%}
	}else{%>	
	  <li>
	    <div class="list-username">You have no submissions, try using the upload button.</div>
	  </li>
	  <%}%>
			</ul>
			<div style="clear: both">&nbsp;</div>
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
<div class="dialog" id="register-dialog">
	<form id="register-form" method="post" action="NetworkServlet?actionIndex=<% out.print(NetworkServlet.UPLOADPOSTER); %>" enctype="multipart/form-data">
			<img src="images/exit.png" onClick="closeRegistration()"/>
			<p>Select your poster file</p>
			<p><input type="file" name="uploadPoster" id="file"/></p>
			<p><input class="button" type="submit" value="Upload Poster"/></p>		
	</form>
</div>
<div id="notification-dialog">
	<div id="notification-area"><p><%
	
if (request.getParameter("msg") != null) {
	if (request.getParameter("msg").equals("upload_success")) {
		out.print("Your poster was uploaded! If you cannot see your poster in the list, please wait a few seconds and click on <u>Refresh</u>.");
	} else if (request.getParameter("msg").equals("upload_invalid_type")) {
		out.print("Your poster was not uploaded. Please try again. Make sure it is one of the following types: .pdf, .ppt, .pptx, .jpg, or .png.");
	} else if (request.getParameter("msg").equals("upload_max_size")) {
		out.print("Your poster was not uploaded. Please try again. Make sure it is no larger than 7 MB.");
	} else if (request.getParameter("msg").equals("upload_backend_error")) {
		out.print("Your poster was not uploaded. Please try again. An unspecified error has occurred.");
	} else if (request.getParameter("msg").equals("upload_no_file")) {
		out.print("You did not select a file to upload!");
	} else if (request.getParameter("msg").equals("delete_success")) {
		out.print("The poster was deleted successfully!");
	} else if (request.getParameter("msg").equals("delete_unprivileged")) {
		out.print("You do not have enough privileges to delete this poster.");
	} else if (request.getParameter("msg").equals("delete_unspecified")) {
		out.print("The poster could not be deleted. Please try again. An unspecified error has occurred.");
	} else if (request.getParameter("msg").equals("delete_status_conflict")) {
		out.print("The poster could not be deleted because its status is no longer <i>Submitted</i>.");
	} else if (request.getParameter("msg").equals("delete_not_exists")) {
		out.print("The poster could not be deleted because it no longer exists.");
	}else if (request.getParameter("msg").equals("edit_password_match_error")) {
		out.print("Passwords did not match");
	}else if (request.getParameter("msg").equals("edit_success")) {
		out.print("Your password has been successfully updated!");
	}else if (request.getParameter("msg").equals("edit_failed")) {
		out.print("Warning: Your password could not be updated.  If this error persists contact an administrator.");
	}
}
			
%></p><p><input class="button" type="button" value="Ok" onclick="closeNotification()" /></p></div>
</div>
<!--Edit user pop up form-->
<div class="dialog" id="register-edit">
	<form id="edit-form" method="post" action="NetworkServlet?actionIndex=<% out.print(NetworkServlet.EDIT); %>">
			<img src="images/exit.png" onClick="closeEdit()"/>	
			<p>New Password</p>
			<p><input type="password" placeholder="New Password" name="password"/></p>		
			<p><input type="password" placeholder="Confirm Password" name="confirm"/></p>				
			<p><input class="button" type="submit" value="Submit Edit"/></p>
			<input type = "hidden" id="editUsername" name="username"/>
	</form>
</div>
</body>

</html><%
			} else {
				// User is not a normal user
				response.sendRedirect("/admin.jsp");
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
