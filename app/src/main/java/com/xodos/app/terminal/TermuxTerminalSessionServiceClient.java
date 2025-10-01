package com.xodos.app.terminal;

import android.app.Service;

import androidx.annotation.NonNull;

import com.xodos.app.xodosService;
import com.xodos.shared.xodos.shell.command.runner.terminal.xodosSession;
import com.xodos.shared.xodos.terminal.xodosTerminalSessionClientBase;
import com.xodos.terminal.TerminalSession;
import com.xodos.terminal.TerminalSessionClient;

/** The {@link TerminalSessionClient} implementation that may require a {@link Service} for its interface methods. */
public class xodosTerminalSessionServiceClient extends xodosTerminalSessionClientBase {

    private static final String LOG_TAG = "xodosTerminalSessionServiceClient";

    private final xodosService mService;

    public xodosTerminalSessionServiceClient(xodosService service) {
        this.mService = service;
    }

    @Override
    public void setTerminalShellPid(@NonNull TerminalSession terminalSession, int pid) {
        xodosSession xodosSession = mService.getxodosSessionForTerminalSession(terminalSession);
        if (xodosSession != null)
            xodosSession.getExecutionCommand().mPid = pid;
    }

}
