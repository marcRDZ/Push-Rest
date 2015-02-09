/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.rodriguezdiaz.marcos.restwebservice;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.rodriguezdiaz.marcos.restwebservice.OfyService.ofy;


@Api(name = "registration", version = "v1", namespace = @ApiNamespace(ownerDomain = "restwebservice.marcos.rodriguezdiaz.com", ownerName = "restwebservice.marcos.rodriguezdiaz.com", packagePath = ""))
public class RegistrationEndpoint {
    private static final Logger log = Logger.getLogger(RegistrationEndpoint.class.getName());
    /**
     * Register a device to the backend
     *
     * @param regId The Google Cloud Messaging registration Id to add
     */
    @ApiMethod(name = "register")
    public void registerDevice(@Named("regId") String regId) {
        if(findRecord(regId) != null) {
            log.info("Device " + regId + " already registered, skipping register");
            return;
        }
        RegistrationRecord record = new RegistrationRecord();
        record.setRegId(regId);
        ofy().save().entity(record).now();
    }
    /**
     * Unregister a device from the backend
     *
     * @param regId The Google Cloud Messaging registration Id to remove
     */
    @ApiMethod(name = "unregister")
    public void unregisterDevice(@Named("regId") String regId) {
        RegistrationRecord record = findRecord(regId);
        if(record == null) {
            log.info("Device " + regId + " not registered, skipping unregister");
            return;
        }
        ofy().delete().entity(record).now();
    }
    /**
     * Return a collection of registered devices
     *
     * @param count The number of devices to list
     * @return a list of Google Cloud Messaging registration Ids
     */
    @ApiMethod(name = "listDevices")
    public CollectionResponse<RegistrationRecord> listDevices(@Named("count") int count) {
        List<RegistrationRecord> records = ofy().load().type(RegistrationRecord.class).limit(count).list();
        return CollectionResponse.<RegistrationRecord>builder().setItems(records).build();
    }
    private RegistrationRecord findRecord(String regId) {
        return ofy().load().type(RegistrationRecord.class).filter("regId", regId).first().now();
    }


    @ApiMethod(name = "record")
    public void registerActivity(@Named("regId") String regId,@Named("idPhone") String idPhone,
                                 @Named("moment") String moment, @Named("typeAct") String typeAct) throws IOException {

        RegistrationRecord act = new RegistrationRecord();
        act.setRegId(regId);
        act.setIdPhone(idPhone);
        act.setMoment(moment);
        act.setTypeAct(typeAct);
        ofy().save().entity(act).now();
        MessagingEndpoint messagingEndpoint = new MessagingEndpoint();
        messagingEndpoint.pushNotification(act);
    }

}
