# MDHT Consolidated CDA Validator: JSON API

## Online Demo
Hosted by [AppFog](http://appfog.com): `http://mdht-webapp.aws.af.cm/validation-request`

```
# Get a sample document
curl "https://raw.github.com/chb/sample_ccdas/master/NIST%20Samples/CCDA_CCD_b1_Ambulatory_v2.xml" \
     -o sample-ccda.xml

# Validate it
curl -X POST \
    --header "Content-Type:application/xml" \
    -d @sample-ccda.xml \
    http://mdht-webapp.aws.af.cm/validation-request

# Wait for it. 

The **first request can take ~15 seconds** as the MDHT framework bootstraps.
Subsequent requests should be more like **~1s**
```

## Build, Test, Deploy

### Setup

Requirements:
* `Java` 1.6+
* `maven 3` (**Note**: *will not work with maven 2*)
* `python`
* `wget`

Tested on Ubuntu 12.10.

```
sh setup.sh
```


### Run in `Jetty`
```
mvn jetty:run
```

### Test with `curl`
```
curl -X POST \
  --header "Content-Type:application/xml" \
  -d @/sample/ccda.xml \
  http://localhost:9090/ccda/validation-request \
  -vvv 
```

### Response

```javascript
{
  "schemaValidationDiagnostics": 0,
  "emfResourceDiagnostics": 0,
  "emfValidationDiagnostics": 233,
  "totalDiagnostics": 233,
  "valid": false,
  "errors":   [
    "Consol Cognitive Status Problem Observation SHALL contain at least one [1..*] entryRelationship (CONF:14335, CONF:14589, CONF:14352) Contains @typeCode=\"REFR\" REFR, and Contains exactly one [1..1] Caregiver Characteristics (templateId: 2.16.840.1.113883.10.20.22.4.72)",
    "Consol Cognitive Status Problem Observation SHALL contain at least one [1..*] entryRelationship (CONF:14467, CONF:14590, CONF:14468) Contains @typeCode=\"COMP\" COMP, and Contains exactly one [1..1] Assessment Scale Observation (templateId: 2.16.840.1.113883.10.20.22.4.69)",
    "Mu2consol Social History Section SHALL contain at least one [1..*] entry Contains exactly one [1..1] Smoking Status Observation (templateId: 2.16.840.1.113883.10.22.4.78)"
  ],
  "warnings":   [
    "Consol General Header Constraints SHALL contain at least one [1..*] recordTarget (CONF:5266) each SHALL contain exactly one [1..1] patientRole, where  (CONF:5268) patient Role SHALL contain exactly one [1..1] patient, where  (CONF:5283) each SHOULD contain zero or more [0..*] languageCommunication, where  (CONF:5406) languageCommunication SHOULD contain zero or one [0..1] proficiencyLevelCode, where the @code SHALL be selected from ValueSet LanguageAbilityProficiency 2.16.840.1.113883.1.11.12199 STATIC (CONF:9965)",
    "Consol General Header Cons
...
  ],
  "infos": []
}
```

### Deploying
Deploy `target/ccda-server.war` as needed.


## Limitations

* *MDHT stack is not thread-safe*, so the validation code is currently wrapped in
  a `synchronized` block.  This effectively limits the number of concurrent
  requests to 1.  (Workaround: host multiple instances of the servlet multiple
  and reverse proxy to them.)

* Validation is slow (on the the order of 15 seconds per document).
