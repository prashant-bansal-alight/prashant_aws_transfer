package com.alight.microservice.getplan.response;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "dataElementsId",
    "dataElementsKeyId",
    "dataElementsBenefitLevels"
})
public class HpccDataElement {

    @JsonProperty("dataElementsId")
    private String dataElementsId;
    
    @JsonProperty("dataElementsKeyId")
    private String dataElementsKeyId;
    
    @JsonProperty("dataElementsBenefitLevels")
    private Set<HpccDataElementsBenefitLevel> dataElementsBenefitLevels = null;
    
    @JsonProperty("files")
    private List<HpccFile> files = null;
    
   // @JsonProperty("dynamicDataDetail")
   // private Set<HpccDynamicDataDetail> dynamicDataDetail = null;
    
    

	@JsonIgnore
    private boolean dataCheckField;


    public String getDataElementsId() {
		return dataElementsId;
	}

	public void setDataElementsId(String dataElementsId) {
		this.dataElementsId = dataElementsId;
	}



	public String getDataElementsKeyId() {
		return dataElementsKeyId;
	}

	public void setDataElementsKeyId(String dataElementsKeyId) {
		this.dataElementsKeyId = dataElementsKeyId;
	}

	public Set<HpccDataElementsBenefitLevel> getDataElementsBenefitLevels() {
		return dataElementsBenefitLevels;
	}

	public void setDataElementsBenefitLevels(Set<HpccDataElementsBenefitLevel> dataElementsBenefitLevels) {
		this.dataElementsBenefitLevels = dataElementsBenefitLevels;
	}

	public List<HpccFile> getFiles() {
		return files;
	}

	public void setFiles(List<HpccFile> files) {
		this.files = files;
	}

	public boolean isDataCheckField() {
		return dataCheckField;
	}

	public void setDataCheckField(boolean dataCheckField) {
		this.dataCheckField = dataCheckField;
	}

	
	//public Set<HpccDynamicDataDetail> getDynamicDataDetail() {
		//return dynamicDataDetail;
	//}

	//public void setDynamicDataDetail(Set<HpccDynamicDataDetail> dynamicDataDetail) {
	//	this.dynamicDataDetail = dynamicDataDetail;
	//}


	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataElementsId == null) ? 0 : dataElementsId.hashCode());
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
		HpccDataElement other = (HpccDataElement) obj;
		if (dataElementsId == null) {
			if (other.dataElementsId != null)
				return false;
		} else if (!dataElementsId.equals(other.dataElementsId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataElement [dataElementsId=" + dataElementsId + "]";
	}
	
	

}