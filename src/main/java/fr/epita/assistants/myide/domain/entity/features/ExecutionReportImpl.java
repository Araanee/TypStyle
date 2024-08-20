package fr.epita.assistants.myide.domain.entity.features;

import com.sun.net.httpserver.Authenticator;

import fr.epita.assistants.myide.domain.entity.Feature.ExecutionReport;

public class ExecutionReportImpl implements ExecutionReport {

    private final boolean success;
    public ExecutionReportImpl(boolean Success)
    {
        this.success = Success;
    }
    @Override
    public boolean isSuccess() {
        return success;
    }

}
