package com.hsgene.constant;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 14:48 2017/10/26
 * @Modified By:
 */
public class ConstantSymbol {

    public final static String KAFKA = "kafka";
    public final static String HBASE = "hbase";

    public static final String ACTION_TYPE_INSERT = "insert";
    public static final String ACTION_TYPE_DELETE = "delete";
    public static final String ACTION_TYPE_UPDATE = "update";
    public static final String ACTION_TYPE_DBCMD = "db_cmd";
    public static final String ACTION_TYPE_DBSTATEMENT = "db_statement";
    public static final String ACTION_TYPE_DROP = "drop";
    public static final String ACTION_TYPE_ALTER = "alter";
    public static final String ACTION_TYPE_RENAME = "rename";

    public static final String METADATA_BROKER_LIST = "metadata.broker.list";
    public static final String ZOOKEEPER_CONNECT = "zookeeper.connect";
    public static final String SERIALIZER_CLASS = "serializer.class";
    public static final String PARTITIONER_CLASS = "partitioner.class";
    public static final String REQUEST_REQUIRED_ACKS = "request.required.acks";
    public static final String NUM_PARTITIONS = "num.partitions";

    public static final String MONGODB = "mongodb";
    public static final String MYSQL = "mysql";

    public static final String COLUMNS = "columns";
    public static final String VALUES = "values";
    public static final String WHERECOLUMNS = "wherecolumns";
    public static final String WHEREVALUES = "wherevalues";

    public static final String ADDFIELDS = "addfields";
    public static final String OLDFIELDS = "oldfields";
    public static final String FIELDS = "fields";
    public static final String DROPFIELDS = "dropfields";
    public static final String RENAMEFIELDS = "renamefields";
    public static final String TSSPLIT = "-";

    public static final String PHOENIX_DRIVERCLASS = "org.apache.phoenix.jdbc.PhoenixDriver";
    public static final String PHOENIX_JDBC = "jdbc:phoenix:";

    public static final String MYSQL_DRIVERCLASS = "com.mysql.jdbc.Driver";
    public static final String MYSQL_JDBC = "jdbc:mysql://";

    public static final String MONGODB_ACTION_INSERT = "insert";
    public static final String MONGODB_ACTION_UPDATE = "update";
    public static final String MONGODB_ACTION_DELETE = "delete";

    public static final String CANAL_ZKSERVERS = "canal.zkServers";
    public static final String CANAL_HOST = "canal.host";
    public static final String CANAL_PORT = "canal.port";
    public static final String CANAL_DESTINATION = "canal.destination";

    public static final String NOT_SPLIT = "not.split";

    public static final String SCRAM_SHA_1 = "SCRAM-SHA-1";
    public static final String MONGODB_CR = "MONGODB-CR";

    public static final String TRANSFEREDTO = "patient.disease.transferedTo";

    public static final String URLINFOS_PROPERTIES = "conf/urlinfos.properties";
    public static final String OTHER_PROPERTIES = "conf/other.properties";

    public static final String SYNC_ALL = "sync_all";
    public static final String SYNC_ADD = "sync_add";
    public static final String SYNC_ALL_ADD = "sync_all_add";

    public static final String SPLIT = "_";

    public static final String SNAPSHOT_MYSQL_URL = "snapshot.mysql.url";

    public final static int SO_TIMEOUT = 28800000;

    public final static int RETRY_COUNT = 20;
    public final static int RETRY_SLEEP = 5000;

}
