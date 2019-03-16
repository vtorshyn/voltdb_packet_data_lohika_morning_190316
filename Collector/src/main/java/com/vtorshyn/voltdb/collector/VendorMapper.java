package com.vtorshyn.voltdb.collector;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class VendorMapper  {

    private VoltProcedure proc;
    private final SQLStmt mapVendorToMac;

    public VendorMapper(VoltProcedure executor, SQLStmt mapVendorToMac) {
        proc = executor;
        this.mapVendorToMac = mapVendorToMac;
    }

    public VoltTable[] vendor(String macAddress) {
        final String macPrefix = macAddress.substring(0, 6).toUpperCase();
        proc.voltQueueSQL(mapVendorToMac, macPrefix.toUpperCase());
        return proc.voltExecuteSQL();
    }
}
