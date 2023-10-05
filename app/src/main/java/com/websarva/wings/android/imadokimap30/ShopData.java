package com.websarva.wings.android.imadokimap30;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShopData {

    public static List<Shop> getShopData() {

        List<Shop> Shop = new ArrayList<Shop>();

        // さつまいも
        Shop.add(new Shop("銀座つぼやきいも",
                "〒104-0061 東京都中央区銀座７丁目６−４ GINZA7BLDG 1階",
                "12時00分～22時00分",
                "さつまいも"));

        Shop.add(new Shop("高級芋菓子しみず 築地本店",
                "〒104-0045 東京都中央区築地６丁目２１−４",
                "10時00分～20時00分",
                "さつまいも"));

        Shop.add(new Shop("OIMO 東京ギフトパレット店",
                "〒100-0005 東京都千代田区丸の内１丁目９−１",
                "9時30分～20時30分",
                "さつまいも"));

        // パイナップル
        Shop.add(new Shop("むさしの森珈琲 武蔵野西久保店",
                "〒180-0013 東京都武蔵野市西久保１丁目２６−９",
                "7時00分～22時00分",
                "パイナップル"));

        Shop.add(new Shop("Island Burgers 四谷三丁目店",
                "〒160-0004 東京都新宿区四谷３丁目１−１ 須賀ビル",
                "10時00分～20時00分",
                "パイナップル"));

        Shop.add(new Shop("ラ・オハナ 府中新町店",
                "〒183-0052 東京都府中市新町２丁目２０−１",
                "8時00分～22時30分",
                "パイナップル"));

        // おにぎり
        Shop.add(new Shop("Onigily Cafe",
                "〒153-0061 東京都目黒区中目黒３丁目１−４",
                "8時00分～16時00分",
                "おにぎり"));

        Shop.add(new Shop("TARO TOKYO ONIGIRI 虎ノ門",
                "〒105-0001 東京都港区虎ノ門１丁目１２−１１ 虎ノ門ファーストビル １階",
                "8時00分～16時00分",
                "おにぎり"));

        Shop.add(new Shop("おむすびのGABA 秋葉原店",
                "閉店",
                "-",
                "おにぎり"));

        // うま辛
        Shop.add(new Shop("武蔵小杉ガーデンファーム",
                "〒211-0063 神奈川県川崎市中原区小杉町３丁目４３０−１ 千里ビル 1F",
                "月曜日、11時30分～15時30分、16時30分～23時30分",
                "うま辛"));

        Shop.add(new Shop("パオセン イェーパオズ",
                "閉店",
                "-",
                "うま辛"));

        Shop.add(new Shop("サナギ 新宿",
                "〒160-0022 東京都新宿区新宿３丁目３５−６",
                "月曜日、11時00分～23時00分",
                "うま辛"));

        // 芋スイーツ
        Shop.add(new Shop("uluca",
                "〒171-0031 東京都豊島区目白３丁目６−１ アルファ 目白 1f",
                "月曜日、11時00分～19時00分",
                "芋スイーツ"));

        Shop.add(new Shop("をかしなお芋 芋をかし 下北沢",
                "〒155-0031 東京都世田谷区北沢２丁目１２−１５ 水内ビル 1F",
                "月曜日、11時00分～19時00分",
                "芋スイーツ"));

        Shop.add(new Shop("ハートブレッドアンティーク 銀座店",
                "閉店",
                "-",
                "芋スイーツ"));

        return Shop;
    }

    //Serializabaleインターフェースを実装することでintentにshopdataを渡すことができる
    static class Shop implements Serializable {
        public String name;
        public String address;
        public String hours;
        public String category;
        public String placeId;
        public Shop(String name, String address, String hours, String category) {
            this.name = name;
            this.address = address;
            this.hours = hours;
            this.category = category;
        }
    }

}