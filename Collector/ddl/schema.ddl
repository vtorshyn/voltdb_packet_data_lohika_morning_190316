-- Create a client table
create table Clients(
    client_ip VARCHAR(15) not null,
    port INTEGER not null,
    mac VARCHAR(50),
    total BIGINT,
    PRIMARY KEY (client_ip, port)
);

create table IPTraffic(
    client_ip VARCHAR(15) not null,
    server_ip VARCHAR(15) not null,
    port INTEGER not null,
    protocol VARCHAR(4) not null,
    total BIGINT,
    PRIMARY KEY(client_ip, server_ip, port, protocol)
);

PARTITION TABLE Clients ON COLUMN client_ip;
PARTITION TABLE IPTraffic ON COLUMN client_ip;
load classes /home/vt/IdeaProjects/Collector/collector-sp.jar;
CREATE PROCEDURE 
    PARTITION ON TABLE Clients COLUMN client_ip
    FROM CLASS com.vtorshyn.voltdb.collector.LogPacketData;
