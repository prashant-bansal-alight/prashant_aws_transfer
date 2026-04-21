
package com.alight.microservice.getplan.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "healthManagementPlanChoiceReferenceId",
    "normalizedClientId",
    "sourceSchemaName",
    "sourceSystem",
    "sourceSystemTimestamp",
    "healthPlanData"
})
public class HpccHealthManagementPlanChoice {

	@JsonProperty("healthManagementPlanChoiceReferenceId")
	private String healthManagementPlanChoiceReferenceId;
	
	@JsonProperty("normalizedClientId")
	private String normalizedClientId;
	
	@JsonProperty("sourceSchemaName")
	private String sourceSchemaName;
	
	@JsonProperty("sourceSystem")
	private String sourceSystem;
	
	@JsonProperty("sourceSystemTimestamp")
	private String sourceSystemTimestamp;
	
	
	@JsonProperty("healthPlanComparisonChart")
	private HpccHealthPlanData healthPlanData;

	public String getHealthManagementPlanChoiceReferenceId() {
		return healthManagementPlanChoiceReferenceId;
	}

	public void setHealthManagementPlanChoiceReferenceId(String healthManagementPlanChoiceReferenceId) {
		this.healthManagementPlanChoiceReferenceId = healthManagementPlanChoiceReferenceId;
	}

	public String getNormalizedClientId() {
		return normalizedClientId;
	}

	public void setNormalizedClientId(String normalizedClientId) {
		this.normalizedClientId = normalizedClientId;
	}

	public String getSourceSchemaName() {
		return sourceSchemaName;
	}

	public void setSourceSchemaName(String sourceSchemaName) {
		this.sourceSchemaName = sourceSchemaName;
	}

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public String getSourceSystemTimestamp() {
		return sourceSystemTimestamp;
	}

	public void setSourceSystemTimestamp(String sourceSystemTimestamp) {
		this.sourceSystemTimestamp = sourceSystemTimestamp;
	}


	public HpccHealthPlanData getHealthPlanData() {
		return healthPlanData;
	}

	public void setHealthPlanData(HpccHealthPlanData healthPlanData) {
		this.healthPlanData = healthPlanData;
	}
}