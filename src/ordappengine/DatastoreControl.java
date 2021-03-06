package ordappengine;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class DatastoreControl implements StorageManager {

	@Override
	public boolean isAvailable(String emailAddress) {
		EntityManager em = EMF.get().createEntityManager();
		User user = null;

		try {
			user = em.find(User.class, emailAddress);
		} finally {
			em.close();
		}
		if (user == null)
			return true;
		return false;
	}

	@Override
	public BackendSession authenticateUser(String emailAddress, String password) {
		EntityManager em = EMF.get().createEntityManager();
		User user = null;
		try {
			user = em.find(User.class, emailAddress);
		} finally {
			em.close();
		}

		// if the user exists and the password matches
		if (user != null && user.password.equals(password)) {
			BackendSession session = new BackendSession(generateSessionToken());
			session.isAdmin = user.isAdmin;
			//session.submissions = user.submissions;
			session.emailAddress = user.emailAddress;
			String cacheKey = session.token;
			// Using the synchronous cache
			MemcacheService syncCache = MemcacheServiceFactory
					.getMemcacheService();
			syncCache.setErrorHandler(ErrorHandlers
					.getConsistentLogAndContinue(Level.INFO));

			// put the session object into the Memcache
			byte[] cacheSession = getByteArray(session);

			if (cacheSession != null) {
				syncCache.put(cacheKey, cacheSession);
			}
			return session;
		}
		return null;
	}

	@Override
	public boolean createUser(String emailAddress, String password, boolean isAdmin) {
		EntityManager em = EMF.get().createEntityManager();
		User user = new User();
		user.emailAddress = emailAddress;
		user.password = password;
		user.isAdmin = isAdmin;
		try {
			em.persist(user);
		} catch (PersistenceException e) {
			em.close();
			return false;
		} finally {
			em.close();
		}

		try {
			Mailer.send(user.emailAddress, "Oklahoma Research Day Poster Management System Registration Information", 
					"Thanks for registering in the Oklahoma Research Day Poster Management System. Please use your email address " +
					"to login to the system.\n\nCloud 5olutions Team");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public boolean insertPoster(Submission submission) {
		EntityManager em = EMF.get().createEntityManager();
		try {
			em.persist(submission);
		} finally {
			em.close();
		}
		return true;
	}

	private String generateSessionToken() {
		SecureRandom random = null;
		String sessionKey = null;
		// check if the random number is in the Memcache
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.setErrorHandler(ErrorHandlers
				.getConsistentLogAndContinue(Level.INFO));
		byte[] value = null;
		do {
			random = new SecureRandom();
			sessionKey = new BigInteger(130, random).toString(32);
			value = (byte[]) syncCache.get(sessionKey);
		} while (value != null);
		return sessionKey;
	}

	private byte[] getByteArray(BackendSession session) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] userArray = null;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(session);
			userArray = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				oos.close();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return userArray;
	}

	@Override
	public boolean logout(String sessionID) {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.setErrorHandler(ErrorHandlers
				.getConsistentLogAndContinue(Level.INFO));
		if (syncCache.contains(sessionID)) {
			syncCache.delete(sessionID);
			return true;
		}
		return false;
	}

	@Override
	public boolean isInSession(String sessionID) {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.setErrorHandler(ErrorHandlers
				.getConsistentLogAndContinue(Level.INFO));
		if (syncCache.contains(sessionID))
			return true;
		return false;
	}

	@Override
	public BackendSession getSessionFromCache(String sessionID) {
		BackendSession session = null;

		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.setErrorHandler(ErrorHandlers
				.getConsistentLogAndContinue(Level.INFO));

		if (syncCache.contains(sessionID)) {
			ByteArrayInputStream bis = null;
			ObjectInputStream ois = null;

			try {
				bis = new ByteArrayInputStream(
						(byte[]) syncCache.get(sessionID));
				ois = new ObjectInputStream(bis);

				session = (BackendSession) ois.readObject();
			} catch (Exception e) {

			} finally {
				try {
					if (bis != null) {
						bis.close();
					}

					if (bis != null) {
						ois.close();
					}
				} catch (Exception e) {

				}
			}
		}

		return session;
	}
	
	
	/** 
	 *  This method will return the downloading address with filename in BlobServe object. 
	 *  @param emailAddress emailAddress is optional parameter. Call the method with empty parameter will return
	 *  					all the submissions downloading information in a ArralyList<Submission>;
	 *  We have to use another servlet to serve the blob object. 
	 *  @author Zhibin
	 */
	/**
	 * Was running into an error where I was getting an arrayindexoutofbounds exception, so I separated this method
	 * into two separate methods
	 * @author Matt
	 */
	@Override
	public ArrayList<Submission> getBlobServe() {
		// if the emailAdress is null, return all the submission addresses
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		
		ArrayList<Submission> serves = new ArrayList<Submission>();
		
		// get all submissions
		Query query = new Query("Submission");
		PreparedQuery pq = datastore.prepare(query);
		for(Entity entity : pq.asIterable()){
			String email = (String) entity.getProperty("username");
			String posterName = (String) entity.getProperty("posterName");
			String status = (String) entity.getProperty("posterStatus");
			Key blobKey=entity.getKey();
			String stringKey = blobKey.getName();
			Submission sub = new Submission();
			sub.username = email;
			sub.posterName = posterName;
			sub.posterStatus = status;
			sub.blobKey = stringKey;
			serves.add(sub);
		}
		
		return serves;
	}
	@Override
	public ArrayList<Submission> getBlobServe(String emailAddress) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query.FilterPredicate filter = new Query.FilterPredicate("username",Query.FilterOperator.EQUAL, emailAddress);
		Query query = new Query("Submission").setFilter(filter);
		PreparedQuery pq = datastore.prepare(query);
		ArrayList<Submission> serves = new ArrayList<Submission>();
		for(Entity entity : pq.asIterable()){
			String email = (String) entity.getProperty("username");
			String posterName = (String) entity.getProperty("posterName");
			String status = (String) entity.getProperty("posterStatus");
			Key blobKey=entity.getKey();
			String stringKey = blobKey.getName();
			Submission sub = new Submission();
			sub.username = email;
			sub.posterName = posterName;
			sub.posterStatus = status;
			sub.blobKey = stringKey;
			serves.add(sub);
		}
	return serves;
	}
	@Override
	public Submission getSubmission(String blobKey) {
		DatastoreService datastore =
                DatastoreServiceFactory.getDatastoreService();
		
		Key key = KeyFactory.createKey("Submission", blobKey);
		
		try {
			Submission result = new Submission();
			Entity sub = datastore.get(key);
			
			result.blobKey = sub.getKey().getName();
			result.posterName = sub.getProperty("posterName").toString();
			result.posterStatus = sub.getProperty("posterStatus").toString();
			result.username = sub.getProperty("username").toString();
			
			return result;
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean updateStatus(String blobKey, String status) {
		DatastoreService datastore =
                DatastoreServiceFactory.getDatastoreService();
		Key key = KeyFactory.createKey("Submission", blobKey);
		try {
			Entity sub = datastore.get(key);
			sub.setProperty("posterStatus", status);
			datastore.put(sub);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean deleteSubmission(String blobKey) {
		EntityManager em = EMF.get().createEntityManager();
		Submission submission = null;		
		try {
			submission = em.find(Submission.class, KeyFactory.createKey("Submission", blobKey));			
			if(submission != null) {
				em.remove(submission);
			}
		} finally {
			em.close();
		}		
		if(submission != null)
			return true;
		
		return false;
	}

	@Override
	public User editUser(String emailAddress, String password) {
		DatastoreService datastore =
                DatastoreServiceFactory.getDatastoreService();
		Key key = KeyFactory.createKey("User", emailAddress);
		User result=new User();
		try {
			Entity user = datastore.get(key);
			user.setProperty("password", password);			
			datastore.put(user);
			result.emailAddress=user.getKey().getName();
			result.password=user.getProperty("password").toString();
			result.isAdmin=(boolean)user.getProperty("isAdmin");
			
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return result;		
	}
	@Override
	public User getUser(String emailAddress){
		DatastoreService datastore =
                DatastoreServiceFactory.getDatastoreService();
		
		Key key = KeyFactory.createKey("User", emailAddress);
		
		try {
			User result = new User();
			Entity user = datastore.get(key);
			result.emailAddress=user.getKey().getName();			
			return result;
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
