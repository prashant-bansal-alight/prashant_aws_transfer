package com.alight.microservice.getplan.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "dataElementsBenefitLevelsCode",
    "dataElementsValue"
})
public class HpccDataElementsBenefitLevel {

    @JsonProperty("dataElementsBenefitLevelsCode")
    private String dataElementsBenefitLevelsCode;
   
    @JsonIgnore
    private List<HpccChartForeignLanguage> dataElementsBenefitLevelsText;
    
   

	@JsonProperty("dataElementsValue")
    private List<HpccChartForeignLanguage> dataElementsValue;
	
	 public List<HpccChartForeignLanguage> getDataElementsValue() {
		return dataElementsValue;
	}

	public void setDataElementsValue(List<HpccChartForeignLanguage> dataElementsValue) {
		this.dataElementsValue = dataElementsValue;
	}

	public List<HpccChartForeignLanguage> getDataElementsBenefitLevelsText() {
			return dataElementsBenefitLevelsText;
		}

	public void setDataElementsBenefitLevelsText(List<HpccChartForeignLanguage> dataElementsBenefitLevelsText) {
		this.dataElementsBenefitLevelsText = dataElementsBenefitLevelsText;
	}
	


   

	

	public String getDataElementsBenefitLevelsCode() {
		return dataElementsBenefitLevelsCode;
	}

	public void setDataElementsBenefitLevelsCode(String dataElementsBenefitLevelsCode) {
		this.dataElementsBenefitLevelsCode = dataElementsBenefitLevelsCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataElementsBenefitLevelsCode == null) ? 0 : dataElementsBenefitLevelsCode.hashCode());
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
		HpccDataElementsBenefitLevel other = (HpccDataElementsBenefitLevel) obj;
		if (dataElementsBenefitLevelsCode == null) {
			if (other.dataElementsBenefitLevelsCode != null)
				return false;
		} else if (!dataElementsBenefitLevelsCode.equals(other.dataElementsBenefitLevelsCode))
			return false;
		if (dataElementsValue == null) {
			if (other.dataElementsValue != null)
				return false;
		} else if (!dataElementsValue.equals(other.dataElementsValue))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataElementsBenefitLevel [dataElementsBenefitLevelsCode=" + dataElementsBenefitLevelsCode
				+ ", dataElementsBenefitLevelsText=" + dataElementsBenefitLevelsText + ", dataElementsValue="
				+ dataElementsValue + "]";
	}
    

}