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
	private static final long serialVersionUID = 1L;
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
		// Anti-cache headers. see
		// http://www.xyzws.com/JSPfaq/how-to-disable-browser-caching-for-a-specific-jsp/11
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

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
				BackendSession backendSession;
				backendSession = endpoint.signIn(emailAddress, password);

				if (backendSession == null) {
					// Authentication failed
					response.sendRedirect("/index.jsp?error=bad_credentials");
				} else {
					// Authentication succeeded. Create session and store
					// backend session token
					session = request.getSession(true);
					session.setAttribute("token", backendSession.token);

					// Redirect the user to the list of submissions
					if (backendSession.isAdmin) {
						response.sendRedirect("/list.jsp");
					} else {
						printWriter
								.print("This should have redirected to the normal user website");
					}
				}
			} else {
				// Session exists. Redirect the user to the appropriate site
				System.out.println("Something");
				if (session.getAttribute("token") != null) {
					endpoint.setBackendSessionToken((String) session
							.getAttribute("token"));
					BackendSession backendSession = endpoint
							.authenticateSession();

					if (backendSession != null && backendSession.isAdmin) {
						response.sendRedirect("/list.jsp");
					} else {
						printWriter
								.print("This should have redirected to the normal user website");
					}
				} else {
					printWriter
							.print("This should have redirected to the normal user website");
				}
			}

			break;
		case REGISTER:
			// It seems like this part is ignored after click the register
			// button --Zhibin
			emailAddress = request.getParameter("emailAddress");
			password = request.getParameter("password");
			printWriter.print("REGISTER CALLED WIT" + emailAddress + " "
					+ password);

			break;
		case UPLOADPOSTER:
			String registerEmail = null;
			String registerPassword = null;
			String registerConfirmPassword = null;
			
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iterator = upload.getItemIterator(request);
			
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				if (item.isFormField()) {
					String fieldName = item.getFieldName().toString();
					StringWriter writer = new StringWriter();
					IOUtils.copy(item.openStream(), writer, "UTF-8");
					String fieldValue = writer.toString();
					
					if (fieldName.equals("email")) {
						registerEmail = fieldValue;
					} else if (fieldName.equals("password")) {
						registerPassword = fieldValue;
					} else if (fieldName.equals("confirm")) {
						registerConfirmPassword = fieldValue;
					}
				} else {
				
				}
			}
			
			switch (endpoint.registerUser(registerEmail, registerPassword, registerConfirmPassword).result) {
				case RegisterResult.REGISTER_SUCCESS:
					response.sendRedirect("/index.jsp?error=register_success");
					break;
				case RegisterResult.REGISTER_ERROR_EMAIL_NOT_VALID:
					response.sendRedirect("/index.jsp?error=register_invalid_email");
					break;
				case RegisterResult.REGISTER_ERROR_EMAIL_NOT_AVAILABLE:
					response.sendRedirect("/index.jsp?error=register_unavailable_email");
					break;
				case RegisterResult.REGISTER_ERROR_PASSWORD_NOT_VALID:
					response.sendRedirect("/index.jsp?error=register_invalid_password");
					break;
				case RegisterResult.REGISTER_ERROR_PASSWORD_NOT_MATCH:
					response.sendRedirect("/index.jsp?error=register_password_not_match");
					break;
				default:
					response.sendRedirect("/index.jsp?error=register_other_error");
			}
			
			break;
		case LOGOUT:
			// Check if there is an existing session
			session = request.getSession(false);

			if (session != null) {
				if (session.getAttribute("token") != null) {
					endpoint.setBackendSessionToken((String) session
							.getAttribute("token"));
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