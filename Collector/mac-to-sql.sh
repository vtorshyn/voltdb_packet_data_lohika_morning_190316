#!/bin/bash
if [ ! -f oui.txt ]; then
    wget http://standards-oui.ieee.org/oui.txt
fi
in=oui.txt
echo "CREATE TABLE MACMAP( \
    MACPREFIX VARCHAR(6) NOT NULL, \
    VENDOR VARCHAR(100) NOT NULL, \
    PRIMARY KEY (MACPREFIX) \
);
PARTITION TABLE MACMAP ON COLUMN MACPREFIX;
CREATE PROCEDURE
    PARTITION ON TABLE MACMAP COLUMN MACPREFIX 
    FROM CLASS com.vtorshyn.voltdb.collector.GetVendorByMACAddr;"

mapsql() {
    mac=$1
    shift
    vendor=$(echo -ne $* | tr -d "\r")
    echo "INSERT INTO MACMAP VALUES ('$mac', '$vendor');"
}

cat ${in} | grep 'base 16' | awk '{$2=""; $3=""; print}' | uniq -u | sed "s/'//g" | ( while read L; do mapsql $L; done )
