package com.alight.microservice.getplan.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"dataElementDisplaySequence",
    "dataElementsId",
    "isDataElementsVariable",
    "dataElementsType",
    "dataElementsTitle",
    "dynamicDataDetail"
})
public class HpccChartDataElement implements Comparable<HpccChartDataElement>{

    @JsonProperty("dataElementsId")
    private String dataElementsId;
    @JsonProperty("isDataElementsVariable")
    private String isDataElementsVariable;
    @JsonProperty("dataElementsType")
    private String dataElementsType;
    @JsonProperty("dataElementsTitle")
    private List<HpccChartForeignLanguage> langTextList;
    //Adding Property for display sequence
    @JsonProperty("dataElementDisplaySequence")
    private String dataElementDisplaySequence;
    @JsonProperty("dynamicDataDetail")
    private List<HpccDynamicDataDetail> dynamicDataDetail = null;
    @JsonIgnore   
    private String dataElementsTitle;
    @JsonIgnore
    private String superSectionId;
   
    
	public String getIsDataElementsVariable() {
		return isDataElementsVariable;
	}

	public String getDataElementsId() {
		return dataElementsId;
	}

	public void setDataElementsId(String dataElementsId) {
		this.dataElementsId = dataElementsId;
	}

	public void setIsDataElementsVariable(String isDataElementsVariable) {
		this.isDataElementsVariable = isDataElementsVariable;
	}

    public String getDataElementsType() {
		return dataElementsType;
	}

	public void setDataElementsType(String dataElementsType) {
		this.dataElementsType = dataElementsType;
	}

	public String getDataElementsTitle() {
        return dataElementsTitle;
    }

    public void setDataElementsTitle(String dataElementsTitle) {
        this.dataElementsTitle = dataElementsTitle;
    }

	public String getSuperSectionId() {
		return superSectionId;
	}

	public void setSuperSectionId(String superSectionId) {
		this.superSectionId = superSectionId;
	}

	public List<HpccChartForeignLanguage> getLangTextList() {
		return langTextList;
	}

	public void setLangTextList(List<HpccChartForeignLanguage> langTextList) {
		this.langTextList = langTextList;
	}

	public List<HpccDynamicDataDetail> getDynamicDataDetail() {
		return dynamicDataDetail;
	}

	public void setDynamicDataDetail(List<HpccDynamicDataDetail> dynamicDataDetail) {
		this.dynamicDataDetail = dynamicDataDetail;
	}
	
	public String getDataElementDisplaySequence() {
		return dataElementDisplaySequence;
	}

	public void setDataElementDisplaySequence(String dataElementDisplaySequence) {
		this.dataElementDisplaySequence = dataElementDisplaySequence;
	}

    @Override
    public int hashCode()
    {

        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((dataElementsId == null) ? 0 : dataElementsId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HpccChartDataElement other = (HpccChartDataElement) obj;
        if (dataElementsId == null)
        {
            if (other.dataElementsId != null)
                return false;
        }
        else if (!dataElementsId.equals(other.dataElementsId))
            return false;
        return true;
    }

    @Override
    public int compareTo(HpccChartDataElement o)
    {

        return dataElementsId.compareTo(o.getDataElementsId());
        //return 0;
    }
    

}