package ordappengine;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class BlobstoreServlet extends HttpServlet{
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		BlobstoreService blobService = BlobstoreServiceFactory.getBlobstoreService();
		Map<String, List<BlobKey>> blobMap = blobService.getUploads(req);
		//In order to make this work, the name of the form that used to upload poster should be uploadPoster
		List<BlobKey> blobKeyList = blobMap.get("uploadPoster"); 
		BlobKey blobKey = blobKeyList.get(0);
		BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
		String posterName = blobInfoFactory.loadBlobInfo(blobKey).getFilename();
		//call the post method by using session ID as parameter
		String sessionId = req.getParameter("sessionid");
		if(sessionId != null){
			StorageManager storageManager = new DatastoreControl();			
			BackendSession currentSession = storageManager.getSessionFromCache(sessionId);
			//if the user is in session
			if(currentSession != null){
				String emailAddress = currentSession.emailAddress;
				Submission submission = new Submission();
				submission.blobKey = blobKey;
				submission.posterName =  posterName;
				submission.username = emailAddress;
				storageManager.insertPoster(submission);
				resp.setStatus(HttpServletResponse.SC_OK);
			}
			else{
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}else if(req.getSession(false) != null){
			HttpSession session = req.getSession(false);
			String email = (String)session.getAttribute("email");
			if(email != null){
				StorageManager storageManager = new DatastoreControl();
				Submission submission = new Submission();
				submission.blobKey = blobKey;
				submission.posterName =  posterName;
				submission.username = email;
				storageManager.insertPoster(submission);
				resp.setStatus(HttpServletResponse.SC_OK);
			}else{
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
		else
		{
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}		
	}
}
