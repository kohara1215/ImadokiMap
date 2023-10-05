package com.websarva.wings.android.imadokimap30;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ShopListActivity extends AppCompatActivity {
    //全店のリスト
    List<List<Map<String, Object>>> mappedShopList = new ArrayList<>();
    //1カテゴリー毎の店舗リスト
    List<Map<String, String>> categoryList = new ArrayList<>();
    //データベース
    private DatabaseHelper _helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);

        _helper = new DatabaseHelper(ShopListActivity.this);

        //インテントからデータベース名取得
        Intent intent = getIntent();
        String databaseName = intent.getStringExtra("name");
        Toast.makeText(this,databaseName+"を開きました",Toast.LENGTH_SHORT).show();

        TextView tvDatabaseName = findViewById(R.id.tvDatabaseName);
        tvDatabaseName.append(databaseName);

        View();//データベース内容を画面表示
    }

    //オプションメニューバー追加
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options_menu_list, menu);
        return true;
    }

    //オプションアイテム選択時の動作
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        boolean returnVal = true;
        int itemId = item.getItemId();
        //店舗一覧が選択された場合
        if(itemId == R.id.menuListOptionShopList){
            //メインでは何もしない
        }
        //店舗登録が選択された場合
        if(itemId == R.id.menuListOptionRegist){
            //登録画面へ遷移
            Intent intent = new Intent(ShopListActivity.this, ShopRegistrationActivity.class);
            startActivity(intent);
        }
        return returnVal;
    }


    //ボタン操作
    private class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Button btBack = findViewById(R.id.btBack);
            btBack.setEnabled(true);
        }
    }

    //戻るボタン動作
    public void onBackButtonClick(View view) {
        finish();
    }


    //コンテクストメニュー追加
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.setHeaderTitle(R.string.menu_list_context_header);//ヘッダー
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_shoplist_context_menu_list, menu);//コンテクストメニュー呼び出し
    }


    //コンテキストメニューアイテム選択時の動作
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean returnVal = true;
        item.getMenuInfo();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int listPosition = info.position;
        Map<String, String> menu = categoryList.get(listPosition);
        int itemId = item.getItemId();//選択されたコンテキストメニュー
        //地図表示
        if(itemId == R.id.menuListContextMap){

            TextView tvAddress = findViewById(R.id.shopinfo_address_text);
            String searchWord = tvAddress.getText().toString();
            try{
                searchWord = URLEncoder.encode(searchWord,"UTF-8");
                String uriStr = "geo:0,0?q=" + searchWord;
                Uri uri = Uri.parse(uriStr);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }catch (UnsupportedEncodingException ex){
                Log.e("ShopInfomationActivity","検索キーワード変換失敗",ex);
            }
        }
        //詳細画面へ遷移
        if(itemId == R.id.menuListContextInfo){
            Intent intent = new Intent(ShopListActivity.this, ShopInfomationActivity.class);
            //intent.putExtra("testdata",new ShopData.Shop("a","b","c","d"));
            //startActivity(intent);
        }
        //編集画面へ遷移
        if(itemId == R.id.menuListContextEdit){
            Intent intent = new Intent(ShopListActivity.this, ShopInfomationActivity.class);
            startActivity(intent);
        }
        return returnVal;
    }

    //店舗一覧表示
    private void View() {

        // カテゴリーごとに振り分け
        HashMap<String, List<ShopData.Shop>> shopCategory = new HashMap<>();
        for(ShopData.Shop shop: ShopData.getShopData()) {

            String categoryName = shop.category;

            List<ShopData.Shop> category = shopCategory.get(categoryName);

            if(category == null) {
                category = new ArrayList<ShopData.Shop>();
            }

            category.add(shop);

            shopCategory.put(categoryName, category);
        }


        for(String categoryName: shopCategory.keySet()){
            //カテゴリー表示用データ作成
            Map<String, String> categoryData = new HashMap<>();
            categoryData.put("category", categoryName);
            categoryList.add(categoryData);

            //店舗データ作成
            List<Map<String, Object>> categoryMemberList = new ArrayList<>();

            for(ShopData.Shop shop: shopCategory.get(categoryName)){
                Map<String, Object> shopData = new HashMap<>();
                shopData.put("name", shop.name);
                shopData.put("address", shop.address);
                shopData.put("hours", shop.hours);
                shopData.put("category", shop.category);
                shopData.put("object", shop); // ←【重要】表示しないけど要素に紐づけて保持する
                categoryMemberList.add(shopData);
            }

            mappedShopList.add(categoryMemberList);
        }

        // Adapterを作成
        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                this, categoryList, android.R.layout.simple_expandable_list_item_1,
                new String []{"category"}, new int []{android.R.id.text1},
                mappedShopList, R.layout.list_item,
                new String []{"name", "address", "hours"}, // メンバー表示用データから実際に使うもののキー
                new int []{R.id.name, R.id.address, R.id.hours} // 　メンバー表示のテキスト部分に振るID
        );

        // インスタンス作成
        ExpandableListView exShopList = (ExpandableListView)findViewById(R.id.exShopList);
        // Adapter登録
        exShopList.setAdapter(adapter);
        //コンテクストメニューを追加
        registerForContextMenu(exShopList);
        //子要素がクリックされたとき
        exShopList.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ExpandableListAdapter adapter = parent.getExpandableListAdapter();
                Map<String, ShopData.Shop> shopData = (Map<String, ShopData.Shop>) adapter.getChild(groupPosition, childPosition);
                ShopData.Shop shop = (ShopData.Shop)shopData.get("object");

                Intent intent = new Intent(ShopListActivity.this, ShopInfomationActivity.class);
                intent.putExtra("shopData",shop);
                startActivity(intent);

                return true;
            }
        });
    }

    public void test(View view){
       //Intent intent = new Intent(this, TestMapDisplay.class);
       // startActivity(intent);
    }
}