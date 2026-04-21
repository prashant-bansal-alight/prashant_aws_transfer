package com.alight.microservice.getplan.response;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "compositePlanDesignId",
    "optionId",
    "dataElements"
})
public class HpccPlanDesignData {

    public String getOptionId() {
		return optionId;
	}

	public void setOptionId(String optionId) {
		this.optionId = optionId;
	}

	@JsonProperty("compositePlanDesignId")
    private String compositePlanDesignId;
    
    @JsonProperty("optionId")
    private String optionId;
    
    @JsonProperty("dataElements")
    private List<HpccDataElement> dataElements = null;
   


    public String getCompositePlanDesignId() {
		return compositePlanDesignId;
	}

	public void setCompositePlanDesignId(String compositePlanDesignId) {
		this.compositePlanDesignId = compositePlanDesignId;
	}

	public List<HpccDataElement> getDataElements() {
        return dataElements;
    }

    public void setDataElements(List<HpccDataElement> dataElements) {
        this.dataElements = dataElements;
    }
    
 

}
