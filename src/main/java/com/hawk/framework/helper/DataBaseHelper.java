package com.hawk.framework.helper;

import cn.hutool.core.convert.Convert;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 数据库助手
 *
 * @author Lion Li
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataBaseHelper {

//    private static final DynamicRoutingDataSource DS = SpringUtils.getBean(DynamicRoutingDataSource.class);

    /**
     * 获取当前数据库类型
     */
//    public static DataBaseType getDataBaseType() {
//        DataSource dataSource = DS.determineDataSource();
//        try (Connection conn = dataSource.getConnection()) {
//            DatabaseMetaData metaData = conn.getMetaData();
//            String databaseProductName = metaData.getDatabaseProductName();
//            return DataBaseType.find(databaseProductName);
//        } catch (SQLException e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }

    public static boolean isMySql() {
        return true;
    }

    public static boolean isOracle() {
        return false;
    }

    public static boolean isPostgerSql() {
        return false;
    }

    public static boolean isSqlServer() {
        return false;
    }

    public static String findInSet(Object var1, String var2) {
//        DataBaseType dataBasyType = getDataBaseType();
        String var = Convert.toStr(var1);
//        if (dataBasyType == DataBaseType.SQL_SERVER) {
//            // charindex(',100,' , ',0,100,101,') <> 0
//            return "charindex('," + var + ",' , ','+" + var2 + "+',') <> 0";
//        } else if (dataBasyType == DataBaseType.POSTGRE_SQL) {
//            // (select position(',100,' in ',0,100,101,')) <> 0
//            return "(select position('," + var + ",' in ','||" + var2 + "||',')) <> 0";
//        } else if (dataBasyType == DataBaseType.ORACLE) {
//            // instr(',0,100,101,' , ',100,') <> 0
//            return "instr(','||" + var2 + "||',' , '," + var + ",') <> 0";
//        }
        // find_in_set('100' , '0,100,101')
        return "find_in_set('" + var + "' , " + var2 + ") <> 0";
    }
}
