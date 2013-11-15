package com.joejag.code.orders.restservices;

import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;

import net.sf.json.JSONObject;

import java.io.IOException;
import java.io.ByteArrayInputStream;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import org.eclipse.emf.common.util.Diagnostic;
import org.openhealthtools.mdht.uml.cda.AssignedAuthor;
import org.openhealthtools.mdht.uml.cda.Author;
import org.openhealthtools.mdht.uml.cda.CDAFactory;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.openhealthtools.mdht.uml.cda.InfrastructureRootTypeId;
import org.openhealthtools.mdht.uml.cda.Organization;
import org.openhealthtools.mdht.uml.cda.Patient;
import org.openhealthtools.mdht.uml.cda.PatientRole;
import org.openhealthtools.mdht.uml.cda.Person;
import org.openhealthtools.mdht.uml.cda.RecordTarget;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;
import org.openhealthtools.mdht.uml.cda.util.ValidationResult;
import org.openhealthtools.mdht.uml.hl7.datatypes.CE;
import org.openhealthtools.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.openhealthtools.mdht.uml.hl7.datatypes.II;
import org.openhealthtools.mdht.uml.hl7.datatypes.PN;
import org.openhealthtools.mdht.uml.hl7.datatypes.ST;
import org.openhealthtools.mdht.uml.hl7.datatypes.TS;

import org.openhealthtools.mdht.uml.cda.mu2consol.Mu2consolPackage;


@Path("")
public class ValidatorService
{
    public static Map<String, String> orders = new TreeMap<String, String>();


    public static  synchronized JSONObject validateHelper(ByteArrayInputStream inStream) throws Exception {

        JSONObject ret = new JSONObject();

        ArrayList<String> errors = new ArrayList<String>();
        ArrayList<String> warnings = new ArrayList<String>();
        ArrayList<String> infos = new ArrayList<String>();

        ValidationResult result = new ValidationResult();

        long t0 = System.currentTimeMillis();

	CDAUtil.loadAs(
		inStream,  
		Mu2consolPackage.eINSTANCE.getTransitionOfCareAmbulatorySummary(),
		result);

        for (Diagnostic diagnostic : result.getErrorDiagnostics()) {
            errors.add(diagnostic.getMessage());
        }
        for (Diagnostic diagnostic : result.getWarningDiagnostics()) {
            warnings.add(diagnostic.getMessage());
        }
        for (Diagnostic diagnostic : result.getInfoDiagnostics()) {
            infos.add(diagnostic.getMessage());
        }
        long t1 = System.currentTimeMillis();

        ret.element("schemaValidationDiagnostics", result.getSchemaValidationDiagnostics().size());
        ret.element("emfResourceDiagnostics", result.getEMFResourceDiagnostics().size());
        ret.element("emfValidationDiagnostics",result.getEMFValidationDiagnostics().size());
        ret.element("totalDiagnostics", result.getAllDiagnostics().size());

        ret.element("valid", !result.hasErrors());
        ret.element("errors", errors);
        ret.element("warnings", warnings);
        ret.element("infos", infos);
        ret.element("processingTimeMs", (t1-t0));

        if (!result.hasErrors()) {
            System.out.println("Document is valid");
        } else {
            System.out.println("Document is invalid");
        }

        return ret;
    }

    @Path("/validation-request")
    @POST
    @Consumes({"application/xml", "*/*"})  
    @Produces({MediaType.APPLICATION_JSON })
    public String validateCcda(String docXml) throws Exception
    {
        ByteArrayInputStream inStream = null;
        try {
            inStream = new ByteArrayInputStream(docXml.getBytes("UTF-8"));
        } catch (IOException e) {
             throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return validateHelper(inStream).toString(2);

        //        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
}
