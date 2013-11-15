package ordappengine;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class NetworkEndpointTest {

	private NetworkEndpoint endpoint;

	@Before
	public void setUp() throws Exception {
		endpoint = new NetworkEndpoint();
		endpoint.setStorageManager(new DatastoreControlStub());
	}

	@Test
	public void testCorrectAdminCredentialsSignIn() {
		BackendSession newSession = endpoint.signIn("admin@gmail.com", "admin");
		assertTrue(newSession != null
				&& newSession.emailAddress.equals("admin@gmail.com")
				&& newSession.isAdmin && !newSession.token.isEmpty());
	}

	@Test
	public void testIncorrectCredentialsSignIn() {
		BackendSession newSession;

		newSession = endpoint.signIn("admin@gmail.com", "AdMiN");
		assertTrue(newSession == null);

		newSession = endpoint.signIn("AdMiN@gmail.com", "admin");
		assertTrue(newSession == null);

		newSession = endpoint.signIn("", "");
		assertTrue(newSession == null);

		newSession = endpoint.signIn(null, null);
		assertTrue(newSession == null);
	}

}
