package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Reference : http://blog.naver.com/PostView.nhn?blogId=hee072794&logNo=220619425456
 */

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
        //컨텍스트, db의 이름, 커서(널값으로 넣어도 무방), db의 버전
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db 생성시 한번만 실행 id 값은 행을 추가하면 자동으로 증가하는 프라이머리 키, 테이블 이름은 SQLITE임
        //그러나 db가 아닌 테이블을 삭제하고 다시 생성하여 사용할 경우엔 별 의미 없음
        System.out.println("db 생성");
        //db.execSQL("CREATE TABLE SQLITE (id INTEGER PRIMARY KEY AUTOINCREMENT, cheonan_day_rev TEXT, terminal_day_rev TEXT, asan_day_rev TEXT, cheonan_fri_rev TEXT, asan_fri_rev TEXT, terminal_fri_rev TEXT, cheonan_sat_rev TEXT, asan_sat_rev TEXT, terminal_sat_rev TEXT, cheonan_sun_rev TEXT, asan_sun_rev TEXT, terminal_sun_rev TEXT, terminal_day TEXT, asan_day TEXT, cheonan_day TEXT, terminal_fri TEXT, asan_fri TEXT, cheonan_fri TEXT, cheonan_asan_sat TEXT, terminal_sat TEXT, cheonan_asan_sun TEXT, terminal_sun TEXT);");
    }

    public void insert(String time) {
        //db에 한 행씩 넣음. 순서는 프라이머리키, time(넣을 값), 나머지 행의 칸들. 이때 만들어진 테이블의 행의 갯수과 SQL문에서 추가하려는 행의 갯수가 다르면 오류가 발생함
        System.out.println("테이블 인서트");
        SQLiteDatabase db = getWritableDatabase();
        if (time == "end") {
            //가장 마지막에 null값을 삽입하기 위해 "end"를 받았을 때 모든 행에 null값을 추가
            db.execSQL("INSERT INTO SQLITE VALUES(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);");
        } else {
            //추가하려는 값을 가장 처음 행으로, 나머지 모든 행이 null인 행을 추가. 여기서 가장 처음 값은 primary key로 자동으로 숫자가 증가하는 primary key autoincrement이므로 신경쓰지 않고 갯수만 일치하면 된다
            db.execSQL("INSERT INTO SQLITE VALUES(null,'" + time + "',null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);");
        }
        db.close();
    }

    public void update(String time, Integer id, String column) {
        //time 값을 column 행의 id 열 위치로 넣음(기존 값을 갱신시킴)
        System.out.println("테이블 업데이트");
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE SQLITE SET " + column + "='" + time + "' WHERE id=" + id + ";");
        db.close();
    }

    public String read(int id, int column) {
        //column 행의 id 열 위치의 값을 리턴함 만약 값이 null일 경우엔 "end"를 반환
        Cursor cursor = null;
        try {
            System.out.println("테이블 읽기");
            SQLiteDatabase db = getReadableDatabase();
            String result = "";
            cursor = db.rawQuery("SELECT * FROM SQLITE WHERE id=" + id + "", null);
            cursor.moveToFirst();
            System.out.println(cursor.getString(0));
            if (cursor.getString(column) == null) {
                return "end";
            } else {
                result += cursor.getString(column);
                return result;
            }
        } finally {
            cursor.close();
        }
    }

    public void createtable() {
        //db의 테이블을 만듬 _id 값은 자동으로 증가하는 프라이머리 키 이며 그 다음부터 있는 것들은 모두 행임
        System.out.println("크리에이트 테이블");
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("CREATE TABLE SQLITE (id INTEGER PRIMARY KEY AUTOINCREMENT, cheonan_day_rev TEXT, terminal_day_rev TEXT, asan_day_rev TEXT, cheonan_fri_rev TEXT, asan_fri_rev TEXT, terminal_fri_rev TEXT, cheonan_sat_rev TEXT, asan_sat_rev TEXT, terminal_sat_rev TEXT, cheonan_sun_rev TEXT, asan_sun_rev TEXT, terminal_sun_rev TEXT, terminal_day TEXT, asan_day TEXT, cheonan_day TEXT, terminal_fri TEXT, asan_fri TEXT, cheonan_fri TEXT, cheonan_asan_sat TEXT, terminal_sat TEXT, cheonan_asan_sun TEXT, terminal_sun TEXT);");
    }

    public boolean table_exists(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name = 'SQLITE'",null);
        cursor.moveToFirst();
        if(cursor.getCount()>0){
            return true;
        }else{
            return false;
        }
    }

    public void droptable() {
        //db의 테이블이 존재하면 삭제시킴
        System.out.println("드랍 테이블");
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS SQLITE;");
        db.close();
    }

    public void delete(String item) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM SQLITE WHERE item='" + item + "';");
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}