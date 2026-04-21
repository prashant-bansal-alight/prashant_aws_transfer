package com.alight.microservice.getplan.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "filesDisplayText",
    "filesName",
    "filesSize",
    "filesInventoryManagementId"
})
public class HpccFile {

    @JsonProperty("filesDisplayText")
    private List<HpccChartForeignLanguage> filesDisplayTextBlue;
    
    @JsonProperty("filesName")
    private List<HpccChartForeignLanguage> filesName;
    
    @JsonProperty("filesSize")
    private String filesSize;
    @JsonProperty("filesInventoryManagementId")
    private String filesInventoryManagementId;
    
	
	public List<HpccChartForeignLanguage> getFilesDisplayTextBlue() {
		return filesDisplayTextBlue;
	}
	public void setFilesDisplayTextBlue(List<HpccChartForeignLanguage> filesDisplayTextBlue) {
		this.filesDisplayTextBlue = filesDisplayTextBlue;
	}
	public List<HpccChartForeignLanguage> getFilesName() {
		return filesName;
	}
	public void setFilesName(List<HpccChartForeignLanguage> filesName) {
		this.filesName = filesName;
	}
	public String getFilesSize() {
		return filesSize;
	}
	public void setFilesSize(String filesSize) {
		this.filesSize = filesSize;
	}
	public String getFilesInventoryManagementId() {
		return filesInventoryManagementId;
	}
	public void setFilesInventoryManagementId(String filesInventoryManagementId) {
		this.filesInventoryManagementId = filesInventoryManagementId;
	}
	@Override
	public String toString() {
		return "File [filesDisplayTextBlue=" + filesDisplayTextBlue + ", filesName=" + filesName + ", filesSize="
				+ filesSize + ", filesInventoryManagementId=" + filesInventoryManagementId + "]";
	}

}