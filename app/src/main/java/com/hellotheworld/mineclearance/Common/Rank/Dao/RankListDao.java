package com.hellotheworld.mineclearance.Common.Rank.Dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.hellotheworld.mineclearance.Common.Data.DatabaseHelper;
import com.hellotheworld.mineclearance.Common.Rank.Bean.RankBean;
import com.hellotheworld.mineclearance.Common.Rank.Bean.RankGradeEnum;
import com.hellotheworld.mineclearance.Utils.ConstUtil;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class RankListDao {
    private DatabaseHelper databaseHelper;

    public RankListDao(Context context) {
        databaseHelper = new DatabaseHelper(context, ConstUtil.DATABASE_HELPER_NAME, null, ConstUtil.DATABASE_VERSION);
    }


    private static final String SQL_FIND_RANK_LIST_BY_LEVEL = "select * from Rank where level = ? order by time asc limit ?";

    public List<RankBean> getRankListByLevel(RankGradeEnum level) {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor s = database.rawQuery(SQL_FIND_RANK_LIST_BY_LEVEL, new String[]{String.valueOf(level.ordinal()), ConstUtil.RANK_LIST_NUMBER_TO_SHOWN.toString()});

        List<RankBean> list = new ArrayList<RankBean>();
        if (s != null) {
            if (s.moveToFirst()) {
                do {
                    RankBean bean = new RankBean(
                            s.getString(s.getColumnIndex(DatabaseHelper.TABLE_RANK_COLUMN_NAME)),
                            s.getFloat(s.getColumnIndex(DatabaseHelper.TABLE_RANK_COLUMN_TIME)),
                            s.getString(s.getColumnIndex(DatabaseHelper.TABLE_RANK_COLUMN_SCREENSHOT)));
                    list.add(bean);
                } while (s.moveToNext());
            }
            s.close();
        }
        return list;
    }

    private static final String SQL_INSERT_RECORD_INTO_RANK_LIST = "insert into Rank (name,time,level,screenshot) values (?,?,?,?)";

    public void InsertWinRecord(String name, Float time, RankGradeEnum level, String screenShot) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        database.execSQL(SQL_INSERT_RECORD_INTO_RANK_LIST, new Object[]{name, time, level.ordinal(), screenShot});
    }
}
