package com.alight.microservice.getplan.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonPropertyOrder({
    "dataElementsBenefitLevelsCode",
    "dataElementsBenefitLevelsText",
    "dataElementsValue"
})

public class HpccBenefitLevels {
	
	@JsonIgnore
	private String dataElementsId;
	
	@JsonProperty("dataElementsBenefitLevelsCode")
	private String dataElementsBenefitLevelsCode;
	
	@JsonInclude(Include.NON_NULL)
	@JsonProperty("dataElementsBenefitLevelsText")
	private List<HpccChartBenefitLevelFrgnLang> dataElementsBenefitLevelsText;
	
	@JsonProperty("dataElementsValue")
	private List<HpccChartBenefitLevelFrgnLang> dataElementsValue;
	


	public String getDataElementsId() {
		return dataElementsId;
	}

	public void setDataElementsId(String dataElementsId) {
		this.dataElementsId = dataElementsId;
	}

	public String getDataElementsBenefitLevelsCode() {
		return dataElementsBenefitLevelsCode;
	}

	public void setDataElementsBenefitLevelsCode(String dataElementsBenefitLevelsCode) {
		this.dataElementsBenefitLevelsCode = dataElementsBenefitLevelsCode;
	}

	public List<HpccChartBenefitLevelFrgnLang> getDataElementsBenefitLevelsText() {
		return dataElementsBenefitLevelsText;
	}

	public void setDataElementsBenefitLevelsText(List<HpccChartBenefitLevelFrgnLang> dataElementsBenefitLevelsText) {
		this.dataElementsBenefitLevelsText = dataElementsBenefitLevelsText;
	}

	public List<HpccChartBenefitLevelFrgnLang> getDataElementsValue() {
		return dataElementsValue;
	}

	public void setDataElementsValue(List<HpccChartBenefitLevelFrgnLang> dataElementsValue) {
		this.dataElementsValue = dataElementsValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataElementsBenefitLevelsCode == null) ? 0 : dataElementsBenefitLevelsCode.hashCode());
		result = prime * result
				+ ((dataElementsBenefitLevelsText == null) ? 0 : dataElementsBenefitLevelsText.hashCode());
		result = prime * result + ((dataElementsId == null) ? 0 : dataElementsId.hashCode());
		result = prime * result + ((dataElementsValue == null) ? 0 : dataElementsValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HpccBenefitLevels other = (HpccBenefitLevels) obj;
		if (dataElementsBenefitLevelsCode == null) {
			if (other.dataElementsBenefitLevelsCode != null)
				return false;
		} else if (!dataElementsBenefitLevelsCode.equals(other.dataElementsBenefitLevelsCode))
			return false;
		if (dataElementsBenefitLevelsText == null) {
			if (other.dataElementsBenefitLevelsText != null)
				return false;
		} else if (!dataElementsBenefitLevelsText.equals(other.dataElementsBenefitLevelsText))
			return false;
		if (dataElementsId == null) {
			if (other.dataElementsId != null)
				return false;
		} else if (!dataElementsId.equals(other.dataElementsId))
			return false;
		if (dataElementsValue == null) {
			if (other.dataElementsValue != null)
				return false;
		} else if (!dataElementsValue.equals(other.dataElementsValue))
			return false;
		return true;
	}

	



	
	
	
	
}
