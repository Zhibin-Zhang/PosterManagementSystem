package ordappengine;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class NetworkServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final int SIGNIN = 0;
	public static final int REGISTER = 1;
	public static final int UPLOADPOSTER = 2;
	public static final int DELETESUBMISSION = 3;
	public static final int GETSUBMISSIONS = 4;
	public static final int LOGOUT = 5;
	public static final int UPDATE = 6;

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
		BackendSession backendSession;
		HttpSession session;

		switch (actionIndex) {
		case SIGNIN:
			// Obtain entered information
			emailAddress = request.getParameter("emailAddress");
			password = request.getParameter("password");

			// Check if there is an existing session
			session = request.getSession(false);

			// If there is an existing session, log out first
			if (session != null) {
				if (session.getAttribute("token") != null) {
					endpoint.setBackendSessionToken((String) session
							.getAttribute("token"));
					endpoint.logout();
				}

				// Invalidate HTTP session
				session.invalidate();
			}

			// Authenticate
			backendSession = endpoint.signIn(emailAddress, password);

			if (backendSession == null) {
				// Authentication failed
				response.sendRedirect("/index.jsp?msg=bad_credentials");
			} else {
				// Authentication succeeded. Create session and store
				// backend session token
				session = request.getSession(true);
				session.setAttribute("token", backendSession.token);

				// Redirect the user to the list of submissions
				/**
				 * Added the lines here to get the submissions before redirect.
				 * no longer need these lines, pulling from the endpoint on the webpage
				 * - Matt
				 */
				if (backendSession.isAdmin) {
					//endpoint.getAllSubmissions();
					response.sendRedirect("/admin.jsp");
				} else {
					//endpoint.getSubmissions(emailAddress);
					response.sendRedirect("/user.jsp");
				}
			}

			break;
		case UPLOADPOSTER:
			boolean registerFirst = true;
			
			// Check if there is an existing session
			session = request.getSession(false);

			// If there is an existing session, make sure it's valid
			backendSession = null;
			
			if (session != null) {
				if (session.getAttribute("token") != null) {
					endpoint.setBackendSessionToken((String)session.getAttribute("token"));
					backendSession = endpoint.authenticateSession();
					
					if (backendSession != null) {
						if (!backendSession.isAdmin) {
							registerFirst = false;
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
			}
			
			// Obtain registration information
			String registerEmail = null;
			String registerPassword = null;
			String registerConfirmPassword = null;
			ByteArrayBody file = null;

			boolean validFile = true;
			boolean registerSuccessful = false;

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
					// Validate the type file
					if (!item.getContentType().equals("image/jpeg")
							&& !item.getContentType().equals(
									"application/vnd.ms-powerpoint")
							&& !item.getContentType()
									.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")
							&& !item.getContentType().equals("application/pdf")
							&& !item.getContentType().equals("image/png")) {
						validFile = false;
						break;
					}

					// Read the file into a ByteArrayOutputStream:
					// http://stackoverflow.com/questions/9375697/process-binary-file-in-java-using-fileitemstream
					InputStream is = new BufferedInputStream(item.openStream());
					ByteArrayOutputStream out = new ByteArrayOutputStream();

					try {
						out = new ByteArrayOutputStream();
						int data = -1;
						while ((data = is.read()) != -1) {
							out.write(data);
						}
					} catch (Exception e) {
						System.out.println(e.getMessage());
					} finally {
						out.flush();
						is.close();
						out.close();
					}

					// Put the file in a ByteArrayBody for later
					file = new ByteArrayBody(out.toByteArray(), item.getName());
					
					// Check the file size
					if (out.toByteArray().length > 1024 * 1024) {
						validFile = false;
						break;
					}
				}
			}

			if (!validFile) {
				response.sendRedirect("/index.jsp?msg=register_invalid_file");
				break;
			}

			if (registerFirst) {
				// Create user first
				switch (endpoint.registerUser(registerEmail, registerPassword,
						registerConfirmPassword).result) {
				case RegisterResult.REGISTER_SUCCESS:
					response.sendRedirect("/index.jsp?msg=register_success");
					registerSuccessful = true;
					break;
				case RegisterResult.REGISTER_ERROR_EMAIL_NOT_VALID:
					response.sendRedirect("/index.jsp?msg=register_invalid_email");
					break;
				case RegisterResult.REGISTER_ERROR_EMAIL_NOT_AVAILABLE:
					response.sendRedirect("/index.jsp?msg=register_unavailable_email");
					break;
				case RegisterResult.REGISTER_ERROR_PASSWORD_NOT_VALID:
					response.sendRedirect("/index.jsp?msg=register_invalid_password");
					break;
				case RegisterResult.REGISTER_ERROR_PASSWORD_NOT_CONFIRMED:
					response.sendRedirect("/index.jsp?msg=register_password_not_confirmed");
					break;
				case RegisterResult.REGISTER_ERROR_PASSWORD_NOT_MATCH:
					response.sendRedirect("/index.jsp?msg=register_password_not_match");
					break;
				default:
					response.sendRedirect("/index.jsp?msg=register_other_error");
				}
			}

			// If the user was created, upload the file to blobstore
			if (registerFirst && registerSuccessful) {
				// First authenticate to create a session
				backendSession = endpoint.signIn(registerEmail,
						registerPassword);

				if (backendSession != null) {
					// Authentication succeeded. Create session and store
					// backend session token
					session = request.getSession(true);
					session.setAttribute("token", backendSession.token);

					try {
						uploadPoster(request, backendSession.getToken(), file);
					} catch (Exception e) {
					}

					// Redirect the user to the list of submissions
					response.sendRedirect("/user.jsp");
				} else {
					// Authentication failed for some reason
					response.sendRedirect("/index.jsp?msg=register_authentication_error");
				}
			} else if (!registerFirst) {
				// If we get to this point, we know there is an existing valid session
				// Just upload poster
				try {
					uploadPoster(request, backendSession.getToken(), file);
				} catch (Exception e) {
				}
				
				// Redirect the user to the list of submissions
				response.sendRedirect("/user.jsp");
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
		case UPDATE:
			session = request.getSession(false);			
			if(session!=null){
				endpoint.updateStatus((BlobKey)session.getAttribute("blobkey"),(String) session.getAttribute("status"));
			}
			response.sendRedirect("/admin.jsp");
		}
	}

	private synchronized boolean uploadPoster(HttpServletRequest request,
			String sessionID, ByteArrayBody body) throws Exception {
		// Obtain the upload URL
		BlobstoreService blobService = BlobstoreServiceFactory
				.getBlobstoreService();
		String uploadUrl = blobService.createUploadUrl("/upload");

		// Build a POST request to that URL
		// http://stackoverflow.com/questions/5071568/multi-part-post-with-file-and-string-in-httpclient-4-1
		HttpPost post = new HttpPost(uploadUrl);

		MultipartEntityBuilder entity = MultipartEntityBuilder.create();
		entity.addTextBody("sessionid", sessionID);
		entity.addPart("uploadPoster", body);

		post.setEntity(entity.build());

		// Send the request
		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response = null;

		try {
			response = client.execute(post);
			System.out.println(response);
		} catch (ClientProtocolException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

		return true;
	}

}
