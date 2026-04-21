package com.alight.microservice.getplan.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "effectiveBeginDate",
    "effectiveEndDate",
    "chartData"
})
public class HpccChartForeignLanguage implements Comparable<HpccChartForeignLanguage> {

	@JsonIgnore
	private String effBeginDate;
	@JsonIgnore
	private String effEndDate;	
	@JsonIgnore
	private String chartId;
	@JsonIgnore
	private String superSectionId;
	@JsonIgnore
    private List<HpccDynamicDataDetail> dynamicDataDetail = null;
	
	
	@JsonIgnore
	private String sectionId;
	
	@JsonIgnore
	private String dataElementId;
	
		
	@JsonProperty("language")
	private String langCode;

	@JsonProperty("text")
	private String text;
    
	@JsonIgnore
	private String chartTitlefrgn;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	
	
	


	public String getEffBeginDate() {
		return effBeginDate;
	}

	public void setEffBeginDate(String effBeginDate) {
		this.effBeginDate = effBeginDate;
	}

	public String getEffEndDate() {
		return effEndDate;
	}

	public void setEffEndDate(String effEndDate) {
		this.effEndDate = effEndDate;
	}



	public String getSuperSectionId() {
		return superSectionId;
	}

	public void setSuperSectionId(String superSectionId) {
		this.superSectionId = superSectionId;
	}

	@Override
	public String toString() {
		return "SuperSection [superSectionId=" + superSectionId + ", sectionId=" + sectionId + ", valueText=" + text + ", dataElementId=" + dataElementId + "]";
	}

	@Override
	public int compareTo(HpccChartForeignLanguage o) {
		return effBeginDate.compareTo(o.getEffBeginDate());
	}


	public String getChartId() {
		return chartId;
	}

	public void setChartId(String chartId) {
		this.chartId = chartId;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public String getDataElementId() {
		return dataElementId;
	}

	public void setDataElementId(String dataElementId) {
		this.dataElementId = dataElementId;
	}

	public String getChartTitlefrgn() {
		return chartTitlefrgn;
	}

	public void setChartTitlefrgn(String chartTitlefrgn) {
		this.chartTitlefrgn = chartTitlefrgn;
	}

	public List<HpccDynamicDataDetail> getDynamicDataDetail() {
		return dynamicDataDetail;
	}

	public void setDynamicDataDetail(List<HpccDynamicDataDetail> dynamicDataDetail) {
		this.dynamicDataDetail = dynamicDataDetail;
	}

}