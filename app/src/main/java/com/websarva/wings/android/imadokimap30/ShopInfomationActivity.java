package com.websarva.wings.android.imadokimap30;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.PlaceLikelihood;

public class ShopInfomationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private PlacesClient placesClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private ShopData.Shop shop;

    private static final String TAG = "ShopInfomationActivity";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    private final float DEFAULT_ZOOM = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_infomation);

        Intent intent = getIntent();
        shop = (ShopData.Shop) intent.getSerializableExtra("shopData");

        //-----> データ取得
        TextView tvName = findViewById(R.id.shopinfo_name_text);
        tvName.setText(shop.name);

        TextView tvAddress = findViewById(R.id.shopinfo_address_text);
        tvAddress.setText(shop.address);

        TextView tvHours = findViewById(R.id.shopinfo_hours_text);
        tvHours.setText(shop.hours);

        TextView tvCategory = findViewById(R.id.shopinfo_category_text);
        tvCategory.setText(shop.category);

        //地図データ取得
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    //マップ表示の座標を指定
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Geocoder gcoder = new Geocoder(this, Locale.getDefault());
        double latitude = 0;//緯度
        double longitude = 0;//経度
        TextView tvWord = findViewById(R.id.shopinfo_name_text);//ショップ住所
        String searchWord = tvWord.getText().toString();//座標を取得したい店名
        StringBuffer strAddr = new StringBuffer();//住所


        int maxResults = 1;
        try {
            //---->店名から住所取得
            List<Address> lstAddr = gcoder.getFromLocationName(searchWord, maxResults);
            for (Address addr : lstAddr) {
                int idx = addr.getMaxAddressLineIndex();
                for (int i = 1; i <= idx; i++) {
                    strAddr.append(addr.getAddressLine(i));
                    Log.v("addr", addr.getAddressLine(i));
                }
            }

            //緯度、経度を取得
            if (lstAddr != null && lstAddr.size() > 0) {
                Address addr = lstAddr.get(0);
                latitude = addr.getLatitude();
                longitude = addr.getLongitude();
            }
        } catch (IOException ex) {
            Log.e("ShopInfomationActivity", "検索キーワード変換失敗", ex);
        }

        //地図表示
        mMap = googleMap;
        LatLng shopPosition = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(shopPosition).title(searchWord));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(shopPosition));

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
            finish();
        }
        //店舗登録が選択された場合
        if(itemId == R.id.menuListOptionRegist){
            finish();
        }
        return returnVal;
    }

    //ボタン操作
    private class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Button btBack = findViewById(R.id.bt_cancel);
            btBack.setEnabled(true);
        }
    }


    //戻るボタンクリック時動作
    public void onBackButtonClick(View view) {
        finish();
    }

    //ここへ行くボタンをクリック時動作
    public void onGoMapClick(View view){

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
}