package com.websarva.wings.android.imadokimap30;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    //テーブル名
    private final String TABLE_NAME = "databasename";
    //カラム名
    private final String COLUMN_NAME = "name";
    //データベース名のデータベースアクセス
    private DatabaseNameDatabaseHelper _DatabaseNameDatabaseHelper;
    //データベース
    private SQLiteDatabase db;
    //画面に表示するデータベースリスト
    private ListView listView;
    //データベースリストとデータのマッピング
    private List<Map<String,String>> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //画面部品:新規作成ボタン
        Button createNewDatabaseButton = findViewById(R.id.bt_create_new);

        //データベース接続
        _DatabaseNameDatabaseHelper = new DatabaseNameDatabaseHelper(this);

        //画面部品:ListView
        listView = findViewById(R.id.lvCocktail);
        //ListViewにClickListener追加
        listView.setOnItemClickListener(new ListItemClickListener());
        //ListViewにContextMenu追加
        registerForContextMenu(listView);

        //Listviewへデータベースを読み込み
        databaseToListview();
    }



    //ListViewのアイテムがクリックされた時のリスナー
    private class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //タップされた行のデータを取得
            Map<String,String> data = (Map<String,String>)parent.getItemAtPosition(position);
            String name = data.get(COLUMN_NAME);
            openShopList(name);
        }
    }



    //指定された名前のショップリストデータベースを開く
    public void openShopList(String name){
        Intent intent = new Intent(MainActivity.this,ShopListActivity.class);
        intent.putExtra(COLUMN_NAME,name);//データベース名を渡す
        startActivity(intent);
    }




    //ContextMenu設定
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_context_menu_list, menu);
        menu.setHeaderTitle(R.string.main_context_menu_header);
    }



    //ListViewのアイテムが長押しされた時のリスナー
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean returnVal = true;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        //長押しされたリストの名前を取得
        int listPosition = info.position;
        Map<String, String> data = dataList.get(listPosition);
        String dataName = data.get(COLUMN_NAME);

        //選択されたメニューのIDを取得
        int itemId = item.getItemId();

        //「開く」選択
        if (itemId == R.id.menuListOpen) {
            openShopList(dataName);
        //「削除」選択
        } else if (itemId == R.id.menuListDelete) {
            deleteFromDatabase(dataName);
            databaseToListview();//view更新
        //「詳細」選択
        } else if (itemId == R.id.menuListDetail) {

        }
        return returnVal;
    }



    //指定の名前のデータベースを削除
    private void deleteFromDatabase(String name){
        db.delete(TABLE_NAME, COLUMN_NAME +"=?", new String[]{name});
    }



    //新規作成ボタンクリック時の動作
    public void onCreateDatabaseButtonClick(View view) {

        if (_DatabaseNameDatabaseHelper == null) {
            _DatabaseNameDatabaseHelper = new DatabaseNameDatabaseHelper(getApplicationContext());
        }

        if (db == null) {
            db = _DatabaseNameDatabaseHelper.getWritableDatabase();
        }

        //テキスト入力用ダイアログ表示
        EditText editText = new EditText(MainActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("データベース名を入力");
        builder.setView(editText);
        builder.setPositiveButton("OK", new ImplementsDialogInterfaceClick(editText));
        builder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //キャンセル時は何もしない
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    //EditTextから入力データを取得するためのクラス
    private class ImplementsDialogInterfaceClick implements DialogInterface.OnClickListener {
        EditText edittext;

        // コンストラクターのオーバーロード
        ImplementsDialogInterfaceClick(EditText e) {
            this.edittext = e;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            String text = edittext.getText().toString();
            Toast.makeText(getApplicationContext(), text+"を作成しました", Toast.LENGTH_SHORT).show();
            //データベース新規作成
            createDatabase(text);
            //データベース読み込み
            databaseToListview();
        }
    }




    //指定の名前でデータベース新規作成
    private void createDatabase(String name) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        db.insert(TABLE_NAME, null, values);
        //名前登録だけして実際にデータベースを作成するのは呼び出されたとき
    }





    //データベースを読み込んでリストに表示する
    private void databaseToListview() {

        if (_DatabaseNameDatabaseHelper == null) {
            _DatabaseNameDatabaseHelper = new DatabaseNameDatabaseHelper(getApplicationContext());
        }

        if (db == null) {
            db = _DatabaseNameDatabaseHelper.getReadableDatabase();
        }

        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{COLUMN_NAME},
                null,
                null,
                null,
                null,
                null
        );

        //クーザーを先頭へ
        cursor.moveToFirst();

        //名前データリスト読み込み
        ArrayList<String> strList = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            strList.add(cursor.getString(0));
            cursor.moveToNext();
        }

        //クーザー解放
        cursor.close();

        //リスト登録
        dataList = new ArrayList<>();
        for (String str : strList) {
            Map<String,String> data = new HashMap<>();
            data.put(COLUMN_NAME, str);
            dataList.add(data);
        }

        //ビュー登録（連携）
        String[] from = {COLUMN_NAME};
        int[] to = {android.R.id.text1};
        SimpleAdapter adapter = new SimpleAdapter(this, dataList, android.R.layout.simple_list_item_1, from, to);
        listView.setAdapter(adapter);
    }




    //DBヘルパーオブジェクト解放
    @Override
    protected void onDestroy(){
        _DatabaseNameDatabaseHelper.close();
        super.onDestroy();
    }
}