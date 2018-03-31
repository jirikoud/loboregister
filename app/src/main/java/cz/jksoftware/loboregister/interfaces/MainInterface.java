package cz.jksoftware.loboregister.interfaces;

import cz.jksoftware.loboregister.api.model.ReportModel;

/**
 * Created by Koudy on 3/30/2018.
 * Main activity actions
 */

public interface MainInterface {

    void onReportSelected(ReportModel reportModel);
    void onAuthorSelected(String authorId);

}
