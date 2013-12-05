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
		 		String user = request.getParameter("q");
		 		
		 		if(user != null) {
		 			submissions=endpoint.getSubmissions(user);
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
</head>

<body>

<div id="menu-wrapper">
	<div id="menu-center">
		<a href="index.jsp"><img alt="" src="images/whitelogo.png" /></a>
	</div>
</div>
<div id="content-wrapper">
	<div id="content-window">
		<div id="side-pane" style="height:<%=sidePaneSize%>">
			<form method="post" action="NetworkServlet">		
				<input type="search" placeholder="Search by user" results="5" name="q" autosave="ord">
				<input type="hidden" name="actionIndex" value="<%=NetworkServlet.SEARCH%>"/>
			</form>
			<div class="welcome-message">Sort by date</div>
				<ul>
					<li>newest</li>
					<li>oldest</li>
				</ul>
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
		<div id="content-pane">
			<p align="right"><b>Welcome <%= backendSession.emailAddress %> (<a href="NetworkServlet?actionIndex=<%= NetworkServlet.LOGOUT %>">Logout</a>)</b></p>

<ul>
<%if(submissions.size()==0){
    out.print("Either you have no submissions, or your filter turned up zero results.");
    }%>
	<%for(int i =0; i< submissions.size(); i++){%>
		<li>
			<div class="list-username"><% out.print(submissions.get(i).username);%></div>
			<div class="list-filename"><%=submissions.get(i).posterName%></div>
					<form class="formright">
						<input class="button" type="submit" value="Delete"/>
					</form>	
					<form class="formright">
						<input class="button" type="submit" value="Download"/>
					</form>
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