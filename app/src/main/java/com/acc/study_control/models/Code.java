package com.acc.study_control.models;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by acondori on 20/05/2018.
 */

public class Code extends SugarRecord implements Serializable {
    public String id;
    public String code;
    public String url;
    public String district_id;
    public String chruch_id;
    public String group_id;

    public Code() {
    }
}
