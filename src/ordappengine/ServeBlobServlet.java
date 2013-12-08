package ordappengine;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

/**
 * Form url request with parameter blob-key?=BlobkeyString
 *
 */
public class ServeBlobServlet extends HttpServlet{
	private static final Logger log = Logger.getLogger(ServeBlobServlet.class.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = -2517145612641539508L;
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//super.doPost(req, resp);
		try {
			processRequest(req,resp);
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
		BlobInfoFactory blobInfoFactory = new BlobInfoFactory(DatastoreServiceFactory.getDatastoreService());
		//BlobInfo blobInfo = blobInfoFactory.loadBlobInfo(blobKey);
		resp.setContentLength(new Long(blobInfo.getSize()).intValue());
		resp.setHeader("content-type", blobInfo.getContentType());
		resp.setHeader("content-disposition", "attachment;filename=" + blobInfo.getFilename());
		blobstoreService.serve(blobKey, resp);*/
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//super.doPost(req, resp);
		try {
			processRequest(req,resp);
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void processRequest(HttpServletRequest req,
			HttpServletResponse resp) throws FileUploadException,
			IOException {
		System.out.print("Blobkey: "+req.getParameter("blob-key"));
		BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
		DatastoreControl dc = new DatastoreControl();
		Submission sub = dc.getSubmission(req.getParameter("blob-key"));
		BlobInfoFactory blobInfoFactory = new BlobInfoFactory(DatastoreServiceFactory.getDatastoreService());
		BlobInfo blobInfo = blobInfoFactory.loadBlobInfo(blobKey);
		System.out.print("filename: " + sub.posterName);
		resp.setContentLength(new Long(blobInfo.getSize()).intValue());
		resp.setHeader("content-type", blobInfo.getContentType());
		resp.setHeader("content-disposition", "attachment; filename=" + sub.posterName);
		blobstoreService.serve(blobKey, resp);		
	}
		
}
