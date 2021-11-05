package com.amg.framework.boot.utils.lbs;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;

/**
 * @author zhengzhouyang
 * @date 2020/8/24 16:09
 * @describe 根据地理位置信息计算距离
 */
public class DistanceCalcs {
    /**
     * 计算两个坐标的距离
     * @param startPoint 原点
     * @param endPoint 计算位置
     * @return 返回double  单位 米
     */
    public static Double getDistance(Coordinate startPoint, Coordinate endPoint) {
        GlobalCoordinates source = new GlobalCoordinates(startPoint.getLat(), startPoint.getLng());
        GlobalCoordinates target = new GlobalCoordinates(endPoint.getLat(), endPoint.getLng());
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.Sphere,source, target);
        return geoCurve.getEllipsoidalDistance();
    }
}
