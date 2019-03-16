package com.vtorshyn.voltdb.collector;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;
// CLIENTS
// VARCHAR(IP) String(MAC) BIGINT(TOTAL)
// SERVERS
// INT(ID) VARCHAR(IP) BIGINT(TOTAL)
//
// SMALLINT(clientIp) INT(SERVER_ID) SMALLINT(port) TINYINT(PROTO) BIGINT(TOTAL)

public class LogPacketData extends VoltProcedure {
    private final SQLStmt getClient = new SQLStmt("SELECT client_ip, total FROM CLIENTS "+
            "WHERE client_ip = ? and port = ?");
    private final SQLStmt createClient = new SQLStmt("INSERT into CLIENTS " +
            "VALUES (?, ?, ?, ?)");
    private final SQLStmt updateClient = new SQLStmt("UPDATE CLIENTS " +
            "SET total = ? " +
            "WHERE client_ip = ? and port = ?");
    private final SQLStmt getIPTrafficRow = new SQLStmt("SELECT total FROM IPTraffic " +
            "WHERE client_ip = ? and server_ip = ? and port = ? and protocol = ?");

    private final SQLStmt createIPTrafficRow = new SQLStmt("INSERT into IPTraffic " +
            "VALUES (?, ?, ?, ?, ?)");

    private final SQLStmt updateIPTrafficTotal = new SQLStmt("UPDATE IPTraffic " +
            "SET total = ? " +
            "WHERE client_ip = ? and server_ip = ? and port = ? and protocol = ?");

    protected void createClientRow(String clientIp, String macAddress, int port, int size) {
        long totalSizeByClient = size;
        // Updating clients only stats first
        voltQueueSQL(getClient, clientIp, port);
        VoltTable res[] = voltExecuteSQL();
        if (res[0].getRowCount() < 1) {
            // Hm, no such client yet. Let's create his data
            voltQueueSQL(createClient, clientIp, port, macAddress, size);
            voltExecuteSQL(); // We don't really care about result :D ... YET
        } else {
            VoltTable clientData = res[0];
            clientData.advanceRow();
            totalSizeByClient = clientData.getLong(1) + size; // First column IS BIGINT see getClient query
            voltQueueSQL(updateClient, totalSizeByClient, clientIp, port);
            voltExecuteSQL(); // Again, we don't care. Exception will be thrown if something went wrong
        }
    }
    /**
     *
     * Collects stats and creates records.
     * Please note PK in DDL and here
     * @param clientIp - Is a primary key
     * @throws VoltAbortException
     */
    public VoltTable[] run(String clientIp, String remoteIp, String proto, String macAddress, int port, int size) throws VoltAbortException {
        long totalSizeByServerAndClient = size;

        createClientRow(clientIp, macAddress.toUpperCase(), port, size);

        // Updating overall statistics
        voltQueueSQL(getIPTrafficRow, clientIp, remoteIp, port, proto);
        VoltTable res[] = voltExecuteSQL();
        if (res[0].getRowCount() < 1) { // No record yet
            voltQueueSQL(createIPTrafficRow, clientIp, remoteIp, port, proto, size);
        } else {
            VoltTable ipTrafficData = res[0];
            ipTrafficData.advanceRow();
            totalSizeByServerAndClient = ipTrafficData.getLong(0) + size;
            voltQueueSQL(updateIPTrafficTotal, totalSizeByServerAndClient, clientIp, remoteIp, port, proto);
        }

        return voltExecuteSQL(true);
    }
}
