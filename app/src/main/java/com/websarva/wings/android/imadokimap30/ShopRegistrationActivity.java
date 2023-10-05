package com.websarva.wings.android.imadokimap30;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShopRegistrationActivity extends AppCompatActivity {
    private DatabaseHelper _helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_registration);

        //画面部品ボタン
        Button registButton = findViewById(R.id.bt_gomap);
        Button cancelButton = findViewById(R.id.bt_cancel);

        _helper = new DatabaseHelper(ShopRegistrationActivity.this);
    }




    //オプションメニューバー追加
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options_menu_list, menu);
        return true;
    }




    //オプションアイテム選択の動作処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        boolean returnVal = true;
        int itemId = item.getItemId();
        //店舗一覧が選択された場合
        if(itemId == R.id.menuListOptionShopList){
            //登録画面を終了する
            finish();
        }
        //店舗登録が選択された場合
        if(itemId == R.id.menuListOptionRegist){
            //何もしない
        }
        return returnVal;
    }




    //検索ボタン動作
    public void onSearchButtonClick(View view) {

        // TextViewに入力された店舗名取得
        TextView editText = findViewById(R.id.shopregist_name_text);
        String text = editText.getText().toString();

        // Places APIを初期化
        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_api));

        // placesClientを初期化
        PlacesClient placesClient = Places.createClient(this);

        // Autocompleteのリクエストを作成
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setQuery(text)
                .build();

        // 店舗名からPlaceIdを取得
        placesClient.findAutocompletePredictions(request).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
            @Override
            public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                if (task.isSuccessful()) {
                    FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                    if (predictionsResponse != null) {
                        List<AutocompletePrediction> predictions = predictionsResponse.getAutocompletePredictions();
                        if (!predictions.isEmpty()) {
                            // 最初の予測結果からPlace IDを取得
                            AutocompletePrediction prediction = predictions.get(0);
                            String placeId = prediction.getPlaceId(); // ここで placeId を代入

                            // 場所の詳細情報をリクエスト
                            List<Place.Field> placeFields = Arrays.asList(
                                    Place.Field.NAME, Place.Field.ADDRESS, Place.Field.PHONE_NUMBER, Place.Field.OPENING_HOURS, Place.Field.ID, Place.Field.TYPES);
                            FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.newInstance(placeId, placeFields);

                            // PlaceIdから店舗情報を取得
                            placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener((response) -> {
                                Place place = response.getPlace();

                                //店名を取得
                                getName(place);

                                // 住所を取得
                                getAddress(place);

                                //営業時間を取得
                                getOpeningHours(place);

                                //IDを取得
                                getType(place);

                            }).addOnFailureListener((exception) -> {
                                // エラー処理
                                Log.e("Place Details", "Place not found: " + exception.getMessage());
                            });
                        } else {
                            Toast.makeText(ShopRegistrationActivity.this, "Place not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(ShopRegistrationActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    // 店名を取得
    public void getName(Place place){
        if (place.getName() != null) {
            String name = place.getName();
            EditText etName = findViewById(R.id.shopregist_name_text);
            etName.setText(name);
        }
    }



    //住所を取得
    public void getAddress(Place place){
        if (place.getAddress() != null) {
            String fullAddress = place.getAddress().toString();

            // 郵便番号を含む住所文字列から郵便番号を特定する正規表現
            String postalCodePattern = "\\d{3}-\\d{4}|\\d{3}-\\d{2}|\\d{5}|\\d{7}";

            // 正規表現を使って郵便番号を特定
            Pattern pattern = Pattern.compile(postalCodePattern);
            Matcher matcher = pattern.matcher(fullAddress);

            String addressWithoutPostalCode;

            if (matcher.find()) {
                int endIndex = matcher.end(); // 郵便番号の終了位置
                addressWithoutPostalCode = fullAddress.substring(endIndex).trim();
            } else {
                // 郵便番号が見つからない場合、元の住所をそのまま使う
                addressWithoutPostalCode = fullAddress;
            }

            EditText etAddress = findViewById(R.id.shopregist_address_text);
            etAddress.setText(addressWithoutPostalCode);
        }
    }





    // 電話番号を取得
    public void getPhoneNumber(Place place){
        if (place.getPhoneNumber() != null) {
            String phoneNumber = place.getPhoneNumber();
            //EditText etPhoneNumber = findViewById(R.id.shopregist_phone_number);
            //etPhoneNumber.setText(phoneNumber);
        }
    }



    // 営業時間を取得
    public void getOpeningHours(Place place){
        if (place.getOpeningHours() != null) {
            List<String> openingHours = place.getOpeningHours().getWeekdayText();
            String openingHoursText="";
            EditText etOpeningHours = findViewById(R.id.shopregist_hours_text);
            //営業時間をfor文で書き出し
            for (String hours : openingHours) {
                openingHoursText += hours;
                //最後の行以外は改行を追加
                if(openingHours.indexOf(hours) < openingHours.size()-1){
                    openingHoursText += "\n";
                }
            }
            etOpeningHours.setText(openingHoursText);
        }
    }




    // idを取得
    public void getType(Place place){
        if (place.getTypes() != null) {
            String type = place.getTypes().get(0).toString();
            EditText etType = findViewById(R.id.shopregist_category_text);
            etType.setText(type);
        }
    }




    // idを取得
    public void getId(Place place){
        if (place.getId() != null) {
            String id = place.getId();
            //EditText etId = findViewById(R.id.shopregist_category_id);
            //etId.setText(id);
        }
    }



    //登録ボタン動作
    public void onRegistButtonClick(View view) {
        //データベースを開く
        SQLiteDatabase db = _helper.getWritableDatabase();
        ContentValues values = new ContentValues();//オブジェクトの作成

        EditText etName = findViewById(R.id.shopregist_name_text);
        EditText etAddress = findViewById(R.id.shopregist_address_text);
        EditText etHours = findViewById(R.id.shopregist_hours_text);
        EditText etCategory = findViewById(R.id.shopregist_category_text);

        String name = etName.getText().toString();
        String address = etAddress.getText().toString();
        String hours = etHours.getText().toString();
        String category = etCategory.getText().toString();

        values.put("_name", name);//データ格納
        values.put("_address", address);
        values.put("_hours", hours);
        values.put("_category", category);

        long ret;
        try {
            //データを追加する
            ret = db.insert("DataBaseTable", null, values);
        } finally {
            db.close();
        }

        if (ret == -1) {
            Toast.makeText(this, "データベース登録に失敗しました", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "データベースに登録しました", Toast.LENGTH_SHORT).show();
        }
        db.close();//データベースを閉じる
    }



    //キャンセルボタン
    public void onCancelButtonClick(View view){
        finish();
    }
}