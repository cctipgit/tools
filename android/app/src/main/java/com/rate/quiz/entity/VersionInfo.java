package com.rate.quiz.entity;

import java.io.Serializable;

/**
 * Version Info
 */
public class VersionInfo implements Serializable {

    /*** description */
    public String description;

    /*** downloadUrl */
    public String downloadUrl;

    /*** Whether to force update 0-NO 1-YES defaule-1 */
    public int forceUpdate;

    /*** system type 1 ios , 2 android */
    public String type;

    /*** versionName，如:1.2.6 */
    public String versionName;

    /*** versionCode,If the version number of the customer's mobile phone is less than the latest version number, it needs to be upgraded */
    public int versionCode;
}
