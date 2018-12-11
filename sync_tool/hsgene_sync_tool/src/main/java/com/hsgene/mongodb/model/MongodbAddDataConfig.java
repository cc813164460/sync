package com.hsgene.mongodb.model;

import com.hsgene.model.AddDataConfig;
import org.bson.BsonTimestamp;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 15:19 2017/10/23
 * @Modified By:
 */
public class MongodbAddDataConfig extends AddDataConfig {
    private BsonTimestamp ts;

    public BsonTimestamp getTs() {
        return ts;
    }

    public void setTs(BsonTimestamp ts) {
        this.ts = ts;
    }

    @Override
    public String toString() {
        return "MongodbAddDataConfig{" +
               "canalZkServers='" + canalZkServers + '\'' +
               "ts=" + ts +
               ", canalDestination='" + canalDestination + '\'' +
               ", canalHost='" + canalHost + '\'' +
               ", canalPort='" + canalPort + '\'' +
               '}';
    }
}
