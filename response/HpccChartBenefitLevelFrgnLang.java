package com.alight.microservice.getplan.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"language",
    "text"
})


public class HpccChartBenefitLevelFrgnLang {
	
	

	@JsonProperty("language")
	private String langCode;

	@JsonProperty("text")
	private String Text;
	
	 @JsonIgnore
	private String dataElementValue;

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public String getText() {
		return Text;
	}

	public void setText(String text) {
		this.Text = text;
	}

	public String getDataElementValue() {
		return dataElementValue;
	}

	public void setDataElementValue(String dataElementValue) {
		this.dataElementValue = dataElementValue;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Text == null) ? 0 : Text.hashCode());
		result = prime * result + ((dataElementValue == null) ? 0 : dataElementValue.hashCode());
		result = prime * result + ((langCode == null) ? 0 : langCode.hashCode());
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
		HpccChartBenefitLevelFrgnLang other = (HpccChartBenefitLevelFrgnLang) obj;
		if (Text == null) {
			if (other.Text != null)
				return false;
		} else if (!Text.equals(other.Text))
			return false;
		if (dataElementValue == null) {
			if (other.dataElementValue != null)
				return false;
		} else if (!dataElementValue.equals(other.dataElementValue))
			return false;
		if (langCode == null) {
			if (other.langCode != null)
				return false;
		} else if (!langCode.equals(other.langCode))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "HpccChartBenefitLevelFrgnLang [langCode=" + langCode + ", text=" + Text + "]";
	}
	
	
}
