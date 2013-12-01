package ordappengine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

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

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpMediaType;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.MultipartContent;
import com.google.api.client.http.MultipartContent.Part;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.appengine.api.blobstore.BlobInfoFactory;
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
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

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
			BackendSession backendSession;
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
				if (backendSession.isAdmin) {
					response.sendRedirect("/admin.jsp");
				} else {
					response.sendRedirect("/user.jsp");
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
			Submission submission = null;
			boolean hasPosterFile = false;
			MultipartContent content = null;
			

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
					hasPosterFile = true;
					HttpMediaType mediaType = new HttpMediaType("multipart","form-data");
					mediaType.setParameter("name", "uploadPoster");
					content = new MultipartContent();
					content.setMediaType(mediaType);
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					IOUtils.copy(item.openStream(), out);
					byte[] data = out.toByteArray();
					ByteArrayContent byteArrayContent = new ByteArrayContent(item.getContentType(), data);
					//InputStreamContent inputStreamContent = new InputStreamContent(item.getContentType(), item.openStream());
					content.addPart(new Part(byteArrayContent));
					//long length = MultipartContent.computeLength(content);
					endpoint.registerUser(registerEmail, registerPassword,
							registerConfirmPassword);
					if(hasPosterFile){
						session = request.getSession(true);
						session.setAttribute("email", registerEmail);
						try {
							uploadPoster(request, content);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			if(!hasPosterFile)
			switch (endpoint.registerUser(registerEmail, registerPassword,
					registerConfirmPassword).result) {
			case RegisterResult.REGISTER_SUCCESS:
				response.sendRedirect("/index.jsp?msg=register_success");

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
	
	private synchronized boolean uploadPoster(HttpServletRequest request, MultipartContent content) throws Exception{
		String uploadUrl = null;
		HttpRequestFactory httpRequestFactory = HTTP_TRANSPORT.createRequestFactory();
		URL url = new URL(request.getScheme(),request.getServerName(),request.getServerPort(), "/upurl");
		GenericUrl gUrl = new GenericUrl(url);
		HttpRequest uploadGetRequest = httpRequestFactory.buildGetRequest(gUrl);
		HttpResponse uploadGetResponse = uploadGetRequest.execute();
		//check the response status
		if(uploadGetResponse.getStatusCode() == HttpServletResponse.SC_OK)
		{
			HttpHeaders headers = uploadGetResponse.getHeaders();
			uploadUrl = headers.getFirstHeaderStringValue("uploadUrl");
		}

		//String contentType = file.getContentType();
		//String fileName = file.getName();
		//MultipartContent content = new MultipartContent();
		//byte[] data = IOUtils.toByteArray(is);
		//InputStreamContent inputStreamContent = new InputStreamContent(contentType, poster);
		//content.addPart(new Part(inputStreamContent));
		GenericUrl gUpUrl = new GenericUrl(new URL(uploadUrl));
		HttpRequest uploadPostRequest = httpRequestFactory.buildPostRequest(gUpUrl, content);
		HttpResponse uploadedResponse = uploadPostRequest.execute();
		if(uploadedResponse.getStatusCode() == HttpServletResponse.SC_OK)
			return true;
		else
			return false;
	}
	
}