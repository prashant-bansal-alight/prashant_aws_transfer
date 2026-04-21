package com.alight.microservice.getplan.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "effectiveBeginDate",
    "effectiveEndDate",
    "benefitLevels"
})
public class HpccBenefitLevelsEffectiveDate {

	@JsonProperty("effectiveBeginDate")
	private String effectiveBeginDate;
	@JsonProperty("effectiveEndDate")
	private String effectiveEndDate;
	@JsonProperty("dataElementsBenefitLevelsAttributes")
	private List<HpccBenefitLevelsData> benefitLevels;
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
	public List<HpccBenefitLevelsData> getBenefitLevels() {
		return benefitLevels;
	}
	public void setBenefitLevels(List<HpccBenefitLevelsData> benefitLevels) {
		this.benefitLevels = benefitLevels;
	}
	
	
	
}
