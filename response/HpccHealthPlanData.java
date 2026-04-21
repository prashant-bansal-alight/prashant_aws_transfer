package com.alight.microservice.getplan.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "chartStructure",
    "planDesigns",
    "benefitLevels"
})
public class HpccHealthPlanData {

	@JsonProperty("chartStructure")
	private List<HpccChartDataEffectiveDate> chartStructure = null;

	@JsonProperty("planDesigns")
	private List<HpccPlanDesign> planDesigns = null;
	
	@JsonProperty("dataElementsBenefitLevels")
	private List<HpccBenefitLevelsEffectiveDate> benefitLevels = null;

	public List<HpccChartDataEffectiveDate> getchartStructure() {
		return chartStructure;
	}

	public void setchartStructure(List<HpccChartDataEffectiveDate> healthPlanChartDataHistory) {
		this.chartStructure = healthPlanChartDataHistory;
	}

	public List<HpccPlanDesign> getPlanDesigns() {
		return planDesigns;
	}

	public void setPlanDesigns(List<HpccPlanDesign> planDesignData) {
		this.planDesigns = planDesignData;
	}

	public List<HpccBenefitLevelsEffectiveDate> getBenefitLevels() {
		return benefitLevels;
	}

	public void setBenefitLevels(List<HpccBenefitLevelsEffectiveDate> benefitLevels) {
		this.benefitLevels = benefitLevels;
	}
	
}