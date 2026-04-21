package com.alight.enrollnment.healthdata.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;

import com.alight.microservice.getplan.model.HealthPlanRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class HealthPlanAvailabilityHelper {

	public void execute(HealthPlanRequest request) {
		System.out.println("inside healthPlan execute************************");
		MongoClient client = null;

		try {
			if (request == null || request.getSourceSystem() == null) {
				System.err.println("Invalid request");
				return;
			}

			// Create ObjectMapper with JSR310 module
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule()); // Add this line

			String dbName = "udpdev";
			client = MongoClientFactory.getInstance().getConnection("aws", dbName);
			MongoDatabase mongoDb = client.getDatabase(dbName);
			MongoCollection<Document> planChoiceCollection = mongoDb.getCollection("healthManagementPlanChoice");

			String referenceId = populateHpccReferenceId(request.getSourceSystem()).toString();
			Document filter = new Document("healthManagementPlanChoiceReferenceId", referenceId);

			Document result = planChoiceCollection.find(filter).first();

			// Convert HealthPlanRequest to Map, then to Document
			@SuppressWarnings("unchecked")
			Map<String, Object> requestMap = mapper.convertValue(request, Map.class);
			Document healthPlanDetailsDoc = new Document(requestMap);

			if (result != null) {

		        System.out.println("Document found. Merging plans...");

		        // Get existing healthPlanDetails
		        Document existingHealthPlanDetails = (Document) result.get("healthPlanDetails");
		        List<Document> existingPlans = (List<Document>) existingHealthPlanDetails.get("plans");
		        
		        // Convert request plans to Documents
		        @SuppressWarnings("unchecked")
		        List<Map<String, Object>> requestPlansAsMap = mapper.convertValue(request.getPlans(), 
		            mapper.getTypeFactory().constructCollectionType(List.class, Map.class));
		        List<Document> requestPlans = requestPlansAsMap.stream()
		            .map(Document::new)
		            .collect(java.util.stream.Collectors.toList());

		        // Merge plans: Update existing or add new
		        for (Document requestPlan : requestPlans) {
		            String requestPlanType = requestPlan.getString("planType");
		            boolean planFound = false;

		            // Check if plan with same planType exists
		            for (int i = 0; i < existingPlans.size(); i++) {
		                Document existingPlan = existingPlans.get(i);
		                String existingPlanType = existingPlan.getString("planType");

		                if (existingPlanType != null && existingPlanType.equals(requestPlanType)) {
		                    // Plan with same type found - Update it
		                    System.out.println("Plan with type '" + requestPlanType + "' found. Updating...");
		                    existingPlans.set(i, requestPlan);
		                    planFound = true;
		                    break;
		                }
		            }

		            // If plan type not found - Add as new entry
		            if (!planFound) {
		                System.out.println("Plan with type '" + requestPlanType + "' not found. Creating new entry...");
		                existingPlans.add(requestPlan);
		            }
		        }

		        // Convert thresholdReportData
		        @SuppressWarnings("unchecked")
		        List<Map<String, Object>> thresholdAsMap = mapper.convertValue(request.getThresholdReportData(), 
		            mapper.getTypeFactory().constructCollectionType(List.class, Map.class));
		        List<Document> thresholdDocs = thresholdAsMap.stream()
		            .map(Document::new)
		            .collect(java.util.stream.Collectors.toList());

		        // Create updated healthPlanDetails
		        Document updatedHealthPlanDetails = new Document()
		            .append("sourceSystem", request.getSourceSystem())
		            .append("clientId", request.getClientId())
		            .append("plans", existingPlans)  // Merged plans
		            .append("thresholdReportData", thresholdDocs);

		        // Update document in database
		        Document updateDoc = new Document("$set",
		            new Document()
		                .append("healthPlanDetails", updatedHealthPlanDetails)
		                .append("lastModifiedTimestamp", formatTimestamp(System.currentTimeMillis()))
		                .append("version", result.getInteger("version", 1) + 1));

		        planChoiceCollection.updateOne(filter, updateDoc);
		        System.out.println("Document updated successfully with merged plans");
		        printMergedPlansInfo(existingPlans);
			} else {
				System.out.println("Creating new document...");

				Document newDoc = new Document().append("healthManagementPlanChoiceReferenceId", referenceId)
						.append("normalizedClientId", "19968E")
						.append("sourceSystem", request.getSourceSystem())
						.append("sourceSchemaName", request.getSourceSystem())
						.append("healthPlanDetails", healthPlanDetailsDoc)
						.append("sourceSystemTimestamp", formatTimestamp(System.currentTimeMillis()))
						.append("lastModifiedTimestamp", formatTimestamp(System.currentTimeMillis()))
						.append("isActive", true).append("version", 1);

				planChoiceCollection.insertOne(newDoc);
				System.out.println("New document created successfully");
			}

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} 
	}

	private StringBuilder populateHpccReferenceId(String sourceSystem) {
		StringBuilder healthManagementPlanChoiceReferenceId = new StringBuilder();
		
		healthManagementPlanChoiceReferenceId.append("sourceSystem").append("=").append(sourceSystem).append(";")
				.append("sourceSchemaName").append("=").append(("T1PL030E").toUpperCase()).append(";")
				.append("normalizedClientId").append("=").append("19968E").append(";type=hpcc");
		return healthManagementPlanChoiceReferenceId;
	}
	private static String formatTimestamp(Long aLongDate) {
		Date date = new Date(aLongDate);
		return DateFormatUtils.format(date, "yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
	}
	private void printMergedPlansInfo(List<Document> plans) {
	    System.out.println("\n===== Merged Plans Summary =====");
	    for (int i = 0; i < plans.size(); i++) {
	        Document plan = plans.get(i);
	        System.out.println("Plan " + (i + 1) + ": Type=" + plan.getString("planType") + 
	                         ", ID=" + plan.getString("planId"));
	    }
	    System.out.println("Total plans: " + plans.size());
	    System.out.println("================================\n");
	}
	
}
