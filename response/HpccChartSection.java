package com.alight.microservice.getplan.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"sectionDisplaySequence",
    "sectionsId",
    "isSectionVariable",
    "suppressCode",
    "sectionsTitle",
    "dataElements"
})
public class HpccChartSection implements Comparable<HpccChartSection>{

    @JsonProperty("sectionsId")
    private String sectionsId;
    
    @JsonProperty("isSectionVarCode")
    private String isSectionVariable;  
    
	@JsonProperty("sectionsTitle")
    private List<HpccChartForeignLanguage> langTextList;    
    
    @JsonIgnore
    private String sectionsTitle;
    @JsonProperty("dataElements")
    private List<HpccChartDataElement> dataElements = null;
    
    @JsonProperty("suppressCode")
    private String suppressCode;
    
    //Adding property for display sequence
    @JsonProperty("sectionDisplaySequence")
    private String sectionDisplaySequence;

	@JsonIgnore
    private String superSectionId;

    
    
    public String getSectionsId() {
		return sectionsId;
	}

	public void setSectionsId(String sectionsId) {
		this.sectionsId = sectionsId;
	}

	public String getSectionsTitle() {
        return sectionsTitle;
    }

    public void setSectionsTitle(String sectionsTitle) {
        this.sectionsTitle = sectionsTitle;
    }

    public List<HpccChartDataElement> getDataElements() {
        return dataElements;
    }

    public void setDataElements(List<HpccChartDataElement> dataElements) {
        this.dataElements = dataElements;
    }

	
	  public String getSuperSectionId() {
		return superSectionId;
	}

	public void setSuperSectionId(String superSectionId) {
		this.superSectionId = superSectionId;
	}

	public String getIsSectionVariable() {
			return isSectionVariable;
		}

		public void setIsSectionVariable(String isSectionVariable) {
			this.isSectionVariable = isSectionVariable;
		}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sectionsId == null) ? 0 : sectionsId.hashCode());
		result = prime * result + ((superSectionId == null) ? 0 : superSectionId.hashCode());
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
		HpccChartSection other = (HpccChartSection) obj;
		if (sectionsId == null) {
			if (other.sectionsId != null)
				return false;
		} else if (!sectionsId.equals(other.sectionsId))
			return false;
		if (superSectionId == null) {
			if (other.superSectionId != null)
				return false;
		} else if (!superSectionId.equals(other.superSectionId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ChartSection [sectionsId=" + sectionsId + ", sectionsTitle=" + sectionsTitle + "]";
	}

	@Override
	public int compareTo(HpccChartSection o) {
		return sectionsId.compareTo(o.getSectionsId());
	}

	public List<HpccChartForeignLanguage> getLangTextList() {
		return langTextList;
	}

	public void setLangTextList(List<HpccChartForeignLanguage> langTextList) {
		this.langTextList = langTextList;
	}

	public String getSuppressCode() {
		return suppressCode;
	}

	public void setSuppressCode(String suppressCode) {
		this.suppressCode = suppressCode;
	}

	public String getSectionDisplaySequence() {
		return sectionDisplaySequence;
	}

	public void setSectionDisplaySequence(String sectionDisplaySequence) {
		this.sectionDisplaySequence = sectionDisplaySequence;
	}

    
}