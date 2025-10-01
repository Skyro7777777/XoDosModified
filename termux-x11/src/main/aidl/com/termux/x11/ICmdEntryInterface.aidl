package com.xodos.x11;

// This interface is used by utility on xodos side.
interface ICmdEntryInterface {
    ParcelFileDescriptor getXConnection();
    ParcelFileDescriptor getLogcatOutput();
}
