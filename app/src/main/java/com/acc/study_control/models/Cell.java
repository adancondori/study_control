/*
 * Copyright (c) 2018. Evren Coşkun
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.acc.study_control.models;

import com.evrencoskun.tableview.filter.IFilterableModel;
import com.evrencoskun.tableview.sort.ISortableModel;

/**
 * Created by evrencoskun on 11/06/2017.
 */

public class Cell implements ISortableModel, IFilterableModel {

    private String mId;
    private Object mData;
    private String mFilterKeyword;

    public Cell(String id) {
        this.mId = id;
    }

    public Cell(String id, Object data) {
        this.mId = id;
        this.mData = data;
        this.mFilterKeyword = String.valueOf(data);
    }

    public Cell(String id, Object data, String filter) {
        this.mId = id;
        this.mData = data;
        this.mFilterKeyword = String.valueOf(filter);
    }

    /**
     * This is necessary for sorting process.
     * See ISortableModel
     */
    @Override
    public String getId() {
        return mId;
    }

    /**
     * This is necessary for sorting process.
     * See ISortableModel
     */
    @Override
    public Object getContent() {
        return mData;
    }


    public Object getData() {
        return mData;
    }

    public Object getData(int column) {
        String cad = "empty";
        switch (column) {
            case 0:
                cad = ((Member) getData()).name;
                break;
            case 1:
                cad = ((Member) getData()).is_one ? "si" : "no";
                break;
            case 2:
                cad = ((Member) getData()).is_two ? "si" : "no";
                break;
            case 3:
                cad = ((Member) getData()).is_three ? "si" : "no";
                break;
            case 4:
                cad = ((Member) getData()).is_four ? "si" : "no";
                break;
            case 5:
                cad = ((Member) getData()).is_five ? "si" : "no";
                break;
            case 6:
                cad = ((Member) getData()).is_six ? "si" : "no";
                break;
            case 7:
                cad = ((Member) getData()).is_seven ? "si" : "no";
                break;
            case 8:
                cad = ((Member) getData()).is_eight ? "si" : "no";
                break;
            case 9:
                cad = ((Member) getData()).is_nine ? "si" : "no";
                break;
            case 10:
                cad = ((Member) getData()).is_ten ? "si" : "no";
                break;
            case 11:
                cad = ((Member) getData()).is_eleven ? "si" : "no";
                break;
            case 12:
                cad = ((Member) getData()).is_twelve ? "si" : "no";
                break;
            case 13:
                cad = ((Member) getData()).is_thirteen ? "si" : "no";
                break;
            case 14:
                cad = ((Member) getData()).is_fourteen ? "si" : "no";
                break;
            case 15:
                cad = ((Member) getData()).is_fifteen ? "si" : "no";
                break;
            case 16:
                cad = ((Member) getData()).is_sixteen ? "si" : "no";
                break;
            case 17:
                cad = ((Member) getData()).is_seventeen ? "si" : "no";
                break;
            case 18:
                cad = ((Member) getData()).is_eigthten ? "si" : "no";
                break;
            case 19:
                cad = ((Member) getData()).is_nineteen ? "si" : "no";
                break;
            case 20:
                cad = ((Member) getData()).is_twenty ? "si" : "no";
                break;

        }

        return cad;
    }

    public void setData(String data) {
        mData = data;
    }

    public String getFilterKeyword() {
        return mFilterKeyword;
    }

    public void setFilterKeyword(String filterKeyword) {
        this.mFilterKeyword = filterKeyword;
    }

    @Override
    public String getFilterableKeyword() {
        return mFilterKeyword;
    }
}

