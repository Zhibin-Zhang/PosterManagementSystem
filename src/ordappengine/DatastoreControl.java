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
import java.util.List;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import com.google.appengine.api.blobstore.BlobKey;
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
			session.submissions = user.submissions;
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
					"Thanks for registering Oklahoma Research Day Poster Management System. Please use your email address " +
					"to login to the system./n/nCloud 5lusions Team");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public boolean insertPoster(String emailAddress, Submission submission) {
		EntityManager em = EMF.get().createEntityManager();
		User user = null;
		try {
			user = em.find(User.class, emailAddress);
			if (user == null)
				return false;
			user.addSubmission(submission);
			em.persist(user);
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
	@Override
	public ArrayList<Submission> getBlobServe(String... emailAddress) {
		//if the emailAdress is null, return all the submission addresses
		EntityManager em = EMF.get().createEntityManager();
		ArrayList<Submission> serves = new ArrayList<Submission>();
		List<User> results = null;	//List of users
		if(emailAddress ==null){
			//get all submissions
			Query query = em.createNativeQuery("SELECT * FROM User");
			results = query.getResultList();
			for(User user : results){
				ArrayList<Submission> submissions = user.submissions;
				for(Submission sub : submissions){
					serves.add(sub);
				}
			}
		}else
		{
			//get submissions from specific user
			String email = emailAddress[0];
			Query query = em.createNamedQuery("SELECT u FROM User WHERE u.name = :email");
			query.setParameter("email", email);
			User user = (User)query.getSingleResult();
			for(Submission sub : user.submissions){
				serves.add(sub);
			}
		}
		return serves;
	}
}