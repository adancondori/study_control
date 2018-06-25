package com.acc.study_control.utils

import android.content.Context
import android.widget.Toast
import com.orm.SchemaGenerator
import com.orm.SugarContext
import com.orm.SugarDb

object Utils {
    // General Utils Functions
    fun showToast(context: Context, message: String) =
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    fun cleanDataBase(context: Context) {
        SugarContext.terminate()
        val schemaGenerator = SchemaGenerator(context)
        schemaGenerator.deleteTables(SugarDb(context).db)
        SugarContext.init(context)
        schemaGenerator.createDatabase(SugarDb(context).db)
    }
}