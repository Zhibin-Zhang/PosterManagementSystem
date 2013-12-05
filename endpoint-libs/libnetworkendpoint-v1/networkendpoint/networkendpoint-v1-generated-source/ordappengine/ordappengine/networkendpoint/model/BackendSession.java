/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2013-11-22 19:59:01 UTC)
 * on 2013-12-05 at 08:38:15 UTC 
 * Modify at your own risk.
 */

package ordappengine.ordappengine.networkendpoint.model;

/**
 * Model definition for BackendSession.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the networkendpoint. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class BackendSession extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean admin;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String emailAddress;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean isAdmin;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<Submission> submissions;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String token;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getAdmin() {
    return admin;
  }

  /**
   * @param admin admin or {@code null} for none
   */
  public BackendSession setAdmin(java.lang.Boolean admin) {
    this.admin = admin;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getEmailAddress() {
    return emailAddress;
  }

  /**
   * @param emailAddress emailAddress or {@code null} for none
   */
  public BackendSession setEmailAddress(java.lang.String emailAddress) {
    this.emailAddress = emailAddress;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getIsAdmin() {
    return isAdmin;
  }

  /**
   * @param isAdmin isAdmin or {@code null} for none
   */
  public BackendSession setIsAdmin(java.lang.Boolean isAdmin) {
    this.isAdmin = isAdmin;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<Submission> getSubmissions() {
    return submissions;
  }

  /**
   * @param submissions submissions or {@code null} for none
   */
  public BackendSession setSubmissions(java.util.List<Submission> submissions) {
    this.submissions = submissions;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getToken() {
    return token;
  }

  /**
   * @param token token or {@code null} for none
   */
  public BackendSession setToken(java.lang.String token) {
    this.token = token;
    return this;
  }

  @Override
  public BackendSession set(String fieldName, Object value) {
    return (BackendSession) super.set(fieldName, value);
  }

  @Override
  public BackendSession clone() {
    return (BackendSession) super.clone();
  }

}
