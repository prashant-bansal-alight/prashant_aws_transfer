package com.alight.microservice.getplan.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "dataElementsDynamicId", 
	                "dataElementsDynamicDisplayText", 
					"dataElementsDynamicDefaultText" })
public class HpccDynamicDataDetail {

	@JsonProperty("dataElementsDynamicId")
	private String dataElementsDynamicId;

	@JsonProperty("dataElementsDynamicDisplayText")
	private List<HpccChartForeignLanguage> dataElementsDynmDsplTx;

	@JsonProperty("dataElementsDynamicDefaultText")
	private List<HpccChartForeignLanguage> dataElementsDynmDefltDsplTx;

	public List<HpccChartForeignLanguage> getDataElementsDynmDsplTx() {
		return dataElementsDynmDsplTx;
	}

	public void setDataElementsDynmDsplTx(List<HpccChartForeignLanguage> dataElementsDynmDsplTx) {
		this.dataElementsDynmDsplTx = dataElementsDynmDsplTx;
	}

	public List<HpccChartForeignLanguage> getDataElementsDynmDefltDsplTx() {
		return dataElementsDynmDefltDsplTx;
	}

	public void setDataElementsDynmDefltDsplTx(List<HpccChartForeignLanguage> dataElementsDynmDefltDsplTx) {
		this.dataElementsDynmDefltDsplTx = dataElementsDynmDefltDsplTx;
	}

	public String getDataElementsDynamicId() {
		return dataElementsDynamicId;
	}

	public void setDataElementsDynamicId(String dataElementsDynamicId) {
		this.dataElementsDynamicId = dataElementsDynamicId;
	}
	
}