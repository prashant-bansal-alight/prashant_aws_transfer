package com.alight.microservice.getplan.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "effectiveBeginDate",
    "effectiveEndDate",
    "chartData"
})
public class HpccChartDataEffectiveDate implements Comparable<HpccChartDataEffectiveDate>{

	@JsonProperty("effectiveBeginDate")
	private String effectiveBeginDate;
	@JsonProperty("effectiveEndDate")
	private String effectiveEndDate;
	@JsonProperty("chartAttributes")
	private List<HpccChartData> chartData;
	
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
	public List<HpccChartData> getChartData() {
		return chartData;
	}
	public void setChartData(List<HpccChartData> chartData) {
		this.chartData = chartData;
	}
	@Override
	public int compareTo(HpccChartDataEffectiveDate o) {
		return effectiveBeginDate.compareTo(o.getEffectiveBeginDate());
	}
	
	
}