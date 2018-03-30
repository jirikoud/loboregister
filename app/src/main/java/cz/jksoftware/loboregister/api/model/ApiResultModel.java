package cz.jksoftware.loboregister.api.model;

import cz.jksoftware.loboregister.api.LoadStatus;

/**
 * Created by Koudy on 3/30/2018.
 * Base response model
 */

public class ApiResultModel {

    public LoadStatus loadStatus;
    public ErrorModel errorModel;

    public ApiResultModel(LoadStatus loadStatus, ErrorModel errorModel) {
        this.loadStatus = loadStatus;
        this.errorModel = errorModel;
    }

}
