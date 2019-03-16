package com.vtorshyn.voltdb.collector;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class GetVendorByMACAddr  extends VoltProcedure {
    // This is a limitation. SQLStmt should be in SP file
    private final SQLStmt mapVendorToMac = new SQLStmt("SELECT vendor FROM MACMAP " +
            "WHERE macprefix = ?");
    public VoltTable[] run(String macAddress) throws VoltProcedure.VoltAbortException {
        return new VendorMapper(this, mapVendorToMac).vendor(macAddress);
    }
}
