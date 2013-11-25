package ordappengine;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
		BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
		BlobInfoFactory blobInfoFactory = new BlobInfoFactory(DatastoreServiceFactory.getDatastoreService());
		BlobInfo blobInfo = blobInfoFactory.loadBlobInfo(blobKey);
		resp.setContentLength(new Long(blobInfo.getSize()).intValue());
		resp.setHeader("content-type", blobInfo.getContentType());
		resp.setHeader("content-disposition", "attachment;filename=" + blobInfo.getFilename());
		blobstoreService.serve(blobKey, resp);
	}
	
}
