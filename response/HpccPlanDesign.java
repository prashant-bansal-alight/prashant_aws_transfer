package com.alight.microservice.getplan.response;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "effectiveBeginDate",
    "effectiveEndDate",
    "planDesignData"
})
public class HpccPlanDesign {
	@JsonProperty("effectiveBeginDate")
	private String effectiveBeginDate;
	@JsonProperty("effectiveEndDate")
	private String effectiveEndDate;
	@JsonProperty("planDesignsAttributes")
	private List<HpccPlanDesignData> planDesignData = null;
	
	public String getEffectiveBeginDate() {
		return effectiveBeginDate;
	}
	public void setEffectiveBeginDate(String effectiveBeginDate) {
		this.effectiveBeginDate = effectiveBeginDate;
	}
	public String getEffectiveEndDate() {
		return effectiveEndDate;
	}
	public void setEffectiveEndDate(String effectiveEndDate) {
		this.effectiveEndDate = effectiveEndDate;
	}
	public List<HpccPlanDesignData> getPlanDesignData() {
		return planDesignData;
	}
	public void setPlanDesignData(List<HpccPlanDesignData> planDesignData) {
		this.planDesignData = planDesignData;
	}
	
}
