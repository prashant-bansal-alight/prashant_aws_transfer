package com.alight.microservice.getplan.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
	"chartReferenceId",
    "chartsId",
    "chartsTitle",
    "superSections"
})
public class HpccChartData implements Comparable<HpccChartData>{

	@JsonProperty("chartReferenceId")
	private String chartIdReferenceId;
	@JsonProperty("chartsId")
    private String chartId;
	@JsonProperty("chartsTitle")
	private List<HpccChartForeignLanguage> langTextList;   
	@JsonIgnore
    private String chartTitle;
    @JsonProperty("superSections")
    private List<HpccChartSuperSection> superSections = null;
    

    public String getChartIdReferenceId() {
		return chartIdReferenceId;
	}

	public String getChartId() {
		return chartId;
	}

	public void setChartId(String chartId) {
		this.chartId = chartId;
	}

	public void setChartIdReferenceId(String chartIdReferenceId) {
		this.chartIdReferenceId = chartIdReferenceId;
	}


    public String getChartTitle() {
		return chartTitle;
	}

	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}

	public List<HpccChartSuperSection> getSuperSections() {
        return superSections;
    }

    public void setSuperSections(List<HpccChartSuperSection> superSections) {
        this.superSections = superSections;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chartId == null) ? 0 : chartId.hashCode());
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
		HpccChartData other = (HpccChartData) obj;
		if (chartId == null) {
			if (other.chartId != null)
				return false;
		} else if (!chartId.equals(other.chartId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ChartData [chartId=" + chartId + "]";
	}

	@Override
	public int compareTo(HpccChartData o) {
		return chartId.compareTo(o.getChartId());
	}

	public List<HpccChartForeignLanguage> getLangTextList() {
		return langTextList;
	}

	public void setLangTextList(List<HpccChartForeignLanguage> langTextList) {
		this.langTextList = langTextList;
	}
	
	

    
}
