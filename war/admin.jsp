<%@ page session="false" %><%@ 
page import="ordappengine.*,java.util.ArrayList" %><%

// Anti-cache headers. see http://www.xyzws.com/JSPfaq/how-to-disable-browser-caching-for-a-specific-jsp/11
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);

// Check if session exists and is valid
HttpSession session = request.getSession(false);
NetworkEndpoint endpoint = new NetworkEndpoint();

if (session != null) {
	if (session.getAttribute("token") != null) {
		endpoint.setBackendSessionToken((String)session.getAttribute("token"));
		BackendSession backendSession = endpoint.authenticateSession();
		
		if (backendSession != null) {
			if (backendSession.isAdmin) {
		
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta content="text/html; charset=utf-8" http-equiv="Content-Type" />
<link href="css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="css/ord.css" media="screen" rel="stylesheet" type="text/css" />
<link href='http://fonts.googleapis.com/css?family=Roboto' rel='stylesheet' type='text/css'>
<link href='http://fonts.googleapis.com/css?family=Roboto:300' rel='stylesheet' type='text/css'>
<title>Computer Science</title>
</head>

<body>

<div id="menu-wrapper">
	<div id="menu-center">
		<a href="index.jsp"><img alt="" src="images/whitelogo.png" /></a>
	</div>
</div>
<div id="content-wrapper">
	<div id="content-window">
		<div id="side-pane">
			<form>		
				<input type="search" placeholder="Search by user" results="5" name="q" autosave="ord">
			</form>
			<div class="welcome-message">Sort by date</div>
				<ul>
					<li>Newest</li>
					<li>Oldest</li>
				</ul>
			<div class="welcome-message">Sort by status</div>
				<ul>
					<li>Submitted</li>
					<li>Processing</li>
					<li>Printed</li>
					<li>Finished</li>
					<li>Wrong Format/Size</li>
					<li>Custom</li>
				</ul>
		</div>
		<div id="content-pane">
			<p align="right"><b>Welcome <%= backendSession.emailAddress %> (<a href="NetworkServlet?actionIndex=<%= NetworkServlet.LOGOUT %>">Logout</a>)</b></p>
			<ul>
<%

// Here is where the list of submissions should be output
// ArrayList<Submission> submissions = endpoint.getAllSubmissions();

%>
				<li>
					<div class="list-username">bob@aol.com</div>
					<div class="list-filename">submission.ptx</div>
					
					<form>
						<input class="button" type="submit" value="Delete"/>
					</form>	
					<form>
						<input class="button" type="submit" value="Update status"/>
					</form>
					<form>
						<input class="button" type="submit" value="Download"/>
					</form>
					<select>
						<option>Submitted</option>
						<option>Processing</option>
						<option>Printed</option>
						<option>Finished</option>
						<option>Wrong Format/Size</option>
						<option>Custom</option>
					</select>
				</li>
				<li>
					<div class="list-username">jeff@gmail.com</div>
					<div class="list-filename">submission.ptx</div>
					
					<form>
						<input class="button" type="submit" value="Delete"/>
					</form>	
					<form>
						<input class="button" type="submit" value="Update status"/>
					</form>
					<form>
						<input class="button" type="submit" value="Download"/>
					</form>
					<select>
						<option>Submitted</option>
						<option>Processing</option>
						<option>Printed</option>
						<option>Finished</option>
						<option>Wrong Format/Size</option>
						<option>Custom</option>
					</select>
				</li>
				<li>
					<div class="list-username">john@uco.edu</div>
					<div class="list-filename">submission.ptx</div>
					
					<form>
						<input class="button" type="submit" value="Delete"/>
					</form>	
					<form>
						<input class="button" type="submit" value="Update status"/>
					</form>
					<form>
						<input class="button" type="submit" value="Download"/>
					</form>
					<select>
						<option>Submitted</option>
						<option>Processing</option>
						<option>Printed</option>
						<option>Finished</option>
						<option>Wrong Format/Size</option>
						<option>Custom</option>
					</select>
				</li>
				<li>
					<div class="list-username">greg@hotmail.com</div>
					<div class="list-filename">ord.pdf</div>
					
					<form>
						<input class="button" type="submit" value="Delete"/>
					</form>	
					<form>
						<input class="button" type="submit" value="Update status"/>
					</form>
					<form>
						<input class="button" type="submit" value="Download"/>
					</form>
					<select>
						<option>Submitted</option>
						<option>Processing</option>
						<option>Printed</option>
						<option>Finished</option>
						<option>Wrong Format/Size</option>
						<option>Custom</option>
					</select>
				</li>
			</ul>
		</div>
	</div>
</div>
<div id="footer-wrapper">
	<div id="menu-center">
		<img alt="" src="images/bwbroncho.png" /> 
		<div class="footer-content underlined">
			<a class="footer-link" href="#">Uconnect</a>
			<a class="footer-link" href="#">MSDN-AA [DreamSpark]</a>
			<a class="footer-link" href="#">D2L</a>
			<a class="footer-link" href="#">ACM Club</a>
		</div>
		<div class="footer-content">
			&copy; 2013 Department of Computer Science<br>
			100 North University Drive, Edmond, OK 73034 &middot; (405) 974-5717 <br>
			Designed by Stephen Staker
		</div>
	</div>
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