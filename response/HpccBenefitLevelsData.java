package com.alight.microservice.getplan.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class HpccBenefitLevelsData {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({
	    "compositePlanDesignGroupId",
	    "dataElementsId",
	    "benefitLevels"
	})
	
	@JsonProperty("compositePlanDesignGroupId")
	private String compositePlanDesignGroupId;

	@JsonProperty("dataElementsId")
	private String dataElementsId;
	
	@JsonInclude(Include.NON_NULL)
    @JsonProperty("dataElementType")
    private String dataElementType;
	
	@JsonProperty("dataElementsBenefitLevelValue")
	private List<HpccBenefitLevels> benefitLevels=null ;
	
	public String getCompositePlanDesignGroupId() {
		return compositePlanDesignGroupId;
	}


	public void setCompositePlanDesignGroupId(String compositePlanDesignGroupId) {
		this.compositePlanDesignGroupId = compositePlanDesignGroupId;
	}


	public List<HpccBenefitLevels> getBenefitLevels() {
		return benefitLevels;
	}


	public void setBenefitLevels(List<HpccBenefitLevels> benefitLevels) {
		this.benefitLevels = benefitLevels;
	}


	public String getDataElementsId() {
		return dataElementsId;
	}


	public void setDataElementsId(String dataElementsId) {
		this.dataElementsId = dataElementsId;
	}

    public String getDataElementType() {
        return dataElementType;
    }
    
    public void setDataElementType(String dataElementType) {
        this.dataElementType = dataElementType;
    }

	
}
