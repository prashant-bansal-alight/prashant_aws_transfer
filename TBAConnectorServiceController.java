package com.alight.microservice.application.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alight.asg.IntegrationException;
import com.alight.asg.IntegrationExceptionConstants;
import com.alight.asg.helper.ServiceHelperContext;
import com.alight.asg.model.header.v1_0.RequestHeader;
import com.alight.asg.model.header.v1_0.ResponseHeader;
import com.alight.asg.model.header.v1_0.SystemTicket;
import com.alight.asg.model.header.v1_0.TransactionHeader;
import com.alight.asg.model.header.v1_0.TransactionInfo;
import com.alight.asg.service.ServiceDelegator;
import com.alight.enrollment.AvailableOptionsResponse;
import com.alight.enrollment.AvailableOptionsResponseChoices;
import com.alight.enrollment.EligibleBusinessProcessesResponse;
import com.alight.enrollment.EligibleDependentsResponse;
import com.alight.enrollment.EligiblePlansResponse;
import com.alight.enrollment.EligibleRelationships;
import com.alight.enrollment.EligibleRelationshipsInner;
import com.alight.enrollment.EnrollmentsResponse;
import com.alight.enrollment.EnrollmentsResponseEnrollments;
import com.alight.enrollment.ExcessCreditsResponse;
import com.alight.enrollment.PendingElectionsResponse;
import com.alight.enrollment.PendingElectionsResponseCoverages;
import com.alight.enrollment.PendingElectionsResponseDependents;
import com.alight.enrollment.PendingElectionsResponseRelationshipCoverages;
import com.alight.enrollment.PersonContactsMaintenancePostalAddressSimulationRequest;
import com.alight.enrollment.PersonMaintenanceSimulationRequest;
import com.alight.enrollment.PersonRelationshipMaintenanceSimulationRequest;
import com.alight.enrollment.PersonRelationshipMaintenanceSimulationResponse;
import com.alight.enrollment.PersonTasksResponse;
import com.alight.enrollment.PlanAvailabilityResponse;
import com.alight.enrollment.PrimaryCareProvidersResponse;
import com.alight.enrollment.ProviderNetworksResponse;
import com.alight.enrollment.ProvisionContactsMaintenancePostalAddressSimulationResponse;
import com.alight.enrollment.ReviseCancelRequest;
import com.alight.enrollment.ReviseCancelRequestPlans;
import com.alight.enrollment.ReviseElectionsRequest;
import com.alight.enrollment.ReviseElectionsRequestDependentPlans;
import com.alight.enrollment.ReviseElectionsRequestElections;
import com.alight.enrollment.ReviseElectionsResponse;
import com.alight.enrollment.ReviseExcessCreditsRequest;
import com.alight.enrollment.SuggestionSupportResponse;
import com.alight.enrollment.UpdatePersonTasksRequest;
import com.alight.enrollments.util.EligiblePlansResponseUtility;
import com.alight.enrollments.util.RequestVariableValidationUtility;
import com.alight.enrollments.util.ValidValues;
import com.alight.enrollments.util.ValidValues.BundleTypes;
import com.alight.enrollments.util.ValidValues.CacheConstants;
import com.alight.enrollments.util.ValidValues.OperationNames;
import com.alight.enrollments.util.ValidValues.ReviseElectionType;
import com.alight.enrollments.util.ValidValues.SuggestionSupportType;
import com.alight.enrollnment.healthdata.service.HealthPlanAvailabilityHelper;
import com.alight.microservice.application.constants.EnrollmentConstants;
import com.alight.microservice.application.utils.DistributedCacheRedisUtil;
import com.alight.microservice.application.utils.EnrollmentCacheUtil;
import com.alight.microservice.getplan.model.HealthPlanRequest;
import com.alight.microservice.helper.availableOptions.AvailableOptionsDataHelper;
import com.alight.microservice.helper.cancel.CancelDataHelper;
import com.alight.microservice.helper.commit.CommitDataHelper;
import com.alight.microservice.helper.eligibleBusinessProcessClassifications.EligibleBusinessProcessClassificationsHelper;
import com.alight.microservice.helper.eligibleDependents.EligibleDependentsDataHelper;
import com.alight.microservice.helper.eligibleOffers.EligibleOffersDataHelper;
import com.alight.microservice.helper.eligiblePlans.EligiblePlansDataHelper;
import com.alight.microservice.helper.enrollments.EnrollmentDataHelper;
import com.alight.microservice.helper.enrollments.PostEnrollmentDataHelper;
import com.alight.microservice.helper.excessCredits.ExcessCreditsDataHelper;
import com.alight.microservice.helper.hpccSummary.CurrencyFormatConstants;
import com.alight.microservice.helper.hpccSummary.HPCCDataHelper;
import com.alight.microservice.helper.hpccSummary.HPCCSummaryDataHelper;
import com.alight.microservice.helper.pendingElections.PendingElectionsDataHelper;
import com.alight.microservice.helper.personBenefitsImpact.GetPersonBenefitsImpactHelper;
import com.alight.microservice.helper.personContactMaintenancePostalAddressSimulation.PutPersonContactsMaintenancePostalAddressSimulationHelper;
import com.alight.microservice.helper.personMaintenanceSimulation.PutPersonMaintenanceSimulationHelper;
import com.alight.microservice.helper.personRelationshipMaintenanceSimulation.PersonRelationshipMaintenanceSimulationHelper;
import com.alight.microservice.helper.personRelationshipMaintenanceSimulation.PostHMAddDependentSimulationHelper;
import com.alight.microservice.helper.personTasks.PersonTasksDataHelper;
import com.alight.microservice.helper.personTasks.PersonTasksPUTDataHelper;
import com.alight.microservice.helper.personUnlock.PersonUnlockDataHelper;
import com.alight.microservice.helper.planavailability.PlanAvailabilityHelper;
import com.alight.microservice.helper.primaryCareProviderElections.PrimaryCareProviderElectionsDataHelper;
import com.alight.microservice.helper.providerNetworks.ProviderNetworksDataHelper;
import com.alight.microservice.helper.provisionContactMaintenancePostalAddressSimulation.ProvisionContactsMaintenancePostalAddressSimulationHelper;
import com.alight.microservice.helper.reviseCancel.ReviseCancelDataHelper;
import com.alight.microservice.helper.reviseElections.ReviseDependentElectionsDataHelper;
import com.alight.microservice.helper.reviseElections.ReviseElectionsDataHelper;
import com.alight.microservice.helper.reviseExcessCredits.ReviseExcessCreditsDataHelper;
import com.alight.microservice.helper.suggestionSupport.SuggestionSupportDataHelper;
import com.alight.microservice.helper.validate.ValidateDataHelper;
import com.alight.model.cache.EnrollmentCache;
import com.alight.model.cache.EnrollmentCacheList;
import com.alight.enrollment.EligibleOffersResponse;
import com.alight.model.healthplancomparison.HealthPlanComparisonAvailableOptionsResponse;
import com.alight.model.healthplancomparison.HealthPlanComparisonResponse;
import com.alight.model.reviseelections.ReviseElections;
import com.alight.prvd.tba.model.personMaintenanceSimulation.PRCSHMENACAPIRequest;
import com.aonhewitt.logging.events.ErrorLogEvent;
import com.aonhewitt.logging.helpers.ErrorLogEventHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class TBAConnectorServiceController {

	public final static String ENROLLMENT_SERVICE = "enrollment";

	/*
	 * The special comment we can manipulate to kick off CI build process if it
	 * happens to fail.
	 * 
	 * MOTB-Be your best self.
	 * "All that we see or seem is but a dream within a dream." Edgar Allan Poe
	 */
	@Autowired
	private ApplicationContext appContext;

	@Autowired
	DistributedCacheRedisUtil distributedCacheUtil;

	@Autowired
	EnrollmentCacheUtil enrollmentCacheUtil;

	@Value("${app.cache.enrollment.availableOptions:true}")
	private Boolean toggleAvailableOptionsCache;

	public Boolean isToggleAvailableOptionsCache() {
		return toggleAvailableOptionsCache;
	}

	@Value("${app.cache.enrollment.enrollmentsWithoutSystemTicket:true}")
	private Boolean toggleEnrollmentsWithoutSystemTicketCache;

	public Boolean istoggleEnrollmentsWithoutSystemTicketCache() {
		return toggleEnrollmentsWithoutSystemTicketCache;
	}

	@Value("${app.cache.enrollment.pendingElections:true}")
	private Boolean togglePendingElectionsCacheStopExecuteHelper;

	public Boolean isTogglePendingElectionsCache() {
		return togglePendingElectionsCacheStopExecuteHelper;
	}

	public void setTogglePendingElectionsCacheStopExecuteHelper(Boolean togglePendingElectionsCacheStopExecuteHelper) {
		this.togglePendingElectionsCacheStopExecuteHelper = togglePendingElectionsCacheStopExecuteHelper;
	}

	public void settoggleEnrollmentsWithoutSystemTicketCache(Boolean toggleEnrollmentsWithoutSystemTicketCache) {
		this.toggleEnrollmentsWithoutSystemTicketCache = toggleEnrollmentsWithoutSystemTicketCache;
	}
	
	@GetMapping(value = "enrollments", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getEnrollment(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

		String resource;
		resource = "enrollments";

		EnrollmentsResponse expectedResponse;
		expectedResponse = new EnrollmentsResponse();

		HttpStatusCode httpStatus = HttpStatus.OK;

		try {

			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * the "resource" name, and appContext. We need the resource name in situations
			 * where we want to control TBA routing based on the resource. This could also
			 * be accomplished in most cases using the systemInstanceId, so the resource
			 * name is optional-you can send in null if you want and it will choose the
			 * default TBA routing for the consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource,
					appContext);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.
			 */
			EnrollmentDataHelper helper = new EnrollmentDataHelper(serviceHelperContext);

			ResponseEntity<String> responseEntity;

			/*
			 * Begin the Redis caching solution for enrollments. We are going to handle
			 * cache for enrollments call only if the request contains systemTicket in it.
			 */
			RequestHeader reqHdr = (RequestHeader) serviceHelperContext.get(ServiceHelperContext.ALIGHT_REQUEST_HEADER);
			boolean isCachedResponse = false;
			String serviceName = ENROLLMENT_SERVICE;
			EnrollmentCache enrollmentCache = null;
			enrollmentCacheUtil = new EnrollmentCacheUtil();
			helper.checkSystemTicket(reqHdr);

			if (reqHdr != null && serviceHelperContext.get(CacheConstants.SYSTEMTICKET) != null) {
				String subjectId = reqHdr.getSubjectId();
				String clientId = reqHdr.getClientId();
				String cacheKey = EnrollmentCacheUtil.createCacheKey(serviceName, clientId, subjectId);
				EnrollmentCacheList ecl = (EnrollmentCacheList) distributedCacheUtil.getObjectFromCache(cacheKey);				
				enrollmentCache = enrollmentCacheUtil.getEnrollmentCacheForSystemTicket(ecl, reqHdr.getSystemTickets());
				/*
				 * If cache is present for enrollments with reference to the systemTicket get
				 * cached Object and return as response. else If cache is present and
				 * enrollments object is not available then call sTBA and save the return
				 * response to cache else If cache is not available for the given systemTicket
				 * then call sTBA and initiate enrollment cache with return response
				 */
				if (enrollmentCache != null) {										
					if( enrollmentCache.getEnrollmentsResponse() != null) {
						EnrollmentsResponse enrollments = enrollmentCache.getEnrollmentsResponse();
						serviceHelperContext.set("responseBody", enrollments);
						httpStatus = HttpStatus.OK;
						isCachedResponse = true;
					}else {
						responseEntity = helper.executeService(String.class);
						httpStatus = responseEntity.getStatusCode();
						enrollmentCache.setEnrollmentsResponse(
								(EnrollmentsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY));
					}					
				}else {
					responseEntity = helper.executeService(String.class);
					httpStatus = responseEntity.getStatusCode();
					enrollmentCache = enrollmentCacheUtil.initializeEnrollmentCache(reqHdr.getSystemTickets(),
							((EnrollmentsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY)), null,
							null, null);
					List<EnrollmentCache> enrollmentCacheList = new ArrayList<EnrollmentCache>();
					if (ecl != null && !(ecl.getEnrollmentCache().isEmpty())) {
						enrollmentCacheList = ecl.getEnrollmentCache();
					}
					enrollmentCacheList.add(enrollmentCache);
					if (ecl == null) {
						ecl = new EnrollmentCacheList();
					}
					ecl.setEnrollmentCache(enrollmentCacheList);
				}
				distributedCacheUtil.saveObjectInCache(cacheKey, ecl);
			} else if (reqHdr != null && serviceHelperContext.get(CacheConstants.SYSTEMTICKET) == null){
				if (istoggleEnrollmentsWithoutSystemTicketCache()) {
					String subjectId = reqHdr.getSubjectId();
					String clientId = reqHdr.getClientId();
					String cacheKey = EnrollmentCacheUtil.createCacheKey(serviceName, clientId, subjectId);
					EnrollmentCacheList ecl = (EnrollmentCacheList) distributedCacheUtil.getObjectFromCache(cacheKey);
					enrollmentCache = enrollmentCacheUtil.getEnrollmentCacheForSystemTicket(ecl, null);

					if (enrollmentCache != null) { 
						if(enrollmentCache.getEnrollmentsResponse() != null) {
							EnrollmentsResponse enrollments = enrollmentCache.getEnrollmentsResponse();
							serviceHelperContext.set("responseBody", enrollments);
							httpStatus = HttpStatus.OK;
							isCachedResponse = true;
						}else {
							responseEntity = helper.executeService(String.class);
							httpStatus = responseEntity.getStatusCode();
							enrollmentCache.setEnrollmentsResponse(
									(EnrollmentsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY));
						}
					} else {
						responseEntity = helper.executeService(String.class);
						httpStatus = responseEntity.getStatusCode();
						enrollmentCache = enrollmentCacheUtil.initializeEnrollmentCache(null,
								((EnrollmentsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY)),
								null, null, null);
						List<EnrollmentCache> enrollmentCacheList = new ArrayList<EnrollmentCache>();
						if (ecl != null && !(ecl.getEnrollmentCache().isEmpty())) {
							enrollmentCacheList = ecl.getEnrollmentCache();
						}
						enrollmentCacheList.add(enrollmentCache);
						if (ecl == null) {
							ecl = new EnrollmentCacheList();
						}
						ecl.setEnrollmentCache(enrollmentCacheList);
					}
					distributedCacheUtil.saveObjectInCache(cacheKey, ecl);

				} else {
					responseEntity = helper.executeService(String.class);
					httpStatus = responseEntity.getStatusCode();
				}
			} else {
				throw new IntegrationException("Invalid request header");
			}

			if (isCachedResponse) {
				serviceHelperContext.set(ServiceHelperContext.ALIGHT_RESPONSE_HEADER,
						enrollmentCacheUtil.getResponseHeaderFromRequest(reqHdr));
			}

			EnrollmentsResponse response;
			response = (EnrollmentsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);
			ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			return new ResponseEntity<EnrollmentsResponse>(response, httpHeaders, httpStatus);

		} catch (IntegrationException e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "IntegrationException in getEnrollment()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), httpStatus);

		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "Exception in getEnrollment()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), httpStatus);

		}

	}

	@GetMapping(value = "/enrollments/{businessProcessClassification}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getEnrollment(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			@PathVariable String businessProcessClassification) {

		String resource;
		resource = "enrollments";

		EnrollmentsResponse expectedResponse;
		expectedResponse = new EnrollmentsResponse();

		HttpStatusCode httpStatus = HttpStatus.OK;

		try {

			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * and the "resource" name, and appContext. We need the resource name in
			 * situations where we want to control TBA routing based on the resource. This
			 * could also be accomplished in most cases using the systemInstanceId, so the
			 * resource name is optional-you can send in null if you want and it will choose
			 * the default TBA routing for the consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource,
					appContext);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.
			 */
			EnrollmentDataHelper helper = new EnrollmentDataHelper(serviceHelperContext);

			/*
			 * Attach the RequestParam values to the ServiceHelperContext instance as
			 * name/value pairs.
			 */
			RequestVariableValidationUtility.validateStringElement(businessProcessClassification,
					"businessProcessClassification", true);
			serviceHelperContext.set("businessProcessClassification", businessProcessClassification);

			ResponseEntity<String> responseEntity;

			/*
			 * Begin the Redis caching solution for enrollments. We are going to handle
			 * cache for enrollments call only if the request contains systemTicket in it.
			 */
			RequestHeader reqHdr = (RequestHeader) serviceHelperContext.get(ServiceHelperContext.ALIGHT_REQUEST_HEADER);
			boolean isCachedResponse = false;
			String serviceName = ENROLLMENT_SERVICE;
			EnrollmentCache enrollmentCache = null;
			enrollmentCacheUtil = new EnrollmentCacheUtil();
			helper.checkSystemTicket(reqHdr);

			if (reqHdr != null && serviceHelperContext.get(CacheConstants.SYSTEMTICKET) != null){
				String subjectId = reqHdr.getSubjectId();
				String clientId = reqHdr.getClientId();
				String cacheKey = EnrollmentCacheUtil.createCacheKey(serviceName, clientId, subjectId);
				EnrollmentCacheList ecl = (EnrollmentCacheList) distributedCacheUtil.getObjectFromCache(cacheKey);
				enrollmentCache = enrollmentCacheUtil.getEnrollmentCacheForSystemTicket(ecl, reqHdr.getSystemTickets());

				/*
				 * If cache is present for enrollments with reference to the systemTicket get
				 * cached Object and return as response. else If cache is present and
				 * enrollments object is not available then call sTBA and save the return
				 * response to cache else If cache is not available for the given systemTicket
				 * then call sTBA and initiate enrollment cache with return response
				 */
				if (enrollmentCache != null) {
					if( enrollmentCache.getEnrollmentsResponse() != null) {
						EnrollmentsResponse enrollments = enrollmentCache.getEnrollmentsResponse();
						serviceHelperContext.set("responseBody", enrollments);
						httpStatus = HttpStatus.OK;
						isCachedResponse = true;
					} else{
						responseEntity = helper.executeService(String.class);
						httpStatus = responseEntity.getStatusCode();
						enrollmentCache.setEnrollmentsResponse(
							(EnrollmentsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY));
					} 
				} else {
					responseEntity = helper.executeService(String.class);
					httpStatus = responseEntity.getStatusCode();
					enrollmentCache = enrollmentCacheUtil.initializeEnrollmentCache(reqHdr.getSystemTickets(),
							((EnrollmentsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY)), null,
							null, null);
					List<EnrollmentCache> enrollmentCacheList = new ArrayList<EnrollmentCache>();
					if (ecl != null && !(ecl.getEnrollmentCache().isEmpty())) {
						enrollmentCacheList = ecl.getEnrollmentCache();
					}
					enrollmentCacheList.add(enrollmentCache);
					if (ecl == null) {
						ecl = new EnrollmentCacheList();
					}
					ecl.setEnrollmentCache(enrollmentCacheList);
				}
				distributedCacheUtil.saveObjectInCache(cacheKey, ecl);
			} else if (reqHdr != null && serviceHelperContext.get(CacheConstants.SYSTEMTICKET) == null){
				if (istoggleEnrollmentsWithoutSystemTicketCache()) {
					String subjectId = reqHdr.getSubjectId();
					String clientId = reqHdr.getClientId();
					String cacheKey = EnrollmentCacheUtil.createCacheKey(serviceName, clientId, subjectId);
					EnrollmentCacheList ecl = (EnrollmentCacheList) distributedCacheUtil.getObjectFromCache(cacheKey);
					enrollmentCache = enrollmentCacheUtil.getEnrollmentCacheForSystemTicket(ecl, null);

					if (enrollmentCache != null) {
						if(enrollmentCache.getEnrollmentsResponse() != null){
							EnrollmentsResponse enrollments = enrollmentCache.getEnrollmentsResponse();
							serviceHelperContext.set("responseBody", enrollments);
							httpStatus = HttpStatus.OK;
							isCachedResponse = true;
						}else {
							responseEntity = helper.executeService(String.class);
							httpStatus = responseEntity.getStatusCode();
							enrollmentCache.setEnrollmentsResponse(
									(EnrollmentsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY));
						}
							
					} else {
						responseEntity = helper.executeService(String.class);
						httpStatus = responseEntity.getStatusCode();
						enrollmentCache = enrollmentCacheUtil.initializeEnrollmentCache(null,
								((EnrollmentsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY)),
								null, null, null);
						List<EnrollmentCache> enrollmentCacheList = new ArrayList<EnrollmentCache>();
						if (ecl != null && !(ecl.getEnrollmentCache().isEmpty())) {
							enrollmentCacheList = ecl.getEnrollmentCache();
						}
						enrollmentCacheList.add(enrollmentCache);
						if (ecl == null) {
							ecl = new EnrollmentCacheList();
						}
						ecl.setEnrollmentCache(enrollmentCacheList);
					}
					distributedCacheUtil.saveObjectInCache(cacheKey, ecl);

				} else {
					responseEntity = helper.executeService(String.class);
					httpStatus = responseEntity.getStatusCode();
				}
			} else {
				throw new IntegrationException("Invalid request header");
			}

			if (isCachedResponse) {
				serviceHelperContext.set(ServiceHelperContext.ALIGHT_RESPONSE_HEADER,
						enrollmentCacheUtil.getResponseHeaderFromRequest(reqHdr));
			}

			EnrollmentsResponse response;
			response = (EnrollmentsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);
			ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			return new ResponseEntity<EnrollmentsResponse>(response, httpHeaders, httpStatus);

		} catch (IntegrationException e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"IntegrationException in getEnrollment() with {businessProcessClassification}",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), httpStatus);

		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"Exception in getEnrollment() with {businessProcessClassification}", httpRequest.getPathInfo(), e,
					ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), httpStatus);

		}

	}

	@PostMapping(value = "/enrollments/{businessProcessClassification}", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> postEnrollment(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			@PathVariable String businessProcessClassification,
			@RequestParam(required = false, defaultValue = "1800-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") Date effectiveDate,
			@RequestParam(required = false, defaultValue = "false") Boolean isSimulationBusinessProcess) {

		String resource;
		resource = "enrollments";

		EnrollmentsResponse expectedResponse;
		expectedResponse = new EnrollmentsResponse();

		HttpStatusCode httpStatus = HttpStatus.OK;

		try {

			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * and the "resource" name, and appContext. We need the resource name in
			 * situations where we want to control TBA routing based on the resource. This
			 * could also be accomplished in most cases using the systemInstanceId, so the
			 * resource name is optional-you can send in null if you want and it will choose
			 * the default TBA routing for the consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource,
					appContext);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.
			 */
			PostEnrollmentDataHelper helper = new PostEnrollmentDataHelper(serviceHelperContext);

			/*
			 * Attach the RequestParam values to the ServiceHelperContext instance as
			 * name/value pairs.
			 */
			RequestVariableValidationUtility.validateStringElement(businessProcessClassification,
					"businessProcessClassification", true);
			serviceHelperContext.set("businessProcessClassification", businessProcessClassification);
			serviceHelperContext.set("effectiveDate", effectiveDate);
			serviceHelperContext.set("isSimulationBusinessProcess", isSimulationBusinessProcess);

			ResponseEntity<String> responseEntity;

			/*
			 * I don't think this POST should *EVER* return a response *FROM* the Redis
			 * cache.
			 * 
			 */

			responseEntity = helper.executeService(String.class);
			httpStatus = responseEntity.getStatusCode();

			EnrollmentsResponse response;
			response = (EnrollmentsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);
			ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			// Start POST enrollments cache logic to store the response into
			// cache.
			RequestHeader reqHdr = (RequestHeader) serviceHelperContext.get(ServiceHelperContext.ALIGHT_REQUEST_HEADER);

			if (responseHeader != null && responseHeader.getSystemTickets() != null
					&& responseHeader.getSystemTickets().size() > 0) {
				String subjectId = reqHdr.getSubjectId();
				String clientId = reqHdr.getClientId();
				String cacheKey = EnrollmentCacheUtil.createCacheKey(ENROLLMENT_SERVICE, clientId, subjectId);
				EnrollmentCache enrollmentCache = enrollmentCacheUtil
						.initializeEnrollmentCache(responseHeader.getSystemTickets(), response, null, null, null);
				List<EnrollmentCache> enrollmentCacheList = new ArrayList<EnrollmentCache>();
				enrollmentCacheList.add(enrollmentCache);
				EnrollmentCacheList ecl = new EnrollmentCacheList();
				ecl.setEnrollmentCache(enrollmentCacheList);

				distributedCacheUtil.saveObjectInCache(cacheKey, ecl);
			}

			return new ResponseEntity<EnrollmentsResponse>(response, httpHeaders, httpStatus);

		} catch (IntegrationException e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"IntegrationException in postEnrollment() with {businessProcessClassification}",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), httpStatus);

		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"Exception in postEnrollment() with {businessProcessClassification}", httpRequest.getPathInfo(), e,
					ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), httpStatus);

		}

	}

	@GetMapping(value = "/personEnrollments/{businessProcessReferenceId}/eligiblePlans", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getEligiblePlans(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			@PathVariable String businessProcessReferenceId,
			@RequestParam(value = "eligiblePlanCode", required = false) String[] eligiblePlanCodes,
			@RequestParam(value = "planId", required = false) String[] planIds) {

		String resource;
		resource = "eligiblePlans";

		EligiblePlansResponse expectedResponse;
		expectedResponse = new EligiblePlansResponse();

		try {

			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * and the "resource" name, and appContext. We need the resource name in
			 * situations where we want to control TBA routing based on the resource. This
			 * could also be accomplished in most cases using the systemInstanceId, so the
			 * resource name is optional-you can send in null if you want and it will choose
			 * the default TBA routing for the consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource,
					appContext);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Begin the Redis caching solution for eligiblePlans. Without a systemTicket
			 * the microservice will call TBA and create a cache with the return data. With
			 * a systemTicket the microservice will return the cached data if any is found
			 * for the key. if no data is found for the key then a call is made to TBA and
			 * cache is created with that return data.
			 * 
			 * Key: serviceName=enrollments:clientId=<id>:subjectId=<id>:
			 * businessProcessReferenceId=<id> System Ticket is not part of the key but it
			 * is found on the cached object. This allows the same event to be cached if ppt
			 * renters the same enrollment flow in a single session.
			 */
			HttpStatusCode httpStatus;
			httpStatus = null;

			RequestHeader reqHdr = (RequestHeader) serviceHelperContext.get(ServiceHelperContext.ALIGHT_REQUEST_HEADER);
			Boolean foundCache = false;
			String serviceName = ENROLLMENT_SERVICE;
			EnrollmentCacheList ecl = null;
			List<EnrollmentCache> enrollmentCacheList = null;
			EnrollmentCache enrollmentCache = null;
			enrollmentCacheUtil = new EnrollmentCacheUtil();
			if (reqHdr != null && reqHdr.getSystemTickets() != null && reqHdr.getSystemTickets().size() > 0) {
				String subjectId = reqHdr.getSubjectId();
				String clientId = reqHdr.getClientId();
				String cacheKey = EnrollmentCacheUtil.createCacheKey(serviceName, clientId, subjectId);
				ecl = (EnrollmentCacheList) distributedCacheUtil.getObjectFromCache(cacheKey);
				enrollmentCache = enrollmentCacheUtil.getEnrollmentCacheForSystemTicket(ecl, reqHdr.getSystemTickets());
				if (enrollmentCache != null && enrollmentCache.getEligiblePlansResponse() != null) {
					EligiblePlansResponse eligiblePlans = enrollmentCache.getEligiblePlansResponse();
					foundCache = true;
					serviceHelperContext.set("responseBody", eligiblePlans);
				}
			}

			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);

			RequestVariableValidationUtility.validateStringArray(eligiblePlanCodes, "eligiblePlanCode", false);

			RequestVariableValidationUtility.validateStringArray(planIds, "planId", false);

			String[] cleansedEligiblePlanCodes;
			cleansedEligiblePlanCodes = EligiblePlansResponseUtility.removeDuplicates(eligiblePlanCodes);

			String[] cleansedPlanIds;
			cleansedPlanIds = EligiblePlansResponseUtility.removeDuplicates(planIds);

			if (foundCache == false) {
				/*
				 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
				 * will handle all provider-specific stuff, in this case managing the TBA
				 * connector code. In the case of TBA helpers, this class is responsible for
				 * managing 1:many subservice calls.
				 */
				EligiblePlansDataHelper helper = new EligiblePlansDataHelper(serviceHelperContext);

				/*
				 * Attach the RequestParam values to the ServiceHelperContext instance as
				 * name/value pairs.
				 */
				serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);
				serviceHelperContext.set("eligiblePlanCode", cleansedEligiblePlanCodes);
				serviceHelperContext.set("planId", cleansedPlanIds);

				/*
				 * Execute the helper
				 */
				ResponseEntity<String> responseEntity;
				responseEntity = helper.executeService(String.class);

				httpStatus = responseEntity.getStatusCode();

				/*
				 * TODO: Do we need to check httpStatus = HttpStatus.OK before doing any other
				 * processing at this point?
				 */
				ResponseHeader rspnHdr = (ResponseHeader) serviceHelperContext
						.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);
				if (rspnHdr != null && rspnHdr.getSystemTickets() != null && rspnHdr.getSystemTickets().size() > 0) {
					EligiblePlansResponse eligiblePlans = (EligiblePlansResponse) serviceHelperContext
							.get(ServiceHelperContext.RESPONSE_BODY);

					/*
					 * Update the cache with the return data from TBA
					 */
					if (eligiblePlans!=null) {
						String subjectId = "";
						String clientId = "";
						if (reqHdr !=null) {
							subjectId = reqHdr.getSubjectId();
							clientId = reqHdr.getClientId();
						}

						// if ecl is null, we didn't find anything
						// in the cache for the cacheKey. Create a new ecl to
						// save.
						if (ecl ==null) {
							ecl = new EnrollmentCacheList();
						}

						// if enrollmentCache is null, we didn't find anything
						// in the cache. Create a new enrollmentCacheList &
						// enrollmentCache to save.
						if (enrollmentCache ==null) {
							enrollmentCache = enrollmentCacheUtil.initializeEnrollmentCache(rspnHdr.getSystemTickets(),
									null, eligiblePlans, null, null);
							enrollmentCacheList = new ArrayList<EnrollmentCache>();
							enrollmentCacheList.add(enrollmentCache);
							ecl.setEnrollmentCache(enrollmentCacheList);
						} else {
							enrollmentCache.setEligiblePlansResponse(eligiblePlans);
						}

						String cacheKey = EnrollmentCacheUtil.createCacheKey(serviceName, clientId, subjectId);

						distributedCacheUtil.saveObjectInCache(cacheKey, ecl);
					}
				}
			} else {

				httpStatus = HttpStatus.OK;

			}
			/*
			 * Do any response-specific work like setting the ResponseHeader. In this
			 * specific case, we want to filter out any plans that don't match our set of
			 * cleansed Eligible Plan Codes and our set of cleansed PlanIds
			 */
			EligiblePlansResponse response;
			response = (EligiblePlansResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);

			EligiblePlansResponse filteredEligiblePlansResponse;
			filteredEligiblePlansResponse = EligiblePlansResponseUtility
					.filterEligiblePlansResponseForRequestParams(response, cleansedEligiblePlanCodes, cleansedPlanIds);

			ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);

			/*
			 * If we get the return data from cache then we aren't provided with a response
			 * header. Populate it with the system ticket information as it would be in a
			 * normal eligiblePlans call to TBA.
			 */
			if (foundCache) {
				responseHeader = enrollmentCacheUtil.getResponseHeaderFromRequest(reqHdr);
			}

			if (responseHeader != null && responseHeader.getSystemTickets() != null
					&& responseHeader.getSystemTickets().size() > 0) {
				String subjectId = reqHdr.getSubjectId();
				String clientId = reqHdr.getClientId();
				String cacheKey = EnrollmentCacheUtil.createCacheKey(serviceName, clientId, subjectId);
				ecl = (EnrollmentCacheList) distributedCacheUtil.getObjectFromCache(cacheKey);

				enrollmentCache = enrollmentCacheUtil.getEnrollmentCacheForSystemTicket(ecl,
						responseHeader.getSystemTickets());

				String enrollmentResource = "enrollments";
				EnrollmentsResponse enrollmentExpectedResponse;
				enrollmentExpectedResponse = new EnrollmentsResponse();
				ServiceHelperContext enrollmentsServiceHelperContext = new ServiceHelperContext(httpRequest,
						httpResponse, enrollmentResource, appContext);
				RequestHeader enrollReqHdr = (RequestHeader) enrollmentsServiceHelperContext
						.get(ServiceHelperContext.ALIGHT_REQUEST_HEADER);
				enrollReqHdr.setSystemTickets(responseHeader.getSystemTickets());
				enrollmentsServiceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, enrollmentExpectedResponse);
				EnrollmentDataHelper enrollmentHelper = new EnrollmentDataHelper(enrollmentsServiceHelperContext);
				if (enrollmentCache != null && enrollmentCache.getEnrollmentsResponse() == null) {
					getEnrollmentsResponse(enrollmentHelper, enrollmentsServiceHelperContext,
							businessProcessReferenceId);
					enrollmentCache.setEnrollmentsResponse((EnrollmentsResponse) enrollmentsServiceHelperContext
							.get(ServiceHelperContext.RESPONSE_BODY));
				} else if (enrollmentCache == null) {
					getEnrollmentsResponse(enrollmentHelper, enrollmentsServiceHelperContext,
							businessProcessReferenceId);
					enrollmentCache = enrollmentCacheUtil.initializeEnrollmentCache(reqHdr.getSystemTickets(),
							((EnrollmentsResponse) enrollmentsServiceHelperContext
									.get(ServiceHelperContext.RESPONSE_BODY)),
							null, null, null);
					enrollmentCacheList = new ArrayList<EnrollmentCache>();
					if (ecl != null && !(ecl.getEnrollmentCache().isEmpty())) {
						enrollmentCacheList = ecl.getEnrollmentCache();
					}
					enrollmentCacheList.add(enrollmentCache);
					if (ecl == null) {
						ecl = new EnrollmentCacheList();
					}
					ecl.setEnrollmentCache(enrollmentCacheList);
				}
				distributedCacheUtil.saveObjectInCache(cacheKey, ecl);
			}
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			return new ResponseEntity<EligiblePlansResponse>(filteredEligiblePlansResponse, httpHeaders, httpStatus);

		} catch (RuntimeException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "Runtime Exception : ",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"IntegrationException in getEligiblePlans() with {businessProcessReferenceId}",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"Exception in getEligiblePlans() with {businessProcessReferenceId}", httpRequest.getPathInfo(), e,
					ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
	
	@GetMapping(value = "/personEnrollments/{businessProcessReferenceId}/eligibleOffers", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getEligibleOffers(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			@PathVariable String businessProcessReferenceId,
			@RequestParam(value = "planId", required = false) String planId) {
		
		String resource;
		resource = "eligibleOffers";

		EligibleOffersResponse expectedResponse;
		expectedResponse = new EligibleOffersResponse();

		HttpStatusCode httpStatus = HttpStatus.OK;

		try {

			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * and the "resource" name, and app5Context. We need the resource name in
			 * situations where we want to control TBA routing based on the resource. This
			 * could also be accomplished in most cases using the systemInstanceId, so the
			 * resource name is optional-you can send in null if you want and it will choose
			 * the default TBA routing for the consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource,
					appContext);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);
			
			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.
			 */
			EligibleOffersDataHelper helper = new EligibleOffersDataHelper(serviceHelperContext);
            
			/*
			 * Attach the RequestParam values to the ServiceHelperContext instance as
			 * name/value pairs.
			 */
			
			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);


			serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);
			serviceHelperContext.set("planId", planId);
		
							
			ResponseEntity<String> responseEntity;
			
			responseEntity = helper.executeService(String.class);

			httpStatus = responseEntity.getStatusCode();
			EligibleOffersResponse response = (EligibleOffersResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);
			ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());
			return new ResponseEntity<EligibleOffersResponse>(response, httpHeaders, httpStatus);
			
			
		} catch (IntegrationException e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"IntegrationException in eligibleOffers() ",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), httpStatus);

		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"Exception in eligibleOffers() ", httpRequest.getPathInfo(), e,
					ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), httpStatus);

		}

	}

	private void getEnrollmentsResponse(EnrollmentDataHelper enrollmentHelper,
			ServiceHelperContext enrollmentsServiceHelperContext, String businessProcessReferenceId) {
		enrollmentHelper.executeService(String.class);
		EnrollmentsResponse response = (EnrollmentsResponse) enrollmentsServiceHelperContext
				.get(ServiceHelperContext.RESPONSE_BODY);
		if (response!=null && response.getEnrollments().size() > 1) {
			List<EnrollmentsResponseEnrollments> enrollList = response
					.getEnrollments().stream().filter(s -> s.getPendingPersonBusinessProcess()
							.getBusinessProcessReferenceId().equalsIgnoreCase(businessProcessReferenceId))
					.collect(Collectors.toList());
			response.setEnrollments(enrollList);
		}
		enrollmentsServiceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, response);
	}

	@GetMapping(value = "/personEnrollments/{businessProcessReferenceId}/pendingElections", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getPendingElections(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			@PathVariable String businessProcessReferenceId,
			@RequestParam(value = "planId", required = false) String[] planIds,
			@RequestParam(value="bundleType", required= false) String bundleType) {

		String resource;
		resource = "pendingElections";

		PendingElectionsResponse expectedResponse;
		expectedResponse = new PendingElectionsResponse();

		try {

			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * the "resource" name, and appContext. We need the resource name in situations
			 * where we want to control TBA routing based on the resource. This could also
			 * be accomplished in most cases using the systemInstanceId, so the resource
			 * name is optional-you can send in null if you want and it will choose the
			 * default TBA routing for the consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext;
			serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource, appContext);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.
			 */
			PendingElectionsDataHelper helper;
			helper = new PendingElectionsDataHelper(serviceHelperContext);

			/*
			 * Attach the RequestParam values to the ServiceHelperContext instance as
			 * name/value pairs.
			 */
			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);

			RequestVariableValidationUtility.validateStringArray(planIds, "planId", false);

			serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);
			serviceHelperContext.set("planId", planIds);
			serviceHelperContext.set("bundleType", bundleType);

			// /*
			// * Execute the helper
			// */
			ResponseEntity<String> responseEntity;
			// responseEntity = helper.executeService(String.class);

			/*
			 * Do any response-specific work like setting the ResponseHeader.
			 */
			HttpStatusCode httpStatus = null;
			// httpStatus = responseEntity.getStatusCode();

			PendingElectionsResponse response = null;
			// response = (PendingElectionsResponse)
			// serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);

			/*
			 * TODO: Do we need to check httpStatus = HttpStatus.OK before doing any other
			 * processing at this point?
			 */

			/*
			 * We are getting cached data to determine what we can do depending on various
			 * cases
			 */
			enrollmentCacheUtil = new EnrollmentCacheUtil();
			String serviceName = ENROLLMENT_SERVICE;
			String subjectId = "";
			String clientId = "";
			ArrayList<SystemTicket> systemTickets = new ArrayList<SystemTicket>();
			RequestHeader reqHdr = (RequestHeader) serviceHelperContext.get(ServiceHelperContext.ALIGHT_REQUEST_HEADER);
			if (reqHdr !=null) {
				subjectId = reqHdr.getSubjectId();
				clientId = reqHdr.getClientId();
				systemTickets = reqHdr.getSystemTickets();
			}
			if(bundleType!=null && (bundleType.equals(EnrollmentConstants.PRE)||bundleType.equals(EnrollmentConstants.POST))){
				//make the sTBA Call 
			responseEntity = helper.executeService(String.class);
			httpStatus = responseEntity.getStatusCode();
			response = (PendingElectionsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);
			PendingElectionsResponse tbaPendingElectionsResponse = (PendingElectionsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, tbaPendingElectionsResponse);
			ResponseHeader responseHeader;
			responseHeader = (ResponseHeader) serviceHelperContext.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);
			HttpHeaders httpHeaders;
			httpHeaders = new HttpHeaders();
			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());
			if(serviceHelperContext.get("planId")!= null && tbaPendingElectionsResponse != null) {
				PendingElectionsResponse  acePendingElectionsResponse= new PendingElectionsResponse();
				List<PendingElectionsResponseCoverages> tbaPendingElectionCoverages = tbaPendingElectionsResponse.getCoverages();
				List<PendingElectionsResponseCoverages> acePendingElectionCoverages = new ArrayList<>();
				String[] inputPlanIds;
				inputPlanIds = (String[]) serviceHelperContext.get("planId");
				Set<String> planIdSet = new HashSet<>(Arrays.asList(inputPlanIds));
				
			    acePendingElectionCoverages =  tbaPendingElectionCoverages.stream()
			            .filter(cov -> planIdSet.contains(cov.getPlanId()))
			            .collect(Collectors.toList());
			    
			    acePendingElectionsResponse.setCoverages(acePendingElectionCoverages);
			    if (tbaPendingElectionsResponse.getDependents() != null
						&& tbaPendingElectionsResponse.getDependents().size() > 0) {
				    acePendingElectionsResponse.setDependents(tbaPendingElectionsResponse.getDependents());

				}
			    
			    // Sort dependents by relationship sequence.
				if (acePendingElectionsResponse.getDependents() == null || acePendingElectionsResponse.getDependents().size() <= 1) {
					// Do nothing.  There is no List<PendingElectionsResponseDependents> that requires sorting.
				}
				else {
					acePendingElectionsResponse.getDependents()
					.sort(Comparator.comparing(PendingElectionsResponseDependents::getRelationshipSequence));			
				}
				
				// Pass on footnotes
				acePendingElectionsResponse.setFootnotes(tbaPendingElectionsResponse.getFootnotes());
				acePendingElectionsResponse.setCustomText(tbaPendingElectionsResponse.getCustomText());
				
				if (tbaPendingElectionsResponse.getTotalPriceComponents() != null) {
					// Pass on total price
					acePendingElectionsResponse.setTotalPriceComponents(tbaPendingElectionsResponse.getTotalPriceComponents());
				}
				if (acePendingElectionsResponse != null) {
					if (acePendingElectionsResponse.getCoverages() != null && acePendingElectionsResponse.getCoverages().size() == 0) {
						acePendingElectionsResponse.setCoverages(null);
					}
					if (acePendingElectionsResponse.getDependents() != null && acePendingElectionsResponse.getDependents().size() == 0) {
						acePendingElectionsResponse.setDependents(null);
					}
				}
				
				serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, acePendingElectionsResponse);			
			}		

			return new ResponseEntity<PendingElectionsResponse>(
					(PendingElectionsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY),
					httpHeaders, httpStatus);		
			}
			
			String cacheKey = EnrollmentCacheUtil.createCacheKey(serviceName, clientId, subjectId);
			EnrollmentCacheList ecl = (EnrollmentCacheList) distributedCacheUtil.getObjectFromCache(cacheKey);
			EnrollmentCache enrollmentCache = enrollmentCacheUtil.getEnrollmentCacheForSystemTicket(ecl, systemTickets);

			// enable cache if the config is ON(true)
			if (isTogglePendingElectionsCache()) {

				// if the cache is present and pendingElections exists, update
				// existing data
				// if cache is present but no pendingElections, use the response
				// as the new pendingElections cached object
				// otherwise if cache is not present then create it and use the
				// response as the pendingElections cached object.
				if (enrollmentCache != null && enrollmentCache.getPendingElectionsResponse() != null){
					// If the pendingElections data is present,
					// If flag(refreshPendingElections) is found,
					// call sTBA
					if (enrollmentCache.isRefreshPendingElections()) {
						/*
						 * Execute the helper
						 */
						responseEntity = helper.executeService(String.class);

						httpStatus = responseEntity.getStatusCode();

						response = (PendingElectionsResponse) serviceHelperContext
								.get(ServiceHelperContext.RESPONSE_BODY);

						enrollmentCache.setRefreshPendingElections(false);

						// call function to replace plans that came from TBA
						// response
						PendingElectionsResponse pendingElectionsFinal = enrollmentCacheUtil
								.updatePendingElectionsCacheWithTBAResponse(response,
										enrollmentCache.getPendingElectionsResponse(), serviceHelperContext);
						// replace existing object with new object
						enrollmentCache.setPendingElectionsResponse(pendingElectionsFinal);
						response = pendingElectionsFinal;
					} else {
						// If there is an input planId, and planId is NOT found
						// call sTBA via executeHelper
						if (!ObjectUtils.isEmpty(planIds)
								&& !enrollmentCache.getPendingElectionsResponse().getCoverages().isEmpty()) {
							boolean planNotFound = false;
							List<String> cachedPlanIds = new ArrayList<String>();
							List<PendingElectionsResponseCoverages> cachedCoverages = enrollmentCache
									.getPendingElectionsResponse().getCoverages();
							for (PendingElectionsResponseCoverages coverage : cachedCoverages) {
								cachedPlanIds.add(coverage.getPlanId());
							}
							for (String planId : planIds) {
								if (!cachedPlanIds.contains(planId)) {
									planNotFound = true;
								}
							}

							if (planNotFound) {
								/*
								 * Execute the helper
								 */
								responseEntity = helper.executeService(String.class);

								httpStatus = responseEntity.getStatusCode();

								response = (PendingElectionsResponse) serviceHelperContext
										.get(ServiceHelperContext.RESPONSE_BODY);

								// call function to replace plans that came from
								// TBA
								// response
								PendingElectionsResponse pendingElectionsFinal = enrollmentCacheUtil
										.updatePendingElectionsCacheWithTBAResponse(response,
												enrollmentCache.getPendingElectionsResponse(), serviceHelperContext);
								// replace existing object with new object
								enrollmentCache.setPendingElectionsResponse(pendingElectionsFinal);
								response = pendingElectionsFinal;
							} else {
								// If pendingElections response is found,
								// and input planId found,
								// then do not run executeService
								response = enrollmentCache.getPendingElectionsResponse();
								httpStatus = HttpStatus.OK;
							}
						} else if (enrollmentCache.getPendingElectionsResponse().getCoverages().isEmpty()) {
							// If pendingElections response is found,
							// and coverage is empty,
							// run executeService
							/*
							 * Execute the helper
							 */
							responseEntity = helper.executeService(String.class);

							httpStatus = responseEntity.getStatusCode();

							response = (PendingElectionsResponse) serviceHelperContext
									.get(ServiceHelperContext.RESPONSE_BODY);

							// call function to replace plans that came from TBA
							// response
							PendingElectionsResponse pendingElectionsFinal = enrollmentCacheUtil
									.updatePendingElectionsCacheWithTBAResponse(response,
											enrollmentCache.getPendingElectionsResponse(), serviceHelperContext);
							// replace existing object with new object
							enrollmentCache.setPendingElectionsResponse(pendingElectionsFinal);
							response = pendingElectionsFinal;
						} else {
							// If pendingElections response is found,
							// and coverage is not empty,
							// then do not run executeService
							response = enrollmentCache.getPendingElectionsResponse();
							httpStatus = HttpStatus.OK;
						}
					}

				} else {
					/*
					 * Execute the helper
					 */
					responseEntity = helper.executeService(String.class);

					httpStatus = responseEntity.getStatusCode();

					response = (PendingElectionsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);

					if (enrollmentCache != null && enrollmentCache.getPendingElectionsResponse() == null) {
						enrollmentCache.setPendingElectionsResponse(response);
					} else {
						// create new cache
						enrollmentCache = enrollmentCacheUtil.initializeEnrollmentCache(reqHdr.getSystemTickets(), null,
								null, response, null);
						ArrayList<EnrollmentCache> enrollmentCacheList = new ArrayList<EnrollmentCache>();
						enrollmentCacheList.add(enrollmentCache);
						if (ecl ==null) {
							ecl = new EnrollmentCacheList();
						}
						ecl.setEnrollmentCache(enrollmentCacheList);
					}
				}

			} else {
				/*
				 * Execute the helper
				 */
				responseEntity = helper.executeService(String.class);

				httpStatus = responseEntity.getStatusCode();

				response = (PendingElectionsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);
				if (enrollmentCache != null && enrollmentCache.getPendingElectionsResponse() != null) {
					// call function to replace plans that came from TBA
					// response
					PendingElectionsResponse pendingElectionsFinal = enrollmentCacheUtil
							.updatePendingElectionsCacheWithTBAResponse(response,
									enrollmentCache.getPendingElectionsResponse(), serviceHelperContext);
					// replace existing object with new object
					enrollmentCache.setPendingElectionsResponse(pendingElectionsFinal);
					response = pendingElectionsFinal;
				} else {
					if (enrollmentCache != null && enrollmentCache.getPendingElectionsResponse() == null) {
						enrollmentCache.setPendingElectionsResponse(response);
					} else {
						// create new cache
						enrollmentCache = enrollmentCacheUtil.initializeEnrollmentCache(reqHdr.getSystemTickets(), null,
								null, response, null);
						ArrayList<EnrollmentCache> enrollmentCacheList = new ArrayList<EnrollmentCache>();
						enrollmentCacheList.add(enrollmentCache);
						if (ecl == null) {
							ecl = new EnrollmentCacheList();
						}
						ecl.setEnrollmentCache(enrollmentCacheList);
					}
				}
			}

			distributedCacheUtil.saveObjectInCache(cacheKey, ecl);

			// Only output input plans if plan array is populated
			if (!ObjectUtils.isEmpty(planIds) ) {
				List<PendingElectionsResponseCoverages> tempCoverages = response.getCoverages();
				List<PendingElectionsResponseCoverages> finalCoverages = new ArrayList<PendingElectionsResponseCoverages>();
				boolean foundPlan = false;
				if (tempCoverages != null) {
					for (int i = 0; i < tempCoverages.size(); i++) {
						foundPlan = false;
						for (int j = 0; j < planIds.length && !foundPlan; j++) {

							if (tempCoverages.get(i).getPlanId().equals(planIds[j])) {
								finalCoverages.add(tempCoverages.get(i));
								foundPlan = true;
							}
						}
					}
				}
				response.setCoverages(finalCoverages);
			}

			if (response != null) {
				if (response.getCoverages() != null && response.getCoverages().size() == 0) {
					response.setCoverages(null);
				}
				if (response.getDependents() != null && response.getDependents().size() == 0) {
					response.setDependents(null);
				}
			}
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, response);
			ResponseHeader responseHeader;
			responseHeader = (ResponseHeader) serviceHelperContext.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);

			HttpHeaders httpHeaders;
			httpHeaders = new HttpHeaders();
			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			return new ResponseEntity<PendingElectionsResponse>(
					(PendingElectionsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY),
					httpHeaders, httpStatus);

		} catch (RuntimeException e) {

			throw e;

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"IntegrationException in getPendingElections() with {businessProcessReferenceId}",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"Exception in getPendingElections() with {businessProcessReferenceId}", httpRequest.getPathInfo(),
					e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@GetMapping(value = "/personEnrollments/{businessProcessReferenceId}/suggestionSupport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getSuggestionSupport(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			@PathVariable String businessProcessReferenceId,
			@RequestParam(value = "planId", required = false) String[] planIds,
			@RequestParam(value = "suggestionSupportType", required = false) String[] suggestionSupportTypes) {

		String resource;
		resource = "suggestionSupport";

		SuggestionSupportResponse expectedResponse;
		expectedResponse = new SuggestionSupportResponse();

		try {

			RequestVariableValidationUtility.validateStringArray(planIds, "planId", false);

			RequestVariableValidationUtility.validateStringArray(suggestionSupportTypes, "suggestionSupportType",
					false);

			String[] cleansedPlanIds;
			cleansedPlanIds = EligiblePlansResponseUtility.removeDuplicates(planIds);

			String[] cleansedSuggestionSupportTypes;
			cleansedSuggestionSupportTypes = EligiblePlansResponseUtility.removeDuplicates(suggestionSupportTypes);

			for (int i = 0; cleansedSuggestionSupportTypes != null && i < cleansedSuggestionSupportTypes.length; i++) {
				switch (cleansedSuggestionSupportTypes[i]) {
				case SuggestionSupportType.ALL:
				case SuggestionSupportType.CONTENTCARDS:
				case SuggestionSupportType.PLANELECTIONCHOICETYPE:
				case SuggestionSupportType.STORYBOOKS:
				case SuggestionSupportType.SUGGESTIONS:
					break;
				default:
					throw new IntegrationException(IntegrationExceptionConstants.INVALID_REQUEST_PARAMETER);

				}

			}

			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * the "resource" name, and appContext. We need the resource name in situations
			 * where we want to control TBA routing based on the resource. This could also
			 * be accomplished in most cases using the systemInstanceId, so the resource
			 * name is optional-you can send in null if you want and it will choose the
			 * default TBA routing for the consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext;
			serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource, appContext);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.
			 */
			SuggestionSupportDataHelper helper;
			helper = new SuggestionSupportDataHelper(serviceHelperContext);

			/*
			 * Attach the RequestParam values to the ServiceHelperContext instance as
			 * name/value pairs.
			 */
			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);

			serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);
			serviceHelperContext.set("planId", cleansedPlanIds);
			serviceHelperContext.set("suggestionSupportType", cleansedSuggestionSupportTypes);

			/*
			 * Execute the helper
			 */
			ResponseEntity<String> responseEntity;
			responseEntity = helper.executeService(String.class);

			/*
			 * Do any response-specific work like setting the ResponseHeader.
			 */
			HttpStatusCode httpStatus;
			httpStatus = responseEntity.getStatusCode();

			SuggestionSupportResponse response;
			response = (SuggestionSupportResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);

			ResponseHeader responseHeader;
			responseHeader = (ResponseHeader) serviceHelperContext.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);

			HttpHeaders httpHeaders;
			httpHeaders = new HttpHeaders();
			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			return new ResponseEntity<SuggestionSupportResponse>(response, httpHeaders, httpStatus);

		} catch (RuntimeException e) {

			throw e;

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"IntegrationException in getSuggestionSupport()", httpRequest.getPathInfo(), e,
					ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"Exception in getSuggestionSupport() with {businessProcessReferenceId}", httpRequest.getPathInfo(),
					e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@GetMapping(value = "/personEnrollments/{businessProcessReferenceId}/eligibleDependents", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getEligibleDependents(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			@PathVariable String businessProcessReferenceId,
			@RequestParam(value = "planId", required = false) String[] planIds,
			@RequestParam(value = "relationshipSequence", required = false) String rltnSq,
			@RequestParam(required = false) String planListing) {

		String resource;
		resource = "eligibleDependents";

		EligibleDependentsResponse expectedResponse;
		expectedResponse = new EligibleDependentsResponse();

		try {

			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * the "resource" name, and appContext. We need the resource name in situations
			 * where we want to control TBA routing based on the resource. This could also
			 * be accomplished in most cases using the systemInstanceId, so the resource
			 * name is optional-you can send in null if you want and it will choose the
			 * default TBA routing for the consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext;
			serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource, appContext);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			RequestVariableValidationUtility.validatePlanListing(planListing);
			serviceHelperContext.set("planListing", planListing);

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.
			 */
			EligibleDependentsDataHelper helper;
			helper = new EligibleDependentsDataHelper(serviceHelperContext);

			/*
			 * Attach the RequestParam values to the ServiceHelperContext instance as
			 * name/value pairs.
			 */
			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);

			RequestVariableValidationUtility.validateStringArray(planIds, "planId", false);
			RequestVariableValidationUtility.validateIntegerAsStringElement(rltnSq, "relationshipSequence", false);

			serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);
			serviceHelperContext.set("planId", planIds);
			serviceHelperContext.set("rltnSq", rltnSq);

			/*
			 * Execute the helper
			 */
			ResponseEntity<String> responseEntity;
			responseEntity = helper.executeService(String.class);

			/*
			 * Do any response-specific work like setting the ResponseHeader.
			 */
			HttpStatusCode httpStatus;
			httpStatus = responseEntity.getStatusCode();

			EligibleDependentsResponse response;
			response = (EligibleDependentsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);

			ResponseHeader responseHeader;
			responseHeader = (ResponseHeader) serviceHelperContext.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);

			HttpHeaders httpHeaders;
			httpHeaders = new HttpHeaders();

			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			return new ResponseEntity<EligibleDependentsResponse>(response, httpHeaders, httpStatus);

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"IntegrationException in getEligibleDependents() with {businessProcessReferenceId}",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"Exception in getEligibleDependents() with {businessProcessReferenceId}", httpRequest.getPathInfo(),
					e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@GetMapping(value = "/personEnrollments/{businessProcessReferenceId}/plan/{planId}/availableProviderNetworks", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getAvailableProviderNetworks(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse, @PathVariable String businessProcessReferenceId,
			@PathVariable String planId, @RequestParam(required = false) String viewCode) {

		String resource;
		resource = "availableProviderNetworks";

		ProviderNetworksResponse expectedResponse;
		expectedResponse = new ProviderNetworksResponse();

		try {

			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * and the "resource" name, and appContext. We need the resource name in
			 * situations where we want to control TBA routing based on the resource. This
			 * could also be accomplished in most cases using the systemInstanceId, so the
			 * resource name is optional-you can send in null if you want and it will choose
			 * the default TBA routing for the consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource,
					appContext);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.
			 */
			ProviderNetworksDataHelper helper = new ProviderNetworksDataHelper(serviceHelperContext);

			/*
			 * Attach the RequestParam values to the ServiceHelperContext instance as
			 * name/value pairs.
			 */
			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);

			RequestVariableValidationUtility.validateStringElement(planId, "planId", true);

			RequestVariableValidationUtility.validateStringElement(viewCode, "viewCode", false);

			serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);
			serviceHelperContext.set("planId", planId);
			serviceHelperContext.set("viewCode", viewCode);

			/*
			 * Execute the helper
			 */
			ResponseEntity<String> responseEntity;
			responseEntity = helper.executeService(String.class);

			/*
			 * Do any response-specific work like setting the ResponseHeader.
			 */
			HttpStatusCode httpStatus;
			httpStatus = responseEntity.getStatusCode();

			ProviderNetworksResponse response;
			response = (ProviderNetworksResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);

			ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			return new ResponseEntity<ProviderNetworksResponse>(response, httpHeaders, httpStatus);

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"IntegrationException in getAvailableProviderNetworks()", httpRequest.getPathInfo(), e,
					ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "Exception in getAvailableProviderNetworks()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@GetMapping(value = "/personEnrollments/{businessProcessReferenceId}/plan/{planId}/primaryCareProviderElections", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getPrimaryCareProviderElections(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse, @PathVariable String businessProcessReferenceId,
			@PathVariable String planId) {

		String resource;
		resource = "availableProviderNetworks";

		PrimaryCareProvidersResponse expectedResponse;
		expectedResponse = new PrimaryCareProvidersResponse();

		try {
			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * and the "resource" name, and appContext. We need the resource name in
			 * situations where we want to control TBA routing based on the resource. This
			 * could also be accomplished in most cases using the systemInstanceId, so the
			 * resource name is optional-you can send in null if you want and it will choose
			 * the default TBA routing for the consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource,
					appContext);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.
			 */
			PrimaryCareProviderElectionsDataHelper helper = new PrimaryCareProviderElectionsDataHelper(
					serviceHelperContext);

			/*
			 * Attach the RequestParam values to the ServiceHelperContext instance as
			 * name/value pairs.
			 */
			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);

			RequestVariableValidationUtility.validateStringElement(planId, "planId", true);

			serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);
			serviceHelperContext.set("planId", planId);

			/*
			 * Execute the helper
			 */
			ResponseEntity<String> responseEntity;
			responseEntity = helper.executeService(String.class);

			/*
			 * Do any response-specific work like setting the ResponseHeader.
			 */
			HttpStatusCode httpStatus;
			httpStatus = responseEntity.getStatusCode();

			PrimaryCareProvidersResponse response;
			response = (PrimaryCareProvidersResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);

			ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			return new ResponseEntity<PrimaryCareProvidersResponse>(response, httpHeaders, httpStatus);

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"IntegrationException in getPrimaryCareProviderElections()", httpRequest.getPathInfo(), e,
					ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"Exception in getPrimaryCareProviderElections()", httpRequest.getPathInfo(), e,
					ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@GetMapping(value = "/personEnrollments/{businessProcessReferenceId}/plan/{planId}/availableOptions", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getAvailableOptions(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			@PathVariable String businessProcessReferenceId, @PathVariable String planId) {

		String resource;
		resource = "availableOptions";

		AvailableOptionsResponse expectedResponse;
		expectedResponse = new AvailableOptionsResponse();

		try {
			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * the "resource" name, and appContext. We need the resource name in situations
			 * where we want to control TBA routing based on the resource. This could also
			 * be accomplished in most cases using the systemInstanceId, so the resource
			 * name is optional-you can send in null if you want and it will choose the
			 * default TBA routing for the consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext;
			serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource, appContext);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.
			 */
			AvailableOptionsDataHelper helper = new AvailableOptionsDataHelper(serviceHelperContext);

			/*
			 * Attach the RequestParam values to the ServiceHelperContext instance as
			 * name/value pairs.
			 */
			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);

			RequestVariableValidationUtility.validateStringElement(planId, "planId", true);

			serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);
			serviceHelperContext.set("planId", planId);
			
			ResponseEntity<String> responseEntity;
			AvailableOptionsResponse response = null;
			HttpStatusCode httpStatus = null;

			RequestHeader reqHdr = (RequestHeader) serviceHelperContext.get(ServiceHelperContext.ALIGHT_REQUEST_HEADER);
			Boolean foundCache = false;

			/*
			 * Using app.cache.enrollment.availableOptions config value to stitch between
			 * using cache or not for availableOptions
			 * 
			 * if toggleAvailableOptionsCache = true-cache enabled if
			 * toggleAvailableOptionsCache = false-cache disabled
			 */
			if (isToggleAvailableOptionsCache()) {				
				/*
				 * Update the cache with the return data from TBA. Fetch the cache with the key
				 * to see if one exists today. If one does exist, and system ticket matches,
				 */
				EnrollmentCacheUtil enrollmentCacheUtil = new EnrollmentCacheUtil();
				String serviceName = ENROLLMENT_SERVICE;
				String subjectId = "";
				String clientId = "";
				ArrayList<SystemTicket> systemTickets = new ArrayList<SystemTicket>();
				if (reqHdr != null) {
					subjectId = reqHdr.getSubjectId();
					clientId = reqHdr.getClientId();
					systemTickets = reqHdr.getSystemTickets();
				}
				String cacheKey = EnrollmentCacheUtil.createCacheKey(serviceName, clientId, subjectId);
				EnrollmentCacheList ecl = (EnrollmentCacheList) distributedCacheUtil.getObjectFromCache(cacheKey);
				EnrollmentCache enrollmentCache = enrollmentCacheUtil.getEnrollmentCacheForSystemTicket(ecl,
						systemTickets);
				// if the cache is present and availableOptions exists, use
				// existing cached data
				// if cache is present but no availableOptions, use the response
				// as the new availableOptions cached object
				// otherwise if cache is not present then create it and use
				// TBA return as the availableOptions cached object.
				if (enrollmentCache != null && enrollmentCache.getAvailableOptionsResponseList() != null){
					boolean planFound = false;
					List<AvailableOptionsResponse> avlOptnRspnListCache = enrollmentCache
							.getAvailableOptionsResponseList();

					for (AvailableOptionsResponse avlOptnRspn : avlOptnRspnListCache) {
						if (avlOptnRspn.getChoices().get(0).getPlanId().equals(planId)) {
							response = avlOptnRspn;
							httpStatus = HttpStatus.OK;
							planFound = true;
							foundCache = true;
							break;
						}
					}
					
					if (!planFound) {
						/*
						 * Execute the helper
						 */
						responseEntity = helper.executeService(String.class);
						httpStatus = responseEntity.getStatusCode();
						response = (AvailableOptionsResponse) serviceHelperContext
								.get(ServiceHelperContext.RESPONSE_BODY);
						if (!response.getChoices().isEmpty()) {
							avlOptnRspnListCache.add(response);
							enrollmentCache.setAvailableOptionsResponseList(avlOptnRspnListCache);
						}
					}
				

				} else if (enrollmentCache != null && enrollmentCache.getAvailableOptionsResponseList() == null){
					// call function to replace plans that came from TBA
					// response
					List<AvailableOptionsResponse> availableOptionsResponseList = new ArrayList<AvailableOptionsResponse>();
					/*
					 * Execute the helper
					 */
					responseEntity = helper.executeService(String.class);
					httpStatus = responseEntity.getStatusCode();
					response = (AvailableOptionsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);
					if (!response.getChoices().isEmpty()) {
						availableOptionsResponseList.add(response);
						enrollmentCache.setAvailableOptionsResponseList(availableOptionsResponseList);
					}

				} else {
					// create new cache
					List<AvailableOptionsResponse> availableOptionsResponseList = new ArrayList<AvailableOptionsResponse>();

					/*
					 * Execute the helper
					 */
					responseEntity = helper.executeService(String.class);
					httpStatus = responseEntity.getStatusCode();
					response = (AvailableOptionsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);
					if (!response.getChoices().isEmpty()) {
						availableOptionsResponseList.add(response);
						enrollmentCache = enrollmentCacheUtil.initializeEnrollmentCache(reqHdr.getSystemTickets(), null,
								null, null, availableOptionsResponseList);
					}
					ArrayList<EnrollmentCache> enrollmentCacheList = new ArrayList<EnrollmentCache>();
					enrollmentCacheList.add(enrollmentCache);
					if (ecl == null) {
						ecl = new EnrollmentCacheList();
					}
					ecl.setEnrollmentCache(enrollmentCacheList);
				}

				distributedCacheUtil.saveObjectInCache(cacheKey, ecl);
			} else {
				/*
				 * Execute the helper
				 */
				responseEntity = helper.executeService(String.class);
				httpStatus = responseEntity.getStatusCode();
				response = (AvailableOptionsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);
			}

			/*
			 * Do any response-specific work like setting the ResponseHeader.
			 */

			ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);
			if (foundCache) {
				ArrayList<SystemTicket> systemTickets = new ArrayList<SystemTicket>();
				SystemTicket systemTicket = new SystemTicket();
				String key = reqHdr.getSystemTickets().get(0).getKey();
				String value = reqHdr.getSystemTickets().get(0).getValue();
				if (!reqHdr.getSystemTickets().isEmpty() && (!key.isEmpty() || !value.isEmpty())) {
					systemTicket.setKey(reqHdr.getSystemTickets().get(0).getKey());
					systemTicket.setValue(reqHdr.getSystemTickets().get(0).getValue());
				}
				systemTickets.add(systemTicket);
				responseHeader.setSystemTickets(systemTickets);
				responseHeader.setResponseDescription("");
				responseHeader.setResponseCode("0");
			}

			HttpHeaders httpHeaders;
			httpHeaders = new HttpHeaders();
			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			return new ResponseEntity<AvailableOptionsResponse>(response, httpHeaders, httpStatus);

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "Exception in getAvailableOptions()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "Exception in getAvailableOptions() ",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PutMapping(value = "/personEnrollments/{businessProcessReferenceId}/validate", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> putValidate(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			@PathVariable String businessProcessReferenceId) {

		String resource;
		resource = "validate";

		PendingElectionsResponse expectedResponse;
		expectedResponse = new PendingElectionsResponse();
		
		try {

			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * the "resource" name, and appContext. We need the resource name in situations
			 * where we want to control TBA routing based on the resource. This could also
			 * be accomplished in most cases using the systemInstanceId, so the resource
			 * name is optional-you can send in null if you want and it will choose the
			 * default TBA routing for the consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext;
			serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource, appContext);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.
			 */
			ValidateDataHelper helper = new ValidateDataHelper(serviceHelperContext);

			/*
			 * Attach the RequestParam values to the ServiceHelperContext instance as
			 * name/value pairs.
			 */
			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);

			serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);

			/*
			 * Execute the helper
			 */
			ResponseEntity<String> responseEntity;
			responseEntity = helper.executeService(String.class);

			/*
			 * Do any response-specific work like setting the ResponseHeader.
			 */
			HttpStatusCode httpStatus;
			httpStatus = responseEntity.getStatusCode();

			PendingElectionsResponse response;
			
			response = (PendingElectionsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);

			/*
			 * TODO: Do we need to check httpStatus = HttpStatus.OK before doing any other
			 * processing at this point?
			 */
			
			//  TODO - This commented out code is where I tried to encapsulate this logic to check if simulation.
			//         There is something funky with the autowire, cache and static vs non-static calls. Someday
			//         we should figure that out and encapsulate this logic.
			//isEncapsulateSimulationBusinessProcess = TBAConnectorServiceController
			//		.checkIfEnrollmentCachedAndIsSimulation(httpRequest, httpResponse, serviceHelperContext);
			// checkIfEnrollmentCachedAndIsSimulation();

			boolean updateCacheIndicator  = false;
			boolean isSimulationBusinessProcess  = false;
			String businessProcessSummary ="";					

			/* In order to figure out the isSimulationBusinessProcess we need to look at the enrollment that
			 * is in cache and look at the businessProcessSummary. When it is "simulationEnrollment" then we
			 * know that is for a simulation event.
			 * 
			 * Note, eD is short for enrollmentData
			 */
			RequestHeader eDReqHdr = (RequestHeader) serviceHelperContext.get(ServiceHelperContext.ALIGHT_REQUEST_HEADER);
			
			String serviceName = ENROLLMENT_SERVICE;
			
			EnrollmentCache eDEnrollmentCache = null;
			
			EnrollmentDataHelper eDHelper;
			
			try {
				eDHelper = new EnrollmentDataHelper(serviceHelperContext);

				eDHelper.checkSystemTicket(eDReqHdr);
				
				if (eDReqHdr != null && serviceHelperContext.get(CacheConstants.SYSTEMTICKET) != null){

					String subjectId = eDReqHdr.getSubjectId();
					String clientId = eDReqHdr.getClientId();
					String cacheKey = EnrollmentCacheUtil.createCacheKey(serviceName, clientId, subjectId);
					
					EnrollmentCacheList edecl = (EnrollmentCacheList) distributedCacheUtil.getObjectFromCache(cacheKey);					
					eDEnrollmentCache = enrollmentCacheUtil.getEnrollmentCacheForSystemTicket(edecl, eDReqHdr.getSystemTickets());


					if (eDEnrollmentCache != null && eDEnrollmentCache.getEnrollmentsResponse() != null) {

						EnrollmentsResponse enrollments = eDEnrollmentCache.getEnrollmentsResponse();
						
						// loop thru the Enrollments array, looking for a match on businessProcessReferenceId 
						// AKA Act-Ref-Nmbr-Id
						// If we find a match get the businessProcessSummary
						for (int iEnrollmentsAr = 0; iEnrollmentsAr < enrollments.getEnrollments()
								.size(); iEnrollmentsAr++) {

							if (enrollments.getEnrollments().get(iEnrollmentsAr) != null) {
								
								if (enrollments.getEnrollments().get(iEnrollmentsAr).getPendingPersonBusinessProcess() != null) {
								
									if (enrollments.getEnrollments().get(iEnrollmentsAr).getPendingPersonBusinessProcess().getBusinessProcessReferenceId() != null) {
									
										if (enrollments.getEnrollments().get(iEnrollmentsAr).getPendingPersonBusinessProcess()
												.getBusinessProcessReferenceId().equals(businessProcessReferenceId)) {											
										
											businessProcessSummary = enrollments.getEnrollments().get(iEnrollmentsAr)
													.getBusinessProcess().getBusinessProcessSummary();
										}

									}										
								}	
							
							}
							
						}	

						if (businessProcessSummary != null && businessProcessSummary.equals("simulationEnrollment")) {
							isSimulationBusinessProcess = true;
						}

					}
				}				

			} catch (IntegrationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (isSimulationBusinessProcess)
			{
				if (response.getCoverages().size() > 0) {
					updateCacheIndicator  = true;
				}
			}
			else {
				if (response.getCoverages() != null || response.getDependents() != null) {
					updateCacheIndicator  = true;
				} 
			}
				
			if (updateCacheIndicator) {

				/*
				 * Update the cache with the return data from TBA. Fetch the cache with the key
				 * to see if one exists today. If one does exist, and system ticket matches,
				 * update the existing cache. If one does not exist or it does exist but no
				 * matching system ticket, create a new entry.
				 */
				EnrollmentCacheUtil enrollmentCacheUtil = new EnrollmentCacheUtil();
				
				String subjectId = "";
				String clientId = "";
				ArrayList<SystemTicket> systemTickets = new ArrayList<SystemTicket>();
				RequestHeader reqHdr = (RequestHeader) serviceHelperContext
						.get(ServiceHelperContext.ALIGHT_REQUEST_HEADER);
				if (reqHdr != null) {
					subjectId = reqHdr.getSubjectId();
					clientId = reqHdr.getClientId();
					systemTickets = reqHdr.getSystemTickets();
				}
				String cacheKey = EnrollmentCacheUtil.createCacheKey(serviceName, clientId, subjectId);
				EnrollmentCacheList ecl = (EnrollmentCacheList) distributedCacheUtil.getObjectFromCache(cacheKey);
				EnrollmentCache enrollmentCache = enrollmentCacheUtil.getEnrollmentCacheForSystemTicket(ecl,
						systemTickets);
				// if the cache is present replace with TBA response. Validate
				// replaces the whole cache each time.
				// otherwise if cache is not present then create it and use the
				// response as the pendingElections cached object.
				if (enrollmentCache != null) {
					// replace data in cache with TBA response.
					enrollmentCache.setPendingElectionsResponse(response);
				} else {
					// create new cache
					enrollmentCache = enrollmentCacheUtil.initializeEnrollmentCache(reqHdr.getSystemTickets(), null,
							null, response, null);
					ArrayList<EnrollmentCache> enrollmentCacheList = new ArrayList<EnrollmentCache>();
					enrollmentCacheList.add(enrollmentCache);
					if (ecl == null) {
						ecl = new EnrollmentCacheList();
					}
					ecl.setEnrollmentCache(enrollmentCacheList);
				}
				distributedCacheUtil.saveObjectInCache(cacheKey, ecl);

			}

			ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);
			HttpHeaders httpHeaders = new HttpHeaders();

			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			return new ResponseEntity<PendingElectionsResponse>(response, httpHeaders, httpStatus);

		} catch (RuntimeException e) {

			throw e;

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "IntegrationException in putValidate()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "Exception in putValidate()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@GetMapping(value = "/personEnrollments/{businessProcessReferenceId}/personTasks", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getPersonTasks(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			@PathVariable String businessProcessReferenceId,
			@RequestParam(required = false) String[] taskGroupTypes) {

		String resource;
		resource = "personTasks";

		PersonTasksResponse expectedResponse;
		expectedResponse = new PersonTasksResponse();

		try {

			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * and the "resource" name, and appContext. We need the resource name in
			 * situations where we want to control TBA routing based on the resource. This
			 * could also be accomplished in most cases using the systemInstanceId, so the
			 * resource name is optional-you can send in null if you want and it will choose
			 * the default TBA routing for the consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource,
					appContext);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Adding the Transaction Header in case it is not provided by the client call
			 * to sTBA. Transaction Header contains Transaction Info for
			 * AlternateActivityReferenceId as businessProcessReferenceId.
			 * BusinessProcessReferenceId is fetched from the method URL.
			 */
			RequestHeader requestHeader = (RequestHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_REQUEST_HEADER);

			if (requestHeader != null && requestHeader.getTransactionHeader() == null){

				TransactionHeader transactionHeader = new TransactionHeader();

				ArrayList<TransactionInfo> transactionInfo = new ArrayList<TransactionInfo>();

				TransactionInfo element = new TransactionInfo();

				element.setAlternateActivityReferenceId(businessProcessReferenceId);

				transactionInfo.add(0, element);

				transactionHeader.setTransactionInfo(transactionInfo);

				requestHeader.setTransactionHeader(transactionHeader);

				serviceHelperContext.set(ServiceHelperContext.ALIGHT_REQUEST_HEADER, requestHeader);

			}

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.
			 */
			PersonTasksDataHelper helper = new PersonTasksDataHelper(serviceHelperContext);

			/*
			 * Attach the RequestParam values to the ServiceHelperContext instance as
			 * name/value pairs.
			 */
			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);

			RequestVariableValidationUtility.validateStringArray(taskGroupTypes, "taskGroupTypes", false);
			RequestVariableValidationUtility.validateTaskGroupTypes(taskGroupTypes);

			serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);
			serviceHelperContext.set("taskGroupTypes", taskGroupTypes);

			/*
			 * Execute the helper
			 */
			ResponseEntity<String> responseEntity;
			responseEntity = helper.executeService(String.class);

			/*
			 * Do any response-specific work like setting the ResponseHeader.
			 */
			HttpStatusCode httpStatus;
			httpStatus = responseEntity.getStatusCode();

			PersonTasksResponse response;
			response = (PersonTasksResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);

			ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);
			HttpHeaders httpHeaders = new HttpHeaders();

			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			return new ResponseEntity<Object>(response, httpHeaders, httpStatus);

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "IntegrationException in getPersonTasks()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "Exception in getPersonTasks()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@PutMapping(value = "/personEnrollments/{businessProcessReferenceId}/personTasks", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> putPersonTasks(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			@PathVariable String businessProcessReferenceId, @RequestBody UpdatePersonTasksRequest personTaskData) {

		String resource;
		resource = "personTasks";

		String expectedResponse;
		expectedResponse = "";

		try {

			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * the "resource" name, and appContext. We need the resource name in situations
			 * where we want to control TBA routing based on the resource. This could also
			 * be accomplished in most cases using the systemInstanceId, so the resource
			 * name is optional-you can send in null if you want and it will choose the
			 * default TBA routing for the consumer+client+systemInstanceId+testCfg. The PUT
			 * for personTasks uses the same copybook as the GET
			 */
			ServiceHelperContext serviceHelperContext;
			serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource, appContext);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Adding the Transaction Header in case it is not provided by the client call
			 * to sTBA. Transaction Header contains Transaction Info for
			 * AlternateActivityReferenceId as businessProcessReferenceId.
			 * BusinessProcessReferenceId is fetched from the method URL.
			 */
			RequestHeader requestHeader = (RequestHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_REQUEST_HEADER);

			if (requestHeader != null && requestHeader.getTransactionHeader() == null){

				TransactionHeader transactionHeader = new TransactionHeader();

				ArrayList<TransactionInfo> transactionInfo = new ArrayList<TransactionInfo>();

				TransactionInfo element = new TransactionInfo();

				element.setAlternateActivityReferenceId(businessProcessReferenceId);

				transactionInfo.add(0, element);

				transactionHeader.setTransactionInfo(transactionInfo);

				requestHeader.setTransactionHeader(transactionHeader);

				serviceHelperContext.set(ServiceHelperContext.ALIGHT_REQUEST_HEADER, requestHeader);

			}

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.
			 */
			PersonTasksPUTDataHelper helper = new PersonTasksPUTDataHelper(serviceHelperContext);

			/*
			 * Attach the RequestParam values to the ServiceHelperContext instance as
			 * name/value pairs.
			 */
			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);

			serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);
			serviceHelperContext.set("personTaskData", personTaskData);

			/*
			 * Execute the helper
			 */
			ResponseEntity<String> responseEntity;
			responseEntity = helper.executeService(String.class);

			/*
			 * Get the responseBody object out of the context and return it to the caller as
			 * a JSON string. Also, do any other response-specific items, like set the
			 * ResponseHeader.
			 */
			HttpStatusCode httpStatus;
			httpStatus = responseEntity.getStatusCode();

			Object response;
			response = (Object) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);

			ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			return new ResponseEntity<Object>(response, httpHeaders, httpStatus);

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "IntegrationException in putPersonTasks()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "Exception in putPersonTasks()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@PutMapping(value = "/personEnrollments/{businessProcessReferenceId}/reviseElections", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> putReviseElections(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			@PathVariable String businessProcessReferenceId,
			@RequestParam(required = false) String reviseElectionType,
			@RequestBody ReviseElectionsRequest reviseElectionsRequest) {

		String resource;
		resource = "reviseElections";

		ReviseElectionsResponse expectedResponse;
		expectedResponse = new ReviseElectionsResponse();

		try {
			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * the "resource" name, and appContext. We need the resource name in situations
			 * where we want to control TBA routing based on the resource. This could also
			 * be accomplished in most cases using the systemInstanceId, so the resource
			 * name is optional-you can send in null if you want and it will choose the
			 * default TBA routing for the consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext;
			serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource, appContext);

			/*
			 * Add the requestBody object to the context. This can be whatever object you
			 * want the helper to process (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.REQUEST_BODY, reviseElectionsRequest);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Add the reviseElectionTypes parameter to the contact. This is to determine
			 * which TBA service name to send to TBA and what type of revise to perform.
			 */
			if (RequestVariableValidationUtility.validateReviseElectionType(reviseElectionType)) {
				serviceHelperContext.set(ReviseElectionType.REVISE_ELECTION_TYPE, reviseElectionType);
			} else {
				String ieCause;
				ieCause = IntegrationExceptionConstants.INVALID_REQUEST_PARAMETER + ": " + "reviseElectionType" + ": "
						+ reviseElectionType;
				throw new IntegrationException(ieCause);
			}
			
			/*
			 * Add validation for isFinalElections in request body when reviseElectionType
			 * equals "multiplePlanSelect" for megaReviseElection
			 */
			if (StringUtils.isNotBlank(reviseElectionType) 
					&& reviseElectionType.equalsIgnoreCase(ReviseElectionType.MULTIPLE_PLAN_REVISE)) {
				/*
				 * if (!RequestVariableValidationUtility.validateReviseElectionsIsFinal(
				 * reviseElectionsRequest, serviceHelperContext)) { throw new
				 * IntegrationException("Invalid request data : elections.isFinalElection" ,
				 * IntegrationExceptionConstants.GENERAL_API_EXCEPTION ,
				 * "Invalid request data : elections.isFinalElection must be consistent (all true or all false) across all plans when reviseElectionType is multiplePlanSelect"
				 * , HttpStatus.BAD_REQUEST.value()); }
				 */
			}
			
			/*
			 * Attach the RequestParam values to the ServiceHelperContext instance as
			 * name/value pairs.
			 */
			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);

			serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.
			 */
			if (reviseElectionType != null
					&& reviseElectionType.equalsIgnoreCase(ReviseElectionType.DEPENDENT_REVISE)) {

				ReviseDependentElectionsDataHelper dependentHelper;
				dependentHelper = new ReviseDependentElectionsDataHelper(serviceHelperContext);

				/*
				 * For dependent revise we will be sending only HttpStatus of the service there
				 * will be no response in output for this service. The revise output will be
				 * send in next pendingElection Election page call when user continues to the
				 * flow.
				 */
				ResponseEntity<String> responseEntity;
				responseEntity = dependentHelper.executeService(String.class);

				HttpStatusCode httpStatus = responseEntity.getStatusCode();
				ResponseHeader responseHdr = (ResponseHeader) serviceHelperContext
						.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);

				/*
				 * In next lines of code, we will do following.. 1. Set the refresh indicator to
				 * true for pending election API if it is exist in cache. 2. Remove cache key
				 * from redis for availableOption API. This is needed to allow making TBA call
				 * and cache again latest from TBA.
				 */
				EnrollmentCacheUtil enrollmentCacheUtil = new EnrollmentCacheUtil();
				String serviceName = ENROLLMENT_SERVICE;
				String subjectId = "";
				String clientId = "";
				ArrayList<SystemTicket> systemTickets = new ArrayList<SystemTicket>();
				RequestHeader reqHdr = (RequestHeader) serviceHelperContext
						.get(ServiceHelperContext.ALIGHT_REQUEST_HEADER);
				if (reqHdr != null) {
					subjectId = reqHdr.getSubjectId();
					clientId = reqHdr.getClientId();
					systemTickets = reqHdr.getSystemTickets();
				}
				String cacheKey = EnrollmentCacheUtil.createCacheKey(serviceName, clientId, subjectId);
				EnrollmentCacheList ecl = (EnrollmentCacheList) distributedCacheUtil.getObjectFromCache(cacheKey);
				EnrollmentCache enrollmentCache = enrollmentCacheUtil.getEnrollmentCacheForSystemTicket(ecl,
						systemTickets);
				/*
				 * We will set the refresh indicator for pending election to true if
				 * pendingElectionResponse is already cached for dependent revise flow. This is
				 * to allow TBA call later and cached again with latest pendingElection data.
				 * Since available option does not have indicator, so we will simply delete
				 * cache key. For other cases when pending election is not cached yet, refresh
				 * will pull pull latest data anyway.
				 */
				if (enrollmentCache !=null) {

					/*
					 * saveCacheIndicator variable will help deciding if any TBA refresh is needed
					 * or not. If pendingElections and availableOptions are not yet cached, these
					 * two services will get response data from TBA service call, so nothing to
					 * save.
					 */
					boolean saveCacheIndicator = false;

					if (enrollmentCache.getPendingElectionsResponse() != null){
						enrollmentCache.setRefreshPendingElections(true);
						saveCacheIndicator = true;
					}

					if (enrollmentCache.getAvailableOptionsResponseList() != null) {
						enrollmentCache.setAvailableOptionsResponseList(null);
						saveCacheIndicator = true;
					}

					// Save off the enrollment cache list with new values set
					// above.
					if (saveCacheIndicator) {
						distributedCacheUtil.saveObjectInCache(cacheKey, ecl);
					}
				}

				HttpHeaders responseHeaders = new HttpHeaders();
				responseHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHdr.toJson());

				return new ResponseEntity<ReviseElectionsResponse>(expectedResponse, responseHeaders, httpStatus);
			}

			ReviseElectionsDataHelper helper;
			helper = new ReviseElectionsDataHelper(serviceHelperContext);

			/*
			 * Execute the helper
			 */
			ResponseEntity<String> responseEntity;
			responseEntity = helper.executeService(String.class);

			/*
			 * Do any response-specific items, like set the ResponseHeader.
			 */
			HttpStatusCode httpStatus;
			httpStatus = responseEntity.getStatusCode();

			ReviseElectionsResponse reviseElectionsResponse;
			reviseElectionsResponse = (ReviseElectionsResponse) serviceHelperContext
					.get(ServiceHelperContext.RESPONSE_BODY);

			// Determine if this is a simulation activity or not.
			// Note this should be encapsulated, but there is an issue with the autowire of the cache and trying to call a static method.
			// Should figure that out and refactor that at some point. 
			//
			// isEncapsulateSimulationBusinessProcess = TBAConnectorServiceController
			//		.checkIfEnrollmentCachedAndIsSimulation(httpRequest, httpResponse, serviceHelperContext);
			// checkIfEnrollmentCachedAndIsSimulation();
			//
			// For now just copy and pasting this logic.

			boolean isSimulationBusinessProcess  = false;
			String businessProcessSummary ="";					

			/* In order to figure out the isSimulationBusinessProcess we need to look at the enrollment that
			 * is in cache and look at the businessProcessSummary. When it is "simulationEnrollment" then we
			 * know that is for a simulation event.
			 * 
			 * Note, eD is short for enrollmentData
			 */
			RequestHeader eDReqHdr = (RequestHeader) serviceHelperContext.get(ServiceHelperContext.ALIGHT_REQUEST_HEADER);
			
			// String serviceName = ENROLLMENT_SERVICE;
			
			EnrollmentCache eDEnrollmentCache = null;
			
			EnrollmentDataHelper eDHelper;
			String serviceName = ENROLLMENT_SERVICE;
			
			try {
				eDHelper = new EnrollmentDataHelper(serviceHelperContext);

				eDHelper.checkSystemTicket(eDReqHdr);
				
				if (eDReqHdr != null && serviceHelperContext.get(CacheConstants.SYSTEMTICKET) != null){

					String subjectId = eDReqHdr.getSubjectId();
					String clientId = eDReqHdr.getClientId();
					String cacheKey = EnrollmentCacheUtil.createCacheKey(serviceName, clientId, subjectId);
					
					EnrollmentCacheList edecl = (EnrollmentCacheList) distributedCacheUtil.getObjectFromCache(cacheKey);					
					eDEnrollmentCache = enrollmentCacheUtil.getEnrollmentCacheForSystemTicket(edecl, eDReqHdr.getSystemTickets());


					if (eDEnrollmentCache != null && eDEnrollmentCache.getEnrollmentsResponse() != null) {

						EnrollmentsResponse enrollments = eDEnrollmentCache.getEnrollmentsResponse();
						
						// loop thru the Enrollments array, looking for a match on businessProcessReferenceId 
						// AKA Act-Ref-Nmbr-Id
						// If we find a match get the businessProcessSummary
						for (int iEnrollmentsAr = 0; iEnrollmentsAr < enrollments.getEnrollments()
								.size(); iEnrollmentsAr++) {

							if (enrollments.getEnrollments().get(iEnrollmentsAr) != null) {
								
								if (enrollments.getEnrollments().get(iEnrollmentsAr).getPendingPersonBusinessProcess() != null) {
								
									if (enrollments.getEnrollments().get(iEnrollmentsAr).getPendingPersonBusinessProcess().getBusinessProcessReferenceId() != null) {
									
										if (enrollments.getEnrollments().get(iEnrollmentsAr).getPendingPersonBusinessProcess()
												.getBusinessProcessReferenceId().equals(businessProcessReferenceId)) {											
										
											businessProcessSummary = enrollments.getEnrollments().get(iEnrollmentsAr)
													.getBusinessProcess().getBusinessProcessSummary();
											
											if (businessProcessSummary != null
													&& businessProcessSummary.equals("simulationEnrollment")) {
												// break For loop
												iEnrollmentsAr = enrollments.getEnrollments().size();
											}
										}

									}										
								}	
							
							}
							
						}	

						if (businessProcessSummary != null && businessProcessSummary.equals("simulationEnrollment")) {
							isSimulationBusinessProcess = true;
						}

					}
				}

			} catch (IntegrationException e) {

				ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
						"IntegrationException in putReviseElections() if simulations check", httpRequest.getPathInfo(),
						e, ErrorLogEvent.ERROR_SEVERITY);

				if (e.getHttpStatus() == 0) {

					return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

				} else {

					return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.valueOf(e.getHttpStatus()));

				}

			}

			List<PendingElectionsResponseDependents> dependentPlanListTruth;
			PendingElectionsResponseDependents pendingElectionsResponseDependentsTruth;
			
			//System.out.println("dependentPlanListTruth " + dependentPlanListTruth.toString());

			PendingElectionsResponse pendingElectionsResponse;
			pendingElectionsResponse = reviseElectionsResponse.getPendingElections();

			AvailableOptionsResponse availableOptionsResponse;
			availableOptionsResponse = reviseElectionsResponse.getAvailableOptions();

			ResponseHeader responseHdr = (ResponseHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);

			/*
			 * TODO: Do we need to check httpStatus = HttpStatus.OK before doing any other
			 * processing at this point?
			 */
			if (pendingElectionsResponse != null) {
				
				dependentPlanListTruth = reviseElectionsResponse.getPendingElections().getDependents();

				/*
				 * Update the cache with the return data from TBA. Fetch the cache with the key
				 * to see if one exists today. If one does exist, and system ticket matches,
				 * update the existing cache. If one does not exist or it does exist but no
				 * matching system ticket, create a new entry.
				 */
				EnrollmentCacheUtil enrollmentCacheUtil = new EnrollmentCacheUtil();
				String subjectId = "";
				String clientId = "";
				ArrayList<SystemTicket> systemTickets = new ArrayList<SystemTicket>();
				RequestHeader reqHdr = (RequestHeader) serviceHelperContext
						.get(ServiceHelperContext.ALIGHT_REQUEST_HEADER);
				if (reqHdr != null) {
					subjectId = reqHdr.getSubjectId();
					clientId = reqHdr.getClientId();
					systemTickets = reqHdr.getSystemTickets();
				}
				String cacheKey = EnrollmentCacheUtil.createCacheKey(serviceName, clientId, subjectId);
				EnrollmentCacheList ecl = (EnrollmentCacheList) distributedCacheUtil.getObjectFromCache(cacheKey);
				EnrollmentCache enrollmentCache = enrollmentCacheUtil.getEnrollmentCacheForSystemTicket(ecl,
						systemTickets);
				// if the cache is present and pendingElections exists, update
				// existing data
				// if cache is present but no pendingElections, use the response
				// as the new pendingElections cached object
				// otherwise if cache is not present then create it and use the
				// response as the pendingElections cached object.
				if (enrollmentCache != null && enrollmentCache.getPendingElectionsResponse() != null) {
					// call function to replace plans that came from TBA
					// response
					PendingElectionsResponse pendingElectionsFinal = enrollmentCacheUtil
							.updatePendingElectionsCacheWithTBAResponse(pendingElectionsResponse,
									enrollmentCache.getPendingElectionsResponse(), serviceHelperContext);
					// replace existing object with new object
					enrollmentCache.setPendingElectionsResponse(pendingElectionsFinal);
					pendingElectionsResponse = pendingElectionsFinal;
					reviseElectionsResponse.setPendingElections(pendingElectionsResponse);
				} else {
					if  (enrollmentCache != null && enrollmentCache.getPendingElectionsResponse() == null){
						enrollmentCache.setPendingElectionsResponse(pendingElectionsResponse);
					} else {
						// create new cache
						enrollmentCache = enrollmentCacheUtil.initializeEnrollmentCache(reqHdr.getSystemTickets(), null,
								null, pendingElectionsResponse, null);
						ArrayList<EnrollmentCache> enrollmentCacheList = new ArrayList<EnrollmentCache>();
						enrollmentCacheList.add(enrollmentCache);
						if (ecl == null) {
							ecl = new EnrollmentCacheList();
						}
						ecl.setEnrollmentCache(enrollmentCacheList);
					}
				}
				
				if (isSimulationBusinessProcess) {

					List<PendingElectionsResponseDependents> dependentPlanListFinal;
					PendingElectionsResponseDependents pendingElectionsResponseDependentsFinal;

					dependentPlanListFinal = reviseElectionsResponse.getPendingElections().getDependents();

					//System.out.println("Before dependentPlanListFinal " + dependentPlanListFinal.toString());
					
					// Clean up the dependentPlanListFinal
					Integer iCurrentRelationshipSequence = -1;
					for (int idependentPlanListFinal = 0; idependentPlanListFinal < dependentPlanListFinal
							.size(); idependentPlanListFinal++) {

						pendingElectionsResponseDependentsFinal = dependentPlanListFinal.get(idependentPlanListFinal);

						if (iCurrentRelationshipSequence.intValue() != pendingElectionsResponseDependentsFinal
								.getRelationshipSequence().intValue()) {

							for (int iDependentPlanListTruth = 0; iDependentPlanListTruth < dependentPlanListTruth
									.size(); iDependentPlanListTruth++) {

								pendingElectionsResponseDependentsTruth = dependentPlanListTruth
										.get(iDependentPlanListTruth);

								if (pendingElectionsResponseDependentsFinal.getRelationshipSequence()
										.intValue() == pendingElectionsResponseDependentsTruth.getRelationshipSequence()
												.intValue()) {
									pendingElectionsResponseDependentsFinal.setDependentPersonInternalId(
											pendingElectionsResponseDependentsTruth.getDependentPersonInternalId());
								}

							}

							iCurrentRelationshipSequence = pendingElectionsResponseDependentsFinal
									.getRelationshipSequence();
						} else {
							dependentPlanListFinal.remove(idependentPlanListFinal);
							idependentPlanListFinal--;
						}
						
					} // For loop dependentPlanListFinal

					//System.out.println("AFTER dependentPlanListFinal " + dependentPlanListFinal.toString());

					// Fix relationshipArray
					//
					// including little cheat sheet here:
					//
					// (things you see in Postman) 				(variable names in following code)
					// coverages[         	        	  		pendingElectionsResponseCoveragesList -	 pendingElectionsResponseCoverages
					// 		relationshipCoverages{ 				pendingElectionsResponseRelationshipCoverages
					// 			eligibleRelationships[ 			eligibleRelationshipsList - eligibleRelationships
					// 				dependentPersonInternalId
					// 				relationshipSequence
					// 			]
					// 		} 	 
					// ]
					List<PendingElectionsResponseCoverages> pendingElectionsResponseCoveragesList;
					PendingElectionsResponseCoverages pendingElectionsResponseCoverages;
					PendingElectionsResponseRelationshipCoverages pendingElectionsResponseRelationshipCoverages;

					List<EligibleRelationshipsInner> eligibleRelationshipsList;
					EligibleRelationshipsInner eligibleRelationships;

					pendingElectionsResponseCoveragesList = enrollmentCache.getPendingElectionsResponse()
							.getCoverages();

					Integer eligibleRelationshipsrelationshipSequence;

					for (int iPendingElectionsResponseCoverages = 0; iPendingElectionsResponseCoverages < pendingElectionsResponseCoveragesList
							.size(); iPendingElectionsResponseCoverages++) {

						pendingElectionsResponseCoverages = pendingElectionsResponseCoveragesList
								.get(iPendingElectionsResponseCoverages);

						pendingElectionsResponseRelationshipCoverages = pendingElectionsResponseCoverages
								.getRelationshipCoverages();

						if (pendingElectionsResponseRelationshipCoverages != null) {

							eligibleRelationshipsList = pendingElectionsResponseRelationshipCoverages
									.getEligibleRelationships();
							
							if (eligibleRelationshipsList != null) {

								for (int iEligibleRelationships = 0; iEligibleRelationships < eligibleRelationshipsList
										.size(); iEligibleRelationships++) {

									eligibleRelationships = eligibleRelationshipsList.get(iEligibleRelationships);

									eligibleRelationshipsrelationshipSequence = eligibleRelationships
											.getRelationshipSequence();

									// loop thru truth array
									// if found, freshen up the dependentPersonInternalId
									for (int iDependentPlanListTruth = 0; iDependentPlanListTruth < dependentPlanListTruth
											.size(); iDependentPlanListTruth++) {

										pendingElectionsResponseDependentsTruth = dependentPlanListTruth
												.get(iDependentPlanListTruth);

										if (eligibleRelationshipsrelationshipSequence.equals(
												pendingElectionsResponseDependentsTruth.getRelationshipSequence())) {

											eligibleRelationships.setDependentPersonInternalId(
													pendingElectionsResponseDependentsTruth
															.getDependentPersonInternalId());

											iDependentPlanListTruth = dependentPlanListTruth.size();
										}

									}
								}
							} // if (eligibleRelationshipsList != null)
						} // if (pendingElectionsResponseRelationshipCoverages != null)
					} // loop thru pendingElectionsResponseCoveragesList
				} // (isSimulationBusinessProcess) {

				distributedCacheUtil.saveObjectInCache(cacheKey, ecl);
				// Only output input plans if plan array is populated
				if (reviseElectionsRequest != null) {

					List<PendingElectionsResponseCoverages> tempCoverages = pendingElectionsResponse.getCoverages();
					List<PendingElectionsResponseCoverages> finalCoverages = new ArrayList<PendingElectionsResponseCoverages>();

					List<AvailableOptionsResponseChoices> tempChoices = reviseElectionsResponse.getAvailableOptions()
							.getChoices();
					List<AvailableOptionsResponseChoices> finalChoices = new ArrayList<AvailableOptionsResponseChoices>();

					List<ReviseElectionsRequestElections> electionList;
					electionList = reviseElectionsRequest.getElections();
					if (electionList != null) {

						Boolean foundPlan = false;
						Boolean savingsAccountFound = false;
						String[] planIdSuffix;

						// check to see if there was a HSA/HRA plan in the
						// revise
						for (int j = 0; j < electionList.size(); j++) {
							planIdSuffix = electionList.get(j).getPlanId().split("_");
							if (planIdSuffix.length > 1
									&& (planIdSuffix[1].equals("HSA") || planIdSuffix[1].equals("HRA"))) {
								savingsAccountFound = true;
							}
						}

						// begin plan filtering logic.
						for (int j = 0; j < electionList.size(); j++) {
							// pendingElections plan filtering
							if (tempCoverages != null) {
								// No foundPlan boolean here b/c HSA/HRA plans
								// need to be returned along with medical.
								for (int i = 0; i < tempCoverages.size(); i++) {
									if (tempCoverages.get(i).getPlanId().equals(electionList.get(j).getPlanId())) {
										finalCoverages.add(tempCoverages.get(i));
									}
									// Add HSA/HRA plan
									else if (savingsAccountFound == false && ((electionList.get(j).getPlanId() + "_HSA")
											.equals(tempCoverages.get(i).getPlanId())
											|| (electionList.get(j).getPlanId() + "_HRA")
													.equals(tempCoverages.get(i).getPlanId()))) {
										finalCoverages.add(tempCoverages.get(i));
									}
								}
							}
							// availableOptions plan filtering
							if (tempChoices != null) {
								foundPlan = false;
								for (int i = 0; i < tempChoices.size() && !foundPlan; i++) {
									if (tempChoices.get(i).getPlanId().equals(electionList.get(j).getPlanId())) {
										finalChoices.add(tempChoices.get(i));
										foundPlan = true;
									}
								}
							}
						}
					}
					pendingElectionsResponse.setCoverages(finalCoverages);
					reviseElectionsResponse.getAvailableOptions().setChoices(finalChoices);
				}
			}

			if (availableOptionsResponse != null && isToggleAvailableOptionsCache()) {

				/*
				 * Update the cache with the return data from TBA. Fetch the cache with the key
				 * to see if one exists today. If one does exist, and system ticket matches,
				 * update the existing cache. If one does not exist or it does exist but no
				 * matching system ticket, create a new entry.
				 */
				EnrollmentCacheUtil enrollmentCacheUtil = new EnrollmentCacheUtil();
				
				String subjectId = "";
				String clientId = "";
				ArrayList<SystemTicket> systemTickets = new ArrayList<SystemTicket>();
				RequestHeader reqHdr = (RequestHeader) serviceHelperContext
						.get(ServiceHelperContext.ALIGHT_REQUEST_HEADER);
				if (reqHdr != null) {
					subjectId = reqHdr.getSubjectId();
					clientId = reqHdr.getClientId();
					systemTickets = reqHdr.getSystemTickets();
				}
				String cacheKey = EnrollmentCacheUtil.createCacheKey(serviceName, clientId, subjectId);
				EnrollmentCacheList ecl = (EnrollmentCacheList) distributedCacheUtil.getObjectFromCache(cacheKey);
				EnrollmentCache enrollmentCache = enrollmentCacheUtil.getEnrollmentCacheForSystemTicket(ecl,
						systemTickets);
				// if the cache is present and availableOptions exists, update
				// existing cache with TBA return
				if (enrollmentCache != null && enrollmentCache.getAvailableOptionsResponseList() != null){
					if (StringUtils.isNotBlank(reviseElectionType) && reviseElectionType.equalsIgnoreCase(ReviseElectionType.MULTIPLE_PLAN_REVISE)) {
						enrollmentCache.setAvailableOptionsResponseList(null);
					} else {
						enrollmentCacheUtil.updateAvailableOptionsCache(enrollmentCache, availableOptionsResponse,
								reviseElectionsRequest);
					}
				}
				distributedCacheUtil.saveObjectInCache(cacheKey, ecl);
			}

			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHdr.toJson());

			return new ResponseEntity<ReviseElectionsResponse>(reviseElectionsResponse, responseHeaders, httpStatus);

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "IntegrationException in putReviseElections()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			if (e.getHttpStatus() == 0) {

				return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

			} else {

				return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.valueOf(e.getHttpStatus()));

			}

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "Exception in putReviseElections()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@PutMapping(value = "/personEnrollments/{businessProcessReferenceId}/commit", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> putCommit(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			@PathVariable String businessProcessReferenceId,
			@RequestParam(defaultValue = "false", required = false) String cacheResponse) {

		String resource;
		resource = "commit";

		PendingElectionsResponse expectedResponse;
		expectedResponse = new PendingElectionsResponse();

		try {
			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * the "resource" name, and appContext. We need the resource name in situations
			 * where we want to control TBA routing based on the resource. This could also
			 * be accomplished in most cases using the systemInstanceId, so the resource
			 * name is optional-you can send in null if you want and it will choose the
			 * default TBA routing for the consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext;
			serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource, appContext);

			if (!cacheResponse.equalsIgnoreCase("true")) {
				/*
				 * Add the responseBody object to the context. This can be whatever object you
				 * want the helper to populate (ie-POJO, etc).
				 */
				serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

				/*
				 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
				 * will handle all provider-specific stuff, in this case managing the TBA
				 * connector code. In the case of TBA helpers, this class is responsible for
				 * managing 1:many subservice calls.
				 */
				CommitDataHelper helper;
				helper = new CommitDataHelper(serviceHelperContext);

				/*
				 * Attach the RequestParam values to the ServiceHelperContext instance as
				 * name/value pairs.
				 */
				RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
						"businessProcessReferenceId", true);

				serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);

				/*
				 * Execute the helper
				 */
				ResponseEntity<String> responseEntity;
				responseEntity = helper.executeService(String.class);

				/*
				 * Do any response-specific work like setting the ResponseHeader.
				 */
				HttpStatusCode httpStatus;
				httpStatus = responseEntity.getStatusCode();

				PendingElectionsResponse response;
				response = (PendingElectionsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);

				/*
				 * TODO: Do we need to check httpStatus = HttpStatus.OK before doing any other
				 * processing at this point?
				 */
				/*
				 * Update the cache with the return data from TBA. Fetch the cache with the key
				 * to see if one exists today. If one does exist, and system ticket matches,
				 * update the existing cache. If one does not exist or it does exist but no
				 * matching system ticket, create a new entry.
				 */
				if (response.getCoverages() != null || response.getDependents() != null
						|| response.getFootnotes() != null || response.getCustomText() != null
						|| response.getTotalPriceComponents() != null)  {
					EnrollmentCacheUtil enrollmentCacheUtil = new EnrollmentCacheUtil();
					String serviceName = ENROLLMENT_SERVICE;
					String subjectId = "";
					String clientId = "";
					ArrayList<SystemTicket> systemTickets = new ArrayList<SystemTicket>();
					RequestHeader reqHdr = (RequestHeader) serviceHelperContext
							.get(ServiceHelperContext.ALIGHT_REQUEST_HEADER);

					if (reqHdr != null) {
						subjectId = reqHdr.getSubjectId();
						clientId = reqHdr.getClientId();
						systemTickets = reqHdr.getSystemTickets();
					}

					String cacheKey = EnrollmentCacheUtil.createCacheKey(serviceName, clientId, subjectId);
					EnrollmentCacheList ecl = (EnrollmentCacheList) distributedCacheUtil.getObjectFromCache(cacheKey);
					EnrollmentCache enrollmentCache = enrollmentCacheUtil.getEnrollmentCacheForSystemTicket(ecl,
							systemTickets);

					// if the cache is present replace with TBA response.
					// Validate
					// replaces the whole cache each time.
					// otherwise if cache is not present then create it and use
					// the
					// response as the pendingElections cached object.
					if (enrollmentCache!= null) {
						// call function to replace plans that came from TBA
						// response
						enrollmentCache.setPendingElectionsResponse(response);
					} else {
						// create new cache
						enrollmentCache = enrollmentCacheUtil.initializeEnrollmentCache(reqHdr.getSystemTickets(), null,
								null, response, null);
						ArrayList<EnrollmentCache> enrollmentCacheList = new ArrayList<EnrollmentCache>();
						enrollmentCacheList.add(enrollmentCache);
						if (ecl == null) {
							ecl = new EnrollmentCacheList();
						}
						ecl.setEnrollmentCache(enrollmentCacheList);
					}
					distributedCacheUtil.saveObjectInCache(cacheKey, ecl);
				}
				ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
						.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);

				HttpHeaders httpHeaders = new HttpHeaders();

				if (responseHeader != null) {
					httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());
				}

				return new ResponseEntity<PendingElectionsResponse>(response, httpHeaders, httpStatus);

			} else {
				EnrollmentCacheUtil enrollmentCacheUtil = new EnrollmentCacheUtil();
				String serviceName = ENROLLMENT_SERVICE;
				String subjectId = "";
				String clientId = "";
				ArrayList<SystemTicket> systemTickets = new ArrayList<SystemTicket>();
				RequestHeader reqHdr = (RequestHeader) serviceHelperContext
						.get(ServiceHelperContext.ALIGHT_REQUEST_HEADER);

				if ((reqHdr!= null)) {
					subjectId = reqHdr.getSubjectId();
					clientId = reqHdr.getClientId();
					systemTickets = reqHdr.getSystemTickets();
				}

				String cacheKey = EnrollmentCacheUtil.createCacheKey(serviceName, clientId, subjectId);
				EnrollmentCacheList ecl = (EnrollmentCacheList) distributedCacheUtil.getObjectFromCache(cacheKey);
				EnrollmentCache enrollmentCache = enrollmentCacheUtil.getEnrollmentCacheForSystemTicket(ecl,
						systemTickets);
				ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
						.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);

				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());
				if (enrollmentCache != null && enrollmentCache.getPendingElectionsResponse() != null) {
					PendingElectionsResponse response = enrollmentCache.getPendingElectionsResponse();
					return new ResponseEntity<PendingElectionsResponse>(response, httpHeaders, HttpStatus.OK);
				} else {
					PendingElectionsResponse response = new PendingElectionsResponse();
					return new ResponseEntity<PendingElectionsResponse>(response, httpHeaders, HttpStatus.OK);
				}

			}

		} catch (RuntimeException e) {

			throw e;

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "IntegrationException in putCommit()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "Exception in putCommit()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	private ResponseEntity<?> personUnlock(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

		String resource;
		resource = "personUnlock";

		Object expectedResponse;
		expectedResponse = new Object();

		try {
			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * the "resource" name, and appContext. We need the resource name in situations
			 * where we want to control TBA routing based on the resource. This could also
			 * be accomplished in most cases using the systemInstanceId, so the resource
			 * name is optional-you can send in null if you want and it will choose the
			 * default TBA routing for the consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext;
			serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource, appContext);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.
			 */
			PersonUnlockDataHelper helper;
			helper = new PersonUnlockDataHelper(serviceHelperContext);

			/*
			 * Attach the RequestParam values to the ServiceHelperContext instance as
			 * name/value pairs.
			 * 
			 * There are none.
			 */

			/*
			 * Execute the helper
			 */
			ResponseEntity<String> responseEntity;
			responseEntity = helper.executeService(String.class);

			/*
			 * Do any response-specific work like setting the ResponseHeader.
			 */
			HttpStatusCode httpStatus;
			httpStatus = responseEntity.getStatusCode();

			Object response;
			response = (Object) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);

			ResponseHeader responseHeader;
			responseHeader = (ResponseHeader) serviceHelperContext.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);

			HttpHeaders httpHeaders;
			httpHeaders = new HttpHeaders();

			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			return new ResponseEntity<Object>(response, httpHeaders, httpStatus);

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "IntegrationException in personUnlock()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "Exception in personUnlock()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@PutMapping(value = "/personEnrollments/{businessProcessReferenceId}/cancel", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> cancel(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			@PathVariable String businessProcessReferenceId) {

		/*
		 * The cancel taps into the common logic for person unlock. This service also
		 * clears the cache if an entry is found.
		 */
		String resource;
		resource = "cancel";

		String serviceName = ENROLLMENT_SERVICE;
		String subjectId = "";
		String clientId = "";

		Object expectedResponse;
		expectedResponse = new Object();

		CancelDataHelper helper = null;

		try {

			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * the "resource" name, and appContext. We need the resource name in situations
			 * where we want to control TBA routing based on the resource. This could also
			 * be accomplished in most cases using the systemInstanceId, so the resource
			 * name is optional-you can send in null if you want and it will choose the
			 * default TBA routing for the consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext;
			serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource, appContext);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.
			 */
			helper = new CancelDataHelper(serviceHelperContext);

			/*
			 * Attach the RequestParam values to the ServiceHelperContext instance as
			 * name/value pairs.
			 */
			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);

			serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);

			/*
			 * Execute the helper
			 */
			ResponseEntity<String> responseEntity;
			responseEntity = helper.executeService(String.class);

			HttpStatusCode httpStatus;
			httpStatus = responseEntity.getStatusCode();

			/*
			 * TODO: Do we need to check httpStatus = HttpStatus.OK before doing any other
			 * processing at this point?
			 */
			/*
			 * Delete the cache if one is found.
			 */
			EnrollmentCacheUtil enrollmentCacheUtil = new EnrollmentCacheUtil();

			ArrayList<SystemTicket> systemTickets = new ArrayList<SystemTicket>();
			RequestHeader requestHeader = (RequestHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_REQUEST_HEADER);
			if (requestHeader != null) {
				subjectId = requestHeader.getSubjectId();
				clientId = requestHeader.getClientId();
				systemTickets = requestHeader.getSystemTickets();
			}
			String cacheKey = EnrollmentCacheUtil.createCacheKey(serviceName, clientId, subjectId);
			EnrollmentCacheList ecl = (EnrollmentCacheList) distributedCacheUtil.getObjectFromCache(cacheKey);
			EnrollmentCache enrollmentCache = enrollmentCacheUtil.getEnrollmentCacheForSystemTicket(ecl, systemTickets);
			if (enrollmentCache != null) {
				distributedCacheUtil.deleteObjectFromCache(cacheKey);
			}

			/*
			 * Get the responseBody object out of the context and return it to the caller as
			 * a JSON string. Also, do any other response-specific items, like set the
			 * ResponseHeader.
			 */
			ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);
			HttpHeaders httpHeaders = new HttpHeaders();

			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			/*
			 * If the cancel processing was successful, call the personUnlock() method which
			 * will call sTBA to remove the row from the person lock table.
			 */
			if (httpStatus.equals(HttpStatus.OK)) {

				personUnlock(httpRequest, httpResponse);

			}

			return new ResponseEntity<String>(null, httpHeaders, httpStatus);

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "IntegrationException in cancel()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "Exception in cancel()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	/**
	 * API is to get data for HPCC Details page
	 * 
	 * @param httpRequest
	 * @param httpResponse
	 * @param businessProcessReferenceId
	 * @param planId
	 * @param chartId
	 * @param optionIds
	 * @param compositePlanDesignIds
	 * @param effectiveDate
	 * @return
	 */
	@GetMapping(value = "/personEnrollments/{businessProcessReferenceId}/plan/{planId}/healthPlanComparisonChart/{chartId}", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getHPCCData(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			@PathVariable String businessProcessReferenceId, @PathVariable String planId, @PathVariable String chartId,
			@RequestParam(required = false) String[] optionIds,
			@RequestParam(required = false) String[] compositePlanDesignIds,
			@RequestParam(required = false) String effectiveDate,
			@RequestParam(required = false) String superSectionSummary,
			@RequestParam(required = false) String currencyFormat) {

		String resource;
		resource = "chartStructure";

		HealthPlanComparisonResponse expectedResponse;
		expectedResponse = new HealthPlanComparisonResponse();

		try {

			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * the "resource" name, and appContext. We need the resource name in situations
			 * where we want to control TBA routing based on the resource. This could also
			 * be accomplished in most cases using the systemInstanceId, so the resource
			 * name is optional-you can send in null if you want and it will choose the
			 * default TBA routing for the consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext;
			serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource, appContext);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.
			 */

			// set all parameter to ServiceHelperContext
			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);

			RequestVariableValidationUtility.validateStringElement(planId, "planId", true);

			RequestVariableValidationUtility.validateIntegerAsStringElement(chartId, "chartId", true);

			RequestVariableValidationUtility.validateIntegerAsStringArray(optionIds, "optionIds", false);

			RequestVariableValidationUtility.validateIntegerAsStringArray(compositePlanDesignIds,
					"compositePlanDesignIds", false);

			if ((ObjectUtils.isEmpty(compositePlanDesignIds)) && (!ObjectUtils.isEmpty(optionIds))) {
				String ieCause = IntegrationExceptionConstants.INVALID_REQUEST_PARAMETER
						+ ": optionIds and compositePlanDesignIds are required ";
				IntegrationException ie = new IntegrationException(ieCause);
				throw ie;
			}

			if ((!ObjectUtils.isEmpty(compositePlanDesignIds)) && (ObjectUtils.isEmpty(optionIds))) {
				String ieCause = IntegrationExceptionConstants.INVALID_REQUEST_PARAMETER
						+ ": optionIds and compositePlanDesignIds are required ";
				IntegrationException ie = new IntegrationException(ieCause);
				throw ie;
			}

			if (Objects.nonNull(compositePlanDesignIds) && Objects.nonNull(optionIds)
					&& (compositePlanDesignIds.length == 0 || optionIds.length == 0)) {
				String ieCause = IntegrationExceptionConstants.INVALID_REQUEST_PARAMETER
						+ ": Value of optionIds and compositePlanDesignIds are required ";
				IntegrationException ie = new IntegrationException(ieCause);
				throw ie;

			}
			if (Objects.nonNull(compositePlanDesignIds) && Objects.nonNull(optionIds)
					&& (optionIds.length != compositePlanDesignIds.length)) {

				String ieCause;
				ieCause = IntegrationExceptionConstants.INVALID_REQUEST_PARAMETER
						+ ": optionIds item count and compositePlanDesignIds item count differ.  " + "optionIds: "
						+ Arrays.toString(optionIds) + ".  compositePlanDesignIds: "
						+ Arrays.toString(compositePlanDesignIds) + ".";
				IntegrationException ie;
				ie = new IntegrationException(ieCause);

				throw ie;

			}

			if (Objects.isNull(effectiveDate) || ObjectUtils.isEmpty(effectiveDate)) {
				effectiveDate = LocalDate.now().toString();
			}
			serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);
			serviceHelperContext.set("planId", planId);
			serviceHelperContext.set("chartId", chartId);
			serviceHelperContext.set("optionIds", optionIds);
			serviceHelperContext.set("compositePlanDesignIds", compositePlanDesignIds);

			serviceHelperContext.set("effectiveDate", effectiveDate);

			if (!ObjectUtils.isEmpty(superSectionSummary)) {
				if (RequestVariableValidationUtility.validateSuperSectionSummary(superSectionSummary)) {
					serviceHelperContext.set("superSectionSummary", superSectionSummary);
				} else {
					String ieCause;
					ieCause = IntegrationExceptionConstants.INVALID_REQUEST_PARAMETER + ": " + "superSectionSummary"
							+ ": " + superSectionSummary;
					throw new IntegrationException(ieCause);
				}
			} else {
				serviceHelperContext.set("superSectionSummary", ValidValues.HpccSuperSectionSummary.BASIC);
			}

			if (currencyFormat != null
					&& (!currencyFormat.isEmpty() && (currencyFormat.equals(CurrencyFormatConstants.SYMBOL)
							|| currencyFormat.equals(CurrencyFormatConstants.CODE)
							|| currencyFormat.equals(CurrencyFormatConstants.NONE)))) {
				if (currencyFormat.equals(CurrencyFormatConstants.CODE)) {
					serviceHelperContext.set("currencyFormat", CurrencyFormatConstants.CODE);
				} else if (currencyFormat.equals(CurrencyFormatConstants.NONE)) {
					serviceHelperContext.set("currencyFormat", CurrencyFormatConstants.NONE);
				} else {
					serviceHelperContext.set("currencyFormat", CurrencyFormatConstants.SYMBOL);
				}
			} else {
				serviceHelperContext.set("currencyFormat", CurrencyFormatConstants.SYMBOL);
			}

			HPCCDataHelper helper = new HPCCDataHelper(serviceHelperContext);

			/*
			 * Execute the helper
			 */
			ResponseEntity<String> responseEntity;
			responseEntity = helper.executeService(String.class);

			/*
			 * Do any other response-specific items, like set the ResponseHeader.
			 */
			HttpStatusCode httpStatus;
			httpStatus = responseEntity.getStatusCode();

			HealthPlanComparisonResponse response;
			response = (HealthPlanComparisonResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);

			ResponseHeader responseHeader;
			responseHeader = (ResponseHeader) serviceHelperContext.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);

			HttpHeaders httpHeaders;
			httpHeaders = new HttpHeaders();
			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			return new ResponseEntity<HealthPlanComparisonResponse>(response, httpHeaders, httpStatus);

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "IntegrationException in getHPCCData()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "Exception in getHPCCData()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	/**
	 * API is to get data for HPCC Initial page
	 * 
	 * @param httpRequest
	 * @param httpResponse
	 * @param businessProcessReferenceId
	 * @param planId
	 * @param effectiveDate
	 * @return
	 */
	@GetMapping(value = "/personEnrollments/{businessProcessReferenceId}/plan/{planId}/healthPlanComparisonAvailableOptions", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getHPCCSummaryData(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			@PathVariable String businessProcessReferenceId, @PathVariable String planId,
			@RequestParam(required = false, defaultValue = "1800-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") Date effectiveDate) {

		String resource;
		resource = "healthPlanComparisionSummary";

		HealthPlanComparisonAvailableOptionsResponse expectedResponse;
		expectedResponse = new HealthPlanComparisonAvailableOptionsResponse();

		try {

			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * the "resource" name, and appContext. We need the resource name in situations
			 * where we want to control TBA routing based on the resource. This could also
			 * be accomplished in most cases using the systemInstanceId, so the resource
			 * name is optional-you can send in null if you want and it will choose the
			 * default TBA routing for the consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext;
			serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource, appContext);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.
			 */
			HPCCSummaryDataHelper helper = new HPCCSummaryDataHelper(serviceHelperContext);

			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);

			RequestVariableValidationUtility.validateStringElement(planId, "planId", true);

			serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);
			serviceHelperContext.set("planId", planId);
			serviceHelperContext.set("effectiveDate", effectiveDate);

			/*
			 * Execute the helper
			 */
			ResponseEntity<String> responseEntity;
			responseEntity = helper.executeService(String.class);

			/*
			 * Do any other response-specific items, like set the ResponseHeader.
			 */
			HttpStatusCode httpStatus;
			httpStatus = responseEntity.getStatusCode();

			HealthPlanComparisonAvailableOptionsResponse response;
			response = (HealthPlanComparisonAvailableOptionsResponse) serviceHelperContext
					.get(ServiceHelperContext.RESPONSE_BODY);

			ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			return new ResponseEntity<HealthPlanComparisonAvailableOptionsResponse>(response, httpHeaders, httpStatus);

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "IntegrationException in getHPCCSummaryData()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "Exception in getHPCCSummaryData()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@PutMapping(value = "/personEnrollments/{businessProcessReferenceId}/reviseCancel", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> putReviseCancel(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			@PathVariable String businessProcessReferenceId, @RequestBody ReviseCancelRequest reviseCancelRequest) {

		String resource;
		resource = "reviseCancel";

		PendingElectionsResponse expectedResponse;
		expectedResponse = new PendingElectionsResponse();

		try {

			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * the "resource" name, and appContext. We need the resource name in situations
			 * where we want to control TBA routing based on the resource. This could also
			 * be accomplished in most cases using the systemInstanceId, so the resource
			 * name is optional-you can send in null if you want and it will choose the
			 * default TBA routing for the consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext;
			serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource, appContext);

			/*
			 * Add the requestBody object to the context. This can be whatever object you
			 * want the helper to process (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.REQUEST_BODY, reviseCancelRequest);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.
			 */
			ReviseCancelDataHelper helper;
			helper = new ReviseCancelDataHelper(serviceHelperContext);

			/*
			 * Attach the RequestParam values to the ServiceHelperContext instance as
			 * name/value pairs.
			 */
			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);

			serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);

			serviceHelperContext.set("reviseCancelRequest", reviseCancelRequest);

			/*
			 * Execute the helper
			 */
			ResponseEntity<String> responseEntity;
			responseEntity = helper.executeService(String.class);

			/*
			 * Do any response-specific work like setting the ResponseHeader.
			 */
			HttpStatusCode httpStatus;
			httpStatus = responseEntity.getStatusCode();

			PendingElectionsResponse pendingElectionsResponse;
			pendingElectionsResponse = (PendingElectionsResponse) serviceHelperContext
					.get(ServiceHelperContext.RESPONSE_BODY);

			/*
			 * TODO: Do we need to check httpStatus = HttpStatus.OK before doing any other
			 * processing at this point?
			 */
			if (pendingElectionsResponse != null && httpStatus == HttpStatus.OK) {

				/*
				 * Update the cache with the return data from TBA. Fetch the cache with the key
				 * to see if one exists today. If one does exist, and system ticket matches,
				 * update the existing cache. If one does not exist or it does exist but no
				 * matching system ticket, create a new entry.
				 */
				EnrollmentCacheUtil enrollmentCacheUtil = new EnrollmentCacheUtil();
				String serviceName = ENROLLMENT_SERVICE;
				String subjectId = "";
				String clientId = "";
				ArrayList<SystemTicket> systemTickets = new ArrayList<SystemTicket>();
				RequestHeader reqHdr = (RequestHeader) serviceHelperContext
						.get(ServiceHelperContext.ALIGHT_REQUEST_HEADER);
				if (reqHdr != null) {
					subjectId = reqHdr.getSubjectId();
					clientId = reqHdr.getClientId();
					systemTickets = reqHdr.getSystemTickets();
				}
				String cacheKey;
				cacheKey = EnrollmentCacheUtil.createCacheKey(serviceName, clientId, subjectId);
				EnrollmentCacheList ecl = (EnrollmentCacheList) distributedCacheUtil.getObjectFromCache(cacheKey);
				EnrollmentCache enrollmentCache = enrollmentCacheUtil.getEnrollmentCacheForSystemTicket(ecl,
						systemTickets);
				// if the cache is present and pendingElections exists, update
				// existing data
				// if cache is present but no pendingElections, use the response
				// as the new pendingElections cached object
				// otherwise if cache is not present then create it and use the
				// response as the pendingElections cached object.
				if (enrollmentCache != null && enrollmentCache.getPendingElectionsResponse() != null) {
					// call function to replace plans that came from TBA
					// response
					PendingElectionsResponse pendingElectionsFinal = enrollmentCacheUtil
							.updatePendingElectionsCacheWithTBAResponse(pendingElectionsResponse,
									enrollmentCache.getPendingElectionsResponse(), serviceHelperContext);
					// replace existing object with new object
					enrollmentCache.setPendingElectionsResponse(pendingElectionsFinal);
					pendingElectionsResponse = pendingElectionsFinal;

				} else {
					if (enrollmentCache != null && enrollmentCache.getPendingElectionsResponse() == null) {
						enrollmentCache.setPendingElectionsResponse(pendingElectionsResponse);
					} else {
						// create new cache
						enrollmentCache = enrollmentCacheUtil.initializeEnrollmentCache(reqHdr.getSystemTickets(), null,
								null, pendingElectionsResponse, null);
						ArrayList<EnrollmentCache> enrollmentCacheList = new ArrayList<EnrollmentCache>();
						enrollmentCacheList.add(enrollmentCache);
						if (ecl ==null) {
							ecl = new EnrollmentCacheList();
						}
						ecl.setEnrollmentCache(enrollmentCacheList);
					}
				}
				// Delete out availableOptions Cache for input plans.
				if (isToggleAvailableOptionsCache() && enrollmentCache.getAvailableOptionsResponseList() != null) {
					List<AvailableOptionsResponse> avlOptnRspnListCache = enrollmentCache
							.getAvailableOptionsResponseList();
					List<ReviseCancelRequestPlans> reviseCancelRequestPlansArray;
					reviseCancelRequestPlansArray = reviseCancelRequest.getPlans();
					for (int j = 0; j < reviseCancelRequestPlansArray.size(); j++) {
						ReviseCancelRequestPlans reviseCancelRequestPlans;
						reviseCancelRequestPlans = reviseCancelRequestPlansArray.get(j);

						for (int avlOptnRspnIterator = 0; avlOptnRspnIterator < avlOptnRspnListCache
								.size(); avlOptnRspnIterator++) {
							AvailableOptionsResponse avlOptnRspn = avlOptnRspnListCache.get(avlOptnRspnIterator);
							for (int availableOptionChoiceIterator = 0; availableOptionChoiceIterator < avlOptnRspn
									.getChoices().size(); availableOptionChoiceIterator++) {
								if (avlOptnRspn.getChoices().get(availableOptionChoiceIterator).getPlanId()
										.equals(reviseCancelRequestPlans.getPlanId())) {
									avlOptnRspnListCache.remove(avlOptnRspnIterator);
								}
							}
						}
					}
				}

				distributedCacheUtil.saveObjectInCache(cacheKey, ecl);

				// Only output input plans if plan array is populated
				if (reviseCancelRequest != null) {

					List<ReviseCancelRequestPlans> reviseCancelRequestPlansArray;
					reviseCancelRequestPlansArray = reviseCancelRequest.getPlans();

					Boolean savingsAccountFound = false;
					String[] planIdSuffix;

					// check to see if there was a HSA/HRA plan in the
					// revise
					for (int j = 0; j < reviseCancelRequestPlansArray.size(); j++) {

						ReviseCancelRequestPlans reviseCancelRequestPlans;
						reviseCancelRequestPlans = reviseCancelRequestPlansArray.get(j);

						planIdSuffix = reviseCancelRequestPlans.getPlanId().split("_");

						if (planIdSuffix.length > 1
								&& (planIdSuffix[1].equals("HSA") || planIdSuffix[1].equals("HRA"))) {

							savingsAccountFound = true;

						}

					}

					// begin plan filtering logic.
					List<PendingElectionsResponseCoverages> tempCoverages = pendingElectionsResponse.getCoverages();
					List<PendingElectionsResponseCoverages> finalCoverages = new ArrayList<PendingElectionsResponseCoverages>();

					for (int j = 0; j < reviseCancelRequestPlansArray.size(); j++) {

						ReviseCancelRequestPlans reviseCancelRequestPlans;
						reviseCancelRequestPlans = reviseCancelRequestPlansArray.get(j);

						if (tempCoverages != null) {

							for (int k = 0; k < tempCoverages.size(); k++) {

								PendingElectionsResponseCoverages pendingElectionsResponseCoverages;
								pendingElectionsResponseCoverages = tempCoverages.get(k);

								if (reviseCancelRequestPlans.getPlanId()
										.equals(pendingElectionsResponseCoverages.getPlanId())) {

									finalCoverages.add(pendingElectionsResponseCoverages);

								}
								// Add HSA/HRA plan
								else if (savingsAccountFound == false
										&& ((reviseCancelRequestPlans.getPlanId() + "_HSA")
												.equals(tempCoverages.get(k).getPlanId())
												|| (reviseCancelRequestPlans.getPlanId() + "_HRA")
														.equals(tempCoverages.get(k).getPlanId()))) {
									finalCoverages.add(pendingElectionsResponseCoverages);

								}

							}

						}

					}

					pendingElectionsResponse.setCoverages(finalCoverages);

				}

			}

			ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);
			HttpHeaders httpHeaders = new HttpHeaders();

			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			return new ResponseEntity<PendingElectionsResponse>(pendingElectionsResponse, httpHeaders, httpStatus);

		} catch (RuntimeException e) {

			throw e;

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "IntegrationException in putReviseCancel()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "Exception in putReviseCancel()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@GetMapping(value = "/personEnrollments/{businessProcessReferenceId}/excessCredits", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getExcessCredits(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			@PathVariable String businessProcessReferenceId) {

		String resource;
		resource = "excessFlexDollars";

		ExcessCreditsResponse expectedResponse;
		expectedResponse = new ExcessCreditsResponse();

		try {

			ServiceHelperContext serviceHelperContext;
			serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource, appContext);

			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			ExcessCreditsDataHelper helper;
			helper = new ExcessCreditsDataHelper(serviceHelperContext);

			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);

			serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);

			ResponseEntity<String> responseEntity;
			responseEntity = helper.executeService(String.class);

			HttpStatusCode httpStatus;
			httpStatus = responseEntity.getStatusCode();

			ExcessCreditsResponse response;
			response = (ExcessCreditsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);

			ResponseHeader responseHeader;
			responseHeader = (ResponseHeader) serviceHelperContext.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);

			HttpHeaders httpHeaders;
			httpHeaders = new HttpHeaders();

			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			return new ResponseEntity<ExcessCreditsResponse>(response, httpHeaders, httpStatus);

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"IntegrationException in getExcessFlexDollars()", httpRequest.getPathInfo(), e,
					ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "Exception in getExcessFlexDollars()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

// todo remove this comment just here to help find this line
	@PutMapping(value = "/personEnrollments/{businessProcessReferenceId}/reviseExcessCredits", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> putReviseExcessCredits(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			@PathVariable String businessProcessReferenceId,
			@RequestBody ReviseExcessCreditsRequest reviseExcessFlexDollars) {

		String resource;
		resource = "reviseExcessFlexDollars";

		HttpStatusCode httpStatus;

		Object expectedResponse;
		expectedResponse = new Object();

		try {

			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * the "resource" name, and appContext. We need the resource name in situations
			 * where we want to control TBA routing based on the resource. This could also
			 * be accomplished in most cases using the systemInstanceId, so the resource
			 * name is <<<<<<< HEAD optional-you can send in null if you want and it will
			 * choose the default TBA routing for the ======= optional-you can send in null
			 * if you want and it will choose the default TBA routing for the >>>>>>>
			 * refs/remotes/origin/master consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext;
			serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource, appContext);

			/*
			 * Add the requestBody object to the context. This can be whatever object you
			 * want the helper to process (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.REQUEST_BODY, reviseExcessFlexDollars);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.
			 */
			ReviseExcessCreditsDataHelper helper;
			helper = new ReviseExcessCreditsDataHelper(serviceHelperContext);

			/*
			 * Attach the RequestParam values to the ServiceHelperContext instance as
			 * name/value pairs.
			 */
			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);

			serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);

			/*
			 * Execute the helper
			 */
			ResponseEntity<String> responseEntity;
			responseEntity = helper.executeService(String.class);

			/*
			 * Get the responseBody object out of the context and return it to the caller as
			 * a JSON string. Also, do any other response-specific items, like set the
			 * ResponseHeader.
			 */

			ExcessCreditsResponse response;
			response = (ExcessCreditsResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);

			ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);
			HttpHeaders httpHeaders = new HttpHeaders();

			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			httpStatus = responseEntity.getStatusCode();
			// httpStatus = HttpStatus.OK;

			return new ResponseEntity<ExcessCreditsResponse>(response, httpHeaders, httpStatus);

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"IntegrationException in reviseExcessFlexDollars()", httpRequest.getPathInfo(), e,
					ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "Exception in reviseExcessFlexDollars()",
					httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@DeleteMapping("/personEnrollments/deleteCache")
	public void deleteCache(@RequestParam(required = true) String operationName,
			@RequestParam(value = "planId", required = false) String[] planIds) {

		String serviceName = ENROLLMENT_SERVICE;
		boolean cacheUpdateFlag = false;
		RequestHeader alightRequestHeaderObject = null;
		ServiceDelegator serviceDelegator = null;

		try {
			serviceDelegator = new ServiceDelegator();
			alightRequestHeaderObject = serviceDelegator.getRequestHeader();

			if (alightRequestHeaderObject != null) {
				String cacheKey = EnrollmentCacheUtil.createCacheKey(serviceName,
						alightRequestHeaderObject.getClientId(), alightRequestHeaderObject.getSubjectId());

				EnrollmentCacheList ecl = (EnrollmentCacheList) distributedCacheUtil.getObjectFromCache(cacheKey);

				if (ecl == null) {
					return;
				}

				EnrollmentCache enrollmentCache = enrollmentCacheUtil.getEnrollmentCacheForSystemTicket(ecl,
						serviceDelegator.getRequestHeader().getSystemTickets());
				if (enrollmentCache != null) {
					if (operationName.equals(OperationNames.AVAILABLE_OPTIONS)
							&& enrollmentCache.getAvailableOptionsResponseList()!= null) {
						if (!ObjectUtils.isEmpty(planIds)) {
							for (int i = 0; i < planIds.length; i++) {
								List<AvailableOptionsResponse> avlOptnRspnListCache = enrollmentCache
										.getAvailableOptionsResponseList();
								for (AvailableOptionsResponse avlOptnRspn : avlOptnRspnListCache) {
									if (avlOptnRspn.getChoices().get(i).getPlanId().equals(planIds[i])) {
										avlOptnRspnListCache.remove(i);
										cacheUpdateFlag = true;
										break;
									}
								}
							}
						}
					} else if (operationName.equals(OperationNames.ELIGIBLE_PLANS)){
							if (enrollmentCache.getEligiblePlansResponse() != null) {
								enrollmentCache.setEligiblePlansResponse(null);
								cacheUpdateFlag = true;
							}
					} else if (operationName.equals(OperationNames.PENDING_ELECTIONS)
							&& enrollmentCache.getPendingElectionsResponse()!= null) {
						enrollmentCache.setRefreshPendingElections(true);
						cacheUpdateFlag = true;
					} else if (operationName.equals(OperationNames.ENROLLMENTS)
							&& enrollmentCache.getEnrollmentsResponse()!= null) {
						enrollmentCache.setEnrollmentsResponse(null);
						cacheUpdateFlag = true;
					} else if (operationName.equals(OperationNames.ALL)) {
						enrollmentCache.setRefreshPendingElections(true);
						enrollmentCache.setEligiblePlansResponse(null);
						enrollmentCache.setAvailableOptionsResponseList(null);
						enrollmentCache.setEnrollmentsResponse(null);
						cacheUpdateFlag = true;
					} else {
						throw new IntegrationException("Invalid Operation Name Passed");
					}
					
					if (cacheUpdateFlag) {
						ArrayList<EnrollmentCache> enrollmentCacheList = new ArrayList<EnrollmentCache>();
						enrollmentCacheList.add(enrollmentCache);
						ecl.setEnrollmentCache(enrollmentCacheList);
						distributedCacheUtil.saveObjectInCache(cacheKey, ecl);
					}
				}
			}
		} catch (IntegrationException e) {
			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "Exception in deleteCache()", "", e,
					ErrorLogEvent.ERROR_SEVERITY);
		} catch (IOException e) {
			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(), "Exception in deleteCache()", "", e,
					ErrorLogEvent.ERROR_SEVERITY);
		}
	}

	@GetMapping(value = "/personEnrollments/{businessProcessReferenceId}/personRelationshipMaintenanceSimulation", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getPersonRelationshipMaintenanceSimulation(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse, @PathVariable String businessProcessReferenceId,
			@RequestParam(required = true) String relationshipSequence) {

		String resource;
		resource = "personRelationshipMaintenanceSimulation";

		PersonRelationshipMaintenanceSimulationResponse expectedResponse;
		expectedResponse = new PersonRelationshipMaintenanceSimulationResponse();

		try {

			ServiceHelperContext serviceHelperContext;
			serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource, appContext);

			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			PersonRelationshipMaintenanceSimulationHelper helper;
			helper = new PersonRelationshipMaintenanceSimulationHelper(serviceHelperContext);

			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);

			serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);

			RequestVariableValidationUtility.validateIntegerAsStringElement(relationshipSequence,
					"relationshipSequence", true);

			serviceHelperContext.set("relationshipSequence", relationshipSequence);

			ResponseEntity<String> responseEntity;
			responseEntity = helper.executeService(String.class);

			HttpStatusCode httpStatus;
			httpStatus = responseEntity.getStatusCode();

			PersonRelationshipMaintenanceSimulationResponse response;
			response = (PersonRelationshipMaintenanceSimulationResponse) serviceHelperContext
					.get(ServiceHelperContext.RESPONSE_BODY);

			ResponseHeader responseHeader;
			responseHeader = (ResponseHeader) serviceHelperContext.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);

			HttpHeaders httpHeaders;
			httpHeaders = new HttpHeaders();

			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			return new ResponseEntity<PersonRelationshipMaintenanceSimulationResponse>(response, httpHeaders,
					httpStatus);

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"IntegrationException in getPersonRelationshipMaintenanceSimulation()", httpRequest.getPathInfo(),
					e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"Exception in getPersonRelationshipMaintenanceSimulation()", httpRequest.getPathInfo(), e,
					ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
 
	@GetMapping(value = "/personEnrollments/{businessProcessReferenceId}/provisionContactsMaintenancePostalAddressSimulation", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getProvisionContactsMaintenancePostalAddressSimulation(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse, @PathVariable String businessProcessReferenceId) {

		String resource;
		resource = "provisionContactsMaintenancePostalAddressSimulation";

		ProvisionContactsMaintenancePostalAddressSimulationResponse expectedResponse;
		expectedResponse = new ProvisionContactsMaintenancePostalAddressSimulationResponse();

		try {

			ServiceHelperContext serviceHelperContext;
			serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource, appContext);

			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			ProvisionContactsMaintenancePostalAddressSimulationHelper helper;
			helper = new ProvisionContactsMaintenancePostalAddressSimulationHelper(serviceHelperContext);
			
			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);

			serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);

			ResponseEntity<String> responseEntity;
			responseEntity = helper.executeService(String.class);

			HttpStatusCode httpStatus;

			ProvisionContactsMaintenancePostalAddressSimulationResponse response;
			response = (ProvisionContactsMaintenancePostalAddressSimulationResponse) serviceHelperContext
					.get(ServiceHelperContext.RESPONSE_BODY);
			
            // When we have a null object being returned, we want a 404 httpStatus returned. Not sure why responseEntity.getStatusCode() doesn't
			// get that for us. However, for now just hacking the same.
			if (response.getProvisionPostalContact() == null){
				httpStatus = HttpStatus.NOT_FOUND;
			}
			else { 
				httpStatus = responseEntity.getStatusCode();
			}					
			
			ResponseHeader responseHeader;
			responseHeader = (ResponseHeader) serviceHelperContext.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);

			HttpHeaders httpHeaders;
			httpHeaders = new HttpHeaders();

			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			return new ResponseEntity<ProvisionContactsMaintenancePostalAddressSimulationResponse>(response, httpHeaders,
					httpStatus);

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"IntegrationException in getProvisionContactsMaintenancePostalAddressSimulation()", httpRequest.getPathInfo(),
					e, ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"Exception in getProvisionContactsMaintenancePostalAddressSimulation()", httpRequest.getPathInfo(), e,
					ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@PostMapping(value = "/personEnrollments/{businessProcessReferenceId}/personRelationshipMaintenanceSimulation", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> postPersonRelationshipMaintenanceSimulation(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse, @PathVariable String businessProcessReferenceId,
			@RequestBody PersonRelationshipMaintenanceSimulationRequest personRelationshipMaintenanceSimulationRequest) {

		String resource;
		resource = "personRelationshipMaintenanceSimulation";

		HttpStatusCode httpStatus;

		Object expectedResponse;
		expectedResponse = new Object();

		try {

			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * the "resource" name, and appContext. We need the resource name in situations
			 * where we want to control TBA routing based on the resource. This could also
			 * be accomplished in most cases using the systemInstanceId, so the resource
			 * name is <<<<<<< HEAD optional-you can send in null if you want and it will
			 * choose the default TBA routing for the ======= optional-you can send in null
			 * if you want and it will choose the default TBA routing for the >>>>>>>
			 * refs/remotes/origin/master consumer+client+systemInstanceId+testCfg.
			 */
			ServiceHelperContext serviceHelperContext;
			serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource, appContext);

			/*
			 * Add the requestBody object to the context. This can be whatever object you
			 * want the helper to process (ie-POJO, etc).
			 * 
			 * 
			 */
			serviceHelperContext.set(ServiceHelperContext.REQUEST_BODY, personRelationshipMaintenanceSimulationRequest);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).
			 */
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.
			 */
			PostHMAddDependentSimulationHelper helper;
			helper = new PostHMAddDependentSimulationHelper(serviceHelperContext);

			/*
			 * Attach the RequestParam values to the ServiceHelperContext instance as
			 * name/value pairs.
			 */
			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);

			serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);

			/*
			 * Execute the helper
			 */
			ResponseEntity<String> responseEntity;
			responseEntity = helper.executeService(String.class);

			/*
			 * Get the responseBody object out of the context and return it to the caller as
			 * a JSON string. Also, do any other response-specific items, like set the
			 * ResponseHeader.
			 */

			PersonRelationshipMaintenanceSimulationResponse response;
			response = (PersonRelationshipMaintenanceSimulationResponse) serviceHelperContext
					.get(ServiceHelperContext.RESPONSE_BODY);

			ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);
			HttpHeaders httpHeaders = new HttpHeaders();

			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			 httpStatus = responseEntity.getStatusCode();
			// httpStatus = HttpStatus.OK;
			
			// delete cache so that the new dependent is recognized by the eligiblePlans call
			deleteCache("eligiblePlans", null);
			
			return new ResponseEntity<PersonRelationshipMaintenanceSimulationResponse>(response, httpHeaders,
					httpStatus);

		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"IntegrationException in personRelationshipMaintenanceSimulation()", httpRequest.getPathInfo(), e,
					ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"Exception in personRelationshipMaintenanceSimulation()", httpRequest.getPathInfo(), e,
					ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	
	@PutMapping(value = "/personEnrollments/{businessProcessClassification}/personMaintenanceSimulation", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> putPersonMaintenanceSimulation (HttpServletRequest httpRequest,
			HttpServletResponse httpResponse,
			@PathVariable String businessProcessClassification,
			@RequestBody PersonMaintenanceSimulationRequest personMaintenanceSimulationRequest) {

		String resource;
		resource = "personMaintenanceSimulation";

		HttpStatusCode httpStatus;

		Object expectedResponse;
		expectedResponse = new Object();

		try {

			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * the "resource" name, and appContext. We need the resource name in situations
			 * where we want to control TBA routing based on the resource. This could also
			 * be accomplished in most cases using the systemInstanceId, so the resource
			 * name is <<<<<<< HEAD optional-you can send in null if you want and it will
			 * choose the default TBA routing for the ======= optional-you can send in null
			 * if you want and it will choose the default TBA routing for the >>>>>>>
			 * refs/remotes/origin/master consumer+client+systemInstanceId+testCfg.*/
			ServiceHelperContext serviceHelperContext;
			serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource, appContext);

			/*
			 * Add the requestBody object to the context. This can be whatever object you
			 * want the helper to process (ie-POJO, etc).
			 * 
			 * */
			serviceHelperContext.set(ServiceHelperContext.REQUEST_BODY, personMaintenanceSimulationRequest);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).*/
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.*/
			PutPersonMaintenanceSimulationHelper helper;
			helper = new PutPersonMaintenanceSimulationHelper (serviceHelperContext);

			/*
			 * Attach the RequestParam values to the ServiceHelperContext instance as
			 * name/value pairs.
			 */
			RequestVariableValidationUtility.validateStringElement(businessProcessClassification,
					"businessProcessClassification", true);
			serviceHelperContext.set("businessProcessClassification", businessProcessClassification);
			
			/*
			 * Execute the helper*/
			ResponseEntity<String> responseEntity;
			responseEntity = helper.executeService(String.class);

			/*
			 * Get the responseBody object out of the context and return it to the caller as
			 * a JSON string. Also, do any other response-specific items, like set the
			 * ResponseHeader.*/ 
			httpStatus = responseEntity.getStatusCode();

  			Object response;
			response = (Object) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);

			ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			return new ResponseEntity<Object>(response, httpHeaders, httpStatus);
			
		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"IntegrationException in personMaintenanceSimulation()", httpRequest.getPathInfo(), e,
					ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"Exception in personMaintenanceSimulation()", httpRequest.getPathInfo(), e,
					ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}	 		

	@PutMapping(value = "/personEnrollments/{businessProcessReferenceId}/personContactsMaintenancePostalAddressSimulation", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> putPersonContactsMaintenancePostalAddressSimulation (HttpServletRequest httpRequest,
			HttpServletResponse httpResponse, @PathVariable String businessProcessReferenceId,
			@RequestBody PersonContactsMaintenancePostalAddressSimulationRequest personContactsMaintenancePostalAddressSimulationRequest) {

		String resource;
		resource = "personContactsMaintenancePostalAddressSimulation";

		HttpStatusCode httpStatus;

		Object expectedResponse;
		expectedResponse = new Object();
		
		try {

			/*
			 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
			 * the "resource" name, and appContext. We need the resource name in situations
			 * where we want to control TBA routing based on the resource. This could also
			 * be accomplished in most cases using the systemInstanceId, so the resource
			 * name is <<<<<<< HEAD optional-you can send in null if you want and it will
			 * choose the default TBA routing for the ======= optional-you can send in null
			 * if you want and it will choose the default TBA routing for the >>>>>>>
			 * refs/remotes/origin/master consumer+client+systemInstanceId+testCfg.*/
			 
			ServiceHelperContext serviceHelperContext;
			serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource, appContext);

			/*
			 * Add the requestBody object to the context. This can be whatever object you
			 * want the helper to process (ie-POJO, etc).
			 * 
			 * */
			 
			serviceHelperContext.set(ServiceHelperContext.REQUEST_BODY, personContactsMaintenancePostalAddressSimulationRequest);

			/*
			 * Add the responseBody object to the context. This can be whatever object you
			 * want the helper to populate (ie-POJO, etc).*/
			 
			serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

			/*
			 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
			 * will handle all provider-specific stuff, in this case managing the TBA
			 * connector code. In the case of TBA helpers, this class is responsible for
			 * managing 1:many subservice calls.*/
			 
			PutPersonContactsMaintenancePostalAddressSimulationHelper helper;
			helper = new PutPersonContactsMaintenancePostalAddressSimulationHelper (serviceHelperContext);

			/*
			 * Attach the RequestParam values to the ServiceHelperContext instance as
			 * name/value pairs.*/
			 
			RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
					"businessProcessReferenceId", true);

			serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);

			/*
			 * Execute the helper*/
			 
			ResponseEntity<String> responseEntity;
			responseEntity = helper.executeService(String.class);

			/*
			 * Get the responseBody object out of the context and return it to the caller as
			 * a JSON string. Also, do any other response-specific items, like set the
			 * ResponseHeader.*/ 
			httpStatus = responseEntity.getStatusCode();

  			Object response;
			response = (Object) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);

			ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
					.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

			return new ResponseEntity<Object>(response, httpHeaders, httpStatus);
			
		} catch (IntegrationException e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"IntegrationException in personContactsMaintenancePostalAddressSimulation()", httpRequest.getPathInfo(), e,
					ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {

			ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
					"Exception in personContactsMaintenancePostalAddressSimulation()", httpRequest.getPathInfo(), e,
					ErrorLogEvent.ERROR_SEVERITY);

			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	
	//HMP1-5040 Leave Intg New GET API eligibleBusinessProcessClassifications
		@GetMapping(value = "/enrollments/eligibleBusinessProcessClassifications", produces = { MediaType.APPLICATION_JSON_VALUE })
		public ResponseEntity<?> getEligibleBusinessProcessClassifications(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
				@RequestParam(required = false, defaultValue = "1800-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") Date effectiveDate,
				@RequestParam(value = "businessProcessSummary", required = false) String businessProcessSummary,
				@RequestParam(value = "businessProcessClassificationList", required = false) String[] businessProcessClassificationList) {
						
			String resource;
			resource = "eligibleBusinessProcessClassifications";

			EligibleBusinessProcessesResponse expectedResponse;
			expectedResponse = new EligibleBusinessProcessesResponse();

			HttpStatusCode httpStatus = HttpStatus.OK;

			try {

				/*
				 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
				 * and the "resource" name, and appContext. We need the resource name in
				 * situations where we want to control TBA routing based on the resource. This
				 * could also be accomplished in most cases using the systemInstanceId, so the
				 * resource name is optional-you can send in null if you want and it will choose
				 * the default TBA routing for the consumer+client+systemInstanceId+testCfg.
				 */
				ServiceHelperContext serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource,
						appContext);

				/*
				 * Add the responseBody object to the context. This can be whatever object you
				 * want the helper to populate (ie-POJO, etc).
				 */
				serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);
				
				/*
				 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
				 * will handle all provider-specific stuff, in this case managing the TBA
				 * connector code. In the case of TBA helpers, this class is responsible for
				 * managing 1:many subservice calls.
				 */
				EligibleBusinessProcessClassificationsHelper helper = new EligibleBusinessProcessClassificationsHelper(serviceHelperContext);
	            
				/*
				 * Attach the RequestParam values to the ServiceHelperContext instance as
				 * name/value pairs.
				 */
				
				serviceHelperContext.set("effectiveDate", effectiveDate);
				serviceHelperContext.set("businessProcessSummary", businessProcessSummary);
				
				String[] cleansedBusinessProcessClassificationList=null;
				
				if(businessProcessClassificationList!=null) {
					
					RequestVariableValidationUtility.validateStringArray(businessProcessClassificationList,
							"businessProcessClassificationList", true);
					
					
					cleansedBusinessProcessClassificationList = EligiblePlansResponseUtility.removeDuplicates(businessProcessClassificationList);
					
				}
				
				
				serviceHelperContext.set("businessProcessClassificationList", cleansedBusinessProcessClassificationList);	           
				
				ResponseEntity<String> responseEntity;

				/*
				 * Validate 
				 */
				int busPrcsClassListArCt = 0;
				
				if (cleansedBusinessProcessClassificationList != null && cleansedBusinessProcessClassificationList.length > 0) {
					busPrcsClassListArCt=cleansedBusinessProcessClassificationList.length;
				};
				
				if (busPrcsClassListArCt > 10) {
					
					ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
							"businessProcessClassificationList Query Param (String Array) contains > 10 entries. Array Count = "+ busPrcsClassListArCt,
							"getEligibleBusinessProcessClassifications", 
							null, ErrorLogEvent.ERROR_SEVERITY);
					
					return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
							.body("businessProcessClassificationList Query Param (String Array) contains > 10 entries.");					
					
				}								
				
				responseEntity = helper.executeService(String.class);

				httpStatus = responseEntity.getStatusCode();
				
				EligibleBusinessProcessesResponse response;
				response = (EligibleBusinessProcessesResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);
				ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
						.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());
				return new ResponseEntity<EligibleBusinessProcessesResponse>(response, httpHeaders, httpStatus);
				
			} catch (IntegrationException e) {
				httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
				ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
						"IntegrationException in eligibleBusinessProcessClassifications() ",
						httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);

				return new ResponseEntity<String>(e.getLocalizedMessage(), httpStatus);

			} catch (Exception e) {
				httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
				ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
						"Exception in eligibleBusinessProcessClassifications() ", httpRequest.getPathInfo(), e,
						ErrorLogEvent.ERROR_SEVERITY);

				return new ResponseEntity<String>(e.getLocalizedMessage(), httpStatus);

			}

		}

		@GetMapping(value = "/personEnrollments/{businessProcessReferenceId}/personBenefitsImpact", produces = {
				MediaType.APPLICATION_JSON_VALUE })
		public ResponseEntity<?> getPersonBenefitsImpact (HttpServletRequest httpRequest,
				HttpServletResponse httpResponse, @PathVariable String businessProcessReferenceId) {

			String resource;
			resource = "personBenefitsImpact";

			HttpStatusCode httpStatus;

			Object expectedResponse;
			expectedResponse = new Object();
			
			try {

				/*
				 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
				 * the "resource" name, and appContext. We need the resource name in situations
				 * where we want to control TBA routing based on the resource. This could also
				 * be accomplished in most cases using the systemInstanceId, so the resource
				 * name is <<<<<<< HEAD optional-you can send in null if you want and it will
				 * choose the default TBA routing for the ======= optional-you can send in null
				 * if you want and it will choose the default TBA routing for the >>>>>>>
				 * refs/remotes/origin/master consumer+client+systemInstanceId+testCfg.*/
				 
				ServiceHelperContext serviceHelperContext;
				serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource, appContext);

				/*
				 * Add the requestBody object to the context. This can be whatever object you
				 * want the helper to process (ie-POJO, etc).
				 * 
				 * */
				 
//				serviceHelperContext.set(ServiceHelperContext.REQUEST_BODY, personContactsMaintenancePostalAddressSimulationRequest);

				/*
				 * Add the responseBody object to the context. This can be whatever object you
				 * want the helper to populate (ie-POJO, etc).*/
				 
				serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);

				/*
				 * Create the ServiceHelper class, passing in the ServiceHelperContext. This
				 * will handle all provider-specific stuff, in this case managing the TBA
				 * connector code. In the case of TBA helpers, this class is responsible for
				 * managing 1:many subservice calls.*/
				 
				GetPersonBenefitsImpactHelper helper;
				helper = new GetPersonBenefitsImpactHelper (serviceHelperContext);

				/*
				 * Attach the RequestParam values to the ServiceHelperContext instance as
				 * name/value pairs.*/
				 
				RequestVariableValidationUtility.validateIntegerAsStringElement(businessProcessReferenceId,
						"businessProcessReferenceId", true);

				serviceHelperContext.set("businessProcessReferenceId", businessProcessReferenceId);

				/*
				 * Execute the helper*/
				 
				ResponseEntity<String> responseEntity;
				responseEntity = helper.executeService(String.class);

				/*
				 * Get the responseBody object out of the context and return it to the caller as
				 * a JSON string. Also, do any other response-specific items, like set the
				 * ResponseHeader.*/ 
				httpStatus = responseEntity.getStatusCode();

	  			Object response;
				response = (Object) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);

				ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
						.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());

				return new ResponseEntity<Object>(response, httpHeaders, httpStatus);
				
			} catch (IntegrationException e) {

				ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
						"IntegrationException in personBenefitsImpact()", httpRequest.getPathInfo(), e,
						ErrorLogEvent.ERROR_SEVERITY);

				return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

			} catch (Exception e) {

				ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
						"Exception in personBenefitsImpact()", httpRequest.getPathInfo(), e,
						ErrorLogEvent.ERROR_SEVERITY);

				return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

			}
		}
		
		
	//TODO - This still gets that static error, so giving up on encapsulation for now.
	//       Ideally we would circle back and figure out why we couldn't encapsulate this logic.
	/*
	public boolean checkIfEnrollmentCachedAndIsSimulation(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			ServiceHelperContext serviceHelperContext) {		

		boolean isSimulationBusinessProcess  = false;
		// Check if this is a simulation enrollment 
		
		RequestHeader reqHdr = (RequestHeader) serviceHelperContext.get(ServiceHelperContext.ALIGHT_REQUEST_HEADER);

		String serviceName = "enrollment";
		EnrollmentCache enrollmentCache = null;
		EnrollmentCacheUtil enrollmentCacheUtil = new EnrollmentCacheUtil();
		
		EnrollmentDataHelper helper;
		try {
			helper = new EnrollmentDataHelper(serviceHelperContext);

			helper.checkSystemTicket(reqHdr);

			if (reqHdr != null && serviceHelperContext.get(CacheConstants.SYSTEMTICKET) != null){
				String subjectId = reqHdr.getSubjectId();
				String clientId = reqHdr.getClientId();
				String cacheKey = EnrollmentCacheUtil.createCacheKey(serviceName, clientId, subjectId);
				//EnrollmentCacheList ecl = (EnrollmentCacheList) distributedCacheUtil.getObjectFromCache(cacheKey);
				EnrollmentCacheList ecl = (EnrollmentCacheList) distributedCacheUtil.get
				enrollmentCache = enrollmentCacheUtil.getEnrollmentCacheForSystemTicket(ecl, reqHdr.getSystemTickets());


				if (enrollmentCache != null && enrollmentCache.getEnrollmentsResponse() != null) {
					EnrollmentsResponse enrollments = enrollmentCache.getEnrollmentsResponse();
					String businessProcessSummary;					
					businessProcessSummary = enrollments.getEnrollments().get(0).getBusinessProcess().getBusinessProcessSummary();
					if (businessProcessSummary == "simulationEnrollment"){
						isSimulationBusinessProcess = true;
					}
					return isSimulationBusinessProcess;

				}
			}

		} catch (IntegrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isSimulationBusinessProcess;
	}
*/	 
		
		@GetMapping(value = "/enrollments/planAvailability", produces = { MediaType.APPLICATION_JSON_VALUE })
		public ResponseEntity<?> getPlanAvailability(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
				@RequestParam(value = "businessProcessClassification", required = false) String businessProcessClassification) {
						
			String resource;
			resource = "planAvailability";
			//PlanAvailabilityResponse	
			PlanAvailabilityResponse expectedResponse;
			expectedResponse = new PlanAvailabilityResponse();
			HttpStatusCode httpStatus = HttpStatus.OK;
		
			try {
		
				/*
				 * Create the ServiceHelperContext, passing in the httpRequest, httpResponse,
				 * and the "resource" name, and appContext. We need the resource name in
				 * situations where we want to control TBA routing based on the resource. This
				 * could also be accomplished in most cases using the systemInstanceId, so the
				 * resource name is optional-you can send in null if you want and it will choose
				 * the default TBA routing for the consumer+client+systemInstanceId+testCfg.
				 */
				ServiceHelperContext serviceHelperContext = new ServiceHelperContext(httpRequest, httpResponse, resource,
						appContext);
		
				
				serviceHelperContext.set(ServiceHelperContext.RESPONSE_BODY, expectedResponse);
				
				
				PlanAvailabilityHelper helper = new PlanAvailabilityHelper(serviceHelperContext);
		        
				/*
				 * Attach the RequestParam values to the ServiceHelperContext instance as
				 * name/value pairs.
				 */
				
				//aserviceHelperContext.set("effectiveDate", effectiveDate);
				serviceHelperContext.set("businessProcessClassification", businessProcessClassification);
				
				
				ResponseEntity<String> responseEntity;
				responseEntity = helper.executeService(String.class);
		
				httpStatus = responseEntity.getStatusCode();
				
				PlanAvailabilityResponse response;
				response = (PlanAvailabilityResponse) serviceHelperContext.get(ServiceHelperContext.RESPONSE_BODY);
				ResponseHeader responseHeader = (ResponseHeader) serviceHelperContext
						.get(ServiceHelperContext.ALIGHT_RESPONSE_HEADER);
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.add(ServiceHelperContext.ALIGHT_RESPONSE_HEADER, responseHeader.toJson());
				return new ResponseEntity<PlanAvailabilityResponse>(response, httpHeaders, httpStatus);
				
			} catch (IntegrationException e) {
				httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
				ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
						"IntegrationException in eligibleBusinessProcessClassifications() ",
						httpRequest.getPathInfo(), e, ErrorLogEvent.ERROR_SEVERITY);
		
				return new ResponseEntity<String>(e.getLocalizedMessage(), httpStatus);
		
			} catch (Exception e) {
				httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
				ErrorLogEventHelper.logErrorEvent(this.getClass().getName(),
						"Exception in eligibleBusinessProcessClassifications() ", httpRequest.getPathInfo(), e,
						ErrorLogEvent.ERROR_SEVERITY);
		
				return new ResponseEntity<String>(e.getLocalizedMessage(), httpStatus);
		
			}
		
		}
		
		@PutMapping(value = "/supplimentalhealth/healthPlans", produces = {
				MediaType.APPLICATION_JSON_VALUE })
		public ResponseEntity<?> getHalthPlan(HttpServletRequest httpRequest, 
				HttpServletResponse httpResponse,
				
				 @RequestBody HealthPlanRequest request 
			) {
			System.out.println("inside controller");
			System.out.println(request.getClientId());
			System.out.println(httpRequest.getHeader("alightRequestHeader"));
			
			HealthPlanAvailabilityHelper healthPlanAvailabilityHelper = new HealthPlanAvailabilityHelper();
			healthPlanAvailabilityHelper.execute(request);		
			
			return null;
			
		}
		
}