package com.alight.microservice.getplan.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
	"superSectionDisplaySequence",
    "superSectionsId",
    "superSectionsTitle",
    "superSectionSummary",
    "sections"
})
public class HpccChartSuperSection implements Comparable<HpccChartSuperSection>{

	@JsonProperty("superSectionsId")
    private String superSectionsId;
    @JsonProperty("superSectionsTitle")
    private List<HpccChartForeignLanguage> langTextList;
    @JsonIgnore
    private String superSectionTitle;
    @JsonProperty("superSectionSummary")
    private String superSectionSummary;
    //Adding property for display sequence
    @JsonProperty("superSectionDisplaySequence")
    private String superSectionDisplaySequence;
    @JsonProperty("sections")
    private List<HpccChartSection> sections = null;


    public String getSuperSectionsId() {
		return superSectionsId;
	}

	public void setSuperSectionsId(String superSectionsId) {
		this.superSectionsId = superSectionsId;
	}

	public List<HpccChartForeignLanguage> getLangTextList() {
		return langTextList;
	}

	public void setLangTextList(List<HpccChartForeignLanguage> langTextList) {
		this.langTextList = langTextList;
	}

	public String getSuperSectionTitle() {
		return superSectionTitle;
	}

	public void setSuperSectionTitle(String superSectionTitle) {
		this.superSectionTitle = superSectionTitle;
	}

	public List<HpccChartSection> getSections() {
        return sections;
    }

    public void setSections(List<HpccChartSection> sections) {
        this.sections = sections;
    }
    
    public String getSuperSectionSummary() {
		return superSectionSummary;
	}

	public void setSuperSectionSummary(String superSectionSummary) {
		this.superSectionSummary = superSectionSummary;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((superSectionsId == null) ? 0 : superSectionsId.hashCode());
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
		HpccChartSuperSection other = (HpccChartSuperSection) obj;
		if (superSectionsId == null) {
			if (other.superSectionsId != null)
				return false;
		} else if (!superSectionsId.equals(other.superSectionsId))
			return false;
		
		return true;
	}
	
	@Override
	public int compareTo(HpccChartSuperSection o) {
		return superSectionsId.compareTo(o.getSuperSectionsId());
	}

	@Override
	public String toString() {
		return "ChartSuperSection [superSectionsId=" + superSectionsId + ", langTextList=" + langTextList
				+ ", superSectionTitle=" + superSectionTitle + "]";
	}

	public String getSuperSectionDisplaySequence() {
		return superSectionDisplaySequence;
	}

	public void setSuperSectionDisplaySequence(String superSectionDisplaySequence) {
		this.superSectionDisplaySequence = superSectionDisplaySequence;
	}
	
	
}
