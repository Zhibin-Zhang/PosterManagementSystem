package ordappengine;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

public class NetworkServlet extends HttpServlet {
	public static final int SIGNIN = 0;
	public static final int REGISTER = 1;
	public static final int UPLOADPOSTER = 2;
	public static final int DELETESUBMISSION = 3;
	public static final int GETSUBMISSIONS = 4;
	public static final int LOGOUT = 5;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			processRequest(req, resp);
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			processRequest(req, resp);
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws FileUploadException,
			IOException {
		PrintWriter printWriter = null;
		
		try {
			printWriter = response.getWriter();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		int actionIndex = Integer.parseInt(request.getParameter("actionIndex"));
		
		String password = "";
		String emailAddress = "";

		NetworkEndpoint endpoint = new NetworkEndpoint();
		HttpSession session;
		
		switch (actionIndex) {
		case SIGNIN:
			// Obtain entered information
			emailAddress = request.getParameter("emailAddress");
			password = request.getParameter("password");
			
			// Check if there is an existing session
			session = request.getSession(false);
			
			if (session == null) {
				// Session does not exist. Authenticate
				BackendSession backendSession = endpoint.signIn(emailAddress, password);
				
				if (backendSession.token == null) {
					// Authentication failed
					response.sendRedirect("/index.jsp?error=bad_credentials");
				} else {
					// Authentication succeeded. Create session and store backend session token
					session = request.getSession(true);
					session.setAttribute("token", backendSession.getToken());
					
					// Redirect the user to the list of submissions
					response.sendRedirect("/list.jsp");
				}
			} else {
				// Session exists. Redirect the user to the list of submissions
				response.sendRedirect("/list.jsp");
			}
			break;
		case REGISTER:
			//It seems like this part is ignored after click the register button --Zhibin
			emailAddress = request.getParameter("emailAddress");
			password = request.getParameter("password");
			printWriter.print("REGISTER CALLED WIT" + emailAddress + " "
					+ password);

			break;
		case UPLOADPOSTER:
			response.setContentType("text/plain");
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iterator = upload.getItemIterator(request);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				if (item.isFormField()) {
					StringWriter writer = new StringWriter();
					IOUtils.copy(item.openStream(), writer, "UTF-8");
					String theString = writer.toString();
					printWriter.println(theString);
				}
				else{
					
				}
			}
			break;
		case LOGOUT:
			// Check if there is an existing session
			session = request.getSession(false);
			
			if (session != null) {
				if (session.getAttribute("token") != null) {
					endpoint.setBackendSessionToken((String)session.getAttribute("token"));
					endpoint.logout();
				}
				
				// Invalidate HTTP session
				session.invalidate();
			}
			
			response.sendRedirect("/index.jsp");
			break;
		}
	}
}