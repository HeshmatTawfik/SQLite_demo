package com.heshmat.sqllitedemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class DatabaseAdapter {
    private Context context;
    private SQLiteDatabase sqLiteDatabase;
    private static DatabaseAdapter databaseAdapter;
    private static final String COLUMN_LINKS_ID = "id";
    private static final String COLUMN_LINKS_NAME = "name";
    private static final String COLUMN_LINKS_URL = "url";
    private static final String COLUMN_LINKS_COMMENT = "comment";
    private static final String TABLE_LINKS = "links";
    private static int version = 1;
    private static final String CREATE_TABLE_LIST = String.format("CREATE TABLE  %s(%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,%s VARCHAR,%s VARCHAR,%s VARCHAR)", TABLE_LINKS, COLUMN_LINKS_ID, COLUMN_LINKS_NAME, COLUMN_LINKS_COMMENT, COLUMN_LINKS_URL);
    public static final String DB_NAME = "LINKS";
    LinksAdapter linksAdapter;

    private DatabaseAdapter(Context context, LinksAdapter adapter) {
        if (sqLiteDatabase == null) {
            this.context = context;
            sqLiteDatabase = new DatabaseAdapterHelper(context, DB_NAME, version).getReadableDatabase();
            this.linksAdapter = adapter;
        }

    }

    public boolean insert(LinkModel linkModel) {
        ContentValues values = new ContentValues();
        values.put("name", linkModel.getName());
        values.put("url", linkModel.getUrl());
        values.put("comment", linkModel.getComment());
        long id = sqLiteDatabase.insert(TABLE_LINKS, null, values);
        if (id > -1) {
            linkModel.setId(id);
            linksAdapter.getLinksList().add(0, linkModel);
            linksAdapter.notifyItemInserted(0);

        }
        return id > -1;
    }

    public boolean delete(long id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LINKS_ID, id);
        boolean isDeleted = sqLiteDatabase.delete(TABLE_LINKS, COLUMN_LINKS_ID + " =" + id, null) > 0;

        if (isDeleted) {
            LinkModel linkModel=new LinkModel();
            linkModel.setId(id);
            int indexOfObjectInFilteredList=linksAdapter.getLinksListFiltered().indexOf(linkModel);
            int indexOFObjectInList=linksAdapter.getLinksList().indexOf(linkModel);
            if (indexOfObjectInFilteredList>-1){
                linksAdapter.getLinksListFiltered().remove(linkModel);
                linksAdapter.notifyItemRemoved(indexOfObjectInFilteredList);
                linksAdapter.notifyItemRangeChanged(indexOfObjectInFilteredList, linksAdapter.getLinksListFiltered().size());
                linksAdapter.notifyDataSetChanged();
            }
            if (indexOFObjectInList>-1) {
                linksAdapter.getLinksList().remove(linkModel);
                linksAdapter.notifyItemRemoved(indexOFObjectInList);
                linksAdapter.notifyItemRangeChanged(indexOFObjectInList, linksAdapter.getLinksList().size());
                linksAdapter.notifyDataSetChanged();
            }

        }
        return isDeleted;
    }

    public boolean update(long id, LinkModel linkModel) {
        ContentValues values = new ContentValues();
        values.put("name", linkModel.getName()); // inserting a string
        values.put("url", linkModel.getUrl()); // inserting an int
        values.put("comment", linkModel.getComment());
        boolean isUpdated = sqLiteDatabase.update(TABLE_LINKS, values, COLUMN_LINKS_ID + " =" + id, null) > 0;
        if (isUpdated) {
            int indexOfObjectInFilteredList=linksAdapter.getLinksListFiltered().indexOf(linkModel);
            int indexOFObjectInList=linksAdapter.getLinksList().indexOf(linkModel);
            if (indexOfObjectInFilteredList>-1){
                linksAdapter.getLinksListFiltered().set(indexOfObjectInFilteredList,linkModel);
                linksAdapter.notifyItemChanged(indexOfObjectInFilteredList);
                linksAdapter.notifyDataSetChanged();


            }
            if (indexOFObjectInList>-1){
                linksAdapter.getLinksList().set(indexOFObjectInList,linkModel);
                linksAdapter.notifyItemChanged(indexOFObjectInList);
                linksAdapter.notifyDataSetChanged();

            }
        }

        return isUpdated;
    }

    public static DatabaseAdapter getDatabaseAdapter(Context context, LinksAdapter adapter) {
        if (databaseAdapter == null) {
            databaseAdapter = new DatabaseAdapter(context, adapter);
        }
        return databaseAdapter;
    }

    public ArrayList<LinkModel> linkModels() {
        ArrayList<LinkModel> linkModels = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(TABLE_LINKS, new String[]{COLUMN_LINKS_ID, COLUMN_LINKS_NAME, COLUMN_LINKS_COMMENT, COLUMN_LINKS_URL}, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                LinkModel linkModel = new LinkModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
                linkModels.add(linkModel);
            }
            cursor.close();
            if (linkModels.size()>0){
                linksAdapter.getLinksList().addAll(linkModels);
                linksAdapter.notifyDataSetChanged();
            }
            else {
                Toast.makeText(context, "You have no Links", Toast.LENGTH_SHORT).show();
            }

        }
        else {
            Toast.makeText(context, "You have no Links", Toast.LENGTH_SHORT).show();
        }
        return linkModels;
    }

    private static class DatabaseAdapterHelper extends SQLiteOpenHelper {


        public DatabaseAdapterHelper(@Nullable Context context, @Nullable String name, int version) {
            super(context, name, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_LIST);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
