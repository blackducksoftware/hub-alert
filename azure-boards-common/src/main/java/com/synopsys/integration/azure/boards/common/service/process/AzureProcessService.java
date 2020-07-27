package com.synopsys.integration.azure.boards.common.service.process;

import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;
import com.synopsys.integration.azure.boards.common.http.AzureHttpService;
import com.synopsys.integration.azure.boards.common.http.HttpServiceException;
import com.synopsys.integration.azure.boards.common.model.AzureArrayResponseModel;
import com.synopsys.integration.azure.boards.common.util.AzureSpecTemplate;

/**
 * <a href="https://docs.microsoft.com/en-us/rest/api/azure/devops/processes/work%20item%20types/list?view=azure-devops-rest-5.1">Work Item Types</a>
 * <a href="https://docs.microsoft.com/en-us/rest/api/azure/devops/processes/fields/add?view=azure-devops-rest-5.1">Fields</a>
 */
public class AzureProcessService {
    public static final AzureSpecTemplate API_SPEC_ORGANIZATION_PROCESS_WORKITEMTYPES = new AzureSpecTemplate("/{organization}/_apis/work/processes/{processId}/workItemTypes");
    public static final AzureSpecTemplate API_SPEC_ORGANIZATION_PROCESS_WORKITEMTYPE_FIELDS = new AzureSpecTemplate("/{organization}/_apis/work/processes/{processId}/workItemTypes/{witRefName}/fields");

    private final AzureHttpService azureHttpService;

    public AzureProcessService(AzureHttpService azureHttpService) {
        this.azureHttpService = azureHttpService;
    }

    public AzureArrayResponseModel<ProcessWorkItemTypesResponseModel> getWorkItemTypes(String organization, String processId) throws HttpServiceException {
        String requestSpec = API_SPEC_ORGANIZATION_PROCESS_WORKITEMTYPES
                                 .defineReplacement("{organization}", organization)
                                 .defineReplacement("{processId}", processId)
                                 .populateSpec();
        requestSpec = appendApiVersionQueryParam(requestSpec);
        Type responseType = new TypeToken<AzureArrayResponseModel<ProcessWorkItemTypesResponseModel>>() {}.getType();
        return azureHttpService.get(requestSpec, responseType);
    }

    public ProcessFieldResponseModel addFieldToWorkItemType(String organization, String processId, String workItemTypeRefName, ProcessFieldRequestModel requestBody) throws IOException, HttpServiceException {
        String requestSpec = API_SPEC_ORGANIZATION_PROCESS_WORKITEMTYPE_FIELDS
                                 .defineReplacement("{organization}", organization)
                                 .defineReplacement("{processId}", processId)
                                 .defineReplacement("{witRefName}", workItemTypeRefName)
                                 .populateSpec();
        requestSpec = appendApiVersionQueryParam(requestSpec);
        return azureHttpService.post(requestSpec, requestBody, ProcessFieldResponseModel.class);
    }

    private String appendApiVersionQueryParam(String requestSpec) {
        return String.format("%s?%s=%s", requestSpec, AzureHttpService.AZURE_API_VERSION_QUERY_PARAM_NAME, "5.1-preview.2");
    }

}