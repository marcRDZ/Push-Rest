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
 * (build: 2015-01-14 17:53:03 UTC)
 * on 2015-02-06 at 17:03:26 UTC 
 * Modify at your own risk.
 */

package com.rodriguezdiaz.marcos.restwebservice.registration.model;

/**
 * Model definition for RegistrationRecord.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the registration. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class RegistrationRecord extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String idPhone;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String moment;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String regId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String typeAct;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getIdPhone() {
    return idPhone;
  }

  /**
   * @param idPhone idPhone or {@code null} for none
   */
  public RegistrationRecord setIdPhone(java.lang.String idPhone) {
    this.idPhone = idPhone;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getMoment() {
    return moment;
  }

  /**
   * @param moment moment or {@code null} for none
   */
  public RegistrationRecord setMoment(java.lang.String moment) {
    this.moment = moment;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getRegId() {
    return regId;
  }

  /**
   * @param regId regId or {@code null} for none
   */
  public RegistrationRecord setRegId(java.lang.String regId) {
    this.regId = regId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getTypeAct() {
    return typeAct;
  }

  /**
   * @param typeAct typeAct or {@code null} for none
   */
  public RegistrationRecord setTypeAct(java.lang.String typeAct) {
    this.typeAct = typeAct;
    return this;
  }

  @Override
  public RegistrationRecord set(String fieldName, Object value) {
    return (RegistrationRecord) super.set(fieldName, value);
  }

  @Override
  public RegistrationRecord clone() {
    return (RegistrationRecord) super.clone();
  }

}
