package com.hsgene.elasticsearch.sync.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hsgene.elasticsearch.sync.domain.Cancer;
import com.hsgene.elasticsearch.sync.domain.Medicine;
import com.hsgene.elasticsearch.sync.domain.PoolNumAndEverySize;
import com.hsgene.elasticsearch.sync.domain.Product;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @description: 数据工具类
 * @projectName: sync_elasticsearch
 * @package: sync.util
 * @author: maodi
 * @createDate: 2018/12/3 16:31
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class StringUtil {

    private final static Logger LOGGER = LogManager.getLogger(StringUtil.class);

    /**
     * 线程核心数为:cpu核数 * 2 + 3 时效率最好
     */
    public final static int POOL_NUM = Runtime.getRuntime().availableProcessors() * 2 + 3;

    /**
     * 最大每次传输数量
     */
    public final static int MAX_EVERY_SIZE = (int) (Runtime.getRuntime().maxMemory() * 0.8) / 102400;

    /**
     * 路径分隔符
     */
    private final static String PATH_SPLIT = "/";

    /**
     * @param properties 配置文件名称
     * @return java.lang.String
     * @description 获取配置文件的绝对路径
     * @author maodi
     * @createDate 2018/12/4 16:17
     */
    public static String getAbsolutePath(String properties) {
        String path = StringUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String absolutePath;
        if (path.endsWith(PATH_SPLIT)) {
            absolutePath = path;
        } else {
            absolutePath = path.substring(0, path.lastIndexOf(PATH_SPLIT) + 1);
        }
        absolutePath += properties;
        return absolutePath;
    }

    /**
     * @param futures 线程期货
     * @return int
     * @description 获取总共数量
     * @author maodi
     * @createDate 2018/12/4 14:37
     */
    public static int getAllCount(List<Future<Integer>> futures) {
        int allCount = 0;
        for (Future<Integer> future : futures) {
            try {
                //累加每个线程的同步数据量
                allCount += future.get();
            } catch (InterruptedException e) {
                LOGGER.error("线程中断出错", e);
            } catch (ExecutionException e) {
                LOGGER.error("执行过程中出错", e);
            }
        }
        return allCount;
    }

    /**
     * @param json 源数据
     * @return java.lang.String
     * @description 将源数据改造为目标数据格式
     * @author maodi
     * @createDate 2018/12/4 14:38
     */
    public static String sourceJsonToTargetJson(String json) throws Exception {
        try {
            JSONObject sourceJson = JSONObject.parseObject(json);
            Product product = new Product();
            //套餐id
            String id = sourceJson.getString("id");
            if (StringUtils.isNotBlank(id)) {
                product.setId(id);
            }
            //套餐名称
            String name = sourceJson.getString("name");
            if (StringUtils.isNotBlank(name)) {
                product.setName(name);
            }
            //药物名称数组
            JSONArray medicineNameArray = sourceJson.getJSONArray("medicineNames");
            if (medicineNameArray != null) {
                Object[] medicineNames = medicineNameArray.toArray();
                if (medicineNames != null) {
                    int medicineLength = medicineNames.length;
                    if (medicineLength > 0) {
                        Medicine[] medicines = new Medicine[medicineLength];
                        int i = 0;
                        for (Object medicineName : medicineNames) {
                            Medicine medicine = new Medicine();
                            medicine.setMedicineName(medicineName.toString());
                            medicines[i++] = medicine;
                        }
                        product.setMedicines(medicines);
                    }
                }
            }
            //靶位
            String targets = sourceJson.getString("targets");
            if (StringUtils.isNotBlank(targets)) {
                product.setTargets(targets);
            }
            //基因检测内容
            String detection = sourceJson.getString("detection");
            if (StringUtils.isNotBlank(detection)) {
                product.setDetection(detection);
            }
            //临床诊断
            String clinical = sourceJson.getString("clinical");
            if (StringUtils.isNotBlank(clinical)) {
                product.setClinical(clinical);
            }
            //检测价格
            Float price = sourceJson.getFloat("price");
            if (price != null) {
                product.setPrice(price);
            }
            //佣金(积分)
            Integer commission = sourceJson.getInteger("commission");
            if (commission != null) {
                product.setCommission(commission);
            }
            //疾病id数组
            JSONArray cancerIdArray = sourceJson.getJSONArray("cancerIds");
            int cancerIdsLength = 0;
            if (cancerIdArray != null && cancerIdArray.size() > 0) {
                Object[] cancerIds = cancerIdArray.toArray();
                if (cancerIds != null) {
                    cancerIdsLength = cancerIds.length;
                    if (cancerIdsLength > 0) {
                        Cancer[] cancers = new Cancer[cancerIdsLength];
                        int i = 0;
                        for (Object cancerId : cancerIds) {
                            Cancer cancer = new Cancer();
                            cancer.setId(cancerId.toString());
                            cancers[i++] = cancer;
                        }
                        product.setCancers(cancers);
                    }
                }
            }
            //疾病名称数组
            JSONArray cancerNameArray = sourceJson.getJSONArray("cancerNames");
            if (cancerNameArray != null && cancerNameArray.size() > 0) {
                Object[] cancerNames = cancerNameArray.toArray();
                if (cancerNames != null) {
                    int cancerNamesLength = cancerNames.length;
                    if (cancerNamesLength > 0) {
                        Cancer[] oldCancers = product.getCancers();
                        int count = cancerIdsLength;
                        if (cancerIdsLength < cancerNamesLength) {
                            count = cancerNamesLength;
                        }
                        Cancer[] newCancers = new Cancer[count];
                        for (int i = 0; i < count; i++) {
                            Cancer cancer = new Cancer();
                            if (i + 1 <= cancerIdsLength) {
                                cancer.setId(oldCancers[i].getId());
                            }
                            if (i + 1 <= cancerNamesLength) {
                                cancer.setName(cancerNames[i].toString());
                            }
                            newCancers[i] = cancer;
                        }
                        product.setCancers(newCancers);
                    }
                }
            }
            //药物性质(字典)(治疗类型)(检测类型)
            String nature = sourceJson.getString("nature");
            if (StringUtils.isNotBlank(nature)) {
                product.setNature(nature);
            }
            //产品描述
            String description = sourceJson.getString("description");
            if (StringUtils.isNotBlank(description)) {
                product.setDescription(description);
            }
            //检测机构(英文逗号拼接)
            String orgs = sourceJson.getString("orgs");
            if (StringUtils.isNotBlank(orgs)) {
                product.setOrgs(orgs);
            }
            //使用计数
            Integer usageCount = sourceJson.getInteger("usageCount");
            if (usageCount != null) {
                product.setUsageCount(usageCount);
            }
            //数量
            Integer number = sourceJson.getInteger("number");
            if (number != null) {
                product.setNumber(number);
            }
            //设置修改时间
            product.setUpdateDateTime(new Date());
            return JSONObject.toJSONString(product);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @param resultSet
     * @return java.lang.String
     * @description 将mysql数据转换为elasticsearch的json数据
     * @author maodi
     * @createDate 2018/12/6 13:41
     */
    public static String mysqlDataToJson(ResultSet resultSet) throws Exception {
        try {
            Product product = new Product();
            //套餐id
            String id = resultSet.getString("id");
            if (id != null) {
                product.setId(id.toString());
            }
            //套餐名称
            String name = resultSet.getString("name");
            if (StringUtils.isNotBlank(name)) {
                product.setName(name);
            }
            //药物数组
            Medicine[] medicines = new Medicine[1];
            Medicine medicine = new Medicine();
            //药物id
            String medicineId = resultSet.getString("medicineId");
            if (StringUtils.isNotBlank(medicineId)) {
                medicine.setMedicineId(medicineId);
            }
            //药物名称
            String medicineName = resultSet.getString("medicineName");
            if (StringUtils.isNotBlank(medicineName)) {
                medicine.setMedicineName(medicineName);
            }
            if (medicine.getMedicineId() != null && medicine.getMedicineName() != null) {
                medicines[0] = medicine;
            }
            if (medicines[0] != null && medicines.length > 0) {
                product.setMedicines(medicines);
            }
            //靶位
            String targets = resultSet.getString("targets");
            if (StringUtils.isNotBlank(targets)) {
                product.setTargets(targets);
            }
            //基因检测内容
            String detection = resultSet.getString("detection");
            if (StringUtils.isNotBlank(detection)) {
                product.setDetection(detection);
            }
            //临床诊断
            String clinical = resultSet.getString("clinical");
            if (StringUtils.isNotBlank(clinical)) {
                product.setClinical(clinical);
            }
            //检测价格
            Float price = resultSet.getFloat("price");
            if (price != null) {
                product.setPrice(price);
            }
            //佣金(积分)
            Integer commission = resultSet.getInt("commission");
            if (commission != null) {
                product.setCommission(commission);
            }
            //疾病数组
            Cancer[] cancers = new Cancer[1];
            Cancer cancer = new Cancer();
            //疾病id
            String cancerId = resultSet.getString("cancerId");
            if (StringUtils.isNotBlank(cancerId)) {
                cancer.setId(cancerId);
            }
            //疾病名称
            String cancerName = resultSet.getString("cancerName");
            if (StringUtils.isNotBlank(cancerName)) {
                cancer.setName(cancerName);
            }
            if (cancer.getId() != null && cancer.getName() != null) {
                cancers[0] = cancer;
            }
            if (cancers[0] != null && cancers.length > 0) {
                product.setCancers(cancers);
            }
            //药物性质(字典)(治疗类型)(检测类型)
            String nature = resultSet.getString("nature");
            if (StringUtils.isNotBlank(nature)) {
                product.setNature(nature);
            }
            //产品描述
            String description = resultSet.getString("description");
            if (StringUtils.isNotBlank(description)) {
                product.setDescription(description);
            }
            //检测机构(英文逗号拼接)
            String orgs = resultSet.getString("orgs");
            if (StringUtils.isNotBlank(orgs)) {
                product.setOrgs(orgs);
            }
            //使用计数
            Integer usageCount = resultSet.getInt("usageCount");
            if (usageCount != null) {
                product.setUsageCount(usageCount);
            }
            //数量
            Integer number = resultSet.getInt("number");
            if (number != null) {
                product.setNumber(number);
            }
            //设置修改时间
            product.setUpdateDateTime(new Date());
            //检测周期
            String testPeriod = resultSet.getString("testPeriod");
            if (StringUtils.isNotBlank(testPeriod)) {
                product.setTestPeriod(testPeriod);
            }
            //积分(积分即佣金)
            Integer integration = resultSet.getInt("integration");
            if (integration != null) {
                product.setIntegration(integration);
            }
            //政策类型（0政策内，1政策外）
            Integer policyType = resultSet.getInt("policyType");
            if (policyType != null) {
                product.setPolicyType(policyType);
            }
            //是否被使用（0未使用，1已使用）
            Integer isUsed = resultSet.getInt("isUsed");
            if (isUsed != null) {
                product.setIsUsed(isUsed);
            }
            return JSONObject.toJSONString(product);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @param ip ip地址
     * @return boolean
     * @description 校验ip是否合法
     * @author maodi
     * @createDate 2018/12/6 12:05
     */
    public static boolean ipCheck(String ip) {
        if (StringUtils.isNotBlank(ip)) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." +
                           "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                           "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                           "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            if (ip.matches(regex)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * @param port 端口
     * @return boolean
     * @description 校验端口是否合法
     * @author maodi
     * @createDate 2018/12/6 12:07
     */
    public static boolean portCheck(int port) {
        //端口为1-65535之间
        if (port >= 1 && port <= 65535) {
            return true;
        }
        return false;
    }

    /**
     * @param totalCount
     * @return com.hsgene.elasticsearch.sync.domain.PoolNumAndEverySize
     * @description 计算拆分部分的数量及每部分的数量
     * @author maodi
     * @createDate 2018/12/6 14:10
     */
    public static PoolNumAndEverySize calcPoolNumAndEverySize(int totalCount) {
        int everySize = totalCount / (StringUtil.POOL_NUM - 1);
        int poolNum;
        //判断是否大于最大每次数量
        if (everySize > StringUtil.MAX_EVERY_SIZE) {
            everySize = StringUtil.MAX_EVERY_SIZE;
            int remainder = totalCount % everySize;
            poolNum = totalCount / everySize;
            //如果余数不等于0，则拆分的部分数+1
            if (remainder != 0) {
                poolNum = poolNum + 1;
            }
        } else {
            everySize = totalCount;
            poolNum = 1;
        }
        PoolNumAndEverySize poolNumAndEverySize = new PoolNumAndEverySize();
        poolNumAndEverySize.setPoolNum(poolNum);
        poolNumAndEverySize.setEverySize(everySize);
        return poolNumAndEverySize;
    }

    public static void main(String[] args) {
        String json = "{\n" +
                      "        \"id\": 22,\n" +
                      "        \"name\": \"套餐\",\n" +
                      "        \"medicineNames\": [\n" +
                      "            \"雷莫芦单抗\",\n" +
                      "            \"阿帕替尼\",\n" +
                      "            \"索拉非尼\",\n" +
                      "            \"贝伐珠单抗\"\n" +
                      "        ],\n" +
                      "        \"targets\": \"VEGFR\",\n" +
                      "        \"detection\": \"VEGFR基因表达分析\",\n" +
                      "        \"clinical\": \"VEGFR表达水平高的患者对贝伐单抗药物敏感，VEGFR表达水平低的患者敏感性降低。\",\n" +
                      "        \"price\": 600,\n" +
                      "        \"commission\": 120,\n" +
                      "        \"cancerIds\": [\n" +
                      "            \"4c4ddfa8-8dcf-11e5-99f6-3c970ecb1396\",\n" +
                      "            \"4c4de75b-8dcf-11e5-99f6-3c970ecb1396\",\n" +
                      "            \"4c4e04b9-8dcf-11e5-99f6-3c970ecb1396\",\n" +
                      "            \"4c4dee3d-8dcf-11e5-99f6-3c970ecb1396\",\n" +
                      "            \"4c4df3d1-8dcf-11e5-99f6-3c970ecb1396\",\n" +
                      "            \"4c4ddd66-8dcf-11e5-99f6-3c970ecb1396\"\n" +
                      "        ],\n" +
                      "        \"cancerNames\": [\n" +
                      "            \"非小细胞肺癌\",\n" +
                      "            \"大肠癌(结直肠癌)\",\n" +
                      "            \"卵巢癌\",\n" +
                      "            \"胆管癌\",\n" +
                      "            \"肾癌\",\n" +
                      "            \"乳腺癌\"\n" +
                      "        ],\n" +
                      "        \"nature\": \"1\",\n" +
                      "        \"description\": \"\",\n" +
                      "        \"orgs\": \"宝藤,金域\",\n" +
                      "        \"usageCount\": 1,\n" +
                      "        \"number\": 0\n" +
                      "    }";
    }
}
